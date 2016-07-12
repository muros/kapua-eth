package org.eclipse.kapua.service.device.management.commons.call;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.KapuaDeviceCall;
import org.eclipse.kapua.service.device.call.KapuaDeviceCallFactory;
import org.eclipse.kapua.service.device.call.message.KapuaRequestDestination;
import org.eclipse.kapua.service.device.call.message.KapuaRequestPayload;
import org.eclipse.kapua.service.device.call.message.KapuaResponseMessage;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;

public class DeviceApplicationCall<T>
{
    private KapuaId                                         scopeId;
    private KapuaId                                         deviceId;
    private String                                          appId;
    private String                                          method;
    private String[]                                        resources;
    private KapuaRequestPayload                             requestPayload;

    private AbstractDeviceApplicationCallResponseHandler<T> responseHandler;

    public DeviceApplicationCall(KapuaId scopeId, KapuaId deviceId, String appId, String method)
    {
        this(scopeId, deviceId, appId, method, null);
    }

    public DeviceApplicationCall(KapuaId scopeId, KapuaId deviceId, String appId, String method, String[] resources)
    {
        this.scopeId = scopeId;
        this.deviceId = deviceId;
        this.appId = appId;
        this.method = method;
        this.resources = resources;
    }

    public void setRequestPayload(KapuaRequestPayload requestPayload)
    {
        this.requestPayload = requestPayload;
    }

    public void setResponseHandler(AbstractDeviceApplicationCallResponseHandler<T> responseHandler)
    {
        this.responseHandler = responseHandler;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T send()
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.find(scopeId);

        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = deviceRegistryService.find(scopeId, deviceId);

        //
        // Build request topic
        DeviceManagementSetting devManagementConfig = DeviceManagementSetting.getInstance();
        String topicPrefix = devManagementConfig.getString(DeviceManagementSettingKey.CONTROL_TOPIC_PREFIX);

        KapuaRequestDestination requestDestination = (KapuaRequestDestination) new Object();
        requestDestination.setControlDestinationPrefix(topicPrefix);
        requestDestination.setScopeNamespace(account.getName());
        requestDestination.setClientId(device.getClientId());
        requestDestination.setAppId(appId);
        requestDestination.setMethod(method);
        requestDestination.setResources(resources);

        //
        // Add request body
        // if (requestPayload == null) {
        // requestPayload = new KapuaRequestPayload();
        // }

        //
        // Send request
        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        KapuaDeviceCallFactory kapuaDeviceCallFactory = locator.getFactory(KapuaDeviceCallFactory.class);
        KapuaDeviceCall deviceCall = kapuaDeviceCallFactory.newInstance(requestDestination,
                                                                        requestPayload,
                                                                        config.getLong(DeviceManagementSettingKey.REQUEST_TIMEOUT));

        KapuaResponseMessage responseMessage = deviceCall.send();

        //
        // Handle response
        T response = null;
        if (responseHandler != null) {
            response = responseHandler.handle(responseMessage);
        }

        return response;
    }
}
