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
package com.orientechnologies.orient.core.iterator;

import java.util.Iterator;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordAbstract;
import com.orientechnologies.orient.core.record.impl.ODocument;

@SuppressWarnings("unchecked")
public class OObjectIteratorClass<T> implements Iterator<T>, Iterable<T> {
	private ODatabaseObject									database;
	private ORecordIteratorClass<ODocument>	underlying;
	private String													fetchPlan;

	public OObjectIteratorClass(final ODatabaseObject iDatabase, final ODatabaseRecordAbstract iUnderlyingDatabase,
			final String iClusterName, final boolean iPolymorphic) {
		database = iDatabase;
		underlying = new ORecordIteratorClass<ODocument>((ODatabaseRecord) iDatabase.getUnderlying(), iUnderlyingDatabase,
				iClusterName, iPolymorphic);
	}

	public boolean hasNext() {
		return underlying.hasNext();
	}

	public T next() {
		return next(fetchPlan);
	}

	public T next(final String iFetchPlan) {
		return (T) database.getUserObjectByRecord(underlying.next(), iFetchPlan);
	}

	public void remove() {
		underlying.remove();
	}

	public Iterator<T> iterator() {
		return this;
	}

	public String getFetchPlan() {
		return fetchPlan;
	}

	public OObjectIteratorClass<T> setFetchPlan(String fetchPlan) {
		this.fetchPlan = fetchPlan;
		return this;
	}

}
