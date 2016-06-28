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

import java.util.ArrayList;

import org.eclipse.kapua.app.console.shared.model.GwtSession;

import com.eurotech.cloud.console.client.messages.ConsoleMessages;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DeckPanel;

/**
 * 
 * WizardDialog
 * 
 * A generic wizard dialog that must to be extended to create a specific and concrete wizard.
 * 
 */
public abstract class WizardDialog extends Dialog implements WizardUIActionsInterface
{

    private static final ConsoleMessages 				MSGS = GWT.create(ConsoleMessages.class);
    
    private GwtSession                           gwtSession;
        
    private WizardUIProperties 			   wizardProperties;
    public WizardContext 					  wizardContext;    
    private ArrayList<WizardView> 			    wizardViews;
       
    private WizardNavBar 					   wizardNavBar;    
    private DeckPanel							  deckPanel;    
    private Status 									 status;
    
    // internal current wizard view 
    private int currentView;    
    
    // buttons
    protected Button cancel;    
    protected Button back;
    protected Button next;    
    protected Button apply;


    /**
     * 
     * Initialize the WizardContext based on the given session
     * 
     * @param session
     * @return WizardContext
     */
    abstract public WizardContext initContext(GwtSession session);

    
    abstract public void resetContext();    
    
    
    abstract public void initViews();

    
    abstract public WizardUIProperties setupWizardUIProperties();
    
    
    /**
     * 
     * Return the actual wizard context.
     * 
     * @return WizardContext
     */
    public WizardContext getWizardContext() {
    	return wizardContext;
    }
            
    
    /**
     * 
     * Init the UI aspects of this wizard dialog.
     * The wizardProperties will be used. Internal method.
     * 
     */
    private void initUIProperties()
    {
        // wizard properties
        wizardProperties = setupWizardUIProperties();

        // default properties
        if (wizardProperties == null) {
            wizardProperties = new WizardUIProperties();
        }
    }
    
