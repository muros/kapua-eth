package org.eclipse.kapua.service.device.management.commons.call;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.service.device.call.DeviceCall;
import org.eclipse.kapua.service.device.call.DeviceCallFactory;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.request.KapuaRequestChannel;
import org.eclipse.kapua.service.device.management.request.KapuaRequestMessage;
import org.eclipse.kapua.service.device.management.response.KapuaResponseMessage;
import org.eclipse.kapua.translator.Translator;

@SuppressWarnings("rawtypes")
public class DeviceCallExecutor<C extends KapuaRequestChannel, P extends KapuaPayload, RQ extends KapuaRequestMessage<C, P>, RS extends KapuaResponseMessage>
{
    private RQ   requestMessage;
    private Long timeout;

    public DeviceCallExecutor(RQ requestMessage)
    {
        this(requestMessage, null);
    }

    public DeviceCallExecutor(RQ requestMessage, Long timeout)
    {
        this.requestMessage = requestMessage;
        this.timeout = timeout;
    }

    @SuppressWarnings({ "unchecked" })
    public RS send()
        throws KapuaException
    {
        //
        // Get the correct device call
        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceCallFactory kapuaDeviceCallFactory = locator.getFactory(DeviceCallFactory.class/* , device.getType() */);

        DeviceCall<DeviceRequestMessage, DeviceResponseMessage> deviceCall = kapuaDeviceCallFactory.newDeviceCall();
        Translator tKapuaToClient = Translator.getTranslatorFor(KapuaRequestMessage.class,
                                                                deviceCall.getBaseMessageClass());

        DeviceResponseMessage responseMessage = null;
        timeout = timeout == null ? DeviceManagementSetting.getInstance().getLong(DeviceManagementSettingKey.REQUEST_TIMEOUT) : timeout;

        DeviceRequestMessage deviceRequestMessage = (DeviceRequestMessage) tKapuaToClient.translate(requestMessage);
        switch (requestMessage.getSemanticChannel().getMethod()) {
            case "create":
            {
                responseMessage = deviceCall.create(deviceRequestMessage, timeout);
            }
                break;
            case "read":
            {
                responseMessage = deviceCall.read(deviceRequestMessage, timeout);
            }
                break;
            case "discover":
            {
                responseMessage = deviceCall.options(deviceRequestMessage, timeout);
            }
                break;
            case "delete":
            {
                responseMessage = deviceCall.delete(deviceRequestMessage, timeout);
            }
                break;
            case "execute":
            {
                responseMessage = deviceCall.execute(deviceRequestMessage, timeout);
            }
                break;
            case "write":
            {
                responseMessage = deviceCall.write(deviceRequestMessage, timeout);
            }
                break;
        }

        Translator tClientToKapua = Translator.getTranslatorFor(deviceCall.getBaseMessageClass(),
                                                                KapuaResponseMessage.class);

        return (RS) tClientToKapua.translate(responseMessage);
    }
}
