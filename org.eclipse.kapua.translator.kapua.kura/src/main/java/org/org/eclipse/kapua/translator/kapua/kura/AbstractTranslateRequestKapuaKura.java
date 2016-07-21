package org.org.eclipse.kapua.translator.kapua.kura;

import java.util.Map;

import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraPosition;
import org.eclipse.kapua.service.device.management.request.KapuaRequestChannel;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;

@SuppressWarnings("rawtypes")
public abstract class AbstractTranslateRequestKapuaKura<KPRC extends KapuaRequestChannel, KRRC extends KuraRequestChannel, KPRP extends KapuaPayload, KRRP extends KuraRequestPayload, KPRM extends KapuaRequestMessage, KRRM extends KuraRequestMessage>
{
    Map<String, String> metricsDictionary;
    Map<String, String> methodDictionaty;
    /**
     * create = POST
     * execute = EXEC
     * 
     */
    Map<String, String> applicationDictionaty;

    /**
     * COMMAND = CMQ-V1
     * CONFIGURATION =
     */

    protected KRRM translate(KPRM requestMessage)
    {

    }

    protected KRRC translate(KPRC requestChannel)
    {

    }

    protected KRRP translate(Map<String, String> metricsDictionary, byte[] body, KuraPosition position)
    {
        KRRP k = (KRRP) new KuraRequestPayload();

        k.metrics().putAll(metrics);
        k.setBody(body);
        k.setPosition(position);
        k.set
    }

}
