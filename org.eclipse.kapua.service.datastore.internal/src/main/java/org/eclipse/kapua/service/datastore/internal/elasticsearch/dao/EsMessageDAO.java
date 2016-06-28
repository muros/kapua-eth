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
import java.util.List;
import java.util.Map;

import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsDatastoreException;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsSchema;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsUtils;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
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
import org.elasticsearch.search.sort.SortOrder;

public class EsMessageDAO {
	
	private EsTypeDAO esTypeDAO;
	
	private String[] getFetchIncluded(MessageFetchStyle fetchStyle) {
		
		// Fetch mode
		String[] includeSource = null;
		
		switch (fetchStyle) {
			case METADATA:
				includeSource = new String[] {""};
				break;
			case METADATA_HEADERS:
				includeSource = new String[] {EsSchema.MESSAGE_COLLECTED_ON, EsSchema.MESSAGE_POS + ".*", EsSchema.MESSAGE_MTR + ".*"};
				break;
			case METADATA_HEADERS_PAYLOAD:
				includeSource = new String[] {"*"};
		}
		
		return includeSource;
	}
	
	private String[] getFetchExcluded(MessageFetchStyle fetchStyle) {
		
		// Fetch mode
		String[] excludeSource = null;
		
		switch (fetchStyle) {
			case METADATA:
				excludeSource = new String[] {"*"};
				break;
			case METADATA_HEADERS:
				excludeSource = new String[] {EsSchema.MESSAGE_BODY};
				break;
			case METADATA_HEADERS_PAYLOAD:
				excludeSource = new String[] {""};
		}
		
		return excludeSource;
	}
	
	private EsMessageDAO() {}
	
	public EsMessageDAO setListener(EsDaoListener daoListener) throws EsDatastoreException {
		this.esTypeDAO.setListener(daoListener);
		return this;
	}
	
	public EsMessageDAO unsetListener(EsDaoListener daoListener) throws EsDatastoreException {
		this.esTypeDAO.unsetListener(daoListener);
		return this;
	}
	
	public static EsMessageDAO connection(Client client) throws UnknownHostException {
		EsMessageDAO esMessageDAO = new EsMessageDAO();
		esMessageDAO.esTypeDAO = EsTypeDAO.connection(client);
		return esMessageDAO;
	}
	
	public EsMessageDAO instance(String indexName, String typeName) {
		this.esTypeDAO.instance(indexName, typeName);
		return this;
	}
	
	public UpdateRequest getUpsertReq(String id, Map<String, Object> esAsset) {
		return this.esTypeDAO.getUpsertRequest(id, esAsset);
	}
	
	public UpdateRequest getUpsertReq(String id, XContentBuilder esAsset) {
		return this.esTypeDAO.getUpsertRequest(id, esAsset);
	}
	
	public UpdateResponse upsert(String id, XContentBuilder esAsset) {
		return this.esTypeDAO.upsert(id, esAsset);
	}
	
	public UpdateResponse upsert(String id, Map<String, Object> esAsset) {
		return this.esTypeDAO.upsert(id, esAsset);
	}
	
	public void deleteByQuery(BoolQueryBuilder boolQuery) {		
		this.esTypeDAO.deleteByQuery(boolQuery);
	}
	
	public BoolQueryBuilder getQueryByAccountAndDate(String account, boolean isAnyAccount, long start, long end) {
		
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

		// Timestamp clauses
		QueryBuilder dateQuery = QueryBuilders.rangeQuery(EsSchema.MESSAGE_TIMESTAMP).from(start).to(end);
		boolQuery.must(dateQuery);
		
		return boolQuery;
	}
	
	public BoolQueryBuilder getQueryByAssetAndDate(String asset, boolean isAnyAsset, long start, long end) {
		
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

		// Asset clauses
		QueryBuilder assetQuery = null;
		if (!isAnyAsset) {
			assetQuery = QueryBuilders.termQuery(EsSchema.MESSAGE_AS_NAME, asset);
			boolQuery.must(assetQuery);
		}

		// Timestamp clauses
		QueryBuilder dateQuery = QueryBuilders.rangeQuery(EsSchema.MESSAGE_TIMESTAMP).from(start).to(end);
		boolQuery.must(dateQuery);
		
		return boolQuery;
	}
	
