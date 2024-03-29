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
package com.orientechnologies.common.concur.lock;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.common.profiler.OProfiler;

public class OLockException extends OException {
	private static final long	serialVersionUID	= 2215169397325875189L;

	public OLockException(String iMessage) {
		super(iMessage);
		OProfiler.getInstance().updateCounter("OLockException", +1);
	}

	public OLockException(String iMessage, Exception iException) {
		super(iMessage, iException);
		OProfiler.getInstance().updateCounter("OLockException", +1);
	}
}
