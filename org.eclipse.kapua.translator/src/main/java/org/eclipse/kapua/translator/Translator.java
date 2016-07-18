package org.eclipse.kapua.translator;

import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.message.Channel;
import org.eclipse.kapua.message.Message;
import org.eclipse.kapua.message.Payload;

public interface Translator<FROM_C extends Channel, TO_C extends Channel, FROM_P extends Payload, TO_P extends Payload, FROM_M extends Message, TO_M extends Message>
{
    @SuppressWarnings("rawtypes")
    static ServiceLoader<Translator> translators = ServiceLoader.load(Translator.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <FROM_C extends Channel, TO_C extends Channel, FROM_P extends Payload, TO_P extends Payload, FROM_M extends Message, TO_M extends Message, T extends Translator<FROM_C, TO_C, FROM_P, TO_P, FROM_M, TO_M>> T getTranslatorFor(Class<FROM_M> fromMessageClass,
                                                                                                                                                                                                                                                Class<TO_M> toMessageClass)
        throws KapuaException
    {

        T translator = null;

        for (Translator t : translators) {
            if (t.getClassFrom().equals(fromMessageClass) &&
                t.getClassTo().equals(toMessageClass)) {
                translator = (T) t;
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

    public TO_C translate(FROM_C channel)
        throws KapuaException;

    public TO_P translate(FROM_P payload)
        throws KapuaException;

    public TO_M translate(FROM_M message)
        throws KapuaException;

    public Class<?> getClassFrom();

    public Class<?> getClassTo();
}
