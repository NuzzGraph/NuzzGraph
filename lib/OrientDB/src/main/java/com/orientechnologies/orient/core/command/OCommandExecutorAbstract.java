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
package com.orientechnologies.orient.core.command;

import java.util.Map;

import com.orientechnologies.common.listener.OProgressListener;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

/**
 * Abstract implementation of Executor Command interface.
 * 
 * @author Luca Garulli
 * 
 */
@SuppressWarnings("unchecked")
public abstract class OCommandExecutorAbstract extends OCommandToParse implements OCommandExecutor {
	protected OProgressListener		progressListener;
	protected int									limit	= -1;
	protected Map<Object, Object>	parameters;
	protected OCommandContext			context;

	public OCommandExecutorAbstract init(final String iText) {
		text = iText;
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [text=" + text + "]";
	}

	public OProgressListener getProgressListener() {
		return progressListener;
	}

	public <RET extends OCommandExecutor> RET setProgressListener(OProgressListener progressListener) {
		this.progressListener = progressListener;
		return (RET) this;
	}

	public int getLimit() {
		return limit;
	}

	public <RET extends OCommandExecutor> RET setLimit(final int iLimit) {
		this.limit = iLimit;
		return (RET) this;
	}

	public Map<Object, Object> getParameters() {
		return parameters;
	}

	public OCommandContext getContext() {
		return context;
	}

	public ODatabaseRecord getDatabase() {
		return ODatabaseRecordThreadLocal.INSTANCE.get();
	}
}
