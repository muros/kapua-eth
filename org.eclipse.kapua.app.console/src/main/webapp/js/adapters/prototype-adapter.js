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
/*
 Highcharts JS v2.1.9 (2011-11-11)
 Prototype adapter

 @author Michael Nelson, Torstein H?nsi.

 Feel free to use and modify this script.
 Highcharts license: www.highcharts.com/license.
*/
var HighchartsAdapter=function(){var g=typeof Effect!=="undefined";return{init:function(c){if(g)Effect.HighchartsTransition=Class.create(Effect.Base,{initialize:function(a,b,d,e){var f;this.element=a;this.key=b;f=a.attr(b);if(b==="d"){this.paths=c.init(a,a.d,d);this.toD=d;f=0;d=1}this.start(Object.extend(e||{},{from:f,to:d,attribute:b}))},setup:function(){HighchartsAdapter._extend(this.element);if(!this.element._highchart_animation)this.element._highchart_animation={};this.element._highchart_animation[this.key]=
this},update:function(a){var b=this.paths;if(b)a=c.step(b[0],b[1],a,this.toD);this.element.attr(this.options.attribute,a)},finish:function(){delete this.element._highchart_animation[this.key]}})},addNS:function(c){var a=/^(?:click|mouse(?:down|up|over|move|out))$/;return/^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/.test(c)||a.test(c)?c:"h:"+c},addEvent:function(c,a,b){if(c.addEventListener||c.attachEvent)Event.observe($(c),HighchartsAdapter.addNS(a),b);else{HighchartsAdapter._extend(c);
c._highcharts_observe(a,b)}},animate:function(c,a,b){var d;b=b||{};b.delay=0;b.duration=(b.duration||500)/1E3;if(g)for(d in a)new Effect.HighchartsTransition($(c),d,a[d],b);else for(d in a)c.attr(d,a[d]);if(!c.attr)throw"Todo: implement animate DOM objects";},stop:function(c){var a;if(c._highcharts_extended&&c._highchart_animation)for(a in c._highchart_animation)c._highchart_animation[a].cancel()},each:function(c,a){$A(c).each(a)},fireEvent:function(c,a,b,d){if(c.fire)c.fire(HighchartsAdapter.addNS(a),
b);else if(c._highcharts_extended){b=b||{};c._highcharts_fire(a,b)}if(b&&b.defaultPrevented)d=null;d&&d(b)},removeEvent:function(c,a,b){if($(c).stopObserving){if(a)a=HighchartsAdapter.addNS(a);$(c).stopObserving(a,b)}if(window===c)Event.stopObserving(c,a,b);else{HighchartsAdapter._extend(c);c._highcharts_stop_observing(a,b)}},grep:function(c,a){return c.findAll(a)},map:function(c,a){return c.map(a)},merge:function(){function c(a,b){var d,e;for(e in b){d=b[e];a[e]=d&&typeof d==="object"&&d.constructor!==
Array&&typeof d.nodeType!=="number"?c(a[e]||{},d):b[e]}return a}return function(){var a=arguments,b,d={};for(b=0;b<a.length;b++)d=c(d,a[b]);return d}.apply(this,arguments)},_extend:function(c){c._highcharts_extended||Object.extend(c,{_highchart_events:{},_highchart_animation:null,_highcharts_extended:true,_highcharts_observe:function(a,b){this._highchart_events[a]=[this._highchart_events[a],b].compact().flatten()},_highcharts_stop_observing:function(a,b){if(a)if(b)this._highchart_events[a]=[this._highchart_events[a]].compact().flatten().without(b);
else delete this._highchart_events[a];else this._highchart_events={}},_highcharts_fire:function(a,b){(this._highchart_events[a]||[]).each(function(d){if(!b.stopped){b.preventDefault=function(){b.defaultPrevented=true};d.bind(this)(b)===false&&b.preventDefault()}}.bind(this))}})}}}();
