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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.kapua.KapuaErrorCodes;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.commons.util.KapuaDateUtils;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.message.KapuaInvalidTopicException;
import org.eclipse.kapua.message.KapuaMessage;
import org.eclipse.kapua.message.KapuaPayload;
import org.eclipse.kapua.message.KapuaPosition;
import org.eclipse.kapua.message.KapuaTopic;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.service.account.Account;
import org.eclipse.kapua.service.account.AccountService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.Permission;
import org.eclipse.kapua.service.authorization.PermissionFactory;
import org.eclipse.kapua.service.config.KapuaConfigurableService;
import org.eclipse.kapua.service.datastore.StorableQuery;
import org.eclipse.kapua.service.datastore.internal.MessageStoreServiceAction;
import org.eclipse.kapua.service.datastore.internal.config.KapuaDatastoreConfig;
import org.eclipse.kapua.service.datastore.internal.config.KapuaDatastoreConfigKeys;
import org.eclipse.kapua.service.datastore.internal.model.DataIndexBy;
import org.eclipse.kapua.service.datastore.internal.model.MessageImpl;
import org.eclipse.kapua.service.datastore.internal.model.MetricsIndexBy;
import org.eclipse.kapua.service.datastore.internal.model.TopicImpl;
import org.eclipse.kapua.service.datastore.model.Message;
import org.eclipse.kapua.service.datastore.model.MessageCreator;
import org.eclipse.kapua.service.datastore.model.Payload;
import org.eclipse.kapua.service.datastore.model.Position;
import org.eclipse.kapua.service.datastore.model.Topic;
import org.eclipse.kapua.service.datastore.model.query.MessageFetchStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsMessageStoreServiceImpl
{
    private static final Logger logger = LoggerFactory.getLogger(EsMessageStoreServiceImpl.class);

    /**
     * This is the max value that an account can have as TTL in days.
     * 
     * The Cassandra driver 'Hector' accepts a {@code int} value as ttl (measured in seconds),
     * so TTL for data needs to be limited!
     */
    public static final int      MAX_DAYS_TTL_VALUE       = Integer.MAX_VALUE / 60 / 60 / 24;

    private static final long    DAY_SECS                 = 24 * 60 * 60;
    private static final long    DAY_MILLIS               = DAY_SECS * 1000;

    private static final long    EUROTECH_TTL_DAYS        = 30;
    private static final long    EUROTECH_TTL_SECS        = EUROTECH_TTL_DAYS * DAY_SECS;
    private static final long    EUROTECH_TTL_MILLIS      = EUROTECH_TTL_DAYS * DAY_MILLIS;

    private KapuaConfigurableService configurationService;
    private EsDatastoreAdapter     datastoreFacade;
    private int                  maxTopicDepth;
    private static int           counter                = 0;
    private static long          timingProfileThreshold = 1000; // in milliseconds
    private static boolean       enableTimingProfile    = false;

    public EsMessageStoreServiceImpl(KapuaConfigurableService configurationService)
    {
        this.configurationService = configurationService;
		datastoreFacade = new EsDatastoreAdapter();
		KapuaDatastoreConfig config = KapuaDatastoreConfig.getInstance();
        maxTopicDepth = config.getInt(KapuaDatastoreConfigKeys.CONFIG_TOPIC_MAX_DEPTH);
        enableTimingProfile = config.getBoolean(KapuaDatastoreConfigKeys.CONFIG_DATA_STORAGE_ENABLE_TIMING_PROFILE, false);
        timingProfileThreshold = config.getLong(KapuaDatastoreConfigKeys.CONFIG_DATA_STORAGE_TIMING_PROFILE_THRESHOLD, 1000L);
    }

    public String store(String scopeName, MessageCreator messageCreator)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeName, "scopeName");
        ArgumentValidator.notNull(messageCreator, "messageCreator");
        
        KapuaMessage kapuaMessage = null;
        try 
        {
            kapuaMessage = MessageConverter.toKapuaMessage(messageCreator);
        }
        catch (KapuaInvalidTopicException exc)
        {
            throw KapuaException.internalError(exc);
        }
        
           
        String forceUUID = UUID.randomUUID().toString();
        this.storeMessageImpl(scopeName, kapuaMessage, forceUUID);
        return forceUUID;
    }
    
    public Message find(String scopeName, String uuid, MessageFetchStyle fetchStyle) throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeName, "scopeName");
        ArgumentValidator.notNull(uuid, "uuid");
        
        KapuaMessageQuery q = new KapuaMessageQuery();
        q.addKey(uuid);
        q.setLimit(1);
        q.setFetchStyle(fetchStyle);
        
        KapuaListResult<KapuaMessage> result = this.findMessagesByID(scopeName, q);
        if (result == null || result.size() == 0)
            return null;
        
        KapuaMessage kapuaMessage = result.get(0);
        Topic topic = new TopicImpl();
        topic.setTopicName(topic.getTopicName());
        MessageImpl message = new MessageImpl(kapuaMessage.getUuid(), kapuaMessage.getTimestamp(), topic); 
        return message;
    }
    
    private void storeMessageImpl(String accountName, KapuaMessage message, String forceUUID)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(message, "message");
        
        if (!message.hasEdcPayload())
        	throw new KapuaException(KapuaErrorCodes.ILLEGAL_ARGUMENT); // TODO find a better exception
        
        //
        // Store the message
        Date start = KapuaDateUtils.getEdcSysDate();

        boolean getTiming = ((counter++ % 100) == 0);
        boolean getTimingProfile = enableTimingProfile && getTiming;
        LinkedHashMap<String, Long> timingProfile = null;

        if (getTimingProfile) {
            timingProfile = new LinkedHashMap<String, Long>();
            timingProfile.put("start", KapuaDateUtils.getEdcSysDate().getTime());
        }

        //
        // Check Storage Access
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);

        long ttl = accountServicePlan.getDataTimeToLive() * DAY_SECS;
        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            String msg = String.format("Storage not enabled for account %s, not storing message", accountName);
            logger.debug(msg);
            throw KapuaException.internalError(msg);
        }

        // If eurotech set ttl equals to 1 month
        if (ttl < 0) {
            ttl = EUROTECH_TTL_SECS;
        }

        if (getTimingProfile) {
            timingProfile.put("Parameter validation", KapuaDateUtils.getEdcSysDate().getTime());
        }

        // connection
        if (getTimingProfile) {
            timingProfile.put("Cassandra connection", KapuaDateUtils.getEdcSysDate().getTime());
        }

        // MessageDAO: prepare the store parameters
        Date capturedOn = null;
        if (message.hasEdcPayload()) {
            capturedOn = message.getKapuaPayload().getTimestamp();
        }

        long currentDate = KapuaDateUtils.getEdcSysDate().getTime();
        
        long receivedOn = currentDate;
        if (message.getTimestamp() != null)
        	receivedOn = message.getTimestamp().getTime();
        
        // Overwrite timestamp if necessary
        // Use the account service plan to determine whether we will give precede to the device time
        long indexedOn = currentDate;
        if (DataIndexBy.DEVICE_TIMESTAMP.equals(accountServicePlan.getDataIndexBy()) && capturedOn != null)
        	indexedOn = capturedOn.getTime();
        
        if (getTimingProfile)
            timingProfile.put("MessageDAO - prepare the store parameters", KapuaDateUtils.getEdcSysDate().getTime());
        
        // MessageDAO: query
        try {
        	
        	this.datastoreFacade.storeMessage(accountName, 
        									   message, 
        									   maxTopicDepth, 
        									   indexedOn, 
        									   receivedOn,
        									   ttl, 
        									   forceUUID, 
        									   timingProfile, 
        									   accountServicePlan.getMetricsIndexBy());
        }
        catch (Exception e) { // TODO create e new datastore exception
        	// TODO manage execeptions
            //CassandraUtils.handleException(e);
        	// TODO Remove
        	e.printStackTrace();
        	throw KapuaException.internalError(e);
        }

        // get some timing
        Date end = KapuaDateUtils.getEdcSysDate();
        if (getTiming) {
            long elapsed = end.getTime() - start.getTime();
            logger.info("storeMessage in {} ms", elapsed);
            if (getTimingProfile && elapsed > timingProfileThreshold) {
                long prevTime = -1;
                for (String key : timingProfile.keySet()) {
                    Long nextTime = timingProfile.get(key);
                    if (prevTime >= 0) {
                        logger.debug(" {}: {} ms", key, nextTime - prevTime);
                    }
                    prevTime = nextTime;
                }
            }
        }
    }

    
    // -----------------------------------------------------------------------------------------
    //
    // Messages
    //
    // -----------------------------------------------------------------------------------------
    public KapuaListResult<KapuaMessage> findMessagesByTopic(String accountName, String topic, KapuaMessageQuery query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
        ArgumentValidator.notEmptyOrNull(topic, "topic");
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.dateRange(query.getStartDate(), query.getEndDate());

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaMessage>();
        }

        //
        // If eurotech set ttl equals to 30 days
        if (ttl < 0) {
            ttl = EUROTECH_TTL_MILLIS;
        }

        // MessageDAO: prepare query
        long end = KapuaDateUtils.getEdcSysDate().getTime();
        long start = end - ttl;
        if (query.getStartDate() != -1) {
            start = query.getStartDate();
        }
        if (query.getEndDate() != -1) {
            end = query.getEndDate();
        }
        if (query.getKeyOffset() != null) {
            if (!query.isAscendingSort())
                end = (Long) query.getKeyOffset();
            else
                start = (Long) query.getKeyOffset() - 1; // if not -1 we miss one record
        }
        
        try {
        	KapuaListResult<KapuaMessage> result = null;
        	result = this.datastoreFacade.findMessagesByTopic(accountName,
	        								  		   topic,
	        								  		   start,
	        								  		   end,
	        								  		   query.getIndexOffset(),
	        								  		   query.getLimit(),
	        								  		   query.isAscendingSort(),
	        								  		   query.getFetchStyle(),
	        								  		   query.isAskTotalCount());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public KapuaListResult<KapuaMessage> findMessagesByAsset(String accountName, String asset, KapuaMessageQuery query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(asset, "asset");
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.dateRange(query.getStartDate(), query.getEndDate());

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaMessage>();
        }

        //
        // If eurotech set ttl equals to 30 days
        if (ttl < 0) {
            ttl = EUROTECH_TTL_MILLIS;
        }

        //
        // MessageDAO: prepare query
        long end = KapuaDateUtils.getEdcSysDate().getTime();
        long start = end - ttl;
        if (query.getStartDate() != -1) {
            start = query.getStartDate();
        }
        if (query.getEndDate() != -1) {
            end = query.getEndDate();
        }
        if (query.getKeyOffset() != null) {
            if (!query.isAscendingSort())
                end = (Long) query.getKeyOffset();
            else
                start = (Long) query.getKeyOffset() - 1; // if not -1 we miss one record
        }
        
        try {
        	KapuaListResult<KapuaMessage> result = null;
        	result = this.datastoreFacade.findMessagesByAsset(accountName,
	        								  		   asset,
	        								  		   start,
	        								  		   end,
	        								  		   query.getIndexOffset(),
	        								  		   query.getLimit(),
	        								  		   query.isAscendingSort(),
	        								  		   query.getFetchStyle(),
	        								  		   query.isAskTotalCount());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public KapuaListResult<KapuaMessage> findMessagesByAccount(String accountName, KapuaMessageQuery query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.dateRange(query.getStartDate(), query.getEndDate());

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaMessage>();
        }
        //
        // If eurotech set ttl equals to 30 days
        if (ttl < 0) {
            ttl = EUROTECH_TTL_MILLIS;
        }

        //
        // MessageDAO: prepare query
        long end = KapuaDateUtils.getEdcSysDate().getTime();
        long start = end - ttl;
        if (query.getStartDate() != -1) {
            if (query.getStartDate() > start) // don't set a date older than ttl
                start = query.getStartDate();
        }
        if (query.getEndDate() != -1) {
            end = query.getEndDate();
        }
        if (query.getKeyOffset() != null) {
            if (!query.isAscendingSort())
                end = (Long) query.getKeyOffset();
            else
                start = (Long) query.getKeyOffset() - 1; // if not -1 we miss one record
        }
        
        try {
        	KapuaListResult<KapuaMessage> result = null;
        	result = this.datastoreFacade.findMessagesByAccount(accountName,
	        								  		   start,
	        								  		   end,
	        								  		   query.getIndexOffset(),
	        								  		   query.getLimit(),
	        								  		   query.isAscendingSort(),
	        								  		   query.getFetchStyle(),
	        								  		   query.isAskTotalCount());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public KapuaListResult<KapuaMessage> findMessagesByID(String accountName, KapuaMessageQuery query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getKeys(), "query.keys");

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaMessage>();
        }
        
        try {
        	KapuaListResult<KapuaMessage> result = null;
        	result = this.datastoreFacade.findMessagesById(accountName,
        											   query.getKeys(),
	        								  		   query.getLimit(),
	        								  		   query.getFetchStyle());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public <T> KapuaListResult<KapuaMessage> findMessagesByMetric(String accountName, String topic, KapuaMetricQuery<T> query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.dateRange(query.getStartDate(), query.getEndDate());

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaMessage>();
        }

        //
        // If eurotech set ttl equals to 30 days
        if (ttl < 0) {
            ttl = EUROTECH_TTL_MILLIS;
        }

    	Date sysDate = KapuaDateUtils.getEdcSysDate();
    	
        long endTime = sysDate.getTime();
        long startTime = endTime - ttl;
        if (query.getStartDate() != -1) {
            startTime = query.getStartDate();
        }
        if (query.getEndDate() != -1) {
            endTime = query.getEndDate();
        }
        if (query.getKeyOffset() != null) {
            startTime = ((Date) query.getKeyOffset()).getTime();
        }
        T start = query.getMinValue();
        T end = query.getMaxValue();

        
        try {
        	KapuaListResult<KapuaMessage> result = null;
        	result = this.datastoreFacade.findMessagesByMetric(accountName,
        														topic,
        														query.getName(),
        														query.getType(),
				        								  		startTime,
				        								  		endTime,
				        								  		(Object)start,
				        								  		(Object)end,
				        								  		query.getIndexOffset(),
				        								  		query.getLimit(),
				        								  		query.isAscendingSort(),
				        								  		query.getFetchStyle(),
				        								  		query.isAskTotalCount());
        	// TODO Ask pierantonio for this
        	query.setIndexOffset(0);

        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public long getTotalNumberOfMessages(String accountName)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return 0;
        }
        //
        // If eurotech set ttl equals to 30 days
        if (ttl < 0) {
            ttl = EUROTECH_TTL_MILLIS;
        }

        
        try {
        	long result = this.datastoreFacade.findMessagesByAccountCount(accountName);
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public long deleteMessages(String account, long accountId, String topic, int startYear, int startWeek, int endYear, int endWeek, boolean purge)
        throws KapuaException
    {
        throw KapuaException.internalError("***** Implement me Please *****");
//        //
//        // Argument Validation
//        ArgumentValidator.notEmptyOrNull(account, "account");
//        ArgumentValidator.notNull(startWeek, "startWeek");
//        ArgumentValidator.notNull(endWeek, "endWeek");
//
//        //
//        // Check Access
//        checkDataAccess(account, DatastorePermAction.DELETE);
//
//        //
//        // Prepare the TaskCreator
//        DoDeleteMessages doDeleteMessages = new DoDeleteMessages();
//        doDeleteMessages.setAccount(account);
//        doDeleteMessages.setStartWeek(startWeek);
//
//        Properties props = new Properties();
//        props.setProperty("account", account);
//        props.setProperty("startYear", String.valueOf(startYear));
//        props.setProperty("endYear", String.valueOf(endYear));
//        props.setProperty("startWeek", String.valueOf(startWeek));
//        props.setProperty("endWeek", String.valueOf(endWeek));
//        props.setProperty("purge", String.valueOf(purge));
//        if ((topic != null) && (topic.compareTo(account + "/+/#") != 0))
//            props.setProperty("topic", topic);
//
//        TaskCreator taskCreator = new TaskCreator();
//        taskCreator.setAccountId(accountId);
//        taskCreator.setName("deleteMessages-" + account);
//        taskCreator.setProperties(props);
//        taskCreator.setTaskExecutorClassName(doDeleteMessages.getClass().getName());
//
//        //
//        // Create the Task
//        TaskService taskService = ServiceLocator.getInstance().getTaskService();
//        Task task = taskService.create(taskCreator);
//        taskService.queue(task.getId());
//        return task.getId();
    }

    public void deleteSingleMessage(String accountName, String topic, long date, String uuid)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");
        ArgumentValidator.notNull(topic, "topic");
        ArgumentValidator.notNegative(date, "date");
        ArgumentValidator.notNull(uuid, "uuid");

        if (topic.contains("#") || topic.contains("+"))
            throw new KapuaException(KapuaErrorCodes.ILLEGAL_ARGUMENT, null, "Delete single message cannot contain # or + wildcards");

        //
        // Check access
        checkDataAccess(accountName, MessageStoreServiceAction.DELETE);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, return", accountName);
            return;
        }
        
        try {
        	
        	// TODO Ask Pierantonio about deletion of metrics ?? Still needed with elestisearch
        	/*
            //
            // Find metrics for topic
            KapuaListResult<KapuaMetricInfo<?>> metricResult = null;
            EdcQuery metricQuery = new EdcQuery();
            metricQuery.setLimit(Integer.MAX_VALUE - 1);
            metricResult = findMetricsByTopic(accountName, topic, metricQuery);

            //
            // Delete row in every metric for topic
            List<String> ls = null;
            if ((uuid != null) && (!uuid.isEmpty())) {
                ls = new ArrayList<String>();
                ls.add(uuid);
            }
            for (int i = 0; i < metricResult.size(); i++) {
                Class<?> c = metricResult.get(i).getType();
                MetricDAO.deleteMetricsValueByTopicByDate(keyspace, accountName, topic, metricResult.get(i).getName(), c, date, date, ls);
                MetricDAO.deleteMetricsTimestampByTopicByDate(keyspace, accountName, topic, metricResult.get(i).getName(), c, date, date, ls);
            }
			*/
        	
            this.datastoreFacade.deleteMessageById(accountName,
            										uuid);
            
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public void executeDeleteMessages(String accountName, String topic, int startYear, int startWeek, int endYear, int endWeek, boolean purge, KapuaProgressListener pl)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");

        //
        // Check access
        checkDataAccess(accountName, MessageStoreServiceAction.DELETE);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long daysTtl = accountServicePlan.getDataTimeToLive();

        if (!accountServicePlan.getDataStorageEnabled() || daysTtl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, return", accountName);
            return;
        }

        if (daysTtl == LocalServicePlan.UNLIMITED) {
            daysTtl = EUROTECH_TTL_DAYS;
        }

        int weeksTtl = (int) Math.ceil((double) daysTtl / 7.0);
        long ttl = daysTtl * DAY_MILLIS;

        long t0 = KapuaDateUtils.getEdcSysDate().getTime();
        
       
        try {
        	
        	boolean purgeMetadata = purge;
        	this.datastoreFacade.deleteMessages(accountName, topic, 
        										 startYear, startWeek,
        										 endYear, endWeek, 
        										 weeksTtl, ttl, purgeMetadata, pl);
        	
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    // -----------------------------------------------------------------------------------------
    //
    // Metrics
    //
    // -----------------------------------------------------------------------------------------
    public KapuaListResult<KapuaMetricInfo<?>> findMetricsByTopic(String accountName, String topic, KapuaQuery query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(topic, "topic");
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaMetricInfo<?>>();
        }
        
        try {
        	
        	KapuaListResult<KapuaMetricInfo<?>> result = null;
        	result = this.datastoreFacade.findMetricsByTopic(accountName,
											                  topic,
											                  query.getIndexOffset(),
											                  query.getLimit());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public <T> KapuaListResult<KapuaMetricValue> findMetricsByValue(String accountName, String topic, KapuaMetricQuery<T> query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaMetricValue>();
        }

        // TODO check the indexing mode is by value
        
        //
        // If eurotech set ttl equals to 30 days
        if (ttl < 0) {
            ttl = EUROTECH_TTL_MILLIS;
        }
        
        try {
        	//TODO review time span definition
            long endTime = System.currentTimeMillis();
            long startTime = endTime - ttl;
        	
        	KapuaListResult<KapuaMetricValue> result = null;
        	result = this.datastoreFacade.findMetricsByValue(accountName,
											                 topic,
											                 query.getName(),
											                 query.getType(),
											                 startTime,
											                 endTime,
											                 query.getMinValue(),
											                 query.getMaxValue(),
											                 query.getIndexOffset(),
											                 query.getLimit(),
											                 query.isAscendingSort());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public <T> KapuaListResult<KapuaMetricValue> findMetricsByTimestamp(String accountName, String topic, KapuaMetricQuery<T> query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(query, "query");

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaMetricValue>();
        }
        
        // TODO check the indexing mode is by timestamp (otherwise an error should be raised).
        
        try {
        	//TODO review time span definition
            long endTime = query.getEndDate();
            long startTime = query.getStartDate();
        	
        	KapuaListResult<KapuaMetricValue> result = null;
        	result = this.datastoreFacade.findMetricsByTimestamp(accountName,
												                  topic,
												                  query.getName(),
												                  query.getType(),
												                  startTime,
												                  endTime,
												                  query.getIndexOffset(),
												                  query.getLimit(),
												                  query.isAscendingSort());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> KapuaMetricInfo<T> getMetricLastValue(String accountName, String topic, String metricName, Class<T> metricType)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(topic, "topic");
        ArgumentValidator.notNull(metricName, "metricName");
        ArgumentValidator.notNull(metricType, "metricType");

        //
        // Check Access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaMetricInfo<T>();
        }

        //
        // Find
        KapuaMessageQuery query = new KapuaMessageQuery();
        query.setLimit(1); // return the last message received under this topic
        query.setFetchStyle(MessageFetchStyle.METADATA_HEADERS);
        KapuaListResult<KapuaMessage> results = findMessagesByTopic(accountName, topic, query);
        T result = null;
        for (KapuaMessage message : results) {
            KapuaPayload payload = message.getKapuaPayload();
            if (payload != null) {
                List<String> metricNames = new ArrayList<String>(payload.metricNames());
                for (String metric : metricNames) {
                    if (metricName.contains(metric)) {
                        result = (T) payload.getMetric(metric);
                    }
                }
            }
        }
        return new KapuaMetricInfo<T>(metricName, metricType, result);
    }

    // -----------------------------------------------------------------------------------------
    //
    // Topic
    //
    // -----------------------------------------------------------------------------------------
    public KapuaListResult<KapuaTopicInfo> findTopicsByAccount(String accountName, KapuaTopicQuery query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(query, "query");

        //
        // Check access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaTopicInfo>();
        }
        
        try {
        	
        	KapuaListResult<KapuaTopicInfo> result = null;
        	result = this.datastoreFacade.findTopicsByAccount(accountName,
        													   query.getPrefix(),
											                   query.getIndexOffset(),
											                   query.getLimit());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public KapuaListResult<KapuaTopicInfo> findTopicsByAsset(String accountName, String asset, KapuaTopicQuery query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(asset, "asset");
        ArgumentValidator.notNull(query, "query");

        //
        // Check access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaTopicInfo>();
        }
        
        try {
        	
        	KapuaListResult<KapuaTopicInfo> result = null;
        	result = this.datastoreFacade.findTopicsByAsset(accountName,
        													 asset,
        													 query.getPrefix(),
											                 query.getIndexOffset(),
											                 query.getLimit());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    /**
     * Deletes data from MessagesByTopic, MetricsByTopic, TopicsByAccount and MetricsByValue/Timestamp.
     */
    public void deleteTopics(String accountName)
        throws KapuaException
    {
        //
        // Argument Validator
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");

        //
        // Check access
        checkDataAccess(accountName, MessageStoreServiceAction.DELETE);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, return", accountName);
            return;
        }

        long daysTtl = accountServicePlan.getDataTimeToLive();

        // If eurotech set ttl equals to days from it creations
        if (daysTtl < 0) {
            daysTtl = EUROTECH_TTL_DAYS;
        }

        int weeksTtl = (int) Math.ceil((double) daysTtl / 7.0);
        
        
        try {

        	Date endDate = KapuaDateUtils.getEdcSysDate();
        	this.datastoreFacade.deleteTopics(accountName, weeksTtl, ttl, endDate.getTime());
        	
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    // -----------------------------------------------------------------------------------------
    //
    // Asset
    //
    // -----------------------------------------------------------------------------------------
    public KapuaListResult<KapuaAssetInfo> findAssetsByAccount(String accountName, StorableQuery query)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notNull(accountName, "accountName");
        ArgumentValidator.notNull(query, "query");

        //
        // Check access
        checkDataAccess(accountName, MessageStoreServiceAction.READ);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, returning empty result", accountName);
            return new KapuaListResult<KapuaAssetInfo>();
        }
        
        try {
        	
        	KapuaListResult<KapuaAssetInfo> result = null;
        	result = this.datastoreFacade.findAssetsByAccount(accountName,
											                   query.getIndexOffset(),
											                   query.getLimit());
        
        	return result;
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    public void deleteAssets(String accountName)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");

        //
        // Check access
        checkDataAccess(accountName, MessageStoreServiceAction.DELETE);

        //
        // Do the find
        LocalServicePlan accountServicePlan = getAccountServicePlan(accountName);
        long ttl = accountServicePlan.getDataTimeToLive() * DAY_MILLIS;

        if (!accountServicePlan.getDataStorageEnabled() || ttl == LocalServicePlan.DISABLED) {
            logger.debug("Storage not enabled for account {}, return", accountName);
            return;
        }
        
        try {

        	Date endDate = KapuaDateUtils.getEdcSysDate();
        	this.datastoreFacade.deleteAssets(accountName, ttl, endDate.getTime());
        	
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
    }

    // -----------------------------------------------------------------------------------------
    //
    // Utility methods
    //
    // -----------------------------------------------------------------------------------------
    public MetricsIndexBy getMetricsIndexBy(String accountName)
        throws KapuaException
    {
        LocalServicePlan plan = getAccountServicePlan(accountName);
        return plan.getMetricsIndexBy();
    }

    public void resetCache(String accountName, String topic)
        throws KapuaException
    {
        //
        // Argument Validation
        ArgumentValidator.notEmptyOrNull(accountName, "accountName");

        try {
        	
        	this.datastoreFacade.resetCache(accountName, topic);
        	
        } catch (Exception exc) {
        	// TODO manage exeception
            //CassandraUtils.handleException(e);
        	throw KapuaException.internalError(exc);
        }
        
    }

    // -----------------------------------------------------------------------------------------
    //
    // Private methods
    //
    // -----------------------------------------------------------------------------------------

    private void checkDataAccess(String accountName, MessageStoreServiceAction action)
        throws KapuaException
    {
        //
        // Check Access
        KapuaLocator serviceLocator = KapuaLocator.getInstance();
        AccountService accountService = serviceLocator.getService(AccountService.class);
        AuthorizationService authorizationService = serviceLocator.getService(AuthorizationService.class);
        PermissionFactory permissionFactory = serviceLocator.getFactory(PermissionFactory.class);

        Account account = accountService.findByName(accountName);

        //TODO add enum for actions
        Permission permission = permissionFactory.newPermission("data", action.key(), account.getId());
        authorizationService.checkPermission(permission);
    }

    private LocalServicePlan getAccountServicePlan(String accountName)
        throws KapuaException
    {
        KapuaLocator locator = KapuaLocator.getInstance();
        AccountService accountService = locator.getService(AccountService.class);
        Account account = accountService.findByName(accountName);
        return new LocalServicePlan(this.configurationService.getConfigValues(account.getId()));
    }

}
