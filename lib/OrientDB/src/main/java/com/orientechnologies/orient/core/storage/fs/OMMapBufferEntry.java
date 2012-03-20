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
package com.orientechnologies.orient.core.storage.fs;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.common.profiler.OProfiler;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.memory.OMemoryWatchDog;

public class OMMapBufferEntry implements Comparable<OMMapBufferEntry> {
	private static final int	FORCE_DELAY;
	private static final int	FORCE_RETRY;

	static Class<?>						sunClass	= null;
	OFileMMap									file;
	MappedByteBuffer					buffer;
	long											beginOffset;
	int												size;
	long											counter;
	volatile boolean					dirty;

	static {
		FORCE_DELAY = OGlobalConfiguration.FILE_MMAP_FORCE_DELAY.getValueAsInteger();
		FORCE_RETRY = OGlobalConfiguration.FILE_MMAP_FORCE_RETRY.getValueAsInteger();

		// GET SUN JDK METHOD TO CLEAN MMAP BUFFERS
		try {
			// sunClass = Class.forName("sun.nio.ch.DirectBuffer");
		} catch (Exception e) {
			// IGNORE IT AND USE GC TO FREE RESOURCES
		}
	}

	public OMMapBufferEntry(final OFileMMap iFile, final MappedByteBuffer buffer, final long beginOffset, final int size) {
		this.file = iFile;
		this.buffer = buffer;
		this.beginOffset = beginOffset;
		this.size = size;
		this.counter = 0;
		this.dirty = false;
	}

	/**
	 * Flushes the memory mapped buffer to disk only if it's dirty.
	 * 
	 * @return true if the buffer has been successfully flushed, otherwise false.
	 */
	public boolean flush() {
		if (!dirty)
			return true;

		final long timer = OProfiler.getInstance().startChrono();

		// FORCE THE WRITE OF THE BUFFER
		for (int i = 0; i < FORCE_RETRY; ++i) {
			try {
				buffer.force();
				dirty = false;
				break;
			} catch (Exception e) {
				OLogManager.instance().debug(this, "Cannot write memory buffer to disk. Retrying (" + (i + 1) + "/" + FORCE_RETRY + ")...");
				OMemoryWatchDog.freeMemory(FORCE_DELAY);
			}
		}

		if (dirty)
			OLogManager.instance().debug(this, "Cannot commit memory buffer to disk after %d retries", FORCE_RETRY);
		else
			OProfiler.getInstance().updateCounter("OMMapManager.pagesCommitted", 1);

		OProfiler.getInstance().stopChrono("OMMapManager.commitPages", timer);

		return !dirty;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("OMMapBufferEntry [file=").append(file).append(", beginOffset=").append(beginOffset).append(", size=")
				.append(size).append("]");
		return builder.toString();
	}

	/**
	 * Force closing of file is it's opened yet.
	 */
	public void close() {
		flush();

		if (file != null) {
			if (!file.isClosed()) {
				try {
					file.close();
				} catch (IOException e) {
				}
			}
			file = null;
		}

		if (buffer != null && sunClass != null) {
			// USE SUN JVM SPECIAL METHOD TO FREE RESOURCES
			try {
				final Method m = sunClass.getMethod("cleaner");
				final Object cleaner = m.invoke(buffer);
				cleaner.getClass().getMethod("clean").invoke(cleaner);
			} catch (Exception e) {
				OLogManager.instance().error(this, "Error on calling MMap buffer clean", e);
			}
		}

		buffer = null;
		counter = 0;
	}

	public int compareTo(final OMMapBufferEntry iOther) {
		return (int) (beginOffset - iOther.beginOffset);
	}

	public boolean isValid() {
		return buffer != null;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty() {
		this.dirty = true;
	}
}