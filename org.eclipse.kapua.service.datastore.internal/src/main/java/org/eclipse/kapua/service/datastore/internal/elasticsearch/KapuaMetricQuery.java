/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

public class KapuaMetricQuery<T> extends KapuaMessageQuery
{
	private String name;
	private Class<T> type;
	private T minValue;
	private T maxValue;
	private boolean timeSort;
	
    public KapuaMetricQuery()
    {
		timeSort=false;
	}

    public String getName()
    {
		return name;
	}

    public KapuaMetricQuery<T> setName(String name)
    {
		this.name = name;
		return this;
	}

    public KapuaMetricQuery<T> setType(Class<T> t)
    {
		this.type=t;
		return this;
	}

    public Class<T> getType()
    {
		return type;
	}

    public T getMinValue()
    {
		return minValue;
	}

    public KapuaMetricQuery<T> setMinValue(T minValue)
    {
		this.minValue = minValue;
		return this;
	}

    public T getMaxValue()
    {
		return maxValue;
	}

    public KapuaMetricQuery<T> setMaxValue(T maxValue)
    {
		this.maxValue = maxValue;
		return this;
	}

    public KapuaMetricQuery<T> setKeyOffset(Object offset)
    {
		super.setKeyOffset(offset);
		return this;
	}

    public boolean isTimeSort()
    {
		return timeSort;
	}

    public void setTimeSort(boolean timeSort)
    {
        this.timeSort = timeSort;
	}
}
