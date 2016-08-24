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
package org.eclipse.kapua.commons.configuration;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.configuration.metatype.TmetadataImpl;
import org.eclipse.kapua.commons.model.query.predicate.AndPredicate;
import org.eclipse.kapua.commons.model.query.predicate.AttributePredicate;
import org.eclipse.kapua.commons.security.KapuaSecurityUtils;
import org.eclipse.kapua.commons.service.internal.ServiceDAO;
import org.eclipse.kapua.commons.util.EntityManager;
import org.eclipse.kapua.commons.util.ResourceUtils;
import org.eclipse.kapua.commons.util.XmlUtil;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.config.metatype.Tad;
import org.eclipse.kapua.model.config.metatype.Tmetadata;
import org.eclipse.kapua.model.config.metatype.Tocd;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.predicate.KapuaAttributePredicate.Operator;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.Actions;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.eclipse.kapua.service.config.KapuaConfigurableService;

@SuppressWarnings("serial")
public abstract class AbstractKapuaConfigurableService implements KapuaConfigurableService, Serializable
{
    private String domain = null;
    private String pid    = null;

    private static TmetadataImpl readMetadata(String pid)
        throws IOException, Exception, XMLStreamException, FactoryConfigurationError
    {
        TmetadataImpl metaData = null;
        StringBuilder sbMetatypeXmlName = new StringBuilder();
        sbMetatypeXmlName.append("META-INF/metatype/").append(pid).append(".xml");

        String metatypeXmlName = sbMetatypeXmlName.toString();
        URL metatypeXmlURL = ResourceUtils.getResource(metatypeXmlName);
        String metatypeXml = ResourceUtils.readResource(metatypeXmlURL);
        if (metatypeXml != null) {
            metaData = XmlUtil.unmarshal(metatypeXml, TmetadataImpl.class);
        }
        return metaData;
    }

    private static void validateConfigurations(String pid, Tocd ocd, Map<String, Object> updatedProps)
        throws KapuaException
    {
        if (ocd != null) {

            // build a map of all the attribute definitions
            Map<String, Tad> attrDefs = new HashMap<String, Tad>();
            List<Tad> defs = ocd.getAD();
            for (Tad def : defs) {
                attrDefs.put(def.getId(), def);
            }

            // loop over the proposed property values
            // and validate them against the definition
            for (Entry<String, Object> property : updatedProps.entrySet()) {

                String key = property.getKey();
                Tad attrDef = attrDefs.get(key);

                // is attribute undefined?
                if (attrDef == null) {
                    // we do not have an attribute descriptor to the validation
                    // against
                    // As OSGI insert attributes at runtime like service.pid,
                    // component.name,
                    // for the attribute for which we do not have a definition,
                    // just accept them.
                    continue;
                }

                // validate the attribute value
                Object objectValue = property.getValue();
                String stringValue = StringUtil.valueToString(objectValue);
                if (stringValue != null) {
                    String result = TadValidator.validate(attrDef, stringValue);
                    if (result != null && !result.isEmpty()) {
                        throw new KapuaConfigurationException(KapuaConfigurationErrorCodes.CONFIGURATION_ATTRIBUTE_INVALID, null, attrDef.getId() + ": " + result);
                    }
                }

            }

            // make sure all required properties are set
            if (ocd != null) {
                for (Tad attrDef : ocd.getAD()) {
                    // to the required attributes make sure a value is defined.
                    if (attrDef.isRequired()) {
                        if (updatedProps.get(attrDef.getId()) == null) {
                            // if the default one is not defined, throw
                            // exception.
                            throw new KapuaConfigurationException(KapuaConfigurationErrorCodes.CONFIGURATION_REQUIRED_ATTRIBUTE_MISSING, attrDef.getId());
                        }
                    }
                }
            }
        }
    }

    private static Properties toProperties(Map<String, Object> values)
    {
        Properties props = new Properties();
        for(Entry<String, Object> entry:values.entrySet())
            props.setProperty(entry.getKey(), StringUtil.valueToString(entry.getValue()));
        
        return props;
    }
    
