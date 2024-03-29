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
package com.orientechnologies.orient.client.remote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.core.index.OIndexManager;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.enterprise.channel.binary.OChannelBinaryClient;
import com.orientechnologies.orient.enterprise.channel.binary.OChannelBinaryProtocol;

/**
 * Remote administration class of OrientDB Server instances.
 */
public class OServerAdmin {
	private OStorageRemote	storage;
	private int							sessionId	= -1;

	/**
	 * Creates the object passing a remote URL to connect.
	 * 
	 * @param iURL
	 *          URL to connect. It supports only the "remote" storage type.
	 * @throws IOException
	 */
	public OServerAdmin(String iURL) throws IOException {
		if (iURL.startsWith(OEngineRemote.NAME))
			iURL = iURL.substring(OEngineRemote.NAME.length() + 1);

		if (!iURL.contains("/"))
			iURL += "/";

		storage = new OStorageRemote(null, iURL, "");
	}

	/**
	 * Creates the object starting from an existent remote storage.
	 * 
	 * @param iStorage
	 */
	public OServerAdmin(final OStorageRemote iStorage) {
		storage = iStorage;
	}

	/**
	 * Connects to a remote server.
	 * 
	 * @param iUserName
	 *          Server's user name
	 * @param iUserPassword
	 *          Server's password for the user name used
	 * @return The instance itself. Useful to execute method in chain
	 * @throws IOException
	 */
	public synchronized OServerAdmin connect(final String iUserName, final String iUserPassword) throws IOException {
		storage.createConnectionPool();

		try {
			final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_CONNECT);

			storage.sendClientInfo(network);

			try {
				network.writeString(iUserName);
				network.writeString(iUserPassword);
			} finally {
				storage.endRequest(network);
			}

			try {
				storage.beginResponse(network);
				sessionId = network.readInt();
				storage.setSessionId(sessionId);
			} finally {
				storage.endResponse(network);
			}

		} catch (Exception e) {
			OLogManager.instance().error(this, "Cannot connect to the remote server: " + storage.getName(), e, OStorageException.class);
			storage.close(true);
		}
		return this;
	}

	/**
	 * List the databases on a remote server.
	 * 
	 * @param iUserName
	 *          Server's user name
	 * @param iUserPassword
	 *          Server's password for the user name used
	 * @return The instance itself. Useful to execute method in chain
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public synchronized Map<String, String> listDatabases() throws IOException {
		storage.createConnectionPool();
		final ODocument result = new ODocument();
		try {
			final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_DB_LIST);
			storage.endRequest(network);

			try {
				storage.beginResponse(network);
				result.fromStream(network.readBytes());
			} finally {
				storage.endResponse(network);
			}

		} catch (Exception e) {
			OLogManager.instance().exception("Cannot retrieve the configuration list", e, OStorageException.class);
			storage.close(true);
		}
		return (Map<String, String>) result.field("databases");
	}

	public int getSessionId() {
		return sessionId;
	}

	/**
	 * Creates a database in a remote server.
	 * 
	 * @param iStorageMode
	 *          Storage mode. Null to use the default ("CSV")
	 * @return The instance itself. Useful to execute method in chain
	 * @throws IOException
	 */
	public synchronized OServerAdmin createDatabase(String iStorageMode) throws IOException {
		storage.checkConnection();

		try {
			if (storage.getName() == null || storage.getName().length() <= 0) {
				OLogManager.instance().error(this, "Cannot create unnamed remote storage. Check your syntax", OStorageException.class);
			} else {
				if (iStorageMode == null)
					iStorageMode = "csv";

				final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_DB_CREATE);
				try {
					network.writeString(storage.getName());
					network.writeString(iStorageMode);
				} finally {
					storage.endRequest(network);
				}

				storage.getResponse(network);

			}

		} catch (Exception e) {
			OLogManager.instance().error(this, "Cannot create the remote storage: " + storage.getName(), e, OStorageException.class);
			storage.close(true);
		}
		return this;
	}

	/**
	 * Checks if a database exists in the remote server.
	 * 
	 * @return true if exists, otherwise false
	 * @throws IOException
	 */
	public synchronized boolean existsDatabase() throws IOException {
		storage.checkConnection();

		try {
			final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_DB_EXIST);
			try {
				network.writeString(storage.getName());
			} finally {
				storage.endRequest(network);
			}

			try {
				storage.beginResponse(network);
				return network.readByte() == 1;
			} finally {
				storage.endResponse(network);
			}

		} catch (Exception e) {
			OLogManager.instance().exception("Error on checking existence of the remote storage: " + storage.getName(), e,
					OStorageException.class);
			storage.close(true);
		}
		return false;
	}

	/**
	 * Deprecated. Use dropDatabase() instead.
	 * 
	 * @return The instance itself. Useful to execute method in chain
	 * @see #dropDatabase()
	 * @throws IOException
	 */
	@Deprecated
	public OServerAdmin deleteDatabase() throws IOException {
		return dropDatabase();
	}

	/**
	 * Drops a database from a remote server instance.
	 * 
	 * @return The instance itself. Useful to execute method in chain
	 * @throws IOException
	 */
	public synchronized OServerAdmin dropDatabase() throws IOException {
		storage.checkConnection();

		try {

			final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_DB_DROP);
			try {
				network.writeString(storage.getName());
			} finally {
				storage.endRequest(network);
			}

			storage.getResponse(network);

		} catch (Exception e) {
			OLogManager.instance().exception("Cannot delete the remote storage: " + storage.getName(), e, OStorageException.class);
			storage.close(true);
		}

		storage.setSessionId(-1);

		for (OStorage s : Orient.instance().getStorages()) {
			if (s.getURL().startsWith(getURL())) {
				s.removeResource(OSchema.class.getSimpleName());
				s.removeResource(OIndexManager.class.getSimpleName());
				s.removeResource(OSecurity.class.getSimpleName());
			}
		}

		ODatabaseRecordThreadLocal.INSTANCE.set(null);

		return this;
	}

	/**
	 * 
	 * @param iDatabaseName
	 * @param iDatabaseUserName
	 * @param iDatabaseUserPassword
	 * @param iRemoteName
	 * @param iRemoteEngine
	 * @param iSynchronousMode
	 * @return The instance itself. Useful to execute method in chain
	 * @throws IOException
	 */
	public synchronized OServerAdmin shareDatabase(final String iDatabaseName, final String iDatabaseUserName,
			final String iDatabaseUserPassword, final String iRemoteName, final String iRemoteEngine) throws IOException {
		storage.checkConnection();

		try {

			final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_DB_COPY);
			try {
				network.writeString(iDatabaseName);
				network.writeString(iDatabaseUserName);
				network.writeString(iDatabaseUserPassword);
				network.writeString(iRemoteName);
				network.writeString(iRemoteEngine);
			} finally {
				storage.endRequest(network);
			}

			storage.getResponse(network);

			OLogManager.instance().debug(this, "Database '%s' has been shared with the server '%s'", iDatabaseName, iRemoteName);

		} catch (Exception e) {
			OLogManager.instance().exception("Cannot share the database: " + iDatabaseName, e, OStorageException.class);
		}

		return this;
	}

	public synchronized Map<String, String> getGlobalConfigurations() throws IOException {
		storage.checkConnection();

		final Map<String, String> config = new HashMap<String, String>();

		try {
			final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_CONFIG_LIST);
			storage.endRequest(network);

			try {
				storage.beginResponse(network);
				final int num = network.readShort();
				for (int i = 0; i < num; ++i)
					config.put(network.readString(), network.readString());
			} finally {
				storage.endResponse(network);
			}

		} catch (Exception e) {
			OLogManager.instance().exception("Cannot retrieve the configuration list", e, OStorageException.class);
			storage.close(true);
		}
		return config;
	}

	public synchronized String getGlobalConfiguration(final OGlobalConfiguration iConfig) throws IOException {
		storage.checkConnection();

		try {
			final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_CONFIG_GET);
			network.writeString(iConfig.getKey());
			storage.beginResponse(network);

			try {
				return network.readString();
			} finally {
				storage.endResponse(network);
			}

		} catch (Exception e) {
			OLogManager.instance().exception("Cannot retrieve the configuration value: " + iConfig.getKey(), e, OStorageException.class);
			storage.close(true);
		}
		return null;
	}

	public synchronized OServerAdmin setGlobalConfiguration(final OGlobalConfiguration iConfig, final Object iValue)
			throws IOException {
		storage.checkConnection();

		try {
			final OChannelBinaryClient network = storage.beginRequest(OChannelBinaryProtocol.REQUEST_CONFIG_SET);
			network.writeString(iConfig.getKey());
			network.writeString(iValue != null ? iValue.toString() : "");
			storage.getResponse(network);

		} catch (Exception e) {
			OLogManager.instance().exception("Cannot set the configuration value: " + iConfig.getKey(), e, OStorageException.class);
			storage.close(true);
		}
		return this;
	}

	/**
	 * Close the connection if open.
	 */
	public synchronized void close() {
		storage.close();
	}

	public synchronized void close(boolean iForce) {
		storage.close(iForce);
	}

	public synchronized String getURL() {
		return storage != null ? storage.getURL() : null;
	}
}
