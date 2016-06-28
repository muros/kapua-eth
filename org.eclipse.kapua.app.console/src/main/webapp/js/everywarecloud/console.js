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
function whenAvailable(name, callback) {
	var interval = 10; // ms
	window.setTimeout(function() {
		if (window[name]) {
			callback(window[name]);
		} else {
			window.setTimeout(arguments.callee, interval);
		}
	}, interval);
}

//
// Adds a script element with the given src and id to the document head
function downloadJs(elementSrc, elementId) {
	var element = document.createElement("script");
	element.type = "text/javascript";
	element.src = elementSrc;
	element.id = elementId;
	document.head.appendChild(element);
}

//
// Adds a script element with the given src and id to the document head
function downloadCss(elementSrc, elementId) {
	var element = document.createElement("link");
	element.rel = "stylesheet";
	element.type = "text/css";
	element.href = elementSrc;
	element.id = elementId;
	document.head.appendChild(element);
}

//
// Adds jQuery script
function downloadJsJQuery() {
	// downloadJs("js/jquery/jquery-1.4.2.js", "jQueryScript");
}

//
// Adds highchart script
function downloadJsHighchart() {
	// downloadJs("js/highcharts/highcharts.js", "highchartScript");
}

//
// Adds Highchart theme scripts
function downloadJsHighchartTheme() {
	downloadJs("js/highcharts/highcharts-theme-grid.js", "highchartThemeScript");
}

//
// Add GWT scripts
function downloadJsGWT() {
	// downloadJs("console/console.nocache.js","gwtScript");
}

//
// Adds Google Analytics script
function downloadJsGoogleAnalytics() {
	downloadJs("js/google/analytics.js", "googleAnalitycsScript");
}

//
// Adds Openlayers script
function downloadJsOpenlayers() {
	downloadJs("js/openlayers/2.11/openlayers.js", "openlayersScript");
}

//
// Adds console sking script
function downloadJsConsoleSkin() {
	downloadJs("console/skin/skin.js?v=1", "consoleSkinScript");
}

//
// Adds console css
function downloadCssConsole() {
	downloadCss("css/console.css", "consoleCss")
}

//
// Adds console skin css
function downloadCssConsoleSkin() {
	downloadCss("console/skin/skin.css?v=1", "consoleSkinCss")
}

//
// Downloads all necessary resources asynchronously
function deferResourcesDownload() {

	// JS Resources
	downloadJsJQuery();

	// whenAvailable("jQuery", function(t) {
	// downloadJsHighchart();
	//
	// whenAvailable("Highcharts", function(t) {
	downloadJsHighchartTheme();
	//
	// downloadJsGWT();
	// });
	// });

	downloadJsGoogleAnalytics();

	downloadJsOpenlayers();

	downloadJsConsoleSkin();

	// CSS resources
	downloadCssConsole();

	downloadCssConsoleSkin();
}

if (window.addEventListener)
	window.addEventListener("load", deferResourcesDownload, false);
else if (window.attachEvent)
	window.attachEvent("onload", deferResourcesDownload);
else
	window.onload = deferResourcesDownload;