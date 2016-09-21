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
package org.eclipse.kapua.app.console.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundleWithLookup {

    Resources INSTANCE = GWT.create(Resources.class);

    @Source("icons/16x16/server_cluster_upgrade.png")
    ImageResource serverClusterUpgrade16();

    @Source("icons/32x32/server_cluster_upgrade.png")
    ImageResource serverClusterUpgrade32();

    @Source("icons/16x16/server_cluster.png")
    ImageResource serverCluster16();

    @Source("icons/32x32/server_cluster.png")
    ImageResource serverCluster32();

    @Source("icons/16x16/firefox.png")
    ImageResource browserFirefox16();

    @Source("icons/32x32/firefox.png")
    ImageResource browserFirefox32();

    @Source("icons/16x16/safari_browser.png")
    ImageResource browserSafari16();

    @Source("icons/32x32/safari_browser.png")
    ImageResource browserSafari32();

    @Source("icons/16x16/internet_explorer.png")
    ImageResource browserIE16();

    @Source("icons/32x32/internet_explorer.png")
    ImageResource browserIE32();

    @Source("icons/16x16/chrome.png")
    ImageResource browserChrome16();

    @Source("icons/16x16/flag_united_kingdom.png")
    ImageResource flagUnitedKingdom16();

    @Source("icons/32x32/flag_united_kingdom.png")
    ImageResource flagUnitedKingdom32();

    @Source("icons/16x16/flag_italy.png")
    ImageResource flagItaly16();

    @Source("icons/32x32/flag_italy.png")
    ImageResource flagItaly32();

    @Source("icons/16x16/locate.png")
    ImageResource locate16();

    @Source("icons/32x32/locate.png")
    ImageResource locate32();

    @Source("icons/16x16/group_go.png")
    ImageResource switchToAccount16();

    @Source("icons/16x16/server.png")
    ImageResource server16();

    @Source("icons/32x32/group_go.png")
    ImageResource switchToAccount32();

    @Source("icons/16x16/door_out.png")
    ImageResource userLogout16();

    @Source("icons/32x32/door_out.png")
    ImageResource userLogout32();

    @Source("icons/16x16/user_edit.png")
    ImageResource userEdit16();

    @Source("icons/32x32/user_edit.png")
    ImageResource userEdit32();

    @Source("icons/others/iabarcode.png")
    ImageResource instantActivationBarCode();

    @Source("icons/16x16/connect.png")
    ImageResource wrench();

    @Source("icons/16x16/magnifier.png")
    ImageResource magnifier16();

    @Source("icons/32x32/magnifier.png")
    ImageResource magnifier32();

    @Source("icons/16x16/preferences-desktop-remote-desktop.png")
    ImageResource provisioning16();

    @Source("icons/32x32/preferences-desktop-remote-desktop.png")
    ImageResource provisioning32();

    @Source("icons/16x16/filter_delete.png")
    ImageResource filterDelete16();

    @Source("icons/16x16/download_for_mac.png")
    ImageResource downloadMac16();

    @Source("icons/16x16/download_for_linux.png")
    ImageResource downloadLinux16();

    @Source("icons/16x16/download_for_windows.png")
    ImageResource downloadWindows16();

    @Source("icons/16x16/key_go.png")
    ImageResource keyGo();

    @Source("icons/16x16/key_delete.png")
    ImageResource keyRemove();

    @Source("icons/16x16/server_key.png")
    ImageResource serverKey();

    @Source("icons/16x16/computer_key.png")
    ImageResource computerKey();

    @Source("icons/16x16/cross.png")
    ImageResource cross16();

    @Source("icons/16x16/tickGreen.png")
    ImageResource greenTick16();

    @Source("icons/16x16/tickYellow.png")
    ImageResource yellowTick16();

    @Source("icons/16x16/textfield_rename.png")
    ImageResource selectAllText16();

    @Source("icons/16x16/document_info.png")
    ImageResource jobLog16();

    @Source("icons/16x16/resultset_next.png")
    ImageResource play16();

    @Source("icons/32x32/router_cog.png")
    ImageResource provisionRequest32();

    @Source("icons/16x16/router_cog.png")
    ImageResource provisionRequest16();

    @Source("icons/16x16/script_gear.png")
    ImageResource scriptGear16();

    @Source("icons/16x16/script_play_grey.png")
    ImageResource scriptPlayGrey16();

    @Source("icons/16x16/script_play.png")
    ImageResource scriptPlay16();

    @Source("icons/16x16/script_error.png")
    ImageResource scriptError16();

    @Source("icons/16x16/script_go.png")
    ImageResource scriptGo16();

    @Source("icons/16x16/script_tic.png")
    ImageResource scriptTic16();

    @Source("icons/16x16/script_delete.png")
    ImageResource scriptDelete16();

    @Source("icons/32x32/script_gear.png")
    ImageResource batch32();

    @Source("icons/16x16/script_gear.png")
    ImageResource batch16();

    @Source("icons/others/bullet_yellow_14x14.png")
    ImageResource yellowBullet14();

    @Source("icons/others/bullet_red_14x14.png")
    ImageResource redBullet14();

    @Source("icons/others/bullet_green_14x14.png")
    ImageResource greenBullet14();

    @Source("icons/others/bullet_black_14x14.png")
    ImageResource blackBullet14();

    @Source("icons/others/bullet_green_and_black_14x14.png")
    ImageResource greenAndBlackBullet14();

    @Source("icons/others/apple.png")
    ImageResource apple();

    @Source("icons/others/android.png")
    ImageResource android();

    @Source("icons/others/blank.png")
    ImageResource blank();

    @Source("icons/16x16/to_do_list_cheked_1.png")
    ImageResource toDoListChecked();

    @Source("icons/16x16/bullet_greengray.png")
    ImageResource disabledButConnected16();

    @Source("icons/32x32/bullet_green_and_black.png")
    ImageResource disabledButConnected32();

    @Source("icons/16x16/bullet_black.png")
    ImageResource black16();

    @Source("icons/32x32/bullet_black.png")
    ImageResource black32();

    @Source("icons/16x16/server_edit.png")
    ImageResource cloudSettings16();

    @Source("icons/32x32/server_edit.png")
    ImageResource cloudSettings32();

    @Source("icons/16x16/server_edit.png")
    ImageResource cloudBoxSettings16();

    @Source("icons/32x32/server_edit.png")
    ImageResource cloudBoxSettings32();

    @Source("icons/16x16/administrator.png")
    ImageResource administrator();

    @Source("icons/16x16/chart_organisation.png")
    ImageResource childAccounts16();

    @Source("icons/32x32/chart_organisation.png")
    ImageResource childAccounts32();

    @Source("icons/32x32/traffic_usage.png")
    ImageResource usages32();

    @Source("icons/16x16/traffic_usage.png")
    ImageResource usages16();

    @Source("icons/16x16/arrow_divide.png")
    ImageResource rules16();

    @Source("icons/32x32/arrow_divide.png")
    ImageResource rules32();

    @Source("icons/others/wheel.gif")
    ImageResource wheel();

    @Source("icons/32x32/radiolocator.png")
    ImageResource dashboard32();

    @Source("icons/16x16/radiolocator.png")
    ImageResource dashboard();

    @Source("icons/32x32/bell.png")
    ImageResource alerts32();

    @Source("icons/16x16/bell.png")
    ImageResource alerts();

    @Source("icons/32x32/router.png")
    ImageResource devices32();

    @Source("icons/16x16/router.png")
    ImageResource devices();

    @Source("icons/32x32/database.png")
    ImageResource data32();

    @Source("icons/16x16/database.png")
    ImageResource data();

    @Source("icons/32x32/folder_database.png")
    ImageResource dataByTopic32();

    @Source("icons/16x16/folder_database.png")
    ImageResource dataByTopic();

    @Source("icons/32x32/server_database.png")
    ImageResource dataByAsset32();

    @Source("icons/16x16/server_database.png")
    ImageResource dataByAsset();

    @Source("icons/32x32/directory_listing.png")
    ImageResource audit32();

    @Source("icons/16x16/directory_listing.png")
    ImageResource audit();

    @Source("icons/32x32/setting_tools.png")
    ImageResource settings32();

    @Source("icons/16x16/setting_tools.png")
    ImageResource settings();

    @Source("icons/16x16/system_monitor.png")
    ImageResource healthCheck16();

    @Source("icons/32x32/system_monitor.png")
    ImageResource healthCheck32();

    @Source("icons/32x32/update.png")
    ImageResource update32();

    @Source("icons/16x16/update.png")
    ImageResource update();

    @Source("icons/16x16/add.png")
    ImageResource add();

    @Source("icons/16x16/pencil.png")
    ImageResource edit();

    @Source("icons/16x16/delete.png")
    ImageResource delete();

    @Source("icons/16x16/delete.png")
    ImageResource delete16();

    @Source("icons/iconjar/Cloud_Down_16.png")
    ImageResource download();

    @Source("icons/16x16/export_excel.png")
    ImageResource exportExcel();

    @Source("icons/16x16/document_export.png")
    ImageResource exportCSV();

    @Source("icons/16x16/diagramm.png")
    ImageResource live();

    @Source("icons/16x16/network_adapter.png")
    ImageResource deviceProfile();

    @Source("icons/16x16/clock_history_frame.png")
    ImageResource history();

    @Source("icons/16x16/information.png")
    ImageResource info16();

    @Source("icons/32x32/information.png")
    ImageResource info32();

    @Source("icons/32x32/error.png")
    ImageResource warn32();

    @Source("icons/16x16/error.png")
    ImageResource warn16();

    @Source("icons/32x32/exclamation.png")
    ImageResource error32();

    @Source("icons/16x16/exclamation.png")
    ImageResource error16();

    @Source("icons/16x16/chart_curve.png")
    ImageResource chart();

    @Source("icons/16x16/chart_curve_edit.png")
    ImageResource chartEdit();

    @Source("icons/16x16/table.png")
    ImageResource table();

    @Source("icons/32x32/table.png")
    ImageResource table32();

    @Source("icons/32x32/group.png")
    ImageResource users32();

    @Source("icons/16x16/group.png")
    ImageResource users16();

    @Source("icons/16x16/card_chip_gold.png")
    ImageResource sim16();

    @Source("icons/16x16/three_tags.png")
    ImageResource simGroups16();

    @Source("icons/16x16/card_chip_gold.png")
    ImageResource simprovider16();

    @Source("icons/32x32/card_chip_gold.png")
    ImageResource simprovider32();

    @Source("icons/16x16/document_signature.png")
    ImageResource pki16();

    @Source("icons/32x32/document_signature.png")
    ImageResource pki32();

    @Source("icons/16x16/document_signature_refuse.png")
    ImageResource pki16Refuse();

    @Source("icons/32x32/document_signature_refuse.png")
    ImageResource pki32Refuse();

    @Source("icons/16x16/document_signature_accept.png")
    ImageResource pki16Accept();

    @Source("icons/32x32/document_signature_accept.png")
    ImageResource pki32Accept();

    @Source("icons/16x16/card_export.png")
    ImageResource simOperations();

    @Source("icons/16x16/note_go.png")
    ImageResource wakeupSMS();

    @Source("icons/16x16/note_go.png")
    ImageResource rebootSMS();

    @Source("icons/16x16/bullet_green.png")
    ImageResource enabled16();

    @Source("icons/16x16/bullet_yellow.png")
    ImageResource bad16();

    @Source("icons/16x16/bullet_red.png")
    ImageResource disabled16();

    @Source("icons/32x32/bullet_green.png")
    ImageResource enabled32();

    @Source("icons/32x32/bullet_yellow.png")
    ImageResource bad32();

    @Source("icons/32x32/bullet_red.png")
    ImageResource disabled32();

    @Source("icons/16x16/check_green.png")
    ImageResource alwaysPassed16();

    @Source("icons/16x16/check_yellow.png")
    ImageResource sometimesFailed16();

    @Source("icons/16x16/forbid_red.png")
    ImageResource alwaysFailed16();

    @Source("icons/16x16/no_data_grey.png")
    ImageResource noData16();

    @Source("icons/16x16/calendar.png")
    ImageResource calendar();

    @Source("icons/16x16/google_map.png")
    ImageResource deviceMap();

    @Source("icons/16x16/email.png")
    ImageResource email();

    @Source("icons/32x32/email.png")
    ImageResource email32();

    @Source("icons/16x16/phone.png")
    ImageResource phone();

    @Source("icons/16x16/arrow_rotate_clockwise.png")
    ImageResource reprovision();

    @Source("icons/16x16/arrow_inout.png")
    ImageResource sync();

    @Source("icons/32x32/phone.png")
    ImageResource phone32();

    @Source("icons/16x16/twilio.png")
    ImageResource twilio();

    @Source("icons/32x32/twilio.png")
    ImageResource twilio32();

    @Source("icons/16x16/twitter.png")
    ImageResource twitter();

    @Source("icons/32x32/twitter.png")
    ImageResource twitter32();

    @Source("icons/others/mqtt16.png")
    ImageResource mqtt();

    @Source("icons/others/mqtt32.png")
    ImageResource mqtt32();

    @Source("icons/16x16/world.png")
    ImageResource rest();

    @Source("icons/32x32/world.png")
    ImageResource rest32();

    @Source("icons/16x16/error.png")
    ImageResource alert();

    @Source("icons/32x32/error.png")
    ImageResource alert32();

    @Source("icons/16x16/account_balances.png")
    ImageResource accounts();

    @Source("icons/32x32/account_balances.png")
    ImageResource accounts32();

    @Source("icons/16x16/sheduled_task.png")
    ImageResource tasks();

    @Source("icons/32x32/sheduled_task.png")
    ImageResource tasks32();

    @Source("icons/16x16/change_password.png")
    ImageResource change_password();

    @Source("icons/32x32/change_password.png")
    ImageResource change_password32();

    @Source("icons/16x16/ssl_certificates.png")
    ImageResource ssl_certificates();

    @Source("icons/32x32/ssl_certificates.png")
    ImageResource ssl_certificates32();

    @Source("icons/16x16/mail_server_setting.png")
    ImageResource mail_server_setting();

    @Source("icons/32x32/mail_server_setting.png")
    ImageResource mail_server_setting32();

    @Source("icons/16x16/bluetooth.png")
    ImageResource bluetooth();

    @Source("icons/16x16/monitor.png")
    ImageResource monitorDenali();

    @Source("icons/16x16/control_repeat_blue.png")
    ImageResource redo();

    @Source("icons/16x16/arrow_refresh.png")
    ImageResource refresh();

    @Source("icons/16x16/processor.png")
    ImageResource provision();

    @Source("icons/16x16/cog.png")
    ImageResource configuration();

    @Source("icons/32x32/control_play_blue.png")
    ImageResource playEnabled();

    @Source("icons/32x32/control_play.png")
    ImageResource playDisabled();

    @Source("icons/16x16/application_osx_terminal.png")
    ImageResource terminal();

    @Source("icons/16x16/package.png")
    ImageResource package16();

    @Source("icons/16x16/package_green.png")
    ImageResource packageGreen16();

    @Source("icons/16x16/package_go.png")
    ImageResource packageGo16();

    @Source("icons/16x16/package_add.png")
    ImageResource packageAdd();

    @Source("icons/16x16/package_delete.png")
    ImageResource packageDelete();

    @Source("icons/16x16/plugin.png")
    ImageResource plugin();

    @Source("icons/16x16/disconnect.png")
    ImageResource disconnect();

    @Source("icons/16x16/weather_clouds.png")
    ImageResource cloud16();

    @Source("icons/16x16/clock.png")
    ImageResource clock16();

    @Source("icons/16x16/google_map.png")
    ImageResource gps16();

    @Source("icons/16x16/dog.png")
    ImageResource dog16();

    @Source("icons/16x16/plugin.png")
    ImageResource plugin16();

    @Source("icons/16x16/plugin.png")
    ImageResource component();

    @Source("icons/16x16/network_adapter.png")
    ImageResource network();

    @Source("icons/16x16/accept.png")
    ImageResource accept();

    @Source("icons/16x16/accept.png")
    ImageResource accept16();

    @Source("icons/32x32/accept.png")
    ImageResource accept32();

    @Source("icons/16x16/cancel.png")
    ImageResource cancel16();

    @Source("icons/16x16/upload.png")
    ImageResource settingsUpload();

    @Source("icons/32x32/upload.png")
    ImageResource settingsUpload32();

    @Source("icons/16x16/download.png")
    ImageResource settingsDownload();

    @Source("icons/32x32/download.png")
    ImageResource settingsDownload32();

    @Source("icons/16x16/script.png")
    ImageResource snapshots();

    @Source("icons/16x16/script_add.png")
    ImageResource snapshotUpload();

    @Source("icons/16x16/script_go.png")
    ImageResource snapshotRollback();

    @Source("icons/16x16/script_save.png")
    ImageResource snapshotDownload();

    @Source("icons/16x16/bricks.png")
    ImageResource bundles();

    @Source("icons/16x16/brick_add.png")
    ImageResource bundleStart();

    @Source("icons/16x16/brick_delete.png")
    ImageResource bundleStop();

    @Source("icons/16x16/database_connect.png")
    ImageResource databaseConnect();

    @Source("icons/16x16/lock.png")
    ImageResource lock16();

    @Source("icons/16x16/lock_green.png")
    ImageResource lockGreen16();

    @Source("icons/16x16/lock_break.png")
    ImageResource unlock16();

    // DM Device Management Icons
    @Source("icons/16x16/lock.png")
    ImageResource dmLock16();

    @Source("icons/16x16/lock_open.png")
    ImageResource dmUnlock16();

    @Source("icons/16x16/lock_break.png")
    ImageResource dmLockBreak16();

    // alternative dmLockBreakRed16 = lock_delete / exclamation.png
    @Source("icons/16x16/exclamation.png")
    ImageResource dmLockBreakRed16();

    @Source("icons/16x16/help.png")
    ImageResource help16();

    @Source("icons/32x32/help.png")
    ImageResource help32();

    @Source("icons/others/vpn16.png")
    ImageResource vpn();

    @Source("icons/others/vpn32.png")
    ImageResource vpn32();

    @Source("icons/others/ifttt16.png")
    ImageResource ifttt();

    @Source("icons/others/ifttt32.png")
    ImageResource ifttt32();

    @Source("icons/16x16/small_business.png")
    ImageResource smallBusiness();

    @Source("icons/32x32/small_business.png")
    ImageResource smallBusiness32();

    @Source("icons/others/stethoscope.png")
    ImageResource diagnostics();

    @Source("icons/others/stethoscope32.png")
    ImageResource diagnostics32();

    @Source("html/documentation.html")
    TextResource documentationHtml();

    @Source("html/devices.html")
    TextResource devicesHtml();

}
