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
package org.eclipse.kapua.service.authentication;

import java.util.Date;

import org.eclipse.kapua.commons.model.AbstractKapuaEntity;
import org.eclipse.kapua.commons.model.id.KapuaEid;
import org.eclipse.kapua.model.id.KapuaId;

public class AccessTokenImpl extends AbstractKapuaEntity implements AccessToken {
	
	private static final long serialVersionUID = -6003387376828196787L;
	
	private String tokenId;
	private KapuaId userId;
	private KapuaId userScopeId;
	
	public AccessTokenImpl(KapuaEid userId, KapuaEid scopeId, KapuaEid userScopeId, String tokenId) {
		super(null);
		this.scopeId = scopeId;
		this.userId = userId;
		this.userScopeId = userScopeId;
		this.tokenId = tokenId;
		createdOn = new Date();
		createdBy = id;
	}

	@Override
	public KapuaId getId() {
		return id;
	}

	@Override
	public KapuaId getScopeId() {
		return scopeId;
	}

	@Override
	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public KapuaId getCreatedBy() {
		return createdBy;
	}

	@Override
	public String getTokenId() {
		return tokenId;
	}

	public KapuaId getUserScopeId() {
		return userScopeId;
	}
	
	public KapuaId getUserId() {
		return userId;
	}
	
}
