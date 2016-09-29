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
 *
 *******************************************************************************/
package org.eclipse.kapua.broker.core.converter;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsMessage;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.listener.CamelConstants;
import org.eclipse.kapua.broker.core.message.CamelKapuaMessage;
import org.eclipse.kapua.broker.core.message.CamelUtil;
import org.eclipse.kapua.broker.core.message.JmsUtil;
import org.eclipse.kapua.broker.core.message.MessageConstants;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor.MESSAGE_TYPE;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.metric.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

/**
 * Kapua message converter used to convert from Camel incoming messages ({@link JmsMessage}) to a platform specific message type.
 */
public class KapuaConverter
{

    public static final Logger logger = LoggerFactory.getLogger(KapuaConverter.class);

    // metrics
    private final static String         METRIC_COMPONENT_NAME = "converter";
    private final static MetricsService metricsService        = KapuaLocator.getInstance().getService(MetricsService.class);

    private Counter metricConverterJmsMessage;
    private Counter metricConverterJmsErrorMessage;
    private Counter metricConverterDataMessage;
    private Counter metricConverterAppMessage;
    private Counter metricConverterBirthMessage;
    private Counter metricConverterDcMessage;
    private Counter metricConverterMissingMessage;
    private Counter metricConverterErrorMessage;

    public KapuaConverter()
    {
        metricConverterJmsMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "jms", "messages", "count");
        metricConverterJmsErrorMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "jms", "messages", "error", "count");
        metricConverterDataMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "data", "count");
        metricConverterAppMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "app", "count");
        metricConverterBirthMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "birth", "count");
        metricConverterDcMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "dc", "count");
        metricConverterMissingMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "missing", "count");
        metricConverterErrorMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "error", "count");
    }

    @Converter
    /**
     * Convert incoming message to a Kapua data message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains data message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    public CamelKapuaMessage<?> convertToData(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterDataMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.data);
    }

    @Converter
    /**
     * Convert incoming message to a Kapua application message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains application message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    public CamelKapuaMessage<?> convertToApps(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterAppMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.app);
    }

    @Converter
    /**
     * Convert incoming message to a Kapua birth message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains birth message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    public CamelKapuaMessage<?> convertToBirth(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterBirthMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.birth);
    }

    @Converter
    /**
     * Convert incoming message to a Kapua disconnect message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains disconnect message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    public CamelKapuaMessage<?> convertToDisconnect(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterDcMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.disconnect);
    }

    @Converter
    /**
     * Convert incoming message to a Kapua missing message
     * 
     * @param exchange
     * @param value
     * @return Message container that contains missing message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    public CamelKapuaMessage<?> convertToMissing(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterMissingMessage.inc();
        return convertTo(exchange, value, MESSAGE_TYPE.missing);
    }

    /**
     * Convert incoming message to a Kapua message (depending on messageType parameter)
     * 
     * @param exchange
     * @param value
     * @param messageType expected incoming message type
     * @return Message container that contains message of asked type
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage or an error during conversion occurred
     */
    private CamelKapuaMessage<?> convertTo(Exchange exchange, Object value, MESSAGE_TYPE messageType) throws KapuaException
    {
        // assume that the message is a Camel Jms message
        org.apache.camel.component.jms.JmsMessage message = (org.apache.camel.component.jms.JmsMessage) exchange.getIn();
        if ((Message) message.getJmsMessage() instanceof javax.jms.BytesMessage) {
            try {
                Date queuedOn = new Date(message.getHeader(CamelConstants.JMS_HEADER_TIMESTAMP, Long.class));
                KapuaId connectionId = (KapuaId) message.getHeader(MessageConstants.HEADER_KAPUA_CONNECTION_ID);
                ConnectorDescriptor connectorDescriptor = (ConnectorDescriptor) message.getHeader(MessageConstants.HEADER_KAPUA_CONNECTOR_DEVICE_PROTOCOL);
                return JmsUtil.convertToCamelKapuaMessage(connectorDescriptor, messageType, (byte[]) value, CamelUtil.getTopic(message), queuedOn, connectionId);
            }
            catch (JMSException e) {
                metricConverterErrorMessage.inc();
                logger.error("Exception converting message {}", e.getMessage(), e);
                throw KapuaException.internalError(e, "Cannot convert the message type " + exchange.getIn().getClass());
            }
        }
        metricConverterErrorMessage.inc();
        throw KapuaException.internalError("Cannot convert the message - Wrong instance type: " + exchange.getIn().getClass());
    }

    @Converter
    /**
     * Convert incoming message to a javax.jms.Message
     * 
     * @param exchange
     * @param value
     * @return jms Message
     * @throws KapuaException if incoming message does not contain a javax.jms.BytesMessage
     */
    public Message convertToJmsMessage(Exchange exchange, Object value) throws KapuaException
    {
        metricConverterJmsMessage.inc();
        // assume that the message is a Camel Jms message
        org.apache.camel.component.jms.JmsMessage message = (org.apache.camel.component.jms.JmsMessage) exchange.getIn();
        if (message.getJmsMessage() instanceof javax.jms.BytesMessage) {
            return (Message) message.getJmsMessage();
        }
        metricConverterJmsErrorMessage.inc();
        throw KapuaException.internalError("Cannot convert the message - Wrong instance type: " + exchange.getIn().getClass());
    }

}
