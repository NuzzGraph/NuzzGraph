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
package com.orientechnologies.orient.core.type.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.orientechnologies.common.collection.OMVRBTree;
import com.orientechnologies.common.collection.OMVRBTreeEntry;
import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.common.profiler.OProfiler;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.memory.OLowMemoryException;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.type.tree.provider.OMVRBTreeProvider;

/**
 * Persistent based MVRB-Tree implementation. The difference with the class OMVRBTreePersistent is the level. In facts this class
 * works directly at the storage level, while the other at database level. This class is used for Logical Clusters. It can'be
 * transactional. It uses the entryPoints tree-map to get the closest entry point where start searching a node.
 * 
 */
@SuppressWarnings("serial")
public abstract class OMVRBTreePersistent<K, V> extends OMVRBTree<K, V> {

	protected final OMVRBTreeProvider<K, V>										dataProvider;
	protected ORecord<?>																			owner;
	protected final Set<OMVRBTreeEntryPersistent<K, V>>				recordsToCommit			= new HashSet<OMVRBTreeEntryPersistent<K, V>>();

	// STORES IN MEMORY DIRECT REFERENCES TO PORTION OF THE TREE
	protected int																							optimizeThreshold;
	protected volatile int																		optimization				= 0;
	private int																								insertionCounter		= 0;
	protected int																							entryPointsSize;

	protected float																						optimizeEntryPointsFactor;
	private final TreeMap<K, OMVRBTreeEntryPersistent<K, V>>	entryPoints					= new TreeMap<K, OMVRBTreeEntryPersistent<K, V>>();
	private final Map<ORID, OMVRBTreeEntryPersistent<K, V>>		cache								= new HashMap<ORID, OMVRBTreeEntryPersistent<K, V>>();

	private static final int																	OPTIMIZE_MAX_RETRY	= 10;

	public OMVRBTreePersistent(OMVRBTreeProvider<K, V> iProvider) {
		super();
		pageLoadFactor = (Float) OGlobalConfiguration.MVRBTREE_LOAD_FACTOR.getValue();
		dataProvider = iProvider;
		config();
	}

	@Override
	protected OMVRBTreeEntryPersistent<K, V> createEntry(OMVRBTreeEntry<K, V> iParent) {
		adjustPageSize();
		return new OMVRBTreeEntryPersistent<K, V>(iParent, iParent.getPageSplitItems());
	}

	@Override
	protected OMVRBTreeEntryPersistent<K, V> createEntry(final K key, final V value) {
		adjustPageSize();
		return new OMVRBTreeEntryPersistent<K, V>(this, key, value, null);
	}

	/**
	 * Create a new entry for {@link #loadEntry(OMVRBTreeEntryPersistent, ORID)}.
	 */
	protected OMVRBTreeEntryPersistent<K, V> createEntry(OMVRBTreeEntryPersistent<K, V> iParent, ORID iRecordId) {
		return new OMVRBTreeEntryPersistent<K, V>(this, iParent, iRecordId);
	}

	public OMVRBTreePersistent<K, V> load() {
		dataProvider.load();

		// RESET LAST SEARCH STATE
		setLastSearchNode(null, null);

		// LOAD THE ROOT OBJECT AFTER ALL
		final ORID rootRid = dataProvider.getRoot();
		if (rootRid != null && rootRid.isValid())
			root = loadEntry(null, rootRid);
		return this;
	}

	protected void initAfterLoad() throws IOException {
	}

	public OMVRBTreePersistent<K, V> save() {
		commitChanges();
		return this;
	}

	protected void saveTreeNode() throws IOException {
		if (root != null) {
			OMVRBTreeEntryPersistent<K, V> pRoot = (OMVRBTreeEntryPersistent<K, V>) root;
			if (pRoot.getProvider().getIdentity().isNew()) {
				// FIRST TIME: SAVE IT
				pRoot.save();
			}
		}

		dataProvider.save();
	}

