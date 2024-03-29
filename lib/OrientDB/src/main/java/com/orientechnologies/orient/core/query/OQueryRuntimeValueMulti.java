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
package com.orientechnologies.orient.core.query;

import com.orientechnologies.orient.core.sql.filter.OSQLFilterItemFieldMultiAbstract;

/**
 * Represent multiple values in query.
 * 
 * @author Luca Garulli
 * 
 */
public class OQueryRuntimeValueMulti {
	protected OSQLFilterItemFieldMultiAbstract	definition;
	public Object[]																	values;

	public OQueryRuntimeValueMulti(final OSQLFilterItemFieldMultiAbstract iDefinition, final Object[] iValues) {
		definition = iDefinition;
		values = iValues;
	}

	@Override
	public String toString() {
		if (values == null)
			return "";

		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		int i = 0;
		for (Object v : values) {
			if (i++ > 0)
				buffer.append(",");
			buffer.append(v);
		}
		buffer.append("]");
		return buffer.toString();
	}

	public OSQLFilterItemFieldMultiAbstract getDefinition() {
		return definition;
	}
}
