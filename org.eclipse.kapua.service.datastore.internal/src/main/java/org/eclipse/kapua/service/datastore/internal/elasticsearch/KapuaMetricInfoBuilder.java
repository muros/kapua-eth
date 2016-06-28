/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

public class KapuaMetricInfoBuilder {

	private KapuaMetricInfo<?> edcMetricInfo;
	
	public KapuaMetricInfoBuilder build(SearchHit searchHit) throws Exception {
		
		Map<String, SearchHitField> fields = searchHit.fields();
		String metricName = (String) fields.get(EsSchema.TOPIC_METRIC_MTR_NAME_FULL).getValue();
		String metricType = (String) fields.get(EsSchema.TOPIC_METRIC_MTR_TYPE_FULL).getValue();
		String metricTimestamp = (String) fields.get(EsSchema.TOPIC_METRIC_MTR_TYPE_FULL).getValue();
		String metricUuid = (String) fields.get(EsSchema.TOPIC_METRIC_MTR_MSG_ID_FULL).getValue();
		Object metricValue = fields.get(EsSchema.TOPIC_METRIC_MTR_VALUE_FULL).getValue();
		
		String edcMetricName = EsUtils.restoreMetricName(metricName);
		KapuaMetricInfo<?> metricInfo = null;
		if (EsUtils.ES_TYPE_STRING.equals(metricType))
			metricInfo = new KapuaMetricInfo<String>(edcMetricName, String.class, (String)metricValue, metricUuid);
		
		if (EsUtils.ES_TYPE_INTEGER.equals(metricType))
			metricInfo = new KapuaMetricInfo<Integer>(edcMetricName, Integer.class, (Integer)metricValue, metricUuid);
		
		if (EsUtils.ES_TYPE_LONG.equals(metricType)) {
			Object obj = metricValue;
			if (metricValue != null && metricValue instanceof Integer) 
				obj = ((Integer)metricValue).longValue();
			
			metricInfo = new KapuaMetricInfo<Long>(edcMetricName, Long.class, (Long)obj, metricUuid);
		}
		
		if (EsUtils.ES_TYPE_FLOAT.equals(metricType))
			metricInfo = new KapuaMetricInfo<Float>(edcMetricName, Float.class, (Float)metricValue, metricUuid);
		
		if (EsUtils.ES_TYPE_DOUBLE.equals(metricType))
			metricInfo = new KapuaMetricInfo<Double>(edcMetricName, Double.class, (Double)metricValue, metricUuid);
		
		if (EsUtils.ES_TYPE_BOOL.equals(metricType))
			metricInfo = new KapuaMetricInfo<Boolean>(edcMetricName, Boolean.class, (Boolean)metricValue, metricUuid);
		
		if (EsUtils.ES_TYPE_BINARY.equals(metricType))
			metricInfo = new KapuaMetricInfo<byte[]>(edcMetricName, byte[].class, (byte[])metricValue, metricUuid);
			
		if (metricInfo == null)
			throw new Exception(String.format("Unknown metric type [%s]", metricType));

		this.edcMetricInfo = metricInfo;
		return this;
	}
	
	public KapuaMetricInfo<?> getKapuaMetricInfo() {
		return edcMetricInfo;
	}
}