	/**
	 * Lazy loads a node.
	 */
	protected OMVRBTreeEntryPersistent<K, V> loadEntry(final OMVRBTreeEntryPersistent<K, V> iParent, final ORID iRecordId) {
		// SEARCH INTO THE CACHE
		OMVRBTreeEntryPersistent<K, V> entry = searchNodeInCache(iRecordId);
		if (entry == null) {
			// NOT FOUND: CREATE IT AND PUT IT INTO THE CACHE
			entry = createEntry(iParent, iRecordId);
			addNodeInMemory(entry);

			// RECONNECT THE LOADED NODE WITH IN-MEMORY PARENT, LEFT AND RIGHT
			if (entry.parent == null && entry.dataProvider.getParent().isValid()) {
				// TRY TO ASSIGN THE PARENT IN CACHE IF ANY
				final OMVRBTreeEntryPersistent<K, V> parentNode = searchNodeInCache(entry.dataProvider.getParent());
				if (parentNode != null)
					entry.setParent(parentNode);
			}

			if (entry.left == null && entry.dataProvider.getLeft().isValid()) {
				// TRY TO ASSIGN THE PARENT IN CACHE IF ANY
				final OMVRBTreeEntryPersistent<K, V> leftNode = searchNodeInCache(entry.dataProvider.getLeft());
				if (leftNode != null)
					entry.setLeft(leftNode);
			}

			if (entry.right == null && entry.dataProvider.getRight().isValid()) {
				// TRY TO ASSIGN THE PARENT IN CACHE IF ANY
				final OMVRBTreeEntryPersistent<K, V> rightNode = searchNodeInCache(entry.dataProvider.getRight());
				if (rightNode != null)
					entry.setRight(rightNode);
			}

		} else {
			// COULD BE A PROBLEM BECAUSE IF A NODE IS DISCONNECTED CAN IT STAY IN CACHE?
			// entry.load();
			if (iParent != null)
				// FOUND: ASSIGN IT ONLY IF NOT NULL
				entry.setParent(iParent);
		}

		entry.checkEntryStructure();

		return entry;
	}

	public int size() {
		return dataProvider.getSize();
	}

	protected void setSize(final int iSize) {
		if (dataProvider.setSize(iSize))
			markDirty();
	}

	public int getDefaultPageSize() {
		return dataProvider.getDefaultPageSize();
	}

	@Override
	public void clear() {
		final long timer = OProfiler.getInstance().startChrono();

		try {
			recordsToCommit.clear();
			if (root != null) {
				try {
					((OMVRBTreeEntryPersistent<K, V>) root).delete();
				} catch (ORecordNotFoundException e) {
				}
				super.clear();
				markDirty();
			}
			entryPoints.clear();
			cache.clear();

		} catch (IOException e) {
			OLogManager.instance().error(this, "Error on deleting the tree: " + dataProvider, e, OStorageException.class);
		} finally {
			OProfiler.getInstance().stopChrono("OMVRBTreePersistent.clear", timer);
		}
	}

	public void delete() {
		clear();
		dataProvider.delete();
	}

	/**
	 * Unload all the in-memory nodes. This is called on transaction rollback.
	 */
	public void unload() {
		final long timer = OProfiler.getInstance().startChrono();

		try {
			// DISCONNECT ALL THE NODES
			for (OMVRBTreeEntryPersistent<K, V> entryPoint : entryPoints.values())
				entryPoint.disconnectLinked(true);
			entryPoints.clear();
			cache.clear();

			recordsToCommit.clear();
			root = null;

			load();

		} catch (Exception e) {
			OLogManager.instance().error(this, "Error on unload the tree: " + dataProvider, e, OStorageException.class);
		} finally {
			OProfiler.getInstance().stopChrono("OMVRBTreePersistent.unload", timer);
		}
	}

