package org.eclipse.kapua.commons.configuration;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.commons.util.EntityManager;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;

public class ServiceConfigDAO extends ServiceDAO
{
    public static ServiceConfigImpl create(EntityManager em, ServiceConfigCreatorImpl serviceConfigCreator)
            throws KapuaException
        {
            //
            // Create User
            ServiceConfigImpl serviceConfigImpl = new ServiceConfigImpl(serviceConfigCreator.getScopeId());

            serviceConfigImpl.setPid(serviceConfigCreator.getPid());

            return ServiceDAO.create(em, serviceConfigImpl);
        }

        public static ServiceConfig update(EntityManager em, ServiceConfig serviceConfig)
            throws KapuaException
        {
            //
            // Update user
            ServiceConfigImpl serviceConfigImpl = (ServiceConfigImpl) serviceConfig;

            return ServiceDAO.update(em, ServiceConfigImpl.class, serviceConfigImpl);
        }

        public static void delete(EntityManager em, KapuaId userId)
        {
            ServiceDAO.delete(em, ServiceConfigImpl.class, userId);
        }

        public static ServiceConfig find(EntityManager em, KapuaId userId)
        {
            return em.find(ServiceConfigImpl.class, userId);
        }

        public static ServiceConfig findByName(EntityManager em, String name)
        {
            return ServiceDAO.findByName(em, ServiceConfigImpl.class, name);
        }

        public static ServiceConfigListResult query(EntityManager em, KapuaQuery<ServiceConfig> serviceConfigQuery)
            throws KapuaException
        {
            return ServiceDAO.query(em, ServiceConfig.class, ServiceConfigImpl.class, new ServiceConfigListResultImpl(), serviceConfigQuery);
        }

        public static long count(EntityManager em, KapuaQuery<ServiceConfig> serviceConfigQuery)
            throws KapuaException
        {
            return ServiceDAO.count(em, ServiceConfig.class, ServiceConfigImpl.class, serviceConfigQuery);
        }

}
