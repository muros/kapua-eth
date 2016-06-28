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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;

public class EsDocumentBuilder {
	
    private static final Logger s_logger = LoggerFactory.getLogger(EsDocumentBuilder.class);

	private String messageId;
	private XContentBuilder message;
	private Map<String, Object> messageMap;
	
	private String topicId;
	private XContentBuilder topic;
	private List<EsTopicMetric> topicMetrics;
	private String assetId;
	private XContentBuilder asset;
	private String assetTopicId;
	private XContentBuilder assetTopic;
	private Map<String, EsMetric> messageMetrics;
	
	private void init() {

		messageId = null;;
		message = null;
		messageMap = null;
		
		topicId  = null;
		topic = null;
		topicMetrics = null;
		assetId = null;;
		asset = null;
		assetTopicId = null;
		assetTopic = null;
		messageMetrics = null;
	}

	private String getHashCode(String aString) {
		byte[] hashCode =  Hashing.sha256()
		        				  .hashString(aString, StandardCharsets.UTF_8)
		        				  .asBytes();
		
		return Base64.encodeBytes(hashCode);
		//return aString;
	}
	
	private String getTopicKey(String topicFullName) {
		return this.getHashCode(topicFullName);
	}
	
	private String getAssetKey(String accountName, String assetName) {
		String assetFullName = String.format("%s/%s", accountName, assetName);
		String assetHashCode = this.getHashCode(assetFullName);
		return assetHashCode;
	}
	
	private String getTopicMetricKey(String topicFullName, String metricMappedName) {
		String topicMetricFullName = String.format("%s/%s", topicFullName, metricMappedName);
		String topicMetricHashCode = this.getHashCode(topicMetricFullName);
		return topicMetricHashCode;
	}
	
	private XContentBuilder getAssetBuilder(String asset, String msgId, Date msgTimestamp, String account) throws IOException {
		
		XContentBuilder builder = XContentFactory.jsonBuilder()
			    .startObject()
			        .field(EsSchema.ASSET_NAME, asset)
			        .field(EsSchema.ASSET_MESSAGE_ID, msgId)
			        .field(EsSchema.ASSET_TIMESTAMP, msgTimestamp)
			        .field(EsSchema.ASSET_ACCOUNT, account)
			    .endObject();
		
		return builder;
	}

	private XContentBuilder getAssetTopicBuilder(String asset, String account, String topic, String msgId, Date msgTimestamp) throws IOException {
		
		XContentBuilder builder = XContentFactory.jsonBuilder()
			    .startObject()
			        .field(EsSchema.ASSET_TOPIC_AS_NAME, asset)
			        .field(EsSchema.ASSET_TOPIC_ACCOUNT, account)
			        .startObject(EsSchema.ASSET_TOPIC_TPC)
		        		.field(EsSchema.ASSET_TOPIC_TPC_NAME, topic)
		        		.field(EsSchema.ASSET_TOPIC_TPC_TIMESTAMP, msgTimestamp)
		        		.field(EsSchema.ASSET_TOPIC_TPC_MSG_ID, msgId)
				    .endObject()
			    .endObject();
		
		return builder;
	}

	private XContentBuilder getTopicBuilder(String semTopic, String msgId, Date msgTimestamp, String asset, String account) throws IOException {
		
		XContentBuilder builder = XContentFactory.jsonBuilder()
				.startObject()
					.field(EsSchema.TOPIC_SEM_NAME, semTopic)
					.field(EsSchema.TOPIC_TIMESTAMP, msgTimestamp)
					.field(EsSchema.TOPIC_ASSET, asset)
					.field(EsSchema.TOPIC_ACCOUNT, account)
		        	.field(EsSchema.TOPIC_MESSAGE_ID, msgId)
				.endObject();
		
		return builder;
	}
	
	private XContentBuilder getTopicMetricBuilder(String account, String asset, String topic, String metricName, Object value, Date msgTimestamp, String msgId) throws IOException {
		
		XContentBuilder builder = XContentFactory.jsonBuilder()
				.startObject()
					.field(EsSchema.TOPIC_METRIC_ACCOUNT, account)
					.field(EsSchema.TOPIC_METRIC_ASSET, asset)
					.field(EsSchema.TOPIC_METRIC_SEM_NAME, topic)
					.startObject(EsSchema.TOPIC_METRIC_MTR)
						.field(EsSchema.TOPIC_METRIC_MTR_NAME, metricName)
						.field(EsSchema.TOPIC_METRIC_MTR_TYPE, EsUtils.getEsTypeFromValue(value))
						.field(EsSchema.TOPIC_METRIC_MTR_VALUE, value)
						.field(EsSchema.TOPIC_METRIC_MTR_TIMESTAMP, msgTimestamp)
						.field(EsSchema.TOPIC_METRIC_MTR_MSG_ID, msgId)
					.endObject()
				.endObject();
		
		return builder;
	}
	