	/**
	 * Calls the optimization in soft mode: free resources only if needed.
	 */
	protected void optimize() {
		optimize(false);
	}

	/**
	 * Optimizes the memory needed by the tree in memory by reducing the number of entries to the configured size.
	 * 
	 * @return The total freed nodes
	 */
	public int optimize(final boolean iForce) {
		if (optimization == -1)
			// IS ALREADY RUNNING
			return 0;

		if (!iForce && optimization == 0)
			// NO OPTIMIZATION IS NEEDED
			return 0;

		// SET OPTIMIZATION STATUS AS RUNNING
		optimization = -1;

		final long timer = OProfiler.getInstance().startChrono();

		try {
			if (root == null)
				return 0;

			if (OLogManager.instance().isDebugEnabled())
				OLogManager.instance().debug(this, "Starting optimization of MVRB+Tree with %d items in memory...", cache.size());

			// printInMemoryStructure();

			if (entryPoints.size() == 0)
				// FIRST TIME THE LIST IS NULL: START FROM ROOT
				addNodeAsEntrypoint((OMVRBTreeEntryPersistent<K, V>) root);

			// RECONFIG IT TO CATCH CHANGED VALUES
			config();

			if (OLogManager.instance().isDebugEnabled())
				OLogManager.instance().debug(this, "Found %d items on disk, threshold=%f, entryPoints=%d, nodesInCache=%d", size(),
						(entryPointsSize * optimizeEntryPointsFactor), entryPoints.size(), cache.size());

			final int nodesInMemory = cache.size();

			if (!iForce && nodesInMemory < entryPointsSize * optimizeEntryPointsFactor)
				// UNDER THRESHOLD AVOID TO OPTIMIZE
				return 0;

			lastSearchFound = false;
			lastSearchKey = null;
			lastSearchNode = null;

			int totalDisconnected = 0;

			if (nodesInMemory > entryPointsSize) {
				// REDUCE THE ENTRYPOINTS
				final int distance = nodesInMemory / entryPointsSize + 1;

				final Set<OMVRBTreeEntryPersistent<K, V>> entryPointsToRemove = new HashSet<OMVRBTreeEntryPersistent<K, V>>(nodesInMemory
						- entryPointsSize + 2);

				// REMOVE ENTRYPOINTS AT THE SAME DISTANCE
				int currNode = 0;
				for (final Iterator<OMVRBTreeEntryPersistent<K, V>> it = entryPoints.values().iterator(); it.hasNext();) {
					final OMVRBTreeEntryPersistent<K, V> currentNode = it.next();

					// JUMP THE FIRST (1 cannot never be the % of distance) THE LAST, ROOT AND LAST USED
					if (currentNode != root && currentNode != lastSearchNode && it.hasNext())
						if (++currNode % distance != 0) {
							// REMOVE THE NODE
							entryPointsToRemove.add(currentNode);
							it.remove();
						}
				}
				addNodeAsEntrypoint((OMVRBTreeEntryPersistent<K, V>) lastSearchNode);
				addNodeAsEntrypoint((OMVRBTreeEntryPersistent<K, V>) root);

				// DISCONNECT THE REMOVED NODES
				for (OMVRBTreeEntryPersistent<K, V> currentNode : entryPointsToRemove)
					totalDisconnected += currentNode.disconnectLinked(false);
			}

			if (isRuntimeCheckEnabled()) {
				for (OMVRBTreeEntryPersistent<K, V> entryPoint : entryPoints.values())
					for (OMVRBTreeEntryPersistent<K, V> e = (OMVRBTreeEntryPersistent<K, V>) entryPoint.getFirstInMemory(); e != null; e = e
							.getNextInMemory())
						e.checkEntryStructure();
			}

			// COUNT ALL IN-MEMORY NODES BY BROWSING ALL THE ENTRYPOINT NODES
			if (OLogManager.instance().isDebugEnabled())
				OLogManager.instance().debug(this, "After optimization: %d items on disk, threshold=%f, entryPoints=%d, nodesInCache=%d",
						size(), (entryPointsSize * optimizeEntryPointsFactor), entryPoints.size(), cache.size());

			if (debug) {
				int i = 0;
				System.out.println();
				for (OMVRBTreeEntryPersistent<K, V> entryPoint : entryPoints.values())
					System.out.println("- Entrypoint " + ++i + "/" + entryPoints.size() + ": " + entryPoint);
			}

			return totalDisconnected;
		} finally {
			optimization = 0;
			if (isRuntimeCheckEnabled()) {
				if (!entryPoints.isEmpty())
					for (OMVRBTreeEntryPersistent<K, V> entryPoint : entryPoints.values())
						checkTreeStructure(entryPoint.getFirstInMemory());
				else
					checkTreeStructure(root);
			}

			OProfiler.getInstance().stopChrono("OMVRBTreePersistent.optimize", timer);

			if (OLogManager.instance().isDebugEnabled())
				OLogManager.instance().debug(this, "Optimization completed in %d ms\n", System.currentTimeMillis() - timer);
		}
	}

