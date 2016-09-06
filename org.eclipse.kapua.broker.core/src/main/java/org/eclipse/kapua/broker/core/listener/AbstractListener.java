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

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.service.metric.MetricsService;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;

/**
 * Default camel pojo endpoint listener
 *
 */
public abstract class AbstractListener {
	
	//metrics
	private String metricComponentName = "listener";
  	private final static MetricsService metricsService = KapuaLocator.getInstance().getService(MetricsService.class);
  	
  	protected String name;
	
	protected AbstractListener(String name) {
		this.name = name;
	}
	
	protected AbstractListener(String metricComponentName, String name) {
		this(name);
		this.metricComponentName = metricComponentName;
	}
	
	protected Counter registerCounter(String... names) {
		return metricsService.getCounter(metricComponentName, name, names);
	}
	
	protected Timer registerTimer(String... names) {
		return metricsService.getTimer(metricComponentName, name, names);
	}

}