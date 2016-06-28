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
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.eclipse.kapua.client.message.KapuaInvalidTopicException;
import org.eclipse.kapua.client.message.KapuaMessage;
import org.eclipse.kapua.client.message.KapuaTopic;
import org.eclipse.kapua.commons.cache.LocalCache;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.service.datastore.internal.config.KapuaDatastoreConfig;
import org.eclipse.kapua.service.datastore.internal.config.KapuaDatastoreConfigKeys;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsAssetDAO;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsAssetTopicDAO;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsDaoEvent;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsDaoListener;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsMessageDAO;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsTopicDAO;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsTopicMetricDAO;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsTypeDAO;
import org.eclipse.kapua.service.datastore.internal.model.MetricsIndexBy;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.index.engine.DocumentAlreadyExistsException;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO review signatures to reduce the number of params (use the same query object as servicebeans ?)

/**
 * @author stefano.morson
 * This class must be thread safe
 */
public class EsMessageAdapter {
	
	private class EventDispatcher implements EsDaoListener {

		private KapuaProgressListener progressListener;
		
		public void deregisterProgressListener(KapuaProgressListener progressListener) throws Exception {
			if (progressListener != this.progressListener)
				throw new Exception("Unable to deregister listener");
			
			this.progressListener = null;
		}

		public void registerProgressListener(KapuaProgressListener progressListener) throws Exception {
			if (this.progressListener != null)
				throw new Exception("A listener is already registered");
			
			this.progressListener = progressListener;
		}

		@Override
		public void notify(EsDaoEvent anEvent) {
			// TODO Auto-generated method stub	
		}
	}
	
    private static final Logger s_logger = LoggerFactory.getLogger(EsMessageAdapter.class);
	
	private final EsSchema esSchema;
	
	private final LocalCache<String, Boolean> topicCache;
	private final LocalCache<String, Boolean> topicMetricCache;
	private final LocalCache<String, Boolean> assetCache;
	private final LocalCache<String, Boolean> assetTopicCache;
	
	private final Object metadataUpdateSync;
	
	public EsMessageAdapter() {	

		esSchema = new EsSchema();
		
        KapuaDatastoreConfig  config = KapuaDatastoreConfig.getInstance();
        int expireAfter = config.getInt(KapuaDatastoreConfigKeys.CONFIG_CACHE_LOCAL_EXPIRE_AFTER);
        int sizeMax = config.getInt(KapuaDatastoreConfigKeys.CONFIG_CACHE_LOCAL_SIZE_MAXIMUM);

        topicCache = new LocalCache<String, Boolean>(sizeMax, expireAfter, false);
		topicMetricCache = new LocalCache<String, Boolean>(sizeMax, expireAfter, false);
		assetCache = new LocalCache<String, Boolean>(sizeMax, expireAfter, false);
		assetTopicCache = new LocalCache<String, Boolean>(sizeMax, expireAfter, false);	 
		
		metadataUpdateSync = new Object();
	}

    private static long msgCount = 0;
    private static long totTime = 0;
    
