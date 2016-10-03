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
package org.eclipse.kapua.translator.kura.kapua;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.DeployMetrics;
import org.eclipse.kapua.service.device.call.kura.app.ResponseMetrics;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraBundleInfo;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackage;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackages;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentFactory;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentPackage;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentPackageListResult;
import org.eclipse.kapua.service.device.management.deploy.DevicePackageBundleInfo;
import org.eclipse.kapua.service.device.management.deploy.DevicePackageBundleInfoListResult;
import org.eclipse.kapua.service.device.management.deploy.internal.DeployAppProperties;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponseChannel;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponseMessage;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponsePayload;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;

public class TranslatorAppDeployKuraKapua extends Translator<KuraResponseMessage, DeployResponseMessage>
{
    private static final String                            CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<DeployMetrics, DeployAppProperties> metricsDictionary;

    public TranslatorAppDeployKuraKapua()
    {
        metricsDictionary = new HashMap<>();

        metricsDictionary.put(DeployMetrics.APP_ID, DeployAppProperties.APP_NAME);
        metricsDictionary.put(DeployMetrics.APP_VERSION, DeployAppProperties.APP_VERSION);
    }

    @Override
    public DeployResponseMessage translate(KuraResponseMessage kuraMessage)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(kuraMessage.getChannel().getScope());

        DeployResponseChannel responseChannel = translate(kuraMessage.getChannel());

        //
        // Kura payload
        DeployResponsePayload responsePayload = translate(kuraMessage.getPayload());

        //
        // Kura Message
        DeployResponseMessage kapuaMessage = new DeployResponseMessage();
        kapuaMessage.setScopeId(account.getId());
        kapuaMessage.setChannel(responseChannel);
        kapuaMessage.setPayload(responsePayload);
        kapuaMessage.setCapturedOn(kuraMessage.getPayload().getTimestamp());
        kapuaMessage.setSentOn(kuraMessage.getPayload().getTimestamp());
        kapuaMessage.setReceivedOn(kuraMessage.getTimestamp());
        kapuaMessage.setResponseCode(TranslatorKuraKapuaUtils.translate((Integer) kuraMessage.getPayload().getMetrics().get(ResponseMetrics.RESP_METRIC_EXIT_CODE.getValue())));

        //
        // Return Kapua Message
        return kapuaMessage;
    }

    private DeployResponseChannel translate(KuraResponseChannel kuraChannel)
        throws KapuaException
    {

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(kuraChannel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          kuraChannel.getMessageClassification());
        }

        DeployResponseChannel responseChannel = new DeployResponseChannel();

        String[] appIdTokens = kuraChannel.getAppId().split("-");

        if (!DeployMetrics.APP_ID.getValue().equals(appIdTokens[0])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_NAME,
                                          null,
                                          appIdTokens[0]);
        }

        if (!DeployMetrics.APP_VERSION.getValue().equals(appIdTokens[1])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_VERSION,
                                          null,
                                          appIdTokens[1]);
        }

        responseChannel.setAppName(DeployAppProperties.APP_NAME);
        responseChannel.setVersion(DeployAppProperties.APP_VERSION);

        //
        // Return Kapua Channel
        return responseChannel;
    }

    private DeployResponsePayload translate(KuraResponsePayload kuraResponsePayload)
        throws KapuaException
    {
        DeployResponsePayload responsePayload = new DeployResponsePayload();

        Map<String, Object> metrics = kuraResponsePayload.getMetrics();
        responsePayload.setExceptionMessage((String) metrics.get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        responsePayload.setExceptionStack((String) metrics.get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);
        String body = null;
        try {
            body = new String(kuraResponsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                          e,
                                          kuraResponsePayload.getBody());
        }

        KuraDeploymentPackages kuraDeploymentPackages = null;
        try {
            kuraDeploymentPackages = XmlUtil.unmarshal(body, KuraDeploymentPackages.class);
        }
        catch (Exception e) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                          e,
                                          body);
        }

        translate(responsePayload, charEncoding, kuraDeploymentPackages);

        //
        // Return Kapua Payload
        return responsePayload;
    }

    private void translate(DeployResponsePayload packageResponsePayload,
                           String charEncoding,
                           KuraDeploymentPackages kuraDeploymentPackages)
        throws KapuaException
    {
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceDeploymentFactory deviceDeploymentFactory = locator.getFactory(DeviceDeploymentFactory.class);

            KuraDeploymentPackage[] deploymentPackageArray = kuraDeploymentPackages.getDeploymentPackages();
            DeviceDeploymentPackageListResult deviceDeploymentPackages = deviceDeploymentFactory.newDeviceDeploymentPackageListResultInstance();

            for (KuraDeploymentPackage deploymentPackage : deploymentPackageArray) {
                DeviceDeploymentPackage deviceDeploymentPackage = deviceDeploymentFactory.newDeviceDeploymentPackageInstance();
                deviceDeploymentPackage.setName(deploymentPackage.getName());
                deviceDeploymentPackage.setVersion(deploymentPackage.getVersion());

                DevicePackageBundleInfoListResult devicePackageBundleInfos = deviceDeploymentFactory.newDevicePackageBundleInfoListResultInstance();
                KuraBundleInfo[] bundleInfoArray = deploymentPackage.getBundleInfos();
                for (KuraBundleInfo bundleInfo : bundleInfoArray) {
                    DevicePackageBundleInfo devicePackageBundleInfo = deviceDeploymentFactory.newDevicePackageBundleInfoInstance();
                    devicePackageBundleInfo.setName(bundleInfo.getName());
                    devicePackageBundleInfo.setVersion(bundleInfo.getVersion());

                    // Add the new DevicePackageBundleInfo object to the corresponding list
                    devicePackageBundleInfos.add(devicePackageBundleInfo);
                }

                // Add the new DeviceDeploymentPackage object to the corresponding list
                deviceDeploymentPackages.add(deviceDeploymentPackage);
            }

            StringWriter sw = new StringWriter();
            XmlUtil.marshal(deviceDeploymentPackages, sw);
            byte[] requestBody = sw.toString().getBytes(charEncoding);

            packageResponsePayload.setBody(requestBody);
        }
        catch (Exception e) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_BODY,
                                          e,
                                          kuraDeploymentPackages);
        }
    }

    @Override
    public Class<KuraResponseMessage> getClassFrom()
    {
        return KuraResponseMessage.class;
    }

    @Override
    public Class<DeployResponseMessage> getClassTo()
    {
        return DeployResponseMessage.class;
    }
}
