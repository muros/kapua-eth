package org.eclipse.kapua.app.console.client.util;

import com.eurotech.cloud.console.client.messages.ValidationMessages;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.core.client.GWT;

public class NumberFieldValidator implements Validator {
    private static final ValidationMessages MSGS = GWT.create(ValidationMessages.class);

    protected NumberField                   m_numberField;

    protected Long                        minValue;
    protected Long                        maxValue;
    protected String                        fieldName;

    public NumberFieldValidator(Long minValue,
                                Long maxValue,
                                String fieldName) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.fieldName = fieldName;
    }

    @Override
    public String validate(Field<?> field, String value) {
        if (value == null || value.isEmpty())
            return null;

        Long valueL = Long.parseLong(value);

        if (minValue != null &&
                minValue > valueL)
            return MSGS.getString(fieldName + "Min");

        if (maxValue != null &&
                maxValue < valueL)
            return MSGS.getString(fieldName + "Max");

        // TODO Auto-generated method stub
        return null;
    }

}
