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
import org.eclipse.kapua.broker.core.metrics.MetricsService;
import org.eclipse.kapua.broker.core.metrics.internal.MetricsServiceBean;
import org.eclipse.kapua.broker.core.plugin.KapuaSecurityBrokerFilter;
import org.eclipse.kapua.message.KapuaInvalidTopicException;
import org.eclipse.kapua.model.id.KapuaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

public class KapuaConverter {
	
	public static final Logger logger = LoggerFactory.getLogger(KapuaConverter.class);
	
	//metrics
	private final static String METRIC_COMPONENT_NAME = "converter";
	//TODO get the metric service through the service locator
  	private final static MetricsService metricsService = MetricsServiceBean.getInstance();
	
	private Counter metricConverterJmsMessage;
	private Counter metricConverterJmsErrorMessage;
	private Counter metricConverterMessage;
	private Counter metricConverterErrorMessage;
	
	public KapuaConverter() {
		metricConverterJmsMessage      = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "jms", "messages", "count");
		metricConverterJmsErrorMessage = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "jms", "messages", "error", "count");
		metricConverterMessage         = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "count");
		metricConverterErrorMessage    = metricsService.getCounter(METRIC_COMPONENT_NAME, "kapua", "kapua_message", "messages", "error", "count");
	}
 
	@Converter
	public CamelKapuaMessage convertTo(Exchange exchange, Object value) throws KapuaException {
		metricConverterMessage.inc();
		//assume that the message is a Camel Jms message
		org.apache.camel.component.jms.JmsMessage message = (org.apache.camel.component.jms.JmsMessage) exchange.getIn();
		if ((Message)message.getJmsMessage() instanceof javax.jms.BytesMessage) {
			try {
		        Date queuedOn = new Date(message.getHeader(CamelConstants.JMS_HEADER_TIMESTAMP, Long.class));
		        KapuaId connectionId = (KapuaId)message.getHeader(KapuaSecurityBrokerFilter.HEADER_KAPUA_CONNECTION_ID);
				return JmsUtil.convertToKapuaMessage((javax.jms.BytesMessage)value, CamelUtil.getTopic(message), queuedOn, connectionId);
			} 
			catch (JMSException | KapuaInvalidTopicException e) {
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