package org.eclipse.kapua.broker.core.listener;

public interface CamelConstants {
	
	public String JMS_EXCHANGE_FAILURE_ENDPOINT  = "CamelFailureEndpoint";
	public String JMS_EXCHANGE_FAILURE_EXCEPTION = "CamelExceptionCaught";
	public String JMS_EXCHANGE_REDELIVERED       = "JMSRedelivered";

	public String JMS_HEADER_TIMESTAMP    = "JMSTimestamp";
	public String JMS_HEADER_DESTINATION  = "JMSDestination";
	
}