package org.eclipse.kapua.service.device.call.message.kura;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.kapua.service.device.call.message.DeviceChannel;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;

public class KuraChannel implements DeviceChannel
{
    protected final static String destinationControlPrefix = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_CONTROL_PREFIX);

    protected String              messageClassification;
    protected String              scopeNamespace;
    protected String              clientId;

    public List<String>           semanticChannel          = new ArrayList<>();

    public KuraChannel()
    {
    }

    public KuraChannel(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public KuraChannel(String messageClassification, String scopeNamespace, String clientId)
    {
        this.messageClassification = messageClassification;
        this.scopeNamespace = scopeNamespace;
        this.clientId = clientId;
    }

    public String getMessageClassification()
    {
        return messageClassification;
    }

    public void setMessageClassification(String messageClassification)
    {
        this.messageClassification = messageClassification;
    }

    public String getScope()
    {
        return scopeNamespace;
    }

    public void setScope(String scope)
    {
        this.scopeNamespace = scope;
    }

    public String getClientId()
    {
        return clientId;
    }

    @Override
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public List<String> getSemanticChannel()
    {
        return semanticChannel;
    }

}