	@Override
	public V put(final K key, final V value) {
		optimize();
		final long timer = OProfiler.getInstance().startChrono();

		try {
			final V v = internalPut(key, value);
			commitChanges();
			return v;
		} finally {

			OProfiler.getInstance().stopChrono("OMVRBTreePersistent.put", timer);
		}
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> map) {
		final long timer = OProfiler.getInstance().startChrono();

		try {
			for (Entry<? extends K, ? extends V> entry : map.entrySet())
				internalPut(entry.getKey(), entry.getValue());

			commitChanges();

		} finally {
			OProfiler.getInstance().stopChrono("OMVRBTreePersistent.putAll", timer);
		}
	}

	@Override
	public V remove(final Object key) {
		optimize();
		final long timer = OProfiler.getInstance().startChrono();

		try {
			for (int i = 0; i < OPTIMIZE_MAX_RETRY; ++i) {
				try {

					V v = super.remove(key);
					commitChanges();
					return v;

				} catch (OLowMemoryException e) {
					OLogManager.instance().debug(this, "Optimization required during remove %d/%d", i, OPTIMIZE_MAX_RETRY);

					// LOW MEMORY DURING REMOVAL: THIS MEANS DEEP LOADING OF NODES. EXECUTE THE OPTIMIZATION AND RETRY IT
					optimize(true);

					System.gc();

					if (i > 0)
						// WAIT A PROPORTIONAL TIME
						try {
							Thread.sleep(300 * i);
						} catch (InterruptedException e1) {
						}

					// AVOID CONTINUE EXCEPTIONS
					optimization = -1;
				}
			}
		} finally {
			OProfiler.getInstance().stopChrono("OMVRBTreePersistent.remove", timer);
		}

		throw new OLowMemoryException("OMVRBTreePersistent.remove()");
	}

	public int commitChanges() {
		final long timer = OProfiler.getInstance().startChrono();

		int totalCommitted = 0;
		try {

			if (!recordsToCommit.isEmpty()) {
				final List<OMVRBTreeEntryPersistent<K, V>> tmp = new ArrayList<OMVRBTreeEntryPersistent<K, V>>();

				while (recordsToCommit.iterator().hasNext()) {
					// COMMIT BEFORE THE NEW RECORDS (TO ASSURE RID IN RELATIONSHIPS)
					tmp.addAll(recordsToCommit);

					recordsToCommit.clear();

					for (OMVRBTreeEntryPersistent<K, V> node : tmp)
						if (node.dataProvider.isEntryDirty()) {
							boolean wasNew = node.dataProvider.getIdentity().isNew();

							// CREATE THE RECORD
							node.save();

							if (debug)
								System.out.printf("\nSaved %s tree node %s: parent %s, left %s, right %s", wasNew ? "new" : "",
										node.dataProvider.getIdentity(), node.dataProvider.getParent(), node.dataProvider.getLeft(),
										node.dataProvider.getRight());
						}

					totalCommitted += tmp.size();
					tmp.clear();
				}
			}

			if (dataProvider.isDirty())
				// TREE IS CHANGED AS WELL
				saveTreeNode();

		} catch (IOException e) {
			OLogManager.instance().exception("Error on saving the tree", e, OStorageException.class);

		} finally {

			OProfiler.getInstance().stopChrono("OMVRBTreePersistent.commitChanges", timer);
		}

		return totalCommitted;
	}

