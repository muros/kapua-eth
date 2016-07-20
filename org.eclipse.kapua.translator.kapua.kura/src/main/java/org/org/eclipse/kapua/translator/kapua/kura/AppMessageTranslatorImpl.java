package org.org.eclipse.kapua.translator.kapua.kura;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.device.app.KapuaAppChannel;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.message.kura.KuraChannel;
import org.eclipse.kapua.service.device.call.message.kura.KuraMessage;
import org.eclipse.kapua.service.device.call.message.kura.KuraPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;

@SuppressWarnings("rawtypes")
public class AppMessageTranslatorImpl implements Translator<KapuaAppChannel, KuraChannel, KapuaPayload, KuraPayload, KapuaMessage, KuraMessage>
{
    @Override
    public KuraMessage translate(KapuaMessage message)
        throws KapuaException
    {
        //
        // Kura channel
        KuraChannel kuraChannel = translate(message.getChannel());

        //
        // Kura payload
        KuraPayload kuraPayload = translate(message.getPayload());

        //
        // Kura message
        KuraMessage kuraMessage = new KuraMessage<KuraChannel, KuraPayload>(kuraChannel,
                                                                            message.getReceivedOn(),
                                                                            kuraPayload);

        //
        // Return result
        return kuraMessage;
    }

    @Override
    public KuraChannel translate(KapuaAppChannel kapuaChannel)
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

    @Override
    public Class<?> getClassFrom()
    {
        return KapuaMessage.class;
    }

    @Override
    public Class<?> getClassTo()
    {
        return KuraMessage.class;
    }

}
