package org.eclipse.kapua.translator.kura.kapua;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaChannel;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.internal.KapuaChannelImpl;
import org.eclipse.kapua.message.internal.KapuaMessageImpl;
import org.eclipse.kapua.message.internal.KapuaPayloadImpl;
import org.eclipse.kapua.message.internal.KapuaPositionImpl;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.DevicePosition;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;

@SuppressWarnings("rawtypes")
public class TranslatorDataKuraKapua implements Translator<KuraMessage, KapuaMessage>
{

    @SuppressWarnings("unchecked")
    @Override
    public KapuaMessage translate(KuraMessage kuraMessage)
        throws KapuaException
    {
        //
        // Kapua channel
        KapuaChannel kapuaChannel = translate(kuraMessage.getChannel());

        //
        // Kapua payload
        KapuaPayload kapuaPayload = translate(kuraMessage.getPayload());

        //
        // Kapua message
        KapuaMessage kapuaMessage = new KapuaMessageImpl(kapuaChannel,
                                                         kapuaPayload);
        kapuaMessage.setCapturedOn(kuraMessage.getPayload().getTimestamp());
        kapuaMessage.setSentOn(kuraMessage.getPayload().getTimestamp());
        kapuaMessage.setReceivedOn(kuraMessage.getTimestamp());
        kapuaMessage.setPosition(translate(kuraMessage.getPayload().getPosition()));

        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);

        Account account = accountService.findByName(kuraMessage.getChannel().getScope());
        kapuaMessage.setScopeId(account.getId());

        Device device = deviceRegistryService.findByClientId(account.getId(), kuraMessage.getChannel().getClientId());
        kapuaMessage.setDeviceId(device.getId());

        //
        // Return Kapua Message
        return kapuaMessage;
    }

    public KapuaChannel translate(KuraChannel kuraChannel)
        throws KapuaException
    {
        KapuaChannel kapuaChannel = new KapuaChannelImpl();
        kapuaChannel.setSemanticParts(kuraChannel.getSemanticChannelParts());

        //
        // Return Kapua Channel
        return kapuaChannel;
    }

    public KapuaPayload translate(KuraPayload kuraPayload)
        throws KapuaException
    {
        KapuaPayload kapuaPayload = new KapuaPayloadImpl();
        kapuaPayload.setBody(kuraPayload.getBody());
        kapuaPayload.setProperties(kuraPayload.getMetrics());

        //
        // Return Kapua payload
        return kapuaPayload;
    }

    private KapuaPosition translate(DevicePosition kuraPosition)
    {
        KapuaPosition kapuaPosition = new KapuaPositionImpl();

        kapuaPosition.setAltitude(kuraPosition.getAltitude());
        kapuaPosition.setHeading(kuraPosition.getHeading());
        kapuaPosition.setLatitude(kuraPosition.getLatitude());
        kapuaPosition.setLongitude(kuraPosition.getLongitude());
        kapuaPosition.setPrecision(kuraPosition.getPrecision());
        kapuaPosition.setSatellites(kuraPosition.getSatellites());
        kapuaPosition.setSpeed(kuraPosition.getSpeed());
        kapuaPosition.setStatus(kuraPosition.getStatus());
        kapuaPosition.setTimestamp(kuraPosition.getTimestamp());

        //
        // Return Kapua Position
        return kapuaPosition;
    }

    @Override
    public Class<KuraMessage> getClassFrom()
    {
        return KuraMessage.class;
    }

    @Override
    public Class<KapuaMessage> getClassTo()
    {
        return KapuaMessage.class;
    }

}
