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
package com.orientechnologies.orient.core.query.nativ;

import java.util.List;

import com.orientechnologies.orient.core.command.OCommandResultListener;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordAbstract;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.OStorageEmbedded;

@SuppressWarnings("serial")
public abstract class ONativeAsynchQuery<CTX extends OQueryContextNative> extends ONativeQuery<CTX> {
	protected OCommandResultListener	resultListener;
	protected int											resultCount	= 0;
	protected ORecordInternal<?>			record;

	public ONativeAsynchQuery(final String iCluster, final CTX iQueryRecordImpl) {
		this(iCluster, iQueryRecordImpl, null);
	}

	public ONativeAsynchQuery(final String iCluster, final CTX iQueryRecordImpl, final OCommandResultListener iResultListener) {
		super(iCluster);
		resultListener = iResultListener;
		queryRecord = iQueryRecordImpl;
		record = new ODocument();
	}

	@Deprecated
	public ONativeAsynchQuery(final ODatabaseRecord iDatabase, final String iCluster, final CTX iQueryRecordImpl,
			final OCommandResultListener iResultListener) {
		this(iCluster, iQueryRecordImpl, iResultListener);
	}

	public boolean isAsynchronous() {
		return resultListener != this;
	}

	public boolean foreach(final ORecordInternal<?> iRecord) {
		final ODocument record = (ODocument) iRecord;
		queryRecord.setRecord(record);

		if (filter(queryRecord)) {
			resultCount++;
			resultListener.result(record.copy());

			if (limit > -1 && resultCount == limit)
				// BREAK THE EXECUTION
				return false;
		}
		return true;
	}

	public List<ODocument> run(final Object... iArgs) {
		final ODatabaseRecord database = ODatabaseRecordThreadLocal.INSTANCE.get();

		if (!(database.getStorage() instanceof OStorageEmbedded))
			throw new OCommandExecutionException("Native queries can run only in embedded-local version. Not in the remote one.");

		queryRecord.setSourceQuery(this);

		// CHECK IF A CLASS WAS CREATED
		final OClass cls = database.getMetadata().getSchema().getClass(className);
		if (cls == null)
			throw new OCommandExecutionException("Class '" + className + "' was not found");

		final ORecordIteratorClass<ORecordInternal<?>> target = new ORecordIteratorClass<ORecordInternal<?>>(database,
				(ODatabaseRecordAbstract) database, className, isPolymorphic());

		// BROWSE ALL THE RECORDS
		for (OIdentifiable id : target) {
			final ORecordInternal<?> record = (ORecordInternal<?>) id.getRecord();

			if (record != null && record.getRecordType() != ODocument.RECORD_TYPE)
				// WRONG RECORD TYPE: JUMP IT
				continue;

			queryRecord.setRecord((ODocument) record);

			if (filter(queryRecord)) {
				resultCount++;
				resultListener.result(record.copy());

				if (limit > -1 && resultCount == limit)
					// BREAK THE EXECUTION
					break;
			}
		}

		return null;
	}

	public ODocument runFirst(final Object... iArgs) {
		setLimit(1);
		execute();
		return null;
	}

	@Override
	public OCommandResultListener getResultListener() {
		return resultListener;
	}

	@Override
	public void setResultListener(final OCommandResultListener resultListener) {
		this.resultListener = resultListener;
	}
}