    private static Map<String, Object> toValues(Tocd ocd, Properties props) throws KapuaException
    {
        if (props == null)
            return null;
        
        List<Tad> ads = ocd.getAD();
        Map<String, Tad> tadMap = new HashMap<String, Tad>();
        for(Tad ad:ads)
            tadMap.put(ad.getId(), ad);
        
        Map<String, Object> values = new HashMap<String, Object>();
        for(Entry<Object, Object> entry:props.entrySet())
        {
            if (!tadMap.containsKey(entry.getKey()))
                throw KapuaException.internalError("Unknown property");
            
            Tad tad = tadMap.get(entry.getKey());
            values.put((String)entry.getKey(), StringUtil.stringToValue(tad.getType().value(), props.getProperty((String)entry.getKey())));
        }
        
        return values;
    }
    
    protected AbstractKapuaConfigurableService(String pid, String domain)
    {
        this.pid = pid;
        this.domain = domain;
    }

    public static void main(String[] args)
    {
        try {
            Properties props = new Properties();
            props.setProperty("publish.qos", "122");
            Tmetadata metadata = readMetadata("AbstractKapuaConfigurableService");
            List<Tocd> ocds = metadata.getOCD();
            toValues(ocds.get(0), props);
        }
        catch (FactoryConfigurationError | Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Tocd getConfigMetadata()
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        
        KapuaId scopeId = KapuaSecurityUtils.getSession().getScopeId();
        authorizationService.checkPermission(permissionFactory.newPermission(domain, Actions.read, scopeId));

        try {
            TmetadataImpl metadata = readMetadata(this.pid);
            if (metadata.getOCD() != null && metadata.getOCD().size() > 0) {
                for (Tocd ocd : metadata.getOCD()) {
                    if (ocd.getId() != null && ocd.getId().equals(pid)) {
                        return ocd;
                    }
                }
            }
            return null;
        }
        catch (Exception e) {
            throw KapuaException.internalError(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getConfigValues(KapuaId scopeId)
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(domain, Actions.read, scopeId));

        AndPredicate predicate = new AndPredicate()
                                                   .and(new AttributePredicate<String>("pid", this.pid, Operator.EQUAL))
                                                   .and(new AttributePredicate<KapuaId>("scopeId", scopeId, Operator.EQUAL));

        ServiceConfigQueryImpl query = new ServiceConfigQueryImpl(scopeId);
        query.setPredicate(predicate);

        EntityManager em = ServiceConfigEntityFactory.getEntityManager();
        ServiceConfigListResult result = ServiceDAO.query(em, ServiceConfig.class, ServiceConfigImpl.class, new ServiceConfigListResultImpl(), query);
        if (result == null || result.size() == 0)
            throw KapuaException.internalError("Record not found");

        Tocd ocd = this.getConfigMetadata();
        return toValues(ocd, result.get(0).getConfigurations());
    }

    @Override
    public void setConfigValues(KapuaId scopeId, Map<String, Object> values)
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AuthorizationService authorizationService = locator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = locator.getFactory(PermissionFactory.class);
        authorizationService.checkPermission(permissionFactory.newPermission(domain, Actions.write, scopeId));

        Tocd ocd = this.getConfigMetadata();
        validateConfigurations(this.pid, ocd, values);
        
        Properties props = toProperties(values);
        
        AndPredicate predicate = new AndPredicate()
                                                   .and(new AttributePredicate<String>("pid", this.pid, Operator.EQUAL))
                                                   .and(new AttributePredicate<KapuaId>("scopeId", scopeId, Operator.EQUAL));

        ServiceConfigQueryImpl query = new ServiceConfigQueryImpl(scopeId);
        query.setPredicate(predicate);

        ServiceConfig serviceConfig = null;
        EntityManager em = ServiceConfigEntityFactory.getEntityManager();
        ServiceConfigListResultImpl result = ServiceConfigDAO.query(em, ServiceConfig.class, ServiceConfigImpl.class, new ServiceConfigListResultImpl(), query);
        if (result == null || result.size() == 0) {
            ServiceConfigImpl serviceConfigNew = new ServiceConfigImpl(scopeId);
            serviceConfigNew.setPid(this.pid);
            serviceConfigNew.setConfigurations(props);
            serviceConfig = ServiceDAO.create(em, serviceConfigNew);
            return;
        }

        serviceConfig = result.get(0);
        serviceConfig.setConfigurations(props);
        ServiceDAO.update(em, ServiceConfig.class, serviceConfig);
        return;
    }
}
