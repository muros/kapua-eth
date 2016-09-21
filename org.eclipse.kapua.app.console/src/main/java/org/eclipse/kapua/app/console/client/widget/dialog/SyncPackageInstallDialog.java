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
package org.eclipse.kapua.app.console.client.widget.dialog;

import org.eclipse.kapua.app.console.client.util.DialogUtils;
import org.eclipse.kapua.app.console.shared.model.GwtXSRFToken;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class SyncPackageInstallDialog extends PackageInstallDialog
{
    private String              m_actionUrl;
    private String              m_account;
    private String              m_clientId;

    //
    // File fields
    private HiddenField<String> xsrfTokenFieldFile;
    private TabItem             m_tabFile;
    private FormPanel           m_formPanelFile;
    private FileUploadField     m_fileUploadField;

    //
    // URL fields
    private HiddenField<String> xsrfTokenFieldUrl;
    private TabItem             m_tabUrl;
    private FormPanel           m_formPanelUrl;
    private TextField<String>   m_textFieldUrl;

    public SyncPackageInstallDialog(String actionUrl,
                                    String account,
                                    String clientId)
    {
        super();

        m_actionUrl = actionUrl;
        m_account = account;
        m_clientId = clientId;

        DialogUtils.resizeDialog(this, 500, 185);
    }

    @Override
    public String getInfoMessage()
    {
        return MSGS.packageInstallSyncInfo();
    }

    @Override
    public void createTabItems()
    {
        //
        // File upload tab
        FormData formData = new FormData("-10");

        m_formPanelFile = new FormPanel();
        m_formPanelFile.setFrame(false);
        m_formPanelFile.setHeaderVisible(false);
        m_formPanelFile.setBodyBorder(false);
        m_formPanelFile.setBorders(false);
        m_formPanelFile.setAction(m_actionUrl + "/upload");
        m_formPanelFile.setEncoding(Encoding.MULTIPART);
        m_formPanelFile.setMethod(Method.POST);
        m_formPanelFile.setButtonAlign(HorizontalAlignment.CENTER);

        m_fileUploadField = new FileUploadField();
        m_fileUploadField.setAllowBlank(false);
        m_fileUploadField.setName("uploadedFile");
        m_fileUploadField.setFieldLabel(MSGS.fileLabel());

        m_formPanelFile.add(m_fileUploadField, formData);

        HiddenField<String> accountField = new HiddenField<String>();
        accountField.setId("account");
        accountField.setName("account");
        accountField.setValue(m_account);

        HiddenField<String> clientIdField = new HiddenField<String>();
        clientIdField.setId("clientId");
        clientIdField.setName("clientId");
        clientIdField.setValue(m_clientId);

        xsrfTokenFieldFile = new HiddenField<String>();
        xsrfTokenFieldFile.setId("xsrfToken");
        xsrfTokenFieldFile.setName("xsrfToken");
        xsrfTokenFieldFile.setValue("");

        m_formPanelFile.add(accountField);
        m_formPanelFile.add(clientIdField);
        m_formPanelFile.add(xsrfTokenFieldFile);

        m_tabFile = new TabItem(MSGS.fileLabel());
        m_tabFile.setBorders(false);
        m_tabFile.setLayout(new FormLayout());
        m_tabFile.setStyleAttribute("background-color", "#E8E8E8");
        m_tabFile.add(m_formPanelFile);

        //
        // Download URL tab
        m_formPanelUrl = new FormPanel();
        m_formPanelUrl.setFrame(false);
        m_formPanelUrl.setHeaderVisible(false);
        m_formPanelUrl.setBodyBorder(false);
        m_formPanelUrl.setBorders(false);
        m_formPanelUrl.setEncoding(Encoding.MULTIPART);
        m_formPanelUrl.setMethod(Method.POST);
        m_formPanelUrl.setAction(m_actionUrl + "/url");

        m_textFieldUrl = new TextField<String>();
        m_textFieldUrl.setAllowBlank(false);
        m_textFieldUrl.setName("packageUrl");
        m_textFieldUrl.setFieldLabel(MSGS.urlLabel());

        accountField = new HiddenField<String>();
        accountField.setId("account");
        accountField.setName("account");
        accountField.setValue(m_account);

        clientIdField = new HiddenField<String>();
        clientIdField.setId("clientId");
        clientIdField.setName("clientId");
        clientIdField.setValue(m_clientId);

        xsrfTokenFieldUrl = new HiddenField<String>();
        xsrfTokenFieldUrl.setId("xsrfToken");
        xsrfTokenFieldUrl.setName("xsrfToken");
        xsrfTokenFieldUrl.setValue("");

        m_formPanelUrl.add(accountField);
        m_formPanelUrl.add(clientIdField);
        m_formPanelUrl.add(xsrfTokenFieldUrl);

        m_formPanelUrl.add(m_textFieldUrl, formData);

        m_tabUrl = new TabItem(MSGS.urlLabel());
        m_tabUrl.setBorders(false);
        m_tabUrl.setLayout(new FormLayout());
        m_tabUrl.setStyleAttribute("background-color", "#E8E8E8");
        m_tabUrl.add(m_formPanelUrl);

        //
        // Submit listener
        Listener<FormEvent> submitListener = new Listener<FormEvent>() {

            public void handleEvent(FormEvent be)
            {
                String htmlResponse = be.getResultHtml();

                if (htmlResponse == null || htmlResponse.isEmpty()) {
                    m_exitStatus = true;
                    m_exitMessage = MSGS.packageInstallSyncSuccess();
                }
                else {
                    String errMsg = htmlResponse;
                    int startIdx = htmlResponse.indexOf("<pre>");
                    int endIndex = htmlResponse.indexOf("</pre>");
                    if (startIdx != -1 && endIndex != -1) {
                        errMsg = htmlResponse.substring(startIdx + 5, endIndex);
                    }

                    m_exitStatus = false;
                    if (errMsg.contains("Timeout while waiting for the response")) {
                        m_exitMessage = MSGS.packageInstallSyncTimeout();
                    }
                    else {
                        m_exitMessage = MSGS.packageInstallSyncFailure(errMsg);
                    }
                }

                hide();
            }
        };

        m_formPanelFile.addListener(Events.Submit, submitListener);
        m_formPanelUrl.addListener(Events.Submit, submitListener);

        //
        // Add tabs to the tabs panel
        m_tabsPanel.add(m_tabUrl);
        m_tabsPanel.add(m_tabFile);
    }

    @Override
    public void submit()
    {
        FormPanel formPanel;
        TabItem selectedItem = m_tabsPanel.getSelectedItem();

        if (selectedItem == m_tabFile) {
            formPanel = m_formPanelFile;
        }
        else {
            formPanel = m_formPanelUrl;
        }

        if (!formPanel.isValid()) {
            return;
        }

        formPanel.submit();
    }

    @Override
    public void setXsrfToken(GwtXSRFToken token)
    {
        if (xsrfTokenFieldFile != null) {
            xsrfTokenFieldFile.setValue(token.getToken());
        }

        if (xsrfTokenFieldUrl != null) {
            xsrfTokenFieldUrl.setValue(token.getToken());
        }
    }

	@Override
	protected void addListeners() {
	}
	
}