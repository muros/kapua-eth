package org.eclipse.kapua.service.device.call.message.kura;

import java.util.Arrays;

import org.eclipse.kapua.service.device.call.message.KapuaRequestDestination;

public class KuraRequestDestination extends AbstractKuraAppDestination implements KapuaRequestDestination
{
    private String   method;
    private String[] resources;

    public KuraRequestDestination(String scopeNamespace, String clientId)
    {
        this(null, scopeNamespace, clientId);
    }

    public KuraRequestDestination(String controlDestinationPrefix, String scopeNamespace, String clientId)
    {
        super(controlDestinationPrefix, scopeNamespace, clientId);
    }

    @Override
    public String toClientDestination()
    {
        StringBuilder destinationSb = new StringBuilder(super.toClientDestination());

        destinationSb.append(destinationSeparator)
                     .append(getMethod());

        if (resources != null) {
            for (String r : resources) {
                destinationSb.append(destinationSeparator)
                             .append(r);
            }
        }

        return destinationSb.toString();
    }

    @Override
    public void fromClientDestination(String destination)
    {
        super.fromClientDestination(destination);

        String[] destinationTokens = destination.split(destinationSeparator);

        if (destinationControlPrefix.equals(destinationTokens[0])) {

            if (destinationTokens.length > 4) {
                setAppId(destinationTokens[4]);

                if (destinationTokens.length > 5) {
                    setResources(Arrays.copyOfRange(destinationTokens, 5, destinationTokens.length));
                }
            }

        }
    }

    @Override
    public String getMethod()
    {
        return method;
    }

    @Override
    public void setMethod(String method)
    {
        this.method = method;
    }

    @Override
    public String[] getResources()
    {
        return resources;
    }

    @Override
    public void setResources(String[] resources)
    {
        this.resources = resources;
    }
}
