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
package com.orientechnologies.orient.core.sql.functions.coll;

import java.util.Collection;
import java.util.HashSet;

import com.orientechnologies.orient.core.command.OCommandExecutor;
import com.orientechnologies.orient.core.db.record.OIdentifiable;

/**
 * This operator can work as aggregate or inline. If only one argument is passed than aggregates, otherwise executes, and returns,
 * the INTERSECTION of the collections received as parameters.
 * 
 * @author Luca Garulli (l.garulli--at--orientechnologies.com)
 * 
 */
public class OSQLFunctionIntersect extends OSQLFunctionCollAbstract {
	public static final String	NAME	= "intersect";

	public OSQLFunctionIntersect() {
		super(NAME, 1, 1);
	}

	public Object execute(final OIdentifiable iCurrentRecord, final Object[] iParameters, OCommandExecutor iRequester) {
		Object value = iParameters[0];

		if (value == null || !(value instanceof Collection<?>))
			return null;

		final Collection<?> coll = (Collection<?>) value;

		if (iParameters.length == 1) {
			// AGGREGATION MODE (STATEFULL)
			if (context == null) {
				// ADD ALL THE ITEMS OF THE FIRST COLLECTION
				context = new HashSet<Object>(coll);
			} else {
				// INTERSECT IT AGAINST THE CURRENT COLLECTION
				context.retainAll(coll);
			}
			return null;
		} else {
			// IN-LINE MODE (STATELESS)
			final HashSet<Object> result = new HashSet<Object>(coll);

			for (int i = 1; i < iParameters.length; ++i) {
				value = iParameters[i];
				result.retainAll((Collection<?>) value);
			}

			return result;
		}
	}

	public String getSyntax() {
		return "Syntax error: intersect(<field>*)";
	}
}