	public void signalNodeChanged(final OMVRBTreeEntry<K, V> iNode) {
		recordsToCommit.add((OMVRBTreeEntryPersistent<K, V>) iNode);
	}

	@Override
	public int hashCode() {
		return dataProvider.hashCode();
	}

	protected void adjustPageSize() {
	}

	@Override
	public V get(final Object iKey) {
		for (int i = 0; i < OPTIMIZE_MAX_RETRY; ++i) {
			try {
				return super.get(iKey);
			} catch (OLowMemoryException e) {
				OLogManager.instance().debug(this, "Optimization required during node search %d/%d", i, OPTIMIZE_MAX_RETRY);

				// LOW MEMORY DURING LOAD: THIS MEANS DEEP LOADING OF NODES. EXECUTE THE OPTIMIZATION AND RETRY IT
				optimize(true);

				System.gc();

				if (i > 0)
					// WAIT A PROPORTIONAL TIME
					try {
						Thread.sleep(300 * i);
					} catch (InterruptedException e1) {
					}
			}
		}

		throw new OLowMemoryException("OMVRBTreePersistent.get()");
	}

	@Override
	public boolean containsKey(final Object iKey) {
		for (int i = 0; i < OPTIMIZE_MAX_RETRY; ++i) {
			try {
				return super.containsKey(iKey);
			} catch (OLowMemoryException e) {
				OLogManager.instance().debug(this, "Optimization required during node search %d/%d", i, OPTIMIZE_MAX_RETRY);

				// LOW MEMORY DURING LOAD: THIS MEANS DEEP LOADING OF NODES. EXECUTE THE OPTIMIZATION AND RETRY IT
				optimize(true);

				System.gc();

				if (i > 0)
					// WAIT A PROPORTIONAL TIME
					try {
						Thread.sleep(300 * i);
					} catch (InterruptedException e1) {
					}
			}
		}

		throw new OLowMemoryException("OMVRBTreePersistent.containsKey()");
	}

	@Override
	public boolean containsValue(final Object iValue) {
		for (int i = 0; i < OPTIMIZE_MAX_RETRY; ++i) {
			try {
				return super.containsValue(iValue);
			} catch (OLowMemoryException e) {
				OLogManager.instance().debug(this, "Optimization required during node search %d/%d", i, OPTIMIZE_MAX_RETRY);

				// LOW MEMORY DURING LOAD: THIS MEANS DEEP LOADING OF NODES. EXECUTE THE OPTIMIZATION AND RETRY IT
				optimize(true);

				System.gc();

				if (i > 0)
					// WAIT A PROPORTIONAL TIME
					try {
						Thread.sleep(300 * i);
					} catch (InterruptedException e1) {
					}
			}
		}

		throw new OLowMemoryException("OMVRBTreePersistent.containsValue()");
	}

	public OMVRBTreeProvider<K, V> getProvider() {
		return dataProvider;
	}

	public int getOptimization() {
		return optimization;
	}

	public void setOptimization(final int i) {
		if (i > 0 && optimization == -1)
			// IGNORE IT, ALREADY RUNNING
			return;

		optimization = i;
	}

	/**
	 * Checks if optimization is needed by raising a {@link OLowMemoryException}.
	 */
	@Override
	protected void searchNodeCallback() {
		if (optimization > 0)
			throw new OLowMemoryException("Optimization level: " + optimization);
	}

