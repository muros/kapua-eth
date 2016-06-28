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
package org.eclipse.kapua.app.console.client.widget;

import org.eclipse.kapua.app.console.shared.model.GwtSession;


/**
 *
 *	Wizard action interface
 *
 */
public interface WizardUIActionsInterface {

	
	/**
	 * 
	 * Exit from the wizard.
	 * 
	 */
	abstract public void cancel();
	
	
	/**
	 * 
	 * Apply the wizard information at the final wizard step.
	 * 
	 */
	abstract public void apply();

	
    /**
     * 
     * Initialize the wizard context.
     * It's possible to use some session information if needed.
     * 
     * @param session
     * @return WizardContext
     */
    abstract public WizardContext initWizardContext(GwtSession session);

    
    /**
     * 
     * Reset the wizard context.
     * 
     */
    abstract public void resetContext();    
    
    
    /**
     * 
     * Set up the different wizard views.
     * Create the wizard view process as a sequence of WizardView objects.
     * 
     * Example:
     * 
     * addWizardView(new WizardStepLanding(this, 0, "Welcome", "Welcome", context));
     * 
     */
    abstract public void initViews();

    
    /**
     * 
     * Set up and return the wizard properties to configure the wizard behavior.
     * 
     * @return WizardProperties
     */
    abstract public WizardUIProperties setupWizardUIProperties();
    
	
}