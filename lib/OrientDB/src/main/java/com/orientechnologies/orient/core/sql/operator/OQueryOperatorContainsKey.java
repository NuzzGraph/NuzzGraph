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
package com.orientechnologies.orient.core.sql.operator;

import java.util.Map;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.sql.filter.OSQLFilterCondition;

/**
 * CONTAINS KEY operator.
 * 
 * @author Luca Garulli
 * 
 */
public class OQueryOperatorContainsKey extends OQueryOperatorEqualityNotNulls {

	public OQueryOperatorContainsKey() {
		super("CONTAINSKEY", 5, false);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean evaluateExpression(final OIdentifiable iRecord, final OSQLFilterCondition iCondition, final Object iLeft,
			final Object iRight, OCommandContext iContext) {

		if (iLeft instanceof Map<?, ?>) {

			final Map<String, ?> map = (Map<String, ?>) iLeft;
			return map.containsKey(iRight);
		} else if (iRight instanceof Map<?, ?>) {

			final Map<String, ?> map = (Map<String, ?>) iRight;
			return map.containsKey(iLeft);
		}
		return false;
	}

	@Override
	public OIndexReuseType getIndexReuseType(final Object iLeft, final Object iRight) {
    return OIndexReuseType.INDEX_METHOD;
	}


  @Override
  public ORID getBeginRidRange(Object iLeft, Object iRight) {
    return null;
  }

  @Override
  public ORID getEndRidRange(Object iLeft, Object iRight) {
    return null;
  }
}
