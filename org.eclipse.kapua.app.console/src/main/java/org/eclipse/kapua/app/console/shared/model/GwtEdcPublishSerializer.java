package org.eclipse.kapua.app.console.shared.model;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.SerialTypes;

@SerialTypes(value = {GwtEdcPublish.class})
public abstract class GwtEdcPublishSerializer extends AtmosphereGWTSerializer {
}

