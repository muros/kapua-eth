package org.eclipse.kapua.app.console.client.util;

import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;


public class SwappableListStore<M extends ModelData> extends ListStore<M> {

    public SwappableListStore(ListLoader<?> loader) {
        super(loader);
    }

    public void swapModelInstance (M oldModel, M newModel) {
        super.swapModelInstance (oldModel, newModel);
    }
}
