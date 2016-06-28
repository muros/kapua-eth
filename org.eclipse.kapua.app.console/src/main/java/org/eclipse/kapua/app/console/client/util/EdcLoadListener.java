package org.eclipse.kapua.app.console.client.util;

import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.event.LoadListener;

public class EdcLoadListener extends LoadListener {
    public void loaderLoadException(LoadEvent le) {
        if (le.exception != null) {
            FailureHandler.handle(le.exception);
        }
    }
}
