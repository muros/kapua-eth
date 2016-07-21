/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
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
package org.eclipse.kapua.app.console.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.eclipse.kapua.app.console.config.ConsoleConfig;
import org.eclipse.kapua.app.console.config.ConsoleConfigKeys;
import org.eclipse.kapua.app.console.server.util.EdcExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.GwtBundleInfo;
import org.eclipse.kapua.app.console.shared.model.GwtConfigComponent;
import org.eclipse.kapua.app.console.shared.model.GwtConfigParameter;
import org.eclipse.kapua.app.console.shared.model.GwtConfigParameter.GwtConfigParameterType;
import org.eclipse.kapua.app.console.shared.model.GwtDeploymentPackage;
import org.eclipse.kapua.app.console.shared.model.GwtDevice;
import org.eclipse.kapua.app.console.shared.model.GwtDevice.GwtDeviceCredentialsTight;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceCommandInput;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceCommandOutput;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceCreator;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceEvent;
import org.eclipse.kapua.app.console.shared.model.GwtDeviceQueryPredicates;
import org.eclipse.kapua.app.console.shared.model.GwtGroupedNVPair;
import org.eclipse.kapua.app.console.shared.model.GwtSnapshot;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.service.GwtDeviceService;
import org.eclipse.kapua.app.console.shared.util.KapuaGwtConverter;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.commons.model.query.FieldSortCriteria;
import org.eclipse.kapua.commons.model.query.FieldSortCriteria.SortOrder;
import org.eclipse.kapua.commons.model.query.predicate.AndPredicate;
import org.eclipse.kapua.commons.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.Tad;
import org.eclipse.kapua.model.config.metatype.Ticon;
import org.eclipse.kapua.model.config.metatype.Tocd;
import org.eclipse.kapua.model.config.metatype.Toption;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.predicate.KapuaAndPredicate;
import org.eclipse.kapua.model.query.predicate.KapuaAttributePredicate.Operator;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundle;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleListResult;
import org.eclipse.kapua.service.device.management.bundle.DeviceBundleManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandFactory;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandManagementService;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfigParamPassowrd;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationFactory;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationManagementService;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeployManagementService;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentPackage;
import org.eclipse.kapua.service.device.management.deploy.DeviceDeploymentPackageListResult;
import org.eclipse.kapua.service.device.management.deploy.DevicePackageBundleInfo;
import org.eclipse.kapua.service.device.management.deploy.DevicePackageBundleInfoListResult;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshot;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotListResult;
import org.eclipse.kapua.service.device.management.snapshots.DeviceSnapshotManagementService;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceCredentialsMode;
import org.eclipse.kapua.service.device.registry.DeviceFactory;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DevicePredicates;
import org.eclipse.kapua.service.device.registry.DeviceQuery;
import org.eclipse.kapua.service.device.registry.DeviceRegistryService;
import org.eclipse.kapua.service.device.registry.DeviceStatus;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnection;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionFactory;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionPredicates;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionQuery;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionService;
import org.eclipse.kapua.service.device.registry.connection.DeviceConnectionStatus;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventFactory;
import org.eclipse.kapua.service.device.registry.event.DeviceEventListResult;
import org.eclipse.kapua.service.device.registry.event.DeviceEventPredicates;
import org.eclipse.kapua.service.device.registry.event.DeviceEventQuery;
import org.eclipse.kapua.service.device.registry.event.DeviceEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * 
 * The server side implementation of the Device RPC service.
 * 
 */
public class GwtDeviceServiceImpl extends KapuaRemoteServiceServlet implements GwtDeviceService
{
    private static Logger     logger           = LoggerFactory.getLogger(GwtDeviceServiceImpl.class);

    private static final long serialVersionUID = -1391026997499175151L;

