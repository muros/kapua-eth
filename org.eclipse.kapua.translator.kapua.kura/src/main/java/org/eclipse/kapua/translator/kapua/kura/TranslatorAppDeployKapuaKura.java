/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.translator.kapua.kura;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.DeployMetrics;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.packages.internal.PackageAppProperties;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestChannel;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestMessage;
import org.eclipse.kapua.service.device.management.packages.message.internal.PackageRequestPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;

public class TranslatorAppDeployKapuaKura extends Translator<PackageRequestMessage, KuraRequestMessage>
{
    private static final String                            CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<PackageAppProperties, DeployMetrics> propertiesDictionary;

    public TranslatorAppDeployKapuaKura()
    {
        propertiesDictionary = new HashMap<>();
        propertiesDictionary.put(PackageAppProperties.APP_NAME, DeployMetrics.APP_ID);
        propertiesDictionary.put(PackageAppProperties.APP_VERSION, DeployMetrics.APP_VERSION);
    }

    @Override
    public KuraRequestMessage translate(PackageRequestMessage kapuaMessage)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.find(kapuaMessage.getScopeId());

        DeviceRegistryService deviceService = locator.getService(DeviceRegistryService.class);
        Device device = deviceService.find(kapuaMessage.getScopeId(),
                                           kapuaMessage.getDeviceId());

        KuraRequestChannel kuraRequestChannel = translate(kapuaMessage.getChannel());
        kuraRequestChannel.setScope(account.getName());
        kuraRequestChannel.setClientId(device.getClientId());

        //
        // Kura payload
        KuraRequestPayload kuraPayload = translate(kapuaMessage.getPayload());

        //
        // Return Kura Message
        return new KuraRequestMessage(kuraRequestChannel,
                                      kapuaMessage.getReceivedOn(),
                                      kuraPayload);
    }

    private KuraRequestChannel translate(PackageRequestChannel kapuaChannel)
        throws KapuaException
    {
        KuraRequestChannel kuraRequestChannel = new KuraRequestChannel();
        kuraRequestChannel.setMessageClassification(CONTROL_MESSAGE_CLASSIFIER);

        // Build appId
        StringBuilder appIdSb = new StringBuilder();
        appIdSb.append(propertiesDictionary.get(PackageAppProperties.APP_NAME).getValue())
               .append("-")
               .append(propertiesDictionary.get(PackageAppProperties.APP_VERSION).getValue());

        kuraRequestChannel.setAppId(appIdSb.toString());
        kuraRequestChannel.setMethod(MethodDictionaryKapuaKura.get(kapuaChannel.getMethod()));

        // Build resources
        List<String> resources = new ArrayList<>();
        switch (kapuaChannel.getMethod()) {
            case READ:
                resources.add("packages");
                break;
            case EXECUTE:
            {
                if (kapuaChannel.isInstall()) {
                    resources.add("install");
                }
                else {
                    resources.add("uninstall");
                }
            }
                break;
            case CREATE:
            case DELETE:
            case OPTIONS:
            case WRITE:
            default:
                break;

        }
        kuraRequestChannel.setResources(resources.toArray(new String[resources.size()]));

        //
        // Return Kura Channel
        return kuraRequestChannel;
    }

    private KuraRequestPayload translate(PackageRequestPayload kapuaPayload)
        throws KapuaException
    {
        KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

        String deployFileName = kapuaPayload.getDeployInstallFileName();
        if (deployFileName != null) {
            kuraRequestPayload.getMetrics()
                              .put(propertiesDictionary.get(PackageAppProperties.APP_PROPERTY_DEPLOY_INSTALL_FILENAME).getValue(),
                                   deployFileName);
        }

        String deployInstallUrl = kapuaPayload.getDeployInstallUrl();
        if (deployInstallUrl != null) {
            kuraRequestPayload.getMetrics()
                              .put(propertiesDictionary.get(PackageAppProperties.APP_PROPERTY_DEPLOY_INSTALL_URL).getValue(),
                                   deployInstallUrl);
        }

        String deployUninstallFileName = kapuaPayload.getDeployUninstallFileName();
        if (deployUninstallFileName != null) {
            try {
                kuraRequestPayload.setBody(deployUninstallFileName.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                              e,
                                              deployUninstallFileName);
            }
        }

        byte[] deployBody = kapuaPayload.getBody();
        if (deployBody != null) {
            kuraRequestPayload.setBody(deployBody);
        }

        //
        // Return Kura Payload
        return kuraRequestPayload;
    }

    @Override
    public Class<PackageRequestMessage> getClassFrom()
    {
        return PackageRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo()
    {
        return KuraRequestMessage.class;
    }
}
