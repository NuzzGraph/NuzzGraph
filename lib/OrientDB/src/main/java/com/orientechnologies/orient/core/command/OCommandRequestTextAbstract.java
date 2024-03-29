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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.orientechnologies.common.collection.OCompositeKey;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.OMemoryStream;
import com.orientechnologies.orient.core.serialization.OSerializableStream;
import com.orientechnologies.orient.core.serialization.serializer.OStringSerializerHelper;
import com.orientechnologies.orient.core.serialization.serializer.record.string.ORecordSerializerStringAbstract;
import com.orientechnologies.orient.core.serialization.serializer.stream.OCompositeKeySerializer;

/**
 * Text based Command Request abstract class.
 * 
 * @author Luca Garulli
 * 
 */
@SuppressWarnings("serial")
public abstract class OCommandRequestTextAbstract extends OCommandRequestAbstract implements OCommandRequestText {
	protected String	text;

	protected OCommandRequestTextAbstract() {
	}

	protected OCommandRequestTextAbstract(final String iText) {
		if (iText == null)
			throw new IllegalArgumentException("Text cannot be null");

		text = iText.trim();
	}

	/**
	 * Delegates the execution to the configured command executor.
	 */
	@SuppressWarnings("unchecked")
	public <RET> RET execute(final Object... iArgs) {
		setParameters(iArgs);
		return (RET) ODatabaseRecordThreadLocal.INSTANCE.get().getStorage().command(this);
	}

	public String getText() {
		return text;
	}

	public OCommandRequestText setText(final String iText) {
		this.text = iText;
		return this;
	}

	public OSerializableStream fromStream(byte[] iStream) throws OSerializationException {
		final OMemoryStream buffer = new OMemoryStream(iStream);
		try {
			text = buffer.getAsString();

			parameters = null;

			final boolean simpleParams = buffer.getAsBoolean();
			if (simpleParams) {
				final byte[] paramBuffer = buffer.getAsByteArray();
				final ODocument param = new ODocument();
				param.fromStream(paramBuffer);

				Map<String, Object> params = param.field("params");

				parameters = new HashMap<Object, Object>();
				for (Entry<String, Object> p : params.entrySet()) {
					final Object value;
					if (p.getValue() instanceof String)
						value = ORecordSerializerStringAbstract.getTypeValue((String) p.getValue());
					else
						value = p.getValue();

					if (Character.isDigit(p.getKey().charAt(0)))
						parameters.put(Integer.parseInt(p.getKey()), value);
					else
						parameters.put(p.getKey(), value);
				}
			}

			final boolean compositeKeyParamsPresent = buffer.getAsBoolean();
			if (compositeKeyParamsPresent) {
				final byte[] paramBuffer = buffer.getAsByteArray();
				final ODocument param = new ODocument();
				param.fromStream(paramBuffer);

				final Map<String, Object> compositeKeyParams = param.field("compositeKeyParams");

				if (parameters == null)
					parameters = new HashMap<Object, Object>();

				for (final Entry<String, Object> p : compositeKeyParams.entrySet()) {
					final Object value = OCompositeKeySerializer.INSTANCE.fromStream(OStringSerializerHelper.getBinaryContent(p.getValue()));

					if (Character.isDigit(p.getKey().charAt(0)))
						parameters.put(Integer.parseInt(p.getKey()), value);
					else
						parameters.put(p.getKey(), value);
				}
			}
		} catch (IOException e) {
			throw new OSerializationException("Error while unmarshalling OCommandRequestTextAbstract impl", e);
		}
		return this;
	}

	public byte[] toStream() throws OSerializationException {
		final OMemoryStream buffer = new OMemoryStream();
		try {
			buffer.set(text);

			if (parameters == null || parameters.size() == 0) {
				// simple params are absent
				buffer.set(false);
				// composite keys are absent
				buffer.set(false);
			} else {
				final Map<Object, Object> params = new HashMap<Object, Object>();
				final Map<Object, byte[]> compositeKeyParams = new HashMap<Object, byte[]>();

				for (final Entry<Object, Object> paramEntry : parameters.entrySet())
					if (paramEntry.getValue() instanceof OCompositeKey)
						compositeKeyParams.put(paramEntry.getKey(), OCompositeKeySerializer.INSTANCE.toStream(paramEntry.getValue()));
					else if(paramEntry.getValue() instanceof String) {
            final StringBuilder builder = new StringBuilder(  );
            ORecordSerializerStringAbstract.simpleValueToStream( builder, OType.STRING, paramEntry.getValue() );
            params.put(paramEntry.getKey(), builder.toString());
          }
          else
            params.put(paramEntry.getKey(), paramEntry.getValue());

				buffer.set(!params.isEmpty());
				if (!params.isEmpty()) {
					final ODocument param = new ODocument();
					param.field("params", params);
					buffer.set(param.toStream());
				}

				buffer.set(!compositeKeyParams.isEmpty());
				if (!compositeKeyParams.isEmpty()) {
					final ODocument compositeKey = new ODocument();
					compositeKey.field("compositeKeyParams", compositeKeyParams);
					buffer.set(compositeKey.toStream());
				}
			}

		} catch (IOException e) {
			throw new OSerializationException("Error while marshalling OCommandRequestTextAbstract impl", e);
		}

		return buffer.toByteArray();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [text=" + text + "]";
	}
}
