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
package org.eclipse.kapua.service.datastore.internal.elasticsearch;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.eclipse.kapua.KapuaIllegalNullArgumentException;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

public class KapuaAssetInfoBuilder {

	private KapuaAssetInfo assetInfo;
	
	public KapuaAssetInfoBuilder build(SearchHit searchHit) throws ParseException, KapuaIllegalNullArgumentException {
		
		ArgumentValidator.notNull(searchHit, "searchHit");
		
		Map<String, SearchHitField> fields = searchHit.getFields();
		String assetName = fields.get(EsSchema.ASSET_NAME).getValue();
		String timestampStr = fields.get(EsSchema.ASSET_TIMESTAMP).getValue();
		//String account = fields.get(EsSchema.ASSET_ACCOUNT).getValue();
		this.assetInfo = new KapuaAssetInfo(assetName, (Date) EsUtils.convertToEdcObject("date", timestampStr));
		return this;
	}
	
	public KapuaAssetInfo getAssetInfo() {
		return this.assetInfo;
	}
}
