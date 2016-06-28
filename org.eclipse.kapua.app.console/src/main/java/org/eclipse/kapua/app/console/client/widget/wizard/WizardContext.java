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
package org.eclipse.kapua.app.console.client.widget.wizard;


/**
 * 
 * WizardContext
 *
 */
public abstract class WizardContext implements java.io.Serializable {

	public static final long serialVersionUID = 5732082819204294773L;
	
	private boolean isComplete;
	
	
	public WizardContext() {		
		reset();
	}


	/**
	 * 
	 * Reset the current wizard context.
	 * 
	 */
	public abstract void reset();

	
	/**
	 * 
	 * Return true if the wizard context is set as completed.
	 * 
	 * @return boolean
	 */
	public boolean isComplete() {
    	return isComplete;    	
    }    
    
    
    /**
     * 
     * Mark the context as completed.
     * 
     */
    public void setAsComplete() {
    	isComplete=true;
    }

	
}
