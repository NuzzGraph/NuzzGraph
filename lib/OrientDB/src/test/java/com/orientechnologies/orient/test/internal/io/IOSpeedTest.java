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
package com.orientechnologies.orient.test.internal.io;

import org.testng.annotations.Test;

import com.orientechnologies.common.test.SpeedTestGroup;

@Test(enabled = false)
public class IOSpeedTest extends SpeedTestGroup {
	protected static final int	TEST_CYCLES	= 1;

	public static void main(String[] iArgs) {
		new IOSpeedTest().testOnce();
	}

	public void testOnce() {
		addTest(new OMMapFileTest()).data().setCycles(TEST_CYCLES);

		addTest(new OClassicFileTest()).data().setCycles(TEST_CYCLES);
		go();
	}
}