	private static Map<String, Object> mapBuilder = null;
	private static Map<String, Object> position = null;
	
	private Map<String, Object> getMessageBuilderMap(String accountName,
											  KapuaMessage message, String asset, String topicFull,
											  String semTopic, String[] topicParts, String messageId,
											  Date indexedOn, Date receivedOn) throws IOException, ParseException {

		if (mapBuilder == null)
			/*Map<String, Object>*/ mapBuilder = new HashMap<String, Object>();
		
			mapBuilder.put(EsSchema.MESSAGE_TIMESTAMP, indexedOn);
			mapBuilder.put(EsSchema.MESSAGE_RECEIVED_ON, receivedOn);
			mapBuilder.put(EsSchema.MESSAGE_IP_ADDRESS, "127.0.0.1");
			mapBuilder.put(EsSchema.MESSAGE_ACCOUNT, accountName);
			mapBuilder.put(EsSchema.MESSAGE_AS_NAME, asset);
			mapBuilder.put(EsSchema.MESSAGE_SEM_TOPIC, semTopic);
//			mapBuilder.put(EsSchema.MESSAGE_TOPIC_PARTS, topicParts);
		
		KapuaPayload edcPayload = message.getKapuaPayload();
		if (edcPayload == null) {
			mapBuilder.remove(EsSchema.MESSAGE_COLLECTED_ON);
			mapBuilder.remove(EsSchema.MESSAGE_POS);
			mapBuilder.remove(EsSchema.MESSAGE_BODY);
			mapBuilder.remove(EsSchema.MESSAGE_MTR);
			return mapBuilder;
		}

		mapBuilder.put(EsSchema.MESSAGE_COLLECTED_ON, edcPayload.getTimestamp());
		
		KapuaPosition edcPosition = edcPayload.getPosition();
		if (edcPosition != null) {
			
			Map<String, Object> location = new HashMap<String, Object>();
			location.put("lon", edcPosition.getLongitude());
			location.put("lat", edcPosition.getLatitude());
			
			if (position == null)
				/*Map<String, Object>*/ position = new HashMap<String, Object>();
			
			position.put(EsSchema.MESSAGE_POS_LOCATION, location);
			position.put(EsSchema.MESSAGE_POS_ALT, edcPosition.getAltitude());
			position.put(EsSchema.MESSAGE_POS_PRECISION, edcPosition.getPrecision());
			position.put(EsSchema.MESSAGE_POS_HEADING, edcPosition.getHeading());
			position.put(EsSchema.MESSAGE_POS_SPEED, edcPosition.getSpeed());
			position.put(EsSchema.MESSAGE_POS_TIMESTAMP, edcPosition.getTimestamp());
			position.put(EsSchema.MESSAGE_POS_SATELLITES, edcPosition.getSatellites());
			position.put(EsSchema.MESSAGE_POS_STATUS, edcPosition.getStatus());
			mapBuilder.put(EsSchema.MESSAGE_POS, position);
		} else {
			mapBuilder.remove(EsSchema.MESSAGE_POS);			
		}

		mapBuilder.put(EsSchema.MESSAGE_BODY, edcPayload.getBody());

		Map<String, EsMetric> metricMappings = new HashMap<String, EsMetric>();
		List<EsTopicMetric> esTopicMetrics = new ArrayList<EsTopicMetric>();
		
		Map<String, Object> edcMetrics = edcPayload.metrics();
		if (edcMetrics != null) {

			Map<String, Object> metrics = new HashMap<String, Object>();
			String[] metricNames = edcMetrics.keySet().toArray(new String[] {});
			for (String edcMetricName:metricNames) {
				
				Object metricValue = edcMetrics.get(edcMetricName);
				//////////////////////
				//Sanitize field names. '.' is not allowed
				String esMetricName = EsUtils.normalizeMetricName(edcMetricName);
				String esType = EsUtils.getEsTypeFromValue(metricValue);
				String esTypeAcronim = EsUtils.getEsTypeAcronym(esType);
				EsMetric esMetric = new EsMetric();
				esMetric.setName(esMetricName);
				esMetric.setType(esType);
				//////////////////////

				Map<String, Object> field = new HashMap<String, Object>();
				field.put(esTypeAcronim, metricValue);
				metrics.put(esMetricName, field);

				// each metric is potentially a dynamic field so report it a new mapping
				String mappedName = EsUtils.getMetricValueQualifier(esMetricName, esType);
				metricMappings.put(mappedName, esMetric);
				
				EsTopicMetric topicMetric = new EsTopicMetric();
				String topicMetricId = getTopicMetricKey(topicFull, mappedName);
				topicMetric.setId(topicMetricId);
				
				// TODO retrieve the uuid field
				topicMetric.setContent(this.getTopicMetricBuilder(accountName, asset, semTopic, mappedName, metricValue, indexedOn, messageId));
				esTopicMetrics.add(topicMetric);
			}
			mapBuilder.put(EsSchema.MESSAGE_MTR, metrics);
		} else {
			mapBuilder.remove(EsSchema.MESSAGE_MTR);			
		}
		
		
		this.setMessageMetrics(metricMappings);
//		this.setTopicMetrics(esTopicMetrics);
		return mapBuilder;
	}
	