    private void updateIndividually(EsSchema.Metadata schemaMetadata, EsDocumentBuilder docBuilder) throws UnknownHostException, EsDatastoreException {
    	
    	String indexName = schemaMetadata.getPublicIndexName();
    	String messageTypeName = schemaMetadata.getMessageTypeName();
    	String edcIndexName = schemaMetadata.getPrivateIndexName();
    	String topicTypeName = schemaMetadata.getTopicTypeName();
    	String topicMetricTypeName = schemaMetadata.getTopicMetricTypeName();
    	String assetTypeName = schemaMetadata.getAssetTypeName();
    	String assetTopicTypeName = schemaMetadata.getAssetTopicTypeName();

    	Date startDate = new Date();

    	// Save message (the big one)
    	// TODO check response
    	EsMessageDAO.connection(EsClient.getcurrent())
    				.instance(indexName, messageTypeName)
				    .upsert(docBuilder.getMessageId(), docBuilder.getMessage());
    	
		// Save topic. Look up topic in the cache, and cache it if it doesn't exist
    	if (!this.topicCache.get(docBuilder.getTopicId())) {
    		
    		// The code is safe even without the synchronized block
    		// Synchronize in order to let the first thread complete its update
    		// then the others of the same type will find the cache updated and skip
    		// the update.
    		synchronized(this.metadataUpdateSync) {
    			if (!this.topicCache.get(docBuilder.getTopicId())) {
		    		UpdateResponse response = null;
		    		try {
						response = EsTopicDAO.connection(EsClient.getcurrent())
											 .instance(edcIndexName, topicTypeName)
											 .upsert(docBuilder.getTopicId(), docBuilder.getTopic());
		    			s_logger.debug(String.format("Upsert on topic succesfully executed [%s.%s, %s]", edcIndexName, topicTypeName, response.getId()));
			   		} catch (DocumentAlreadyExistsException exc) {   			
		    			s_logger.trace(String.format("Upsert failed because topic already exists [%s, %s]", docBuilder.getTopicId(), exc.getMessage()));
		    		}
			    	// Update cache if topic update is completed successfully
		   	     	this.topicCache.put(docBuilder.getTopicId(), true);
	    		}
    		}
    	}
    	
     	// Save topic metrics
     	EsTopicMetricDAO.connection(EsClient.getcurrent())
     					.instance(edcIndexName, topicMetricTypeName);
     	
     	BulkRequest bulkRequest = new BulkRequest();
    	List<EsTopicMetric> esTopicMetrics = docBuilder.getTopicMetrics();
    	if (esTopicMetrics != null) {
    		for(EsTopicMetric esTopicMetric:esTopicMetrics){
    			if (this.topicMetricCache.get(esTopicMetric.getId()))
    				continue;
//    			this.esTopicMetricDAO.upsert(esTopicMetric);
    			bulkRequest.add(EsTopicMetricDAO.connection(EsClient.getcurrent())
    											.instance(edcIndexName, topicMetricTypeName)
    											.getUpsertRequest(esTopicMetric));
    		}
    	}
    	
    	if (bulkRequest.numberOfActions() > 0) {
    		
    		// The code is safe even without the synchronized block
    		// Synchronize in order to let the first thread complete its update
    		// then the others of the same type will find the cache updated and skip
    		// the update.
    		synchronized(this.metadataUpdateSync) {
	    		BulkResponse response = EsTopicMetricDAO.connection(EsClient.getcurrent())
	    												.bulk(bulkRequest);
	    		
	    		BulkItemResponse[] itemResponses = response.getItems();
	    		if (itemResponses != null) {
	    			for (BulkItemResponse bulkItemResponse:itemResponses) {
	    				String topicMetricId = ((UpdateResponse)bulkItemResponse.getResponse()).getId();
	    				if (bulkItemResponse.isFailed()) {
	    					String failureMessage = bulkItemResponse.getFailureMessage();
	    					s_logger.trace(String.format("Upsert failed because topic metric already exists [%s, %s]", topicMetricId, failureMessage));
	    					continue;
	    				}
	    				
		    			s_logger.debug(String.format("Upsert on topic metric succesfully executed [%s.%s, %s]", edcIndexName, topicTypeName, topicMetricId));
	    				
	        			if (this.topicMetricCache.get(topicMetricId))
	        				continue;
	        			
	        	    	// Update cache if topic metric update is completed successfully
	    				this.topicMetricCache.put(topicMetricId, true);
	    			}
	    		}
    		}
    	}
    	
    	// Save asset
		if (!this.assetCache.get(docBuilder.getAssetId())) {

			// The code is safe even without the synchronized block
    		// Synchronize in order to let the first thread complete its update
    		// then the others of the same type will find the cache updated and skip
    		// the update.
    		synchronized(this.metadataUpdateSync) {
    			if (!this.assetCache.get(docBuilder.getAssetId())) {
					UpdateResponse response = null;
					try {
						response = EsAssetDAO.connection(EsClient.getcurrent())
											 .instance(edcIndexName, assetTypeName)
											 .upsert(docBuilder.getAssetId(), docBuilder.getAsset());
		    			s_logger.debug(String.format("Upsert on asset succesfully executed [%s.%s, %s]", edcIndexName, topicTypeName, response.getId()));
					} catch (DocumentAlreadyExistsException exc) {
						s_logger.trace(String.format("Upsert failed because asset already exists [%s, %s]", docBuilder.getAssetId(), exc.getMessage()));
					}
					// Update cache if asset update is completed successfully
					this.assetCache.put(docBuilder.getAssetId(), true);
    			}
    		}
		}

    	// Save asset topic
    	if (!this.assetTopicCache.get(docBuilder.getAssetTopicId())) {

    		// The code is safe even without the synchronized block
    		// Synchronize in order to let the first thread complete its update
    		// then the others of the same type will find the cache updated and skip
    		// the update.
    		synchronized(this.metadataUpdateSync) {
    	    	if (!this.assetTopicCache.get(docBuilder.getAssetTopicId())) {
		    		UpdateResponse response = null;
		    		try {
				    	response = EsAssetTopicDAO.connection(EsClient.getcurrent())
			    								  .instance(edcIndexName, assetTopicTypeName)
			    								  .upsert(docBuilder.getAssetTopicId(), docBuilder.getAssetTopic());
		    			s_logger.debug(String.format("Upsert on asset topic succesfully executed [%s.%s, %s]", edcIndexName, topicTypeName, response.getId()));
		    		} catch (DocumentAlreadyExistsException exc) {   			
		    			s_logger.trace(String.format("Upsert failed because asset topic already exists [%s, %s]", docBuilder.getAssetTopicId(), exc.getMessage()));
		    		}
			    	// Update cache if asset topic update is completed successfully
			    	this.assetTopicCache.put(docBuilder.getAssetTopicId(), true);
    	    	}
    		}
    	}
    	
    	Date endDate = new Date();
    	
        msgCount++;
        totTime+= endDate.getTime() - startDate.getTime();
        if (msgCount % 1000 == 0)
        	System.out.println(String.format("Elasticsearch update took: %d, messages: %d", totTime, msgCount));
    }

    private void updateBulk(EsSchema.Metadata schemaMetadata, EsDocumentBuilder docBuilder) throws UnknownHostException, EsDatastoreException {
    	
    	String indexName = schemaMetadata.getPublicIndexName();
    	String messageTypeName = schemaMetadata.getMessageTypeName();
    	String edcIndexName = schemaMetadata.getPrivateIndexName();
    	String topicTypeName = schemaMetadata.getTopicTypeName();
    	String topicMetricTypeName = schemaMetadata.getTopicMetricTypeName();
    	String assetTypeName = schemaMetadata.getAssetTypeName();
    	String assetTopicTypeName = schemaMetadata.getAssetTopicTypeName();

    	Date startDate = new Date();

    	// Save message (the big one)
    	UpdateRequest upsertReq = EsMessageDAO.connection(EsClient.getcurrent())
    										  .instance(indexName, messageTypeName)
    										  .getUpsertReq(docBuilder.getMessageId(), docBuilder.getMessage());
    	
    	BulkRequest bulkRequests = new BulkRequest();
    	bulkRequests.add(upsertReq);
    	
    	// Save topic
    	upsertReq = EsTopicDAO.connection(EsClient.getcurrent())
    						  .instance(edcIndexName, topicTypeName)
    						  .getUpsertRequest(docBuilder.getTopicId(), docBuilder.getTopic());
    	bulkRequests.add(upsertReq);
    	
     	// Save topic metrics
     	EsTopicMetricDAO.connection(EsClient.getcurrent())
     					.instance(edcIndexName, topicMetricTypeName);
     	
    	List<EsTopicMetric> esTopicMetrics = docBuilder.getTopicMetrics();
    	if (esTopicMetrics != null) {
    		for(EsTopicMetric esTopicMetric:esTopicMetrics){
    			upsertReq = EsTopicMetricDAO.connection(EsClient.getcurrent())
    										.getUpsertRequest(esTopicMetric);
    	    	bulkRequests.add(upsertReq);
    		}
    	}
    	
    	// Save asset
    	upsertReq = EsAssetDAO.connection(EsClient.getcurrent())
    						  .instance(edcIndexName, assetTypeName)
    						  .getUpsertRequest(docBuilder.getAssetId(), docBuilder.getAsset());
    	bulkRequests.add(upsertReq);

    	// Save asset topic
    	upsertReq = EsAssetTopicDAO.connection(EsClient.getcurrent())
    							   .instance(edcIndexName, assetTopicTypeName)
    							   .getUpsertRequest(docBuilder.getAssetTopicId(), docBuilder.getAssetTopic());
    	bulkRequests.add(upsertReq);
    	
    	BulkResponse bulkResponse = EsTypeDAO.connection(EsClient.getcurrent())
    										 .bulk(bulkRequests);
    	
    	Date endDate = new Date();
    	
        msgCount++;
        totTime+= endDate.getTime() - startDate.getTime();
        if (msgCount % 1000 == 0)
        	System.out.println(String.format("Update took: %d", totTime));
    }
    
