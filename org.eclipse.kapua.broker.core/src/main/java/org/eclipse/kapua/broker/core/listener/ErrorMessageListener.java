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
public class ErrorMessageListener extends AbstractListener {
	
	private static final Logger s_logger = LoggerFactory.getLogger(ErrorMessageListener.class);
	
	private Counter metricErrorLifeCycleMessage;
	
	public ErrorMessageListener() {
		super("error");
		metricErrorLifeCycleMessage   = registerCounter("messages", "life_cycle", "count");
	}
	
	public void processMessage(Exchange exchange, Object message) throws KapuaException {
		metricErrorLifeCycleMessage.inc();
		logError(exchange, message, "LifeCycle");
	}
	
	public void lifeCycleMessage(Exchange exchange, Object message) throws KapuaException {
		metricErrorLifeCycleMessage.inc();
		logError(exchange, message, "LifeCycle");
	}
	
	public void processUnmatchedMessage(Exchange exchange, Object message) throws KapuaException {
		metricErrorLifeCycleMessage.inc();
		logUnmatched(exchange, message, "unmatched");
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
	
	private void logUnmatched(Exchange exchange, Object message, String serviceName) {
		s_logger.warn("Processing unmatched message for {}... {} - {}", 
				new Object[]{
				serviceName,
				(message != null ? message.getClass().getName() : "null"), 
				exchange.getProperty(CamelConstants.JMS_EXCHANGE_FAILURE_ENDPOINT)});
	}
	
}