package org.eclipse.kapua.service.device.call.message.kura.lifecycle;

import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;

public class KuraBirthChannel extends KuraChannel
{
    protected static final String DESTINATION_CONTROL_PREFIX = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);

    public KuraBirthChannel()
    {
    }

    public KuraBirthChannel(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public KuraBirthChannel(String messageClassification, String scopeNamespace, String clientId)
    {
        this.messageClassification = messageClassification;
        this.scopeNamespace = scopeNamespace;
        this.clientId = clientId;
    }

}
