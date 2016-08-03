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
package org.eclipse.kapua.configuration.spi;

import java.util.Map;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.model.config.ComponentConfiguration;
import org.eclipse.kapua.model.config.metatype.Tocd;
import org.eclipse.kapua.service.config.KapuaServiceConfigurationProvider;

public class DefaultServiceConfigurationProvider implements KapuaServiceConfigurationProvider
{
    // TODO Implement!
    public ComponentConfiguration getServiceConfiguration(String pid)
    {
        return null;
    }

    // TODO Implement!
    public void setServiceConfiguration(String pid, Map<String, Object> values)
    {
        // EntityManager em = null;
        // try {
        // em = JpaUtils.getEntityManager();
        // ServiceConfigEntity entity = new ServiceConfigEntity((KapuaId) values.get("scopeId"));
        //
        // JpaUtils.beginTransaction(em);
        // em.persist(entity);
        // JpaUtils.commit(em);
        //
        // }
        // catch (KapuaException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // finally {
        // JpaUtils.close(em);
        // }
    }

    @Override
    public Tocd getConfigMetadata()
        throws KapuaException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
