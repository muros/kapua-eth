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
package org.eclipse.kapua.app.console.server;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.app.console.server.util.EdcExceptionHandler;
import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.EdcBasePagingCursor;
import org.eclipse.kapua.app.console.shared.model.EdcBasePagingLoadConfig;
import org.eclipse.kapua.app.console.shared.model.EdcBasePagingLoadResult;
import org.eclipse.kapua.app.console.shared.model.EdcPagingLoadConfig;
import org.eclipse.kapua.app.console.shared.model.EdcPagingLoadResult;
import org.eclipse.kapua.app.console.shared.model.GwtAsset;
import org.eclipse.kapua.app.console.shared.model.GwtEdcChartResult;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtMessage;
import org.eclipse.kapua.app.console.shared.model.GwtTopic;
import org.eclipse.kapua.app.console.shared.service.GwtDataService;
import org.eclipse.kapua.app.console.shared.util.KapuaGwtConverter;
import org.eclipse.kapua.service.account.AccountServiceOld;
import org.eclipse.kapua.service.authorization.AuthorizationServiceOLd;
import org.eclipse.kapua.service.authorization.Permission.Action;
import org.eclipse.kapua.service.authorization.Permission.Domain;
import org.eclipse.kapua.service.datastore.DataStoreService;
import org.eclipse.kapua.service.device.registry.DeviceRegistryServiceOld;
import org.eclipse.kapua.service.locator.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eurotech.cloud.commons.model.query.EdcAssetInfo;
import com.eurotech.cloud.commons.model.query.EdcListResult;
import com.eurotech.cloud.commons.model.query.EdcMessageFetchStyle;
import com.eurotech.cloud.commons.model.query.EdcMessageQuery;
import com.eurotech.cloud.commons.model.query.EdcMetricInfo;
import com.eurotech.cloud.commons.model.query.EdcQuery;
import com.eurotech.cloud.commons.model.query.EdcTopicInfo;
import com.eurotech.cloud.commons.model.query.EdcTopicQuery;
import com.eurotech.cloud.message.EdcMessage;
import com.eurotech.cloud.message.EdcTopic;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;

public class GwtDataServiceImpl extends KapuaRemoteServiceServlet implements GwtDataService
{
    private static final long   serialVersionUID = -2526113741011329466L;

    private static final Logger s_logger         = LoggerFactory.getLogger(GwtDataServiceImpl.class);

    // FIXME - we shouldn't limit the number of topics to display - but how do we implement paging here? If we don't do this the limit is 50
    private static final int    TOPIC_LIMIT      = 10000;
    private static final int    ASSET_LIMIT      = 10000;
    private static final int    METRIC_LIMIT     = 10000;

    private static final int    CHART_SIZE       = 250;