	public BoolQueryBuilder getQueryByTopic(String asset,
										  boolean isAnyAsset,
										  String semTopic,
										  boolean isAnySubtopic) {
		
		// Asset clauses
		QueryBuilder assetQuery = null;
		if (!isAnyAsset) {
			assetQuery = QueryBuilders.termQuery(EsSchema.MESSAGE_AS_NAME, asset);
		}
		
		// Topic clauses
		QueryBuilder topicQuery = null;
		if (isAnySubtopic) {
			topicQuery = QueryBuilders.prefixQuery(EsSchema.MESSAGE_SEM_TOPIC, semTopic);
		} else { 
			topicQuery = QueryBuilders.termQuery(EsSchema.MESSAGE_SEM_TOPIC, semTopic);	
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
		
		BoolQueryBuilder boolQuery = this.getQueryByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);
		
		// Timestamp clauses
		QueryBuilder dateQuery = QueryBuilders.rangeQuery(EsSchema.MESSAGE_TIMESTAMP).from(start).to(end);
		boolQuery.must(dateQuery);
		
		return boolQuery;
	}
	
	public BoolQueryBuilder getQueryByMetricValueAndDate(String asset,
														boolean isAnyAsset,
														String semTopic,
														boolean isAnySubtopic,
														String fullMetricName,
													  	long start, 
													  	long end, 
													  	Object min,
													  	Object max) {
		
		BoolQueryBuilder boolQuery = this.getQueryByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);
		
		// Timestamp clauses
		QueryBuilder dateQuery = QueryBuilders.rangeQuery(EsSchema.MESSAGE_TIMESTAMP).from(start).to(end);
		boolQuery.must(dateQuery);
		
		// Metric value clause
		QueryBuilder valueQuery = QueryBuilders.rangeQuery(fullMetricName).gte(min).lte(max);	
		boolQuery.must(valueQuery);	
		//
		