	public int getOptimizeThreshold() {
		return optimizeThreshold;
	}

	public void setOptimizeThreshold(int iOptimizeThreshold) {
		this.optimizeThreshold = iOptimizeThreshold;
	}

	public int getEntryPointSize() {
		return entryPointsSize;
	}

	public void setEntryPointSize(int entryPointSize) {
		this.entryPointsSize = entryPointSize;
	}

	@Override
	public String toString() {

		final StringBuilder buffer = new StringBuilder();
		buffer.append("size=");
		final int size = size();
		buffer.append(size);

		final OMVRBTreeEntry<K, V> firstEntry = getFirstEntry();

		if (firstEntry != null) {
			final int currPageIndex = pageIndex;
			buffer.append(" ");
			buffer.append(firstEntry.getFirstKey());
			if (size > 1) {
				buffer.append("-");
				buffer.append(getLastEntry().getLastKey());
			}
			pageIndex = currPageIndex;
		}

		return buffer.toString();
	}

	protected V internalPut(final K key, final V value) throws OLowMemoryException {
		ORecordInternal<?> rec;

		if (key instanceof ORecordInternal<?>) {
			// RECORD KEY: ASSURE IT'S PERSISTENT TO AVOID STORING INVALID RIDs
			rec = (ORecordInternal<?>) key;
			if (!rec.getIdentity().isValid())
				rec.save();
		}

		if (value instanceof ORecordInternal<?>) {
			// RECORD VALUE: ASSURE IT'S PERSISTENT TO AVOID STORING INVALID RIDs
			rec = (ORecordInternal<?>) value;
			if (!rec.getIdentity().isValid())
				rec.save();
		}

		for (int i = 0; i < OPTIMIZE_MAX_RETRY; ++i) {
			try {
				final V previous = super.put(key, value);

				if (optimizeThreshold > -1 && ++insertionCounter >= optimizeThreshold) {
					insertionCounter = 0;
					optimization = 2;
					optimize(true);
				}

				return previous;
			} catch (OLowMemoryException e) {
				OLogManager.instance().debug(this, "Optimization required during put %d/%d", i, OPTIMIZE_MAX_RETRY);

				// LOW MEMORY DURING PUT: THIS MEANS DEEP LOADING OF NODES. EXECUTE THE OPTIMIZATION AND RETRY IT
				optimize(true);

				System.gc();

				if (i > 0)
					// WAIT A PROPORTIONAL TIME
					try {
						Thread.sleep(300 * i);
					} catch (InterruptedException e1) {
					}
			}
		}

		throw new OLowMemoryException("OMVRBTreePersistent.put()");
	}

	/**
	 * Returns the best entry point to start the search. Searches first between entrypoints. If nothing is found "root" is always
	 * returned.
	 */
	@Override
	protected OMVRBTreeEntry<K, V> getBestEntryPoint(final K iKey) {
		if (!entryPoints.isEmpty()) {
			// SEARCHES EXACT OR BIGGER ENTRY
			Entry<K, OMVRBTreeEntryPersistent<K, V>> closerNode = entryPoints.floorEntry(iKey);
			if (closerNode != null)
				return closerNode.getValue();

			// NO WAY: TRY WITH ANY NODE BEFORE THE KEY
			closerNode = entryPoints.ceilingEntry(iKey);
			if (closerNode != null)
				return closerNode.getValue();
		}

		// USE ROOT
		return super.getBestEntryPoint(iKey);
	}

	/**
	 * Remove an entry point from the list
	 */
	void removeEntryPoint(final OMVRBTreeEntryPersistent<K, V> iEntry) {
		entryPoints.remove(iEntry);
	}

