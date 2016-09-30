/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
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

import java.util.Stack;

import com.extjs.gxt.ui.client.data.PagingLoadResult;

public interface KapuaPagingLoadResult<Data> extends PagingLoadResult<Data> {

    public int getVirtualOffset();

    public void setVirtualOffset(int offset);

    public Stack<KapuaBasePagingCursor> getCursorOffset();

    public int getLastOffset();
}
