package org.eclipse.kapua.app.console.client.widget;


/**
 * 
 * WizardUIProperties
 *
 */
public class WizardUIProperties {

	// wizard dialog
	private String heading;
	private int width;
	private int height;
	
	// nav bar
	private String wizardNavTitle;
	
	
	public WizardUIProperties() {
		
		// set the default wizard properties
		heading="Everyware Cloud Wizard";
		width=600;
		height=480;
		
		// nav bar
		setWizardNavTitle("Wizard steps:");
	}
	
	
	public String getHeading() {
		return heading;
	}
	
	public void setHeading(String heading) {
		this.heading = heading;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getWizardNavTitle() {
		return wizardNavTitle;
	}

	public void setWizardNavTitle(String wizardNavTitle) {
		this.wizardNavTitle = wizardNavTitle;
	}

	
}
