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
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import org.eclipse.kapua.service.datastore.internal.elasticsearch.KapuaMetricValue;

public class KapuaMetricValueBuilder 
{
	private KapuaMetricValue metricValue;
	
	/**
	 * @param searchHit
	 * @return
	 * @throws ParseException 
	 */
	public KapuaMetricValueBuilder build(SearchHit searchHit, String metricName, Class metricType) throws ParseException {
		
		// TODO manage the case where the expected fields aren't present
		Map<String, SearchHitField>  shFields = searchHit.getFields();
		
		String id = searchHit.getId();

		SearchHitField timestampObj = shFields.get(EsSchema.MESSAGE_TIMESTAMP);
		Date timestamp = (Date) (timestampObj == null ? null : EsUtils.convertToEdcObject("date", (String) timestampObj.getValue()));
		
		SearchHitField shMetrics = shFields.get(String.format("%s.%s", EsSchema.MESSAGE_MTR, metricName));
		KapuaMetricValue edcMetricValue = new KapuaMetricValue(timestamp.getTime(), shMetrics.getValue(), id);
		this.metricValue = edcMetricValue;
		return this;
	}
	
	public KapuaMetricValue getMetricValue() {
		return this.metricValue;
	}
}
