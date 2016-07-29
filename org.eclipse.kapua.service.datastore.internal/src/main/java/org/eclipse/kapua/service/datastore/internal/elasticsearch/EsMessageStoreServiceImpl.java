///*******************************************************************************
// * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
// *
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     Eurotech - initial API and implementation
// *
// *******************************************************************************/
//package org.eclipse.kapua.service.datastore.internal.elasticsearch;
        //
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
        //
//import org.eclipse.kapua.KapuaErrorCodes;
//import org.eclipse.kapua.KapuaException;
//import org.eclipse.kapua.commons.util.ArgumentValidator;
//import org.eclipse.kapua.commons.util.KapuaDateUtils;
//import org.eclipse.kapua.locator.KapuaLocator;
//import org.eclipse.kapua.message.KapuaInvalidTopicException;
//import org.eclipse.kapua.message.KapuaTopic;
//import org.eclipse.kapua.model.id.KapuaId;
//import org.eclipse.kapua.service.account.Account;
//import org.eclipse.kapua.service.account.AccountService;
//import org.eclipse.kapua.service.authorization.AuthorizationService;
//import org.eclipse.kapua.service.authorization.Permission;
//import org.eclipse.kapua.service.authorization.PermissionFactory;
//import org.eclipse.kapua.service.config.KapuaConfigurableService;
//import org.eclipse.kapua.service.datastore.internal.DatastoreServiceAction;
//import org.eclipse.kapua.service.datastore.internal.config.DatastoreSettings;
//import org.eclipse.kapua.service.datastore.internal.elasticsearch.dao.EsMessageDAO;
//import org.eclipse.kapua.service.datastore.internal.config.DatastoreSettingKey;
//import org.eclipse.kapua.service.datastore.internal.model.DataIndexBy;
//import org.eclipse.kapua.service.datastore.internal.model.MessageListResultImpl;
//import org.eclipse.kapua.service.datastore.internal.model.MetricsIndexBy;
//import org.eclipse.kapua.service.datastore.internal.model.query.AndPredicateImpl;
//import org.eclipse.kapua.service.datastore.internal.model.query.IdsPredicateImpl;
//import org.eclipse.kapua.service.datastore.internal.model.query.MessageQueryImpl;
//import org.eclipse.kapua.service.datastore.model.AssetInfoCreator;
//import org.eclipse.kapua.service.datastore.model.Message;
//import org.eclipse.kapua.service.datastore.model.MessageCreator;
//import org.eclipse.kapua.service.datastore.model.MessageListResult;
//import org.eclipse.kapua.service.datastore.model.Payload;
//import org.eclipse.kapua.service.datastore.model.StorableId;
//import org.eclipse.kapua.service.datastore.model.StorableListResult;
//import org.eclipse.kapua.service.datastore.model.query.AndPredicate;
//import org.eclipse.kapua.service.datastore.model.query.IdsPredicate;
//import org.eclipse.kapua.service.datastore.model.query.TopicMatchPredicate;
//import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
//import org.eclipse.kapua.service.datastore.model.query.MessageQuery;
//import org.eclipse.kapua.service.datastore.model.query.RangePredicate;
//import org.eclipse.kapua.service.datastore.model.query.SortDirection;
//import org.eclipse.kapua.service.datastore.model.query.StorablePredicate;
//import org.eclipse.kapua.service.datastore.model.query.StorableQuery;
//import org.eclipse.kapua.service.datastore.model.query.TermPredicate;
//import org.elasticsearch.action.search.SearchAction;
//import org.elasticsearch.action.search.SearchRequestBuilder;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.RangeQueryBuilder;
//import org.elasticsearch.index.query.TermQueryBuilder;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.sort.SortOrder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
        //
//public class EsMessageStoreServiceImpl
//{
//    private static final Logger logger = LoggerFactory.getLogger(EsMessageStoreServiceImpl.class);
        //
//    /**
//     * This is the max value that an account can have as TTL in days.
//     * 
//     * The Cassandra driver 'Hector' accepts a {@code int} value as ttl (measured in seconds),
//     * so TTL for data needs to be limited!
//     */
//    public static final int      MAX_DAYS_TTL_VALUE       = Integer.MAX_VALUE / 60 / 60 / 24;
        //
//    private static final long    DAY_SECS                 = 24 * 60 * 60;
//    private static final long    DAY_MILLIS               = DAY_SECS * 1000;
    //
//    private static final long    EUROTECH_TTL_DAYS        = 30;
//    private static final long    EUROTECH_TTL_SECS        = EUROTECH_TTL_DAYS * DAY_SECS;
//    private static final long    EUROTECH_TTL_MILLIS      = EUROTECH_TTL_DAYS * DAY_MILLIS;
    //
//    private KapuaConfigurableService configurationService;
//    private EsDatastoreAdapter     datastoreFacade;
//    private int                  maxTopicDepth;
//    private static int           counter                = 0;
//    private static long          timingProfileThreshold = 1000; // in milliseconds
//    private static boolean       enableTimingProfile    = false;
        //
