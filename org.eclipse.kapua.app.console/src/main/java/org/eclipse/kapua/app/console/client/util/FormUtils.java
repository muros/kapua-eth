package org.eclipse.kapua.app.console.client.util;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.MultiField;

public class FormUtils {
    public static boolean isDirty(FieldSet fieldSet) {
        List<Component> fields = fieldSet.getItems();
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i) instanceof MultiField) {
                MultiField<?> multiField = (MultiField<?>) fields.get(i);
                for (Field<?> field : multiField.getAll()) {
                    if (field.isRendered() && field.isDirty()) {
                        return true;
                    }
                }
            } else if (fields.get(i) instanceof Field) {
                Field<?> field = (Field<?>) fields.get(i);
                if (field.isRendered() && field.isDirty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValid(FieldSet fieldSet) {
        List<Component> fields = fieldSet.getItems();
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i) instanceof Field) {
                Field<?> field = (Field<?>) fields.get(i);
                if (field.isRendered() && !field.isValid()) {
                    return false;
                }
            }
        }
        return true;
    }
}