	public String storeMessage(String accountName,
							 KapuaMessage message, 
							 int maxTopicDepth,
							 long indexedOn,
							 long receivedOn,
							 long ttl,
							 String forceUUID, 
							 LinkedHashMap<String, Long> timingProfile,
							 MetricsIndexBy indexBy) 
		throws IOException, ParseException, EsDatastoreException {

    	
    	// Extract schema metadata
    	EsSchema.Metadata schemaMetadata = this.esSchema.synch(accountName, indexedOn);
    	
    	Date indexedOnDt = new Date(indexedOn);
    	Date receivedOnDt = new Date(receivedOn);

    	String messageId;
		if ((forceUUID != null) && (!forceUUID.isEmpty()))
			messageId = forceUUID;
		else {
			UUID uuid = UUID.randomUUID();
			messageId = uuid.toString();
		}
    	
    	// Parse document
		EsDocumentBuilder docBuilder = new EsDocumentBuilder();
    	docBuilder.build(accountName, message, messageId, indexedOnDt, receivedOnDt, maxTopicDepth);
    	
    	Map<String, EsMetric> esMetrics = docBuilder.getMessageMetrics();
    	this.esSchema.updateMessageMappings(accountName, indexedOn, esMetrics);
    	
    	// TODO Investigate why update indivudual performs better than update bulk (!!!)
    	this.updateIndividually(schemaMetadata, docBuilder);
    	//this.updateBulk(schemaMetadata, docBuilder);    	
    	
    	return messageId;
	}
	
	public void deleteMessageById(String accountName,
			  					  String uuid) throws Exception {
		
    	String everyIndex = EsUtils.getAnyIndexName(accountName);
		EsMessageDAO.connection(EsClient.getcurrent())
					.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
					.deleteById(uuid);
	}

	public void deleteMessages(String accountName, 
							   String topic, 
							   int startYear,
							   int startWeek,
							   int endYear,
							   int endWeek,
							   int weeksTtl,
							   long ttl,
							   boolean purgeMetadata, 
							   KapuaProgressListener pl) throws Exception {
		
		EventDispatcher eventDispatcher = new EventDispatcher();
		
		try {
			
			eventDispatcher.registerProgressListener(pl);
			
			// Invariant
			boolean unlimitedRange = endYear == -1 && endWeek == -1 && startYear == -1 && endWeek == -1;			
			if (purgeMetadata && !unlimitedRange)
				throw new Exception("Purge can't be used in conjunction with a start and/or end dates");

	        //
	        // MessageDAO: prepare query
	        long endDate = KapuaDateUtils.getEdcSysDate().getTime();
	        long startDate = endDate - ttl;
	        int nWeeks = weeksTtl;
	        if ((!purgeMetadata) && (endYear != -1) && (endWeek != -1)) {
	            endDate = KapuaDateUtils.getStartOfWeek(endYear, endWeek + 1).getTime(); // +1, endWeek is included so calculate time of the end of the week;
	            if ((startYear != -1) && (startWeek != -1)) {
	                startDate = KapuaDateUtils.getStartOfWeek(startYear, startWeek).getTime();
	            }
	            double nDays = (double) (endDate - startDate) / (double) (24 * 60 * 60 * 1000);
	            nWeeks = (int) Math.ceil(nDays / 7.0);
	        }

	        
			boolean isDeleteByTopic = false;
			boolean isDeleteByAccount = false;
			boolean isAnyAccount = false;
			boolean isAnyAsset = false;
			boolean isAnySubtopic = false;
			String account = "";
			String asset = "";
			String semTopic = "";
			KapuaTopic kapuaTopic = null;
			
			// Determine the type of delete that is requested
			if (topic != null && !topic.isEmpty()) {
				kapuaTopic = new KapuaTopic(topic);
				account = kapuaTopic.getAccount();
				isAnyAccount = kapuaTopic.isAnyAccount();
				asset = kapuaTopic.getAsset();
				isAnyAsset = kapuaTopic.isAnyAsset();
				semTopic = kapuaTopic.getSemanticTopic();
				isAnySubtopic = kapuaTopic.isAnySubtopic();
				if (isAnySubtopic) {
					semTopic = kapuaTopic.getParentTopic();
					semTopic = semTopic == null ? "" : semTopic;
				}
				
				if (isAnyAccount && isAnyAsset && KapuaTopic.MULTI_LEVEL_WCARD.equals(semTopic))
					isDeleteByAccount = true; // +/+/# is equivalent to deleting by account
				else
					isDeleteByTopic = true;
				
			} else {
				isDeleteByAccount = true;
			}
	        
	    	String everyIndex = EsUtils.getAnyIndexName(accountName);
	    	
			if (isDeleteByTopic) {
				
				EsMessageDAO.connection(EsClient.getcurrent())
							.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
							.setListener(eventDispatcher)
							.deleteByTopic(asset, isAnyAsset, 
										   semTopic, isAnySubtopic,
										   startDate, endDate);
			} 
			
			if (isDeleteByAccount) {
				// TODO implement deletion of indexes for all the complete weeks between the start date
				// and the end date
				EsMessageDAO.connection(EsClient.getcurrent())
							.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
							.setListener(eventDispatcher)
							.deleteByAccount(startDate, endDate);
			}
		} finally {
			eventDispatcher.deregisterProgressListener(pl);
		}
	}

