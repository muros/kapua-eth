package org.org.eclipse.kapua.translator.kura.kapua;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.kura.ResponseMetrics;
import org.eclipse.kapua.service.device.call.kura.app.BundleMetrics;
import org.eclipse.kapua.service.device.call.kura.model.XmlBundleInfo;
import org.eclipse.kapua.service.device.call.kura.model.XmlDeploymentPackage;
import org.eclipse.kapua.service.device.call.kura.model.XmlDeploymentPackages;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseChannel;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponseMessage;
import org.eclipse.kapua.service.device.call.message.app.response.kura.KuraResponsePayload;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentFactory;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentPackage;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentPackageListResult;
import org.eclipse.kapua.service.device.management.deploy.DevicePackageBundleInfo;
import org.eclipse.kapua.service.device.management.deploy.DevicePackageBundleInfoListResult;
import org.eclipse.kapua.service.device.management.response.KapuaResponseCode;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
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

    private TranslatorAppBundleKuraKapua()
    {
        metricsDictionary = new HashMap<>();

        metricsDictionary.put(BundleMetrics.APP_ID, BundleAppProperties.APP_NAME);
        metricsDictionary.put(BundleMetrics.APP_VERSION, BundleAppProperties.APP_VERSION);
    }

    @Override
    public BundleResponseMessage translate(KuraResponseMessage message)
        throws KapuaException
    {
        //
        // Kura channel
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(message.getChannel().getScope());

        DeviceRegistryService deviceService = locator.getService(DeviceRegistryService.class);
        Device device = deviceService.findByClientId(account.getId(), message.getChannel().getClientId());

        BundleResponseChannel bundleResponseChannel = translate(message.getChannel());

        //
        // Kura payload
        BundleResponsePayload responsePayload = translate(message.getPayload());

        //
        // Kura Message
        BundleResponseMessage kapuaMessage = new BundleResponseMessage();
        kapuaMessage.setScopeId(account.getId());
        kapuaMessage.setDeviceId(device.getId());
        kapuaMessage.setSemanticChannel(bundleResponseChannel);
        kapuaMessage.setPayload(responsePayload);
        kapuaMessage.setCapturedOn(message.getPayload().getTimestamp());
        kapuaMessage.setSentOn(message.getPayload().getTimestamp());
        kapuaMessage.setReceivedOn(message.timestamp());
        kapuaMessage.setResponseCode(KapuaResponseCode.valueOf((String) message.getPayload().metrics().get(ResponseMetrics.RESP_METRIC_EXIT_CODE.getValue())));

        //
        // Return result
        return kapuaMessage;
    }

    private BundleResponseChannel translate(KuraResponseChannel channel)
        throws KapuaException
    {

        if (!CONTROL_MESSAGE_CLASSIFIER.equals(channel.getMessageClassification())) {
            throw new TranslatorException(TranslatorErrorCodes.INVALID_CHANNEL_CLASSIFIER,
                                          null,
                                          channel.getMessageClassification());
        }

        BundleResponseChannel bundleResponseChannel = new BundleResponseChannel();

        String[] appIdTokens = channel.getAppId().split("-");

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

        return bundleResponseChannel;
    }

    private BundleResponsePayload translate(KuraResponsePayload payload)
        throws KapuaException
    {
        BundleResponsePayload bundleResponsePayload = new BundleResponsePayload();

        bundleResponsePayload.setExceptionMessage((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_MESSAGE.getValue()));
        bundleResponsePayload.setExceptionStack((String) payload.metrics().get(ResponseMetrics.RESP_METRIC_EXCEPTION_STACK.getValue()));

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

        return bundleResponsePayload;
    }

    private void translateBody(BundleResponsePayload bundleResponsePayload, String charEncoding, XmlDeploymentPackages kuraDevicePackages)
        throws DeviceManagementException
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
            throw new DeviceManagementException(DeviceManagementErrorCodes.REQUEST_EXCEPTION, e, (Object[]) null); // null for now
        }
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