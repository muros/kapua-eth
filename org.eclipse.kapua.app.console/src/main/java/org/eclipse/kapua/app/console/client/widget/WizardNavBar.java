package org.eclipse.kapua.app.console.client.widget;

import java.util.ArrayList;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * WizardNavBar
 * 
 * Side navigation bar component.
 *
 */
public class WizardNavBar extends LayoutContainer {
		
	private WizardDialog wizardDialog;
	
	private SimplePanel container;
	private ContentPanel panel;
	
	
	public WizardNavBar(WizardDialog wizardDialog) {		
		this.wizardDialog=wizardDialog;
		this.container=new SimplePanel();
	}
	
	
	protected void onRender(final Element parent, int index) {
		super.onRender(parent, index);

		setLayout(new FitLayout());
		
		setWidth(150);
		setHeight(wizardDialog.getWizardProperties().getHeight());		
		setBorders(true);
		
		container.setHeight(wizardDialog.getHeight()+"px");
		add(container);
	}	
	
	
	public void update(int id) {		
		container.clear();
		
		panel = new ContentPanel();
		panel.setScrollMode(Scroll.AUTOY);		
		panel.setBorders(false);		
		panel.setHeaderVisible(false);
		panel.setLayout(new RowLayout(Orientation.VERTICAL));		
		panel.setBodyStyle("background-color:#F0F0F0");

		// Top wizard navigation title
		final Text wizardNavTitle = new Text(wizardDialog.getWizardProperties().getWizardNavTitle()+"</BR>");
		wizardNavTitle.setStyleAttribute("margin", "5px");		
		panel.add(wizardNavTitle);
				
		ArrayList<WizardView> wizardViews=wizardDialog.getWizardViews();		
		for (int i=0; i<wizardViews.size(); i++) {			
			WizardView vw=wizardViews.get(i);
			
			int step=i+1;		
			
			// Show the short description
			Text label = new Text(step+". "+vw.getNavTitle());
			label.setStyleAttribute("margin", "5px");
			label.setBorders(false);
								
			if (i==id) {
				label.setText("<b>"+step+". "+vw.getNavTitle()+"<b>");
			}
				
			panel.add(label);
		}								
		
		container.add(panel.asWidget());				
	}
	
	
}
