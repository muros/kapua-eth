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
package org.eclipse.kapua.app.console.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.account.AccountOld;
import org.eclipse.kapua.service.account.AccountCreatorOld;
import org.eclipse.kapua.service.account.AccountServiceOld;
import org.eclipse.kapua.service.authorization.AuthorizationServiceOLd;
import org.eclipse.kapua.service.authorization.EdcUsername;
import org.eclipse.kapua.service.authorization.LoginSourceType;
import org.eclipse.kapua.service.config.EdcConfig;
import org.eclipse.kapua.service.device.registry.DeviceOld;
import org.eclipse.kapua.service.device.registry.DeviceCreatorOld;
import org.eclipse.kapua.service.device.registry.DeviceRegistryServiceOld;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.eclipse.kapua.service.user.EdcSysAuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.service.sim.SimCard;
import com.eurotech.cloud.service.sim.SimCardCreator;
import com.eurotech.cloud.service.sim.SimCardGroup;
import com.eurotech.cloud.service.sim.SimCardGroupCreator;
import com.eurotech.cloud.service.sim.SimCardGroupType;
import com.eurotech.cloud.service.sim.SimCardService;
import com.eurotech.cloud.service.sim.SimCardStatus;
import com.eurotech.cloud.service.sim.SimProvider;
import com.eurotech.cloud.service.sim.SimProviderCreator;
import com.eurotech.cloud.service.sim.SimProviderService;
import com.eurotech.cloud.service.sim.SyncStatus;
import com.eurotech.cloud.service.sim.inbound.SimProviders;

public class SimCardGroupMigrationTestServlet extends HttpServlet {

    private static final long serialVersionUID = -8576667807368399016L;

