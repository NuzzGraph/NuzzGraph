/*
 *
 * Copyright 2012 Luca Molino (molino.luca--AT--gmail.com)
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
package com.orientechnologies.orient.core.fetch.object;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OFetchException;
import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.fetch.OFetchContext;
import com.orientechnologies.orient.core.fetch.OFetchListener;
import com.orientechnologies.orient.core.record.ORecordSchemaAware;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializerHelper;

/**
 * @author luca.molino
 * 
 */
public class OObjectFetchListener implements OFetchListener {

	public void processStandardField(ORecordSchemaAware<?> iRecord, Object iFieldValue, String iFieldName, OFetchContext iContext)
			throws OFetchException {
	}

	public void processStandardCollectionValue(Object iFieldValue, OFetchContext iContext) throws OFetchException {
	}

	public void parseLinked(final ORecordSchemaAware<?> iRootRecord, final OIdentifiable iLinked, final Object iUserObject,
			final String iFieldName, final OFetchContext iContext) throws OFetchException {
		final Class<?> type = OObjectSerializerHelper.getFieldType(iUserObject, iFieldName);
		if (type == null || Set.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type)
				|| Map.class.isAssignableFrom(type) || type.isArray()) {
			return;
		} else if (iLinked instanceof ORecordSchemaAware
				&& !(((OObjectFetchContext) iContext).getObj2RecHandler().existsUserObjectByRID(iLinked.getIdentity()))) {
			fetchLinked(iRootRecord, iUserObject, iFieldName, (ORecordSchemaAware<?>) iLinked, iContext);
		}
	}

	public Object fetchLinkedMapEntry(final ORecordSchemaAware<?> iRoot, final Object iUserObject, final String iFieldName,
			String iKey, final ORecordSchemaAware<?> iLinked, final OFetchContext iContext) throws OFetchException {
		Object value = null;
		final Class<?> type = OObjectSerializerHelper.getFieldType((ODocument) iLinked,
				((OObjectFetchContext) iContext).getEntityManager());
		final Class<?> fieldClass = ((OObjectFetchContext) iContext).getEntityManager().getEntityClass(type.getSimpleName());
		if (fieldClass != null) {
			// RECOGNIZED TYPE
			value = ((OObjectFetchContext) iContext).getObj2RecHandler().getUserObjectByRecord((ODocument) iLinked,
					((OObjectFetchContext) iContext).getFetchPlan());
		}
		return value;
	}

	public Object fetchLinkedCollectionValue(final ORecordSchemaAware<?> iRoot, final Object iUserObject, final String iFieldName,
			final ORecordSchemaAware<?> iLinked, final OFetchContext iContext) throws OFetchException {
		Object value = null;
		final Class<?> fieldClass = OObjectSerializerHelper.getFieldType((ODocument) iLinked,
				((OObjectFetchContext) iContext).getEntityManager());

		if (fieldClass != null) {
			// RECOGNIZED TYPE
			value = ((OObjectFetchContext) iContext).getObj2RecHandler().getUserObjectByRecord((ODocument) iLinked,
					((OObjectFetchContext) iContext).getFetchPlan());
		}

		return value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object fetchLinked(ORecordSchemaAware<?> iRoot, Object iUserObject, String iFieldName, ORecordSchemaAware<?> iLinked,
			OFetchContext iContext) throws OFetchException {
		if (iUserObject == null)
			return null;
		final Class<?> type;
		if (iLinked != null && iLinked instanceof ODocument)
			// GET TYPE BY DOCUMENT'S CLASS. THIS WORKS VERY WELL FOR SUB-TYPES
			type = OObjectSerializerHelper.getFieldType((ODocument) iLinked, ((OObjectFetchContext) iContext).getEntityManager());
		else
			// DETERMINE TYPE BY REFLECTION
			type = OObjectSerializerHelper.getFieldType(iUserObject, iFieldName);

		if (type == null)
			throw new OSerializationException(
					"Linked type of field '"
							+ iRoot.getClassName()
							+ "."
							+ iFieldName
							+ "' is unknown. Probably needs to be registered with <db>.getEntityManager().registerEntityClasses(<package>) or <db>.getEntityManager().registerEntityClass(<class>) or the package cannot be loaded correctly due to a classpath problem. In this case register the single classes one by one.");

		Object fieldValue = null;
		Class<?> fieldClass;
		if (type.isEnum()) {

			String enumName = ((ODocument) iLinked).field(iFieldName);
			Class<Enum> enumClass = (Class<Enum>) type;
			fieldValue = Enum.valueOf(enumClass, enumName);

		} else {

			fieldClass = ((OObjectFetchContext) iContext).getEntityManager().getEntityClass(type.getSimpleName());
			if (fieldClass != null) {
				// RECOGNIZED TYPE
				fieldValue = ((OObjectFetchContext) iContext).getObj2RecHandler().getUserObjectByRecord((ODocument) iLinked,
						((OObjectFetchContext) iContext).getFetchPlan());
			}
		}

		OObjectSerializerHelper.setFieldValue(iUserObject, iFieldName,
				OObjectSerializerHelper.unserializeFieldValue(iUserObject, iFieldName, fieldValue));

		return fieldValue;
	}
}
