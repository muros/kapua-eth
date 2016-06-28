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

import org.eclipse.kapua.message.KapuaInvalidTopicException;
import org.eclipse.kapua.message.KapuaTopic;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsDatastoreException;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsSchema;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsTopicMetric;
import org.eclipse.kapua.service.datastore.internal.elasticsearch.EsUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;

public class EsTopicMetricDAO
{

    private EsTypeDAO esTypeDAO;

    private EsTopicMetricDAO()
    {
    }

    public EsTopicMetricDAO setListener(EsDaoListener daoListener)
        throws EsDatastoreException
    {
        this.esTypeDAO.setListener(daoListener);
        return this;
    }

    public EsTopicMetricDAO unsetListener(EsDaoListener daoListener)
        throws EsDatastoreException
    {
        this.esTypeDAO.unsetListener(daoListener);
        return this;
    }

    public static EsTopicMetricDAO connection(Client client)
        throws UnknownHostException
    {
        EsTopicMetricDAO esTopicMetricDAO = new EsTopicMetricDAO();
        esTopicMetricDAO.esTypeDAO = EsTypeDAO.connection(client);
        return esTopicMetricDAO;
    }

    public EsTopicMetricDAO instance(String indexName, String typeName)
    {
        this.esTypeDAO.instance(indexName, typeName);
        return this;
    }

    public UpdateRequest getUpsertRequest(EsTopicMetric esTopicMetric)
    {
        return this.esTypeDAO.getUpsertRequest(esTopicMetric.getId(), esTopicMetric.getContent());
    }

    public UpdateResponse upsert(EsTopicMetric esTopicMetric)
    {
        return esTypeDAO.upsert(esTopicMetric.getId(), esTopicMetric.getContent());
    }

    public void deleteByQuery(BoolQueryBuilder boolQuery)
    {
        this.esTypeDAO.deleteByQuery(boolQuery);
    }

    public BoolQueryBuilder getQueryByTopic(String asset,
                                            boolean isAnyAsset,
                                            String semTopic,
                                            boolean isAnySubtopic)
    {

        // Asset clauses
        QueryBuilder assetQuery = null;
        if (!isAnyAsset) {
            assetQuery = QueryBuilders.termQuery(EsSchema.TOPIC_METRIC_ASSET, asset);
        }

        // Topic clauses
        QueryBuilder topicQuery = null;
        if (isAnySubtopic) {
            topicQuery = QueryBuilders.prefixQuery(EsSchema.TOPIC_METRIC_SEM_NAME, semTopic);
        }
        else {
            topicQuery = QueryBuilders.termQuery(EsSchema.TOPIC_METRIC_SEM_NAME, semTopic);
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
                                                   long end)
    {

        // Composite clause
        BoolQueryBuilder boolQuery = this.getQueryByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);
        //
        // Timestamp clauses
        QueryBuilder dateQuery = QueryBuilders.rangeQuery(EsSchema.TOPIC_METRIC_MTR_TIMESTAMP).from(start).to(end);
        boolQuery.must(dateQuery);

        return boolQuery;
    }

    public void deleteByTopic(String asset,
                              boolean isAnyAsset,
                              String semTopic,
                              boolean isAnySubtopic)
    {

        // Asset clauses
        BoolQueryBuilder boolQuery = this.getQueryByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);
        this.esTypeDAO.deleteByQuery(boolQuery);
    }

    public void deleteByTopic(String asset,
                              boolean isAnyAsset,
                              String semTopic,
                              boolean isAnySubtopic,
                              long start,
                              long end)
    {

        // Asset clauses
        BoolQueryBuilder boolQuery = this.getQueryByTopicAndDate(asset, isAnyAsset, semTopic, isAnySubtopic, start, end);
        this.esTypeDAO.deleteByQuery(boolQuery);
    }

    public void deleteByAccount(String accountName, long start, long end)
        throws KapuaInvalidTopicException
    {

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

    public BulkResponse bulk(BulkRequest aBulkRequest)
    {
        return this.esTypeDAO.bulk(aBulkRequest);
    }

    public SearchHits findByTopic(String asset,
                                  boolean isAnyAsset,
                                  String semTopic,
                                  boolean isAnySubtopic,
                                  int offset,
                                  int limit)
    {

        long timeout = EsUtils.getQueryTimeout();

        BoolQueryBuilder topicQuery = this.getQueryByTopic(asset, isAnyAsset, semTopic, isAnySubtopic);

        SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
                                           .setTypes(esTypeDAO.getTypeName())
                                           .setFetchSource(false)
                                           .addFields(EsSchema.TOPIC_METRIC_MTR_NAME_FULL,
                                                      EsSchema.TOPIC_METRIC_MTR_TYPE_FULL,
                                                      EsSchema.TOPIC_METRIC_MTR_VALUE_FULL,
                                                      EsSchema.TOPIC_METRIC_MTR_TIMESTAMP_FULL,
                                                      EsSchema.TOPIC_METRIC_MTR_MSG_ID_FULL)
                                           .setQuery(topicQuery)
                                           .setFrom(offset)
                                           .setSize(limit)
                                           .get(TimeValue.timeValueMillis(timeout));

        SearchHits searchHits = response.getHits();
        return searchHits;
    }
}