    private static Logger     s_logger         = LoggerFactory.getLogger(SimCardGroupMigrationTestServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        EdcConfig config = EdcConfig.getInstance();

        if (!config.getEnabledTestServlets()) {
            response.sendError(404);
        } else {

            try {
                ServiceLocator locator = ServiceLocator.getInstance();
                AuthorizationServiceOLd as = locator.getAuthorizationService();

                EdcSysAuthToken token = as.createEdcSysAuthToken("sim-group-test");

                EdcUsername edcUsername = EdcUsername.parse(token.getUsername());
                edcUsername.setClientId(token.getClientId());

                as.login(edcUsername,
                         token.getPassword(),
                         LoginSourceType.QA_TEST);

                long now = System.currentTimeMillis();

                AccountServiceOld accountService = locator.getAccountService();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(now);
                cal.add(Calendar.MONTH, 3);

                // create the first Account
                AccountOld account = createTestAccount();
                
                SimProviderCreator spc = initializeSpc(now);
                
                
                SimProviderService providerService = locator.getSimProviderService();
                SimProvider provider = providerService.create(spc);

                // create first sim Group
                String groupName = MessageFormat.format("test-group-{0,number,#}", now);
                String externalGroup = MessageFormat.format("test-externalGroup-{0,number,#}", now);
                SimCardGroupCreator scgc = new SimCardGroupCreator(provider.getId(), 
                                                                   account.getId(),
                                                                   groupName, 
                                                                   SimCardGroupType.EXCLUSIVE);
                scgc.setExternalProviderGroupId(externalGroup);
                SimCardGroup scg = providerService.createSimCardGroup(scgc);

                // create the first device
                String prefix = "01";
                DeviceCreatorOld dc = prepareDevice(account.getId(), prefix);
                DeviceRegistryServiceOld drs = locator.getDeviceRegistryService();
                DeviceOld d = drs.create(dc);

                // create sim card
                String imsi = prefix + "imsi";
                String iccid = prefix + "iccid";
                SimCardCreator scc = new SimCardCreator(scg.getId(), iccid, imsi, SimCardStatus.ENABLED, provider.getId());
                scc.setConnectionStatus("1");
                scc.setLatitude(((float) now) / 10000);
                String logInformations = MessageFormat.format("test-logInformationsStatus-{0,number,#}", now);
                scc.setLogInformations(logInformations);
                scc.setLongitude(((float) now) / 10000);
                scc.setSyncOn(new Date(now));
                scc.setSyncStatus(SyncStatus.SYNCED);
                SimCardService simService = locator.getSimCardService();
                SimCard sim = simService.create(scc);

                now = System.currentTimeMillis();

                // create the second Account
                AccountOld account2 = createTestAccount();

                // update the first device
                d = drs.find(d.getAccountId(), d.getId());
                d.setIccid("");
                d.setImsi("");
                drs.update(d);

                // create the second device
                String prefix2 = "02";
                DeviceCreatorOld dc2 = prepareDevice(account2.getId(), prefix2);
                dc2.setIccid(iccid);
                dc2.setImsi(imsi);
                DeviceOld d2 = drs.create(dc2);

                // create new sim Group
                String groupName2 = MessageFormat.format("test-group-{0,number,#}", now);
                String externalGroup2 = MessageFormat.format("test-externalGroup-{0,number,#}", now);
                SimCardGroupCreator scgc2 = new SimCardGroupCreator(provider.getId(), account2.getId(), groupName2, SimCardGroupType.EXCLUSIVE);
                scgc2.setExternalProviderGroupId(externalGroup2);
                SimCardGroup scg2 = providerService.createSimCardGroup(scgc2);

                // update the sim card
                sim = simService.find(scg.getId(), sim.getId());
                s_logger.info("----------------------------------");
                s_logger.info("Old group = {}", sim.getSimGroupId());
                s_logger.info("----------------------------------");
                sim.setSimGroupId(scg2.getId());
                sim.setImei(prefix2 + "imei");
                simService.update(sim);

                // check the updated sim card

                sim = simService.find(scg2.getId(), sim.getId());

                s_logger.info("----------------------------------");
                s_logger.info("New group = {}", sim.getSimGroupId());
                s_logger.info("----------------------------------");

                assertEquals(sim.getConnectionStatus(), "1");
                assertEquals(sim.getLogInformations(), logInformations);
                assertEquals(sim.getIccid(), iccid);
                assertEquals(sim.getImei(), prefix2 + "imei");
                assertEquals(sim.getImsi(), imsi);
                assertEquals(sim.getSimGroupId(), scg2.getId());
                assertEquals(sim.getSimProviderId(), provider.getId());
                assertTrue(Math.abs(sim.getLatitude() - (((float) now) / 10000)) < 1000);
                assertTrue(Math.abs(sim.getLongitude() - (((float) now) / 10000)) < 1000);
                assertEquals(sim.getStatus(), SimCardStatus.ENABLED);
                assertEquals(sim.getSyncStatus(), SyncStatus.SYNCED);

                // delete all the items

                sim = simService.find(scg2.getId(), sim.getId());
                simService.delete(sim);
                drs.delete(d2);
                drs.delete(d);
                providerService.deleteSimCardGroup(scg2);
                providerService.deleteSimCardGroup(scg);
                providerService.delete(provider);
                accountService.delete(account2);
                accountService.delete(account);

                response.setStatus(200);

            } catch (Exception e) {
                s_logger.error("Error during sim card group migration test", e);
                response.sendError(500);
                throw new ServletException(e);
            }
        }
    }

    // -----------------------------------------------------------------------------------------
    //
    // Private Methods
    //
    // -----------------------------------------------------------------------------------------

