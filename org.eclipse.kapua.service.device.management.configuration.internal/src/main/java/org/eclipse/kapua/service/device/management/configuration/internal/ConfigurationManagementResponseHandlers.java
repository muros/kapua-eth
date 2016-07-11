package org.eclipse.kapua.service.device.management.configuration.internal;

import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.service.device.management.commons.call.AbstractDeviceApplicationCallResponseHandler;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;

public class ConfigurationManagementResponseHandlers
{
    public static class GET extends AbstractDeviceApplicationCallResponseHandler<DeviceConfiguration>
    {
        @Override
        protected DeviceConfiguration handleAcceptedRequest(KapuaResponseMessage responseMessage)
            throws DeviceManagementException
        {
            KapuaResponsePayload responsePayload = responseMessage.getResponsePayload();

            DeviceManagementSetting config = DeviceManagementSetting.getInstance();
            String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

            String body = null;
            try {
                body = new String(responsePayload.getResponseBody(), charEncoding);
            }
            catch (Exception e) {
                throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION,
                                                    e,
                                                    responsePayload.getResponseBody());

            }

            DeviceConfiguration deviceConfiguration = null;
            try {
                deviceConfiguration = XmlUtil.unmarshal(body, DeviceConfiguration.class);
            }
            catch (Exception e) {
                throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION,
                                                    e,
                                                    body);

            }

            return deviceConfiguration;
        }
    }

    public static class PUT extends AbstractDeviceApplicationCallResponseHandler<Void>
    {

        @Override
        protected Void handleAcceptedRequest(KapuaResponseMessage responseMessage)
            throws DeviceManagementException
        {
            // TODO: Create event!
            return null;
        }

    }
}
