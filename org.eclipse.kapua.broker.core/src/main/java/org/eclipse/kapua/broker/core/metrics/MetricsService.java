package org.eclipse.kapua.broker.core.metrics;

import org.eclipse.kapua.KapuaException;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Metric service definition
 *
 */
public interface MetricsService {
	
	/**
	 * Get a Counter for the specified name. If the counter doesn't exist the method should create a new one counter with the specified name.
	 * @param module
	 * @param component
	 * @param names
	 * @return
	 */
	public Counter getCounter(String module, String component, String... names);
	
	/**
	 * Get a Histogram for the specified name. If the histogram doesn't exist the method should create a new one histogram with the specified name.
	 * @param module
	 * @param component
	 * @param names
	 * @return
	 */
	public Histogram getHistogram(String module, String component, String... names);
	
	/**
	 * Get a Timer for the specified name. If the timer doesn't exist the method should create a new one timer with the specified name.
	 * @param module
	 * @param component
	 * @param names
	 * @return
	 */
	public Timer getTimer(String module, String component, String... names);
	
	/**
	 * Register a Gauge for the specified name. If the Gauge exists the method throws exception.
	 * @param module
	 * @param component
	 * @param names
	 * @return
	 * @throws KapuaException if the metric is already defined
	 */
	public void registerGauge(Gauge<?> gauge, String module, String component, String... names) throws KapuaException;
	
	/**
	 * Return the MetricRegistry containing all the metrics
	 * 
	 * @return MetricRegistry
	 */
	public MetricRegistry getMetricRegistry();
	
}