    /**
     * 
     * Display the given view.
     * 
     * @param viewId
     */
    public void showView(int viewId)
    {
        deckPanel.showWidget(viewId);

        wizardNavBar.update(viewId);

        // manage the button status
        WizardView view = wizardViews.get(viewId);
        if (view != null) {

            // standard button manage
            manageButtonStatus(view.getStatus());

            view.open();
        }
    }
    
    
    /**
     * 
     * Create the wizard dialog window
     * 
     * @param gwtSession
     */
    public WizardDialog(GwtSession gwtSession)
    {    	
        try {    	
	    	this.gwtSession=gwtSession;
	    	    	
	    	initUIProperties();
	    	    	
	    	setWizardViews(new ArrayList<WizardView>());
	    	
	        setButtonAlign(HorizontalAlignment.LEFT);
	        setButtons(""); // don't show OK button
	        setIcon(IconHelper.createStyle("user"));
	        setHeading(wizardProperties.getHeading());
	        setModal(true);
	        setBodyBorder(true);	        
	        setWidth(wizardProperties.getWidth());
	        setHeight(wizardProperties.getHeight());
	        
	        setResizable(false);
	        setClosable(false);
	
	        // deck panel
	        deckPanel = new DeckPanel();
	        deckPanel.setAnimationEnabled(true);
	        deckPanel.setWidth("470px");
	        
	        // set the Gwt session
	        wizardContext=initContext(getGwtSession());
	
	    	initWizardNavBar();    	
	
	    	initViews();
	    	setCurrentView(0); 	
	
	    	HorizontalPanel hp = new HorizontalPanel();
	    	hp.add(wizardNavBar);
	    	hp.add(deckPanel);
	    	hp.setStyleAttribute("background-color", "white");    	
	    	
	        add(hp);                

			showView(currentView);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
   
    
    /**
     * Move the wizard on a specific wizard view.
     * 
     * @param viewId
     */
    public void moveToView(int viewId) {     	
    	setCurrentView(viewId); 
    	showView(currentView);    	
    }
    
    
    /**
     * 
     * Init the wizard navigation bar.
     * 
     */
    private void initWizardNavBar() {
    	wizardNavBar=new WizardNavBar(this);
    	
    	// hide the nav bar
    	if (!wizardProperties.isShowNavbar()) {
    		wizardNavBar.hide();
    	}
    }

    
    /**
     * 
     * Disable the all buttons.
     * 
     */
    public void disableButtons() {
    	cancel.disable();
    	back.disable();
    	next.disable();
    	apply.disable();    	
    }
    
    
    /**
     * 
     * Enable the all buttons.
     * 
     */
    public void unblockButtons() {
    	cancel.enable();
    	back.enable();
    	
    	next.enable();
    	apply.enable();    	
    }
    
    
    /**
     * 
     * Disable the previous button.
     * 
     */
    public void disablePreviousButton() {
    	back.disable();    	
    }
    
    
    /**
     * 
     * Enable the previous button.
     * 
     */
    public void enablePreviousButton() {
    	back.enable();
    }
    
    
    /**
     * 
     * Disable the next button.
     * 
     */
    public void disableNextButton() {
    	next.disable();    	
    }

    
    /**
     * 
     * Enable the next button.
     * 
     */
    public void enableNextButton() {
    	next.enable();    	
    }
    
    
    /**
     * 
     * Disable the cancel button to abort the wizard dialog.
     * 
     */
    public void disableCancelButton() {
    	cancel.disable();    	
    }

    
    /**
     * 
     * Enable the cancel button to abort the wizard dialog.
     * 
     */
    public void enableCancelButton() {
    	cancel.enable();    	
    }    
    
    
	/**
	 * 
	 * Manage the button status based on the WizardViewStatus object.
	 * If a validation is in progress the buttons will be automatically disable.
	 * This method enable or disable the back or nect button and enable the aplply button only at the last step. 
	 * 
	 * @param status
	 */
	public void manageButtonStatus(WizardViewStatus status) {    			
		if (status.isAsyncValidationInProgress()) {			
			// disable all buttons
			disableButtons();
	    	
		} else {			
	    	// always available
	    	cancel.enable();
	    	    	
	    	int max=getWizardViews().size();
	    	
	    	// back    	
	    	if (currentView==0)	back.disable();
	    	if (currentView>=1)	back.enable();
	    	
	    	// next & apply
	    	if (currentView<max-1)	{
	    		next.enable();	    		
	    		apply.disable();
	    	}
	    	if (currentView==max-1) {
	    		next.disable();	    		
	    		apply.enable();
	    	}  			
		}		
    }
    
	
	/**
	 * 
	 * Reset and re-initializze the actual wizard view.
	 * 
	 */
	public void resetView() {
    	deckPanel.clear();	
    	setWizardViews(new ArrayList<WizardView>());    	
    	initViews();
	}
	
    
    /**
     * 
     * Add the specific wizard view to this wizard dialog.
     * 
     * @param wizardView
     */
    public void addWizardView(WizardView wizardView) {    	
    	wizardView.setContext(this.getWizardContext());    	
    	
    	getWizardViews().add(wizardView);   	
    	deckPanel.add(wizardView.asWidget());    	
    }
    
    
    @Override
    protected void createButtons()
    {
        super.createButtons();

        // status
        status = new Status();
        status.setBusy(MSGS.waitMsg());
        status.hide();
        status.setAutoWidth(true);

        getButtonBar().add(status);
        getButtonBar().add(new FillToolItem());

        // cancel
        cancel = new Button(MSGS.cancelButton());
        cancel.setToolTip("Exit to the wizard setup and return to the login page, every informations will lost.");
        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
            	MessageBox.confirm(MSGS.confirm(), "Are you sure that you want to close this wizard ? Every informations will lost.",
                        new Listener<MessageBoxEvent>() {
		                    public void handleEvent(MessageBoxEvent ce) {
		                        // if cancel is confirmed
		                        Dialog dialog = ce.getDialog();
		                        if (dialog.yesText.equals(ce.getButtonClicked().getText())) {
		                        	
		                        	// call the close method on the WizardView
		                        	WizardView view=wizardViews.get(currentView);   		                        	
		                        	view.close();
		                        	
		                        	hide();

		                        	cancel();

//		                            EdcInfo.display("Info", "Wizard aborted.");		                        	
		                        }
		                    }
                });                		
            }
        });

        addButton(cancel);
        