	synchronized void removeEntry(final ORID iEntryId) {
		// DELETE THE NODE FROM THE PENDING RECORDS TO COMMIT
		for (OMVRBTreeEntryPersistent<K, V> node : recordsToCommit) {
			if (node.dataProvider.getIdentity().equals(iEntryId)) {
				recordsToCommit.remove(node);
				break;
			}
		}
	}

	/**
	 * Returns the first Entry in the OMVRBTree (according to the OMVRBTree's key-sort function). Returns null if the OMVRBTree is
	 * empty.
	 */
	@Override
	protected OMVRBTreeEntry<K, V> getFirstEntry() {
		if (!entryPoints.isEmpty()) {
			// FIND THE FIRST ELEMENT STARTING FROM THE FIRST ENTRY-POINT IN MEMORY
			final Map.Entry<K, OMVRBTreeEntryPersistent<K, V>> entry = entryPoints.firstEntry();

			if (entry != null) {
				OMVRBTreeEntryPersistent<K, V> e = entry.getValue();

				OMVRBTreeEntryPersistent<K, V> prev;
				do {
					prev = (OMVRBTreeEntryPersistent<K, V>) predecessor(e);
					if (prev != null)
						e = prev;
				} while (prev != null);

				if (e != null && e.getSize() > 0)
					pageIndex = 0;

				return e;
			}
		}

		// SEARCH FROM ROOT
		return super.getFirstEntry();
	}

	/**
	 * Returns the last Entry in the OMVRBTree (according to the OMVRBTree's key-sort function). Returns null if the OMVRBTree is
	 * empty.
	 */
	@Override
	protected OMVRBTreeEntry<K, V> getLastEntry() {
		if (!entryPoints.isEmpty()) {
			// FIND THE LAST ELEMENT STARTING FROM THE FIRST ENTRY-POINT IN MEMORY
			final Map.Entry<K, OMVRBTreeEntryPersistent<K, V>> entry = entryPoints.lastEntry();

			if (entry != null) {
				OMVRBTreeEntryPersistent<K, V> e = entry.getValue();

				OMVRBTreeEntryPersistent<K, V> next;
				do {
					next = (OMVRBTreeEntryPersistent<K, V>) successor(e);
					if (next != null)
						e = next;
				} while (next != null);

				if (e != null && e.getSize() > 0)
					pageIndex = e.getSize() - 1;

				return e;
			}
		}

		// SEARCH FROM ROOT
		return super.getLastEntry();
	}

	@Override
	protected void setRoot(final OMVRBTreeEntry<K, V> iRoot) {
		if (iRoot == root)
			return;

		super.setRoot(iRoot);

		if (iRoot == null)
			dataProvider.setRoot(null);
		else
			dataProvider.setRoot(((OMVRBTreeEntryPersistent<K, V>) iRoot).getProvider().getIdentity());
	}

	protected void config() {
		if (dataProvider.updateConfig())
			markDirty();
		pageLoadFactor = OGlobalConfiguration.MVRBTREE_LOAD_FACTOR.getValueAsFloat();
		optimizeEntryPointsFactor = OGlobalConfiguration.MVRBTREE_OPTIMIZE_ENTRYPOINTS_FACTOR.getValueAsFloat();
		optimizeThreshold = OGlobalConfiguration.MVRBTREE_OPTIMIZE_THRESHOLD.getValueAsInteger();
		entryPointsSize = OGlobalConfiguration.MVRBTREE_ENTRYPOINTS.getValueAsInteger();
	}

	@Override
	protected void rotateLeft(final OMVRBTreeEntry<K, V> p) {
		if (debug && p != null)
			System.out.printf("\nRotating to the left the node %s", ((OMVRBTreeEntryPersistent<K, V>) p).dataProvider.getIdentity());
		super.rotateLeft(p);
	}

	@Override
	protected void rotateRight(final OMVRBTreeEntry<K, V> p) {
		if (debug && p != null)
			System.out.printf("\nRotating to the right the node %s", ((OMVRBTreeEntryPersistent<K, V>) p).dataProvider.getIdentity());
		super.rotateRight(p);
	}