    public long getNumOfDevices(String scopeIdString)
        throws GwtEdcException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        DeviceFactory deviceFactory = locator.getFactory(DeviceFactory.class);
        try {
            DeviceQuery query = deviceFactory.newQuery(KapuaEid.parseShortId(scopeIdString));
            return deviceRegistryService.count(query);
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
            return 0;
        }
    }

    public Map<String, Integer> getNumOfDevicesConnected(String scopeIdString)
        throws GwtEdcException
    {
        KapuaId scopeId = KapuaEid.parseShortId(scopeIdString);

        Map<String, Integer> devicesStatus = new HashMap<String, Integer>();
        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceConnectionService dcs = locator.getService(DeviceConnectionService.class);
        DeviceConnectionFactory deviceConnectionFactory = locator.getFactory(DeviceConnectionFactory.class);
        try {
            DeviceConnectionQuery query = deviceConnectionFactory.newQuery(scopeId);

            query.setPredicate(new AttributePredicate<DeviceConnectionStatus>(DeviceConnectionPredicates.CONNECTION_STATUS, DeviceConnectionStatus.CONNECTED));
            int count = new Long(dcs.count(query)).intValue();
            if (count > 0) {
                devicesStatus.put("connected", count);
            }

            query.setPredicate(new AttributePredicate<DeviceConnectionStatus>(DeviceConnectionPredicates.CONNECTION_STATUS, DeviceConnectionStatus.DISCONNECTED));
            count = new Long(dcs.count(query)).intValue();
            if (count > 0) {
                devicesStatus.put("disconnected", count);
            }

            query.setPredicate(new AttributePredicate<DeviceConnectionStatus>(DeviceConnectionPredicates.CONNECTION_STATUS, DeviceConnectionStatus.MISSING));
            count = new Long(dcs.count(query)).intValue();
            if (count > 0) {
                devicesStatus.put("missing", count);
            }

        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
            return null;
        }
        return devicesStatus;
    }

    public GwtDevice findDevice(String scopeIdString, String clientId)
        throws GwtEdcException
    {
        GwtDevice gwtDevice = null;
        try {
            KapuaId scopeId = KapuaEid.parseShortId(scopeIdString);

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
            Device device = deviceRegistryService.findByClientId(scopeId, clientId);

            gwtDevice = KapuaGwtConverter.convert(device);
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtDevice;
    }

    public ListLoadResult<GwtGroupedNVPair> findDeviceProfile(String scopeIdString, String clientId)
        throws GwtEdcException
    {
        List<GwtGroupedNVPair> pairs = new ArrayList<GwtGroupedNVPair>();
        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceRegistryService drs = locator.getService(DeviceRegistryService.class);
        DeviceConnectionService dcs = locator.getService(DeviceConnectionService.class);
        try {

            KapuaId scopeId = KapuaEid.parseShortId(scopeIdString);

            Device device = drs.findByClientId(scopeId, clientId);

            if (device != null) {
                pairs.add(new GwtGroupedNVPair("devInfo", "devStatus", device.getStatus().toString()));

                DeviceConnection deviceConnection = dcs.findByClientId(scopeId, device.getClientId());
                DeviceConnectionStatus connectionStatus = null;
                if (deviceConnection != null) {
                    connectionStatus = deviceConnection.getStatus();
                }
                else {
                    connectionStatus = DeviceConnectionStatus.DISCONNECTED;
                }

                pairs.add(new GwtGroupedNVPair("devInfo", "devConnectionStatus", connectionStatus.toString()));
                pairs.add(new GwtGroupedNVPair("devInfo", "devClientId", device.getClientId()));
                pairs.add(new GwtGroupedNVPair("devInfo", "devDisplayName", device.getDisplayName()));
                String lastEventType = device.getLastEventType() != null ? device.getLastEventType().toString() : "";
                pairs.add(new GwtGroupedNVPair("devInfo", "devLastEventType", lastEventType));
                if (device.getLastEventOn() != null) {
                    pairs.add(new GwtGroupedNVPair("devInfo", "devLastEventOn", String.valueOf(device.getLastEventOn().getTime())));
                }
                else {
                    pairs.add(new GwtGroupedNVPair("devInfo", "devLastEventOn", null));
                }

                if (device.getPreferredUserId() != null) {
                    pairs.add(new GwtGroupedNVPair("devInfo", "devLastUserUsed", device.getPreferredUserId().getShortId()));
                }

                pairs.add(new GwtGroupedNVPair("devInfo", "devApps", device.getApplicationIdentifiers()));
                pairs.add(new GwtGroupedNVPair("devInfo", "devAccEnc", device.getAcceptEncoding()));

                pairs.add(new GwtGroupedNVPair("devAttributesInfo", "devCustomAttribute1", device.getCustomAttribute1()));
                pairs.add(new GwtGroupedNVPair("devAttributesInfo", "devCustomAttribute2", device.getCustomAttribute2()));
                pairs.add(new GwtGroupedNVPair("devAttributesInfo", "devCustomAttribute3", device.getCustomAttribute3()));
                pairs.add(new GwtGroupedNVPair("devAttributesInfo", "devCustomAttribute4", device.getCustomAttribute4()));
                pairs.add(new GwtGroupedNVPair("devAttributesInfo", "devCustomAttribute5", device.getCustomAttribute5()));

                // Credentials tight
                pairs.add(new GwtGroupedNVPair("devSecurity", "devSecurityCredentialsTight", GwtDeviceCredentialsTight.valueOf(device.getCredentialsMode().name()).getLabel()));
                pairs.add(new GwtGroupedNVPair("devSecurity", "devSecurityAllowCredentialsChange", device.getPreferredUserId() == null ? "Yes" : "No"));

                pairs.add(new GwtGroupedNVPair("devHw", "devModelName", device.getModelId()));
                pairs.add(new GwtGroupedNVPair("devHw", "devModelId", device.getModelId()));
                pairs.add(new GwtGroupedNVPair("devHw", "devSerialNumber", device.getSerialNumber()));

                pairs.add(new GwtGroupedNVPair("devSw", "devFirmwareVersion", device.getFirmwareVersion()));
                pairs.add(new GwtGroupedNVPair("devSw", "devBiosVersion", device.getBiosVersion()));
                pairs.add(new GwtGroupedNVPair("devSw", "devOsVersion", device.getOsVersion()));

                pairs.add(new GwtGroupedNVPair("devJava", "devJvmVersion", device.getJvmVersion()));

                pairs.add(new GwtGroupedNVPair("netInfo", "netConnIp", deviceConnection.getClientIp()));

                pairs.add(new GwtGroupedNVPair("gpsInfo", "gpsLat", String.valueOf(device.getGpsLatitude())));
                pairs.add(new GwtGroupedNVPair("gpsInfo", "gpsLong", String.valueOf(device.getGpsLongitude())));

                pairs.add(new GwtGroupedNVPair("modemInfo", "modemImei", device.getImei()));
                pairs.add(new GwtGroupedNVPair("modemInfo", "modemImsi", device.getImsi()));
                pairs.add(new GwtGroupedNVPair("modemInfo", "modemIccid", device.getIccid()));
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return new BaseListLoadResult<GwtGroupedNVPair>(pairs);
    }

    public PagingLoadResult<GwtDevice> findDevices(PagingLoadConfig loadConfig,
                                                   String scopeIdString,
                                                   GwtDeviceQueryPredicates predicates)
        throws GwtEdcException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        DeviceFactory deviceFactory = locator.getFactory(DeviceFactory.class);

        List<GwtDevice> gwtDevices = new ArrayList<GwtDevice>();
        BasePagingLoadResult<GwtDevice> gwtResults;
        try {
            BasePagingLoadConfig bplc = (BasePagingLoadConfig) loadConfig;
            DeviceQuery deviceQuery = deviceFactory.newQuery(KapuaEid.parseShortId(scopeIdString));
            deviceQuery.setLimit(bplc.getLimit() + 1);
            deviceQuery.setOffset(bplc.getOffset());

            AndPredicate andPred = new AndPredicate();

            if (predicates.getClientId() != null) {
                andPred = andPred.and(new AttributePredicate<String>(DevicePredicates.CLIENT_ID, predicates.getUnescapedClientId(), Operator.STARTS_WITH));
            }
            if (predicates.getDisplayName() != null) {
                andPred = andPred.and(new AttributePredicate<String>(DevicePredicates.DISPLAY_NAME, predicates.getUnescapedDisplayName(), Operator.STARTS_WITH));
            }
            if (predicates.getSerialNumber() != null) {
                andPred = andPred.and(new AttributePredicate<String>(DevicePredicates.SERIAL_NUMBER, predicates.getUnescapedSerialNumber()));
            }
            if (predicates.getDeviceStatus() != null) {
                andPred = andPred.and(new AttributePredicate<DeviceStatus>(DevicePredicates.STATUS, DeviceStatus.valueOf(predicates.getDeviceStatus())));
            }

            if (predicates.getSortAttribute() != null) {
                SortOrder sortOrder = SortOrder.ASCENDING;
                if (predicates.getSortOrder().equals(SortOrder.DESCENDING.name())) {
                    sortOrder = SortOrder.DESCENDING;
                }
                if (predicates.getSortAttribute().equals(GwtDeviceQueryPredicates.GwtSortAttribute.CLIENT_ID.name())) {
                    deviceQuery.setSortCriteria(new FieldSortCriteria(DevicePredicates.CLIENT_ID, sortOrder));
                }
                else if (predicates.getSortAttribute().equals(GwtDeviceQueryPredicates.GwtSortAttribute.DISPLAY_NAME.name())) {
                    deviceQuery.setSortCriteria(new FieldSortCriteria(DevicePredicates.DISPLAY_NAME, sortOrder));
                }
                else if (predicates.getSortAttribute().equals(GwtDeviceQueryPredicates.GwtSortAttribute.LAST_EVENT_ON.name())) {
                    deviceQuery.setSortCriteria(new FieldSortCriteria(DevicePredicates.LAST_EVENT_ON, sortOrder));
                }
            }
            else {
                deviceQuery.setSortCriteria(new FieldSortCriteria(DevicePredicates.CLIENT_ID, SortOrder.ASCENDING));
            }

            deviceQuery.setPredicate(andPred);

            DeviceListResult devices = deviceRegistryService.query(deviceQuery);

            DeviceConnectionService deviceConnectionService = locator.getService(DeviceConnectionService.class);
            DeviceEventService deviceEventService = locator.getService(DeviceEventService.class);
            DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);

            DeviceEventQuery eventQuery = deviceEventFactory.newQuery(deviceQuery.getScopeId());
            eventQuery.setLimit(1);
            eventQuery.setSortCriteria(new FieldSortCriteria(DeviceEventPredicates.RECEIVED_ON, SortOrder.DESCENDING));

            for (Device d : devices) {
                DeviceConnection deviceConnection = deviceConnectionService.findByClientId(d.getScopeId(),
                                                                                           d.getClientId());

                // Connection info
                GwtDevice gwtDevice = KapuaGwtConverter.convert(d);
                gwtDevice.setConnectionIp(deviceConnection.getClientIp());
                gwtDevice.setGwtDeviceConnectionStatus(deviceConnection.getStatus().name());
                gwtDevice.setLastEventOn(deviceConnection.getModifiedOn());

                // Event infos
                eventQuery.setPredicate(new AttributePredicate<KapuaId>(DeviceEventPredicates.DEVICE_ID, d.getId()));
                DeviceEventListResult events = deviceEventService.query(eventQuery);

                if (!events.isEmpty()) {
                    DeviceEvent lastEvent = events.get(0);

                    gwtDevice.setLastEventType(lastEvent.getEventType());
                    gwtDevice.setLastEventOn(lastEvent.getReceivedOn());
                }

                gwtDevices.add(gwtDevice);
            }
        }
        catch (Throwable t) {

            EdcExceptionHandler.handle(t);
        }

        gwtResults = new BasePagingLoadResult<GwtDevice>(gwtDevices);
        gwtResults.setOffset(loadConfig.getOffset());

        // FIXME: precision loss if number of devices > Integer.MAX_VALUE
        if (gwtDevices.size() == loadConfig.getLimit() + 1) {
            gwtResults.setTotalLength(loadConfig.getOffset() + gwtDevices.size());
        }
        else {
            gwtResults.setTotalLength(loadConfig.getOffset() + gwtDevices.size());
        }

        return gwtResults;
    }

    public GwtDevice createDevice(GwtXSRFToken xsrfToken, GwtDeviceCreator gwtDeviceCreator)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        DeviceFactory deviceFactory = locator.getFactory(DeviceFactory.class);
        GwtDevice gwtDevice = null;

        try {
            KapuaId scopeId = KapuaEid.parseShortId(gwtDeviceCreator.getScopeId());

            DeviceCreator deviceCreator = deviceFactory.newCreator(scopeId,
                                                                   gwtDeviceCreator.getClientId());
            deviceCreator.setDisplayName(gwtDeviceCreator.getDisplayName());

            deviceCreator.setCredentialsMode(DeviceCredentialsMode.valueOf(gwtDeviceCreator.getGwtCredentialsTight().name()));

            deviceCreator.setCustomAttribute1(gwtDeviceCreator.getCustomAttribute1());
            deviceCreator.setCustomAttribute2(gwtDeviceCreator.getCustomAttribute2());
            deviceCreator.setCustomAttribute3(gwtDeviceCreator.getCustomAttribute3());
            deviceCreator.setCustomAttribute4(gwtDeviceCreator.getCustomAttribute4());
            deviceCreator.setCustomAttribute5(gwtDeviceCreator.getCustomAttribute5());

            Device device = deviceRegistryService.create(deviceCreator);

            gwtDevice = KapuaGwtConverter.convert(device);
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }

        return gwtDevice;
    }

    public GwtDevice updateAttributes(GwtXSRFToken xsrfToken, GwtDevice gwtDevice)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceRegistryService deviceRegistryService = locator.getService(DeviceRegistryService.class);
        Device device = null;
        GwtDevice gwtDeviceUpdated = null;

        try {
            //
            // Find original device
            KapuaId scopeId = KapuaEid.parseShortId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(gwtDevice.getId());
            device = deviceRegistryService.find(scopeId, deviceId);

            //
            // Updated values
            // Gerenal info
            device.setDisplayName(gwtDevice.getUnescapedDisplayName());
            device.setStatus(DeviceStatus.valueOf(gwtDevice.getGwtDeviceStatus()));

            // Security Stuff
            device.setCredentialsMode(DeviceCredentialsMode.valueOf(gwtDevice.getCredentialsTight()));
            KapuaId deviceUserId = KapuaEid.parseShortId(gwtDevice.getDeviceUserId());
            device.setPreferredUserId(deviceUserId);

            // Custom attributes
            device.setCustomAttribute1(gwtDevice.getUnescapedCustomAttribute1());
            device.setCustomAttribute2(gwtDevice.getUnescapedCustomAttribute2());
            device.setCustomAttribute3(gwtDevice.getUnescapedCustomAttribute3());
            device.setCustomAttribute4(gwtDevice.getUnescapedCustomAttribute4());
            device.setCustomAttribute5(gwtDevice.getUnescapedCustomAttribute5());

            device.setOptlock(gwtDevice.getOptlock());

            // Do the update
            device = deviceRegistryService.update(device);

            // Convert to gwt object
            gwtDeviceUpdated = KapuaGwtConverter.convert(device);

        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtDeviceUpdated;
    }

    public void deleteDevice(GwtXSRFToken xsrfToken, String scopeIdString, String clientId)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaId scopeId = KapuaEid.parseShortId(scopeIdString);

            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceRegistryService drs = locator.getService(DeviceRegistryService.class);
            Device d = drs.findByClientId(scopeId, clientId);
            drs.delete(d);
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    public PagingLoadResult<GwtDeviceEvent> findDeviceEvents(PagingLoadConfig loadConfig,
                                                             GwtDevice gwtDevice,
                                                             Date startDate,
                                                             Date endDate)
        throws GwtEdcException
    {
        ArrayList<GwtDeviceEvent> gwtDeviceEvents = new ArrayList<GwtDeviceEvent>();
        BasePagingLoadResult<GwtDeviceEvent> gwtResults = null;

        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceEventService des = locator.getService(DeviceEventService.class);
        DeviceEventFactory deviceEventFactory = locator.getFactory(DeviceEventFactory.class);

        try {

            // prepare the query
            BasePagingLoadConfig bplc = (BasePagingLoadConfig) loadConfig;
            DeviceEventQuery query = deviceEventFactory.newQuery(KapuaEid.parseShortId(gwtDevice.getScopeId()));

            KapuaAndPredicate andPredicate = new AndPredicate();

            andPredicate.and(new AttributePredicate<KapuaId>(DeviceEventPredicates.DEVICE_ID, KapuaEid.parseShortId(gwtDevice.getId())));
            // .and(new AttributePredicate<Date>(DeviceEventPredicates.RECEIVED_ON, startDate, Operator.GREATER_THAN));
            // .and(new AttributePredicate<Date>(DeviceEventPredicates.RECEIVED_ON, startDate, Operator.LESS_THAN));

            query.setPredicate(andPredicate);
            query.setOffset(bplc.getOffset());
            query.setLimit(bplc.getLimit());

            // query execute
            DeviceEventListResult deviceEvents = des.query(query);

            // prepare results
            for (DeviceEvent deviceEvent : deviceEvents) {
                gwtDeviceEvents.add(KapuaGwtConverter.convert(deviceEvent));
            }
            gwtResults = new BasePagingLoadResult<GwtDeviceEvent>(gwtDeviceEvents);
            gwtResults.setOffset(loadConfig.getOffset());
            gwtResults.setTotalLength((int) des.count(query));

        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtResults;
    }

    public ListLoadResult<GwtGroupedNVPair> findBundles(GwtDevice device)
        throws GwtEdcException
    {
        List<GwtGroupedNVPair> pairs = new ArrayList<GwtGroupedNVPair>();

        try {

            // get the configuration
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceBundleManagementService deviceBundleManagementService = locator.getService(DeviceBundleManagementService.class);

            DeviceBundleListResult bundles = null;
            KapuaId scopeId = KapuaEid.parseShortId(device.getScopeId());
            KapuaId id = KapuaEid.parseShortId(device.getId());
            bundles = deviceBundleManagementService.get(scopeId, id);

            for (DeviceBundle bundle : bundles) {

                GwtGroupedNVPair pair = new GwtGroupedNVPair();
                pair.setId(String.valueOf(bundle.getId()));
                pair.setName(bundle.getName());
                pair.setStatus(toStateString(bundle));
                pair.setVersion(bundle.getVersion());

                pairs.add(pair);
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtGroupedNVPair>(pairs);
    }

    public void startBundle(GwtXSRFToken xsrfToken, GwtDevice device, GwtGroupedNVPair pair)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceBundleManagementService deviceBundleManagementService = locator.getService(DeviceBundleManagementService.class);

            KapuaId scopeId = KapuaEid.parseShortId(device.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(device.getId());
            deviceBundleManagementService.start(scopeId,
                                                deviceId,
                                                String.valueOf(pair.getId()));
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    public void stopBundle(GwtXSRFToken xsrfToken, GwtDevice device, GwtGroupedNVPair pair)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceBundleManagementService deviceBundleManagementService = locator.getService(DeviceBundleManagementService.class);

            KapuaId scopeId = KapuaEid.parseShortId(device.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(device.getId());
            deviceBundleManagementService.stop(scopeId,
                                               deviceId,
                                               String.valueOf(pair.getId()));
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    public List<GwtConfigComponent> findDeviceConfigurations(GwtDevice device)
        throws GwtEdcException
    {
        List<GwtConfigComponent> gwtConfigs = new ArrayList<GwtConfigComponent>();
        try {

            // get the configuration
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceConfigurationManagementService deviceService = locator.getService(DeviceConfigurationManagementService.class);

            KapuaId scopeId = KapuaEid.parseShortId(device.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(device.getId());
            DeviceConfiguration deviceConfigurations = deviceService.get(scopeId,
                                                                         deviceId,
                                                                         null);
            if (deviceConfigurations != null) {

                // sort the list alphabetically by service name
                List<DeviceComponentConfiguration> configs = deviceConfigurations.getComponentConfigurations();
                Collections.sort(configs, new Comparator<DeviceComponentConfiguration>() {
                    public int compare(DeviceComponentConfiguration arg0,
                                       DeviceComponentConfiguration arg1)
                    {
                        String name0 = arg0.getComponentId().substring(arg0.getComponentId().lastIndexOf("."));
                        String name1 = arg1.getComponentId().substring(arg1.getComponentId().lastIndexOf("."));
                        return name0.compareTo(name1);
                    }
                });

                // prepare results
                ConsoleConfig consoleConfig = ConsoleConfig.getInstance();
                List<String> serviceIgnore = consoleConfig.getList(String.class, ConsoleConfigKeys.DEVICE_CONFIGURATION_SERVICE_IGNORE);
                for (DeviceComponentConfiguration config : deviceConfigurations.getComponentConfigurations()) {

                    // ignore items we want to hide
                    if (serviceIgnore != null && serviceIgnore.contains(config.getComponentId())) {
                        continue;
                    }

                    Tocd ocd = config.getDefinition();
                    if (ocd != null) {
                        GwtConfigComponent gwtConfig = new GwtConfigComponent();
                        gwtConfig.setComponentId(config.getComponentId());
                        gwtConfig.setComponentName(ocd.getName());
                        gwtConfig.setComponentDescription(ocd.getDescription());
                        if (ocd.getIcon() != null && ocd.getIcon().size() > 0) {
                            Ticon icon = ocd.getIcon().get(0);

                            checkIconResource(icon);

                            gwtConfig.setComponentIcon(icon.getResource());
                        }

                        List<GwtConfigParameter> gwtParams = new ArrayList<GwtConfigParameter>();
                        gwtConfig.setParameters(gwtParams);
                        for (Tad ad : ocd.getAD()) {
                            if (ad != null) {
                                GwtConfigParameter gwtParam = new GwtConfigParameter();
                                gwtParam.setId(ad.getId());
                                gwtParam.setName(ad.getName());
                                gwtParam.setDescription(ad.getDescription());
                                gwtParam.setType(GwtConfigParameterType.valueOf(ad.getType().name()));
                                gwtParam.setRequired(ad.isRequired());
                                gwtParam.setCardinality(ad.getCardinality());
                                if (ad.getOption() != null && ad.getOption().size() > 0) {
                                    Map<String, String> options = new HashMap<String, String>();
                                    for (Toption option : ad.getOption()) {
                                        options.put(option.getLabel(), option.getValue());
                                    }
                                    gwtParam.setOptions(options);
                                }
                                gwtParam.setMin(ad.getMin());
                                gwtParam.setMax(ad.getMax());

                                if (config.getProperties() != null) {

                                    // handle the value based on the cardinality of the attribute
                                    int cardinality = ad.getCardinality();
                                    Object value = config.getProperties().get(ad.getId());
                                    if (value != null) {

                                        if (cardinality == 0 || cardinality == 1 || cardinality == -1) {
                                            gwtParam.setValue(value.toString());
                                        }
                                        else {
                                            // this could be an array value
                                            if (value instanceof Object[]) {
                                                Object[] objValues = (Object[]) value;
                                                List<String> strValues = new ArrayList<String>();
                                                for (Object v : objValues) {
                                                    if (v != null) {
                                                        strValues.add(v.toString());
                                                    }
                                                }
                                                gwtParam.setValues(strValues.toArray(new String[] {}));
                                            }
                                        }
                                    }
                                    gwtParams.add(gwtParam);
                                }
                            }
                        }
                        gwtConfigs.add(gwtConfig);
                    }
                }
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtConfigs;
    }

    public void updateComponentConfiguration(GwtXSRFToken xsrfToken,
                                             GwtDevice gwtDevice,
                                             GwtConfigComponent gwtCompConfig)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceConfigurationManagementService deviceConfigurationManagementService = locator.getService(DeviceConfigurationManagementService.class);
        DeviceConfigurationFactory deviceConfigurationManagementFactory = locator.getFactory(DeviceConfigurationFactory.class);

        // set name and properties
        DeviceComponentConfiguration compConfig = deviceConfigurationManagementFactory.newComponentConfigurationInstance(gwtCompConfig.getUnescapedComponentId());
        compConfig.setComponentName(gwtCompConfig.getUnescapedComponentName());

        Map<String, Object> compProps = new HashMap<String, Object>();
        for (GwtConfigParameter gwtConfigParam : gwtCompConfig.getParameters()) {

            Object objValue = null;
            int cardinality = gwtConfigParam.getCardinality();
            if (cardinality == 0 || cardinality == 1 || cardinality == -1) {

                String strValue = gwtConfigParam.getValue();
                objValue = getObjectValue(gwtConfigParam, strValue);
            }
            else {

                String[] strValues = gwtConfigParam.getValues();
                objValue = getObjectValue(gwtConfigParam, strValues);
            }
            compProps.put(gwtConfigParam.getName(), objValue);
        }
        compConfig.setProperties(compProps);

        // execute the update
        try {
            KapuaId scopeId = KapuaEid.parseShortId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(gwtDevice.getId());
            deviceConfigurationManagementService.put(scopeId,
                                                     deviceId,
                                                     compConfig);

            // //
            // // Add an additional delay after the configuration update
            // // to give the time to the device to apply the received
            // // configuration
            // EdcConfig edcConfig = EdcConfig.getInstance();
            // int delay = edcConfig.getConsoleDeviceUpdateConfDelay();
            // if (delay > 0) {
            // Thread.sleep(delay);
            // }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    @Override
    public List<GwtDeploymentPackage> findDeviceDeploymentPackages(GwtDevice gwtDevice)
        throws GwtEdcException
    {

        List<GwtDeploymentPackage> gwtPkgs = new ArrayList<GwtDeploymentPackage>();
        try {

            // get the configuration
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceDeployManagementService deviceService = locator.getService(DeviceDeployManagementService.class);

            KapuaId scopeId = KapuaEid.parseShortId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(gwtDevice.getId());
            DeviceDeploymentPackageListResult deploymentPackages = deviceService.get(scopeId, deviceId);

            for (DeviceDeploymentPackage deploymentPackage : deploymentPackages) {
                GwtDeploymentPackage gwtPkg = new GwtDeploymentPackage();
                gwtPkg.setName(deploymentPackage.getName());
                gwtPkg.setVersion(deploymentPackage.getVersion());

                DevicePackageBundleInfoListResult devicePackageBundleInfos = deploymentPackage.getBundleInfos();

                if (devicePackageBundleInfos != null) {
                    // List<GwtBundleInfo> gwtBundleInfos = gwtPkg.getBundleInfos();
                    List<GwtBundleInfo> gwtBundleInfos = new ArrayList<GwtBundleInfo>();
                    for (DevicePackageBundleInfo bundleInfo : devicePackageBundleInfos) {

                        GwtBundleInfo gwtBundleInfo = new GwtBundleInfo();
                        gwtBundleInfo.setName(bundleInfo.getName());
                        gwtBundleInfo.setVersion(bundleInfo.getVersion());

                        gwtBundleInfos.add(gwtBundleInfo);
                    }
                    gwtPkg.setBundleInfos(gwtBundleInfos);
                }
                gwtPkgs.add(gwtPkg);
            }

        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtPkgs;
    }

    @Override
    public void uninstallDeploymentPackage(GwtXSRFToken xsrfToken, GwtDevice gwtDevice, String packageName)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        KapuaLocator locator = KapuaLocator.getInstance();
        DeviceDeployManagementService deviceDeployMangamentService = locator.getService(DeviceDeployManagementService.class);

        try {
            KapuaId scopeId = KapuaEid.parseShortId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(gwtDevice.getId());
            deviceDeployMangamentService.uninstall(scopeId,
                                                   deviceId,
                                                   packageName);
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    @Override
    public GwtDeviceCommandOutput executeCommand(GwtXSRFToken xsrfToken, GwtDevice gwtDevice, GwtDeviceCommandInput gwtCommandInput)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        GwtDeviceCommandOutput gwtCommandOutput = new GwtDeviceCommandOutput();
        try {

            // execute the command
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceCommandManagementService deviceCommandManagementService = locator.getService(DeviceCommandManagementService.class);
            DeviceCommandFactory deviceCommandFactory = locator.getFactory(DeviceCommandFactory.class);

            StringTokenizer st = new StringTokenizer(gwtCommandInput.getCommand());
            int count = st.countTokens();

            String command = count > 0 ? st.nextToken() : null;
            String[] args = count > 1 ? new String[count - 1] : null;
            int i = 0;
            while (st.hasMoreTokens()) {
                args[i++] = st.nextToken();
            }

            DeviceCommandInput commandInput = deviceCommandFactory.newInputInstance();
            // commandInput.setArguments(gwtCommandInput.getArguments());
            commandInput.setArguments(args);
            // commandInput.setCommand(gwtCommandInput.getCommand());
            commandInput.setCommand(command);
            commandInput.setEnvironment(gwtCommandInput.getEnvironment());
            commandInput.setRunAsynch(gwtCommandInput.isRunAsynch() != null ? gwtCommandInput.isRunAsynch().booleanValue() : false);
            commandInput.setStdin(gwtCommandInput.getStdin());
            commandInput.setTimeout(gwtCommandInput.getTimeout() != null ? gwtCommandInput.getTimeout().intValue() : 0);
            commandInput.setWorkingDir(gwtCommandInput.getWorkingDir());
            commandInput.setBody(gwtCommandInput.getZipBytes());

            KapuaId scopeId = KapuaEid.parseShortId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(gwtDevice.getId());
            DeviceCommandOutput commandOutput = deviceCommandManagementService.exec(scopeId,
                                                                                    deviceId,
                                                                                    commandInput);

            if (commandOutput.getExceptionMessage() != null) {
                gwtCommandOutput.setExceptionMessage(commandOutput.getExceptionMessage().replace("\n", "<br>"));
            }
            if (commandOutput.getExceptionStack() != null) {
                gwtCommandOutput.setExceptionStack(commandOutput.getExceptionStack().replace("\n", "<br>"));
            }
            gwtCommandOutput.setExitCode(commandOutput.getExitCode());
            if (commandOutput.getStderr() != null) {
                gwtCommandOutput.setStderr(commandOutput.getStderr().replace("\n", "<br>"));
            }
            gwtCommandOutput.setStdout(commandOutput.getStdout());
            gwtCommandOutput.setTimedout(commandOutput.hasTimedout());
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }

        return gwtCommandOutput;
    }

    public ListLoadResult<GwtSnapshot> findDeviceSnapshots(GwtDevice gwtDevice)
        throws GwtEdcException
    {
        List<GwtSnapshot> snapshots = new ArrayList<GwtSnapshot>();
        try {

            // execute the command
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceSnapshotManagementService deviceSnapshotManagementService = locator.getService(DeviceSnapshotManagementService.class);

            KapuaId scopeId = KapuaEid.parseShortId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(gwtDevice.getId());
            DeviceSnapshotListResult snapshotIds = deviceSnapshotManagementService.get(scopeId,
                                                                                       deviceId);
            // sort them by most recent first

            // sort the list alphabetically by service name
            Collections.sort(snapshotIds, new Comparator<DeviceSnapshot>() {
                public int compare(DeviceSnapshot arg0,
                                   DeviceSnapshot arg1)
                {
                    Long snapshotId0 = arg0.getId();
                    Long snapshotId1 = arg1.getId();
                    return snapshotId0.compareTo(snapshotId1);
                }
            });

            for (DeviceSnapshot snapshot : snapshotIds) {
                GwtSnapshot gwtSnapshot = new GwtSnapshot();
                gwtSnapshot.setCreatedOn(new Date(snapshot.getId()));
                snapshots.add(gwtSnapshot);
            }

        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }

        return new BaseListLoadResult<GwtSnapshot>(snapshots);
    }

    public void rollbackDeviceSnapshot(GwtXSRFToken xsrfToken, GwtDevice gwtDevice, GwtSnapshot snapshot)
        throws GwtEdcException
    {
        //
        // Checking validity of the given XSRF Token
        checkXSRFToken(xsrfToken);

        try {
            KapuaLocator locator = KapuaLocator.getInstance();
            DeviceSnapshotManagementService deviceService = locator.getService(DeviceSnapshotManagementService.class);

            KapuaId scopeId = KapuaEid.parseShortId(gwtDevice.getScopeId());
            KapuaId deviceId = KapuaEid.parseShortId(gwtDevice.getId());
            deviceService.rollback(scopeId,
                                   deviceId,
                                   String.valueOf(snapshot.getSnapshotId()));

            // //
            // // Add an additional delay after the configuration update
            // // to give the time to the device to apply the received
            // // configuration
            // EdcConfig edcConfig = EdcConfig.getInstance();
            // int delay = edcConfig.getConsoleDeviceUpdateConfDelay();
            // if (delay > 0) {
            // Thread.sleep(delay);
            // }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
    }

    private String formatUptime(long uptime)
    {
        int days = (int) TimeUnit.MILLISECONDS.toDays(uptime);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime) - (TimeUnit.MILLISECONDS.toHours(uptime) * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime) - (TimeUnit.MILLISECONDS.toMinutes(uptime) * 60);

        StringBuilder sb = new StringBuilder();
        sb.append(days)
          .append(" days ")
          .append(hours)
          .append(":")
          .append(minutes)
          .append(":")
          .append(seconds)
          .append(" hms");

        return sb.toString();
    }

    private String toStateString(DeviceBundle bundle)
    {
        String state = bundle.getState();
        if (state.equals("INSTALLED")) {
            return "bndInstalled";
        }
        else if (state.equals("RESOLVED")) {
            return "bndResolved";
        }
        else if (state.equals("STARTING")) {
            return "bndStarting";
        }
        else if (state.equals("ACTIVE")) {
            return "bndActive";
        }
        else if (state.equals("STOPPING")) {
            return "bndStopping";
        }
        else if (state.equals("UNINSTALLED")) {
            return "bndUninstalled";
        }
        else {
            return "bndUnknown";
        }
    }

    private String fromStateString(GwtGroupedNVPair pair)
    {
        String state = pair.getStatus();
        if (state.equals("bndInstalled")) {
            return "INSTALLED";
        }
        else if (state.equals("bndResolved")) {
            return "RESOLVED";
        }
        else if (state.equals("bndStarting")) {
            return "STARTING";
        }
        else if (state.equals("bndActive")) {
            return "ACTIVE";
        }
        else if (state.equals("bndStopping")) {
            return "STOPPING";
        }
        else if (state.equals("bndUninstalled")) {
            return "UNINSTALLED";
        }
        else {
            return "UNKNOWN";
        }
    }

    private Object getObjectValue(GwtConfigParameter gwtConfigParam, String strValue)
    {
        Object objValue = null;
        if (strValue != null) {
            GwtConfigParameterType gwtType = gwtConfigParam.getType();
            switch (gwtType) {
                case LONG:
                    objValue = Long.parseLong(strValue);
                    break;
                case DOUBLE:
                    objValue = Double.parseDouble(strValue);
                    break;
                case FLOAT:
                    objValue = Float.parseFloat(strValue);
                    break;
                case INTEGER:
                    objValue = Integer.parseInt(strValue);
                    break;
                case SHORT:
                    objValue = Short.parseShort(strValue);
                    break;
                case BYTE:
                    objValue = Byte.parseByte(strValue);
                    break;
                case BOOLEAN:
                    objValue = Boolean.parseBoolean(strValue);
                    break;
                case PASSWORD:
                    objValue = new DeviceComponentConfigParamPassowrd(strValue);
                    break;
                case CHAR:
                    objValue = Character.valueOf(strValue.charAt(0));
                    break;
                case STRING:
                    objValue = strValue;
                    break;
            }
        }
        return objValue;
    }

    private Object[] getObjectValue(GwtConfigParameter gwtConfigParam, String[] defaultValues)
    {
        List<Object> values = new ArrayList<Object>();
        GwtConfigParameterType type = gwtConfigParam.getType();
        switch (type) {
            case BOOLEAN:
                for (String value : defaultValues) {
                    values.add(Boolean.valueOf(value));
                }
                return values.toArray(new Boolean[] {});

            case BYTE:
                for (String value : defaultValues) {
                    values.add(Byte.valueOf(value));
                }
                return values.toArray(new Byte[] {});

            case CHAR:
                for (String value : defaultValues) {
                    values.add(new Character(value.charAt(0)));
                }
                return values.toArray(new Character[] {});

            case DOUBLE:
                for (String value : defaultValues) {
                    values.add(Double.valueOf(value));
                }
                return values.toArray(new Double[] {});

            case FLOAT:
                for (String value : defaultValues) {
                    values.add(Float.valueOf(value));
                }
                return values.toArray(new Float[] {});

            case INTEGER:
                for (String value : defaultValues) {
                    values.add(Integer.valueOf(value));
                }
                return values.toArray(new Integer[] {});

            case LONG:
                for (String value : defaultValues) {
                    values.add(Long.valueOf(value));
                }
                return values.toArray(new Long[] {});

            case SHORT:
                for (String value : defaultValues) {
                    values.add(Short.valueOf(value));
                }
                return values.toArray(new Short[] {});

            case PASSWORD:
                for (String value : defaultValues) {
                    values.add(new DeviceComponentConfigParamPassowrd(value));
                }
                return values.toArray(new DeviceComponentConfigParamPassowrd[] {});

            case STRING:
                return defaultValues;
        }

        return null;
    }

    /**
     * Checks the source of the icon.
     * The component config icon can be one of the well known icon (i.e. MqttDataTransport icon)
     * as well as an icon loaded from external source with an HTTP link.
     * 
     * We need to filter HTTP link to protect the console page and also to have content always served from
     * EC console. Otherwise browsers can alert the user that content is served from domain different from
     * *.everyware-cloud.com and over insicure connection.
     * 
     * To avoid this we will download the image locally on the server temporary directory and give back the page
     * a token URL to get the file.
     * 
     * @param icon The icon from the OCD of the component configuration.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ImageReadException
     */
    private void checkIconResource(Ticon icon)
    {
        ConsoleConfig config = ConsoleConfig.getInstance();

        String iconResource = icon.getResource();

        //
        // Check if the resource is an HTTP URL or not
        if (iconResource != null &&
            (iconResource.toLowerCase().startsWith("http://") ||
             iconResource.toLowerCase().startsWith("https://"))) {
            File tmpFile = null;

            try {
                logger.info("Got configuration component icon from URL: {}", iconResource);

                //
                // Tmp file name creation
                String systemTmpDir = System.getProperty("java.io.tmpdir");
                String iconResourcesTmpDir = config.getString(ConsoleConfigKeys.DEVICE_CONFIGURATION_ICON_FOLDER);
                String tmpFileName = Base64.encodeBase64String(MessageDigest.getInstance("MD5").digest(iconResource.getBytes("UTF-8")));

                // Conversions needed got security reasons!
                // On the file servlet we use the regex [0-9A-Za-z]{1,} to validate the given file id.
                // This validation prevents the caller of the file servlet to try to move out of the directory where the icons are stored.
                tmpFileName = tmpFileName.replaceAll("/", "a");
                tmpFileName = tmpFileName.replaceAll("\\+", "m");
                tmpFileName = tmpFileName.replaceAll("=", "z");

                //
                // Tmp dir check and creation
                StringBuilder tmpDirPathSb = new StringBuilder().append(systemTmpDir);
                if (!systemTmpDir.endsWith("/")) {
                    tmpDirPathSb.append("/");
                }
                tmpDirPathSb.append(iconResourcesTmpDir);

                File tmpDir = new File(tmpDirPathSb.toString());
                if (!tmpDir.exists()) {
                    logger.info("Creating tmp dir on path: {}", tmpDir.toString());
                    tmpDir.mkdir();
                }

                //
                // Tmp file check and creation
                tmpDirPathSb.append("/")
                            .append(tmpFileName);
                tmpFile = new File(tmpDirPathSb.toString());

                // Check date of modification to avoid caching forever
                if (tmpFile.exists()) {
                    long lastModifiedDate = tmpFile.lastModified();

                    long maxCacheTime = config.getLong(ConsoleConfigKeys.DEVICE_CONFIGURATION_ICON_CACHE_TIME);

                    if (System.currentTimeMillis() - lastModifiedDate > maxCacheTime) {
                        logger.info("Deleting old cached file: {}", tmpFile.toString());
                        tmpFile.delete();
                    }
                }

                // If file is not cached, download it.
                if (!tmpFile.exists()) {
                    // Url connection
                    URL iconUrl = new URL(iconResource);
                    URLConnection urlConnection = iconUrl.openConnection();
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.setReadTimeout(2000);

                    // Length check
                    String contentLengthString = urlConnection.getHeaderField("Content-Length");

                    long maxLength = config.getLong(ConsoleConfigKeys.DEVICE_CONFIGURATION_ICON_SIZE_MAX);

                    try {
                        Long contentLength = Long.parseLong(contentLengthString);
                        if (contentLength > maxLength) {
                            logger.warn("Content lenght exceeded ({}/{}) for URL: {}",
                                        new Object[] { contentLength, maxLength, iconResource });
                            throw new IOException("Content-Length reported a length of " + contentLength + " which exceeds the maximum allowed size of " + maxLength);
                        }
                    }
                    catch (NumberFormatException nfe) {
                        logger.warn("Cannot get Content-Length header!");
                    }

                    logger.info("Creating file: {}", tmpFile.toString());
                    tmpFile.createNewFile();

                    // Icon download
                    InputStream is = urlConnection.getInputStream();
                    OutputStream os = new FileOutputStream(tmpFile);
                    byte[] buffer = new byte[4096];
                    try {
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            os.write(buffer, 0, len);

                            maxLength -= len;

                            if (maxLength < 0) {
                                logger.warn("Maximum content lenght exceeded ({}) for URL: {}",
                                            new Object[] { maxLength, iconResource });
                                throw new IOException("Maximum content lenght exceeded (" + maxLength + ") for URL: " + iconResource);
                            }
                        }
                    }
                    finally {
                        os.close();
                    }

                    logger.info("Downloaded file: {}", tmpFile.toString());

                    // Image metadata content checks
                    ImageFormats imgFormat = (ImageFormats) Imaging.guessFormat(tmpFile);

                    switch (imgFormat) {
                        case BMP:
                        case GIF:
                        case JPEG:
                        case PNG:
                        {
                            logger.info("Detected image format: {}", imgFormat.name());
                        }
                            break;
                        case UNKNOWN:
                        {
                            logger.error("Unknown file format for URL: {}", iconResource);
                            throw new IOException("Unknown file format for URL: " + iconResource);
                        }
                        default:
                        {
                            logger.error("Usupported file format ({}) for URL: {}", imgFormat, iconResource);
                            throw new IOException("Unknown file format for URL: {}" + iconResource);
                        }
                    }

                    logger.info("Image validation passed for URL: {}", iconResource);
                }
                else {
                    logger.info("Using cached file: {}", tmpFile.toString());
                }

                //
                // Injecting new URL for the icon resource
                String newResourceURL = new StringBuilder().append("img://console/file/icons?id=")
                                                           .append(tmpFileName)
                                                           .toString();

                logger.info("Injecting configuration component icon: {}", newResourceURL);
                icon.setResource(newResourceURL);
            }
            catch (Exception e) {
                if (tmpFile != null &&
                    tmpFile.exists()) {
                    tmpFile.delete();
                }

                icon.setResource("Default");

                logger.error("Error while checking component configuration icon. Using the default plugin icon.", e);
            }
        }
        //
        // If not, all is fine.
    }
}