	private XContentBuilder getMessageBuilder(String accountName,
											  KapuaMessage message, String asset, String topicFull,
											  String semTopic, String[] topicParts, String messageId,
											  Date indexedOn, Date receivedOn) throws IOException, ParseException {
		
		XContentBuilder messageBuilder = XContentFactory.jsonBuilder()
			    .startObject()
			        .field(EsSchema.MESSAGE_TIMESTAMP, indexedOn)
					.field(EsSchema.MESSAGE_RECEIVED_ON, receivedOn) //TODO Which field ??
			        .field(EsSchema.MESSAGE_IP_ADDRESS, "127.0.0.1")
			        .field(EsSchema.MESSAGE_ACCOUNT, accountName)
			        .field(EsSchema.MESSAGE_AS_NAME, asset)
			        .field(EsSchema.MESSAGE_SEM_TOPIC, semTopic)
			        .field(EsSchema.MESSAGE_TOPIC_PARTS, topicParts);
		
		KapuaPayload edcPayload = message.getKapuaPayload();
		if (edcPayload == null) {
			messageBuilder.endObject();
			return messageBuilder;
		}

		messageBuilder.field(EsSchema.MESSAGE_COLLECTED_ON, edcPayload.getTimestamp());
		
		KapuaPosition edcPosition = edcPayload.getPosition();
		if (edcPosition != null) {
			
			Map<String, Object> location = new HashMap<String, Object>();
			location.put("lon", edcPosition.getLongitude());
			location.put("lat", edcPosition.getLatitude());
			
			Map<String, Object> position = new HashMap<String, Object>();
			position.put(EsSchema.MESSAGE_POS_LOCATION, location);
			position.put(EsSchema.MESSAGE_POS_ALT, edcPosition.getAltitude());
			position.put(EsSchema.MESSAGE_POS_PRECISION, edcPosition.getPrecision());
			position.put(EsSchema.MESSAGE_POS_HEADING, edcPosition.getHeading());
			position.put(EsSchema.MESSAGE_POS_SPEED, edcPosition.getSpeed());
			position.put(EsSchema.MESSAGE_POS_TIMESTAMP, edcPosition.getTimestamp());
			position.put(EsSchema.MESSAGE_POS_SATELLITES, edcPosition.getSatellites());
			position.put(EsSchema.MESSAGE_POS_STATUS, edcPosition.getStatus());
			messageBuilder.field(EsSchema.MESSAGE_POS, position);
		}

		messageBuilder.field(EsSchema.MESSAGE_BODY, edcPayload.getBody());

		Map<String, EsMetric> metricMappings = new HashMap<String, EsMetric>();
		List<EsTopicMetric> esTopicMetrics = new ArrayList<EsTopicMetric>();
		
		Map<String, Object> edcMetrics = edcPayload.metrics();
		if (edcMetrics != null) {

			Map<String, Object> metrics = new HashMap<String, Object>();
			String[] metricNames = edcMetrics.keySet().toArray(new String[] {});
			for (String edcMetricName:metricNames) {
				
				Object metricValue = edcMetrics.get(edcMetricName);
				//////////////////////
				//Sanitize field names. '.' is not allowed
				String esMetricName = EsUtils.normalizeMetricName(edcMetricName);
				String esType = EsUtils.getEsTypeFromValue(metricValue);
				String esTypeAcronim = EsUtils.getEsTypeAcronym(esType);
				EsMetric esMetric = new EsMetric();
				esMetric.setName(esMetricName);
				esMetric.setType(esType);
				//////////////////////

				Map<String, Object> field = new HashMap<String, Object>();
				field.put(esTypeAcronim, metricValue);
				metrics.put(esMetricName, field);

				// each metric is potentially a dynamic field so report it a new mapping
				String mappedName = EsUtils.getMetricValueQualifier(esMetricName, esType);
				metricMappings.put(mappedName, esMetric);
				
				EsTopicMetric topicMetric = new EsTopicMetric();
				String topicMetricId = getTopicMetricKey(topicFull, mappedName);
				topicMetric.setId(topicMetricId);
				
				// TODO retrieve the uuid field
				topicMetric.setContent(this.getTopicMetricBuilder(accountName, asset, semTopic, mappedName, metricValue, indexedOn, messageId));
				esTopicMetrics.add(topicMetric);
			}
			messageBuilder.field(EsSchema.MESSAGE_MTR, metrics);
		}
		
		messageBuilder.endObject();
		
		this.setMessageMetrics(metricMappings);
		this.setTopicMetrics(esTopicMetrics);
		return messageBuilder;
	}
	
