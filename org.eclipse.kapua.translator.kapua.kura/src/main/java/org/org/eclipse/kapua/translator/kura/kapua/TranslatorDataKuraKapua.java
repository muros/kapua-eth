package org.org.eclipse.kapua.translator.kura.kapua;

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
    public KapuaMessage translate(KuraMessage message)
        throws KapuaException
    {
        //
        // Kapua channel
        KapuaChannel kapuaChannel = translate(message.getChannel());

        //
        // Kapua payload
        KapuaPayload kapuaPayload = translate(message.getPayload());

        //
        // Kapua message
        KapuaMessage kapuaMessage = new KapuaMessageImpl(kapuaChannel,
                                                         kapuaPayload);
        kapuaMessage.setCapturedOn(message.getPayload().getTimestamp());
        kapuaMessage.setSentOn(message.getPayload().getTimestamp());
        kapuaMessage.setReceivedOn(message.timestamp());
        kapuaMessage.setPosition(translate(message.getPayload().getPosition()));

        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);

        Account account = accountService.findByName(message.getChannel().getScope());
        kapuaMessage.setScopeId(account.getId());

        Device device = deviceRegistryService.findByClientId(account.getId(), message.getChannel().getClientId());
        kapuaMessage.setDeviceId(device.getId());

        //
        // Return Result
        return kapuaMessage;
    }

    public KapuaChannel translate(KuraChannel channel)
        throws KapuaException
    {
        KapuaChannel kapuaChannel = new KapuaChannelImpl();
        kapuaChannel.setSemanticParts(channel.getSemanticChannelParts());

        return kapuaChannel;
    }

    public KapuaPayload translate(KuraPayload payload)
        throws KapuaException
    {
        KapuaPayload kapuaPayload = new KapuaPayloadImpl();
        kapuaPayload.setBody(payload.getBody());
        kapuaPayload.setProperties(payload.metrics());

        return kapuaPayload;
    }

    private KapuaPosition translate(DevicePosition position)
    {
        KapuaPosition kapuaPosition = new KapuaPositionImpl();

        kapuaPosition.setAltitude(position.getAltitude());
        kapuaPosition.setHeading(position.getHeading());
        kapuaPosition.setLatitude(position.getLatitude());
        kapuaPosition.setLongitude(position.getLongitude());
        kapuaPosition.setPrecision(position.getPrecision());
        kapuaPosition.setSatellites(position.getSatellites());
        kapuaPosition.setSpeed(position.getSpeed());
        kapuaPosition.setStatus(position.getStatus());
        kapuaPosition.setTimestamp(position.getTimestamp());

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
