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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import com.orientechnologies.common.concur.resource.OSharedResourceAdaptive;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.cache.OLevel2RecordCache;
import com.orientechnologies.orient.core.command.OCommandRequestText;
import com.orientechnologies.orient.core.config.OStorageConfiguration;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.OCluster;
import com.orientechnologies.orient.core.storage.OPhysicalPosition;
import com.orientechnologies.orient.core.storage.ORawBuffer;
import com.orientechnologies.orient.core.storage.ORecordCallback;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.enterprise.channel.binary.OChannelBinaryClient;
import com.orientechnologies.orient.enterprise.channel.binary.ORemoteServerEventListener;

/**
 * Wrapper of OStorageRemote that maintains the sessionId. It's bound to the ODatabase and allow to use the shared OStorageRemote.
 */
@SuppressWarnings("unchecked")
public class OStorageRemoteThread implements OStorage {
	private static AtomicInteger	sessionSerialId	= new AtomicInteger(-1);

	private final OStorageRemote	delegate;
	private int										sessionId;

	public OStorageRemoteThread(final OStorageRemote iSharedStorage) {
		delegate = iSharedStorage;
		sessionId = sessionSerialId.decrementAndGet();
	}

	public OStorageRemoteThread(final OStorageRemote iSharedStorage, final int iSessionId) {
		delegate = iSharedStorage;
		sessionId = iSessionId;
	}

	public void open(final String iUserName, final String iUserPassword, final Map<String, Object> iOptions) {
		delegate.setSessionId(sessionId);
		delegate.open(iUserName, iUserPassword, iOptions);
		sessionId = delegate.getSessionId();
	}

	public void create(final Map<String, Object> iOptions) {
		delegate.setSessionId(sessionId);
		delegate.create(iOptions);
		sessionId = delegate.getSessionId();
	}

	public void close(boolean iForce) {
		delegate.setSessionId(sessionId);
		delegate.close(iForce);
		Orient.instance().unregisterStorage(this);
	}

	public boolean dropCluster(final String iClusterName) {
		delegate.setSessionId(sessionId);
		return delegate.dropCluster(iClusterName);
	}

	public int getUsers() {
		delegate.setSessionId(sessionId);
		return delegate.getUsers();
	}

	public int addUser() {
		delegate.setSessionId(sessionId);
		return delegate.addUser();
	}

	public OSharedResourceAdaptive getLock() {
		delegate.setSessionId(sessionId);
		return delegate.getLock();
	}

	public void setSessionId(final int iSessionId) {
		sessionId = iSessionId;
		delegate.setSessionId(iSessionId);
	}

	public void reload() {
		delegate.setSessionId(sessionId);
		delegate.reload();
	}

	public boolean exists() {
		delegate.setSessionId(sessionId);
		return delegate.exists();
	}

	public int removeUser() {
		delegate.setSessionId(sessionId);
		return delegate.removeUser();
	}

	public void close() {
		delegate.setSessionId(sessionId);
		delegate.close();
	}

	public void delete() {
		delegate.setSessionId(sessionId);
		delegate.delete();
		Orient.instance().unregisterStorage(this);
	}

	public Set<String> getClusterNames() {
		delegate.setSessionId(sessionId);
		return delegate.getClusterNames();
	}

	public long createRecord(final ORecordId iRid, final byte[] iContent, final byte iRecordType, final int iMode,
			ORecordCallback<Long> iCallback) {
		delegate.setSessionId(sessionId);
		return delegate.createRecord(iRid, iContent, iRecordType, iMode, null);
	}

	public ORawBuffer readRecord(final ORecordId iRid, final String iFetchPlan, ORecordCallback<ORawBuffer> iCallback) {
		delegate.setSessionId(sessionId);
		return delegate.readRecord(iRid, iFetchPlan, null);
	}

	public int updateRecord(final ORecordId iRid, final byte[] iContent, final int iVersion, final byte iRecordType, final int iMode,
			ORecordCallback<Integer> iCallback) {
		delegate.setSessionId(sessionId);
		return delegate.updateRecord(iRid, iContent, iVersion, iRecordType, iMode, null);
	}

	public boolean deleteRecord(final ORecordId iRid, final int iVersion, final int iMode, ORecordCallback<Boolean> iCallback) {
		delegate.setSessionId(sessionId);
		return delegate.deleteRecord(iRid, iVersion, iMode, null);
	}

	public long count(final int iClusterId) {
		delegate.setSessionId(sessionId);
		return delegate.count(iClusterId);
	}

	public String toString() {
		delegate.setSessionId(sessionId);
		return delegate.toString();
	}

	public long[] getClusterDataRange(final int iClusterId) {
		delegate.setSessionId(sessionId);
		return delegate.getClusterDataRange(iClusterId);
	}

	public long getSize() {
		delegate.setSessionId(sessionId);
		return delegate.getSize();
	}

	public long countRecords() {
		delegate.setSessionId(sessionId);
		return delegate.countRecords();
	}

	public long count(final int[] iClusterIds) {
		delegate.setSessionId(sessionId);
		return delegate.count(iClusterIds);
	}

