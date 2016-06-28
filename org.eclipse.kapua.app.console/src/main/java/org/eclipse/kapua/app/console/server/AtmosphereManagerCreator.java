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
package org.eclipse.kapua.app.console.server;

public class AtmosphereManagerCreator {
    /**
     * Instance of the {@link AtmosphereManager}
     * */
    private static AtmosphereManager instance;

    /**
     * Default constructor<br>
     * Build the instance of {@link AtmosphereManager} if it is null
     */
    public AtmosphereManagerCreator() {
        if (instance == null)
            instance = new AtmosphereManager();
    }

    /**
     * Get the instance of the {@link AtmosphereManager}
     *
     * @return
     */
    public AtmosphereManager getAtmosphereManager() {
        return instance;
    }
}
