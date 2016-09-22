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
package org.eclipse.kapua.model.query;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.kapua.model.KapuaEntity;

@XmlRootElement(name = "result")
@XmlType(propOrder = {"limitExceeded", "size", "items"})
public interface KapuaListResult<E extends KapuaEntity>
{
	@XmlElement(name = "limitExceeded")
    public boolean isLimitExceeded();

    public void setLimitExceeded(boolean limitExceeded);

    @XmlElementWrapper
    @XmlElement(name = "item")
    public List<E> getItems();
    
    public E getItem(int i);
    
    @XmlElement(name = "size")
    public int getSize();
    
    public boolean isEmpty();
    
    public void addItems(Collection<? extends E> items);
    
    public void clearItems();
}
