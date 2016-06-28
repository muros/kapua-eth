package org.eclipse.kapua.app.console.client.widget;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Element;

/**
 * 
 * WizardView
 * 
 * A generic wizard step.
 *
 */
public abstract class WizardView extends LayoutContainer {
	
	private WizardContext context;	
	private WizardViewStatus status;
		
	private WizardDialog parent;	
	
	private int wizardId;	
	private String viewTitle;
	private String navTitle;
	
	
	/**
	 * 
	 * A generic wizard step
	 * 
	 * @param parentWizardDialog
	 * @param id
 	 * @param viewTitle 
 	 * @param navTitle
	 * @param wizardContext
	 */
	public WizardView(WizardDialog parentWizardDialog,
					  int id, String viewTitle, String navTitle,
					  WizardContext wizardContext)
	{
		parent=parentWizardDialog;
		setWizardId(id);
		setViewTitle(viewTitle);
		
		// nav title
		if ((navTitle==null) || ("".equals(navTitle))) {
			setViewTitle(viewTitle);
		}
		
		setNavTitle(navTitle);
		
		setContext(wizardContext);		
		
		// status
		setStatus(new WizardViewStatus());
		resetStatus();
	}
		
	
	public void resetStatus() {
		status.setAsyncValidationInProgress(false);
		status.setWizardStepValid(false);
	}
	
	
	public boolean isValidationInProgress() {
		return status.isAsyncValidationInProgress();
	}
	
	
	public boolean isWizardStepValid() {
		return status.isWizardStepValid();
	}	
	
	
	@Override
	public boolean equals(Object obj) {		
		if (obj!=null) {
			if (obj instanceof WizardView) {
				return (wizardId==((WizardView) obj).getWizardId());
			}
		}		
		return false;		
	}

	
	protected void onRender(final Element parent, int index) {
		super.onRender(parent, index);

		// TO DO: to use the wizard properties do determine the correct window size
		// now we put fixed sized
		setWidth(420);
		setHeight(450);		

		setScrollMode(Scroll.AUTOY);		
		setBorders(true);		
//		setStyleAttribute("margin", "5px");
		
		// Add a form layout
        FormLayout formLayout = new FormLayout();
        formLayout.setLabelWidth(100);
        formLayout.setDefaultWidth(260);      

        setLayout(formLayout);        
		
        // Create the wizard view title
		Text t = new Text("<b>"+viewTitle+"</b>");
		t.setStyleAttribute("margin", "5px");		
		add(t);	        
        
        addControls(context);        
	}

	
	abstract public void addControls(WizardContext wizardContext);
	
	
	abstract public void validate();
	
	
	public void startValidation() {
		status.setAsyncValidationInProgress(true);
		status.setWizardStepValid(false);

		parent.manageButtonStatus(status);
	}
	
	
	public void validationFailure() {
		status.setAsyncValidationInProgress(false);		
		status.setWizardStepValid(false);

		parent.manageButtonStatus(status);
	}
	
	
	/**
	 * 
	 * End the validation step and move to the next given viewId.
	 * 
	 * This allow to move back to the previous pages or forward in a NOT linear wizard process.
	 * 
	 * @param nextViewId
	 */
	public void endValidation(int nextViewId) {
		status.setAsyncValidationInProgress(false);
		status.setWizardStepValid(true);
		status.setNextPage(nextViewId);
		
		// notify to manage the buttons status
		parent.manageButtonStatus(status);
		
		// move to the next page
		try {
			parent.moveToView(nextViewId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * End the validation step and move to the next page automatically.
	 * No next viewId is needed. Standard flow is used.
	 * 
	 */
	public void endValidation() {
		
		// no next page required, standard algorithm
		
		status.setAsyncValidationInProgress(false);
		status.setWizardStepValid(true);
//		getStatus().setNextPage(nextViewId);
		
		parent.manageButtonStatus(status);
		
		// move to the next page with the standard flow
		parent.moveNext(context);
	}	
	
		
	public WizardContext getContext() {
		return context;
	}


	public void setContext(WizardContext context) {
		this.context = context;
	}


	public int getWizardId() {
		return wizardId;
	}


	public void setWizardId(int id) {
		this.wizardId = id;
	}


	public String getViewTitle() {
		return viewTitle;
	}


	public void setViewTitle(String viewTitle) {
		this.viewTitle = viewTitle;
	}


	public String getNavTitle() {
		return navTitle;
	}


	public void setNavTitle(String navTitle) {
		this.navTitle = navTitle;
	}


	public WizardViewStatus getStatus() {
		return status;
	}


	public void setStatus(WizardViewStatus status) {
		this.status = status;
	}

	
}