//    public EsMessageStoreServiceImpl(KapuaConfigurableService configurationService)
//    {
//        this.configurationService = configurationService;
//		datastoreFacade = new EsDatastoreAdapter();
//		DatastoreSettings config = DatastoreSettings.getInstance();
//        maxTopicDepth = config.getInt(DatastoreSettingKey.CONFIG_TOPIC_MAX_DEPTH);
//        enableTimingProfile = config.getBoolean(DatastoreSettingKey.CONFIG_DATA_STORAGE_ENABLE_TIMING_PROFILE, false);
//        timingProfileThreshold = config.getLong(DatastoreSettingKey.CONFIG_DATA_STORAGE_TIMING_PROFILE_THRESHOLD, 1000L);
//    }
        //
//    public String storeAssetInfo(String scopeName, AssetInfoCreator assetInfoCreator)
//        throws KapuaException
//    {
//        throw KapuaException.internalError(String.format("Method not implemented"));
//    }
        //
//    public String storeMessage(String scopeName, MessageCreator messageCreator)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(scopeName, "scopeName");
//        ArgumentValidator.notNull(messageCreator, "messageCreator");
        //
        //
//        String forceUUID = UUID.randomUUID().toString();
//        this.storeMessageImpl(scopeName, messageCreator, forceUUID);
//        return forceUUID;
//    }
        //
//    public Message findMessage(String scopeName, StorableId id, MessageFetchStyle fetchStyle) throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(scopeName, "scopeName");
//        ArgumentValidator.notNull(id, "id");
        //
//        MessageQueryImpl q = new MessageQueryImpl();
//        q.setLimit(1);
//        q.setFetchStyle(fetchStyle);
        //
//        ArrayList<StorableId> ids = new ArrayList<StorableId>();
//        ids.add(id);
        //
//        AndPredicateImpl allPredicates =  new AndPredicateImpl();
//        allPredicates.addPredicate(new IdsPredicateImpl(EsMessageField.ID, ids));
        //
//        MessageListResult result = this.queryMessage(scopeName, q);
//        if (result == null || result.size() == 0)
//            return null;
        //
//        Message message = result.get(0);
//        return message;
//    }
        //
//    public void deleteMessage(String accountName, StorableId id)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
//        ArgumentValidator.notNull(id, "id");
        //
//        //
//        // Check access
//        checkDataAccess(accountName, DatastoreServiceAction.DELETE);
        //
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
        //
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, return", accountName);
//            return;
//        }
        //
//        try
//        {
//            String everyIndex = EsUtils.getAnyIndexName(accountName);
//            EsMessageDAO.connection(EsClient.getcurrent())
//                        .instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
//                        .deleteById(id.toString());
//        } 
//        catch (Exception exc) 
//        {
//            // TODO manage exeception
//            //CassandraUtils.handleException(e);
//            throw KapuaException.internalError(exc);
//        }
//    }
        //
//    private void storeAssetInfoImpl(String accountName, AssetInfoCreator assetInfoCreator) 
//    {
        //
//    }
        //
//    private void storeMessageImpl(String accountName, MessageCreator messageCreator, String forceUUID)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(accountName, "accountName");
//        ArgumentValidator.notNull(messageCreator, "messageCreator");
        //
//        if (!(messageCreator.getPayload() == null))
//        	throw new KapuaException(KapuaErrorCodes.ILLEGAL_ARGUMENT); // TODO find a better exception
        //
//        //
//        // Store the message
//        Date start = KapuaDateUtils.getEdcSysDate();
        //
