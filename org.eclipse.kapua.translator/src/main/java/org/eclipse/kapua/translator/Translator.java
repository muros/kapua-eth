/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.translator;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public abstract class Translator<FROM_M extends Message, TO_M extends Message>
{
	
	public static final Logger logger = LoggerFactory.getLogger(Translator.class);
	
    static ServiceLoader<?> translators = ServiceLoader.load(Translator.class);

    @SuppressWarnings("unchecked")
    public static synchronized <FROM_M extends Message, TO_M extends Message, T extends Translator<FROM_M, TO_M>> T getTranslatorFor(Class<FROM_M> fromMessageClass,
                                                                                                                        Class<TO_M> toMessageClass)
        throws KapuaException
    {
        T translator = null;

        Iterator<T> translatorIterator = (Iterator<T>) translators.iterator();
        while (translatorIterator.hasNext()) {
            T t = translatorIterator.next();

            if ((fromMessageClass.isAssignableFrom(t.getClassFrom())) &&
                toMessageClass.isAssignableFrom(t.getClassTo())) {
                translator = t;
                break;
            }
        }

        if (translator == null) {
        	logger.error("Cannot find translator from - to: {} - {}", new Object[]{fromMessageClass.getName(), toMessageClass.getName()});
            throw new KapuaRuntimeException(KapuaRuntimeErrorCodes.TRANSLATOR_NOT_FOUND,
                                            null,
                                            new Object[] {
                                                           translators,
                                                           fromMessageClass.getName(),
                                                           toMessageClass.getName(),
                                            });
        }

        return translator;
    }

    public abstract TO_M translate(FROM_M message)
        throws KapuaException;

    public abstract Class<FROM_M> getClassFrom();

    public abstract Class<TO_M> getClassTo();
}
