package org.org.eclipse.kapua.translator.kapua.kura;

import java.util.Date;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.device.data.KapuaDataChannel;
import org.eclipse.kapua.message.device.data.KapuaDataMessage;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.service.device.call.message.kura.KuraPosition;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;

@SuppressWarnings("rawtypes")
public class DataMessageTranslatorImpl implements Translator<KapuaDataChannel, KuraChannel, KapuaPayload, KuraPayload, KapuaDataMessage, KuraMessage>
{

    @Override
    public KuraMessage translate(KapuaDataMessage message)
        throws KapuaException
    {
        //
        // Kura channel
        KuraChannel kuraChannel = translate(message.getChannel());

        //
        // Kura payload
        KuraPayload kuraPayload = translate(message.getPayload());
        kuraPayload.setBody(message.getBody());
        kuraPayload.setPosition(translate(message.getPosition()));

        //
        // Kura Message
        KuraMessage kuraMessage = new KuraMessage<KuraChannel, KuraPayload>(kuraChannel,
                                                                            new Date(),
                                                                            kuraPayload);

        //
        // Return result
        return kuraMessage;
    }

    @Override
    public KuraChannel translate(KapuaDataChannel kapuaChannel)
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);

        Account account = accountService.find(kapuaChannel.getScopeId());
        Device device = deviceRegistryService.find(account.getId(), kapuaChannel.getDeviceId());

        KuraChannel kuraChannel = new KuraChannel();
        kuraChannel.setScope(account.getName());
        kuraChannel.setClientId(device.getClientId());

        kuraChannel.setSemanticChannelParts(kapuaChannel.getSemanticChannelParts());
        return kuraChannel;
    }

    @Override
    public KuraPayload translate(KapuaPayload metrics)
        throws KapuaException
    {
        KuraPayload kuraPayload = new KuraPayload();
        kuraPayload.metrics().putAll(metrics);
        return kuraPayload;
    }

    private KuraPosition translate(KapuaPosition position)
    {
        KuraPosition kuraPosition = new KuraPosition();

        kuraPosition.setAltitude(position.getAltitude());
        kuraPosition.setHeading(position.getHeading());
        kuraPosition.setLatitude(position.getLatitude());
        kuraPosition.setLongitude(position.getLongitude());
        kuraPosition.setPrecision(position.getPrecision());
        kuraPosition.setSatellites(position.getSatellites());
        kuraPosition.setSpeed(position.getSpeed());
        kuraPosition.setStatus(position.getStatus());
        kuraPosition.setTimestamp(position.getTimestamp());

        return kuraPosition;
    }

    @Override
    public Class<?> getClassFrom()
    {
        return KapuaDataMessage.class;
    }

    @Override
    public Class<?> getClassTo()
    {
        return KuraMessage.class;
    }

}
