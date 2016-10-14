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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.metatype.Password;
import org.eclipse.kapua.commons.configuration.metatype.TadImpl;
import org.eclipse.kapua.commons.configuration.metatype.TiconImpl;
import org.eclipse.kapua.commons.configuration.metatype.TocdImpl;
import org.eclipse.kapua.commons.configuration.metatype.ToptionImpl;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTicon;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.ConfigurationMetrics;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceComponentConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraPassword;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestMessage;
import org.eclipse.kapua.service.device.call.message.app.request.kura.KuraRequestPayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.internal.DeviceConfigurationAppProperties;
import org.eclipse.kapua.service.device.management.configuration.internal.DeviceConfigurationImpl;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestChannel;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestMessage;
import org.eclipse.kapua.service.device.management.configuration.message.internal.ConfigurationRequestPayload;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;

/**
 * Messages translator implementation from {@link ConfigurationRequestMessage} to {@link KuraRequestMessage}
 * 
 * @since 1.0
 *
 */
public class TranslatorAppConfigurationKapuaKura extends Translator<ConfigurationRequestMessage, KuraRequestMessage>
{
    private static final String                                          CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<DeviceConfigurationAppProperties, ConfigurationMetrics> propertiesDictionary;

    /**
     * Constructor
     */
    public TranslatorAppConfigurationKapuaKura()
    {
        propertiesDictionary = new HashMap<>();

        propertiesDictionary.put(DeviceConfigurationAppProperties.APP_NAME, ConfigurationMetrics.APP_ID);
        propertiesDictionary.put(DeviceConfigurationAppProperties.APP_VERSION, ConfigurationMetrics.APP_VERSION);
    }

    @Override
    public KuraRequestMessage translate(ConfigurationRequestMessage kapuaMessage)
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

    private KuraRequestChannel translate(ConfigurationRequestChannel kapuaChannel)
        throws KapuaException
    {
        KuraRequestChannel kuraRequestChannel = new KuraRequestChannel();
        kuraRequestChannel.setMessageClassification(CONTROL_MESSAGE_CLASSIFIER);

        // Build appId
        StringBuilder appIdSb = new StringBuilder();
        appIdSb.append(propertiesDictionary.get(DeviceConfigurationAppProperties.APP_NAME).getValue())
               .append("-")
               .append(propertiesDictionary.get(DeviceConfigurationAppProperties.APP_VERSION).getValue());

        kuraRequestChannel.setAppId(appIdSb.toString());
        kuraRequestChannel.setMethod(MethodDictionaryKapuaKura.get(kapuaChannel.getMethod()));

        // Build resources
        List<String> resources = new ArrayList<>();
        if (kapuaChannel.getConfigurationId() == null) {
            resources.add("configurations");
            String componentId = kapuaChannel.getComponentId();
            if (componentId != null) {
                resources.add(componentId);
            }
        }
        else if (kapuaChannel.getConfigurationId() != null) {
            resources.add("snapshots");

            String configurationId = kapuaChannel.getConfigurationId();
            if (configurationId != null) {
                resources.add(configurationId);
            }
        }
        kuraRequestChannel.setResources(resources.toArray(new String[resources.size()]));

        //
        // Return Kura Channel
        return kuraRequestChannel;
    }

    private KuraRequestPayload translate(ConfigurationRequestPayload kapuaPayload)
        throws KapuaException
    {
        KuraRequestPayload kuraRequestPayload = new KuraRequestPayload();

        if (kapuaPayload.getBody() != null) {
            DeviceConfiguration kapuaDeviceConfiguration;
            try {
                kapuaDeviceConfiguration = XmlUtil.unmarshal(new String(kapuaPayload.getBody()),
                                                             DeviceConfigurationImpl.class);
            }
            catch (Exception e) {
                throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                              e,
                                              kapuaPayload.getBody());
            }

            KuraDeviceConfiguration kuraDeviceConfiguration = translate(kapuaDeviceConfiguration);

            byte[] body;
            try {
                body = XmlUtil.marshal(kuraDeviceConfiguration).getBytes();
            }
            catch (Exception e) {
                throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                              e,
                                              kapuaPayload.getBody());
            }