	public EsDocumentBuilder clear() {
		this.init();
		return this;
	}
	
	public EsDocumentBuilder build(String accountName, KapuaMessage message, String messageId, Date indexedOn, Date receivedOn, int maxTopicDepth) throws ParseException, IOException {

		assert accountName != null : "Account name must be supplied.";
		assert message != null : "Message must be supplied.";
		assert messageId != null : "Message ID must be supplied.";
		assert !messageId.isEmpty() : "Message ID must be non empty.";
		assert maxTopicDepth > 0 : "Max topic depth must be greater than zero.";
		
		String topicFull = message.getKapuaTopic().getFullTopic();
		String asset = message.getKapuaTopic().getAsset();
		String semTopic = message.getKapuaTopic().getSemanticTopic();
		String[] topicParts = message.getKapuaTopic().getTopicParts();
		
		String topicId = this.getTopicKey(topicFull);
		this.setTopicId(topicId);
		this.setTopic(this.getTopicBuilder(semTopic, messageId, indexedOn, asset, accountName));
		
		String assetId = this.getAssetKey(accountName, asset);
		this.setAssetId(assetId);
		this.setAsset(this.getAssetBuilder(asset, messageId, indexedOn, accountName));
		
		this.setAssetTopicId(topicId);
		this.setAssetTopic(this.getAssetTopicBuilder(asset, accountName, semTopic, messageId, indexedOn));
		
		XContentBuilder messageBuilder = this.getMessageBuilder(accountName,
				message, asset, topicFull, semTopic, topicParts, messageId,
				indexedOn, receivedOn);
		
//		Map<String, Object> mapBuilder = this.getMessageBuilderMap(accountName,
//				message, asset, topicFull, semTopic, topicParts, messageId,
//				indexedOn, receivedOn);
		
		this.setMessageId(messageId);
		this.setMessage(messageBuilder);
		return this;
	}
	
	public String getMessageId() {
		return messageId;
	}
	private void setMessageId(String esMessageId) {
		this.messageId = esMessageId;
	}
	public XContentBuilder getMessage() {
		return message;
	}
	private void setMessage(XContentBuilder esMessage) {
		this.message = esMessage;
	}
	public Map<String, Object> getMessageMap() {
		return messageMap;
	}
	private void setMessageMap(Map<String, Object> esMessage) {
		this.messageMap = esMessage;
	}
	public String getTopicId() {
		return topicId;
	}
	private void setTopicId(String esTopicId) {
		this.topicId = esTopicId;
	}
	public XContentBuilder getTopic() {
		return topic;
	}
	private void setTopic(XContentBuilder esTopic) {
		this.topic = esTopic;
	}
	public List<EsTopicMetric> getTopicMetrics() {
		return topicMetrics;
	}
	private void setTopicMetrics(List<EsTopicMetric> esTopicMetrics) {
		this.topicMetrics = esTopicMetrics;
	}
	public String getAssetId() {
		return assetId;
	}
	private void setAssetId(String esAssetId) {
		this.assetId = esAssetId;
	}
	public XContentBuilder getAsset() {
		return asset;
	}
	private void setAsset(XContentBuilder esAsset) {
		this.asset = esAsset;
	}
	public String getAssetTopicId() {
		return assetTopicId;
	}
	private void setAssetTopicId(String esAssetTopicId) {
		this.assetTopicId = esAssetTopicId;
	}
	public XContentBuilder getAssetTopic() {
		return assetTopic;
	}
	private void setAssetTopic(XContentBuilder esAssetTopic) {
		this.assetTopic = esAssetTopic;
	}
	public Map<String, EsMetric> getMessageMetrics() {
		return messageMetrics;
	}
	private void setMessageMetrics(Map<String, EsMetric> messageMetrics) {
		this.messageMetrics = messageMetrics;
	}
}
