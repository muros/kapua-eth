package org.eclipse.kapua.service.device.call.message.kura;

import org.eclipse.kapua.message.KapuaDestination;
import org.eclipse.kapua.message.internal.AbstractKapuaDestination;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;

public abstract class AbstractKuraDestination extends AbstractKapuaDestination implements KapuaDestination
{
    protected final static String destinationSeparator     = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_SEPARATOR);
    protected final static String destinationControlPrefix = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_CONTROL_PREFIX);

    public AbstractKuraDestination(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public AbstractKuraDestination(String controlDestinationPrefix, String scopeNamespace, String clientId)
    {
        super(controlDestinationPrefix, scopeNamespace, clientId);
    }

    @Override
    public String toClientDestination()
    {
        StringBuilder destinationSb = new StringBuilder();

        if (getControlDestinationPrefix() != null) {
            destinationSb.append(getControlDestinationPrefix())
                         .append(destinationSeparator);
        }

        destinationSb.append(getScopeNamespace())
                     .append(destinationSeparator)
                     .append(getClientId());

        return destinationSb.toString();
    }

    @Override
    public void fromClientDestination(String destination)
    {
        String[] destinationTokens = destination.split(destinationSeparator);

        if (destinationControlPrefix.equals(destinationTokens[0])) {

            if (destinationTokens.length > 2) {
                setControlDestinationPrefix(destinationTokens[0]);
                setScopeNamespace(destinationTokens[1]);
                setClientId(destinationTokens[2]);
            }
        }
        else {
            if (destinationTokens.length > 1) {
                setScopeNamespace(destinationTokens[0]);
                setClientId(destinationTokens[1]);
            }
        }
    }
}