    public GwtTopic findTopicsTree(String accountName)
        throws GwtEdcException
    {
        GwtTopic root = new GwtTopic(accountName, null, "#", null);

        try {
            // build the appropriate topicQuery: <accountName>/+/<semantic_topic>
            EdcTopicQuery topicQuery = new EdcTopicQuery();
            topicQuery.setLimit(TOPIC_LIMIT);
            topicQuery.setPrefix(accountName + "/+/");

            ServiceLocator locator = ServiceLocator.getInstance();
            DataStoreService dss = locator.getDataStoreService();
            EdcListResult<EdcTopicInfo> accountTopicsInfo = dss.findTopicsByAccount(accountName, topicQuery);

            if (accountTopicsInfo == null) {
                return root;
            }

            // build a tree out of the returned topics.
            // it assumes the topics returned are already sorted
            while (!accountTopicsInfo.isEmpty()) {

                EdcTopicInfo accountTopicInfo = accountTopicsInfo.remove(0);
                EdcTopic topic = accountTopicInfo.getEdcTopic();

//                s_logger.info("Processing topic: {} ts: {}", topic.getSemanticTopic(), accountTopicInfo.getLastMessageTimestamp());
//                s_logger.info("topic: " + String.format("%040x", new BigInteger(1, topic.getFullTopic().getBytes())));

                if ("#".equals(topic.getLeafName()) && topic.getParentTopic() == null) {
                    // ignore the root topic for the account. Is already added!
                    root.setTimestamp(accountTopicInfo.getLastMessageTimestamp());
                    continue;
                }

                String topicName;
                if ("#".equals(topic.getLeafName())) {
                    String[] topicNameSplitted = topic.getParentTopic().split("/");
                    topicName = topicNameSplitted[topicNameSplitted.length - 1];
                }
                else {
                    topicName = topic.getLeafName();
                }

                String baseTopic = null;
                if (topic.getSemanticTopic().contains("/")) {
                    baseTopic = topic.getSemanticTopic().substring(0, topic.getSemanticTopic().lastIndexOf("/"));
                }

                GwtTopic gwtParentTopic = findParentTopic(root, topic);

                if (gwtParentTopic != null) {
                    EdcMessageQuery q = new EdcMessageQuery().setLimit(1);
                    EdcListResult<EdcMessage> topicMessage = dss.findMessagesByTopic(accountName, accountTopicInfo.getEdcTopic().getFullTopic(), q);
                    Date timestamp;
                    if (topicMessage.size() > 0) {
                        timestamp = topicMessage.get(0).getTimestamp();
                    }
                    else {
                        s_logger.warn("Could not find the last message on topic: " + topic.getFullTopic());
                        timestamp = accountTopicInfo.getLastMessageTimestamp();
                    }

                    GwtTopic gwtTopic = new GwtTopic(topicName, baseTopic, topic.getSemanticTopic(), timestamp);
                    gwtParentTopic.add(gwtTopic);
                }
                else {
                    s_logger.debug("Re-adding topic to queue");
                    accountTopicsInfo.add(accountTopicInfo);
                }
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return root;
    }

    private GwtTopic findParentTopic(GwtTopic root, EdcTopic topic)
    {
        GwtTopic gwtParentTopic = null;

        String baseTopicRoot = root.getUnescapedSemanticTopic().replace("/#", "");
        String semanticTopic = topic.getSemanticTopic();
        String parentTopic = topic.getParentTopic();

        if (root.getChildCount() == 0) {
            if (baseTopicRoot.equals(parentTopic) || // Same topic root
                parentTopic == null || // Direct child of topic #
                (semanticTopic.endsWith("#") && // Only topic that that # at the end can have child
                (semanticTopic.split("/").length - baseTopicRoot.split("/").length < 3))) {
                gwtParentTopic = root;
            }
        }
        else {
            if (baseTopicRoot.equals(parentTopic) || parentTopic == null) {
                gwtParentTopic = root;
            }
            else {

                for (ModelData m : root.getChildren()) {
                    GwtTopic gwtChildTopic = (GwtTopic) m;
                    if (gwtChildTopic.getUnescapedSemanticTopic().endsWith("/#")) {
                        String baseTopicChild = gwtChildTopic.getUnescapedSemanticTopic().replace("/#", "");

                        if (parentTopic.equals(baseTopicChild)) {
                            gwtParentTopic = gwtChildTopic;
                            break;
                        }
                        else if (parentTopic.startsWith(baseTopicChild + "/")) {

                            //
                            // Avoid digging over the 5th level
                            if (gwtChildTopic.getSemanticTopic().split("/").length > 5) {
                                gwtParentTopic = gwtChildTopic;
                            }
                            else {
                                gwtParentTopic = findParentTopic(gwtChildTopic, topic);
                            }
                            break;
                        }
                    }

                }

                if (gwtParentTopic == null &&
                    (semanticTopic.split("/").length - baseTopicRoot.split("/").length < 3)) {
                    gwtParentTopic = root;
                }
            }
        }
        return gwtParentTopic;
    }

    public List<GwtTopic> findTopicsList(String accountName)
        throws GwtEdcException
    {

        List<GwtTopic> topicList = new ArrayList<GwtTopic>();
        try {

            // build the appropriate topicQuery: <accountName>/+/<semantic_topic>
            EdcTopicQuery topicQuery = new EdcTopicQuery();
            topicQuery.setLimit(TOPIC_LIMIT);
            topicQuery.setPrefix(accountName + "/+/");

            ServiceLocator locator = ServiceLocator.getInstance();
            DataStoreService dss = locator.getDataStoreService();
            EdcListResult<EdcTopicInfo> topicResult = dss.findTopicsByAccount(accountName, topicQuery);

            if (topicResult != null) {
                for (EdcTopicInfo td : topicResult) {

                    EdcTopic topic = td.getEdcTopic();

                    topicList.add(new GwtTopic(topic.getLeafName(),
                                               topic.getFullTopic(),
                                               topic.getSemanticTopic(),
                                               null));
                }
            }

        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return topicList;
    }

    public ListLoadResult<GwtAsset> findAssets(LoadConfig config, String accountName)
        throws GwtEdcException
    {

        s_logger.debug("findAssets for account: {}", accountName);

        List<GwtAsset> gwtAssets = new ArrayList<GwtAsset>();
        try {

            // String[] getAssetsByAccount(String account);
            ServiceLocator locator = ServiceLocator.getInstance();
            AccountServiceOld acs = locator.getAccountService();
            DataStoreService dss = locator.getDataStoreService();
            DeviceRegistryServiceOld drs = locator.getDeviceRegistryService();

            long accountId = acs.getAccountId(accountName);

            EdcQuery edcQuery = new EdcQuery();
            edcQuery.setLimit(ASSET_LIMIT);

            EdcListResult<EdcAssetInfo> assets = null;
            assets = dss.findAssetsByAccount(accountName, edcQuery);
            if (assets != null) {

                for (EdcAssetInfo asset : assets) {

                    String assetId = asset.getAsset();
                    GwtAsset gwtAsset = new GwtAsset(assetId, asset.getLastMessageTimestamp());

                    String friendlyAsset = assetId;
                    String displayName = drs.getDeviceDisplayName(accountId, assetId);
                    if (displayName != null) {
                        StringBuilder sbFriendlyAsset = new StringBuilder(displayName);
                        sbFriendlyAsset.append(" (").append(friendlyAsset).append(")");
                        friendlyAsset = sbFriendlyAsset.toString();
                    }
                    gwtAsset.setFriendlyAsset(friendlyAsset);
                    gwtAssets.add(gwtAsset);
                }
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return new BaseListLoadResult<GwtAsset>(gwtAssets);
    }

    public ListLoadResult<GwtHeader> findHeaders(LoadConfig config,
                                                 String accountName,
                                                 GwtAsset asset)
        throws GwtEdcException
    {

        // build the appropriate topicQuery: <accountName>/+/<asset>/#
        StringBuilder topicQuery = new StringBuilder();
        topicQuery.append(accountName).append("/").append(asset.getUnescapedAsset()).append("/#");

        return findHeadersWithTopicQuery(config, accountName, topicQuery.toString());
    }

    public ListLoadResult<GwtHeader> findHeaders(LoadConfig config,
                                                 String accountName,
                                                 GwtTopic topic)
        throws GwtEdcException
    {

        // build the appropriate topicQuery: <accountName>/+/<semantic_topic>
        StringBuilder topicQuery = new StringBuilder();
        topicQuery.append(accountName).append("/+/").append(topic.getUnescapedSemanticTopic());

        return findHeadersWithTopicQuery(config, accountName, topicQuery.toString());
    }

    public ListLoadResult<GwtHeader> findNumberHeaders(LoadConfig config,
                                                       String accountName,
                                                       GwtTopic topic)
        throws GwtEdcException
    {
        ListLoadResult<GwtHeader> headers = findHeaders(config, accountName, topic);
        List<GwtHeader> filteredHeaders = new ArrayList<GwtHeader>();

        for (GwtHeader header : headers.getData()) {
            if (header.getType().compareTo("STRING") != 0 &&
                header.getType().compareTo("BOOLEAN") != 0 &&
                header.getType().compareTo("BYTE") != 0) {
                filteredHeaders.add(header);
            }
        }

        return new BaseListLoadResult<GwtHeader>(filteredHeaders);
    }

    private ListLoadResult<GwtHeader> findHeadersWithTopicQuery(LoadConfig config, String accountName, String topicQuery)
        throws GwtEdcException
    {

        s_logger.debug("findmetrics for account: {} topic: {}", accountName, topicQuery);

        // EdcMetricInfo[] getHeadersByTopic
        List<GwtHeader> metrics = new ArrayList<GwtHeader>();
        try {

            ServiceLocator locator = ServiceLocator.getInstance();
            DataStoreService dss = locator.getDataStoreService();
            EdcListResult<EdcMetricInfo<?>> hds = null;

            EdcQuery edcQuery = new EdcQuery();
            edcQuery.setLimit(METRIC_LIMIT);

            hds = dss.findMetricsByTopic(accountName, topicQuery, edcQuery);
            if (hds != null) {
                for (EdcMetricInfo<?> hd : hds) {
                    if (!hd.getType().equals(byte[].class)) {
                        metrics.add(KapuaGwtConverter.convert(hd));
                    }
                }
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return new BaseListLoadResult<GwtHeader>(metrics);
    }

    public EdcPagingLoadResult<GwtMessage> findMessagesByTopic(EdcPagingLoadConfig loadConfig,
                                                               String accountName,
                                                               GwtTopic topic,
                                                               List<GwtHeader> metrics,
                                                               Date startDate,
                                                               Date endDate)
        throws GwtEdcException
    {

        s_logger.debug("findMessages for account: {} topic: {}  startDate: {}  endDate: {}",
                       new Object[] { accountName, topic.getSemanticTopic(), startDate, endDate });

        EdcBasePagingLoadResult<GwtMessage> gwtResults = null;

        try {

            // Build the appropriate topicQuery: <accountName>/+/<semantic_topic>
            StringBuilder topicQuery = new StringBuilder();
            topicQuery.append(accountName).append("/+/").append(topic.getUnescapedSemanticTopic());

            EdcBasePagingLoadConfig eplc = (EdcBasePagingLoadConfig) loadConfig;

            // Check if first page button was clicked. If yes erase all stack
            if (eplc.getOffset() == 0) {
                eplc.setOffsetCursors(new Stack<EdcBasePagingCursor>());

            }

            //
            // Manage button pressing
            Object keyOffset = null;
            if (eplc.getLastOffset() == eplc.getOffset()) { // UPDATE BUTTON PRESSED OR FIRST LOAD
                if (eplc.getOffsetCursors().size() != 0) { // Is null if first page
                    eplc.getOffsetCursors().pop(); // Remove current cursor because I don't have to move
                    if (eplc.getOffsetCursors().size() != 0) { // Is null if refresh first page
                        keyOffset = eplc.getOffsetCursors().peek().getKeyOffset(); // Read this offset
                    }
                }
            }
            else if (eplc.getLastOffset() < eplc.getOffset()) { // NEXT PAGE BUTTON PRESSED
                keyOffset = eplc.getOffsetCursors().peek().getKeyOffset(); // Simply read the next offset cursor
            }
            else { // PREV PAGE BUTTON PRESSED
                if (eplc.getOffsetCursors().size() != 0) { // This shouldn't be null but check anyway
                    eplc.getOffsetCursors().pop(); // Remove next offset
                    eplc.getOffsetCursors().pop(); // Remove current cursor to step back
                    if (eplc.getOffsetCursors().size() != 0) { // Is null if is first page
                        keyOffset = eplc.getOffsetCursors().peek().getKeyOffset(); // Read this offset
                    }
                }
            }

            //
            // Do query
            EdcMessageQuery query = new EdcMessageQuery()
                                                         .setLimit(eplc.getLimit())
                                                         .setKeyOffset(keyOffset)
                                                         .setDateRange(startDate.getTime(), endDate.getTime())
                                                         .setFetchStyle(EdcMessageFetchStyle.METADATA_HEADERS);
            GwtDataFilterLoader messageLoaderFilter = new GwtDataFilterLoader(accountName, metrics);
            messageLoaderFilter.setTopic(topicQuery.toString());
            messageLoaderFilter.load(query);

            //
            // Manage offset stack
            EdcBasePagingCursor newCurrentCursor = new EdcBasePagingCursor();
            newCurrentCursor.setKeyOffset(messageLoaderFilter.getKeyOffset());
            eplc.getOffsetCursors().push(newCurrentCursor);

            gwtResults = new EdcBasePagingLoadResult<GwtMessage>(messageLoaderFilter.getLoadedGwtMessages());
            gwtResults.setOffset(eplc.getOffset());
            if (messageLoaderFilter.hasNext()) {
                gwtResults.setTotalLength(eplc.getOffset() + query.getLimit() + 1);
            }
            else {
                gwtResults.setTotalLength(eplc.getOffset() + gwtResults.getData().size());
            }
            gwtResults.setCursorOffset(eplc.getOffsetCursors());
            gwtResults.setLastOffset(eplc.getOffset());
            
            s_logger.debug("findMessages - returning {} results", new Object[] { gwtResults.getData().size() });
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtResults;
    }

    @Override
    public List<GwtMessage> findLastMessageByTopic(String accountName, int limit)
        throws GwtEdcException
    {
        s_logger.debug("findLastMessages for account: {}", new Object[] { accountName });

        List<GwtMessage> gwtMsgs = new ArrayList<GwtMessage>();
        try {

            // Build the appropriate topicQuery: <accountName>/+/<semantic_topic>
            StringBuilder topicQuery = new StringBuilder();
            topicQuery.append(accountName).append("/+/#/");

            ServiceLocator locator = ServiceLocator.getInstance();
            AccountServiceOld acs = locator.getAccountService();
            AuthorizationServiceOLd authorizationService = locator.getAuthorizationService();
            DataStoreService dss = locator.getDataStoreService();
            DeviceRegistryServiceOld drs = locator.getDeviceRegistryService();

            long accountId = acs.getAccountId(accountName);

            // Filtering messages not picking ALERT topic
            EdcListResult<EdcMessage> messages = new EdcListResult<EdcMessage>();
            EdcListResult<EdcMessage> filteredMessages = new EdcListResult<EdcMessage>();

            boolean endResults = false;

            EdcMessageQuery query = new EdcMessageQuery()
                                                         .setLimit(limit)
                                                         .setDateRange(System.currentTimeMillis() - (1000 * 3600 * 24 * 30 * 12), System.currentTimeMillis())
                                                         .setFetchStyle(EdcMessageFetchStyle.METADATA_HEADERS);// Set a limit of 1 year???

            while (filteredMessages.size() < limit && !endResults) {
                messages = dss.findMessagesByAccount(accountName, query);
                endResults = messages.isEmpty();
                if (messages.getNextKeyOffset() == null) {
                    endResults = true;
                }
                for (EdcMessage msg : messages) {
                    if (msg.getEdcTopic().getSemanticTopic().compareTo("ALERT") != 0) {
                        filteredMessages.add(msg);
                    }
                }
                query.setKeyOffset(messages.getNextKeyOffset());
            }

            // Retrieving DisplayName for each asset
            Map<String, String> displayNamesStore = new HashMap<String, String>();
            String name = "";

            boolean deviceViewPermission = true;
            try {
                authorizationService.checkAccess(accountName, Domain.device, Action.view);
            }
            catch (KapuaException edce) {
                deviceViewPermission = false;
            }

            // Converting a EdcMessage in GwtMessage
            for (EdcMessage msg : filteredMessages) {
                Map<String, Object> props = new HashMap<String, Object>();
                props.put("timestamp", msg.getTimestamp());
                props.put("topic", msg.getEdcTopic().getSemanticTopic());

                if (deviceViewPermission) {
                    name = displayNamesStore.get(msg.getEdcTopic().getAsset());
                    if (name == null || name.isEmpty()) {
                        name = drs.getDeviceDisplayName(accountId, msg.getEdcTopic().getAsset());
                        if (name == null) {
                            name = "";
                        }
                        displayNamesStore.put(msg.getEdcTopic().getAsset(), name);
                    }
                    name = name.concat(" (").concat(msg.getEdcTopic().getAsset()).concat(")");
                }
                else {
                    name = msg.getEdcTopic().getAsset();
                }

                props.put("asset", name);

                GwtMessage gwtMessage = new GwtMessage();
                gwtMessage.setProperties(props);

                gwtMsgs.add(gwtMessage);
            }
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtMsgs;
    }

    public EdcPagingLoadResult<GwtMessage> findMessagesByAsset(EdcPagingLoadConfig loadConfig,
                                                               String accountName,
                                                               GwtAsset asset,
                                                               List<GwtHeader> metrics,
                                                               Date startDate,
                                                               Date endDate)
        throws GwtEdcException
    {

        s_logger.debug("findMessages for account: {} asset: {}  startDate: {}  endDate: ",
                       new Object[] { accountName, asset.getUnescapedAsset(), startDate, endDate });

        EdcBasePagingLoadResult<GwtMessage> gwtResults = null;

        try {

            EdcBasePagingLoadConfig eplc = (EdcBasePagingLoadConfig) loadConfig;

            // Check if first page button was clicked. If yes erase all stack
            if (eplc.getOffset() == 0) {
                eplc.setOffsetCursors(new Stack<EdcBasePagingCursor>());

            }

            //
            // Manage button pressing
            Object keyOffset = null;
            if (eplc.getLastOffset() == eplc.getOffset()) { // UPDATE BUTTON PRESSED OR FIRST LOAD
                if (eplc.getOffsetCursors().size() != 0) { // Is null if first page
                    eplc.getOffsetCursors().pop(); // Remove current cursor because I don't have to move
                    if (eplc.getOffsetCursors().size() != 0) { // Is null if refresh first page
                        keyOffset = eplc.getOffsetCursors().peek().getKeyOffset(); // Read this offset
                    }
                }
            }
            else if (eplc.getLastOffset() < eplc.getOffset()) { // NEXT PAGE BUTTON PRESSED
                keyOffset = eplc.getOffsetCursors().peek().getKeyOffset(); // Simply read the next offset cursor
            }
            else { // PREV PAGE BUTTON PRESSED
                if (eplc.getOffsetCursors().size() != 0) { // This shouldn't be null but check anyway
                    eplc.getOffsetCursors().pop(); // Remove next offset
                    eplc.getOffsetCursors().pop(); // Remove current cursor to step back
                    if (eplc.getOffsetCursors().size() != 0) { // Is null if is first page
                        keyOffset = eplc.getOffsetCursors().peek().getKeyOffset(); // Read this offset
                    }
                }
            }

            //
            // Do query
            EdcMessageQuery query = new EdcMessageQuery()
                                                         .setLimit(eplc.getLimit())
                                                         .setKeyOffset(keyOffset)
                                                         .setDateRange(startDate.getTime(), endDate.getTime())
                                                         .setFetchStyle(EdcMessageFetchStyle.METADATA_HEADERS);
            GwtDataFilterLoader messageLoaderFilter = new GwtDataFilterLoader(accountName, metrics);
            messageLoaderFilter.setAsset(asset.getUnescapedAsset());
            messageLoaderFilter.load(query);

            //
            // Manage offset stack
            EdcBasePagingCursor newCurrentCursor = new EdcBasePagingCursor();
            newCurrentCursor.setKeyOffset(messageLoaderFilter.getKeyOffset());
            eplc.getOffsetCursors().push(newCurrentCursor);

            gwtResults = new EdcBasePagingLoadResult<GwtMessage>(messageLoaderFilter.getLoadedGwtMessages());
            gwtResults.setOffset(eplc.getOffset());
            if (messageLoaderFilter.hasNext()) {
                gwtResults.setTotalLength(eplc.getOffset() + query.getLimit() + 1);
            }
            else {
                gwtResults.setTotalLength(eplc.getOffset() + gwtResults.getData().size());
            }
            gwtResults.setCursorOffset(eplc.getOffsetCursors());
            gwtResults.setLastOffset(eplc.getOffset());

            s_logger.debug("findMessages - returning {} results", new Object[] { gwtResults.getData().size() });
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return gwtResults;
    }

    public GwtEdcChartResult findMessagesByTopic(String accountName,
                                                 GwtTopic topic,
                                                 List<GwtHeader> metrics,
                                                 Date startDate,
                                                 Date endDate,
                                                 Stack<EdcBasePagingCursor> cursors,
                                                 int limit,
                                                 int lastOffset,
                                                 Integer indexOffset)
        throws GwtEdcException
    {

        GwtEdcChartResult result = new GwtEdcChartResult();
        result.setOffsetCursors(cursors);
        result.setLastOffset(lastOffset);
        try {
            // build the appropriate topicQuery: <accountName>/+/<semantic_topic>
            StringBuilder topicQuery = new StringBuilder();
            topicQuery.append(accountName)
                      .append("/+/")
                      .append(topic.getUnescapedSemanticTopic());

            s_logger.info("findMessagesByTopic() -  Start Date: " + startDate + ", End Date: " + endDate);

            if (indexOffset == 0) {
                cursors = new Stack<EdcBasePagingCursor>();
                result.setOffsetCursors(cursors);
            }

            //
            // Manage button pressing
            Object keyOffset = null;
            if (lastOffset == indexOffset) { // FIRST LOAD
                if (cursors.size() != 0) { // Is null if first page
                    cursors.pop(); // Remove current cursor because I don't have to move
                    if (cursors.size() != 0) { // Is null if refresh first page
                        keyOffset = cursors.peek().getKeyOffset(); // Read this offset
                    }
                }
            }
            else if (lastOffset < indexOffset) { // NEXT PAGE BUTTON PRESSED
                keyOffset = cursors.peek().getKeyOffset(); // Simply read the next offset cursor
            }
            else { // PREV PAGE BUTTON PRESSED
                if (cursors.size() != 0) { // This shouldn't be null but check anyway
                    cursors.pop(); // Remove next offset
                    cursors.pop(); // Remove current cursor to step back
                    if (cursors.size() != 0) { // Is null if is first page
                        keyOffset = cursors.peek().getKeyOffset(); // Read this offset
                    }
                }
            }

            EdcMessageQuery query = new EdcMessageQuery()
                                                         .setLimit(limit)
                                                         .setKeyOffset(keyOffset)
                                                         .setDateRange(startDate.getTime(), endDate.getTime())
                                                         .setFetchStyle(EdcMessageFetchStyle.METADATA_HEADERS);

            GwtChartFilterLoader mapLoader = new GwtChartFilterLoader(accountName, metrics);
            mapLoader.setTopic(topicQuery.toString());
            mapLoader.load(query);

            //
            // Manage offset stack
            EdcBasePagingCursor newCurrentCursor = new EdcBasePagingCursor();
            newCurrentCursor.setKeyOffset(mapLoader.getKeyOffset());

            result.getOffsetCursors().push(newCurrentCursor);
            result.setDataPoint(mapLoader.getDataPoint());
            result.setLastOffset(indexOffset);
            result.setMoreData(mapLoader.hasNext());
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return result;
    }

    public GwtEdcChartResult findMessagesByTopic(String accountName,
                                                 GwtTopic topic,
                                                 List<GwtHeader> metrics,
                                                 Date startDate,
                                                 Date endDate)
        throws GwtEdcException
    {

        StringBuilder topicQuery = new StringBuilder();
        topicQuery.append(accountName)
                  .append("/+/")
                  .append(topic.getUnescapedSemanticTopic());

        s_logger.debug("findMessagesByTopic() -  Start Date: " + startDate + ", End Date: " + endDate);

        GwtChartFilterLoader mapLoader = new GwtChartFilterLoader(accountName, metrics);
        mapLoader.setTopic(topicQuery.toString());

        EdcMessageQuery query = new EdcMessageQuery()
                                                     .setLimit(CHART_SIZE / 5)
                                                     .setKeyOffset(null)
                                                     .setDateRange(startDate.getTime(), endDate.getTime())
                                                     .setFetchStyle(EdcMessageFetchStyle.METADATA_HEADERS);

        mapLoader.loadWithoutPaging(query);

        GwtEdcChartResult result = new GwtEdcChartResult();

        result.setDataPoint(mapLoader.getDataPoint());

        return result;
    }

    public GwtEdcChartResult findMessagesByAsset(String accountName,
                                                 GwtAsset asset,
                                                 List<GwtHeader> metrics,
                                                 Date startDate,
                                                 Date endDate,
                                                 Stack<EdcBasePagingCursor> cursors,
                                                 int limit,
                                                 int lastOffset,
                                                 Integer indexOffset)
        throws GwtEdcException
    {

        GwtEdcChartResult result = new GwtEdcChartResult();
        result.setOffsetCursors(cursors);
        result.setLastOffset(lastOffset);
        try {

            s_logger.info("findMessagesByAsset() -  Start Date: " + startDate + ", End Date: " + endDate);

            if (indexOffset == 0) {
                cursors = new Stack<EdcBasePagingCursor>();
                result.setOffsetCursors(cursors);
            }

            //
            // Manage button pressing
            Object keyOffset = null;
            if (lastOffset == indexOffset) { // FIRST LOAD
                if (cursors.size() != 0) { // Is null if first page
                    cursors.pop(); // Remove current cursor because I don't have to move
                    if (cursors.size() != 0) { // Is null if refresh first page
                        keyOffset = cursors.peek().getKeyOffset(); // Read this offset
                    }
                }
            }
            else if (lastOffset < indexOffset) { // NEXT PAGE BUTTON PRESSED
                keyOffset = cursors.peek().getKeyOffset(); // Simply read the next offset cursor
            }
            else { // PREV PAGE BUTTON PRESSED
                if (cursors.size() != 0) { // This shouldn't be null but check anyway
                    cursors.pop(); // Remove next offset
                    cursors.pop(); // Remove current cursor to step back
                    if (cursors.size() != 0) { // Is null if is first page
                        keyOffset = cursors.peek().getKeyOffset(); // Read this offset
                    }
                }
            }

            EdcMessageQuery query = new EdcMessageQuery()
                                                         .setLimit(limit)
                                                         .setKeyOffset(keyOffset)
                                                         .setDateRange(startDate.getTime(), endDate.getTime())
                                                         .setFetchStyle(EdcMessageFetchStyle.METADATA_HEADERS);

            GwtChartFilterLoader mapLoader = new GwtChartFilterLoader(accountName, metrics);
            mapLoader.setAsset(asset.getUnescapedAsset());

            mapLoader.load(query);

            //
            // Manage offset stack
            EdcBasePagingCursor newCurrentCursor = new EdcBasePagingCursor();
            newCurrentCursor.setKeyOffset(mapLoader.getKeyOffset());

            result.getOffsetCursors().push(newCurrentCursor);
            result.setDataPoint(mapLoader.getDataPoint());
            result.setLastOffset(indexOffset);
            result.setMoreData(mapLoader.hasNext());
        }
        catch (Throwable t) {
            EdcExceptionHandler.handle(t);
        }
        return result;
    }

}