//        boolean getTiming = ((counter++ % 100) == 0);
//        boolean getTimingProfile = enableTimingProfile && getTiming;
//        LinkedHashMap<String, Long> timingProfile = null;
//
//        if (getTimingProfile) {
//            timingProfile = new LinkedHashMap<String, Long>();
//            timingProfile.put("start", KapuaDateUtils.getEdcSysDate().getTime());
//        }
//
//        //
//        // Check Storage Access
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_SECS;
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            String msg = String.format("Storage not enabled for account %s, not storing message", accountName);
//            logger.debug(msg);
//            throw KapuaException.internalError(msg);
//        }
//
//        // If eurotech set ttl equals to 1 month
//        if (ttl < 0) {
//            ttl = EUROTECH_TTL_SECS;
//        }
//
//        if (getTimingProfile) {
//            timingProfile.put("Parameter validation", KapuaDateUtils.getEdcSysDate().getTime());
//        }
//
//        // connection
//        if (getTimingProfile) {
//            timingProfile.put("Cassandra connection", KapuaDateUtils.getEdcSysDate().getTime());
//        }
//
//        // MessageDAO: prepare the store parameters
//        Date capturedOn = null;
//        if (messageCreator.getPayload() != null) {
//            capturedOn = messageCreator.getPayload().getCollectedOn();
//        }
//
//        long currentDate = KapuaDateUtils.getEdcSysDate().getTime();
//        
//        long receivedOn = currentDate;
//        if (messageCreator.getTimestamp() != null)
//        	receivedOn = messageCreator.getTimestamp().getTime();
//        
//        // Overwrite timestamp if necessary
//        // Use the account service plan to determine whether we will give precede to the device time
//        long indexedOn = currentDate;
//        if (DataIndexBy.DEVICE_TIMESTAMP.equals(accountServicePlan.getDataIndexBy()) && capturedOn != null)
//        	indexedOn = capturedOn.getTime();
//        
//        if (getTimingProfile)
//            timingProfile.put("MessageDAO - prepare the store parameters", KapuaDateUtils.getEdcSysDate().getTime());
//        
//        // MessageDAO: query
//        try {
//        	
//        	this.datastoreFacade.storeMessage(accountName, 
//        									   messageCreator, 
//        									   maxTopicDepth, 
//        									   indexedOn, 
//        									   receivedOn,
//        									   ttl, 
//        									   forceUUID, 
//        									   timingProfile, 
//        									   accountServicePlan.getMetricsIndexBy());
//        }
//        catch (Exception e) { // TODO create e new datastore exception
//        	// TODO manage execeptions
//            //CassandraUtils.handleException(e);
//        	// TODO Remove
//        	e.printStackTrace();
//        	throw KapuaException.internalError(e);
//        }
//
//        // get some timing
//        Date end = KapuaDateUtils.getEdcSysDate();
//        if (getTiming) {
//            long elapsed = end.getTime() - start.getTime();
//            logger.info("storeMessage in {} ms", elapsed);
//            if (getTimingProfile && elapsed > timingProfileThreshold) {
//                long prevTime = -1;
//                for (String key : timingProfile.keySet()) {
//                    Long nextTime = timingProfile.get(key);
//                    if (prevTime >= 0) {
//                        logger.debug(" {}: {} ms", key, nextTime - prevTime);
//                    }
//                    prevTime = nextTime;
//                }
//            }
//        }
//    }
//
//    // -----------------------------------------------------------------------------------------
//    //
//    // Messages
//    //
//    // -----------------------------------------------------------------------------------------
//    public MessageListResult queryMessage(String accountName, MessageQuery query)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
//        ArgumentValidator.notNull(query, "query");
//
//        //
//        // Check Access
//        checkDataAccess(account, DatastorePermAction.DELETE);
//
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
//
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) 
//        {
//            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
//            return new MessageListResultImpl();
//        }
//
//        try 
//        {
//            String everyIndex = EsUtils.getAnyIndexName(accountName);
//            MessageListResult result = null;
//            result = EsMessageDAO.connection(EsClient.getcurrent())
//                                 .instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
//                                 .query(query);
//
//            return result;
//        }
//        catch (Exception exc) 
//        {
//            // TODO manage exeception
//            // CassandraUtils.handleException(e);
//            throw KapuaException.internalError(exc);
//        }
//    }
//
//    public long countMessage(String scopeName, MessageQuery query)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notEmptyOrNull(scopeName, "scopeName");
//        ArgumentValidator.notNull(query, "query");
        //
//        //
//        // Check Access
//        checkDataAccess(scopeName, DatastoreServiceAction.READ);
        //
//        try {
//            String everyIndex = EsUtils.getAnyIndexName(scopeName);
//            long count = EsMessageDAO.connection(EsClient.getcurrent())
//                                     .instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
//                                     .count(query);
        //
//            return count;
//        }
//        catch (Exception exc) {
//            // TODO manage exeception
//            // CassandraUtils.handleException(e);
//            throw KapuaException.internalError(exc);
//        }
//    }
            //
