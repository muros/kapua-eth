package org.eclipse.kapua.service.device.call.message.kura;

import java.util.List;

import org.eclipse.kapua.service.device.call.message.data.DeviceDataChannel;

public class KuraDataChannel extends KuraChannel implements DeviceDataChannel
{

    private List<String> semanticChannelParts;

    @Override
    public List<String> getSemanticChannelParts()
    {
        return semanticChannelParts;
    }

    public void setSemanticChannelParts(List<String> semanticChannelParts)
    {
        this.semanticChannelParts = semanticChannelParts;
    }
}
