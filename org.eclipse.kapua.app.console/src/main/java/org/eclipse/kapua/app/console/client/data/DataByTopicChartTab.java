package org.eclipse.kapua.app.console.client.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.kapua.app.console.client.mqtt.MqttConsoleCallback;
import org.eclipse.kapua.app.console.client.mqtt.MqttMessageDispatcher;
import org.eclipse.kapua.app.console.client.mqtt.MqttMessageDispatcherCreator;
import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.client.util.FailureHandler;
import org.eclipse.kapua.app.console.client.widget.DateRangeSelectorChart;
import org.eclipse.kapua.app.console.client.widget.DateRangeSelectorListener;
import org.eclipse.kapua.app.console.shared.analytics.GoogleAnalytics;
import org.eclipse.kapua.app.console.shared.model.EdcBasePagingCursor;
import org.eclipse.kapua.app.console.shared.model.GwtDataPoint;
import org.eclipse.kapua.app.console.shared.model.GwtEdcChartResult;
import org.eclipse.kapua.app.console.shared.model.GwtEdcPublish;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.GwtTopic;
import org.eclipse.kapua.app.console.shared.model.GwtHeader.GwtHeaderType;
import org.eclipse.kapua.app.console.shared.service.GwtDataService;
import org.moxieapps.gwt.highcharts.client.Axis;
import org.moxieapps.gwt.highcharts.client.AxisTitle;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.ChartTitle;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.DateTimeLabelFormats;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Legend.Layout;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.SplinePlotOptions;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.eurotech.cloud.console.shared.service.GwtDataServiceAsync;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class DataByTopicChartTab extends LayoutContainer implements MqttConsoleCallback
{

    private static final ConsoleMessages MSGS            = GWT.create(ConsoleMessages.class);
    private final GwtDataServiceAsync    gwtDataService  = GWT.create(GwtDataService.class);

    private static final int             CHART_SIZE      = 250;
    private static final int             CHART_SIZE_LIVE = 50;

    private boolean                      m_initialized;
    private boolean                      m_dirty;
    private boolean                      m_loadingResults;

    private DataByTopicView              m_dataView;
    private GwtSession                   m_currentSession;

    private GwtTopic                     m_topic;
    private List<GwtHeader>              m_metrics;

    private ContentPanel                 m_chartPanel;
    private Chart                        m_chart;

    private ToolBar                      m_toolBar;
    private ToolBar                      m_pageToolbar;
    private ToggleButton                 m_toggleLive;
    private DateRangeSelectorChart       m_dateRangeSelector;

    private Button                       m_nextButton;
    private Button                       m_prevButton;

    private Map<String, Series>          m_series;
    private int                          m_subscriptionId;
    private MqttMessageDispatcher        m_mqttMessageDispatcher;

    private Stack<EdcBasePagingCursor>   m_cursors;
    private int                          m_lastOffset;
    private Integer                      m_offset;
    private int                          m_limit;
    private Date                         m_startDate;
    private Date                         m_endDate;
    private boolean                      m_morePages;

    private static final String[]        ChartColor      = { "#3f43d3", "#aa4643", "#89a54e", "#80699b", "#3d96ae", "#db843d", "#92a8cd", "#a47d7c" };

    public DataByTopicChartTab(DataByTopicView dataView, GwtSession currentSession)
    {

        m_dataView = dataView;
        m_currentSession = currentSession;
        m_initialized = false;
        m_dirty = true;
        m_loadingResults = false;
        m_subscriptionId = -1;
        m_mqttMessageDispatcher = null;
        m_offset = 0;
        m_limit = CHART_SIZE;
        m_morePages = false;
    }

    public GwtTopic getTopic()
    {
        return m_topic;
    }

    public void setTopic(GwtTopic topic)
    {
        m_topic = topic;
    }

    public List<GwtHeader> getHeaders()
    {
        return m_metrics;
    }

    public void setHeaders(List<GwtHeader> metrics)
    {
        m_metrics = new ArrayList<GwtHeader>();
        for (GwtHeader metric : metrics) {
            GwtHeaderType type = GwtHeaderType.valueOf(metric.getType());
            switch (type) {
                case FLOAT:
                case INTEGER:
                case DOUBLE:
                case LONG:
                    m_metrics.add(metric);
                    break;

                default:
                    continue;
            }
        }
    }

    public boolean isDirty()
    {
        return m_dirty;
    }

    public void setDirty(boolean dirty)
    {
        m_dirty = dirty;
    }

    protected void onRender(Element parent, int index)
    {

        super.onRender(parent, index);
        setLayout(new FitLayout());

        initToolbar();

        m_chartPanel = new ContentPanel();
        m_chartPanel.setBorders(false);
        m_chartPanel.setBodyBorder(false);
        m_chartPanel.setHeaderVisible(false);
        m_chartPanel.setScrollMode(Scroll.AUTO);
        m_chartPanel.setLayout(new FitLayout());

        setHighchartTimezone();
        m_chart = new Chart()
                             .setType(Series.Type.SPLINE)
                             .setZoomType(Chart.ZoomType.X)
                             .setBorderWidth(0)
                             .setChartTitleText(MSGS.dataQueryForData())
                             .setCredits(new Credits().setEnabled(false))
                             .setLegend(new Legend().setLayout(Layout.HORIZONTAL))
                             .setToolTip(new ToolTip()
                                                      .setFormatter(new ToolTipFormatter() {
                                                          public String format(ToolTipData toolTipData)
                                                          {
                                                              return "<b>" + toolTipData.getSeriesName() + "</b><br/>" +
                                                                     DateTimeFormat.getFormat("MM/dd/y HH:mm:ss.SSS").format(
                                                                                                                             new Date(toolTipData.getXAsLong()))
                                                                     + ": " + toolTipData.getYAsDouble();
                                                          }
                                                      }));
        m_chart.getYAxis().setAxisTitle(new AxisTitle().setText(""));
        m_chart.getXAxis()
               .setType(Axis.Type.DATE_TIME)
               .setDateTimeLabelFormats(new DateTimeLabelFormats()
                                                                  .setMinute("%H:%M")
                                                                  .setHour("%H:%M")
                                                                  .setDay("%e/%m %H:%M")
                                                                  .setMonth("%e. %b")
                                                                  .setYear("%b") // don't display the dummy year
        );

        m_nextButton = new Button();
        m_nextButton.setText(">");
        m_nextButton.disable();
        m_nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce)
            {
                if (m_offset > 0) {
                    m_offset -= m_limit;
                    if (m_offset < 0) {
                        m_offset = 0;
                        m_nextButton.disable();
                    }
                    m_dirty = true;
                    refresh(true);
                }
                else {
                    m_nextButton.disable();
                }
            }
        });

        m_prevButton = new Button();
        m_prevButton.setText("<");
        m_prevButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce)
            {
                m_offset += m_limit;
                m_nextButton.enable();
                m_dirty = true;
                refresh(true);
            }
        });

        m_pageToolbar = new ToolBar();
        m_pageToolbar.add(m_prevButton);
        m_pageToolbar.add(new FillToolItem());
        m_pageToolbar.add(m_nextButton);

        m_chartPanel.setTopComponent(m_toolBar);
        m_chartPanel.add(m_chart);
        m_chartPanel.setBottomComponent(m_pageToolbar);

        add(m_chartPanel);
        m_initialized = true;
    }

    public static native void setHighchartTimezone() /*-{
                                                     $wnd.Highcharts.setOptions({
                                                     global : {
                                                     useUTC : false
                                                     }
                                                     });
                                                     }-*/;

    public void initToolbar()
    {

        m_toolBar = new ToolBar();
        m_dateRangeSelector = new DateRangeSelectorChart();
        m_dateRangeSelector.setListener(new DateRangeSelectorListener() {
            public void onUpdate()
            {

                m_offset = 0;
                m_morePages = false;
                m_dirty = true;
                refreshQuery();
            }
        });
        m_dateRangeSelector.disable();

        m_toggleLive = new ToggleButton(MSGS.live(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce)
            {
                if (m_toggleLive.isPressed()) {
                    m_dirty = true;
                    m_limit = CHART_SIZE_LIVE;
                    m_prevButton.disable();
                    m_nextButton.disable();
                    refresh(true);
                }
                else {
                    m_dirty = true;
                    m_limit = CHART_SIZE;
                    m_prevButton.enable();
                    m_nextButton.enable();
                    unsubscribe();
                    refresh(true);
                }
            }
        });
        m_toggleLive.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.live()));
        m_toggleLive.toggle(false);
        m_toggleLive.disable();

        m_toolBar.add(m_toggleLive);
        m_toolBar.add(new FillToolItem());
        m_toolBar.add(new LabelToolItem(MSGS.dataEndRange()));
        m_toolBar.add(m_dateRangeSelector);
    }

    public void refreshQuery()
    {
        m_endDate = m_dateRangeSelector.getEndDate();
        if (m_endDate.before(m_currentSession.getSelectedAccount().getCreatedOn())) {
            m_endDate = m_currentSession.getSelectedAccount().getCreatedOn();
        }
        m_startDate = m_currentSession.getSelectedAccount().getCreatedOn();
        m_dirty = true;
        refresh(true);
    }

    public void refresh(boolean removeAllSeries)
    {
        if (m_initialized && !m_dirty) {
            return;
        }
        if (m_chart != null && m_topic != null) {

            if (m_toggleLive.isPressed()) {
                GoogleAnalytics.trackPageview(GoogleAnalytics.GA_DATA_BYTOPIC_CHART_LIVE_ON);
            }
            else {
                GoogleAnalytics.trackPageview(GoogleAnalytics.GA_DATA_BYTOPIC_CHART_LIVE_OFF);
            }

            if (removeAllSeries) {
                m_chart.setTitle(new ChartTitle().setText(m_topic.getSemanticTopic()), null);
                m_chart.showLoading(MSGS.loading());
                m_chart.removeAllSeries(false);
                m_series = new HashMap<String, Series>();
            }

            m_loadingResults = true;
            gwtDataService.findMessagesByTopic(m_currentSession.getSelectedAccount().getName(),
                                               m_topic,
                                               m_metrics,
                                               m_startDate,
                                               m_endDate,
                                               m_cursors,
                                               m_limit,
                                               m_lastOffset,
                                               m_offset,
                                               new AsyncCallback<GwtEdcChartResult>() {

                                                   public void onFailure(Throwable caught)
                                                   {
                                                       m_chart.redraw();
                                                       m_dirty = false;
                                                       m_chart.hideLoading();
                                                       m_dataView.queryDone();
                                                       FailureHandler.handle(caught);
                                                   }

                                                   public void onSuccess(GwtEdcChartResult data)
                                                   {
                                                       boolean newSeries = false;
                                                       int index = 0;

                                                       m_cursors = data.getOffsetCursors();
                                                       m_lastOffset = data.getLastOffset();

                                                       if (data.hasMoreData() && !m_toggleLive.isPressed()) {
                                                           m_prevButton.enable();
                                                       }
                                                       else {
                                                           m_prevButton.disable();
                                                       }

                                                       for (GwtHeader metric : m_metrics) {

                                                           String seriesName = metric.getName();
                                                           Series series = m_series.get(seriesName);

                                                           if (series == null) {
                                                               newSeries = true;
                                                               series = m_chart.createSeries();
                                                               series.setName(seriesName);
                                                               series.setType(Series.Type.SPLINE);
                                                               m_series.put(seriesName, series);
                                                           }

                                                           if (data.getDataPoint().get(seriesName) != null) {

                                                               List<GwtDataPoint> list = data.getDataPoint().get(seriesName);
                                                               int size = list.size();
                                                               if (size >= m_limit) {
                                                                   m_morePages = true;
                                                               }

                                                               SplinePlotOptions po = new SplinePlotOptions().setColor(ChartColor[index])
                                                                                                             .setMarker(new Marker().setSymbol(Marker.Symbol.CIRCLE));
                                                               index++;
                                                               if (index == ChartColor.length) {
                                                                   index = 0;
                                                               }

                                                               GwtDataPoint dataPoint = null;
                                                               for (int i = size - 1; i >= 0; i--) {
                                                                   dataPoint = list.get(i);
                                                                   series.addPoint(dataPoint.getTimestamp(), dataPoint.getValue(), false, false, false)
                                                                         .setPlotOptions(po);
                                                               }
                                                               if (newSeries) {
                                                                   m_chart.addSeries(series, false, false);
                                                               }
                                                           }
                                                       }
                                                       m_chart.redraw();

                                                       m_dirty = false;
                                                       m_chart.hideLoading();
                                                       m_toggleLive.enable();
                                                       m_dateRangeSelector.enable();
                                                       m_dataView.queryDone();

                                                       // register for live update of the graph
                                                       if (m_toggleLive.isPressed()) {
                                                           subscribe();
                                                       }
                                                   }
                                               });
        }
    }

    // --------------------------------------------------------------------------------------
    //
    // Subscription Management APIs
    //
    // --------------------------------------------------------------------------------------

    public void subscribe()
    {

        // clean-up
        unsubscribe();

        // new subscription
        StringBuilder topicQuery = new StringBuilder();
        topicQuery.append(m_currentSession.getSelectedAccount().getName())
                  .append("/+/")
                  .append(m_topic.getUnescapedSemanticTopic());

        if (m_mqttMessageDispatcher == null) {
            m_mqttMessageDispatcher = new MqttMessageDispatcherCreator(m_currentSession).getMqttMessageDispatcher();
        }

        // By default, use the broker assigned for the account
        String brokerUrl = m_currentSession.getSelectedAccount().getBrokerURL();

        m_subscriptionId = m_mqttMessageDispatcher.registerCallback(brokerUrl, this, new String[] { topicQuery.toString() });
    }

    public void unsubscribe()
    {
        if (m_mqttMessageDispatcher != null) {
            m_mqttMessageDispatcher.unregisterCallback(m_subscriptionId);
            m_mqttMessageDispatcher.shutdown();
            m_mqttMessageDispatcher = null;
        }
    }

    public void cleanSubscription()
    {
        if (m_toggleLive != null) {
            m_toggleLive.toggle(false);
        }
        unsubscribe();
    }

    // --------------------------------------------------------------------------------------
    //
    // MqttCallbackHandler APIs
    //
    // --------------------------------------------------------------------------------------

    public void connectionLost()
    {
        // TODO Auto-generated method stub

    }

    public void publishArrived(GwtEdcPublish publish)
    {
        Map<String, Number> values = publish.getNumberValues();
        boolean added = false;
        List<Point> tempPoints = new ArrayList<Point>();

        for (String seriesName : m_series.keySet()) {

            Series series = m_series.get(seriesName);

            if (values.keySet().contains(seriesName)) {
                Number value = values.get(seriesName);
                if (value != null) {
                    added = true;
                    if (series.getPoints().length == 0) {
                        series.addPoint(publish.getTimestamp().getTime(), value, true, false, true);
                    }
                    else if (series.getPoints().length < CHART_SIZE_LIVE &&
                             series.getPoints()[0].getX().longValue() >= m_chart.getXAxis().getExtremes().getDataMin().longValue()) {
                        series.addPoint(publish.getTimestamp().getTime(), value, true, false, true);
                    }
                    else {
                        series.addPoint(publish.getTimestamp().getTime(), value, true, false, true);
                        tempPoints.add(series.getPoints()[0]);
                    }
                }
            }
            else if (series.getPoints().length > 0) {
                if (series.getPoints().length < CHART_SIZE_LIVE) {
                    if (series.getPoints()[0].getX().longValue() <= m_chart.getXAxis().getExtremes().getDataMin().longValue()) {
                        tempPoints.add(series.getPoints()[0]);
                    }
                }
                else {
                    tempPoints.add(series.getPoints()[0]);
                }
            }
        }

        if (added) {
            for (Point p : tempPoints) {
                p.remove();
            }
        }
    }

    public void published(int messageId)
    {
        // TODO Auto-generated method stub

    }

    public void subscribed(int messageId)
    {
        // TODO Auto-generated method stub

    }

    public void unsubscribed(int messageId)
    {
        // TODO Auto-generated method stub

    }

    // --------------------------------------------------------------------------------------
    //
    // Unload of the GXT Component
    //
    // --------------------------------------------------------------------------------------

    public void onUnload()
    {
        unsubscribe();
        super.onUnload();
    }
}