//// 
////    public MessageListResult findMessagesByTopic(String accountName, String topic, MessageQuery query)
////        throws KapuaException
////    {
////        //
////        // Argument Validation
////        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
////        ArgumentValidator.notEmptyOrNull(topic, "topic");
////        ArgumentValidator.notNull(query, "query");
////        ArgumentValidator.notNull(query.getPredicate(), "query.predicate");
////        ArgumentValidator.dateRange(query.getPredicate().getTimestampStart(), query.getPredicate().getTimestampEnd());
////
////        //
////        // Check Access
////        checkDataAccess(accountName, MessageStoreServiceAction.READ);
////
////        //
////        // Do the find
////        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
////        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
////
////        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
////            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
////            return new MessageListResultImpl();
////        }
////
////        //
////        // If eurotech set ttl equals to 30 days
////        if (ttl < 0) {
////            ttl = EUROTECH_TTL_MILLIS;
////        }
////
////        // MessageDAO: prepare query
////        long end = KapuaDateUtils.getEdcSysDate().getTime();
////        long start = end - ttl;
////        if (query.getPredicate().getTimestampStart() != -1) {
////            start = query.getPredicate().getTimestampStart();
////        }
////        if (query.getEndDate() != -1) {
////            end = query.getEndDate();
////        }
////        if (query.getKeyOffset() != null) {
////            if (!query.isAscendingSort())
////                end = (Long) query.getKeyOffset();
////            else
////                start = (Long) query.getKeyOffset() - 1; // if not -1 we miss one record
////        }
////        
////        try {
////        	MessageListResult result = null;
////        	result = this.datastoreFacade.findMessagesByTopic(accountName,
////	        								  		   topic,
////	        								  		   start,
////	        								  		   end,
////	        								  		   query.getOffset(),
////	        								  		   query.getLimit(),
////	        								  		   query.isAscendingSort(),
////	        								  		   query.getFetchStyle(),
////	        								  		   query.isAskTotalCount());
////        
////        	return result;
////        } catch (Exception exc) {
////        	// TODO manage exeception
////            //CassandraUtils.handleException(e);
////        	throw KapuaException.internalError(exc);
////        }
////    }
////
////    public MessageListResult findMessagesByAsset(String accountName, String asset, MessageQuery query)
////        throws KapuaException
////    {
////        //
////        // Argument Validation
////        ArgumentValidator.notNull(accountName, "accountName");
////        ArgumentValidator.notNull(asset, "asset");
////        ArgumentValidator.notNull(query, "query");
////        ArgumentValidator.dateRange(query.getStartDate(), query.getEndDate());
////
////        //
////        // Check Access
////        checkDataAccess(accountName, MessageStoreServiceAction.READ);
////
////        //
////        // Do the find
////        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
////        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
////
////        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
////            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
////            return new MessageListResultImpl();
////        }
////
////        //
////        // If eurotech set ttl equals to 30 days
////        if (ttl < 0) {
////            ttl = EUROTECH_TTL_MILLIS;
////        }
////
////        //
////        // MessageDAO: prepare query
////        long end = KapuaDateUtils.getEdcSysDate().getTime();
////        long start = end - ttl;
////        if (query.getStartDate() != -1) {
////            start = query.getStartDate();
////        }
////        if (query.getEndDate() != -1) {
////            end = query.getEndDate();
////        }
////        if (query.getKeyOffset() != null) {
////            if (!query.isAscendingSort())
////                end = (Long) query.getKeyOffset();
////            else
////                start = (Long) query.getKeyOffset() - 1; // if not -1 we miss one record
////        }
////        
////        try {
////        	MessageListResult result = null;
////        	result = this.datastoreFacade.findMessagesByAsset(accountName,
////	        								  		   asset,
////	        								  		   start,
////	        								  		   end,
////	        								  		   query.getOffset(),
////	        								  		   query.getLimit(),
////	        								  		   query.isAscendingSort(),
////	        								  		   query.getFetchStyle(),
////	        								  		   query.isAskTotalCount());
////        
////        	return result;
////        } catch (Exception exc) {
////        	// TODO manage exeception
////            //CassandraUtils.handleException(e);
////        	throw KapuaException.internalError(exc);
////        }
////    }
////
////    public MessageListResult findMessagesByAccount(String accountName, MessageQuery query)
////        throws KapuaException
////    {
////        //
////        // Argument Validation
////        ArgumentValidator.notNull(accountName, "accountName");
////        ArgumentValidator.notNull(query, "query");
////
////        //
////        // Check Access
////        checkDataAccess(accountName, MessageStoreServiceAction.READ);
////
////        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
////        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
////
////        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
////            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
////            return new MessageListResultImpl();
////        }
////        //
////        // If eurotech set ttl equals to 30 days
////        if (ttl < 0) {
////            ttl = EUROTECH_TTL_MILLIS;
////        }
////        
////        try {
////            
////            MessageQuery localQuery = query.copy();
////            // get one plus (if there is one) to later get the next key value 
////            int limit = query.getLimit() + 1;
////            query.setLimit(limit);
////            
////            String everyIndex = EsUtils.getAnyIndexName(accountName);
////            SearchHits searchHits = EsMessageDAO.connection(EsClient.getcurrent())
////                                                .instance(everyIndex, EsSchema.MESSAGE_TYPE_NAME)
////                                                .query(localQuery);
////            
////            if (searchHits == null || searchHits.getTotalHits() == 0)
////                return new MessageListResultImpl();
////
////            int i = 0;
////            int searchHitsSize = searchHits.getHits().length;
////
////            List<Message> messages = new ArrayList<Message>();
////            MessageBuilder msgBuilder = new MessageBuilder();
////            for(SearchHit searchHit:searchHits.getHits()) {
////                if (i < limit) {
////                    Message message = msgBuilder.build(searchHit, query.getFetchStyle()).getMessage();
////                    messages.add(message);
////                }
////                i++;
////            }
////            
////            // TODO check equivalence with CX with Pierantonio
////            // TODO what is this nextKey
////            Object nextKey = null;
////            if (searchHitsSize > limit) {
////                Message lastMessage = msgBuilder.build(searchHits.getHits()[searchHitsSize-1], query.getFetchStyle()).getMessage();
////                nextKey = lastMessage.getTimestamp().getTime();
////            }
////
////            // TODO verifiy total count
////            long totalCount = 0;
////            if (query.isAskTotalCount()) {
////                // totalCount = MessageDAO.findByTopicCount(keyspace, topic, start, end); 
////                totalCount = searchHits.getTotalHits();
////            }
////            
////            if (totalCount > Integer.MAX_VALUE)
////                throw new Exception("Total hits exceeds integer max value");
////            
////            MessageListResultImpl result = new MessageListResultImpl(nextKey, (int)totalCount);
////            result.addAll(messages);
////            
////        } catch (Exception exc) {
////        	// TODO manage exeception
////            //CassandraUtils.handleException(e);
////        	throw KapuaException.internalError(exc);
////        }
////    }
////
////    public MessageListResult findMessagesByID(String accountName, MessageQuery query)
////        throws KapuaException
////    {
////        //
////        // Argument Validation
////        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
////        ArgumentValidator.notNull(query, "query");
////        ArgumentValidator.notNull(query.getKeys(), "query.keys");
////
////        //
////        // Check Access
////        checkDataAccess(accountName, MessageStoreServiceAction.READ);
////
////        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
////        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
////
////        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
////            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
////            return new MessageListResultImpl();
////        }
////        
////        try {
////        	MessageListResult result = null;
////        	result = this.datastoreFacade.findMessagesById(accountName,
////        											   query.getKeys(),
////	        								  		   query.getLimit(),
////	        								  		   query.getFetchStyle());
////        
////        	return result;
////        } catch (Exception exc) {
////        	// TODO manage exeception
////            //CassandraUtils.handleException(e);
////        	throw KapuaException.internalError(exc);
////        }
////    }
////
////    public <T> MessageListResult findMessagesByMetric(String accountName, String topic, KapuaMetricQuery<T> query)
////        throws KapuaException
////    {
////        //
////        // Argument Validation
////        ArgumentValidator.notNull(accountName, "accountName");
////        ArgumentValidator.notNull(query, "query");
////        ArgumentValidator.dateRange(query.getStartDate(), query.getEndDate());
////
////        //
////        // Check Access
////        checkDataAccess(accountName, MessageStoreServiceAction.READ);
////
////        //
////        // Do the find
////        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
////        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
////
////        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
////            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
////            return new MessageListResultImpl();
////        }
////
////        //
////        // If eurotech set ttl equals to 30 days
////        if (ttl < 0) {
////            ttl = EUROTECH_TTL_MILLIS;
////        }
////
////    	Date sysDate = KapuaDateUtils.getEdcSysDate();
////    	
////        long endTime = sysDate.getTime();
////        long startTime = endTime - ttl;
////        if (query.getStartDate() != -1) {
////            startTime = query.getStartDate();
////        }
////        if (query.getEndDate() != -1) {
////            endTime = query.getEndDate();
////        }
////        if (query.getKeyOffset() != null) {
////            startTime = ((Date) query.getKeyOffset()).getTime();
////        }
////        T start = query.getMinValue();
////        T end = query.getMaxValue();
////
////        
////        try {
////        	MessageListResult result = null;
////        	result = this.datastoreFacade.findMessagesByMetric(accountName,
////        														topic,
////        														query.getName(),
////        														query.getType(),
////				        								  		startTime,
////				        								  		endTime,
////				        								  		(Object)start,
////				        								  		(Object)end,
////				        								  		query.getIndexOffset(),
////				        								  		query.getLimit(),
////				        								  		query.isAscendingSort(),
////				        								  		query.getFetchStyle(),
////				        								  		query.isAskTotalCount());
////        	// TODO Ask pierantonio for this
////        	query.setIndexOffset(0);
////
////        	return result;
////        } catch (Exception exc) {
////        	// TODO manage exeception
////            //CassandraUtils.handleException(e);
////        	throw KapuaException.internalError(exc);
////        }
////    }
////
////    public long getTotalNumberOfMessages(String accountName)
////        throws KapuaException
////    {
////        //
////        // Argument Validation
////        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
////
////        //
////        // Check Access
////        checkDataAccess(accountName, MessageStoreServiceAction.READ);
////
////        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
////        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
////
////        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
////            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
////            return 0;
////        }
////        //
////        // If eurotech set ttl equals to 30 days
////        if (ttl < 0) {
////            ttl = EUROTECH_TTL_MILLIS;
////        }
////
////        
////        try {
////        	long result = this.datastoreFacade.findMessagesByAccountCount(accountName);
////        	return result;
////        } catch (Exception exc) {
////        	// TODO manage exeception
////            //CassandraUtils.handleException(e);
////        	throw KapuaException.internalError(exc);
////        }
////    }
            //
