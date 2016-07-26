package org.org.eclipse.kapua.service.device.call.test;

import java.util.Date;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.device.call.DeviceCall;
import org.eclipse.kapua.service.device.call.DeviceCallFactory;
import org.eclipse.kapua.service.device.call.kura.CommandMetrics;
import org.eclipse.kapua.service.device.call.kura.KuraMethod;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.junit.Assert;
import org.junit.Test;

public class KuraCallTest extends Assert
{
    @SuppressWarnings("unchecked")
    @Test
    public void testKuraCall()
        throws Exception
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceCallFactory deviceCallFactory = locator.getFactory(DeviceCallFactory.class);
        DeviceCall deviceCall = deviceCallFactory.newDeviceCall();

        KuraRequestChannel requestChannel = new KuraRequestChannel("$EDC", "edcguest", "asd");
        requestChannel.setAppId("CMD-V1");
        requestChannel.setMethod(KuraMethod.EXEC);
        requestChannel.setResources(new String[] { "command" });

        KuraRequestPayload requestPayload = new KuraRequestPayload();
        requestPayload.metrics().put(CommandMetrics.APP_METRIC_CMD.getValue(), "ifconfig");

        KuraRequestMessage requestMessage = new KuraRequestMessage(requestChannel, new Date(), requestPayload);

        deviceCall.execute(requestMessage, 3000000L);
    }
}