		return boolQuery;
	}
	
	public void deleteByTopic(String asset,
							  boolean isAnyAsset,
							  String semTopic,
							  boolean isAnySubtopic,
							  long start,
							  long end) {
		
		BoolQueryBuilder boolQuery = this.getQueryByTopicAndDate(asset, isAnyAsset, semTopic, isAnySubtopic, start, end);
		this.esTypeDAO.deleteByQuery(boolQuery);
	}
	
	public void deleteByAccount(long start,
								long end) {
		
		
		// Query clauses
		boolean isAnyAccount = true;
		BoolQueryBuilder boolQuery = this.getQueryByAccountAndDate(null, isAnyAccount, start, end);
		this.esTypeDAO.deleteByQuery(boolQuery);
	}
	
	public SearchHits findByTopic(String asset,
								  boolean isAnyAsset,
								  String semTopic, 
								  boolean isAnySubtopic,
								  long start, 
								  long end, 
								  int indexOffset, 
								  int limit, 
								  Boolean isSortAscending,
							  	  MessageFetchStyle fetchStyle) {
		
		// Asset clauses
		QueryBuilder boolQuery = this.getQueryByTopicAndDate(asset, isAnyAsset, semTopic, isAnySubtopic, start, end);
		//
		
		String[] includeFetch = this.getFetchIncluded(fetchStyle);
		String[] excludeFetch = this.getFetchExcluded(fetchStyle);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(includeFetch, excludeFetch)
										.addFields(EsSchema.MESSAGE_ACCOUNT, 
												EsSchema.MESSAGE_AS_NAME, 
												EsSchema.MESSAGE_SEM_TOPIC, 
												EsSchema.MESSAGE_TIMESTAMP)
										.setQuery(boolQuery)
										.addSort(EsSchema.MESSAGE_TIMESTAMP, 
												 isSortAscending ? SortOrder.ASC : SortOrder.DESC)
										.setFrom(indexOffset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));

		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public SearchHits findByAsset(String asset,
								  boolean isAnyAsset,
								  long start, 
								  long end, 
								  int indexOffset, 
								  int limit, 
								  Boolean sortAscending,
							  	  MessageFetchStyle fetchStyle) {
		
		BoolQueryBuilder boolQuery = this.getQueryByAssetAndDate(asset, isAnyAsset, start, end);
		
		String[] includeFetch = this.getFetchIncluded(fetchStyle);
		String[] excludeFetch = this.getFetchExcluded(fetchStyle);
		
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(includeFetch, excludeFetch)
										.addFields(EsSchema.MESSAGE_ACCOUNT, 
												EsSchema.MESSAGE_AS_NAME, 
												EsSchema.MESSAGE_SEM_TOPIC, 
												EsSchema.MESSAGE_TIMESTAMP)
										.setQuery(boolQuery)
										.addSort(EsSchema.MESSAGE_TIMESTAMP, 
												sortAscending ? SortOrder.ASC : SortOrder.DESC)
										.setFrom(indexOffset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));

		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public SearchHits findByAccount(String account, 
								  	long start, 
								  	long end, 
								  	int indexOffset, 
								  	int limit, 
								  	Boolean sortAscending,
								  	MessageFetchStyle fetchStyle) {
		
		boolean isAnyAccount = true;
		QueryBuilder boolQuery = this.getQueryByAccountAndDate(null, isAnyAccount, start, end);
		
		String[] includeFetch = this.getFetchIncluded(fetchStyle);
		String[] excludeFetch = this.getFetchExcluded(fetchStyle);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(includeFetch, excludeFetch)
										.addFields(EsSchema.MESSAGE_ACCOUNT, 
												EsSchema.MESSAGE_AS_NAME, 
												EsSchema.MESSAGE_SEM_TOPIC, 
												EsSchema.MESSAGE_TIMESTAMP)
										.setQuery(boolQuery)
										.addSort(EsSchema.MESSAGE_TIMESTAMP, 
												sortAscending ? SortOrder.ASC : SortOrder.DESC)
										.setFrom(indexOffset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));

		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public SearchHits findByAccountCount(String account) {
		
		QueryBuilder accountQuery = QueryBuilders.termQuery(EsSchema.MESSAGE_ACCOUNT, account);	
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(false)
										.setQuery(QueryBuilders.boolQuery().filter(accountQuery))
										.setSize(0)
										.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));

		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public SearchHits findById(String account, 
							   List<String> ids,
							   MessageFetchStyle fetchStyle) {
		
		QueryBuilder idsQuery = QueryBuilders.idsQuery(ids.toArray(new String[] {}));	
		
		String[] includeFetch = this.getFetchIncluded(fetchStyle);
		String[] excludeFetch = this.getFetchExcluded(fetchStyle);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(includeFetch, excludeFetch)
										.addFields(EsSchema.MESSAGE_ACCOUNT, 
												EsSchema.MESSAGE_AS_NAME, 
												EsSchema.MESSAGE_SEM_TOPIC, 
												EsSchema.MESSAGE_TIMESTAMP)
										.setQuery(QueryBuilders.boolQuery().filter(idsQuery))
										.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));

		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	/**
	 * @param topic exepected semantic topic
	 * @param metric exepected full qualified metric name (example: "PROBE_TEMP.dbl")
	 * @param start
	 * @param end
	 * @param min
	 * @param max
	 * @param indexOffset
	 * @param limit
	 * @param sortAscending
	 * @param fetchStyle
	 * @return
	 */
	public SearchHits findByMetricValue (String asset,
										 boolean isAnyAsset,
										 String semTopic,
										 boolean isAnySubtopic,
										 String metric,
									  	 long start, 
									  	 long end, 
									  	 Object min,
									  	 Object max,
									  	 int offset, 
									  	 int limit, 
									  	 Boolean sortAscending,
									  	 MessageFetchStyle fetchStyle) {
		
		// Query clause
		String fullMetricName = String.format("%s.%s", EsSchema.MESSAGE_MTR, metric);
		BoolQueryBuilder boolQuery = this.getQueryByMetricValueAndDate(asset, 
																	isAnyAsset, 
																	semTopic, 
																	isAnySubtopic, 
																	fullMetricName, 
																	start, 
																	end, 
																	min, 
																	max);
			
		String[] includeFetch = this.getFetchIncluded(fetchStyle);
		String[] excludeFetch = this.getFetchExcluded(fetchStyle);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(includeFetch, excludeFetch)
										.addFields(EsSchema.MESSAGE_ACCOUNT, 
												EsSchema.MESSAGE_AS_NAME, 
												EsSchema.MESSAGE_SEM_TOPIC, 
												EsSchema.MESSAGE_TIMESTAMP)
										.setQuery(boolQuery)
										.addSort(EsSchema.MESSAGE_TIMESTAMP, 
												sortAscending ? SortOrder.ASC : SortOrder.DESC)
										.setFrom(offset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));

		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public void deleteById(String uuid) {
		
		esTypeDAO.getClient().prepareDelete()
							.setIndex(esTypeDAO.getIndexName())
							.setType(esTypeDAO.getTypeName())
							.setId(uuid)
							.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));
	}
	
	/**
	 * @param topic exepected semantic topic
	 * @param metric exepected full qualified metric name (example: "PROBE_TEMP.dbl")
	 * @param start
	 * @param end
	 * @param min
	 * @param max
	 * @param indexOffset
	 * @param limit
	 * @param sortAscending
	 * @param fetchStyle
	 * @return
	 */
	public SearchHits findMetricsByValue (String asset,
										  boolean isAnyAsset,
										  String semTopic,
										  boolean isAnySubtopic,
										  String metric,
									  	  long start, 
									  	  long end, 
									  	  Object min,
									  	  Object max,
									  	  int offset, 
									  	  int limit, 
									  	  Boolean sortAscending) {
		
		String fullMetricName = String.format("%s.%s", EsSchema.MESSAGE_MTR, metric);
		BoolQueryBuilder boolQuery = this.getQueryByMetricValueAndDate(asset, isAnyAsset, semTopic, 
																	   isAnySubtopic, fullMetricName, 
																	   start, end, min, max);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(false)
										.addFields(EsSchema.ASSET_ACCOUNT, 
												EsSchema.MESSAGE_AS_NAME, 
												EsSchema.MESSAGE_SEM_TOPIC, 
												EsSchema.MESSAGE_TIMESTAMP, 
												EsSchema.MESSAGE_MTR + ".*", fullMetricName)
										.setQuery(boolQuery)
										.addSort(EsSchema.MESSAGE_TIMESTAMP, 
												sortAscending ? SortOrder.ASC : SortOrder.DESC)
										.setFrom(offset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));

		SearchHits searchHits = response.getHits();
		return searchHits;
	}
	
	public SearchHits findMetricsByTimestamp(String asset,
											 boolean isAnyAsset,
											 String semTopic,
											 boolean isAnySubtopic,
											 String metric,
										  	 long start, 
										  	 long end, 
										  	 int offset, 
										  	 int limit, 
										  	 Boolean sortAscending) {

		// TODO Check if adding the field in the field list (below) is enough ... what id the metric does not exist ??
		String fullMetricName = String.format("%s.%s", EsSchema.MESSAGE_MTR, metric);
		
		BoolQueryBuilder boolQuery = this.getQueryByTopicAndDate(asset, isAnyAsset, semTopic, isAnySubtopic, start, end);
		
		SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
										.setTypes(esTypeDAO.getTypeName())
										.setFetchSource(false)
										.addFields(EsSchema.MESSAGE_ACCOUNT, 
												EsSchema.MESSAGE_AS_NAME, 
												EsSchema.MESSAGE_SEM_TOPIC, 
												EsSchema.MESSAGE_TIMESTAMP, 
												fullMetricName)
										.setQuery(boolQuery)
										.addSort(EsSchema.MESSAGE_TIMESTAMP, 
												sortAscending ? SortOrder.ASC : SortOrder.DESC)
										.setFrom(offset)
										.setSize(limit)
										.get(TimeValue.timeValueMillis(EsUtils.getQueryTimeout()));
		
		SearchHits searchHits = response.getHits();
		return searchHits;
	}
}
