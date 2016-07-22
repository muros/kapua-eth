package org.eclipse.kapua.broker.core.converter;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.broker.core.listener.CamelConstants;
import org.eclipse.kapua.broker.core.message.CamelKapuaMessage;
import org.eclipse.kapua.broker.core.message.CamelUtil;
import org.eclipse.kapua.broker.core.message.JmsUtil;
import org.eclipse.kapua.broker.core.message.MessageConstants;
import org.eclipse.kapua.broker.core.plugin.ConnectorDescriptor;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

public class KapuaConverter {
	
	public static final Logger logger = LoggerFactory.getLogger(KapuaConverter.class);
	
	//metrics
	private final static String METRIC_COMPONENT_NAME = "converter";
  	private final static MetricsService metricsService = KapuaLocator.getInstance().getService(MetricsService.class);
	
	private Counter metricConverterJmsMessage;
	private Counter metricConverterJmsErrorMessage;
	private Counter metricConverterDataMessage;
	private Counter metricConverterBirthMessage;
	private Counter metricConverterErrorMessage;
	
	public KapuaConverter() {
		metricConverterJmsMessage      = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "jms", "messages", "count");
		metricConverterJmsErrorMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "jms", "messages", "error", "count");
		metricConverterDataMessage     = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "data", "count");
		metricConverterBirthMessage    = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "birth", "count");
		metricConverterErrorMessage    = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "error", "count");
	}
 
	@Converter
	public CamelKapuaMessage<?> convertTo(Exchange exchange, Object value) throws KapuaException {
		metricConverterDataMessage.inc();
		//assume that the message is a Camel Jms message
		org.apache.camel.component.jms.JmsMessage message = (org.apache.camel.component.jms.JmsMessage) exchange.getIn();
		if ((Message)message.getJmsMessage() instanceof javax.jms.BytesMessage) {
			try {
		        Date queuedOn = new Date(message.getHeader(CamelConstants.JMS_HEADER_TIMESTAMP, Long.class));
		        KapuaId connectionId = (KapuaId)message.getHeader(MessageConstants.HEADER_KAPUA_CONNECTION_ID);
		        ConnectorDescriptor deviceProtocol = (ConnectorDescriptor)message.getHeader(MessageConstants.HEADER_KAPUA_CONNECTOR_DEVICE_PROTOCOL);
				return JmsUtil.convertToCamelKapuaMessage(deviceProtocol, (byte[])value, CamelUtil.getTopic(message), queuedOn, connectionId);
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
	public CamelKapuaMessage<?> convertToBirth(Exchange exchange, Object value) throws KapuaException {
		metricConverterBirthMessage.inc();
		//assume that the message is a Camel Jms message
		org.apache.camel.component.jms.JmsMessage message = (org.apache.camel.component.jms.JmsMessage) exchange.getIn();
		if ((Message)message.getJmsMessage() instanceof javax.jms.BytesMessage) {
			try {
		        Date queuedOn = new Date(message.getHeader(CamelConstants.JMS_HEADER_TIMESTAMP, Long.class));
		        KapuaId connectionId = (KapuaId)message.getHeader(MessageConstants.HEADER_KAPUA_CONNECTION_ID);
		        ConnectorDescriptor deviceProtocol = (ConnectorDescriptor)message.getHeader(MessageConstants.HEADER_KAPUA_CONNECTOR_DEVICE_PROTOCOL);
				return JmsUtil.convertToCamelKapuaMessage(deviceProtocol, (byte[])value, CamelUtil.getTopic(message), queuedOn, connectionId);
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
	public Message convertToJmsMessage(Exchange exchange, Object value) throws KapuaException {
		metricConverterJmsMessage.inc();
		//assume that the message is a Camel Jms message
		org.apache.camel.component.jms.JmsMessage message = (org.apache.camel.component.jms.JmsMessage) exchange.getIn();
		if (message.getJmsMessage() instanceof javax.jms.BytesMessage) {
			return (Message)message.getJmsMessage();
		}
		metricConverterJmsErrorMessage.inc();
		throw KapuaException.internalError("Cannot convert the message - Wrong instance type: " + exchange.getIn().getClass());
    }
	
}