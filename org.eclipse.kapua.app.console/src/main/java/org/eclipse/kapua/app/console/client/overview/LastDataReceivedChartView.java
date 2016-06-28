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
package org.eclipse.kapua.app.console.client.overview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.kapua.app.console.client.resources.Resources;
import org.eclipse.kapua.app.console.client.util.ConsoleInfo;
import org.eclipse.kapua.app.console.client.util.EdcSafeHtmlUtils;
import org.eclipse.kapua.app.console.client.util.FailureHandler;
import org.eclipse.kapua.app.console.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.shared.model.GwtDataPoint;
import org.eclipse.kapua.app.console.shared.model.GwtEdcChartResult;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.model.GwtTopic;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;
import org.eclipse.kapua.app.console.shared.service.GwtAccountService;
import org.eclipse.kapua.app.console.shared.service.GwtDataService;
import org.eclipse.kapua.app.console.shared.service.GwtSecurityTokenService;
import org.moxieapps.gwt.highcharts.client.Axis;
import org.moxieapps.gwt.highcharts.client.AxisTitle;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.DateTimeLabelFormats;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Legend.Layout;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.plotOptions.Marker;
import org.moxieapps.gwt.highcharts.client.plotOptions.SplinePlotOptions;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.eurotech.cloud.console.shared.service.GwtAccountServiceAsync;
import com.eurotech.cloud.console.shared.service.GwtDataServiceAsync;
import com.eurotech.cloud.console.shared.service.GwtSecurityTokenServiceAsync;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class LastDataReceivedChartView extends LayoutContainer
{

    private static final ConsoleMessages       MSGS              = GWT.create(ConsoleMessages.class);

    private final GwtDataServiceAsync          gwtDataService    = GWT.create(GwtDataService.class);
    private final GwtAccountServiceAsync       gwtAccountService = GWT.create(GwtAccountService.class);

    private GwtSession                         m_currentSession;
    private GwtAccount                         m_selectedAccount;

    private ContentPanel                       chartPanel;
    private Chart                              chart;
    private String                             chartColorMetric  = "#51CC51";                                // Green

    private GwtTopic                           m_selectedTopic;
    private GwtHeader                          m_selectedMetric;

    private LabelToolItem                      labelValueTopic;
    private LabelToolItem                      labelValueMetric;

    private ToolBar                            infoToolbar;

    private Button                             changePreferenceButton;

    private final GwtSecurityTokenServiceAsync gwtXSRFService    = GWT.create(GwtSecurityTokenService.class);

    public LastDataReceivedChartView(GwtSession currentSession)
    {
        m_currentSession = currentSession;
        m_selectedAccount = currentSession.getSelectedAccount();

        if (m_selectedAccount.getDashboardPreferredTopic() != null &&
            m_selectedAccount.getDashboardPreferredMetric() != null) {
            m_selectedTopic = new GwtTopic("", "", m_selectedAccount.getDashboardPreferredTopic(), null);
            m_selectedMetric = new GwtHeader(m_selectedAccount.getDashboardPreferredMetric());
        }
    }

    protected void onRender(final Element parent, int index)
    {
        super.onRender(parent, index);

        setLayout(new FitLayout());
        chartPanel = new ContentPanel();
        chartPanel.setBorders(false);
        chartPanel.setBodyBorder(true);
        chartPanel.setHeading(MSGS.dashboardDataChartTitle());
        chartPanel.setLayout(new FitLayout());

        if (m_currentSession.hasDataReadPermission()) {
            chart = new Chart();
            chart.setType(Series.Type.SPLINE);
            chart.setZoomType(Chart.ZoomType.X);
            chart.setBorderWidth(0);
            chart.setChartTitleText(null);
            chart.setPlotBorderWidth(0);
            chart.setPlotShadow(false);
            chart.setBackgroundColor("#FFFFFF");
            chart.setCredits(new Credits().setEnabled(false));
            chart.setLegend(new Legend().setLayout(Layout.HORIZONTAL));
            chart.setToolTip(new ToolTip().setFormatter(new ToolTipFormatter() {
                public String format(ToolTipData toolTipData)
                {
                    StringBuilder sb = new StringBuilder().append("<b>")
                                                          .append(EdcSafeHtmlUtils.htmlEscape(toolTipData.getSeriesName()))
                                                          .append("</b><br/>")
                                                          .append(DateTimeFormat.getFormat("MM/dd/y HH:mm:ss.SSS").format(new Date(toolTipData.getXAsLong())))
                                                          .append(": ")
                                                          .append(toolTipData.getYAsDouble());
                    return sb.toString();
                }
            }));

            chart.setLegend(new Legend().setEnabled(false));

            chart.getYAxis().setAxisTitle(new AxisTitle().setText(""));
            chart.getXAxis()
                 .setType(Axis.Type.DATE_TIME)
                 .setDateTimeLabelFormats(new DateTimeLabelFormats()
                                                                    .setMinute("%H:%M")
                                                                    .setHour("%H:%M")
                                                                    .setDay("%e/%m %H:%M")
                                                                    .setMonth("%e. %b")
                                                                    .setYear("%b"));

            chartPanel.setTopComponent(getFilterChartToolBar());
            chartPanel.add(chart);

            refresh();
        }
        else {
            chartPanel.add(new ForbiddenPanelView());
        }

        add(chartPanel);
    }

    private ToolBar getFilterChartToolBar()
    {
        infoToolbar = new ToolBar();

        LabelToolItem labelLabelTopic = new LabelToolItem(MSGS.dashboardDataChartTitleTopic());
        infoToolbar.add(labelLabelTopic);

        String string;
        if (m_selectedTopic != null) {
            string = m_selectedTopic.getSemanticTopic();
            if (string.length() > 35) {
                string = string.substring(0, 34) + "...";
            }
        }
        else {
            string = MSGS.none();
        }

        labelValueTopic = new LabelToolItem();
        labelValueTopic.setLabel(string);
        labelValueTopic.setWidth(200);
        infoToolbar.add(labelValueTopic);

        infoToolbar.add(new SeparatorToolItem());
        LabelToolItem labelLabelMetric = new LabelToolItem(MSGS.dashboardDataChartTitleMetric());
        infoToolbar.add(labelLabelMetric);

        if (m_selectedMetric != null) {
            string = m_selectedMetric.getName();
            if (string.length() > 35) {
                string = string.substring(0, 34) + "...";
            }
        }
        else {
            string = MSGS.none();
        }
        labelValueMetric = new LabelToolItem();
        labelValueMetric.setLabel(string);
        infoToolbar.add(labelValueMetric);

        infoToolbar.add(new FillToolItem());
        changePreferenceButton = new Button(MSGS.dashboardDataChartChangePreferenceButton(),
                                            AbstractImagePrototype.create(Resources.INSTANCE.chartEdit()),
                                            new SelectionListener<ButtonEvent>() {
                                                @Override
                                                public void componentSelected(ButtonEvent ce)
                                                {
                                                    final RecentDataByTopicPreferenceDialog changePreferenceDialog = new RecentDataByTopicPreferenceDialog(m_currentSession.getSelectedAccount());
                                                    changePreferenceDialog.addListener(Events.Hide, new Listener<ComponentEvent>() {
                                                        public void handleEvent(ComponentEvent be)
                                                        {
                                                            if (changePreferenceDialog.isUpdated()) {
                                                                m_selectedTopic = changePreferenceDialog.getSelectedTopic();
                                                                m_selectedMetric = changePreferenceDialog.getMetricTopic();

                                                                String string = m_selectedTopic.getSemanticTopic();
                                                                if (string.length() > 35) {
                                                                    string = string.substring(0, 34) + "...";
                                                                }
                                                                labelValueTopic.setLabel(string);

                                                                string = m_selectedMetric.getName();
                                                                if (string.length() > 35) {
                                                                    string = string.substring(0, 34) + "...";
                                                                }
                                                                labelValueMetric.setLabel(m_selectedMetric.getName());

                                                                labelValueTopic.setToolTip(m_selectedTopic.getSemanticTopic());
                                                                labelValueMetric.setToolTip(m_selectedMetric.getName());

                                                                storeLastPreference();
                                                                updateChart();
                                                            }
                                                        }
                                                    });
                                                    changePreferenceDialog.show();
                                                }

                                            });

        infoToolbar.add(changePreferenceButton);

        return infoToolbar;
    }

    /**
     * This method store on database last selected topic and metric in overview
     */
    protected void storeLastPreference()
    {

        if (m_selectedTopic == null || m_selectedMetric == null) {
            return;
        }

        if (!m_currentSession.hasAccountUpdatePermission()) {
            ConsoleInfo.display(MSGS.error(), MSGS.dashboardCannotSavePreferences());
            return;
        }

        final GwtAccount accountToUpdate = m_currentSession.getSelectedAccount();

        accountToUpdate.setDashboardPreferredTopic(m_selectedTopic.getUnescapedSemanticTopic());
        accountToUpdate.setDashboardPreferredMetric(m_selectedMetric.getUnescapedName());

        gwtXSRFService.generateSecurityToken(new AsyncCallback<GwtXSRFToken>() {
            @Override
            public void onFailure(Throwable ex)
            {
                FailureHandler.handle(ex);
            }

            @Override
            public void onSuccess(GwtXSRFToken token)
            {
                gwtAccountService.updateAccountProperties(token, accountToUpdate, new AsyncCallback<GwtAccount>() {

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        FailureHandler.handle(caught);
                    }

                    @Override
                    public void onSuccess(GwtAccount account)
                    {
                        ConsoleInfo.display(MSGS.info(), MSGS.dashboardConfirmDashboardSavedPreferences());
                    }
                });
            }
        });

    }

    private void updateChart()
    {
        chart.removeAllSeries();
        if (m_selectedTopic != null && m_selectedMetric != null) {

            chart.showLoading(MSGS.loading());
            List<GwtHeader> metrics = new ArrayList<GwtHeader>();
            metrics.add(m_selectedMetric);

            gwtDataService.findMessagesByTopic(m_currentSession.getSelectedAccount().getName(),
                                               m_selectedTopic,
                                               metrics,
                                               new Date(System.currentTimeMillis() - 1000 * 3600 * 24 * 7),
                                               new Date(),
                                               new AsyncCallback<GwtEdcChartResult>() {

                                                   public void onFailure(Throwable caught)
                                                   {
                                                       chart.hideLoading();
                                                       FailureHandler.handle(caught);
                                                   }

                                                   public void onSuccess(GwtEdcChartResult data)
                                                   {
                                                       GwtHeader metric = m_selectedMetric;

                                                       String seriesName = metric.getName();

                                                       if (data.getDataPoint().get(seriesName) != null) {

                                                           List<GwtDataPoint> list = data.getDataPoint().get(seriesName);
                                                           int size = list.size();
                                                           //
                                                           SplinePlotOptions po = new SplinePlotOptions().setColor(chartColorMetric).setMarker(new Marker().setSymbol(Marker.Symbol.CIRCLE));

                                                           GwtDataPoint dataPoint = null;
                                                           Series serie = chart.createSeries().setName(seriesName);
                                                           for (int i = size - 1; i >= 0; i--) {
                                                               dataPoint = list.get(i);
                                                               serie.addPoint(dataPoint.getTimestamp(),
                                                                              dataPoint.getValue(),
                                                                              true,
                                                                              false,
                                                                              true)
                                                                    .setPlotOptions(po);
                                                           }

                                                           try {
                                                               chart.addSeries(serie, true, true);
                                                               chart.hideLoading();
                                                           }
                                                           catch (Exception e) {
                                                               FailureHandler.handle(e);
                                                               chart.showLoading(MSGS.noResults());
                                                           }

                                                       }
                                                       else {
                                                           chart.showLoading(MSGS.noResults());
                                                       }
                                                   }
                                               });
        }
    }

    public void refresh()
    {
        if (rendered && m_currentSession.hasDataReadPermission()) {
            updateChart();
        }
    }
}
