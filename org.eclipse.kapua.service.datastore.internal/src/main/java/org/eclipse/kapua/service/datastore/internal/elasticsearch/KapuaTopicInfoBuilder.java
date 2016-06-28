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

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.client.message.KapuaInvalidTopicException;
import org.eclipse.kapua.client.message.KapuaTopic;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

public class KapuaTopicInfoBuilder {
	
	private KapuaTopicInfo topicInfo;
	
	public KapuaTopicInfoBuilder buildFromTopic(SearchHit searchHit) throws KapuaInvalidTopicException, ParseException, KapuaIllegalNullArgumentException {
		
		ArgumentValidator.notNull(searchHit, "SearchHit must not be null.");
		
		Map<String, SearchHitField> fields = searchHit.getFields();
		String topicName = fields.get(EsSchema.TOPIC_SEM_NAME).getValue();
		String timestampStr = fields.get(EsSchema.TOPIC_TIMESTAMP).getValue();
		String asset = fields.get(EsSchema.TOPIC_ASSET).getValue();
		String account = fields.get(EsSchema.TOPIC_ACCOUNT).getValue();
		
		KapuaTopic topic = new KapuaTopic(String.format("%s/%s/%s", account, asset, topicName));
		Date timestamp = (Date)EsUtils.convertToEdcObject("date", timestampStr);
		topicInfo = new KapuaTopicInfo(topic, timestamp);
		return this;
	}
	
	public KapuaTopicInfoBuilder buildFromAssetTopic(SearchHit searchHit) throws KapuaInvalidTopicException, ParseException {
		
		assert searchHit != null : "SearchHit must not be null.";		
		
		Map<String, SearchHitField> fields = searchHit.getFields();
		String asset = fields.get(EsSchema.ASSET_TOPIC_AS_NAME).getValue();
		String account = fields.get(EsSchema.ASSET_TOPIC_ACCOUNT).getValue();
		String topicName = fields.get(EsSchema.ASSET_TOPIC_TPC_NAME_FULL).getValue();
		String topicTimestampStr = fields.get(EsSchema.ASSET_TOPIC_TPC_TIMESTAMP_FULL).getValue();
		
		KapuaTopic topic = new KapuaTopic(String.format("%s/%s/%s", account, asset, topicName));
		Date timestamp = (Date)EsUtils.convertToEdcObject("date", topicTimestampStr);
		topicInfo = new KapuaTopicInfo(topic, timestamp);
		return this;
	}

	public KapuaTopicInfo getTopicInfo() {
		return this.topicInfo;
	}
}
