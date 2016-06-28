package org.eclipse.kapua.app.console.client.widget;

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