//    public long deleteMessages(String account, long accountId, String topic, int startYear, int startWeek, int endYear, int endWeek, boolean purge)
//        throws KapuaException
//    {
//        throw KapuaException.internalError("***** Implement me Please *****");
////        //
////        // Argument Validation
////        ArgumentValidator.notEmptyOrNull(account, "account");
////        ArgumentValidator.notNull(startWeek, "startWeek");
////        ArgumentValidator.notNull(endWeek, "endWeek");
////
////        //
////        // Check Access
////        checkDataAccess(account, DatastorePermAction.DELETE);
////
////        //
////        // Prepare the TaskCreator
////        DoDeleteMessages doDeleteMessages = new DoDeleteMessages();
////        doDeleteMessages.setAccount(account);
////        doDeleteMessages.setStartWeek(startWeek);
////
////        Properties props = new Properties();
////        props.setProperty("account", account);
////        props.setProperty("startYear", String.valueOf(startYear));
////        props.setProperty("endYear", String.valueOf(endYear));
////        props.setProperty("startWeek", String.valueOf(startWeek));
////        props.setProperty("endWeek", String.valueOf(endWeek));
////        props.setProperty("purge", String.valueOf(purge));
////        if ((topic != null) && (topic.compareTo(account + "/+/#") != 0))
////            props.setProperty("topic", topic);
////
////        TaskCreator taskCreator = new TaskCreator();
////        taskCreator.setAccountId(accountId);
////        taskCreator.setName("deleteMessages-" + account);
////        taskCreator.setProperties(props);
////        taskCreator.setTaskExecutorClassName(doDeleteMessages.getClass().getName());
////
////        //
////        // Create the Task
////        TaskService taskService = ServiceLocator.getInstance().getTaskService();
////        Task task = taskService.create(taskCreator);
////        taskService.queue(task.getId());
////        return task.getId();
//    }
////
////    public void deleteSingleMessage(String accountName, String topic, long date, String uuid)
////        throws KapuaException
////    {
////        //
////        // Argument Validation
////        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
////        ArgumentValidator.notNull(topic, "topic");
////        ArgumentValidator.notNegative(date, "date");
////        ArgumentValidator.notNull(uuid, "uuid");
////
////        if (topic.contains("#") || topic.contains("+"))
////            throw new KapuaException(KapuaErrorCodes.ILLEGAL_ARGUMENT, null, "Delete single message cannot contain # or + wildcards");
////
////        //
////        // Check access
////        checkDataAccess(accountName, MessageStoreServiceAction.DELETE);
////
////        //
////        // Do the find
////        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
////        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
////
////        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
////            logger.debug("Storage not enabled for account {}, return", accountName);
////            return;
////        }
////        
////        try {
////        	
////        	// TODO Ask Pierantonio about deletion of metrics ?? Still needed with elestisearch
////        	/*
////            //
////            // Find metrics for topic
////            KapuaListResult<KapuaMetricInfo<?>> metricResult = null;
////            EdcQuery metricQuery = new EdcQuery();
////            metricQuery.setLimit(Integer.MAX_VALUE - 1);
////            metricResult = findMetricsByTopic(accountName, topic, metricQuery);
////
////            //
////            // Delete row in every metric for topic
////            List<String> ls = null;
////            if ((uuid != null) && (!uuid.isEmpty())) {
////                ls = new ArrayList<String>();
////                ls.add(uuid);
////            }
////            for (int i = 0; i < metricResult.size(); i++) {
////                Class<?> c = metricResult.get(i).getType();
////                MetricDAO.deleteMetricsValueByTopicByDate(keyspace, accountName, topic, metricResult.get(i).getName(), c, date, date, ls);
////                MetricDAO.deleteMetricsTimestampByTopicByDate(keyspace, accountName, topic, metricResult.get(i).getName(), c, date, date, ls);
////            }
////			*/
////        	
////            this.datastoreFacade.deleteMessageById(accountName,
////            										uuid);
////            
////        } catch (Exception exc) {
////        	// TODO manage exeception
////            //CassandraUtils.handleException(e);
////        	throw KapuaException.internalError(exc);
////        }
////    }
        //
