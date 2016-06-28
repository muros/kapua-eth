package org.eclipse.kapua.app.console.shared.model;

import com.extjs.gxt.ui.client.data.PagingLoader;

public interface EdcPagingLoader<D extends EdcPagingLoadResult<?>> extends PagingLoader<D> {

    public int getVirtualOffset();

    public void setVirtualOffset(int offset);
}
