/*
 * Copyright 1999-2011 Luca Garulli (l.garulli--at--orientechnologies.com)
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
package com.orientechnologies.orient.server.replication;

import java.io.IOException;

/**
 * @author Luca Garulli (l.garulli--at--orientechnologies.com)
 * 
 */
public class ODistributedDatabaseInfo {

	public enum SYNCH_TYPE {
		SYNCH, ASYNCH
	}

	public String						serverId;
	public String						databaseName;
	public String						userName;
	public String						userPassword;
	public SYNCH_TYPE				synchType;
	public ONodeConnection	connection;
	public OOperationLog		log;

	public ODistributedDatabaseInfo(final String iServerid, final String dbName, final String iUserName, final String iUserPasswd,
			final SYNCH_TYPE iSynchType) {
		serverId = iServerid;
		databaseName = dbName;
		userName = iUserName;
		userPassword = iUserPasswd;
		synchType = iSynchType;
	}

	public void connected() throws IOException {
		log = new OOperationLog(serverId, databaseName);
	}

	public void close() throws IOException {
		if (log != null)
			log.close();
	}
}
