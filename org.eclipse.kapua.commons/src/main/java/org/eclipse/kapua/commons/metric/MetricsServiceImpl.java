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
package org.eclipse.kapua.commons.metric;

import java.text.MessageFormat;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.service.metric.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Metric report exporter handler.
 * It provides methods for register/unregister metric in the context
 *
 */
public class MetricsServiceImpl implements MetricsService {
		
	private static Logger logger = LoggerFactory.getLogger(MetricsServiceImpl.class);
	
	public final static String METRICS_NAME_FORMAT = "{0}.{1}.{2}";
	
	private final MetricRegistry metricRegistry;

	public MetricsServiceImpl() {
		 metricRegistry = new MetricRegistry();
	}
	
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}
	
	public Counter getCounter(String module, String component, String... names) {
		String name = getMetricName(module, component, names);
		Counter counter = metricRegistry.getCounters().get(name);
		if(counter == null) {
			logger.debug("Creating a Counter: {}", name);
			counter = metricRegistry.counter(name);
		}
		return counter;
	}
	
	public Histogram getHistogram(String module, String component, String... names) {
		String name = getMetricName(module, component, names);
		Histogram histogram = metricRegistry.getHistograms().get(name);
		if(histogram == null) {
			logger.debug("Creating a Histogram: {}", name);
			histogram = metricRegistry.histogram(name);
		}
		return histogram;
	}
	
	public Timer getTimer(String module, String component, String... names) {
		String name = getMetricName(module, component, names);
		Timer timer = metricRegistry.getTimers().get(name);
		if(timer == null) {
			logger.debug("Creating a Timer: {}", name);
			timer = metricRegistry.timer(name);
		}
		return timer;
	}
	
	public void registerGauge(Gauge<?> gauge, String module, String component, String... names) throws KapuaException {
		String name = getMetricName(module, component, names);
		if (metricRegistry.getGauges().get(name) != null) {
			throw KapuaException.internalError(MessageFormat.format("A metric with the name {0} is already defined!", name));
		}
		else {
			metricRegistry.register(name, gauge);
		}
	}
	
	private String getMetricName(String module, String component, String... metricsName) {
		return MessageFormat.format(METRICS_NAME_FORMAT, new Object[]{module, component, convertToDotNotation(metricsName)});
	}
	
	private String convertToDotNotation(String... metricsName) {
		StringBuilder builder = new StringBuilder();
		boolean firstMetricName = true;
		for(String s : metricsName) {
			if (!firstMetricName) {
				builder.append('.');
			}
			firstMetricName = false;
		    builder.append(s);
		}
		return builder.toString();
	}
	
}
