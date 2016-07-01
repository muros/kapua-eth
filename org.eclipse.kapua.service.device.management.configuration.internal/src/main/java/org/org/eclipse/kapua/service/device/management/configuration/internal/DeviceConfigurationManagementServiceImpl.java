package org.org.eclipse.kapua.service.device.management.configuration.internal;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.org.eclipse.kapua.service.device.management.commons.DeviceManagementRequestTopicBuilder;
import org.org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;

public class DeviceConfigurationManagementServiceImpl implements DeviceConfigurationManagementService
{

    @Override
    public DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId, String configurationComponentPid)
        throws KapuaException
    {
        //
        // ArgumentValidator
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newInstance("device-manage", "get", scopeId));

        //
        // Do get
        DeviceConfiguration deviceConfiguration = null;
        try {
            AccountService accountService = locator.getService(AccountService.class);
            Account account = accountService.find(scopeId);

            DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
            Device device = deviceRegistryService.find(scopeId, deviceId);

            String[] resources = null;
            if (configurationComponentPid != null) {
                resources = new String[] { "configuration", configurationComponentPid };
            }
            else {
                resources = new String[] { "configuration" };
            }

            DeviceManagementRequestTopicBuilder requestTopicBuilder = new DeviceManagementRequestTopicBuilder().withAccountName(account.getName())
                                                                                                               .withAssetId(device.getClientId())
                                                                                                               .withAppId("CONF-V1")
                                                                                                               .withMethod("GET")
                                                                                                               .withResources(resources);

        }
        catch (KapuaException e) {

        }

        return deviceConfiguration;
    }

    @Override
    public void put(KapuaId scopeId, KapuaId deviceId, DeviceComponentConfiguration compConfig)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void apply(KapuaId scopeId, KapuaId deviceId, DeviceConfiguration configuration)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void rollback(KapuaId scopeId, KapuaId deviceId, String configurationId)
        throws KapuaException
    {
        // TODO Auto-generated method stub

    }

}
