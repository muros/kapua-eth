package org.eclipse.kapua.app.console.shared.model;

import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.LoadEvent;

public class EdcBasePagingLoader<D extends EdcPagingLoadResult<?>> extends BasePagingLoader<D> implements EdcPagingLoader<D> {

    protected int virtualOffset = 0;

    @SuppressWarnings("rawtypes")
    public EdcBasePagingLoader(DataProxy proxy) {
        super(proxy);
    }

    @SuppressWarnings("rawtypes")
    public EdcBasePagingLoader(DataProxy proxy, DataReader reader) {
        super(proxy, reader);
    }

    public int getVirtualOffset() {
        return virtualOffset;
    }

    public void load(int offset, int limit, int virtualOffset) {
        this.virtualOffset = virtualOffset;
        super.load(offset, limit);
    }

    public void setVirtualOffset(int virtualOffset) {
        this.virtualOffset = virtualOffset;
    }

    /**
     * Use the specified LoadConfig for all load calls. The {@link #reuseConfig}
     * will be set to true.
     */
    public void useLoadConfig(Object loadConfig) {
        super.useLoadConfig(loadConfig);
    }

    @Override
    protected Object newLoadConfig() {
        return new EdcBasePagingLoadConfig();
    }

    @Override
    protected void onLoadSuccess(Object loadConfig, D result) {
        LoadEvent evt = new LoadEvent(this, loadConfig, result);
        totalCount = result.getTotalLength();
        offset = result.getOffset();
        virtualOffset = result.getOffset();
        fireEvent(Load, evt);
    }

    @Override
    protected Object prepareLoadConfig(Object config) {
        config = super.prepareLoadConfig(config);
        EdcPagingLoadConfig pagingConfig = (EdcPagingLoadConfig) config;
        pagingConfig.setOffset(offset);
        pagingConfig.setLimit(limit);
        return config;
    }
}