        // back
        back = new Button("<< Back");
        back.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {            	
        		try {            	
	            	// move back to the previous step
        			
                	// Call the close method on the WizardView
                	WizardView view=wizardViews.get(currentView);   		                        	
                	view.close();        			
        			
	            	moveBack(wizardContext);            	

					showView(currentView);
				} catch (Exception e) {
					e.printStackTrace();
				}      		
            }
        });

        addButton(back);
        
        // next
        next = new Button("Next >>");
        next.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
            	            	
            	//
            	// Call the validate method, this will send a notification after the validation
            	// and the request will be processes correctly.
            	
            	WizardView view=wizardViews.get(currentView);            	
            	view.validate();
            	
            }
        });

        addButton(next);
        
        // apply
        apply = new Button("Finish");
        apply.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
            	
            	// This is the final wizard process.
            	
            	// Call an internal apply method to realize the possibility to manage and intercept the apply
            	// directly on the single view
            	WizardView view=wizardViews.get(currentView);            	
            	view.applyOnView();
            	
            	//
            	// Call the apply method to persist or manage at the end the collected informations
            	
            	// Note: It's important to manage correctly the exit from the wizard inside            	
            	apply();
            	
            }
        });

        addButton(apply);                
    }
    
    
    /**
     * 
     * Perform a generic logic to move backward to the previous wizard step.
     * Override this method to create a specific wizard steps logic.
     * 
     */
    public void moveNext(WizardContext wizardContext) {
    	moveNext();
    }
    
    
    /**
     * 
     * If it's possible, move to the next wizard view.
     * 
     */
    public void moveNext() {
		if (currentView<getWizardViews().size()-1) {
			currentView++;
		}     	
    }
    
    
    /**
     * Perform a generic logic to move forward to the next wizard step.
     * Override this method to create a specific wizard steps logic.
     * 
     */
    public void moveBack(WizardContext wizardContext) {
    	moveBack();   	
    }
    
    
    /**
     * 
     * If it's possible, move to the previous wizard view.
     * 
     */
    public void moveBack() {
    	if (currentView>0) {
    		currentView--;
    	}        	
    }
    
    
    @Override
    abstract public void apply();

    
    @Override
	abstract public void cancel(); 

    
	/**
	 * 
	 * Set the wizard context as completed and close the wizard dialog.
	 * 
	 */
	public void endWizard() {
    	wizardContext.setAsComplete();    	
    	hide();    	
    }


	/**
	 * 
	 * Return true if this wizard has been completed.
	 * 
	 * @return boolean
	 */
	public boolean isWizardCompleted() {
		return wizardContext.isComplete();
	}


	/**
	 * 
	 * Return the side lateral menu.
	 * 
	 * @return WizardNavBar
	 */
	public WizardNavBar getWizardNav() {
		return wizardNavBar;
	}


	/**
	 * 
	 * Return the current Gwt session.
	 * 
	 * @return GwtSession
	 */
	public GwtSession getGwtSession() {
		return gwtSession;
	}


	/**
	 * 
	 * Set the current Gwt session.
	 * 
	 * @param gwtSession
	 */
	public void setGwtSession(GwtSession gwtSession) {
		this.gwtSession = gwtSession;
	}


	/**
	 * 
	 * Return the wizard view list.
	 * 
	 * @return ArrayList<WizardView>
	 */
	public ArrayList<WizardView> getWizardViews() {
		return wizardViews;
	}


	/**
	 * 
	 * Set the wizard view list.
	 * 
	 * @param wizardViews
	 */
	public void setWizardViews(ArrayList<WizardView> wizardViews) {
		this.wizardViews = wizardViews;
	}


	/**
	 * 
	 * Return the wizard properties.
	 * 
	 * @return WizardUIProperties
	 */
	public WizardUIProperties getWizardProperties() {
		return wizardProperties;
	}


	/**
	 * 
	 * Set the wizard properties to use to customize this wizard dialog.
	 * 
	 * @param wizardProperties
	 */
	public void setWizardProperties(WizardUIProperties wizardProperties) {
		this.wizardProperties = wizardProperties;
	}


	/**
	 * 
	 * Return the current integer Id of the current wizard view.
	 * 
	 * @return int
	 */
	public int getCurrentView() {
		return currentView;
	}


	/**
	 * 
	 * Set the current view by Id.
	 * 
	 * @param currentView
	 */
	public void setCurrentView(int currentView) {
		this.currentView = currentView;
	}


}
