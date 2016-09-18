package org.eclipse.kapua.model.query;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.id.KapuaIdFactory;

public class KapuaListResultAdapter extends XmlAdapter<String, KapuaId>{

	private final KapuaLocator locator = KapuaLocator.getInstance();
	private final KapuaIdFactory kapuaIdFactory = locator.getFactory(KapuaIdFactory.class);

	@Override
	public String marshal(KapuaId v) throws Exception 
	{
		return v.getShortId();
	}

	@Override
	public KapuaId unmarshal(String v) throws Exception {
		return kapuaIdFactory.newKapuaId(v);
	}

}