//    public void executeDeleteMessages(String accountName, String topic, int startYear, int startWeek, int endYear, int endWeek, boolean purge, KapuaProgressListener pl)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
        //
//        //
//        // Check access
//        checkDataAccess(accountName, DatastoreServiceAction.DELETE);
        //
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long daysTtl = accountServicePlan.getDataTimeToLive();
    //
//        if (!accountServicePlan.getDataStorageEnabled() || daysTtl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, return", accountName);
//            return;
//        }
    //
//        if (daysTtl == LocalServicePlan.UNLIMITED) {
//            daysTtl = EUROTECH_TTL_DAYS;
//        }
        //
//        int weeksTtl = (int) Math.ceil((double) daysTtl / 7.0);
//        long ttl = daysTtl * DAY_MILLIS;
        //
//        long t0 = KapuaDateUtils.getEdcSysDate().getTime();
        //
        //
//        try {
        //
//        	boolean purgeMetadata = purge;
//        	this.datastoreFacade.deleteMessages(accountName, topic, 
//        										 startYear, startWeek,
//        										 endYear, endWeek, 
//        										 weeksTtl, ttl, purgeMetadata, pl);
        //
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
        //
//    // -----------------------------------------------------------------------------------------
//    //
//    // Metrics
//    //
//    // -----------------------------------------------------------------------------------------
//    public KapuaListResult<KapuaMetricInfo<?>> findMetricsByTopic(String accountName, String topic, KapuaQuery query)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(accountName, "accountName");
//        ArgumentValidator.notNull(topic, "topic");
//        ArgumentValidator.notNull(query, "query");
        //
//        //
//        // Check Access
//        checkDataAccess(accountName, DatastoreServiceAction.READ);
        //
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
        //
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
//            return new KapuaListResult<KapuaMetricInfo<?>>();
//        }
        //
