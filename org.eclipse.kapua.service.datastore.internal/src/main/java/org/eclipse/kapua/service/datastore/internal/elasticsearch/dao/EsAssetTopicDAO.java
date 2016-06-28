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
package org.eclipse.kapua.service.datastore.internal.elasticsearch.dao;

import java.net.UnknownHostException;

import org.eclipse.kapua.client.message.KapuaInvalidTopicException;
import org.eclipse.kapua.client.message.KapuaTopic;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsDatastoreException;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsSchema;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;

public class EsAssetTopicDAO {

	private EsTypeDAO esTypeDAO;
	
	private EsAssetTopicDAO() {}
	
	public EsAssetTopicDAO setListener(EsDaoListener daoListener) throws EsDatastoreException {
		this.esTypeDAO.setListener(daoListener);
		return this;
	}
	
	public EsAssetTopicDAO unsetListener(EsDaoListener daoListener) throws EsDatastoreException {
		this.esTypeDAO.unsetListener(daoListener);
		return this;
	}
	
	public static EsAssetTopicDAO connection(Client client) throws UnknownHostException {
		EsAssetTopicDAO esAssetTopicDAO = new EsAssetTopicDAO();
		esAssetTopicDAO.esTypeDAO = EsTypeDAO.connection(client);
		return esAssetTopicDAO;
	}
	
	public EsAssetTopicDAO instance(String indexName, String typeName) {
		this.esTypeDAO.instance(indexName, typeName);
		return this;
	}
	
	public BoolQueryBuilder getQueryByAsset(String asset, boolean isAnyAsset) {
		
		BoolQueryBuilder boolQuery = null;
		if (!isAnyAsset) {
			QueryBuilder assetQuery = QueryBuilders.termQuery(EsSchema.ASSET_TOPIC_AS_NAME, asset);
			boolQuery = QueryBuilders.boolQuery().must(assetQuery);
		}
		
		if (boolQuery == null)
			boolQuery = QueryBuilders.boolQuery();
		
		return boolQuery;
	}
	
	public BoolQueryBuilder getQueryByTopic(String asset, 
									  boolean isAnyAsset, 
									  String semTopic, 
									  boolean isAnySubtopic) {
		
		// Asset clauses
		QueryBuilder assetQuery = null;
		if (!isAnyAsset) {
			assetQuery = QueryBuilders.termQuery(EsSchema.ASSET_TOPIC_AS_NAME, asset);
		}
		
		// Topic clauses
		QueryBuilder topicQuery = null;
		if (isAnySubtopic) {
			topicQuery = QueryBuilders.prefixQuery(EsSchema.ASSET_TOPIC_TPC_NAME_FULL, semTopic);
		} else { 
			topicQuery = QueryBuilders.termQuery(EsSchema.ASSET_TOPIC_TPC_NAME_FULL, semTopic);	
		}
		
		// Composite clause
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (assetQuery != null)
			boolQuery.must(assetQuery);
		
		boolQuery.must(topicQuery);
		//
		
		return boolQuery;
	}
	
	public BoolQueryBuilder getQueryByTopicAndDate(String asset, 
									  boolean isAnyAsset, 
									  String semTopic, 
									  boolean isAnySubtopic,
									  long start, 
									  long end) {
		
		
		// Composite clause
		BoolQueryBuilder boolQuery = this.getQueryByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);		
		
		// Timestamp clauses
		QueryBuilder dateQuery = QueryBuilders.rangeQuery(EsSchema.ASSET_TOPIC_TPC_TIMESTAMP_FULL).from(start).to(end);
		boolQuery.must(dateQuery);
		//
		
