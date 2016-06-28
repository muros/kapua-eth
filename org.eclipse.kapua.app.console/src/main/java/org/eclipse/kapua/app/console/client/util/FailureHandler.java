package org.eclipse.kapua.app.console.client.util;

import java.util.List;

import org.eclipse.kapua.app.console.shared.GwtEdcErrorCode;
import org.eclipse.kapua.app.console.shared.GwtEdcException;

import com.allen_sauer.gwt.log.client.Log;
import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.eurotech.cloud.console.client.messages.ValidationMessages;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * Handles GwtExceptions from RCP calls.
 *
 * @author mcarrer
 *
 */
public class FailureHandler {

    private static final ConsoleMessages   CMSGS = GWT.create(ConsoleMessages.class);
    private static final ValidationMessages MSGS = GWT.create(ValidationMessages.class);

    public static void handle(Throwable caught) {
        if (caught instanceof GwtEdcException) {

            GwtEdcException  gee = (GwtEdcException) caught;
            GwtEdcErrorCode code = gee.getCode();
            switch (code) {

            case UNAUTHENTICATED:
                ConsoleInfo.display(CMSGS.loggedOut(), caught.getLocalizedMessage());
                Window.Location.reload();
                break;

            default:
                ConsoleInfo.display(CMSGS.error(), caught.getLocalizedMessage());
                Log.error("RPC Error", caught);
                break;
            }
        } else if (caught instanceof StatusCodeException &&
                   ((StatusCodeException) caught).getStatusCode() == 0) {

            // the current operation was interrupted as the user started a new one
            // or navigated away from the page.
            // we can ignore this error and do nothing.
        } else {

            ConsoleInfo.display(CMSGS.error(), caught.getLocalizedMessage());
            caught.printStackTrace();
        }
    }


    @SuppressWarnings("unchecked")
    public static boolean handleFormException(FormPanel form, Throwable caught) {

        boolean isWarning = false;
        if (caught instanceof GwtEdcException) {

            List<Field<?>> fields = form.getFields();
            GwtEdcException   gee = (GwtEdcException) caught;
            GwtEdcErrorCode  code = gee.getCode();
            switch (code) {

            case INVALID_XSRF_TOKEN:
                ConsoleInfo.display(CMSGS.error(), CMSGS.securityInvalidXSRFToken());
                Window.Location.reload();
                break;

            case UNAUTHENTICATED:
                ConsoleInfo.display(CMSGS.loggedOut(), caught.getLocalizedMessage());
                Window.Location.reload();
                break;

            case DUPLICATE_NAME:
                boolean fieldFound = false;
                String duplicateFieldName = gee.getArguments()[0];
                for (Field<?> field : fields) {
                    if (duplicateFieldName.equals(field.getName())) {
                        TextField<String>  textField = (TextField<String>) field;
                        textField.markInvalid(MSGS.duplicateValue());
                        fieldFound = true;
                        break;
                    }
                }
                if (!fieldFound) {
                    ConsoleInfo.display(CMSGS.error(), caught.getLocalizedMessage());
                }
                break;

            case ILLEGAL_NULL_ARGUMENT:
                String invalidFieldName = gee.getArguments()[0];
                for (Field<?> field : fields) {
                    if (invalidFieldName.equals(field.getName())) {
                        TextField<String>  textField = (TextField<String>) field;
                        textField.markInvalid(MSGS.invalidNullValue());
                        break;
                    }
                }
                break;

            case ILLEGAL_ARGUMENT:
                String invalidFieldName1 = gee.getArguments()[0];
                for (Field<?> field : fields) {
                    if (invalidFieldName1.equals(field.getName())) {
                        TextField<String>  textField = (TextField<String>) field;
                        textField.markInvalid(gee.getCause().getMessage());
                        break;
                    }
                }
                break;

            case CANNOT_REMOVE_LAST_ADMIN:
                String adminFieldName = gee.getArguments()[0];
                for (Field<?> field : fields) {
                    if (adminFieldName.equals(field.getName())) {
                        CheckBoxGroup adminCheckBoxGroup = (CheckBoxGroup) field;
                        adminCheckBoxGroup.markInvalid(MSGS.lastAdministrator());
                        break;
                    }
                }
                break;

            case INVALID_RULE_QUERY:
                for (Field<?> field : fields) {
                    if ("query".equals(field.getName())) {
                        TextArea statement = (TextArea) field;
                        statement.markInvalid(caught.getLocalizedMessage());
                        break;
                    }
                }
                break;

            case WARNING:
                isWarning = true;
                ConsoleInfo.display(CMSGS.warning(), caught.getLocalizedMessage());
                break;

            default:
                ConsoleInfo.display(CMSGS.error(), caught.getLocalizedMessage());
                caught.printStackTrace();
                break;
            }
        } else {

            ConsoleInfo.display(CMSGS.error(), caught.getLocalizedMessage());
            caught.printStackTrace();
        }

        return isWarning;
    }
}
