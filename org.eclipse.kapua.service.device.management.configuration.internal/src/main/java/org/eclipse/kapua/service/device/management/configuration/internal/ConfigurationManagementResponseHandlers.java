package org.eclipse.kapua.service.device.management.configuration.internal;

import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponsePayload;
import org.eclipse.kapua.service.device.management.commons.call.AbstractDeviceApplicationCallResponseHandler;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;

public class ConfigurationManagementResponseHandlers
{
    public static class GET extends AbstractDeviceApplicationCallResponseHandler<DeviceConfiguration>
    {
        @SuppressWarnings("rawtypes")
        @Override
        protected DeviceConfiguration handleAcceptedRequest(DeviceResponseMessage responseMessage)
            throws DeviceManagementException
        {
            DeviceResponsePayload responsePayload = (DeviceResponsePayload) responseMessage.getPayload();

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

        @SuppressWarnings("rawtypes")
        @Override
        protected Void handleAcceptedRequest(DeviceResponseMessage responseMessage)
            throws DeviceManagementException
        {
            // TODO: Create device event!
            return null;
        }

    }
}