	public void deleteTopics(String accountName, int weeksTtl, long ttl, long endDate) throws Exception {		
        
		String esIndex = EsUtils.getAnyIndexName(accountName);
    	String edcIndex = EsUtils.getActualEdcIndexName(accountName, -1);
    	long startDate = endDate - ttl;
    	
    	KapuaTopic topic = new KapuaTopic(accountName, KapuaTopic.SINGLE_LEVEL_WCARD, KapuaTopic.MULTI_LEVEL_WCARD);
    	String asset = topic.getAsset();
    	boolean anyAsset = KapuaTopic.SINGLE_LEVEL_WCARD.equals(asset);
    	String semTopic = topic.getSemanticTopic();
    	boolean topicPrefix = KapuaTopic.MULTI_LEVEL_WCARD.equals(semTopic);
    	if (topicPrefix) {
    		semTopic = topic.getParentTopic();
    		semTopic = semTopic == null ? "" : semTopic;
    	}
    	
    	EventDispatcher eventDispatcher = new EventDispatcher();
		EsMessageDAO.connection(EsClient.getcurrent())
				.instance(esIndex, EsSchema.MESSAGE_TYPE_NAME)
				.setListener(eventDispatcher)
				.deleteByTopic(asset, anyAsset, semTopic, topicPrefix, startDate, endDate);
    	
		EsTopicMetricDAO.connection(EsClient.getcurrent())
				.instance(edcIndex, EsSchema.TOPIC_METRIC_TYPE_NAME)
				.setListener(eventDispatcher)
				.deleteByAccount(accountName, startDate, endDate);

		EsTopicDAO.connection(EsClient.getcurrent())
				.instance(edcIndex, EsSchema.TOPIC_TYPE_NAME)
				.setListener(eventDispatcher)
				.deleteByAccount(accountName, startDate, endDate);
	}

	public void deleteAssets(String accountName, long ttl, long endDate) throws Exception {

    	String edcIndex = EsUtils.getActualEdcIndexName(accountName, -1);
    	long startDate = endDate - ttl;
    	EventDispatcher eventDispatcher = new EventDispatcher();
		EsAssetTopicDAO.connection(EsClient.getcurrent())
				.instance(edcIndex, EsSchema.ASSET_TOPIC_TYPE_NAME)
				.setListener(eventDispatcher)
				.deleteByAccount(accountName, startDate, endDate);

		EsAssetDAO.connection(EsClient.getcurrent())
				.instance(edcIndex, EsSchema.ASSET_TYPE_NAME)
				.setListener(eventDispatcher)
				.deleteByAccount(startDate, endDate);
	}
	
