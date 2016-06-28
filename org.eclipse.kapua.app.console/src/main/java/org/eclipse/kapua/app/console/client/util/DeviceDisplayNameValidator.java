package org.eclipse.kapua.app.console.client.util;

import com.eurotech.cloud.console.client.messages.ValidationMessages;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.core.client.GWT;

public class DeviceDisplayNameValidator implements Validator {

    private static final ValidationMessages MSGS = GWT.create(ValidationMessages.class);

    private TextField<String> m_deviceDisplayNameField;
    private String regex = ".{3,}";

    public DeviceDisplayNameValidator(TextField<String> deviceDisplayNameField) {
        m_deviceDisplayNameField= deviceDisplayNameField;
        m_deviceDisplayNameField.setRegex(regex);
    }


    @Override
    public String validate(Field<?> field, String value) {
        String result = null;

        if(!value.equals(value.trim())) {
            result = MSGS.deviceFormDisplayNameValidationMessage();
        }

        if(!value.trim().matches(m_deviceDisplayNameField.getRegex())) {
            result = MSGS.deviceFormDisplayNameValidationMessage();
        }
        return result;
    }

}
