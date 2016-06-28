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

import org.eclipse.kapua.message.KapuaTopic;
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

public class EsAssetDAO
{

    private EsTypeDAO esTypeDAO;

    private EsAssetDAO()
    {
    }

    public EsAssetDAO setListener(EsDaoListener daoListener)
        throws EsDatastoreException
    {
        this.esTypeDAO.setListener(daoListener);
        return this;
    }

    public EsAssetDAO unsetListener(EsDaoListener daoListener)
        throws EsDatastoreException
    {
        this.esTypeDAO.unsetListener(daoListener);
        return this;
    }

    public static EsAssetDAO connection(Client client)
        throws UnknownHostException
    {
        EsAssetDAO esAssetDAO = new EsAssetDAO();
        esAssetDAO.esTypeDAO = EsTypeDAO.connection(client);
        return esAssetDAO;
    }

    public EsAssetDAO instance(String indexName, String typeName)
    {
        this.esTypeDAO.instance(indexName, typeName);
        return this;
    }

    public BoolQueryBuilder getQueryByAsset(String asset,
                                            boolean isAnyAsset)
    {

        // Asset clauses
        QueryBuilder assetQuery = null;
        if (!isAnyAsset) {
            assetQuery = QueryBuilders.termQuery(EsSchema.ASSET_NAME, asset);
        }

        // Composite clause
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (assetQuery != null)
            boolQuery.must(assetQuery);
        //

        return boolQuery;
    }

    public BoolQueryBuilder getQueryByAssetAndDate(String asset,
                                                   boolean isAnyAsset,
                                                   long start,
                                                   long end)
    {

        // Composite clause
        BoolQueryBuilder boolQuery = this.getQueryByAsset(asset, isAnyAsset);

        // Timestamp clauses
        QueryBuilder dateQuery = QueryBuilders.rangeQuery(EsSchema.ASSET_TIMESTAMP)
                                              .from(start).to(end);
        boolQuery.must(dateQuery);
        //

        return boolQuery;
    }

    public UpdateRequest getUpsertRequest(String id, XContentBuilder esAsset)
    {
        return this.esTypeDAO.getUpsertRequest(id, esAsset);
    }

    public UpdateResponse upsert(String id, XContentBuilder esAsset)
    {
        return this.esTypeDAO.upsert(id, esAsset);
    }

    public UpdateResponse update(String id, XContentBuilder esAsset)
    {
        return this.esTypeDAO.update(id, esAsset);
    }

    public void deleteByQuery(BoolQueryBuilder boolQuery)
    {
        this.esTypeDAO.deleteByQuery(boolQuery);
    }

    public void deleteByAccount(long start, long end)
    {
        this.deleteByQuery(this.getQueryByAssetAndDate(KapuaTopic.SINGLE_LEVEL_WCARD, true, start, end));
    }

    public void deleteByAsset(String asset, boolean isAnyAsset)
    {
        this.deleteByQuery(this.getQueryByAsset(asset, isAnyAsset));
    }

    public SearchHits findByAccount(String name,
                                    int offset,
                                    int size)
    {

        long timeout = EsUtils.getQueryTimeout();

        SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
                                           .setTypes(esTypeDAO.getTypeName())
                                           .setFetchSource(false)
                                           .addFields(EsSchema.ASSET_NAME,
                                                      EsSchema.ASSET_TIMESTAMP,
                                                      EsSchema.ASSET_ACCOUNT)
                                           .setFrom(offset)
                                           .setSize(size)
                                           .get(TimeValue.timeValueMillis(timeout));

        SearchHits searchHits = response.getHits();
        return searchHits;
    }

    public SearchHits findByAsset(String name,
                                  boolean isAnyAccount,
                                  String asset,
                                  boolean isAnyAsset,
                                  int offset,
                                  int size)
    {

        long timeout = EsUtils.getQueryTimeout();

        BoolQueryBuilder boolQuery = this.getQueryByAsset(asset, isAnyAsset);

        SearchResponse response = esTypeDAO.getClient().prepareSearch(esTypeDAO.getIndexName())
                                           .setTypes(esTypeDAO.getTypeName())
                                           .setFetchSource(false)
                                           .addFields(EsSchema.ASSET_NAME,
                                                      EsSchema.ASSET_TIMESTAMP,
                                                      EsSchema.ASSET_ACCOUNT)
                                           .setQuery(boolQuery)
                                           .setFrom(offset)
                                           .setSize(size)
                                           .get(TimeValue.timeValueMillis(timeout));

        SearchHits searchHits = response.getHits();
        return searchHits;
    }
}