            kuraRequestPayload.setBody(body);
        }

        //
        // Return Kura Payload
        return kuraRequestPayload;
    }

    private KuraDeviceConfiguration translate(DeviceConfiguration kapuaDeviceConfiguration)
        throws KapuaException
    {
        KuraDeviceConfiguration kuraDeviceConfiguration = new KuraDeviceConfiguration();

        for (DeviceComponentConfiguration kapuaDeviceCompConf : kapuaDeviceConfiguration.getComponentConfigurations()) {

            KuraDeviceComponentConfiguration kuraComponentConfiguration = new KuraDeviceComponentConfiguration();
            kuraComponentConfiguration.setComponentId(kapuaDeviceCompConf.getId());
            kuraComponentConfiguration.setProperties(translate(kapuaDeviceCompConf.getProperties()));

            // Translate also definitions when they are available
            if (kapuaDeviceCompConf.getDefinition() != null) {
                kuraComponentConfiguration.setDefinition(translate(kapuaDeviceCompConf.getDefinition()));
            }

            // Add to kapua configuration
            kuraDeviceConfiguration.getConfigurations().add(kuraComponentConfiguration);
        }

        return kuraDeviceConfiguration;
    }

    private KapuaTocd translate(KapuaTocd kapuaDefinition)
    {
        TocdImpl definition = new TocdImpl();

        definition.setId(kapuaDefinition.getId());
        definition.setName(kapuaDefinition.getName());
        definition.setDescription(kapuaDefinition.getDescription());

        for (KapuaTad kapuaAd : kapuaDefinition.getAD()) {
            TadImpl ad = new TadImpl();
            ad.setCardinality(kapuaAd.getCardinality());
            ad.setDefault(ad.getDefault());
            ad.setDescription(kapuaAd.getDescription());
            ad.setId(kapuaAd.getId());
            ad.setMax(kapuaAd.getMax());
            ad.setMin(kapuaAd.getMin());
            ad.setName(kapuaAd.getName());
            ad.setType(kapuaAd.getType());
            ad.setRequired(kapuaAd.isRequired());

            for (KapuaToption kuraToption : kapuaAd.getOption()) {
                ToptionImpl kapuaToption = new ToptionImpl();

                kapuaToption.setLabel(kuraToption.getLabel());
                kapuaToption.setValue(kuraToption.getValue());

                ad.addOption(kapuaToption);
            }

            for (Entry<QName, String> entry : kapuaAd.getOtherAttributes().entrySet()) {
                ad.putOtherAttribute(entry.getKey(),
                                     entry.getValue());
            }

            definition.addAD(ad);
        }

        for (KapuaTicon kapuaIcon : kapuaDefinition.getIcon()) {
            KapuaTicon icon = new TiconImpl();
            icon.setResource(kapuaIcon.getResource());
            icon.setSize(kapuaIcon.getSize());

            definition.addIcon(icon);
        }

        for (Object kapuaAny : kapuaDefinition.getAny()) {
            definition.addAny(kapuaAny);
        }

        for (Entry<QName, String> entry : kapuaDefinition.getOtherAttributes().entrySet()) {
            definition.putOtherAttribute(entry.getKey(),
                                         entry.getValue());
        }

        return definition;
    }

    private Map<String, Object> translate(Map<String, Object> kapuaProperties)
    {
        Map<String, Object> properties = new HashMap<>();
        for (Entry<String, Object> entry : kapuaProperties.entrySet()) {
            Object value = entry.getValue();

            //
            // Special management of Password type field
            if (value instanceof Password) {
                value = new KuraPassword(((Password) value).getPassword());
            }
            else if (value instanceof Password[]) {
                Password[] passwords = (Password[]) value;
                KuraPassword[] kuraPasswords = new KuraPassword[passwords.length];

                int i = 0;
                for (Password p : passwords) {
                    kuraPasswords[i++] = new KuraPassword(p.getPassword());
                }

                value = kuraPasswords;
            }

            //
            // Set property
            properties.put(entry.getKey(),
                           value);
        }
        return properties;
    }

    @Override
    public Class<ConfigurationRequestMessage> getClassFrom()
    {
        return ConfigurationRequestMessage.class;
    }

    @Override
    public Class<KuraRequestMessage> getClassTo()
    {
        return KuraRequestMessage.class;
    }

}
