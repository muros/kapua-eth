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

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.metric.MetricsService;
import org.eclipse.kapua.locator.KapuaLocator;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;

/**
 * Default camel pojo endpoint
 *
 * @param <M> Message to process
 */
public abstract class AbstractListener<M extends Object> {
	
	//metrics
	private final static String METRIC_COMPONENT_NAME = "listener";
  	private final static MetricsService metricsService = KapuaLocator.getInstance().getService(MetricsService.class);
  	
  	protected String name;
	
	protected AbstractListener(String name) {
		this.name = name;
	}
	
	public abstract void processMessage(M message) throws KapuaException;
	
	protected Counter registerCounter(String... names) {
		return metricsService.getCounter(METRIC_COMPONENT_NAME, name, names);
	}
	
	protected Timer registerTimer(String... names) {
		return metricsService.getTimer(METRIC_COMPONENT_NAME, name, names);
	}

}