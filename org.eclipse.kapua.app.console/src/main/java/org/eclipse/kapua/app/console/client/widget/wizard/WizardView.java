package org.eclipse.kapua.app.console.client.widget.wizard;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Element;

/**
 * 
 * WizardView
 * 
 * A generic wizard step that must be extended with a concrete implementation.
 * 
 */
public abstract class WizardView extends LayoutContainer
{

    private WizardContext    context;
    private WizardViewStatus status;

    private WizardDialog     dialog;

    private int              wizardId;
    private String           viewTitle;
    private String           navTitle;

    /**
     * 
     * Create a new instance of a generic WizardView
     * 
     * @param parentWizardDialog
     * @param id
     * @param viewTitle
     * @param navTitle
     */
    public WizardView(WizardDialog parentWizardDialog, int id, String viewTitle, String navTitle)
    {
        dialog = parentWizardDialog;
        setWizardId(id);
        setViewTitle(viewTitle);

        // nav title
        if ((navTitle == null) || ("".equals(navTitle))) {
            setViewTitle(viewTitle);
        }

        setNavTitle(navTitle);

        // status
        setStatus(new WizardViewStatus());
        resetStatus();
    }

    /**
     * 
     * Reset the specific internal status related on the async and step validation.
     * 
     */
    public void resetStatus()
    {
        status.setAsyncValidationInProgress(false);
        status.setWizardStepValid(false);
    }

    /**
     * 
     * Return true if a validation is in progress.
     * 
     * @return boolean
     */
    public boolean isValidationInProgress()
    {
        return status.isAsyncValidationInProgress();
    }

    /**
     * 
     * Return true if this wizard passed the validation phase.
     * 
     * @return boolean
     */
    public boolean isWizardStepValid()
    {
        return status.isWizardStepValid();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj != null) {
            if (obj instanceof WizardView) {
                return (wizardId == ((WizardView) obj).getWizardId());
            }
        }
        return false;
    }

    /*
     * Implement a specific onRender behavior to set up a standard form layout view.
     * 
     * (non-Javadoc)
     * 
     * @see com.extjs.gxt.ui.client.widget.LayoutContainer#onRender(com.google.gwt.user.client.Element, int)
     */
    protected void onRender(final Element parent, int index)
    {
        super.onRender(parent, index);

        setWidth("100%");
        setStyleAttribute("background-color", "white");
        setScrollMode(Scroll.AUTOY);
        setBorders(false);

        // Add a form layout
        FormLayout formLayout = new FormLayout();
        formLayout.setLabelWidth(100);

        setLayout(formLayout);

        // Create the wizard view title
        Text t = new Text("<b>" + viewTitle + "</b>");
        t.setStyleAttribute("margin", "5px");
        add(t);

        // Call abstract add controls
        addControls(context);
    }

    /**
     * 
     * Add the concrete front end implementation of the view.
     * 
     * @param wizardContext
     */
    abstract public void addControls(WizardContext wizardContext);

    /**
     * 
     * Open event when the wizard view is set up for the first time.
     * 
     */
    abstract public void open();

    /**
     * 
     * Close this wizard view.
     * 
     */
    abstract public void close();

    /**
     * 
     * Validation step.
     * 
     */
    abstract public void validate();

    /**
     * 
     * Set up the validation step.
     * 
     */
    public void startValidation()
    {
        status.setAsyncValidationInProgress(true);
        status.setWizardStepValid(false);

        dialog.manageButtonStatus(status);
    }

    /**
     * 
     * Set the validation as failure and manages the button status of this wizard view.
     * 
     */
    public void validationFailure()
    {
        status.setAsyncValidationInProgress(false);
        status.setWizardStepValid(false);

        dialog.manageButtonStatus(status);
    }

    /**
     * 
     * End the validation step and move to the next given viewId.
     * 
     * This allow to move back to the previous pages or forward in a NOT linear wizard process.
     * 
     * @param nextViewId
     */
    public void endValidation(int nextViewId)
    {
        status.setAsyncValidationInProgress(false);
        status.setWizardStepValid(true);
        status.setNextPage(nextViewId);

        // manage the buttons status
        dialog.manageButtonStatus(status);

        // move to the next page
        try {
             dialog.moveToView(nextViewId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * End the validation step correctly and move to the next view.
     * No next viewId is needed. Standard flow is used to move on the next view.
     * 
     */
    public void endValidation()
    {

        status.setAsyncValidationInProgress(false);
        status.setWizardStepValid(true);

        dialog.manageButtonStatus(status);

        // move to the next page with the standard flow
        dialog.moveNext(context);

        int viewId = dialog.getCurrentView();
        dialog.moveToView(viewId);
    }

    /**
     * 
     * Execute the apply submit of this view.
     * 
     */
    public void applyOnView()
    {
    }

    /**
     * 
     * Return the actual WizardContext.
     * 
     * @return WizardContext
     */
    public WizardContext getContext()
    {
        return context;
    }

    /**
     * 
     * Set up the current context for this wizard view.
     * 
     * @param context
     */
    public void setContext(WizardContext context)
    {
        this.context = context;
    }

    /**
     * 
     * Return the actual wizard Id view.
     * 
     * @return int
     */
    public int getWizardId()
    {
        return wizardId;
    }

    /**
     * 
     * Set the wizard Id.
     * 
     * @param id
     */
    public void setWizardId(int id)
    {
        this.wizardId = id;
    }

    /**
     * 
     * Return the wizard view title.
     * 
     * @return String
     */
    public String getViewTitle()
    {
        return viewTitle;
    }

    /**
     * 
     * Set the wizard view title to display on the header top panel.
     * 
     * @param viewTitle
     */
    public void setViewTitle(String viewTitle)
    {
        this.viewTitle = viewTitle;
    }

    /**
     * 
     * Return the side lateral title used by the navigation menu.
     * 
     * @return String
     */
    public String getNavTitle()
    {
        return navTitle;
    }

    /**
     * 
     * Set up the side lateral title used by the navigation menu.
     * 
     * @param navTitle
     */
    public void setNavTitle(String navTitle)
    {
        this.navTitle = navTitle;
    }

    /**
     * Return the actual wizard view status.
     * 
     * @return WizardViewStatus
     */
    public WizardViewStatus getStatus()
    {
        return status;
    }

    /**
     * 
     * Set the wizard view status.
     * 
     * @param status
     */
    public void setStatus(WizardViewStatus status)
    {
        this.status = status;
    }

    /**
     * 
     * Return the WizardDialog UI component.
     * 
     * @return WizardDialog
     */
    public WizardDialog getDialog()
    {
        return dialog;
    }

}
