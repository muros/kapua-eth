package org.eclipse.kapua.app.console.client.util;

import com.eurotech.cloud.console.client.messages.ValidationMessages;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;

public class ConfirmPasswordFieldValidator extends PasswordFieldValidator {

    private static final ValidationMessages MSGS = GWT.create(ValidationMessages.class);

    private TextField<String> m_passwordField;

    public ConfirmPasswordFieldValidator(TextField<String> confirmPasswordField, TextField<String> passwordField) {
        super (confirmPasswordField);

        m_passwordField = passwordField;
    }

    public String validate(Field<?> field, String value) {

        String result = super.validate(field, value);
        if (result == null) {
            if (!value.equals(m_passwordField.getValue())) {
                result = MSGS.passwordDoesNotMatch();
            }
        }
        return result;
    }
}
