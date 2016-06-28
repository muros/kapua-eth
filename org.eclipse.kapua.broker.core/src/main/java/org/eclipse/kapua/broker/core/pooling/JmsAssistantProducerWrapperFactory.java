package org.eclipse.kapua.broker.core.pooling;

import javax.jms.Session;

import org.apache.activemq.ActiveMQSession;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.commons.jms.JmsConnectionFactory;

/**
 * {@link BasePooledObjectFactory} assistant broker wrapper factory implementation.
 *
 */
public class JmsAssistantProducerWrapperFactory extends BasePooledObjectFactory<JmsAssistantProducerWrapper> {

    private static Logger s_logger = LoggerFactory.getLogger(JmsAssistantProducerWrapperFactory.class);

    private String destination;

    public JmsAssistantProducerWrapperFactory(String destination) {
        this.destination = destination;
    }

    @Override
    public JmsAssistantProducerWrapper create() throws Exception {
        return new JmsAssistantProducerWrapper(JmsConnectionFactory.vmConnFactory, destination, false, false);
    }

    @Override
    public PooledObject<JmsAssistantProducerWrapper> wrap(JmsAssistantProducerWrapper producerWrapper) {
        return new DefaultPooledObject<JmsAssistantProducerWrapper>(producerWrapper);
    }
    
    @Override
    public boolean validateObject(PooledObject<JmsAssistantProducerWrapper> p) {
    	Session session = p.getObject().session;
    	if (session instanceof ActiveMQSession) {
			return !((ActiveMQSession)session).isClosed();
		}
    	else {
    		s_logger.warn("Wrong session object type {}", session.getClass());
    		return true;
    	}
    }

    @Override
    public void destroyObject(PooledObject<JmsAssistantProducerWrapper> pooledProducerWrapper) throws Exception {
        JmsAssistantProducerWrapper producerWrapper = pooledProducerWrapper.getObject();
        s_logger.info("Close jms broker assistant producer wrapper: {}", producerWrapper.toString());
        producerWrapper.close();
        super.destroyObject(pooledProducerWrapper);
    }

}