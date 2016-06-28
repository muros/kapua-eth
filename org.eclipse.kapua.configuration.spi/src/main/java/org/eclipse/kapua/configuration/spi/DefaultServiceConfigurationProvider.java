/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.configuration.spi;

import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.config.model.AbstractKapuaConfigEntity;
import org.eclipse.kapua.commons.config.model.AbstractKapuaConfigEntityCreator;
import org.eclipse.kapua.commons.util.JpaUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.ComponentConfiguration;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.config.KapuaConfigEntityFactory;
import org.eclipse.kapua.service.config.KapuaServiceConfigurationProvider;

public class DefaultServiceConfigurationProvider<E extends AbstractKapuaConfigEntity, C extends AbstractKapuaConfigEntityCreator<E>, F extends KapuaConfigEntityFactory<E, C>>
                                                implements KapuaServiceConfigurationProvider<E, C, F>
{
    // TODO Implement!
    public ComponentConfiguration getServiceConfiguration(Class<C> clazz, Class<F> clazzFactory, String pid)
    {
        return null;
    }

    // TODO Implement!
    public void setServiceConfiguration(Class<C> clazz, Class<F> clazzFactory, String pid, Map<String, Object> values)
    {
        EntityManager em = null;
        try {
            em = JpaUtils.getEntityManager();
            F f = KapuaLocator.getInstance().getFactory(clazzFactory);
            C creator = f.newConfigurationCreator((KapuaId) values.get("scopeId"));
            E entity = f.newConfiguration(creator);

            JpaUtils.beginTransaction(em);
            em.persist(entity);
            JpaUtils.commit(em);

        }
        catch (KapuaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            JpaUtils.close(em);
        }
    }
}
