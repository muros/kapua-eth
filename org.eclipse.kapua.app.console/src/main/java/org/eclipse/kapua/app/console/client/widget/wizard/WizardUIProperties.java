package org.eclipse.kapua.app.console.client.widget.wizard;


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
	
	private boolean showNavbar;
	
	
	// nav bar
	private String wizardNavTitle;
	private String wizardNavBarColor;
	
	
	public WizardUIProperties() {
		
		// set the default wizard properties
		heading="Everyware Cloud Wizard";
		
		// default wizard window size
		width=630;
		height=490;
		
		// nav bar standard setup
		showNavbar=true;
		setWizardNavTitle("Steps:");		
		wizardNavBarColor="#F0F0F0";		
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


	public boolean isShowNavbar() {
		return showNavbar;
	}


	public void setShowNavbar(boolean showNavbar) {
		this.showNavbar = showNavbar;
	}


	public String getWizardNavBarColor() {
		return wizardNavBarColor;
	}


	public void setWizardNavBarColor(String wizardNavBarColor) {
		this.wizardNavBarColor = wizardNavBarColor;
	}

	
}
