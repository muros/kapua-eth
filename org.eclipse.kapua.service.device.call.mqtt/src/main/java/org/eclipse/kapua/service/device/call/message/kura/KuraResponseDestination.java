package org.eclipse.kapua.service.device.call.message.kura;

import org.eclipse.kapua.service.device.call.message.KapuaResponseDestination;

public class KuraResponseDestination extends AbstractKuraAppDestination implements KapuaResponseDestination
{
    private String requestId;

    public KuraResponseDestination(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public KuraResponseDestination(String controlDestinationPrefix, String scopeNamespace, String clientId)
    {
        super(controlDestinationPrefix, scopeNamespace, clientId);
    }

    @Override
    public String toClientDestination()
    {
        StringBuilder destinationSb = new StringBuilder(super.toClientDestination());

        destinationSb.append(destinationSeparator)
                     .append(getRequestId());

        return destinationSb.toString();
    }

    @Override
    public void fromClientDestination(String destination)
    {
        super.fromClientDestination(destination);

        String[] destinationTokens = destination.split(destinationSeparator);

        if (destinationControlPrefix.equals(destinationTokens[0])) {

            if (destinationTokens.length > 4) {
                setRequestId(destinationTokens[4]);
            }
        }

    }

    @Override
    public String getRequestId()
    {
        return requestId;
    }

    @Override
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

}
