/**
 * 
 */
package org.eclipse.kapua.app.console.shared.analytics;

/**
 * @author Eurotech
 * 
 */
public class GoogleAnalytics
{
    public static final String GA_CHILDACCOUNTS                               = "/childaccounts";
    public static final String GA_CLOUDSETTINGS                               = "/cloudsettings";
    public static final String GA_DASHBOARD                                   = "/dashboard";
    public static final String GA_DASHBOARD_REFRESH                           = "/dashboard/refresh";
    public static final String GA_DATA                                        = "/data";
    public static final String GA_DATA_BYASSET_CHART_LIVE_OFF                 = "/data/byasset/chart/live/off";
    public static final String GA_DATA_BYASSET_CHART_LIVE_ON                  = "/data/byasset/chart/live/on";
    public static final String GA_DATA_BYASSET_TABLE                          = "/data/byasset/table";
    public static final String GA_DATA_BYTOPIC_CHART_LIVE_OFF                 = "/data/bytopic/chart/live/off";
    public static final String GA_DATA_BYTOPIC_CHART_LIVE_ON                  = "/data/bytopic/chart/live/on";
    public static final String GA_DATA_BYTOPIC_TABLE                          = "/data/bytopic/table";
    public static final String GA_DEVICEJOBS                                  = "/devicejobs";
    public static final String GA_DEVICEPROVISIONING                          = "/deviceprovisioning";
    public static final String GA_DEVICES                                     = "/devices";
    public static final String GA_DEVICES_BUNDLES                             = "/devices/bundles";
    public static final String GA_DEVICES_BUNDLES_REFRESH                     = "/devices/bundles/refresh";
    public static final String GA_DEVICES_BUNDLES_START                       = "/devices/bundles/start";
    public static final String GA_DEVICES_BUNDLES_STOP                        = "/devices/bundles/stop";
    public static final String GA_DEVICES_COMMAND                             = "/devices/command";
    public static final String GA_DEVICES_COMMAND_EXECUTE                     = "/devices/command/execute";
    public static final String GA_DEVICES_CONFIGURATION                       = "/devices/configuration";
    public static final String GA_DEVICES_CONFIGURATION_SERVICES_REFRESH      = "/devices/configuration/services/refresh";
    public static final String GA_DEVICES_CONFIGURATION_SERVICES_APPLY        = "/devices/configuration/services/apply";
    public static final String GA_DEVICES_CONFIGURATION_SERVICES_RESET        = "/devices/configuration/services/reset";
    public static final String GA_DEVICES_CONFIGURATION_SNAPSHOTS_REFRESH     = "/devices/configuration/snapshots/refresh";
    public static final String GA_DEVICES_CONFIGURATION_SNAPSHOTS_DOWNLOAD    = "/devices/configuration/snapshots/download";
    public static final String GA_DEVICES_CONFIGURATION_SNAPSHOTS_ROLLBACK    = "/devices/configuration/snapshots/rollback";
    public static final String GA_DEVICES_CONFIGURATION_SNAPSHOTS_UPLOADAPPLY = "/devices/configuration/snapshots/uploadapply";
    public static final String GA_DEVICEPROVISIONING_INSTANT_ACTIVATION       = "/deviceprovisioning/instantactivation";
    //

    public static final String GA_DEVICES_CERTIFICATE                         = "/devices/certificate";
    public static final String GA_DEVICES_DELETE                              = "/devices/delete";
    public static final String GA_DEVICES_DESCRIPTION                         = "/devices/description";
    public static final String GA_DEVICES_EVENTS                              = "/devices/events";
    public static final String GA_DEVICES_EXPORT_CSV                          = "/devices/export/csv";
    public static final String GA_DEVICES_EXPORT_XLS                          = "/devices/export/xls";
    public static final String GA_DEVICES_LIVE_OFF                            = "/devices/live/off";
    public static final String GA_DEVICES_LIVE_ON                             = "/devices/live/on";
    public static final String GA_DEVICES_PACKAGES                            = "/devices/packages";
    public static final String GA_DEVICES_PACKAGES_INSTALL_UPGRADE            = "/devices/packages/install/upgrade";
    public static final String GA_DEVICES_PACKAGES_UNINSTALL                  = "/devices/packages/uninstall";

    public static final String GA_DEVICES_INSTANT_ACTIVATION                  = "/devices/refresh/activation";
    public static final String GA_DEVICES_INSTANT_ACTIVATION_DONE             = "/devices/refresh/activation/done";

    public static final String GA_DEVICES_REFRESH                             = "/devices/refresh";
    public static final String GA_DEVICES_SIMCARD                             = "/devices/simcard";
    public static final String GA_DEVICES_VPN                                 = "/devices/vpn";
    public static final String GA_HEALTHCHECK                                 = "/healthcheck";
    public static final String GA_INFORMATION                                 = "/information";
    public static final String GA_RULES                                       = "/rules";
    public static final String GA_RULES_DELETE                                = "/rules/delete";
    public static final String GA_RULES_EDIT                                  = "/rules/edit";
    public static final String GA_RULES_NEW                                   = "/rules/new";
    public static final String GA_SETTINGS                                    = "/settings";
    public static final String GA_SIMCARD                                     = "/simcard";
    public static final String GA_SIMCARDPROVIDERS                            = "/simcardproviders";
    public static final String GA_TASKS                                       = "/tasks";
    public static final String GA_USAGES                                      = "/usages";
    public static final String GA_USERS                                       = "/users";
    public static final String GA_VPN                                         = "/vpn";
    public static final String GA_PKI                                         = "/pki";
    public static final String GA_PKIMGMT                                     = "/pkiMgmt";

    public static native void setAccount(String account, String edcAccount, String edcUser) /*-{
	    if (typeof ga !== 'undefined') {
    		$wnd.ga('create', account, 'auto');
    		$wnd.ga('set', 'dimension1', edcAccount);
    		$wnd.ga('set', 'dimension2', edcUser);
	    }
	}-*/;

    public static native void setDimensions(String edcAccount, String edcUser) /*-{
	    if (typeof ga !== 'undefined') {
    		$wnd.ga('set', 'dimension1', edcAccount);
    		$wnd.ga('set', 'dimension2', edcUser);
	    }
	}-*/;

    public static native void trackPageview(String page) /*-{
		if (typeof ga !== 'undefined') {
		    $wnd.ga('send', 'pageview', {
			    'page': page
		    });
		}
	}-*/;
}
