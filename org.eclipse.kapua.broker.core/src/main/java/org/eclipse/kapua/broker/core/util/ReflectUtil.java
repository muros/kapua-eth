package org.eclipse.kapua.broker.core.util;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;

public class ReflectUtil {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> create(final Class<T> superType, final String className) throws KapuaException {
		try {
			return (Class<T>) Class.forName(className);
		}
		catch (Throwable t) {
			throw new KapuaException(KapuaErrorCodes.INTERNAL_ERROR, t, (Object[])null);
		}
	}

}