	/**
	 * Removes the node also from the memory.
	 */
	@Override
	protected OMVRBTreeEntry<K, V> removeNode(final OMVRBTreeEntry<K, V> p) {
    final  OMVRBTreeEntryPersistent<K, V> removed = (OMVRBTreeEntryPersistent<K, V>) super.removeNode( p );

    removeNodeFromMemory(removed);

    //this prevents NPE in case if tree contains single node and it was deleted inside of super.removeNode method.
    if(removed.getProvider() != null)
      removed.getProvider().delete();

    //prevent record saving if it has been deleted.
    recordsToCommit.remove( removed );
    return removed;
	}

	/**
	 * Removes the node from the memory.
	 * 
	 * @param iNode
	 *          Node to remove
	 */
	protected void removeNodeFromMemory(final OMVRBTreeEntryPersistent<K, V> iNode) {
		if (iNode.dataProvider != null && iNode.dataProvider.getIdentity().isValid())
			cache.remove(iNode.dataProvider.getIdentity());
		if (iNode.getSize() > 0)
			entryPoints.remove(iNode.getKeyAt(0));
	}

	protected void addNodeInMemory(final OMVRBTreeEntryPersistent<K, V> iNode) {
		addNodeAsEntrypoint(iNode);
		addNodeInCache(iNode);
	}

	protected boolean isNodeEntryPoint(final OMVRBTreeEntryPersistent<K, V> iNode) {
		if (iNode != null && iNode.getSize() > 0)
			return entryPoints.containsKey(iNode.getKeyAt(0));
		return false;
	}

	protected void addNodeAsEntrypoint(final OMVRBTreeEntryPersistent<K, V> iNode) {
		if (iNode != null && iNode.getSize() > 0)
			entryPoints.put(iNode.getKeyAt(0), iNode);
	}

	/**
	 * Updates the position of the node between the entry-points. If the node has 0 items, it's simply removed.
	 * 
	 * @param iOldKey
	 *          Old key to remove
	 * @param iNode
	 *          Node to update
	 */
	protected void updateEntryPoint(final K iOldKey, final OMVRBTreeEntryPersistent<K, V> iNode) {
		final OMVRBTreeEntryPersistent<K, V> node = entryPoints.remove(iOldKey);
		if (node != null) {
			if (node != iNode)
				OLogManager.instance().warn(this, "Entrypoints nodes are different during update: old %s <-> new %s", node, iNode);
			addNodeAsEntrypoint(iNode);
		}
	}

	/**
	 * Keeps the node in memory.
	 * 
	 * @param iNode
	 *          Node to store
	 */
	protected void addNodeInCache(final OMVRBTreeEntryPersistent<K, V> iNode) {
		if (iNode.dataProvider != null && iNode.dataProvider.getIdentity().isValid())
			cache.put(iNode.dataProvider.getIdentity(), iNode);
	}

	/**
	 * Searches the node in local cache by RID.
	 * 
	 * @param iRid
	 *          RID to search
	 * @return Node is found, otherwise NULL
	 */
	protected OMVRBTreeEntryPersistent<K, V> searchNodeInCache(final ORID iRid) {
		return cache.get(iRid);
	}

	public int getNumberOfNodesInCache() {
		return cache.size();
	}

	/**
	 * Returns all the RID of the nodes in memory.
	 */
	protected Set<ORID> getAllNodesInCache() {
		return cache.keySet();
	}

	/**
	 * Removes the node from the local cache.
	 * 
	 * @param iNode
	 *          Node to remove
	 */
	protected void removeNodeFromCache(final ORID iRid) {
		cache.remove(iRid);
	}

	protected void markDirty() {
	}

	public ORecord<?> getOwner() {
		return owner;
	}

	public OMVRBTreePersistent<K, V> setOwner(ORecord<?> owner) {
		this.owner = owner;
		return this;
	}
}
