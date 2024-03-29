/*
 * Copyright 1999-2010 Luca Garulli (l.garulli--at--orientechnologies.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.core.db.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.OConstants;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.config.OStorageConfiguration;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.iterator.ORecordIteratorCluster;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.orientechnologies.orient.core.metadata.schema.OSchemaShared;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.storage.impl.local.OClusterLogical;
import com.orientechnologies.orient.core.storage.impl.local.OStorageLocal;
import com.orientechnologies.orient.core.type.tree.provider.OMVRBTreeMapProvider;

/**
 * Export data from a database to a file.
 * 
 * @author Luca Garulli (l.garulli--at--orientechnologies.com)
 * 
 */
public class ODatabaseExport extends ODatabaseImpExpAbstract {
	private OJSONWriter			writer;
	private long						recordExported;
	public static final int	VERSION	= 2;

	public ODatabaseExport(final ODatabaseRecord iDatabase, final String iFileName, final OCommandOutputListener iListener)
			throws IOException {
		super(iDatabase, iFileName, iListener);

		if (!fileName.endsWith(".gz")) {
			fileName += ".gz";
		}
		final File f = new File(fileName);
		f.mkdirs();
		if (f.exists())
			f.delete();

		writer = new OJSONWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(fileName))));
		writer.beginObject();
		iDatabase.getLevel1Cache().setEnable(false);
		iDatabase.getLevel2Cache().setEnable(false);
	}

	public ODatabaseExport(final ODatabaseRecord iDatabase, final OutputStream iOutputStream, final OCommandOutputListener iListener)
			throws IOException {
		super(iDatabase, "streaming", iListener);

		writer = new OJSONWriter(new OutputStreamWriter(iOutputStream));
		writer.beginObject();
		iDatabase.getLevel1Cache().setEnable(false);
		iDatabase.getLevel2Cache().setEnable(false);
	}

	public ODatabaseExport exportDatabase() {
		database.callInLock(new Callable<Object>() {

			public Object call() {
				try {
					listener.onMessage("\nStarted export of database '" + database.getName() + "' to " + fileName + "...");

					database.getLevel1Cache().setEnable(false);
					database.getLevel2Cache().setEnable(false);

					long time = System.currentTimeMillis();

					if (includeInfo)
						exportInfo();
					exportClusters();
					if (includeSchema)
						exportSchema();
					if (includeRecords)
						exportRecords();

					listener.onMessage("\n\nDatabase export completed in " + (System.currentTimeMillis() - time) + "ms");

					writer.flush();
				} catch (Exception e) {
					e.printStackTrace();
					throw new ODatabaseExportException("Error on exporting database '" + database.getName() + "' to: " + fileName, e);
				} finally {
					close();
				}
				return null;
			}
		}, false);
		return this;
	}

	public long exportRecords() throws IOException {
		long totalRecords = 0;
		int level = 1;
		listener.onMessage("\nExporting records...");

		writer.beginCollection(level, true, "records");
		int exportedClusters = 0;
		int maxClusterId = getMaxClusterId();
		for (int i = 0; exportedClusters <= maxClusterId; ++i) {
			String clusterName = database.getClusterNameById(i);

			exportedClusters++;

			final long recordTot;

			if (clusterName != null) {
				// CHECK IF THE CLUSTER IS INCLUDED
				if (includeClusters != null) {
					if (!includeClusters.contains(clusterName))
						continue;
				} else if (excludeClusters != null) {
					if (excludeClusters.contains(clusterName))
						continue;
				}

				if (excludeClusters.contains(clusterName))
					continue;

				recordTot = database.countClusterElements(clusterName);
			} else
				recordTot = 0;

			listener.onMessage("\n- Cluster " + (clusterName != null ? "'" + clusterName + "'" : "NULL") + " (id=" + i + ")...");

			long recordNum = 0;
			if (clusterName != null)
				for (ORecordIteratorCluster<ORecordInternal<?>> it = database.browseCluster(clusterName); it.hasNext();) {

					ORecordInternal<?> rec = null;

					try {
						rec = it.next();
						if (rec instanceof ODocument) {
							// CHECK IF THE CLASS OF THE DOCUMENT IS INCLUDED
							ODocument doc = (ODocument) rec;
							if (includeClasses != null) {
								if (!includeClasses.contains(doc.getClassName()))
									continue;
							} else if (excludeClasses != null) {
								if (excludeClasses.contains(doc.getClassName()))
									continue;
							}
						}

						exportRecord(recordTot, recordNum++, rec);
					} catch (Throwable t) {
						if (rec != null) {
							final byte[] buffer = rec.toStream();

							OLogManager
									.instance()
									.error(
											this,
											"Error on exporting record #%s. It seems corrupted; size: %d bytes, raw content (as string):\n==========\n%s\n==========",
											t, rec.getIdentity(), buffer.length, new String(buffer));
						}
					}
				}

			listener.onMessage("OK (records=" + recordTot + ")");

			totalRecords += recordTot;
		}
		writer.endCollection(level, true);

		listener.onMessage("\n\nDone. Exported " + totalRecords + " records\n");

		return totalRecords;
	}

	public void close() {
		database.declareIntent(null);

		if (writer == null)
			return;

		try {
			writer.endObject();
			writer.close();
			writer = null;
		} catch (IOException e) {
		}
	}

	private void exportClusters() throws IOException {
		listener.onMessage("\nExporting clusters...");

		writer.beginCollection(1, true, "clusters");
		int exportedClusters = 0;

		int maxClusterId = getMaxClusterId();

		for (int clusterId = 0; clusterId <= maxClusterId; ++clusterId) {

			final String clusterName = database.getClusterNameById(clusterId);

			if (clusterName != null)
				// CHECK IF THE CLUSTER IS INCLUDED
				if (includeClusters != null) {
					if (!includeClusters.contains(clusterName))
						continue;
				} else if (excludeClusters != null) {
					if (excludeClusters.contains(clusterName))
						continue;
				}

			writer.beginObject(2, true, null);

			writer.writeAttribute(0, false, "name", clusterName != null ? clusterName : "");
			writer.writeAttribute(0, false, "id", clusterId);
			writer.writeAttribute(0, false, "type", clusterName != null ? database.getClusterType(clusterName) : "");

			if (clusterName != null && database.getStorage() instanceof OStorageLocal)
				if (database.getClusterType(clusterName).equals("LOGICAL")) {
					OClusterLogical cluster = (OClusterLogical) database.getStorage().getClusterById(clusterId);
					writer.writeAttribute(0, false, "rid", cluster.getRID());
				}

			exportedClusters++;
			writer.endObject(2, false);
		}

		listener.onMessage("OK (" + exportedClusters + " clusters)");

		writer.endCollection(1, true);
	}

	protected int getMaxClusterId() {
		int totalCluster = -1;
		for (String clusterName : database.getClusterNames()) {
			if (database.getClusterIdByName(clusterName) > totalCluster)
				totalCluster = database.getClusterIdByName(clusterName);
		}
		return totalCluster;
	}

	private void exportInfo() throws IOException {
		listener.onMessage("\nExporting database info...");

		writer.beginObject(1, true, "info");
		writer.writeAttribute(2, true, "name", database.getName().replace('\\', '/'));
		writer.writeAttribute(2, true, "default-cluster-id", database.getDefaultClusterId());
		writer.writeAttribute(2, true, "exporter-version", VERSION);
		writer.writeAttribute(2, true, "engine-version", OConstants.ORIENT_VERSION);
		final String engineBuild = OConstants.getBuildNumber();
		if (engineBuild != null)
			writer.writeAttribute(2, true, "engine-build", engineBuild);
		writer.writeAttribute(2, true, "storage-config-version", OStorageConfiguration.CURRENT_VERSION);
		writer.writeAttribute(2, true, "schema-version", OSchemaShared.CURRENT_VERSION_NUMBER);
		writer.writeAttribute(2, true, "mvrbtree-version", OMVRBTreeMapProvider.CURRENT_PROTOCOL_VERSION);
		writer.endObject(1, true);

		listener.onMessage("OK");
	}

	private void exportSchema() throws IOException {
		listener.onMessage("\nExporting schema...");

		writer.beginObject(1, true, "schema");
		OSchemaProxy s = (OSchemaProxy) database.getMetadata().getSchema();
		writer.writeAttribute(2, true, "version", s.getVersion());

		if (!s.getClasses().isEmpty()) {
			writer.beginCollection(2, true, "classes");

			final List<OClass> classes = new ArrayList<OClass>(s.getClasses());
			Collections.sort(classes);

			for (OClass cls : classes) {
				writer.beginObject(3, true, null);
				writer.writeAttribute(0, false, "name", cls.getName());
				writer.writeAttribute(0, false, "default-cluster-id", cls.getDefaultClusterId());
				writer.writeAttribute(0, false, "cluster-ids", cls.getClusterIds());
				if (cls.getSuperClass() != null)
					writer.writeAttribute(0, false, "super-class", cls.getSuperClass().getName());
				if (cls.getShortName() != null)
					writer.writeAttribute(0, false, "short-name", cls.getShortName());

				if (!cls.properties().isEmpty()) {
					writer.beginCollection(4, true, "properties");

					final List<OProperty> properties = new ArrayList<OProperty>(cls.declaredProperties());
					Collections.sort(properties);

					for (OProperty p : properties) {
						writer.beginObject(5, true, null);
						writer.writeAttribute(0, false, "name", p.getName());
						writer.writeAttribute(0, false, "type", p.getType().toString());
						writer.writeAttribute(0, false, "mandatory", p.isMandatory());
						writer.writeAttribute(0, false, "not-null", p.isNotNull());
						if (p.getLinkedClass() != null)
							writer.writeAttribute(0, false, "linked-class", p.getLinkedClass().getName());
						if (p.getLinkedType() != null)
							writer.writeAttribute(0, false, "linked-type", p.getLinkedType().toString());
						if (p.getMin() != null)
							writer.writeAttribute(0, false, "min", p.getMin());
						if (p.getMax() != null)
							writer.writeAttribute(0, false, "max", p.getMax());
						writer.endObject(0, false);
					}
					writer.endCollection(4, true);
				}

				writer.endObject(3, true);
			}
			writer.endCollection(2, true);
		}

		writer.endObject(1, true);

		listener.onMessage("OK (" + s.getClasses().size() + " classes)");
	}

	private void exportRecord(long recordTot, long recordNum, ORecordInternal<?> rec) throws IOException {
		if (rec == null)
			return;

		if (rec.getIdentity().isValid())
			rec.reload();

		if (recordExported > 0)
			writer.append(",");

		writer.append(rec.toJSON("rid,type,version,class,attribSameRow,indent:4,keepTypes"));

		recordExported++;
		recordNum++;

		if (recordTot > 10 && (recordNum + 1) % (recordTot / 10) == 0)
			listener.onMessage(".");
	}
}
