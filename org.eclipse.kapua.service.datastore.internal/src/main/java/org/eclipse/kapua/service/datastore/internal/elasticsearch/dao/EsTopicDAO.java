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

public class EsTopicDAO {

	private EsTypeDAO esTypeDAO;
	
	private EsTopicDAO() {}
	
	public EsTopicDAO setListener(EsDaoListener daoListener) throws EsDatastoreException {
		this.esTypeDAO.setListener(daoListener);
		return this;
	}
	
	public EsTopicDAO unsetListener(EsDaoListener daoListener) throws EsDatastoreException {
		this.esTypeDAO.unsetListener(daoListener);
		return this;
	}
	
	public static EsTopicDAO connection(Client client) throws UnknownHostException {
		EsTopicDAO esTopicDAO = new EsTopicDAO();
		esTopicDAO.esTypeDAO = EsTypeDAO.connection(client);
		return esTopicDAO;
	}
	
	public EsTopicDAO instance(String indexName, String typeName) {
		this.esTypeDAO.instance(indexName, typeName);
		return this;
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
	
	public BoolQueryBuilder getQueryBtTopic(String account,
											boolean isAnyAccount,
											String asset, 
										    boolean isAnyAsset, 
										    String semTopic, 
										    boolean isAnySubtopic) {
		
		QueryBuilder accountQuery = null;
		if (!isAnyAccount)
			accountQuery = QueryBuilders.termQuery(EsSchema.TOPIC_ACCOUNT, account);
		
		// Asset clauses
		QueryBuilder assetQuery = null;
		if (!isAnyAsset) {
			assetQuery = QueryBuilders.termQuery(EsSchema.TOPIC_ASSET, asset);
		}
		
		// Topic clauses
		QueryBuilder topicQuery = null;
		if (isAnySubtopic) {
			topicQuery = QueryBuilders.prefixQuery(EsSchema.TOPIC_SEM_NAME, semTopic);
		} else { 
			topicQuery = QueryBuilders.termQuery(EsSchema.TOPIC_SEM_NAME, semTopic);	
		}
		
		// Composite clause
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		
		if (accountQuery != null)
			boolQuery.must(accountQuery);
		
		if (assetQuery != null)
			boolQuery.must(assetQuery);
		
		boolQuery.must(topicQuery);
		
		return boolQuery;
	}
	
	public BoolQueryBuilder getQueryBtTopicAndDate(String account,
												  boolean isAnyAccount,
												  String asset, 
												  boolean isAnyAsset, 
												  String semTopic, 
												  boolean isAnySubtopic,
												  long start, 
												  long end) {
		
		BoolQueryBuilder boolQuery = this.getQueryBtTopic(account, isAnyAccount, asset, isAnyAsset, semTopic, isAnySubtopic);
		
		// Timestamp clauses
		QueryBuilder dateQuery = QueryBuilders.rangeQuery(EsSchema.TOPIC_TIMESTAMP).from(start).to(end);
		boolQuery.must(dateQuery);
		//
		
		return boolQuery;
	}
	
	public void deleteByTopic(String asset, 
							  boolean isAnyAsset, 
							  String semTopic, 
							  boolean isAnySubtopic) {
		
		boolean isAnyAccount = true;
		BoolQueryBuilder boolQuery = this.getQueryBtTopic(null, isAnyAccount, asset, isAnyAsset, semTopic, isAnySubtopic);
		
		// delete by query API is deprecated, scroll with bulk delete must be used
		this.esTypeDAO.deleteByQuery(boolQuery);
	}
	
	public void deleteByTopic(String asset, 
							  boolean isAnyAsset, 
							  String semTopic, 
							  boolean isAnySubtopic,
							  long start, 
							  long end) {
		
		boolean isAnyAccount = true;
		BoolQueryBuilder boolQuery = this.getQueryBtTopicAndDate(null, isAnyAccount, asset, isAnyAsset, semTopic, isAnySubtopic, start, end);
		
		// delete by query API is deprecated, scroll with bulk delete must be used
		this.esTypeDAO.deleteByQuery(boolQuery);
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
		
		this.deleteByTopic(asset, anyAsset, semTopic, topicPrefix, start, end);
	}
	
	public SearchHits findByAccount(String semTopic,
									boolean isAnySubtopic,
									int offset,
									int size) {
		
		long timeout = EsUtils.getQueryTimeout();
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(false)
										.addFields("sem_topic", "timestamp", "asset", "account")
										.setFrom(offset)
										.setSize(size)
										.get(TimeValue.timeValueMillis(timeout));
		
		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public SearchHits findByTopic(String account,
								  boolean isAnyAccount,
								  String asset,
								  boolean isAnyAsset,
								  String semTopic,
								  boolean isAnySubtopic,
								  int offset,
								  int size) {
		
		long timeout = EsUtils.getQueryTimeout();
		
		QueryBuilder boolQuery = this.getQueryBtTopic(account, isAnyAccount, asset, isAnyAsset, semTopic, isAnySubtopic);
		
		// Search by key should not need offset and size
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(false)
										.addFields(EsSchema.TOPIC_SEM_NAME, 
												   EsSchema.TOPIC_TIMESTAMP, 
												   EsSchema.TOPIC_ASSET, 
												   EsSchema.TOPIC_ACCOUNT)
										.setQuery(boolQuery)
										.setFrom(offset)
										.setSize(size)
										.get(TimeValue.timeValueMillis(timeout));
		
		return response.getHits();
	}
}
