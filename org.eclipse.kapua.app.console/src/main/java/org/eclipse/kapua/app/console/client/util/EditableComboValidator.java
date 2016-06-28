package org.eclipse.kapua.app.console.client.util;

import com.eurotech.cloud.console.client.messages.ValidationMessages;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.core.client.GWT;

public class EditableComboValidator implements Validator {

    private static final ValidationMessages MSGS = GWT.create(ValidationMessages.class);

    private SimpleComboBox<String> m_comboField;
    private String regex = "(^[ ]*0[ ]*-[ ]*[a-zA-z0-9]*|^[ ]*[0-9]*[ ]*)";

    public EditableComboValidator(SimpleComboBox<String> comboField) {
        m_comboField = comboField;
        m_comboField.setRegex(regex);
    }

    public String validate(Field<?> field, String value) {
        String result = null;
        if(!value.matches(m_comboField.getRegex())) {
            result = MSGS.editableComboRegexMsg();
        }
        return result;
    }
}
