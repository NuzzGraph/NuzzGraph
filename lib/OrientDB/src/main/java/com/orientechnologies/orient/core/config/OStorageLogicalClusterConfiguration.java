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
package com.orientechnologies.orient.core.config;

import com.orientechnologies.orient.core.id.ORID;

public class OStorageLogicalClusterConfiguration implements OStorageClusterConfiguration {
	public String	name;
	public int		id;
	public int		physicalClusterId;
	public ORID		map;

	public OStorageLogicalClusterConfiguration(final String name, final int id, final int iPhysicalClusterId, final ORID map) {
		this.name = name;
		this.id = id;
		this.physicalClusterId = iPhysicalClusterId;
		this.map = map;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(final int iId) {
		id = iId;
	}
}