		return boolQuery;
	}
	
	public UpdateRequest getUpsertRequest(String id, XContentBuilder esAsset) {
		return this.esTypeDAO.getUpsertRequest(id, esAsset);
	}
	
	public UpdateResponse upsert(String id, XContentBuilder esAsset) {
		return this.esTypeDAO.upsert(id, esAsset);
	}
	
	public UpdateResponse update(String id, XContentBuilder esAsset) {
		return this.esTypeDAO.update(id, esAsset);
	}
	
	public void deleteByQuery(BoolQueryBuilder boolQuery) {		
		this.esTypeDAO.deleteByQuery(boolQuery);
	}
	
	public void deleteByTopic(String accountName, String asset, boolean isAnyAsset, String semTopic, boolean isAnySubtopic) throws KapuaInvalidTopicException {		
		this.deleteByQuery(this.getQueryByTopic(asset, isAnyAsset, semTopic, isAnySubtopic));		
	}
	
	public void deleteByAccount(String accountName, long start, long end) throws KapuaInvalidTopicException {
		
    	KapuaTopic topic = new KapuaTopic(accountName, KapuaTopic.SINGLE_LEVEL_WCARD, KapuaTopic.MULTI_LEVEL_WCARD);
    	String asset = topic.getAsset();
    	boolean anyAsset = KapuaTopic.SINGLE_LEVEL_WCARD.equals(asset);
    	String semTopic = topic.getSemanticTopic();
    	boolean topicPrefix = KapuaTopic.MULTI_LEVEL_WCARD.equals(semTopic);
    	if (topicPrefix) {
    		semTopic = topic.getParentTopic();
    		semTopic = semTopic == null ? "" : semTopic;
    	}
		
		this.deleteByQuery(this.getQueryByTopicAndDate(asset, anyAsset, semTopic, topicPrefix, start, end));		
	}
	
	public SearchHits findTopicsByAsset(String asset,
										boolean isAnyAsset,
										String topicPrefix,
										boolean isAnySubtopic,
										int offset,
										int limit) {
		
		long timeout = EsUtils.getQueryTimeout();

		QueryBuilder boolQuery = this.getQueryByTopic(asset, isAnyAsset, topicPrefix, isAnySubtopic);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(false)
										.addFields(EsSchema.ASSET_TOPIC_AS_NAME, 
												   EsSchema.ASSET_TOPIC_ACCOUNT, 
												   EsSchema.ASSET_TOPIC_TPC_NAME_FULL, 
												   EsSchema.ASSET_TOPIC_TPC_TIMESTAMP_FULL)
										.setQuery(boolQuery)
										.setFrom(offset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(timeout));
		
		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public SearchHits findByAsset(String asset,
								  boolean isAnyAsset,
								  int offset,
								  int limit) {
		
		long timeout = EsUtils.getQueryTimeout();
		
		BoolQueryBuilder boolQuery = this.getQueryByAsset(asset, isAnyAsset);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(false)
										.addFields(EsSchema.ASSET_TOPIC_AS_NAME, 
												   EsSchema.ASSET_TOPIC_ACCOUNT, 
												   EsSchema.ASSET_TOPIC_TPC_NAME_FULL, 
												   EsSchema.ASSET_TOPIC_TPC_TIMESTAMP_FULL)
										.setQuery(boolQuery)
										.setFrom(offset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(timeout));
		
		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public SearchHits findByTopic(String asset,
								   boolean isAnyAsset,
								   String semTopic,
								   boolean isAnySubtopic,
								   int offset,
								   int limit) {
		
		long timeout = EsUtils.getQueryTimeout();
		
		BoolQueryBuilder boolQuery = this.getQueryByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(false)
										.addFields(EsSchema.ASSET_TOPIC_AS_NAME, 
												   EsSchema.ASSET_TOPIC_ACCOUNT, 
												   EsSchema.ASSET_TOPIC_TPC_NAME_FULL, 
												   EsSchema.ASSET_TOPIC_TPC_TIMESTAMP_FULL)
										.setQuery(boolQuery)
										.setFrom(offset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(timeout));
		
		SearchHits searchHits = response.getHits();
		return searchHits;
	}
}
