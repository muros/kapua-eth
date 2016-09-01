package org.eclipse.kapua.translator.kura.kapua;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.xml.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.app.BundleMetrics;
import org.eclipse.kapua.service.device.call.kura.app.ResponseMetrics;
import org.eclipse.kapua.service.device.call.kura.model.XmlBundleInfo;
import org.eclipse.kapua.service.device.call.kura.model.XmlDeploymentPackage;
import org.eclipse.kapua.service.device.call.kura.model.XmlDeploymentPackages;
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
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.translator.Translator;
import org.eclipse.kapua.translator.exception.TranslatorErrorCodes;
import org.eclipse.kapua.translator.exception.TranslatorException;
import org.org.eclipse.kapua.service.device.management.bundle.internal.BundleAppProperties;
import org.org.eclipse.kapua.service.device.management.bundle.message.internal.BundleResponseChannel;
import org.org.eclipse.kapua.service.device.management.bundle.message.internal.BundleResponseMessage;
import org.org.eclipse.kapua.service.device.management.bundle.message.internal.BundleResponsePayload;

public class TranslatorAppBundleKuraKapua implements Translator<KuraResponseMessage, BundleResponseMessage>
{
    private static final String                            CONTROL_MESSAGE_CLASSIFIER = DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_MESSAGE_CLASSIFIER);
    private static Map<BundleMetrics, BundleAppProperties> metricsDictionary;

    public TranslatorAppBundleKuraKapua()
    {
        metricsDictionary = new HashMap<>();

        metricsDictionary.put(BundleMetrics.APP_ID, BundleAppProperties.APP_NAME);
        metricsDictionary.put(BundleMetrics.APP_VERSION, BundleAppProperties.APP_VERSION);
    }

    @Override
    public BundleResponseMessage translate(KuraResponseMessage kuraMessage)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(kuraMessage.getChannel().getScope());

        BundleResponseChannel bundleResponseChannel = translate(kuraMessage.getChannel());

        //
        // Kura payload
        BundleResponsePayload responsePayload = translate(kuraMessage.getPayload());

        //
        // Kura Message
        BundleResponseMessage kapuaMessage = new BundleResponseMessage();
        kapuaMessage.setScopeId(account.getId());
        kapuaMessage.setChannel(bundleResponseChannel);
        kapuaMessage.setPayload(responsePayload);
        kapuaMessage.setCapturedOn(kuraMessage.getPayload().getTimestamp());
        kapuaMessage.setSentOn(kuraMessage.getPayload().getTimestamp());
        kapuaMessage.setReceivedOn(kuraMessage.getTimestamp());
        kapuaMessage.setResponseCode(translate((Integer) kuraMessage.getPayload().getMetrics().get(ResponseMetrics.RESP_METRIC_EXIT_CODE.getValue())));

        //
        // Return Kapua Message
        return kapuaMessage;
    }

    private BundleResponseChannel translate(KuraResponseChannel kuraChannel)
        throws KapuaException
    {

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(kuraChannel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          kuraChannel.getMessageClassification());
        }

        BundleResponseChannel bundleResponseChannel = new BundleResponseChannel();

        String[] appIdTokens = kuraChannel.getAppId().split("-");

        if (!BundleMetrics.APP_ID.getValue().equals(appIdTokens[0])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_NAME,
                                          null,
                                          appIdTokens[0]);
        }

        if (!BundleMetrics.APP_VERSION.getValue().equals(appIdTokens[1])) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_APP_VERSION,
                                          null,
                                          appIdTokens[1]);
        }

        bundleResponseChannel.setAppName(BundleAppProperties.APP_NAME);
        bundleResponseChannel.setVersion(BundleAppProperties.APP_VERSION);

        //
        // Return Kapua Channel
        return bundleResponseChannel;
    }

    private BundleResponsePayload translate(KuraResponsePayload kuraPayload)
        throws KapuaException
    {
        BundleResponsePayload bundleResponsePayload = new BundleResponsePayload();

        bundleResponsePayload.setExceptionMessage((String) kuraPayload.getMetrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        bundleResponsePayload.setExceptionStack((String) kuraPayload.getMetrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        String body = null;
        try {
            body = new String(bundleResponsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                          e,
                                          bundleResponsePayload.getBody());
        }

        XmlDeploymentPackages kuraDevicePackages = null;
        try {
            kuraDevicePackages = XmlUtil.unmarshal(body, XmlDeploymentPackages.class);
        }
        catch (Exception e) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_PAYLOAD,
                                          e,
                                          body);
        }

        translateBody(bundleResponsePayload, charEncoding, kuraDevicePackages);

        //
        // Return Kapua Payload
        return bundleResponsePayload;
    }

    private void translateBody(BundleResponsePayload bundleResponsePayload, String charEncoding, XmlDeploymentPackages kuraDevicePackages)
        throws KapuaException
    {
        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceDeploymentFactory deviceDeploymentFactory = locator.getFactory(DeviceDeploymentFactory.class);

            XmlDeploymentPackage[] deploymentPackageArray = kuraDevicePackages.getDeploymentPackages();
            DeviceDeploymentPackageListResult deviceDeploymentPackages = deviceDeploymentFactory.newDeviceDeploymentPackageListResultInstance();

            for (XmlDeploymentPackage deploymentPackage : deploymentPackageArray) {
                DeviceDeploymentPackage deviceDeploymentPackage = deviceDeploymentFactory.newDeviceDeploymentPackageInstance();
                deviceDeploymentPackage.setName(deploymentPackage.getName());
                deviceDeploymentPackage.setVersion(deploymentPackage.getVersion());

                DevicePackageBundleInfoListResult devicePackageBundleInfos = deviceDeploymentFactory.newDevicePackageBundleInfoListResultInstance();
                XmlBundleInfo[] bundleInfoArray = deploymentPackage.getBundleInfos();
                for (XmlBundleInfo bundleInfo : bundleInfoArray) {
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

            bundleResponsePayload.setBody(requestBody);
        }
        catch (Exception e) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_BODY,
                                          e,
                                          kuraDevicePackages);
        }
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
    public Class<BundleResponseMessage> getClassTo()
    {
        return BundleResponseMessage.class;
    }
}