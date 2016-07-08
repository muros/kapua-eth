package org.org.eclipse.kapua.service.device.management.bundle.internal;

import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleListResult;
import org.eclipse.kapua.service.device.management.commons.call.AbstractDeviceApplicationCallResponseHandler;
import org.eclipse.kapua.service.device.management.commons.config.DeviceManagementConfig;
import org.eclipse.kapua.service.device.management.commons.config.DeviceManagementConfigKey;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponsePayload;

public class BundleManagementResponseHandlers
{
    public static class GET extends AbstractDeviceApplicationCallResponseHandler<DeviceBundleListResult>
    {
        @Override
        protected DeviceBundleListResult handleAcceptedRequest(KapuaResponseMessage responseMessage)
            throws DeviceManagementException
        {
            KapuaResponsePayload responsePayload = responseMessage.getResponsePayload();

            DeviceManagementConfig config = DeviceManagementConfig.getInstance();
            String charEncoding = config.getString(DeviceManagementConfigKey.CHAR_ENCODING);

            String body = null;
            try {
                body = new String(responsePayload.getResponseBody(), charEncoding);
            }
            catch (Exception e) {
                throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION,
                                                    e,
                                                    responsePayload.getResponseBody());

            }

            DeviceBundleListResult deviceBundleList = null;
            try {
                deviceBundleList = XmlUtil.unmarshal(body, DeviceBundleListResult.class);
            }
            catch (Exception e) {
                throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION,
                                                    e,
                                                    body);

            }

            return deviceBundleList;

        }
    }

    public static class PUT extends AbstractDeviceApplicationCallResponseHandler<Void>
    {

        @Override
        protected Void handleAcceptedRequest(KapuaResponseMessage responseMessage)
            throws DeviceManagementException
        {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
