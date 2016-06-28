/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.app.console.shared.model;

import com.extjs.gxt.ui.client.data.PagingLoader;

public interface EdcPagingLoader<D extends EdcPagingLoadResult<?>> extends PagingLoader<D> {

    public int getVirtualOffset();

    public void setVirtualOffset(int offset);
}
