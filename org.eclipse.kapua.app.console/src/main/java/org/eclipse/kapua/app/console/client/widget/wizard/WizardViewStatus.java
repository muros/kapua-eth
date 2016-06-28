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
 * WizardStepStatus
 *
 * Contains the status of the current wizard step.
 *
 */
public class WizardViewStatus {
	
	private boolean isAsyncValidationInProgress;
	private boolean isWizardStepValid;

	private int nextPage;
	

	public boolean isWizardStepValid() {
		return isWizardStepValid;
	}

	public void setWizardStepValid(boolean isWizardStepValid) {
		this.isWizardStepValid = isWizardStepValid;
	}

	public boolean isAsyncValidationInProgress() {
		return isAsyncValidationInProgress;
	}

	public void setAsyncValidationInProgress(boolean isAsyncValidationInProgress) {
		this.isAsyncValidationInProgress = isAsyncValidationInProgress;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}
		

	
}
