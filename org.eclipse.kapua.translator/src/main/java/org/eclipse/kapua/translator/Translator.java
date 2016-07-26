package org.eclipse.kapua.translator;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.message.Message;

@SuppressWarnings("rawtypes")
public interface Translator<FROM_M extends Message, TO_M extends Message>
{
    static ServiceLoader<?> translators = ServiceLoader.load(Translator.class);

    @SuppressWarnings("unchecked")
    public static <FROM_M extends Message, TO_M extends Message, T extends Translator<FROM_M, TO_M>> T getTranslatorFor(Class<FROM_M> fromMessageClass,
                                                                                                                        Class<TO_M> toMessageClass)
        throws KapuaException
    {

        T translator = null;

        Iterator<T> translatorIterator = (Iterator<T>) translators.iterator();
        while (translatorIterator.hasNext()) {
            translator = translatorIterator.next();
            if (translator.getClassFrom().equals(fromMessageClass) &&
                translator.getClassTo().equals(toMessageClass)) {
                break;
            }
        }

        if (translator == null) {
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

    public TO_M translate(FROM_M message)
        throws KapuaException;

    public Class<FROM_M> getClassFrom();

    public Class<TO_M> getClassTo();
}
