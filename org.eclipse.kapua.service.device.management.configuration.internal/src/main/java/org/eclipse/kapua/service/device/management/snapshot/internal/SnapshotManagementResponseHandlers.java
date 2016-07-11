package org.eclipse.kapua.service.device.management.snapshot.internal;

import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.service.device.management.commons.call.AbstractDeviceApplicationCallResponseHandler;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.commons.message.KapuaResponsePayload;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotListResult;

public class SnapshotManagementResponseHandlers
{

    public static class GET extends AbstractDeviceApplicationCallResponseHandler<DeviceSnapshotListResult>
    {

        @Override
        protected DeviceSnapshotListResult handleAcceptedRequest(KapuaResponseMessage responseMessage)
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

            DeviceSnapshotListResult deviceSnapshots = null;
            try {
                deviceSnapshots = XmlUtil.unmarshal(body, DeviceSnapshotListResult.class);
            }
            catch (Exception e) {
                throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION,
                                                    e,
                                                    body);

            }

            return deviceSnapshots;
        }

    }

    public static class EXEC extends AbstractDeviceApplicationCallResponseHandler<Void>
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
