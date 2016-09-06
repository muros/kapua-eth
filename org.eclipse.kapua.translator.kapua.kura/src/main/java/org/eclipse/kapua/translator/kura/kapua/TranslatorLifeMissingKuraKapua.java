package org.eclipse.kapua.translator.kura.kapua;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingChannel;
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingMessage;
import org.eclipse.kapua.message.device.lifecycle.KapuaMissingPayload;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaMissingChannelImpl;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaMissingMessageImpl;
import org.eclipse.kapua.message.internal.device.lifecycle.KapuaMissingPayloadImpl;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingChannel;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingMessage;
import org.eclipse.kapua.service.device.call.message.kura.lifecycle.KuraMissingPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;

public class TranslatorLifeMissingKuraKapua extends Translator<KuraMissingMessage, KapuaMissingMessage>
{

    @Override
    public KapuaMissingMessage translate(KuraMissingMessage kuraMissingMessage)
        throws KapuaException
    {
        KapuaMissingMessage kapuaMissingMessage = new KapuaMissingMessageImpl();
        kapuaMissingMessage.setChannel(translate(kuraMissingMessage.getChannel()));
        kapuaMissingMessage.setPayload(translate(kuraMissingMessage.getPayload()));

        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(kuraMissingMessage.getChannel().getScope());

        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = deviceRegistryService.findByClientId(account.getId(), kuraMissingMessage.getChannel().getClientId());

        if (device == null) {
            throw new KapuaEntityNotFoundException(Device.class.toString(), kuraMissingMessage.getChannel().getClientId());
        }
        
        kapuaMissingMessage.setDeviceId(device.getId());
        kapuaMissingMessage.setScopeId(account.getId());
        kapuaMissingMessage.setCapturedOn(kuraMissingMessage.getPayload().getTimestamp());
        kapuaMissingMessage.setSentOn(kuraMissingMessage.getPayload().getTimestamp());
        kapuaMissingMessage.setReceivedOn(kuraMissingMessage.getTimestamp());
        kapuaMissingMessage.setPosition(TranslatorKuraKapuaUtils.translate(kuraMissingMessage.getPayload().getPosition()));

        return kapuaMissingMessage;
    }

    private KapuaMissingChannel translate(KuraMissingChannel kuraMissingChannel)
        throws KapuaException
    {
        KapuaMissingChannel kapuaMissingChannel = new KapuaMissingChannelImpl();
        kapuaMissingChannel.setClientId(kuraMissingChannel.getClientId());
        return kapuaMissingChannel;
    }

    private KapuaMissingPayload translate(KuraMissingPayload kuraMissingPayload)
        throws KapuaException
    {
        KapuaMissingPayload kapuaMissingPayload = new KapuaMissingPayloadImpl();
        kapuaMissingPayload.setBody(kuraMissingPayload.getBody());
        kapuaMissingPayload.setProperties(kuraMissingPayload.getMetrics());
        return kapuaMissingPayload;
    }

    public Class<KuraMissingMessage> getClassFrom()
    {
        return KuraMissingMessage.class;
    }

    @Override
    public Class<KapuaMissingMessage> getClassTo()
    {
        return KapuaMissingMessage.class;
    }

}
