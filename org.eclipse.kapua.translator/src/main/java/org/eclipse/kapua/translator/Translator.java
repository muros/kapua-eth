package org.eclipse.kapua.translator;

import java.util.ServiceLoader;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.KapuaRuntimeErrorCodes;
import org.eclipse.kapua.KapuaRuntimeException;
import org.eclipse.kapua.message.Message;

@SuppressWarnings("rawtypes")
public interface Translator<FROM_M extends Message, TO_M extends Message>
{
    static ServiceLoader<Translator> translators = ServiceLoader.load(Translator.class);

    @SuppressWarnings("unchecked")
    public static <FROM_M extends Message, TO_M extends Message, T extends Translator<FROM_M, TO_M>> T getTranslatorFor(Class<FROM_M> fromMessageClass,
                                                                                                                        Class<TO_M> toMessageClass)
        throws KapuaException
    {

        T translator = null;

        for (Translator<FROM_M, TO_M> t : translators) {
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

    public TO_M translate(FROM_M message)
        throws KapuaException;

    public Class<FROM_M> getClassFrom();

    public Class<TO_M> getClassTo();
}
