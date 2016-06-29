package org.eclipse.kapua.broker.core.listener;

import org.apache.camel.Exchange;
import org.apache.camel.spi.UriEndpoint;
import org.eclipse.kapua.KapuaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

@UriEndpoint(
		title="error message processor", 
		syntax="bean:errorMessageListener",
		scheme="bean") 
public class ErrorMessageListener extends AbstractErrorListener<Object> {
	
	private static final Logger s_logger = LoggerFactory.getLogger(ErrorMessageListener.class);
	
	private Counter metricErrorLifeCycleMessage;
	

	public ErrorMessageListener() {
		super("error");
		metricErrorLifeCycleMessage   = registerCounter("messages", "life_cycle", "count");
	}
	
	@Override
	public void processMessage(Exchange exchange, Object message) throws KapuaException {
		metricErrorLifeCycleMessage.inc();
		logError(exchange, message, "LifeCycle");
	}
	
	private void logError(Exchange exchange, Object message, String serviceName) {
		Throwable t = ((Throwable)exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_EXCEPTION));
		s_logger.warn("Processing error message for {}... {} - {} - {}", 
				new Object[]{
				serviceName,
				(message != null ? message.getClass().getName() : "null"), 
				exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_ENDPOINT), 
				t.getMessage()});
		s_logger.warn("Exception: ", t);
	}
	
}