    private DeviceCreatorOld prepareDevice(long accountId, String prefix) {
        DeviceCreatorOld dc = new DeviceCreatorOld(accountId, prefix + "AA:BB:CC:DD:EE");
        dc.setConnectionIp(prefix + "192.168.0.1");
        dc.setDisplayName(prefix + "displayName");
        dc.setSerialNumber(prefix + "serialNumber");
        dc.setImei(prefix + "imei");
        dc.setImsi(prefix + "imsi");
        dc.setIccid(prefix + "iccid");
        dc.setModelId(prefix + "modelId");
        dc.setBiosVersion("biosVersion");
        dc.setFirmwareVersion("firmwareVersion");
        dc.setOsVersion("osVersion");
        dc.setJvmVersion("jvmVersion");
        dc.setOsgiFrameworkVersion("osgiFrameworkVersion");
        dc.setEsfVersion(prefix + "esfVersion");
        dc.setAcceptEncoding(prefix + "acceptEncoding");
        dc.setApplicationIdentifiers(prefix + "applicationIdentifiers");
        dc.setGpsLatitude(1.0);
        dc.setGpsLongitude(1.0);
        dc.setCustomAttribute1(prefix + "customAttribute1");
        dc.setCustomAttribute2(prefix + "customAttribute2");
        dc.setCustomAttribute3(prefix + "customAttribute3");
        dc.setCustomAttribute4(prefix + "customAttribute4");
        dc.setCustomAttribute5(prefix + "customAttribute5");

        StringBuilder sb = new StringBuilder();
        sb.append("uptime=10\n");
        sb.append("modelName=" + prefix + "modelName\n");
        sb.append("partNumber=" + prefix + "partNumber\n");
        sb.append("availableProcessors=" + prefix + "availableProcessors\n");
        sb.append("totalMemory=" + prefix + "totalMemory\n");
        sb.append("os=" + prefix + "os\n");
        sb.append("osArch=" + prefix + "osArch\n");
        sb.append("jvmName=" + prefix + "jvmName\n");
        sb.append("jvmProfile=" + prefix + "jvmProfile\n");
        sb.append("osgiFramework=" + prefix + "osgiFramework\n");
        sb.append("connectionInterface=" + prefix + "connectionInterface\n");
        sb.append("gpsAltitude=1.0\n");

        dc.setProperties(sb.toString());
        return dc;
    }

    private AccountOld createTestAccount()
    throws KapuaException {
        //
        // prepare the AccountCreator
        long now = System.currentTimeMillis();
        String accountName = MessageFormat.format("test-account-{0,number,#}", now);
        String accountPassword = "We!come12345";
        String organizationName = MessageFormat.format("test_organization_{0}", now);
        String organizationEmail = MessageFormat.format("test{0,number,#}@organization.com", now);

        AccountCreatorOld accountCreator = new AccountCreatorOld();
        accountCreator.setAccountName(accountName);
        accountCreator.setAccountPassword(accountPassword);
        accountCreator.setOrganizationName(organizationName);
        accountCreator.setOrganizationEmail(organizationEmail);
        accountCreator.setBrokerUrl(EdcConfig.getInstance().getSystemBrokerUrls().get(0));
        accountCreator.setParentAccountId(1); // Set eurotech as parent
        accountCreator.setDataTimeToLive(10);
        accountCreator.setDataStorageEnabled(true);
        accountCreator.setDataTimeToLive(9);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now);
        cal.add(Calendar.MONTH, 3);
        accountCreator.setExpirationDate(cal.getTime());

        int m_defaultDevices = 1000;
        accountCreator.setMaxNumberOfDevices(m_defaultDevices);

        //
        // create the Account
        ServiceLocator locator = ServiceLocator.getInstance();
        AccountServiceOld accountService = locator.getAccountService();
        AccountOld account = accountService.create(accountCreator);

        return account;
    }
    
    private SimProviderCreator initializeSpc(long now){

		EdcConfig config = EdcConfig.getInstance();
		
		// create the simProvider
		String providerName = MessageFormat.format("test-provider-{0,number,#}", now);
		
		String providerGateway = MessageFormat.format("rmi://{0}:{1,number,#}/{2}", config.getSimGwRmiServerUri(), config.getSimGwRmiServerRegistryPort(),  SimProviders.TEST.getProviderName());
		
		String providerInbound = MessageFormat.format("test-inbound-{0,number,#}", now);
		SimProviderCreator sc = new SimProviderCreator(providerName, providerGateway, providerInbound);
		String providerOutbound = MessageFormat.format("test-outbound-{0,number,#}", now);
		String password = "We!come12345";
		String username = MessageFormat.format("test-username-{0,number,#}", now);
		sc.setOutboundURL(providerOutbound);
		sc.setInboundPassword(password);
		sc.setInboundUsername(username);
		return sc;

	}
}
