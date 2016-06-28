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

import java.util.Map;

import org.eclipse.kapua.app.console.client.util.FailureHandler;
import org.eclipse.kapua.app.console.shared.model.GwtSession;
import org.eclipse.kapua.app.console.shared.service.GwtDeviceService;
import org.moxieapps.gwt.highcharts.client.Chart;
import org.moxieapps.gwt.highcharts.client.Credits;
import org.moxieapps.gwt.highcharts.client.Legend;
import org.moxieapps.gwt.highcharts.client.Point;
import org.moxieapps.gwt.highcharts.client.Series;
import org.moxieapps.gwt.highcharts.client.ToolTip;
import org.moxieapps.gwt.highcharts.client.ToolTipData;
import org.moxieapps.gwt.highcharts.client.ToolTipFormatter;
import org.moxieapps.gwt.highcharts.client.labels.PieDataLabels;
import org.moxieapps.gwt.highcharts.client.plotOptions.PiePlotOptions;
import org.moxieapps.gwt.highcharts.client.plotOptions.PlotOptions;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.eurotech.cloud.console.shared.service.GwtDeviceServiceAsync;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BottomOverviewView extends LayoutContainer
{

    private final GwtDeviceServiceAsync  gwtDeviceService             = GWT.create(GwtDeviceService.class);

    private static final ConsoleMessages MSGS                         = GWT.create(ConsoleMessages.class);
    private GwtSession                   m_currentSession;

    private ContentPanel                 deviceChartPanel;

    private Chart                        m_deviceStatusChart;

    private String                       chartColorMissingDevice      = "#CC5151";
    private String                       chartColorDisconnectedDevice = "#F9AC63";                         // Orange
    private String                       chartColorConnectedDevice    = "#51CC51";                         // Green
    private String                       chartColorNoDeviceFound      = "#8E8E8E";                         // Gray

    public BottomOverviewView(GwtSession currentSession)
    {
        m_currentSession = currentSession;
    }

    protected void onRender(final Element parent, int index)
    {
        super.onRender(parent, index);

        setLayout(new FitLayout());

        ContentPanel mainPanel = new ContentPanel();
        mainPanel.setBorders(false);
        mainPanel.setBodyBorder(false);
        mainPanel.setHeaderVisible(false);
        mainPanel.setLayout(new FitLayout());

        add(mainPanel);

        LayoutContainer layoutContainer = new LayoutContainer();
        layoutContainer.setLayout(new BorderLayout());

        mainPanel.add(layoutContainer);

        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0, 0, 0, 0));
        centerData.setSplit(false);

        ContentPanel centerPanel = new ContentPanel();
        centerPanel.setBodyBorder(false);
        centerPanel.setBorders(false);
        centerPanel.setHeaderVisible(false);
        centerPanel.setLayout(new FitLayout());
        centerPanel.add(getPieChartPanel());

        layoutContainer.add(centerPanel, centerData);

        refreshDeviceStatus();
    }

    private ContentPanel getPieChartPanel()
    {
        deviceChartPanel = new ContentPanel();
        deviceChartPanel.setBorders(false);
        deviceChartPanel.setBodyBorder(true);
        deviceChartPanel.setLayout(new FitLayout());
        deviceChartPanel.setHeading(MSGS.dashboardConnectivityDeviceChartTitle());

        if (m_currentSession.hasDeviceReadPermission()) {
            deviceChartPanel.add(getPieChart());
        }
        else {
            deviceChartPanel.add(new ForbiddenPanelView());
        }

        return deviceChartPanel;
    }

    private Chart getPieChart()
    {
        //
        // Chart drawing
        m_deviceStatusChart = new Chart()
                                         .setType(Series.Type.PIE)
                                         .setChartTitle(null).setPlotBackgroundColor((String) null)
                                         .setBorderWidth(0)
                                         .setPlotBorderWidth(0)
                                         .setCredits(new Credits().setEnabled(false))
                                         .setPlotShadow(true);

        PiePlotOptions plotOptions = new PiePlotOptions();
        plotOptions.setAllowPointSelect(true);
        plotOptions.setCursor(PlotOptions.Cursor.POINTER);
        plotOptions.setPieDataLabels(new PieDataLabels().setEnabled(false));
        plotOptions.setShowInLegend(true);

        m_deviceStatusChart.setBackgroundColor("#FFFFFF");
        m_deviceStatusChart.setPiePlotOptions(plotOptions);

        m_deviceStatusChart.setLegend(new Legend().setLayout(Legend.Layout.VERTICAL).setAlign(Legend.Align.RIGHT).setVerticalAlign(Legend.VerticalAlign.MIDDLE));

        m_deviceStatusChart.setToolTip(new ToolTip()
                                                    .setFormatter(new ToolTipFormatter() {
                                                        public String format(ToolTipData toolTipData)
                                                        {
                                                            if (toolTipData.getPointName().compareTo(MSGS.dashboradConnectivityDeviceChartNoDeviceFound()) == 0) {
                                                                return "<b>" + toolTipData.getPointName() + "</b>";
                                                            }
                                                            else {
                                                                return "<b>" + toolTipData.getPointName() + "</b>: " + toolTipData.getYAsLong();
                                                            }
                                                        }
                                                    }));

        deviceChartPanel.add(m_deviceStatusChart);

        return m_deviceStatusChart;
    }

    public void refreshAll()
    {
        refreshDeviceStatus();
    }

    public void refreshDeviceStatus()
    {
        if (rendered && m_currentSession.hasDeviceReadPermission()) {
            deviceChartPanel.mask(MSGS.loading());

            //
            // Chart data retrieving
            gwtDeviceService.getNumOfDevicesConnected(m_currentSession.getSelectedAccount().getId(), new AsyncCallback<Map<String, Integer>>() {

                @Override
                public void onFailure(Throwable caught)
                {
                    FailureHandler.handle(caught);
                }

                @Override
                public void onSuccess(Map<String, Integer> devicesStatus)
                {
                    m_deviceStatusChart.removeAllSeries();
                    if (devicesStatus.size() > 0) {
                        m_deviceStatusChart.addSeries(m_deviceStatusChart.createSeries().setName("Devices").setPoints(
                                                                                                                      new Point[] {
                                                                                                                                    new Point(MSGS.dashboradConnectivityDeviceChartConnectedDevice(),
                                                                                                                                              devicesStatus.get("connected")).setColor(chartColorConnectedDevice),
                                                                                                                                    new Point(MSGS.dashboradConnectivityDeviceChartDisconnectedDevice(),
                                                                                                                                              devicesStatus.get("disconnected")).setColor(chartColorDisconnectedDevice),
                                                                                                                                    new Point(MSGS.dashboradConnectivityDeviceChartMissingDevice(),
                                                                                                                                              devicesStatus.get("missing")).setColor(chartColorMissingDevice)
                                                                                                                      }));
                    }
                    else {
                        m_deviceStatusChart.addSeries(m_deviceStatusChart.createSeries().setName("NoDevice").setPoints(
                                                                                                                       new Point[] {
                                                                                                                                     new Point(MSGS.dashboradConnectivityDeviceChartNoDeviceFound(),
                                                                                                                                               1).setColor(chartColorNoDeviceFound),
                                                                                                                       }));
                    }
                    m_deviceStatusChart.redraw();
                    deviceChartPanel.unmask();
                }
            });
        }
    }
}