	public Object command(final OCommandRequestText iCommand) {
		delegate.setSessionId(sessionId);
		return delegate.command(iCommand);
	}

	public void commit(final OTransaction iTx) {
		delegate.setSessionId(sessionId);
		delegate.commit(iTx);
	}

	public void rollback(OTransaction iTx) {
		delegate.setSessionId(sessionId);
		delegate.rollback(iTx);
	}

	public int getClusterIdByName(final String iClusterName) {
		delegate.setSessionId(sessionId);
		return delegate.getClusterIdByName(iClusterName);
	}

	public String getClusterTypeByName(final String iClusterName) {
		delegate.setSessionId(sessionId);
		return delegate.getClusterTypeByName(iClusterName);
	}

	public int getDefaultClusterId() {
		delegate.setSessionId(sessionId);
		return delegate.getDefaultClusterId();
	}

	public int addCluster(final String iClusterName, final CLUSTER_TYPE iClusterType, final Object... iArguments) {
		delegate.setSessionId(sessionId);
		return delegate.addCluster(iClusterName, iClusterType, iArguments);
	}

	public boolean dropCluster(final int iClusterId) {
		delegate.setSessionId(sessionId);
		return delegate.dropCluster(iClusterId);
	}

	public int addDataSegment(final String iDataSegmentName) {
		delegate.setSessionId(sessionId);
		return delegate.addDataSegment(iDataSegmentName);
	}

	public int addDataSegment(final String iSegmentName, final String iSegmentFileName) {
		delegate.setSessionId(sessionId);
		return delegate.addDataSegment(iSegmentName, iSegmentFileName);
	}

	public void synch() {
		delegate.setSessionId(sessionId);
		delegate.synch();
	}

	public String getPhysicalClusterNameById(final int iClusterId) {
		delegate.setSessionId(sessionId);
		return delegate.getPhysicalClusterNameById(iClusterId);
	}

	public int getClusters() {
		delegate.setSessionId(sessionId);
		return delegate.getClusters();
	}

	public Collection<OCluster> getClusterInstances() {
		delegate.setSessionId(sessionId);
		return delegate.getClusterInstances();
	}

	public OCluster getClusterById(final int iId) {
		delegate.setSessionId(sessionId);
		return delegate.getClusterById(iId);
	}

	/**
	 * Method that completes the cluster rename operation. <strong>IT WILL NOT RENAME A CLUSTER, IT JUST CHANGES THE NAME IN THE
	 * INTERNAL MAPPING</strong>
	 */
	public void renameCluster(String iOldName, String iNewName) {
		delegate.setSessionId(sessionId);
		delegate.renameCluster(iOldName, iNewName);
	}

	public long getVersion() {
		delegate.setSessionId(sessionId);
		return delegate.getVersion();
	}

	public boolean isPermanentRequester() {
		delegate.setSessionId(sessionId);
		return delegate.isPermanentRequester();
	}

	public void updateClusterConfiguration(final ODocument iContent) {
		delegate.setSessionId(sessionId);
		delegate.updateClusterConfiguration(iContent);
	}

	public OStorageConfiguration getConfiguration() {
		delegate.setSessionId(sessionId);
		return delegate.getConfiguration();
	}

	public boolean isClosed() {
		delegate.setSessionId(sessionId);
		return delegate.isClosed();
	}

	public boolean checkForRecordValidity(final OPhysicalPosition ppos) {
		delegate.setSessionId(sessionId);
		return delegate.checkForRecordValidity(ppos);
	}

	public String getName() {
		delegate.setSessionId(sessionId);
		return delegate.getName();
	}

	public String getURL() {
		return delegate.getURL();
	}

	public void beginResponse(final OChannelBinaryClient iNetwork) throws IOException {
		delegate.setSessionId(sessionId);
		delegate.beginResponse(iNetwork);
	}

	public OLevel2RecordCache getLevel2Cache() {
		return delegate.getLevel2Cache();
	}

	public boolean existsResource(final String iName) {
		return delegate.existsResource(iName);
	}

	public synchronized <T> T getResource(final String iName, final Callable<T> iCallback) {
		return (T) delegate.getResource(iName, iCallback);
	}

	public <T> T removeResource(final String iName) {
		return (T) delegate.removeResource(iName);
	}

	public ODocument getClusterConfiguration() {
		return delegate.getClusterConfiguration();
	}

	public void closeChannel(final OChannelBinaryClient network) {
		delegate.closeChannel(network);
	}

	protected void handleException(final String iMessage, final Exception iException) {
		delegate.handleException(iMessage, iException);
	}

	public <V> V callInLock(final Callable<V> iCallable, final boolean iExclusiveLock) {
		return delegate.callInLock(iCallable, iExclusiveLock);
	}

	public ORemoteServerEventListener getRemoteServerEventListener() {
		return delegate.getAsynchEventListener();
	}

	public void setRemoteServerEventListener(final ORemoteServerEventListener iListener) {
		delegate.setAsynchEventListener(iListener);
	}

	public void removeRemoteServerEventListener() {
		delegate.removeRemoteServerEventListener();
	}

	public static int getNextConnectionId() {
		return sessionSerialId.decrementAndGet();
	}
}
