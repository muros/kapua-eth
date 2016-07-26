package org.eclipse.kapua.service.device.management.snapshot.internal;

import java.util.Date;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.authorization.Actions;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.device.management.KapuaMethod;
import org.eclipse.kapua.service.device.management.commons.DeviceManagementDomain;
import org.eclipse.kapua.service.device.management.commons.call.DeviceCallExecutor;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementErrorCodes;
import org.eclipse.kapua.service.device.management.commons.exception.DeviceManagementException;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSetting;
import org.eclipse.kapua.service.device.management.commons.setting.DeviceManagementSettingKey;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.internal.ConfigurationAppProperties;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotListResult;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotManagementService;
import org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotRequestChannel;
import org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotRequestMessage;
import org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotRequestPayload;
import org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotResponseMessage;
import org.org.eclipse.kapua.service.device.management.configuration.snapshot.internal.SnapshotResponsePayload;

public class DeviceSnapshotManagementServiceImpl implements DeviceSnapshotManagementService
{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DeviceSnapshotListResult get(KapuaId scopeId, KapuaId deviceId, Long timeout)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.read, scopeId));

        //
        // Prepare the request
        SnapshotRequestChannel snapshotRequestChannel = new SnapshotRequestChannel();
        snapshotRequestChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        snapshotRequestChannel.setVersion(ConfigurationAppProperties.APP_VERSION);
        snapshotRequestChannel.setMethod(KapuaMethod.READ);

        SnapshotRequestPayload snapshotRequestPayload = new SnapshotRequestPayload();

        SnapshotRequestMessage snapshotRequestMessage = new SnapshotRequestMessage();
        snapshotRequestMessage.setScopeId(scopeId);
        snapshotRequestMessage.setDeviceId(deviceId);
        snapshotRequestMessage.setCapturedOn(new Date());
        snapshotRequestMessage.setPayload(snapshotRequestPayload);
        snapshotRequestMessage.setSemanticChannel(snapshotRequestChannel);

        //
        // Do get
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(snapshotRequestMessage, timeout);
        SnapshotResponseMessage responseMessage = (SnapshotResponseMessage) deviceApplicationCall.send();

        SnapshotResponsePayload responsePayload = responseMessage.getPayload();

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        String body = null;
        try {
            body = new String(responsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION,
                                                e,
                                                responsePayload.getBody());
        }

        DeviceSnapshotListResult deviceSnapshots = null;
        try {
            deviceSnapshots = XmlUtil.unmarshal(body, DeviceSnapshotListResult.class);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION,
                                                e,
                                                body);
        }

        return deviceSnapshots;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DeviceConfiguration get(KapuaId scopeId, KapuaId deviceId, String snapshotId, Long timeout)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notEmptyOrNull(snapshotId, "snapshotId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.read, scopeId));

        //
        // Prepare the request
        SnapshotRequestChannel snapshotRequestChannel = new SnapshotRequestChannel();
        snapshotRequestChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        snapshotRequestChannel.setVersion(ConfigurationAppProperties.APP_VERSION);
        snapshotRequestChannel.setMethod(KapuaMethod.READ);
        snapshotRequestChannel.setSnapshotId(snapshotId);

        SnapshotRequestPayload snapshotRequestPayload = new SnapshotRequestPayload();

        SnapshotRequestMessage snapshotRequestMessage = new SnapshotRequestMessage();
        snapshotRequestMessage.setScopeId(scopeId);
        snapshotRequestMessage.setDeviceId(deviceId);
        snapshotRequestMessage.setCapturedOn(new Date());
        snapshotRequestMessage.setPayload(snapshotRequestPayload);
        snapshotRequestMessage.setSemanticChannel(snapshotRequestChannel);

        //
        // Do get
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(snapshotRequestMessage, timeout);
        SnapshotResponseMessage responseMessage = (SnapshotResponseMessage) deviceApplicationCall.send();

        //
        // Parse the response
        SnapshotResponsePayload responsePayload = responseMessage.getPayload();

        DeviceManagementSetting config = DeviceManagementSetting.getInstance();
        String charEncoding = config.getString(DeviceManagementSettingKey.CHAR_ENCODING);

        String body = null;
        try {
            body = new String(responsePayload.getBody(), charEncoding);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, responsePayload.getBody());

        }

        DeviceConfiguration deviceConfiguration = null;
        try {
            deviceConfiguration = XmlUtil.unmarshal(body, DeviceConfiguration.class);
        }
        catch (Exception e) {
            throw new DeviceManagementException(DeviceManagementErrorCodes.RESPONSE_PARSE_EXCEPTION, e, body);

        }

        return deviceConfiguration;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void rollback(KapuaId scopeId, KapuaId deviceId, String snapshotId, Long timeout)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(deviceId, "deviceId");
        ArgumentValidator.notEmptyOrNull(snapshotId, "snapshotId");

        //
        // Check Access
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(DeviceManagementDomain.DEVICE_MANAGEMENT, Actions.execute, scopeId));

        //
        // Prepare the request
        SnapshotRequestChannel snapshotRequestChannel = new SnapshotRequestChannel();
        snapshotRequestChannel.setAppName(ConfigurationAppProperties.APP_NAME);
        snapshotRequestChannel.setVersion(ConfigurationAppProperties.APP_VERSION);
        snapshotRequestChannel.setMethod(KapuaMethod.EXECUTE);
        snapshotRequestChannel.setSnapshotId(snapshotId);

        SnapshotRequestPayload snapshotRequestPayload = new SnapshotRequestPayload();

        SnapshotRequestMessage snapshotRequestMessage = new SnapshotRequestMessage();
        snapshotRequestMessage.setScopeId(scopeId);
        snapshotRequestMessage.setDeviceId(deviceId);
        snapshotRequestMessage.setCapturedOn(new Date());
        snapshotRequestMessage.setPayload(snapshotRequestPayload);
        snapshotRequestMessage.setSemanticChannel(snapshotRequestChannel);

        //
        // Do exec
        DeviceCallExecutor deviceApplicationCall = new DeviceCallExecutor(snapshotRequestMessage, timeout);
        SnapshotResponseMessage responseMessage = (SnapshotResponseMessage) deviceApplicationCall.send();
    }
}
