package org.eclipse.kapua.app.console.shared.service;

import java.util.Date;
import java.util.List;
import java.util.Stack;

import org.eclipse.kapua.app.console.shared.GwtEdcException;
import org.eclipse.kapua.app.console.shared.model.EdcBasePagingCursor;
import org.eclipse.kapua.app.console.shared.model.EdcPagingLoadConfig;
import org.eclipse.kapua.app.console.shared.model.EdcPagingLoadResult;
import org.eclipse.kapua.app.console.shared.model.GwtAsset;
import org.eclipse.kapua.app.console.shared.model.GwtEdcChartResult;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtMessage;
import org.eclipse.kapua.app.console.shared.model.GwtTopic;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("data")
public interface GwtDataService extends RemoteService {

    /**
     * Return the Topics for a given account; the returned structure is ready to
     * be fed into the TreeGrid UI widget, so it contains all the topic children.
     *
     * @param config
     * @param accountId
     * @return
     * @throws GwtEdcException
     */
    public GwtTopic findTopicsTree(String accountName) throws GwtEdcException;

    /**
     * Return the Topics for a given account; the returned structure is a list
     * with all tree limb expanded.
     *
     * @param config
     * @param accountId
     * @return
     * @throws GwtEdcException
     */
    public List<GwtTopic> findTopicsList(String accountName) throws GwtEdcException;


    /**
     *
     * @param config
     * @param accountName
     * @return
     * @throws GwtEdcException
     */
    public ListLoadResult<GwtAsset> findAssets(LoadConfig config, String accountName) throws GwtEdcException;


    /**
     * Return the Headers for a given account/topic pair.
     * The returned structure is ready to be fed into the Grid UI widget through a loader.
     *
     * @param config
     * @param accountId
     * @param topic
     * @return
     * @throws GwtEdcException
     */
    public ListLoadResult<GwtHeader> findHeaders(LoadConfig config, String accountName, GwtTopic topic) throws GwtEdcException;

    /**
    * Return the number Headers (int, long, float, double and so on. Not String, Boolean, Byte) for a given account/topic pair.
    * The returned structure is ready to be fed into the Grid UI widget through a loader.
    *
    * @param config
    * @param accountId
    * @param topic
    * @return
    * @throws GwtEdcException
    */
    public ListLoadResult<GwtHeader> findNumberHeaders(LoadConfig config, String accountName, GwtTopic topic) throws GwtEdcException;


    /**
     * Return the Headers for a given account/topic pair.
     * The returned structure is ready to be fed into the Grid UI widget through a loader.
     *
     * @param config
     * @param accountId
     * @param topic
     * @return
     * @throws GwtEdcException
     */
    public ListLoadResult<GwtHeader> findHeaders(LoadConfig config, String accountName, GwtAsset asset) throws GwtEdcException;


    /**
     * Return messages based on the specified parameters.
     *
     * @param loadConfig
     * @param accountId
     * @param topic
     * @param headers
     * @return
     * @throws GwtEdcException
     */
    public EdcPagingLoadResult<GwtMessage> findMessagesByTopic(EdcPagingLoadConfig loadConfig, String accountName,
            GwtTopic topic, List<GwtHeader> headers, Date startDate, Date endDate) throws GwtEdcException;

    public List<GwtMessage> findLastMessageByTopic(String accountName, int limit) throws GwtEdcException;

    public GwtEdcChartResult findMessagesByTopic(String accountName, GwtTopic topic, List<GwtHeader> metrics, Date startDate, Date endDate) throws GwtEdcException;

    public GwtEdcChartResult findMessagesByTopic(String accountName, GwtTopic topic,
            List<GwtHeader> headers, Date startDate, Date endDate, Stack<EdcBasePagingCursor> cursors, int limit, int lastOffset, Integer indexOffset) throws GwtEdcException;

    public EdcPagingLoadResult<GwtMessage> findMessagesByAsset(EdcPagingLoadConfig loadConfig, String accountName,
            GwtAsset asset, List<GwtHeader> headers, Date startDate, Date endDate) throws GwtEdcException;

    public GwtEdcChartResult findMessagesByAsset(String accountName, GwtAsset asset,
            List<GwtHeader> headers, Date startDate, Date endDate, Stack<EdcBasePagingCursor> cursors, int limit, int lastOffset, Integer indexOffset) throws GwtEdcException;

}
