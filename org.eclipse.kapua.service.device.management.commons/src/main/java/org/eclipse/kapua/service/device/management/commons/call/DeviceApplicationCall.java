package org.eclipse.kapua.service.device.management.commons.call;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.device.call.DeviceCall;
import org.eclipse.kapua.service.device.call.DeviceMessageFactory;
import org.eclipse.kapua.service.device.call.KapuaDeviceCallFactory;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestChannel;
import org.eclipse.kapua.service.device.call.message.app.request.DeviceRequestPayload;
import org.eclipse.kapua.service.device.call.message.app.response.DeviceResponseMessage;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSetting;
import org.eclipse.kapua.service.device.call.message.kura.setting.DeviceCallSettingKeys;
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
    private DeviceRequestPayload                            requestPayload;

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

    public void setRequestPayload(DeviceRequestPayload requestPayload)
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
        DeviceMessageFactory messageFactory = locator.getFactory(DeviceMessageFactory.class);
        DeviceRequestChannel requestDestination = messageFactory.newRequestChannel();
        requestDestination.setMessageClassification(DeviceCallSetting.getInstance().getString(DeviceCallSettingKeys.DESTINATION_CONTROL_PREFIX));
        requestDestination.setScope(account.getName());
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
        DeviceCall deviceCall = kapuaDeviceCallFactory.newDeviceCall(requestDestination,
                                                                     requestPayload,
                                                                     config.getLong(DeviceManagementSettingKey.REQUEST_TIMEOUT));

        DeviceResponseMessage responseMessage = deviceCall.send();

        //
        // Handle response
        T response = null;
        if (responseHandler != null) {
            response = responseHandler.handle(responseMessage);
        }

        return response;
    }
}
