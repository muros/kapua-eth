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
 *******************************************************************************/
package org.eclipse.kapua.app.api;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import org.eclipse.kapua.app.api.v1.resources.model.ErrorBean;
import org.eclipse.kapua.commons.configuration.metatype.TocdImpl;
import org.eclipse.kapua.model.config.metatype.KapuaTad;
import org.eclipse.kapua.model.config.metatype.KapuaTicon;
import org.eclipse.kapua.model.config.metatype.KapuaTocd;
import org.eclipse.kapua.model.config.metatype.KapuaToption;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountCreator;
import org.eclipse.kapua.service.account.AccountListResult;
import org.eclipse.kapua.service.account.AccountXmlRegistry;
import org.eclipse.kapua.service.device.call.kura.model.configuration.KuraDeviceConfiguration;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackage;
import org.eclipse.kapua.service.device.call.kura.model.deploy.KuraDeploymentPackages;
import org.eclipse.kapua.service.device.call.kura.model.snapshot.KuraSnapshotIds;
import org.eclipse.kapua.service.device.management.command.DeviceCommandInput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandOutput;
import org.eclipse.kapua.service.device.management.command.DeviceCommandXmlRegistry;
import org.eclipse.kapua.service.device.management.configuration.DeviceComponentConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfiguration;
import org.eclipse.kapua.service.device.management.configuration.DeviceConfigurationXmlRegistry;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackage;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfo;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageBundleInfos;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackageXmlRegistry;
import org.eclipse.kapua.service.device.management.packages.model.DevicePackages;
import org.eclipse.kapua.service.device.management.packages.model.download.DevicePackageDownloadRequest;
import org.eclipse.kapua.service.device.management.packages.model.uninstall.DevicePackageUninstallRequest;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshotXmlRegistry;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshot;
import org.eclipse.kapua.service.device.management.snapshot.DeviceSnapshots;
import org.eclipse.kapua.service.device.registry.Device;
import org.eclipse.kapua.service.device.registry.DeviceCreator;
import org.eclipse.kapua.service.device.registry.DeviceListResult;
import org.eclipse.kapua.service.device.registry.DeviceXmlRegistry;
import org.eclipse.kapua.service.device.registry.event.DeviceEvent;
import org.eclipse.kapua.service.device.registry.event.DeviceEventListResult;
import org.eclipse.kapua.service.device.registry.event.DeviceEventXmlRegistry;
import org.eclipse.kapua.service.user.User;
import org.eclipse.kapua.service.user.UserCreator;
import org.eclipse.kapua.service.user.UserListResult;
import org.eclipse.kapua.service.user.UserXmlRegistry;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

/**
 * Provide a customized JAXBContext that makes the concrete implementations
 * known and available for marshalling
 */
@Provider
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class JaxbContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext jaxbContext;

    public JaxbContextResolver() {
        try {
            jaxbContext = JAXBContextFactory.createContext(new Class[] {
                    ErrorBean.class,
                    Account.class,
                    AccountCreator.class,
                    AccountListResult.class,
                    AccountXmlRegistry.class,
                    User.class,
                    UserCreator.class,
                    UserListResult.class,
                    UserXmlRegistry.class,
                    Device.class,
                    DeviceCreator.class,
                    DeviceListResult.class,
                    DeviceXmlRegistry.class,
                    DeviceCommandInput.class,
                    DeviceCommandXmlRegistry.class,
                    DeviceCommandOutput.class,
                    DeviceConfiguration.class,
                    DeviceComponentConfiguration.class,
                    DeviceConfigurationXmlRegistry.class,
                    DeviceSnapshot.class,
                    DeviceSnapshots.class,
                    DeviceSnapshotXmlRegistry.class,
                    DeviceEvent.class,
                    DeviceEventListResult.class,
                    DeviceEventXmlRegistry.class,
                    DevicePackage.class,
                    DevicePackages.class,
                    DevicePackageBundleInfo.class,
                    DevicePackageBundleInfos.class,
                    DevicePackageXmlRegistry.class,
                    DevicePackageDownloadRequest.class,
                    DevicePackageUninstallRequest.class,
                    KuraSnapshotIds.class,
                    KuraDeviceConfiguration.class,
                    KuraDeploymentPackages.class,
                    KuraDeploymentPackage.class,
                    TocdImpl.class,
                    KapuaTocd.class,
                    KapuaTad.class,
                    KapuaTicon.class,
                    KapuaToption.class
            }, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JAXBContext getContext(Class<?> type) {
        return jaxbContext;
    }

}
