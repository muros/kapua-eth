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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.DeployMetrics;
import org.eclipse.kapua.service.device.call.kura.app.ResponseMetrics;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.deploy.internal.DeployAppProperties;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponseChannel;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponseMessage;
import org.eclipse.kapua.service.device.management.deploy.message.internal.DeployResponsePayload;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
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
        kapuaMessage.setResponseCode(translate((Integer) kuraMessage.getPayload().getMetrics().get(ResponseMetrics.RESP_METRIC_EXIT_CODE.getValue())));

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

    private DeployResponsePayload translate(KuraResponsePayload kuraPayload)
        throws KapuaException
    {
        DeployResponsePayload responsePayload = new DeployResponsePayload();

        Map<String, Object> metrics = kuraPayload.getMetrics();
        responsePayload.setExceptionMessage((String) metrics.get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        responsePayload.setExceptionStack((String) metrics.get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));

        //
        // Return Kapua Payload
        return responsePayload;
    }

    private KapuaResponseCode translate(Integer kuraResponseCode)
    {
        KapuaResponseCode responseCode;
        switch (kuraResponseCode) {
            case 200:
                responseCode = KapuaResponseCode.ACCEPTED;
                break;

            case 400:
                responseCode = KapuaResponseCode.BAD_REQUEST;
                break;

            case 404:
                responseCode = KapuaResponseCode.NOT_FOUND;
                break;
            case 500:
            default:
                responseCode = KapuaResponseCode.INTERNAL_ERROR;
                break;
        }

        return responseCode;
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