//        try {
        //
//        	KapuaListResult<KapuaMetricInfo<?>> result = null;
//        	result = this.datastoreFacade.findMetricsByTopic(accountName,
//											                  topic,
//											                  query.getIndexOffset(),
//											                  query.getLimit());
        //
//        	return result;
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
        //
//    public <T> KapuaListResult<KapuaMetricValue> findMetricsByValue(String accountName, String topic, KapuaMetricQuery<T> query)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(accountName, "accountName");
//        ArgumentValidator.notNull(query, "query");
    //
//        //
//        // Check Access
//        checkDataAccess(accountName, DatastoreServiceAction.READ);
    //
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
        //
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
//            return new KapuaListResult<KapuaMetricValue>();
//        }
        //
//        // TODO check the indexing mode is by value
        //
//        //
//        // If eurotech set ttl equals to 30 days
//        if (ttl < 0) {
//            ttl = EUROTECH_TTL_MILLIS;
//        }
        //
//        try {
//        	//TODO review time span definition
//            long endTime = System.currentTimeMillis();
//            long startTime = endTime - ttl;
        //
//        	KapuaListResult<KapuaMetricValue> result = null;
//        	result = this.datastoreFacade.findMetricsByValue(accountName,
//											                 topic,
//											                 query.getName(),
//											                 query.getType(),
//											                 startTime,
//											                 endTime,
//											                 query.getMinValue(),
//											                 query.getMaxValue(),
//											                 query.getIndexOffset(),
//											                 query.getLimit(),
//											                 query.isAscendingSort());
        //
//        	return result;
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
        //
//    public <T> KapuaListResult<KapuaMetricValue> findMetricsByTimestamp(String accountName, String topic, KapuaMetricQuery<T> query)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(accountName, "accountName");
//        ArgumentValidator.notNull(query, "query");
        //
//        //
//        // Check Access
//        checkDataAccess(accountName, DatastoreServiceAction.READ);
        //
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
    //
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
//            return new KapuaListResult<KapuaMetricValue>();
//        }
    //
//        // TODO check the indexing mode is by timestamp (otherwise an error should be raised).
        //
//        try {
//        	//TODO review time span definition
//            long endTime = query.getEndDate();
//            long startTime = query.getStartDate();
        //
//        	KapuaListResult<KapuaMetricValue> result = null;
//        	result = this.datastoreFacade.findMetricsByTimestamp(accountName,
//												                  topic,
//												                  query.getName(),
//												                  query.getType(),
//												                  startTime,
//												                  endTime,
//												                  query.getIndexOffset(),
//												                  query.getLimit(),
//												                  query.isAscendingSort());
        //
//        	return result;
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
        //
//    @SuppressWarnings("unchecked")
//    public <T> KapuaMetricInfo<T> getMetricLastValue(String accountName, String topic, String metricName, Class<T> metricType)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(accountName, "accountName");
//        ArgumentValidator.notNull(topic, "topic");
//        ArgumentValidator.notNull(metricName, "metricName");
//        ArgumentValidator.notNull(metricType, "metricType");
        //
//        //
//        // Check Access
//        checkDataAccess(accountName, DatastoreServiceAction.READ);
        //
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
    //
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
//            return new KapuaMetricInfo<T>();
//        }
    //
//        //
//        // Find
//        MessageQueryImpl query = new MessageQueryImpl();
//        query.setLimit(1); // return the last message received under this topic
//        query.setFetchStyle(MessageFetchStyle.METADATA_HEADERS);
//        MessageListResult results = findMessagesByTopic(accountName, topic, query);
//        T result = null;
//        for (Message message : results) {
//            Payload payload = message.getPayload();
//            if (payload != null && payload.getMetrics() != null) {
//                Iterator<String> iter = payload.getMetrics().keySet().iterator();
//                while (iter.hasNext()) {
//                    String metric = iter.next();
//                    if (metricName.contains(metric)) {
//                        result = (T) payload.getMetrics().get(metric);
//                    }
//                }
//            }
//        }
//        return new KapuaMetricInfo<T>(metricName, metricType, result);
//    }
        //
//    // -----------------------------------------------------------------------------------------
//    //
//    // Topic
//    //
//    // -----------------------------------------------------------------------------------------
//    public KapuaListResult<KapuaTopicInfo> findTopicsByAccount(String accountName, KapuaTopicQuery query)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(accountName, "accountName");
//        ArgumentValidator.notNull(query, "query");
    //
