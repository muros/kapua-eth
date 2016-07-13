package org.eclipse.kapua.service.device.call.message.kura;

import org.eclipse.kapua.service.device.call.message.KapuaAppDestination;

public abstract class AbstractKuraAppDestination extends AbstractKuraDestination implements KapuaAppDestination
{
    private String appId;

    public AbstractKuraAppDestination(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public AbstractKuraAppDestination(String controlDestinationPrefix, String scopeNamespace, String clientId)
    {
        super(controlDestinationPrefix, scopeNamespace, clientId);
    }

    @Override
    public String getAppId()
    {
        return appId;
    }

    @Override
    public void setAppId(String appId)
    {
        this.appId = appId;
    }

    @Override
    public String toClientDestination()
    {
        StringBuilder destinationSb = new StringBuilder(super.toClientDestination());

        destinationSb.append(destinationSeparator)
                     .append(getAppId());

        return destinationSb.toString();
    }

    @Override
    public void fromClientDestination(String destination)
    {
        super.fromClientDestination(destination);

        String[] destinationTokens = destination.split(destinationSeparator);

        if (destinationControlPrefix.equals(destinationTokens[0])) {
            if (destinationTokens.length > 3) {
                setAppId(destinationTokens[3]);
            }
        }
        // FIXME: what to do? A data message can be used by a device application?
        // If not, throw an exception?
    }
}
