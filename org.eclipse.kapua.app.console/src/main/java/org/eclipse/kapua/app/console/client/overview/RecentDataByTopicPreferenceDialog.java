package org.eclipse.kapua.app.console.client.overview;

import java.util.List;

import org.eclipse.kapua.app.console.client.util.DialogUtils;
import org.eclipse.kapua.app.console.client.util.EdcLoadListener;
import org.eclipse.kapua.app.console.client.util.FailureHandler;
import org.eclipse.kapua.app.console.shared.model.GwtAccount;
import org.eclipse.kapua.app.console.shared.model.GwtHeader;
import org.eclipse.kapua.app.console.shared.model.GwtTopic;
import org.eclipse.kapua.app.console.shared.service.GwtDataService;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.eurotech.cloud.console.shared.service.GwtDataServiceAsync;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RecentDataByTopicPreferenceDialog extends Window {
    private static final ConsoleMessages              MSGS             = GWT.create(ConsoleMessages.class);
    private final GwtDataServiceAsync                 gwtDataService   = GWT.create(GwtDataService.class);

    private static final int                          LABEL_WIDTH_FORM = 50;

    private GwtAccount                                m_currentSelectedAccount;
    private FormPanel                                 m_formPanel;
    private Status                                    m_status;

    private ComboBox<GwtTopic>                        topicCombo;
    private ListStore<GwtTopic>                       topicStore;

    private ComboBox<GwtHeader>                       metricCombo;
    private ListStore<GwtHeader>                      metricStore;
    private BaseListLoader<ListLoadResult<GwtHeader>> headersLoader;

    private GwtTopic                                  selectedTopic;
    protected GwtHeader                               selectedMetric;
    private boolean                                   updated;
    private Button                                    saveButton;

    public RecentDataByTopicPreferenceDialog(GwtAccount gwtAccount) {
        m_currentSelectedAccount = gwtAccount;

        setHeading(MSGS.dashboardDataChartTitleDialogHeader());
        setModal(true);
        setLayout(new FitLayout());
        setResizable(false);
        DialogUtils.resizeDialog(this, 500, 140);
    }

    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        FormData formData = new FormData("-20");

        FormLayout layoutAccount = new FormLayout();
        layoutAccount.setLabelWidth(LABEL_WIDTH_FORM);

        FieldSet fieldSet = new FieldSet();
        fieldSet.setBorders(false);
        fieldSet.setLayout(layoutAccount);
        fieldSet.setStyleAttribute("padding", "0px");

        m_formPanel = new FormPanel();
        m_formPanel.setFrame(false);
        m_formPanel.setBodyBorder(false);
        m_formPanel.setHeaderVisible(false);
        m_formPanel.setScrollMode(Scroll.NONE);
        m_formPanel.setLayout(new FlowLayout());
        m_formPanel.add(fieldSet);

        topicStore = new ListStore<GwtTopic>();
        topicCombo = new ComboBox<GwtTopic>();
        topicCombo.setFieldLabel(MSGS.dashboardDataChartTitleTopic());
        topicCombo.setEmptyText(MSGS.dashboardDataChartTopicComboEmptyText());
        topicCombo.setWidth(150);
        topicCombo.setDisplayField("semanticTopic");
        topicCombo.setEditable(false);
        topicCombo.disable();
        topicCombo.setTriggerAction(TriggerAction.ALL);
        topicCombo.setStore(topicStore);

        fieldSet.add(topicCombo, formData);

        topicCombo.addSelectionChangedListener(new SelectionChangedListener<GwtTopic>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<GwtTopic> se) {
                selectedMetric = null;
                metricCombo.clear();
                metricCombo.disable();

                selectedTopic = topicCombo.getValue();
                topicCombo.setRawValue(selectedTopic.getUnescapedSemanticTopic());// Needed when selecting XSS metrics

                metricCombo.enable();
            }
        });

        RpcProxy<ListLoadResult<GwtHeader>> proxy = new RpcProxy<ListLoadResult<GwtHeader>>() {
            @Override
            protected void load(Object loadConfig, AsyncCallback<ListLoadResult<GwtHeader>> callback) {
                gwtDataService.findNumberHeaders((LoadConfig) loadConfig,
                                                 m_currentSelectedAccount.getName(),
                                                 selectedTopic,
                                                 callback);
            }
        };

        headersLoader = new BaseListLoader<ListLoadResult<GwtHeader>>(proxy);
        headersLoader.addLoadListener(new EdcLoadListener());

        metricStore = new ListStore<GwtHeader>(headersLoader);

        metricCombo = new ComboBox<GwtHeader>();
        metricCombo.setFieldLabel(MSGS.dashboardDataChartTitleMetric());
        metricCombo.setEmptyText(MSGS.dashboardDataChartMetricComboEmptyText());
        metricCombo.setLazyRender(false);
        metricCombo.setDisplayField("name");
        metricCombo.setWidth(250);
        metricCombo.setEditable(false);
        metricCombo.disable();
        metricCombo.setTriggerAction(TriggerAction.ALL);
        metricCombo.setStore(metricStore);

        fieldSet.add(metricCombo, formData);

        metricCombo.addSelectionChangedListener(new SelectionChangedListener<GwtHeader>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<GwtHeader> se) {
                selectedMetric = metricCombo.getValue();

                if (selectedMetric != null) {
                    metricCombo.setRawValue(selectedMetric.getName()); // Needed when selecting XSS metrics
                }
            }
        });

        gwtDataService.findTopicsList(m_currentSelectedAccount.getName(), new AsyncCallback<List<GwtTopic>>() {
            public void onFailure(Throwable caught) {
                FailureHandler.handle(caught);
                topicCombo.disable();
            }

            public void onSuccess(List<GwtTopic> topicList) {
                topicCombo.clear();
                topicStore.add(topicList);
                topicCombo.enable();

                // Pre-load last topic and metric selected
                if (m_currentSelectedAccount.getDashboardPreferredTopic() != null &&
                        m_currentSelectedAccount.getDashboardPreferredMetric() != null) {
                    // Pre-load topic
                    selectedTopic = topicStore.findModel("semanticTopic", m_currentSelectedAccount.getDashboardPreferredTopic());
                    topicCombo.setValue(selectedTopic);

                    // Set a mock metric
                    selectedMetric = new GwtHeader(m_currentSelectedAccount.getDashboardPreferredMetric());
                    metricCombo.setRawValue(m_currentSelectedAccount.getDashboardPreferredMetric());
                }
            }
        });

        m_status = new Status();
        m_status.setBusy(MSGS.waitMsg());
        m_status.hide();
        m_status.setAutoWidth(true);

        m_formPanel.setButtonAlign(HorizontalAlignment.LEFT);
        m_formPanel.getButtonBar().add(m_status);
        m_formPanel.getButtonBar().add(new FillToolItem());

        //
        // Behave of Submit Button
        //
        saveButton = new Button(MSGS.saveButton(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                if (metricCombo.getValue() == null &&
                        metricCombo.getRawValue().isEmpty()) {
                    metricCombo.markInvalid(MSGS.dashboardDataChartMetricComboValidationMessage());
                    return;
                }
                updated = true;
                hide();
            }
        });

        m_formPanel.addButton(saveButton);

        //
        // Cancel Button
        //
        m_formPanel.addButton(new Button(MSGS.cancelButton(), new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }

        }));
        m_formPanel.setButtonAlign(HorizontalAlignment.CENTER);

        add(m_formPanel);
    }

    public GwtTopic getSelectedTopic() {
        return selectedTopic;
    }

    public GwtHeader getMetricTopic() {
        return selectedMetric;
    }

    public boolean isUpdated() {
        return updated;
    }
}