//        //
//        // Check access
//        checkDataAccess(accountName, DatastoreServiceAction.READ);
    //
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
        //
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
//            return new KapuaListResult<KapuaTopicInfo>();
//        }
//        
//        try {
//        	
//        	KapuaListResult<KapuaTopicInfo> result = null;
//        	result = this.datastoreFacade.findTopicsByAccount(accountName,
//        													   query.getPrefix(),
//											                   query.getIndexOffset(),
//											                   query.getLimit());
//        
//        	return result;
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
//
//    public KapuaListResult<KapuaTopicInfo> findTopicsByAsset(String accountName, String asset, KapuaTopicQuery query)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(accountName, "accountName");
//        ArgumentValidator.notNull(asset, "asset");
//        ArgumentValidator.notNull(query, "query");
//
//        //
//        // Check access
//        checkDataAccess(accountName, DatastoreServiceAction.READ);
//
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
//
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
//            return new KapuaListResult<KapuaTopicInfo>();
//        }
//        
//        try {
//        	
//        	KapuaListResult<KapuaTopicInfo> result = null;
//        	result = this.datastoreFacade.findTopicsByAsset(accountName,
//        													 asset,
//        													 query.getPrefix(),
//											                 query.getIndexOffset(),
//											                 query.getLimit());
//        
//        	return result;
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
//
//    /**
//     * Deletes data from MessagesByTopic, MetricsByTopic, TopicsByAccount and MetricsByValue/Timestamp.
//     */
//    public void deleteTopics(String accountName)
//        throws KapuaException
//    {
//        //
//        // Argument Validator
//        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
//
//        //
//        // Check access
//        checkDataAccess(accountName, DatastoreServiceAction.DELETE);
//
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
//
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, return", accountName);
//            return;
//        }
//
//        long daysTtl = accountServicePlan.getDataTimeToLive();
//
//        // If eurotech set ttl equals to days from it creations
//        if (daysTtl < 0) {
//            daysTtl = EUROTECH_TTL_DAYS;
//        }
//
//        int weeksTtl = (int) Math.ceil((double) daysTtl / 7.0);
//        
//        
//        try {
//
//        	Date endDate = KapuaDateUtils.getEdcSysDate();
//        	this.datastoreFacade.deleteTopics(accountName, weeksTtl, ttl, endDate.getTime());
//        	
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
//
//    // -----------------------------------------------------------------------------------------
//    //
//    // Asset
//    //
//    // -----------------------------------------------------------------------------------------
//    public KapuaListResult<KapuaAssetInfo> findAssetsByAccount(String accountName, StorableQuery query)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notNull(accountName, "accountName");
//        ArgumentValidator.notNull(query, "query");
//
//        //
//        // Check access
//        checkDataAccess(accountName, DatastoreServiceAction.READ);
//
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
//
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
//            return new KapuaListResult<KapuaAssetInfo>();
//        }
//        
//        try {
//        	
//        	KapuaListResult<KapuaAssetInfo> result = null;
//        	result = this.datastoreFacade.findAssetsByAccount(accountName,
//											                   query.getOffset(),
//											                   query.getLimit());
//        
//        	return result;
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
//
//    public void deleteAssets(String accountName)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
//
//        //
//        // Check access
//        checkDataAccess(accountName, DatastoreServiceAction.DELETE);
//
//        //
//        // Do the find
//        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
//        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;
//
//        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
//            logger.debug("Storage not enabled for account {}, return", accountName);
//            return;
//        }
//        
//        try {
//
//        	Date endDate = KapuaDateUtils.getEdcSysDate();
//        	this.datastoreFacade.deleteAssets(accountName, ttl, endDate.getTime());
//        	
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//    }
//
//    // -----------------------------------------------------------------------------------------
//    //
//    // Utility methods
//    //
//    // -----------------------------------------------------------------------------------------
//    public MetricsIndexBy getMetricsIndexBy(String accountName)
//        throws KapuaException
//    {
//        LocalServicePlan plan = getAccountServicePlan(accountName);
//        return plan.getMetricsIndexBy();
//    }
//
//    public void resetCache(String accountName, String topic)
//        throws KapuaException
//    {
//        //
//        // Argument Validation
//        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
//
//        try {
//        	
//        	this.datastoreFacade.resetCache(accountName, topic);
//        	
//        } catch (Exception exc) {
//        	// TODO manage exeception
//            //CassandraUtils.handleException(e);
//        	throw KapuaException.internalError(exc);
//        }
//        
//    }
//
//    // -----------------------------------------------------------------------------------------
//    //
//    // Private methods
//    //
//    // -----------------------------------------------------------------------------------------
//
//    private void checkDataAccess(String accountName, DatastoreServiceAction action)
//        throws KapuaException
//    {
//        //
//        // Check Access
//        KapuaLocator serviceLocator = KapuaLocator.getInstance();
//        AccountService accountService = serviceLocator.getService(AccountService.class);
//        AuthorizationService authorizationService = serviceLocator.getService(AuthorizationService.class);
//        PermissionFactory permissionFactory = serviceLocator.getFactory(PermissionFactory.class);
//
//        Account account = accountService.findByName(accountName);
//
//        //TODO add enum for actions
//        Permission permission = permissionFactory.newPermission("data", action.key(), account.getId());
//        authorizationService.checkPermission(permission);
//    }
//
//    private LocalServicePlan getAccountServicePlan(String accountName)
//        throws KapuaException
//    {
//        KapuaLocator locator = KapuaLocator.getInstance();
//        AccountService accountService = locator.getService(AccountService.class);
//        Account account = accountService.findByName(accountName);
//        return new LocalServicePlan(this.configurationService.getConfigValues(account.getId()));
//    }
//
//}
