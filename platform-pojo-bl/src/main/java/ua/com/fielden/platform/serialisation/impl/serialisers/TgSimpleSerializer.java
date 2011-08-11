package ua.com.fielden.platform.serialisation.impl.serialisers;

import java.nio.ByteBuffer;

import ua.com.fielden.platform.serialisation.impl.TgKryo;

import com.esotericsoftware.kryo.serialize.SimpleSerializer;

/**
 * A base class for all custom serialisers.
 * 
 * @author TG Team
 * 
 * @param <T>
 */
public abstract class TgSimpleSerializer<T> extends SimpleSerializer<T> {
    private static final byte NULL = 12;
    private static final byte NOT_NULL = 13;

    protected final TgKryo kryo;

    protected TgSimpleSerializer(final TgKryo kryo) {
	this.kryo = kryo;
    }

    protected void writeValue(final ByteBuffer buffer, final Object value) { // , Class<?> valueType
	if (value == null) {
	    buffer.put(NULL);
	} else {
	    buffer.put(NOT_NULL);
	    kryo.writeObject(buffer, value);
	}
    }

    protected <E> E readValue(final ByteBuffer buffer, final Class<E> valueType) {
	final byte attr = buffer.get();
	if (attr == NULL) {
	    return null;
	} else {
	    return kryo.readObject(buffer, valueType);
	}
    }

    protected void writeBoolean(final ByteBuffer buffer, final Boolean value) {
	buffer.put(value ? (byte) 1 : 0);
    }

    protected boolean readBoolean(final ByteBuffer buffer) {
	return buffer.get() == 1;
    }

}
