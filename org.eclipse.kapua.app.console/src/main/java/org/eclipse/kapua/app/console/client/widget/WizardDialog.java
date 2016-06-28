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
import com.extjs.gxt.ui.client.widget.VerticalPanel;
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
    
    
    public WizardContext getWizardContext() {
    	return wizardContext;
    }
            
    
    private void initUIProperties() {
    	// wizard properties
    	wizardProperties=setupWizardUIProperties();
    	
    	// default if null
    	if (wizardProperties==null) {
        	wizardProperties=new WizardUIProperties();    		
    	}    	
    }
    
    
    public void showWizardView(int viewId) {    	
    	deckPanel.showWidget(viewId);
    	
    	wizardNavBar.update(viewId);
        
    	// manage the button status
    	WizardView view=wizardViews.get(viewId);
    	if (view!=null) {
    		manageButtonStatus(view.getStatus());    	    		
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
	        setBodyStyle("padding: 8px;background: none");
	        
	        setWidth(wizardProperties.getWidth());
	        setHeight(wizardProperties.getHeight());
	        
	        setResizable(false);
	        setClosable(false);
	
	        // deck panel
	        deckPanel=new DeckPanel();
	        deckPanel.setAnimationEnabled(true);
	        
	        wizardContext=initWizardContext(getGwtSession());
	
	    	initWizardNavBar();    	
	
	    	initViews();
	    	setCurrentView(0); 	
	
	    	HorizontalPanel hp=new HorizontalPanel();
	    	hp.add(wizardNavBar);
	    	hp.add(deckPanel);
	    	    	    	
	    	// Vertical layout
	    	VerticalPanel vp=new VerticalPanel();    	
	//    	Label topText=new Label("Additional text");    	    	
	//    	vp.add(topText);        
	    	vp.add(hp);
	    	
	        add(vp);                

			showWizardView(currentView);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
   
    
    /**
     * 
     * Move the wizard on a specific wizard view.
     * 
     * @param viewId
     * @throws Exception 
     */
    public void moveToView(int viewId) {    	
    	setCurrentView(viewId); 
    	showWizardView(currentView);    	
    }
    
    
    private void initWizardNavBar() {
    	wizardNavBar=new WizardNavBar(this);
    }


	public void manageButtonStatus(WizardViewStatus status) {    	
		
		if (status.isAsyncValidationInProgress()) {
			
			// disable all buttons
	    	cancel.disable();
	    	back.disable();
	    	next.disable();
	    	apply.disable();
	    	
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
    
	
	public void resetView() {
    	deckPanel.clear();	
    	setWizardViews(new ArrayList<WizardView>());    	
    	initViews();
	}
	
    
    public void addWizardView(WizardView wizardView) {    	
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
	            	moveBack(wizardContext);            	

					showWizardView(currentView);
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
        apply = new Button("Apply");
        apply.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {

            	//
            	// Call the apply method to persist or manage at the end the collected informations
            	// This is the final wizard process.
            	
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
    
    
    public void moveBack() {
    	if (currentView>0) {
    		currentView--;
    	}        	
    }
    
    
    @Override
    abstract public void apply();

    
    @Override
	abstract public void cancel(); 

    
	public void endWizard() {
    	wizardContext.setAsComplete();    	
    	hide();    	
    }


	public boolean isWizardCompleted() {
		return wizardContext.isComplete();
	}


	public WizardNavBar getWizardNav() {
		return wizardNavBar;
	}


	public GwtSession getGwtSession() {
		return gwtSession;
	}


	public void setGwtSession(GwtSession gwtSession) {
		this.gwtSession = gwtSession;
	}


	public ArrayList<WizardView> getWizardViews() {
		return wizardViews;
	}


	public void setWizardViews(ArrayList<WizardView> wizardViews) {
		this.wizardViews = wizardViews;
	}


	public WizardUIProperties getWizardProperties() {
		return wizardProperties;
	}


	public void setWizardProperties(WizardUIProperties wizardProperties) {
		this.wizardProperties = wizardProperties;
	}


	public int getCurrentView() {
		return currentView;
	}


	public void setCurrentView(int currentView) {
		this.currentView = currentView;
	}


}