	public KapuaListResult<KapuaMessage> findMessagesByTopic(String accountName,
										  		 String topic,
										  		 long start,
										  		 long end,
										  		 int indexOffset,
										  		 int limit,
										  		 boolean sortAscending,
										  		 MessageFetchStyle fetchStyle,
												 boolean askTotalCount) throws Exception {

		// get one plus (if there is one) to later get the next key value 
		int newLimit = limit + 1;
		
		KapuaTopic kapuaTopic = new KapuaTopic(topic);
		boolean isAnyAsset = kapuaTopic.isAnyAsset();
		String semTopic = kapuaTopic.getSemanticTopic();
		boolean isAnySubtopic = kapuaTopic.isAnySubtopic();
		if (isAnySubtopic) {
			semTopic = kapuaTopic.getParentTopic();
			semTopic = semTopic == null ? "" : semTopic;
		}
		
		kapuaTopic.getParentTopic();
    	String everyIndex = EsUtils.getAnyIndexName(accountName);
		SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
											.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
											.findByTopic(kapuaTopic.getAsset(),
														 isAnyAsset,
														 semTopic, 
														 isAnySubtopic,
														 start, 
														 end, 
														 indexOffset, 
														 newLimit, 
														 sortAscending,
												  		 fetchStyle);
		
		if (searchHits == null || searchHits.getHits().length == 0)
			return new KapuaListResult<KapuaMessage>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		List<KapuaMessage> edcMessages = new ArrayList<KapuaMessage>();
		KapuaMessageBuilder edcMsgBuilder = new KapuaMessageBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) { 
				KapuaMessage kapuaMessage = edcMsgBuilder.build(searchHit, fetchStyle).getMessage();
				edcMessages.add(kapuaMessage);
			}
			i++;
		}

		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHitsSize > limit) {
        	KapuaMessage edcLastMessage = edcMsgBuilder.build(searchHits.getHits()[searchHitsSize-1], fetchStyle).getMessage();
            nextKey = edcLastMessage.getTimestamp().getTime();
        }

        // TODO verifiy total count
        long totalCount = 0;
        if (askTotalCount) {
            // totalCount = MessageDAO.findByTopicCount(keyspace, topic, start, end); 
        	totalCount = searchHits.getTotalHits();
        }
        
        if (totalCount > Integer.MAX_VALUE)
        	throw new Exception("Total hits exceeds integer max value");
        
        KapuaListResult<KapuaMessage> result = new KapuaListResult<KapuaMessage>(nextKey, (int)totalCount);
        result.addAll(edcMessages);
        
        return result;
	}
	
	public KapuaListResult<KapuaMessage> findMessagesByAsset(String accountName,
														 String asset,
														 long start,
														 long end,
														 int indexOffset,
														 int limit, 
														 boolean sortAscending,
												  		 MessageFetchStyle fetchStyle,
														 boolean askTotalCount) throws Exception {

		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
        
		boolean isAnyAsset = false;
		if (KapuaTopic.SINGLE_LEVEL_WCARD.equals(asset))
			isAnyAsset = true;
		
    	String everyIndex = EsUtils.getAnyIndexName(accountName);
		SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
											.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
											.findByAsset(asset, 
														 isAnyAsset,
														 start, 
														 end, 
														 indexOffset, 
														 newLimit, 
														 sortAscending,
												  		 fetchStyle);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaMessage>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		List<KapuaMessage> edcMessages = new ArrayList<KapuaMessage>();
		KapuaMessageBuilder edcMsgBuilder = new KapuaMessageBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) {
				KapuaMessage kapuaMessage = edcMsgBuilder.build(searchHit, fetchStyle).getMessage();
				edcMessages.add(kapuaMessage);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHitsSize > limit) {
        	KapuaMessage edcLastMessage = edcMsgBuilder.build(searchHits.getHits()[searchHitsSize-1], fetchStyle).getMessage();
            nextKey = edcLastMessage.getTimestamp().getTime();
        }

        // TODO verifiy total count
        long totalCount = 0;
        if (askTotalCount) {
            // totalCount = MessageDAO.findByTopicCount(keyspace, topic, start, end); 
        	totalCount = searchHits.getTotalHits();
        }
        
        if (totalCount > Integer.MAX_VALUE)
        	throw new Exception("Total hits exceeds integer max value");
        
        KapuaListResult<KapuaMessage> result = new KapuaListResult<KapuaMessage>(nextKey, (int)totalCount);
        result.addAll(edcMessages);
        
        return result;
	}
	
	public KapuaListResult<KapuaMessage> findMessagesByAccount(String accountName,
											               long start,
											               long end,
											               int indexOffset,
											               int limit, 
														   boolean sortAscending,
													  	   MessageFetchStyle fetchStyle,
														   boolean askTotalCount) throws Exception {
		
		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
		
    	String everyIndex = EsUtils.getAnyIndexName(accountName);
		SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
											.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
											.findByAccount(accountName, 
														 start, 
														 end, 
														 indexOffset, 
														 newLimit, 
														 sortAscending,
												  		 fetchStyle);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaMessage>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		List<KapuaMessage> edcMessages = new ArrayList<KapuaMessage>();
		KapuaMessageBuilder edcMsgBuilder = new KapuaMessageBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) {
				KapuaMessage kapuaMessage = edcMsgBuilder.build(searchHit, fetchStyle).getMessage();
				edcMessages.add(kapuaMessage);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHitsSize > limit) {
        	KapuaMessage edcLastMessage = edcMsgBuilder.build(searchHits.getHits()[searchHitsSize-1], fetchStyle).getMessage();
            nextKey = edcLastMessage.getTimestamp().getTime();
        }

        // TODO verifiy total count
        long totalCount = 0;
        if (askTotalCount) {
            // totalCount = MessageDAO.findByTopicCount(keyspace, topic, start, end); 
        	totalCount = searchHits.getTotalHits();
        }
        
        if (totalCount > Integer.MAX_VALUE)
        	throw new Exception("Total hits exceeds integer max value");
        
        KapuaListResult<KapuaMessage> result = new KapuaListResult<KapuaMessage>(nextKey, (int)totalCount);
        result.addAll(edcMessages);
        
        return result;
	}
	
	public KapuaListResult<KapuaMessage> findMessagesById(String accountName, 
													  List<String> ids,
													  int limit,
												  	  MessageFetchStyle fetchStyle) throws Exception {

		String everyIndex = EsUtils.getAnyIndexName(accountName);
		SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
											.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
											.findById(accountName, 
													  ids,
												  	  fetchStyle);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaMessage>();

		List<KapuaMessage> edcMessages = new ArrayList<KapuaMessage>();
		KapuaMessageBuilder edcMsgBuilder = new KapuaMessageBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			KapuaMessage kapuaMessage = edcMsgBuilder.build(searchHit, fetchStyle).getMessage();
			edcMessages.add(kapuaMessage);
		}
		        
        KapuaListResult<KapuaMessage> result = new KapuaListResult<KapuaMessage>(null, (int)edcMessages.size());
        result.addAll(edcMessages);
        return result;
	}
	
	public KapuaListResult<KapuaMessage> findMessagesByMetric(String accountName,
														  String topic,
														  String metricName,
														  Class metricType,
											              long start,
											              long end,
											              Object min,
											              Object max,
											              int indexOffset,
											              int limit, 
														  boolean sortAscending,
													  	  MessageFetchStyle fetchStyle,
														  boolean askTotalCount) throws Exception {
		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
		
    	// Convert to es internal names
    	String esMetricName = EsUtils.normalizeMetricName(metricName);
    	String esTypeName = EsUtils.convertToEsType(metricType);
    	String esMetricValueQualifier = EsUtils.getMetricValueQualifier(esMetricName, esTypeName);
    	
    	// Decompose topic name
    	KapuaTopic kapuaTopic = new KapuaTopic(topic);
    	String asset = kapuaTopic.getAsset();
    	boolean isAnyAsset = kapuaTopic.isAnyAsset();
    	String semTopic = kapuaTopic.getSemanticTopic();
    	boolean isAnySubtopic = kapuaTopic.isAnySubtopic();
    	if (isAnySubtopic) {
    		semTopic = kapuaTopic.getParentTopic();
    		semTopic = semTopic == null ? "" : semTopic;
    	}

    	
    	String everyIndex = EsUtils.getAnyIndexName(accountName);
		SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
											.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
											.findByMetricValue(asset,
															   isAnyAsset,
															   semTopic,
															   isAnySubtopic,
															   esMetricValueQualifier,
															   start, 
															   end, 
															   min,
															   max,
															   indexOffset, 
															   newLimit, 
															   sortAscending,
															   fetchStyle);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaMessage>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		List<KapuaMessage> edcMessages = new ArrayList<KapuaMessage>();
		KapuaMessageBuilder edcMsgBuilder = new KapuaMessageBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) { 
				KapuaMessage kapuaMessage = edcMsgBuilder.build(searchHit, fetchStyle).getMessage();
				edcMessages.add(kapuaMessage);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHitsSize > limit) {
        	KapuaMessage edcLastMessage = edcMsgBuilder.build(searchHits.getHits()[searchHitsSize-1], fetchStyle).getMessage();
            nextKey = edcLastMessage.getTimestamp().getTime();
        }

        // TODO verifiy total count
        long totalCount = 0;
        if (askTotalCount) {
            // totalCount = MessageDAO.findByTopicCount(keyspace, topic, start, end); 
        	totalCount = searchHits.getTotalHits();
        }

        if (totalCount > Integer.MAX_VALUE)
        	throw new Exception("Total hits exceeds integer max value");
        
        // Set to 0 only for backward compatibility (it is done so in CX)
        KapuaListResult<KapuaMessage> result = new KapuaListResult<KapuaMessage>(nextKey, 0);
        result.addAll(edcMessages);
        
        return result;
	}
	
	public long findMessagesByAccountCount(String accountName) throws Exception {
    	
    	String everyIndex = EsUtils.getAnyIndexName(accountName);
		SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
											.instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
											.findByAccountCount(accountName);
		
		return searchHits.getTotalHits();
	}
	
	public KapuaListResult<KapuaMetricInfo<?>> findMetricsByTopic(String accountName,
												              String fullTopic,
												              int indexOffset,
												              int limit) throws Exception {

		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
        KapuaTopic kapuaTopic = new KapuaTopic(fullTopic);
        
    	String asset = kapuaTopic.getAsset();
    	boolean isAnyAsset = kapuaTopic.isAnyAsset();
    	String semTopic = kapuaTopic.getSemanticTopic();
    	boolean isAnySubtopic = kapuaTopic.isAnySubtopic();
    	if (isAnySubtopic) {
    		semTopic = kapuaTopic.getParentTopic();
    		semTopic = semTopic == null ? "" : semTopic;
    	}

    	String edcIndex = EsUtils.getActualEdcIndexName(accountName, -1);
		SearchHits searchHits = EsTopicMetricDAO.connection(EsClient.getcurrent())
										   .instance(edcIndex, EsSchema.TOPIC_METRIC_TYPE_NAME)
										   .findByTopic(asset,
												   		isAnyAsset,
												   		semTopic, 
												   		isAnySubtopic,
														indexOffset, 
														newLimit);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaMetricInfo<?>>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		List<KapuaMetricInfo<?>> edcMetricInfos = new ArrayList<KapuaMetricInfo<?>>();
		KapuaMetricInfoBuilder edcMetricInfoBuilder = new KapuaMetricInfoBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) {
				KapuaMetricInfo<?> edcMetricInfo = edcMetricInfoBuilder.build(searchHit).getKapuaMetricInfo();
				edcMetricInfos.add(edcMetricInfo);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHitsSize > limit) {
            nextKey = limit;
        }
        
        KapuaListResult<KapuaMetricInfo<?>> result = new KapuaListResult<KapuaMetricInfo<?>>(nextKey, edcMetricInfos.size());
        result.addAll(edcMetricInfos);
        
        return result;
	}
	
	public KapuaListResult<KapuaMetricValue> findMetricsByValue(String accountName,
												            String topic,
												            String metricName,
												            Class metricType,
												            long start,
												            long end,
												            Object min,
												            Object max,
												            int offset,
												            int limit,
												            boolean sortAscending) throws Exception {

		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
		
    	// Convert to es internal names
    	String esMetricName = EsUtils.normalizeMetricName(metricName);
    	String esTypeName = EsUtils.convertToEsType(metricType);
    	String esMetricValueQualifier = EsUtils.getMetricValueQualifier(esMetricName, esTypeName);
    	
    	// Decompose topic name
    	KapuaTopic kapuaTopic = new KapuaTopic(topic);
    	String asset = kapuaTopic.getAsset();
    	boolean isAnyAsset = kapuaTopic.isAnyAsset();
    	String semTopic = kapuaTopic.getSemanticTopic();
    	boolean isAnySubtopic = kapuaTopic.isAnySubtopic();
    	if (isAnySubtopic) {
    		semTopic = kapuaTopic.getParentTopic();
    		semTopic = semTopic == null ? "" : semTopic;
    	}
		
    	String everyIndex = EsUtils.getAnyIndexName(accountName);
		SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
										   .instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
										   .findMetricsByValue(asset,
												   			   isAnyAsset,
												   			   semTopic,
												   			   isAnySubtopic,
												   			   esMetricValueQualifier, 
												   			   start, 
												   			   end, 
												   			   min, 
												   			   max, 
												   			   offset, 
												   			   newLimit, 
												   			   sortAscending);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaMetricValue>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;
		
		KapuaListResult<KapuaMetricValue> edcMetricValues = new KapuaListResult<KapuaMetricValue>();
		KapuaMetricValueBuilder edcMetricValueBuilder = new KapuaMetricValueBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) {
				KapuaMetricValue edcMetricValue = edcMetricValueBuilder.build(searchHit, esMetricValueQualifier, metricType).getMetricValue();
				edcMetricValues.add(edcMetricValue);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHitsSize > limit) {
        	MetricColumnBuilder metricColumnBuilder = new MetricColumnBuilder();
        	nextKey = metricColumnBuilder.build(searchHits.getHits()[searchHitsSize - 1], esMetricValueQualifier, metricType)
					 .getMetricColumn();
        }
        
        KapuaListResult<KapuaMetricValue> result = new KapuaListResult<KapuaMetricValue>(nextKey);
        result.addAll(edcMetricValues);
        
        return result;
	}
	
	public KapuaListResult<KapuaMetricValue> findMetricsByTimestamp(String accountName,
													            String topic,
													            String metricName,
													            Class metricType,
													            long start,
													            long end,
													            int offset,
													            int limit,
													            boolean sortAscending) throws Exception {


		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
		
    	// Convert to es internal names
    	String esMetricName = EsUtils.normalizeMetricName(metricName);
    	String esTypeName = EsUtils.convertToEsType(metricType);
    	String esMetricValueQualifier = EsUtils.getMetricValueQualifier(esMetricName, esTypeName);
		
    	KapuaTopic kapuaTopic = new KapuaTopic(topic);
    	String asset = kapuaTopic.getAsset();
    	boolean isAnyAsset = kapuaTopic.isAnyAsset();
    	String semTopic = kapuaTopic.getSemanticTopic();
    	boolean isAnySubtopic = kapuaTopic.isAnySubtopic();
    	if (isAnySubtopic) {
    		semTopic = kapuaTopic.getParentTopic();
    		semTopic = semTopic == null ? "" : semTopic;
    	}
    	
    	String everyIndex = EsUtils.getAnyIndexName(accountName);
		SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
										   .instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
										   .findMetricsByTimestamp(asset,
												   				   isAnyAsset,
												   				   topic,
												   				   isAnySubtopic,
													   			   esMetricValueQualifier, 
													   			   start, 
													   			   end, 
													   			   offset, 
													   			   newLimit, 
													   			   sortAscending);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaMetricValue>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		KapuaListResult<KapuaMetricValue> edcMetricValues = new KapuaListResult<KapuaMetricValue>();
		KapuaMetricValueBuilder edcMetricValueBuilder = new KapuaMetricValueBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) {
				KapuaMetricValue edcMetricValue = edcMetricValueBuilder.build(searchHit, esMetricValueQualifier, metricType).getMetricValue();
				edcMetricValues.add(edcMetricValue);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHits.getTotalHits() > limit) {
        	MetricColumnBuilder metricColumnBuilder = new MetricColumnBuilder();
        	nextKey = metricColumnBuilder.build(searchHits.getHits()[searchHitsSize - 1], esMetricValueQualifier, metricType)
        								 .getMetricColumn();
        }
        
        KapuaListResult<KapuaMetricValue> result = new KapuaListResult<KapuaMetricValue>(nextKey);
        result.addAll(edcMetricValues);
        
        return result;
	}
	
	public KapuaListResult<KapuaTopicInfo> findTopicsByAccount(String accountName,
														   String topicPrefix,
											               int indexOffset,
											               int limit) throws Exception {

		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
        
    	boolean isAnySubtopic = true;
    	
    	// Manage some special cases such as 'acct_name/+/'
    	String semTopic = topicPrefix;
    	if (topicPrefix != null ) {
    		try {
    			KapuaTopic topic = new KapuaTopic(topicPrefix);
    			semTopic = topic.getSemanticTopic();
    		} catch (KapuaInvalidTopicException exc) {
    			String[] topicSplit = topicPrefix.split(KapuaTopic.TOPIC_SEPARATOR);
    			if (topicSplit.length == 3 && (topicSplit[3] == null && topicSplit[3].equals("")))
    					semTopic = "";
    			if (topicSplit.length == 2)
    				semTopic = "";
    		}
    	}
		
    	String edcIndex = EsUtils.getActualEdcIndexName(accountName, -1);
		SearchHits searchHits = EsTopicDAO.connection(EsClient.getcurrent())
										  .instance(edcIndex, EsSchema.TOPIC_TYPE_NAME)
										  .findByAccount(semTopic,
												  		 isAnySubtopic,
												  		 indexOffset,
												  		 newLimit);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaTopicInfo>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		List<KapuaTopicInfo> edcTopicInfos = new ArrayList<KapuaTopicInfo>();
		KapuaTopicInfoBuilder edcTopicInfoBuilder = new KapuaTopicInfoBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) {
				KapuaTopicInfo edcTopicInfo = edcTopicInfoBuilder.buildFromTopic(searchHit).getTopicInfo();
				edcTopicInfos.add(edcTopicInfo);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHits.getTotalHits() > limit) {
        	nextKey = limit;
        }
        
        KapuaListResult<KapuaTopicInfo> result = new KapuaListResult<KapuaTopicInfo>(nextKey, searchHitsSize);
        result.addAll(edcTopicInfos);
        
        return result;
	}
	
	public KapuaListResult<KapuaTopicInfo> findTopicsByAsset(String accountName,
														 String asset,
														 String topicPrefix,
														 int indexOffset,
														 int limit) throws Exception {


		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
        
        boolean isAnySubtopic = true;
        String semTopic = topicPrefix == null ? "" : topicPrefix;
        
        boolean isAnyAsset = false;
        if (KapuaTopic.SINGLE_LEVEL_WCARD.equals(asset))
        	isAnyAsset = true;
        
    	String edcIndex = EsUtils.getActualEdcIndexName(accountName, -1);
		SearchHits searchHits = EsAssetTopicDAO.connection(EsClient.getcurrent())
										  	   .instance(edcIndex, EsSchema.ASSET_TOPIC_TYPE_NAME)
										  	   .findTopicsByAsset(asset,
										  			   			  isAnyAsset,
										  			   			  semTopic,
											  			   		  isAnySubtopic,
													  		 	  indexOffset,
													  		 	  newLimit);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaTopicInfo>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		List<KapuaTopicInfo> edcTopicInfos = new ArrayList<KapuaTopicInfo>();
		KapuaTopicInfoBuilder edcTopicInfoBuilder = new KapuaTopicInfoBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) {
				KapuaTopicInfo edcTopicInfo = edcTopicInfoBuilder.buildFromAssetTopic(searchHit).getTopicInfo();
				edcTopicInfos.add(edcTopicInfo);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHits.getTotalHits() > limit) {
        	nextKey = limit;
        }
        
        KapuaListResult<KapuaTopicInfo> result = new KapuaListResult<KapuaTopicInfo>(nextKey, searchHitsSize);
        result.addAll(edcTopicInfos);
        
        return result;
	}
	
	public KapuaListResult<KapuaAssetInfo> findAssetsByAccount(String accountName,
														   int indexOffset,
														   int limit) throws Exception {

		// get one plus (if there is one) to later get the next key value 
        int newLimit = limit + 1;
		
    	String edcIndex = EsUtils.getActualEdcIndexName(accountName, -1);
		SearchHits searchHits = EsAssetDAO.connection(EsClient.getcurrent())
										  .instance(edcIndex, EsSchema.ASSET_TYPE_NAME)
										  .findByAccount(accountName,
												  		 indexOffset,
												  	     newLimit);
		
		if (searchHits == null || searchHits.getTotalHits() == 0)
			return new KapuaListResult<KapuaAssetInfo>();

		int i = 0;
		int searchHitsSize = searchHits.getHits().length;

		List<KapuaAssetInfo> edcAssetInfos = new ArrayList<KapuaAssetInfo>();
		KapuaAssetInfoBuilder edcAssetInfoBuilder = new KapuaAssetInfoBuilder();
		for(SearchHit searchHit:searchHits.getHits()) {
			if (i < limit) {
				KapuaAssetInfo edcAssetInfo = edcAssetInfoBuilder.build(searchHit).getAssetInfo();
				edcAssetInfos.add(edcAssetInfo);
			}
			i++;
		}
		
		// TODO check equivalence with CX with Pierantonio
		// TODO what is this nextKey
        Object nextKey = null;
        if (searchHits.getTotalHits() > limit) {
        	nextKey = limit;
        }
        
        KapuaListResult<KapuaAssetInfo> result = new KapuaListResult<KapuaAssetInfo>(nextKey, searchHitsSize);
        result.addAll(edcAssetInfos);
        
        return result;
	}

	/**
	 * @param accountName
	 * @param topic null topic means any topic
	 * @throws EsDatastoreException 
	 * @throws UnknownHostException 
	 * @throws EdcInvalidTopicException 
	 */
	public void resetCache(String accountName, String topic) 
			throws UnknownHostException, EsDatastoreException, KapuaInvalidTopicException {

        // Find all topics
		String everyIndex = EsUtils.getAnyIndexName(accountName);

		boolean isAnyAccount;
        String asset;
        boolean isAnyAsset;
        boolean isAssetToDelete = false;
        String semTopic;
        boolean isAnySubtopic;
        if (topic != null) {
        	
        	// determine if we should delete an asset if topic = account/asset/#
        	KapuaTopic kapuaTopic = new KapuaTopic(topic);
        	isAnyAccount = kapuaTopic.isAnyAccount();
        	asset = kapuaTopic.getAsset();
        	isAnyAsset = kapuaTopic.isAnyAsset();
        	semTopic = kapuaTopic.getSemanticTopic();
        	isAnySubtopic = kapuaTopic.isAnySubtopic();
        	if (isAnySubtopic) {
        		semTopic = kapuaTopic.getParentTopic();
        		semTopic = semTopic == null ? "" : kapuaTopic.getParentTopic();
        	}
        	
        	if (semTopic.isEmpty() && !isAnyAsset)
        		isAssetToDelete = true;
        }
        else {
        	isAnyAccount = true;
        	asset = "";
        	isAnyAsset = true;
        	semTopic = "";
        	isAnySubtopic = true;
        	isAssetToDelete = true;
        }
        
        // Remove topic metrics
        final int pageSize = 1000;
        int offset = 0;
        long totalHits = 1;
        while (totalHits > 0) {
	        SearchHits sh = EsTopicMetricDAO.connection(EsClient.getcurrent())
	        								.instance(everyIndex, EsSchema.TOPIC_METRIC_TYPE_NAME)
	        								.findByTopic(asset, 
	        											 isAnyAsset, 
	        											 semTopic, 
	        											 isAnySubtopic, 
	        											 offset, pageSize + 1);
	        totalHits = sh.getTotalHits();
	        long toBeProcessed =  totalHits > pageSize ? pageSize : totalHits;
	        for (int i = 0; i < toBeProcessed; i++) {
	        	String id = sh.hits()[i].getId();
	        	if (this.topicMetricCache.get(id))
	        		this.topicMetricCache.remove(id);
	        }
	        if (totalHits > pageSize + 1)
	        	offset += (pageSize + 1);
        }
        
        s_logger.debug(String.format("Removed cached topic metrics for [%s]", topic));
        
        EsTopicMetricDAO.connection(EsClient.getcurrent())
        				.instance(everyIndex, EsSchema.TOPIC_METRIC_TYPE_NAME)
        				.deleteByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);

        s_logger.debug(String.format("Removed topic metrics for [%s]", topic));
        //
        
        // Remove asset topic
        offset = 0;
        totalHits = 1;
        while (totalHits > 0) {
	        SearchHits sh = EsAssetTopicDAO.connection(EsClient.getcurrent())
										   .instance(everyIndex, EsSchema.ASSET_TOPIC_TYPE_NAME)
	        							   .findByTopic(asset, 
	        											isAnyAsset, 
	        											semTopic, 
	        											isAnySubtopic, 
	        											offset, pageSize + 1);
	        totalHits = sh.getTotalHits();
	        long toBeProcessed =  totalHits > pageSize ? pageSize : totalHits;
	        for (int i = 0; i < toBeProcessed; i++) {
	        	String id = sh.hits()[i].getId();
	        	if (this.assetTopicCache.get(id))
	        		this.assetTopicCache.remove(id);
	        }
	        if (totalHits == pageSize + 1)
	        	offset += (pageSize + 1);
        }
        
        s_logger.debug(String.format("Removed cached asset topics for [%s]", topic));
        
        EsAssetTopicDAO.connection(EsClient.getcurrent())
        			   .instance(everyIndex, EsSchema.ASSET_TOPIC_TYPE_NAME)
        			   .deleteByTopic(accountName, asset, isAnyAsset, semTopic, isAnySubtopic);
        
        s_logger.debug(String.format("Removed asset topics for [%s]", topic));
        //
        
        // Remove topic
        offset = 0;
        totalHits = 1;
        while (totalHits > 0) {
	        SearchHits sh = EsTopicDAO.connection(EsClient.getcurrent())
									  .instance(everyIndex, EsSchema.TOPIC_TYPE_NAME)
	        						  .findByTopic(accountName,
	        								  	   isAnyAccount,
	        								  	   asset, 
	        									   isAnyAsset, 
	        									   semTopic, 
	        									   isAnySubtopic, 
	        									   offset, pageSize + 1);
	        totalHits = sh.getTotalHits();
	        long toBeProcessed =  totalHits > pageSize ? pageSize : totalHits;
	        for (int i = 0; i < toBeProcessed; i++) {
	        	String id = sh.hits()[i].getId();
	        	if (this.topicCache.get(id))
	        		this.topicCache.remove(id);
	        }
	        if (totalHits == pageSize + 1)
	        	offset += (pageSize + 1);
        }
        
        s_logger.debug(String.format("Removed cached topics for [%s]", topic));
        
        EsTopicDAO.connection(EsClient.getcurrent())
        	      .instance(everyIndex, EsSchema.TOPIC_TYPE_NAME)
        		  .deleteByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);
        
        s_logger.debug(String.format("Removed topics for [%s]", topic));
        //

        // Remove asset
        if (isAssetToDelete) {
            offset = 0;
            totalHits = 1;
            while (totalHits > 0) {
    	        SearchHits sh = EsAssetDAO.connection(EsClient.getcurrent())
										  .instance(everyIndex, EsSchema.ASSET_TYPE_NAME)
    	        						  .findByAsset(accountName, 
    	        								  	   isAnyAccount,
    	        								  	   asset, 
    	        								  	   isAnyAsset,
    	        								  	   offset, 
    	        								  	   pageSize + 1);
    	        totalHits = sh.getTotalHits();
    	        long toBeProcessed =  totalHits > pageSize ? pageSize : totalHits;
    	        for (int i = 0; i < toBeProcessed; i++) {
    	        	String id = sh.hits()[i].getId();
    	        	if (this.assetCache.get(id))
    	        		this.assetCache.remove(id);
    	        }
    	        if (totalHits == pageSize + 1)
    	        	offset += (pageSize + 1);
            }
            
            s_logger.debug(String.format("Removed cached assets for [%s]", topic));
            
            EsAssetDAO.connection(EsClient.getcurrent())
        	      	  .instance(everyIndex, EsSchema.ASSET_TYPE_NAME)
            		  .deleteByAsset(asset, isAnyAsset);
            
            s_logger.debug(String.format("Removed assets for [%s]", topic));
        }
	}
}
