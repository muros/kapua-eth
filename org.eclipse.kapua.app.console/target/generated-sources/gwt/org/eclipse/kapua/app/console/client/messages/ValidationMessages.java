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
package org.eclipse.kapua.app.console.client.messages;

/**
 * Interface to represent the constants contained in resource bundle:
 * 	'/Users/alberto.codutti/dev/git/kapua/develop/org.eclipse.kapua.app.console/src/main/resources/org/eclipse/kapua/app/console/client/messages/ValidationMessages.properties'.
 */
public interface ValidationMessages extends com.google.gwt.i18n.client.ConstantsWithLookup {
  
  /**
   * Translated "Being Deleted".
   * 
   * @return translated "Being Deleted"
   */
  @DefaultStringValue("Being Deleted")
  @Key("BEING_DELETED")
  String BEING_DELETED();

  /**
   * Translated "Under Provisioning".
   * 
   * @return translated "Under Provisioning"
   */
  @DefaultStringValue("Under Provisioning")
  @Key("BEING_PROVISIONED")
  String BEING_PROVISIONED();

  /**
   * Translated "Cannot delete the last Administrator of an Account or remove its Administrator Role: {0}.".
   * 
   * @return translated "Cannot delete the last Administrator of an Account or remove its Administrator Role: {0}."
   */
  @DefaultStringValue("Cannot delete the last Administrator of an Account or remove its Administrator Role: {0}.")
  @Key("CANNOT_REMOVE_LAST_ADMIN")
  String CANNOT_REMOVE_LAST_ADMIN();

  /**
   * Translated "Completed".
   * 
   * @return translated "Completed"
   */
  @DefaultStringValue("Completed")
  @Key("COMPLETED")
  String COMPLETED();

  /**
   * Translated "Device Timestamp (if available)".
   * 
   * @return translated "Device Timestamp (if available)"
   */
  @DefaultStringValue("Device Timestamp (if available)")
  @Key("DEVICE_TIMESTAMP")
  String DEVICE_TIMESTAMP();

  /**
   * Translated "Disabled".
   * 
   * @return translated "Disabled"
   */
  @DefaultStringValue("Disabled")
  @Key("DISABLED")
  String DISABLED();

  /**
   * Translated "An entity with the same value for field {0} already exists.".
   * 
   * @return translated "An entity with the same value for field {0} already exists."
   */
  @DefaultStringValue("An entity with the same value for field {0} already exists.")
  @Key("DUPLICATE_NAME")
  String DUPLICATE_NAME();

  /**
   * Translated "Enabled".
   * 
   * @return translated "Enabled"
   */
  @DefaultStringValue("Enabled")
  @Key("ENABLED")
  String ENABLED();

  /**
   * Translated "The current subject is not authorized to perform this operation.".
   * 
   * @return translated "The current subject is not authorized to perform this operation."
   */
  @DefaultStringValue("The current subject is not authorized to perform this operation.")
  @Key("ILLEGAL_ACCESS")
  String ILLEGAL_ACCESS();

  /**
   * Translated "An illegal value was provided for the argument {0}.".
   * 
   * @return translated "An illegal value was provided for the argument {0}."
   */
  @DefaultStringValue("An illegal value was provided for the argument {0}.")
  @Key("ILLEGAL_ARGUMENT")
  String ILLEGAL_ARGUMENT();

  /**
   * Translated "An illegal null value was provided for the argument {0}.".
   * 
   * @return translated "An illegal null value was provided for the argument {0}."
   */
  @DefaultStringValue("An illegal null value was provided for the argument {0}.")
  @Key("ILLEGAL_NULL_ARGUMENT")
  String ILLEGAL_NULL_ARGUMENT();

  /**
   * Translated "{0}".
   * 
   * @return translated "{0}"
   */
  @DefaultStringValue("{0}")
  @Key("INTERNAL_ERROR")
  String INTERNAL_ERROR();

  /**
   * Translated "Rules can have a single From clause for EdcMessageEvent or for a PatternStream".
   * 
   * @return translated "Rules can have a single From clause for EdcMessageEvent or for a PatternStream"
   */
  @DefaultStringValue("Rules can have a single From clause for EdcMessageEvent or for a PatternStream")
  @Key("INVALID_RULE_FROMCLAUSE")
  String INVALID_RULE_FROMCLAUSE();

  /**
   * Translated "{0}".
   * 
   * @return translated "{0}"
   */
  @DefaultStringValue("{0}")
  @Key("INVALID_RULE_QUERY")
  String INVALID_RULE_QUERY();

  /**
   * Translated "Invalid username or password.".
   * 
   * @return translated "Invalid username or password."
   */
  @DefaultStringValue("Invalid username or password.")
  @Key("INVALID_USERNAME_PASSWORD")
  String INVALID_USERNAME_PASSWORD();

  /**
   * Translated "Attempts remaining before lock the user: {0}".
   * 
   * @return translated "Attempts remaining before lock the user: {0}"
   */
  @DefaultStringValue("Attempts remaining before lock the user: {0}")
  @Key("INVALID_USERNAME_PASSWORD_LOGIN_ATTEMPTS")
  String INVALID_USERNAME_PASSWORD_LOGIN_ATTEMPTS();

  /**
   * Translated "The user has been locked!".
   * 
   * @return translated "The user has been locked!"
   */
  @DefaultStringValue("The user has been locked!")
  @Key("INVALID_USERNAME_PASSWORD_USER_LOCKED")
  String INVALID_USERNAME_PASSWORD_USER_LOCKED();

  /**
   * Translated "In Progress".
   * 
   * @return translated "In Progress"
   */
  @DefaultStringValue("In Progress")
  @Key("IN_PROGRESS")
  String IN_PROGRESS();

  /**
   * Translated "Locked".
   * 
   * @return translated "Locked"
   */
  @DefaultStringValue("Locked")
  @Key("LOCKED")
  String LOCKED();

  /**
   * Translated "Your user has been locked. Contact your account administrator to unlock.".
   * 
   * @return translated "Your user has been locked. Contact your account administrator to unlock."
   */
  @DefaultStringValue("Your user has been locked. Contact your account administrator to unlock.")
  @Key("LOCKED_USER")
  String LOCKED_USER();

  /**
   * Translated "None".
   * 
   * @return translated "None"
   */
  @DefaultStringValue("None")
  @Key("NONE")
  String NONE();

  /**
   * Translated "The maximum number of rules allowed for this account has been reached.".
   * 
   * @return translated "The maximum number of rules allowed for this account has been reached."
   */
  @DefaultStringValue("The maximum number of rules allowed for this account has been reached.")
  @Key("OVER_RULE_LIMIT")
  String OVER_RULE_LIMIT();

  /**
   * Translated "Server Timestamp".
   * 
   * @return translated "Server Timestamp"
   */
  @DefaultStringValue("Server Timestamp")
  @Key("SERVER_TIMESTAMP")
  String SERVER_TIMESTAMP();

  /**
   * Translated "Timestamp".
   * 
   * @return translated "Timestamp"
   */
  @DefaultStringValue("Timestamp")
  @Key("TIMESTAMP")
  String TIMESTAMP();

  /**
   * Translated "You are not logged in.".
   * 
   * @return translated "You are not logged in."
   */
  @DefaultStringValue("You are not logged in.")
  @Key("UNAUTHENTICATED")
  String UNAUTHENTICATED();

  /**
   * Translated "Value".
   * 
   * @return translated "Value"
   */
  @DefaultStringValue("Value")
  @Key("VALUE")
  String VALUE();

  /**
   * Translated "Waiting to Start ".
   * 
   * @return translated "Waiting to Start "
   */
  @DefaultStringValue("Waiting to Start ")
  @Key("WAITING_TO_START")
  String WAITING_TO_START();

  /**
   * Translated "The operation completed with a warning. {0}".
   * 
   * @return translated "The operation completed with a warning. {0}"
   */
  @DefaultStringValue("The operation completed with a warning. {0}")
  @Key("WARNING")
  String WARNING();

  /**
   * Translated "Access Token".
   * 
   * @return translated "Access Token"
   */
  @DefaultStringValue("Access Token")
  @Key("accessTokenParameterLabelKey")
  String accessTokenParameterLabelKey();

  /**
   * Translated "Access Token".
   * 
   * @return translated "Access Token"
   */
  @DefaultStringValue("Access Token")
  @Key("accessTokenParameterTooltipKey")
  String accessTokenParameterTooltipKey();

  /**
   * Translated "Access Token Secret".
   * 
   * @return translated "Access Token Secret"
   */
  @DefaultStringValue("Access Token Secret")
  @Key("accessTokenSecretParameterLabelKey")
  String accessTokenSecretParameterLabelKey();

  /**
   * Translated "Access Token Secret".
   * 
   * @return translated "Access Token Secret"
   */
  @DefaultStringValue("Access Token Secret")
  @Key("accessTokenSecretParameterTooltipKey")
  String accessTokenSecretParameterTooltipKey();

  /**
   * Translated "Created By".
   * 
   * @return translated "Created By"
   */
  @DefaultStringValue("Created By")
  @Key("accountCreatedBy")
  String accountCreatedBy();

  /**
   * Translated "Created On".
   * 
   * @return translated "Created On"
   */
  @DefaultStringValue("Created On")
  @Key("accountCreatedOn")
  String accountCreatedOn();

  /**
   * Translated "Account Information".
   * 
   * @return translated "Account Information"
   */
  @DefaultStringValue("Account Information")
  @Key("accountInfo")
  String accountInfo();

  /**
   * Translated "Modified By".
   * 
   * @return translated "Modified By"
   */
  @DefaultStringValue("Modified By")
  @Key("accountModifiedBy")
  String accountModifiedBy();

  /**
   * Translated "Modified On".
   * 
   * @return translated "Modified On"
   */
  @DefaultStringValue("Modified On")
  @Key("accountModifiedOn")
  String accountModifiedOn();

  /**
   * Translated "Account Name".
   * 
   * @return translated "Account Name"
   */
  @DefaultStringValue("Account Name")
  @Key("accountName")
  String accountName();

  /**
   * Translated "Provisioning Status".
   * 
   * @return translated "Provisioning Status"
   */
  @DefaultStringValue("Provisioning Status")
  @Key("accountProvisioningStatus")
  String accountProvisioningStatus();

  /**
   * Translated "Account Status".
   * 
   * @return translated "Account Status"
   */
  @DefaultStringValue("Account Status")
  @Key("accountStatus")
  String accountStatus();

  /**
   * Translated "Twilio Account ID".
   * 
   * @return translated "Twilio Account ID"
   */
  @DefaultStringValue("Twilio Account ID")
  @Key("accountsidParameterLabelKey")
  String accountsidParameterLabelKey();

  /**
   * Translated "The 34 character Account identifier (starting with 'AC'). This can be found on the Twilio dashboard page.".
   * 
   * @return translated "The 34 character Account identifier (starting with 'AC'). This can be found on the Twilio dashboard page."
   */
  @DefaultStringValue("The 34 character Account identifier (starting with 'AC'). This can be found on the Twilio dashboard page.")
  @Key("accountsidParameterTooltipKey")
  String accountsidParameterTooltipKey();

  /**
   * Translated "Alert".
   * 
   * @return translated "Alert"
   */
  @DefaultStringValue("Alert")
  @Key("alertRuleActionLabelKey")
  String alertRuleActionLabelKey();

  /**
   * Translated "Algorithm".
   * 
   * @return translated "Algorithm"
   */
  @DefaultStringValue("Algorithm")
  @Key("algorithm")
  String algorithm();

  /**
   * Translated "Mandatory Provisioning".
   * 
   * @return translated "Mandatory Provisioning"
   */
  @DefaultStringValue("Mandatory Provisioning")
  @Key("allowNewNotProvisionedDevices")
  String allowNewNotProvisionedDevices();

  /**
   * Translated "Any".
   * 
   * @return translated "Any"
   */
  @DefaultStringValue("Any")
  @Key("any")
  String any();

  /**
   * Translated "Attachment Content Type".
   * 
   * @return translated "Attachment Content Type"
   */
  @DefaultStringValue("Attachment Content Type")
  @Key("attachmentContentType")
  String attachmentContentType();

  /**
   * Translated "Attachment #1 Info".
   * 
   * @return translated "Attachment #1 Info"
   */
  @DefaultStringValue("Attachment #1 Info")
  @Key("attachmentInfo1")
  String attachmentInfo1();

  /**
   * Translated "Attachment #10 Info".
   * 
   * @return translated "Attachment #10 Info"
   */
  @DefaultStringValue("Attachment #10 Info")
  @Key("attachmentInfo10")
  String attachmentInfo10();

  /**
   * Translated "Attachment #2 Info".
   * 
   * @return translated "Attachment #2 Info"
   */
  @DefaultStringValue("Attachment #2 Info")
  @Key("attachmentInfo2")
  String attachmentInfo2();

  /**
   * Translated "Attachment #3 Info".
   * 
   * @return translated "Attachment #3 Info"
   */
  @DefaultStringValue("Attachment #3 Info")
  @Key("attachmentInfo3")
  String attachmentInfo3();

  /**
   * Translated "Attachment #4 Info".
   * 
   * @return translated "Attachment #4 Info"
   */
  @DefaultStringValue("Attachment #4 Info")
  @Key("attachmentInfo4")
  String attachmentInfo4();

  /**
   * Translated "Attachment #5 Info".
   * 
   * @return translated "Attachment #5 Info"
   */
  @DefaultStringValue("Attachment #5 Info")
  @Key("attachmentInfo5")
  String attachmentInfo5();

  /**
   * Translated "Attachment #6 Info".
   * 
   * @return translated "Attachment #6 Info"
   */
  @DefaultStringValue("Attachment #6 Info")
  @Key("attachmentInfo6")
  String attachmentInfo6();

  /**
   * Translated "Attachment #7 Info".
   * 
   * @return translated "Attachment #7 Info"
   */
  @DefaultStringValue("Attachment #7 Info")
  @Key("attachmentInfo7")
  String attachmentInfo7();

  /**
   * Translated "Attachment #8 Info".
   * 
   * @return translated "Attachment #8 Info"
   */
  @DefaultStringValue("Attachment #8 Info")
  @Key("attachmentInfo8")
  String attachmentInfo8();

  /**
   * Translated "Attachment #9 Info".
   * 
   * @return translated "Attachment #9 Info"
   */
  @DefaultStringValue("Attachment #9 Info")
  @Key("attachmentInfo9")
  String attachmentInfo9();

  /**
   * Translated "Attachment Name".
   * 
   * @return translated "Attachment Name"
   */
  @DefaultStringValue("Attachment Name")
  @Key("attachmentName")
  String attachmentName();

  /**
   * Translated "Attachment Size".
   * 
   * @return translated "Attachment Size"
   */
  @DefaultStringValue("Attachment Size")
  @Key("attachmentSize")
  String attachmentSize();

  /**
   * Translated "Twilio Auth Token".
   * 
   * @return translated "Twilio Auth Token"
   */
  @DefaultStringValue("Twilio Auth Token")
  @Key("authtokenParameterLabelKey")
  String authtokenParameterLabelKey();

  /**
   * Translated "the 32 character AuthToken. This can be found on the Twilio dashboard page.".
   * 
   * @return translated "the 32 character AuthToken. This can be found on the Twilio dashboard page."
   */
  @DefaultStringValue("the 32 character AuthToken. This can be found on the Twilio dashboard page.")
  @Key("authtokenParameterTooltipKey")
  String authtokenParameterTooltipKey();

  /**
   * Translated "Active".
   * 
   * @return translated "Active"
   */
  @DefaultStringValue("Active")
  @Key("bndActive")
  String bndActive();

  /**
   * Translated "Installed".
   * 
   * @return translated "Installed"
   */
  @DefaultStringValue("Installed")
  @Key("bndInstalled")
  String bndInstalled();

  /**
   * Translated "Resolved".
   * 
   * @return translated "Resolved"
   */
  @DefaultStringValue("Resolved")
  @Key("bndResolved")
  String bndResolved();

  /**
   * Translated "Starting".
   * 
   * @return translated "Starting"
   */
  @DefaultStringValue("Starting")
  @Key("bndStarting")
  String bndStarting();

  /**
   * Translated "Stopping".
   * 
   * @return translated "Stopping"
   */
  @DefaultStringValue("Stopping")
  @Key("bndStopping")
  String bndStopping();

  /**
   * Translated "Uninstalled".
   * 
   * @return translated "Uninstalled"
   */
  @DefaultStringValue("Uninstalled")
  @Key("bndUninstalled")
  String bndUninstalled();

  /**
   * Translated "Unknown".
   * 
   * @return translated "Unknown"
   */
  @DefaultStringValue("Unknown")
  @Key("bndUnknown")
  String bndUnknown();

  /**
   * Translated "Body".
   * 
   * @return translated "Body"
   */
  @DefaultStringValue("Body")
  @Key("bodyParameterLabelKey")
  String bodyParameterLabelKey();

  /**
   * Translated "Email Body".
   * 
   * @return translated "Email Body"
   */
  @DefaultStringValue("Email Body")
  @Key("bodyParameterTooltipKey")
  String bodyParameterTooltipKey();

  /**
   * Translated "Alternative body".
   * 
   * @return translated "Alternative body"
   */
  @DefaultStringValue("Alternative body")
  @Key("bodyTextOnlyParameterLabelKey")
  String bodyTextOnlyParameterLabelKey();

  /**
   * Translated "Alternative email body if the recipient client doesn't support the HTML format.".
   * 
   * @return translated "Alternative email body if the recipient client doesn't support the HTML format."
   */
  @DefaultStringValue("Alternative email body if the recipient client doesn't support the HTML format.")
  @Key("bodyTextOnlyParameterTooltipKey")
  String bodyTextOnlyParameterTooltipKey();

  /**
   * Translated "Compute Optimized 2-Extra Large (c3.2xlarge)".
   * 
   * @return translated "Compute Optimized 2-Extra Large (c3.2xlarge)"
   */
  @DefaultStringValue("Compute Optimized 2-Extra Large (c3.2xlarge)")
  @Key("c3_2xlarge")
  String c3_2xlarge();

  /**
   * Translated "Compute Optimized 4-Extra Large (c3.4xlarge)".
   * 
   * @return translated "Compute Optimized 4-Extra Large (c3.4xlarge)"
   */
  @DefaultStringValue("Compute Optimized 4-Extra Large (c3.4xlarge)")
  @Key("c3_4xlarge")
  String c3_4xlarge();

  /**
   * Translated "Compute Optimized 8-Extra Large (c3.8xlarge)".
   * 
   * @return translated "Compute Optimized 8-Extra Large (c3.8xlarge)"
   */
  @DefaultStringValue("Compute Optimized 8-Extra Large (c3.8xlarge)")
  @Key("c3_8xlarge")
  String c3_8xlarge();

  /**
   * Translated "Compute Optimized Large (c3.large)".
   * 
   * @return translated "Compute Optimized Large (c3.large)"
   */
  @DefaultStringValue("Compute Optimized Large (c3.large)")
  @Key("c3_large")
  String c3_large();

  /**
   * Translated "Compute Optimized Extra Large (c3.xlarge)".
   * 
   * @return translated "Compute Optimized Extra Large (c3.xlarge)"
   */
  @DefaultStringValue("Compute Optimized Extra Large (c3.xlarge)")
  @Key("c3_xlarge")
  String c3_xlarge();

  /**
   * Translated "Certification Authority Id".
   * 
   * @return translated "Certification Authority Id"
   */
  @DefaultStringValue("Certification Authority Id")
  @Key("caId")
  String caId();

  /**
   * Translated "Certification Authority Name".
   * 
   * @return translated "Certification Authority Name"
   */
  @DefaultStringValue("Certification Authority Name")
  @Key("caName")
  String caName();

  /**
   * Translated "Category".
   * 
   * @return translated "Category"
   */
  @DefaultStringValue("Category")
  @Key("categoryParameterLabelKey")
  String categoryParameterLabelKey();

  /**
   * Translated "Could be 'Performance', 'Security', etc ...".
   * 
   * @return translated "Could be 'Performance', 'Security', etc ..."
   */
  @DefaultStringValue("Could be 'Performance', 'Security', etc ...")
  @Key("categoryParameterTooltipKey")
  String categoryParameterTooltipKey();

  /**
   * Translated "Certificate".
   * 
   * @return translated "Certificate"
   */
  @DefaultStringValue("Certificate")
  @Key("certificate")
  String certificate();

  /**
   * Translated "Choice".
   * 
   * @return translated "Choice"
   */
  @DefaultStringValue("Choice")
  @Key("choice")
  String choice();

  /**
   * Translated "Choice".
   * 
   * @return translated "Choice"
   */
  @DefaultStringValue("Choice")
  @Key("choiceParameterLabelKey")
  String choiceParameterLabelKey();

  /**
   * Translated "Choice Tooltip".
   * 
   * @return translated "Choice Tooltip"
   */
  @DefaultStringValue("Choice Tooltip")
  @Key("choiceTooltip")
  String choiceTooltip();

  /**
   * Translated "Code".
   * 
   * @return translated "Code"
   */
  @DefaultStringValue("Code")
  @Key("codeParameterLabelKey")
  String codeParameterLabelKey();

  /**
   * Translated "A code at your convenance ('VPN', 'CPU' etc...)".
   * 
   * @return translated "A code at your convenance ('VPN', 'CPU' etc...)"
   */
  @DefaultStringValue("A code at your convenance ('VPN', 'CPU' etc...)")
  @Key("codeParameterTooltipKey")
  String codeParameterTooltipKey();

  /**
   * Translated "Value must be smaller than {0}".
   * 
   * @return translated "Value must be smaller than {0}"
   */
  @DefaultStringValue("Value must be smaller than {0}")
  @Key("configMaxValue")
  String configMaxValue();

  /**
   * Translated "Value must be greater than {0}".
   * 
   * @return translated "Value must be greater than {0}"
   */
  @DefaultStringValue("Value must be greater than {0}")
  @Key("configMinValue")
  String configMinValue();

  /**
   * Translated "Connection Information".
   * 
   * @return translated "Connection Information"
   */
  @DefaultStringValue("Connection Information")
  @Key("connectionsInfo")
  String connectionsInfo();

  /**
   * Translated "Consumer Key".
   * 
   * @return translated "Consumer Key"
   */
  @DefaultStringValue("Consumer Key")
  @Key("consumerKeyParameterLabelKey")
  String consumerKeyParameterLabelKey();

  /**
   * Translated "you get your app's consumer key/secret pair after registering an application into https://dev.twitter.com/ ".
   * 
   * @return translated "you get your app's consumer key/secret pair after registering an application into https://dev.twitter.com/ "
   */
  @DefaultStringValue("you get your app's consumer key/secret pair after registering an application into https://dev.twitter.com/ ")
  @Key("consumerKeyParameterTooltipKey")
  String consumerKeyParameterTooltipKey();

  /**
   * Translated "Consumer Secret".
   * 
   * @return translated "Consumer Secret"
   */
  @DefaultStringValue("Consumer Secret")
  @Key("consumerSecretParameterLabelKey")
  String consumerSecretParameterLabelKey();

  /**
   * Translated "you get your app's consumer key/secret pair after registering an application into https://dev.twitter.com/ ".
   * 
   * @return translated "you get your app's consumer key/secret pair after registering an application into https://dev.twitter.com/ "
   */
  @DefaultStringValue("you get your app's consumer key/secret pair after registering an application into https://dev.twitter.com/ ")
  @Key("consumerSecretParameterTooltipKey")
  String consumerSecretParameterTooltipKey();

  /**
   * Translated "Created By".
   * 
   * @return translated "Created By"
   */
  @DefaultStringValue("Created By")
  @Key("createdBy")
  String createdBy();

  /**
   * Translated "Creation Date".
   * 
   * @return translated "Creation Date"
   */
  @DefaultStringValue("Creation Date")
  @Key("createdOn")
  String createdOn();

  /**
   * Translated "Data Plan Per Device (MB/m)".
   * 
   * @return translated "Data Plan Per Device (MB/m)"
   */
  @DefaultStringValue("Data Plan Per Device (MB/m)")
  @Key("dataPlanPerDevice")
  String dataPlanPerDevice();

  /**
   * Translated "Data Storage Enabled?".
   * 
   * @return translated "Data Storage Enabled?"
   */
  @DefaultStringValue("Data Storage Enabled?")
  @Key("dataStorage")
  String dataStorage();

  /**
   * Translated "Data Time to Live (days)".
   * 
   * @return translated "Data Time to Live (days)"
   */
  @DefaultStringValue("Data Time to Live (days)")
  @Key("dataTimeToLive")
  String dataTimeToLive();

  /**
   * Translated "Date".
   * 
   * @return translated "Date"
   */
  @DefaultStringValue("Date")
  @Key("date")
  String date();

  /**
   * Translated "Date".
   * 
   * @return translated "Date"
   */
  @DefaultStringValue("Date")
  @Key("dateParameterLabelKey")
  String dateParameterLabelKey();

  /**
   * Translated "This date comes before Thursday January 01 01:00:00 CET 1970".
   * 
   * @return translated "This date comes before Thursday January 01 01:00:00 CET 1970"
   */
  @DefaultStringValue("This date comes before Thursday January 01 01:00:00 CET 1970")
  @Key("dateRangeValidatorAfter1Jan1970")
  String dateRangeValidatorAfter1Jan1970();

  /**
   * Translated "The end date comes before the start date. If not set the start date is automatically set to the current date.".
   * 
   * @return translated "The end date comes before the start date. If not set the start date is automatically set to the current date."
   */
  @DefaultStringValue("The end date comes before the start date. If not set the start date is automatically set to the current date.")
  @Key("dateRangeValidatorEndDateBeforeStartDate")
  String dateRangeValidatorEndDateBeforeStartDate();

  /**
   * Translated "Date Tooltip".
   * 
   * @return translated "Date Tooltip"
   */
  @DefaultStringValue("Date Tooltip")
  @Key("dateTooltip")
  String dateTooltip();

  /**
   * Translated "Device Credential Strategy".
   * 
   * @return translated "Device Credential Strategy"
   */
  @DefaultStringValue("Device Credential Strategy")
  @Key("defaultDeviceTightCredentials")
  String defaultDeviceTightCredentials();

  /**
   * Translated "Broker Image Name".
   * 
   * @return translated "Broker Image Name"
   */
  @DefaultStringValue("Broker Image Name")
  @Key("deploymentBrokerImageName")
  String deploymentBrokerImageName();

  /**
   * Translated "Broker URL".
   * 
   * @return translated "Broker URL"
   */
  @DefaultStringValue("Broker URL")
  @Key("deploymentBrokerURL")
  String deploymentBrokerURL();

  /**
   * Translated "Deployment Info".
   * 
   * @return translated "Deployment Info"
   */
  @DefaultStringValue("Deployment Info")
  @Key("deploymentInfo")
  String deploymentInfo();

  /**
   * Translated "Vpn URL".
   * 
   * @return translated "Vpn URL"
   */
  @DefaultStringValue("Vpn URL")
  @Key("deploymentVpnURL")
  String deploymentVpnURL();

  /**
   * Translated "Accepted Payload Encoding".
   * 
   * @return translated "Accepted Payload Encoding"
   */
  @DefaultStringValue("Accepted Payload Encoding")
  @Key("devAccEnc")
  String devAccEnc();

  /**
   * Translated "Active Cloud Applications".
   * 
   * @return translated "Active Cloud Applications"
   */
  @DefaultStringValue("Active Cloud Applications")
  @Key("devApps")
  String devApps();

  /**
   * Translated "Attributes Information".
   * 
   * @return translated "Attributes Information"
   */
  @DefaultStringValue("Attributes Information")
  @Key("devAttributesInfo")
  String devAttributesInfo();

  /**
   * Translated "Available Processors".
   * 
   * @return translated "Available Processors"
   */
  @DefaultStringValue("Available Processors")
  @Key("devAvailableProcessors")
  String devAvailableProcessors();

  /**
   * Translated "BIOS Version".
   * 
   * @return translated "BIOS Version"
   */
  @DefaultStringValue("BIOS Version")
  @Key("devBiosVersion")
  String devBiosVersion();

  /**
   * Translated "Client ID".
   * 
   * @return translated "Client ID"
   */
  @DefaultStringValue("Client ID")
  @Key("devClientId")
  String devClientId();

  /**
   * Translated "Connection Status".
   * 
   * @return translated "Connection Status"
   */
  @DefaultStringValue("Connection Status")
  @Key("devConnectionStatus")
  String devConnectionStatus();

  /**
   * Translated "Custom Attribute 1".
   * 
   * @return translated "Custom Attribute 1"
   */
  @DefaultStringValue("Custom Attribute 1")
  @Key("devCustomAttribute1")
  String devCustomAttribute1();

  /**
   * Translated "Custom Attribute 2".
   * 
   * @return translated "Custom Attribute 2"
   */
  @DefaultStringValue("Custom Attribute 2")
  @Key("devCustomAttribute2")
  String devCustomAttribute2();

  /**
   * Translated "Custom Attribute 3".
   * 
   * @return translated "Custom Attribute 3"
   */
  @DefaultStringValue("Custom Attribute 3")
  @Key("devCustomAttribute3")
  String devCustomAttribute3();

  /**
   * Translated "Custom Attribute 4".
   * 
   * @return translated "Custom Attribute 4"
   */
  @DefaultStringValue("Custom Attribute 4")
  @Key("devCustomAttribute4")
  String devCustomAttribute4();

  /**
   * Translated "Custom Attribute 5".
   * 
   * @return translated "Custom Attribute 5"
   */
  @DefaultStringValue("Custom Attribute 5")
  @Key("devCustomAttribute5")
  String devCustomAttribute5();

  /**
   * Translated "Display Name".
   * 
   * @return translated "Display Name"
   */
  @DefaultStringValue("Display Name")
  @Key("devDisplayName")
  String devDisplayName();

  /**
   * Translated "KURA/ESF Version".
   * 
   * @return translated "KURA/ESF Version"
   */
  @DefaultStringValue("KURA/ESF Version")
  @Key("devEsfVersion")
  String devEsfVersion();

  /**
   * Translated "Firmware Version".
   * 
   * @return translated "Firmware Version"
   */
  @DefaultStringValue("Firmware Version")
  @Key("devFirmwareVersion")
  String devFirmwareVersion();

  /**
   * Translated "Hardware Information".
   * 
   * @return translated "Hardware Information"
   */
  @DefaultStringValue("Hardware Information")
  @Key("devHw")
  String devHw();

  /**
   * Translated "Cloud Connection Information".
   * 
   * @return translated "Cloud Connection Information"
   */
  @DefaultStringValue("Cloud Connection Information")
  @Key("devInfo")
  String devInfo();

  /**
   * Translated "Java Information".
   * 
   * @return translated "Java Information"
   */
  @DefaultStringValue("Java Information")
  @Key("devJava")
  String devJava();

  /**
   * Translated "Java Virtual Machine".
   * 
   * @return translated "Java Virtual Machine"
   */
  @DefaultStringValue("Java Virtual Machine")
  @Key("devJvmName")
  String devJvmName();

  /**
   * Translated "Java Runtime".
   * 
   * @return translated "Java Runtime"
   */
  @DefaultStringValue("Java Runtime")
  @Key("devJvmProfile")
  String devJvmProfile();

  /**
   * Translated "Java Virtual Machine Version".
   * 
   * @return translated "Java Virtual Machine Version"
   */
  @DefaultStringValue("Java Virtual Machine Version")
  @Key("devJvmVersion")
  String devJvmVersion();

  /**
   * Translated "Last Event On".
   * 
   * @return translated "Last Event On"
   */
  @DefaultStringValue("Last Event On")
  @Key("devLastEventOn")
  String devLastEventOn();

  /**
   * Translated "Last Event Type".
   * 
   * @return translated "Last Event Type"
   */
  @DefaultStringValue("Last Event Type")
  @Key("devLastEventType")
  String devLastEventType();

  /**
   * Translated "Last Successfully Connected User".
   * 
   * @return translated "Last Successfully Connected User"
   */
  @DefaultStringValue("Last Successfully Connected User")
  @Key("devLastUserUsed")
  String devLastUserUsed();

  /**
   * Translated "Model ID".
   * 
   * @return translated "Model ID"
   */
  @DefaultStringValue("Model ID")
  @Key("devModelId")
  String devModelId();

  /**
   * Translated "Model Name".
   * 
   * @return translated "Model Name"
   */
  @DefaultStringValue("Model Name")
  @Key("devModelName")
  String devModelName();

  /**
   * Translated "Number of Processors".
   * 
   * @return translated "Number of Processors"
   */
  @DefaultStringValue("Number of Processors")
  @Key("devNumProc")
  String devNumProc();

  /**
   * Translated "Operating System".
   * 
   * @return translated "Operating System"
   */
  @DefaultStringValue("Operating System")
  @Key("devOs")
  String devOs();

  /**
   * Translated "OS Architecture".
   * 
   * @return translated "OS Architecture"
   */
  @DefaultStringValue("OS Architecture")
  @Key("devOsArch")
  String devOsArch();

  /**
   * Translated "Operating System Version".
   * 
   * @return translated "Operating System Version"
   */
  @DefaultStringValue("Operating System Version")
  @Key("devOsVersion")
  String devOsVersion();

  /**
   * Translated "OSGI Framework".
   * 
   * @return translated "OSGI Framework"
   */
  @DefaultStringValue("OSGI Framework")
  @Key("devOsgiFramework")
  String devOsgiFramework();

  /**
   * Translated "OSGI Framework Version".
   * 
   * @return translated "OSGI Framework Version"
   */
  @DefaultStringValue("OSGI Framework Version")
  @Key("devOsgiFrameworkVersion")
  String devOsgiFrameworkVersion();

  /**
   * Translated "Part Number".
   * 
   * @return translated "Part Number"
   */
  @DefaultStringValue("Part Number")
  @Key("devPartNumber")
  String devPartNumber();

  /**
   * Translated "Free RAM".
   * 
   * @return translated "Free RAM"
   */
  @DefaultStringValue("Free RAM")
  @Key("devRamFree")
  String devRamFree();

  /**
   * Translated "Total RAM".
   * 
   * @return translated "Total RAM"
   */
  @DefaultStringValue("Total RAM")
  @Key("devRamTot")
  String devRamTot();

  /**
   * Translated "Device Credentials Security".
   * 
   * @return translated "Device Credentials Security"
   */
  @DefaultStringValue("Device Credentials Security")
  @Key("devSecurity")
  String devSecurity();

  /**
   * Translated "Allow Credentials Change".
   * 
   * @return translated "Allow Credentials Change"
   */
  @DefaultStringValue("Allow Credentials Change")
  @Key("devSecurityAllowCredentialsChange")
  String devSecurityAllowCredentialsChange();

  /**
   * Translated "Credentials Tight Strategy".
   * 
   * @return translated "Credentials Tight Strategy"
   */
  @DefaultStringValue("Credentials Tight Strategy")
  @Key("devSecurityCredentialsTight")
  String devSecurityCredentialsTight();

  /**
   * Translated "Serial Number".
   * 
   * @return translated "Serial Number"
   */
  @DefaultStringValue("Serial Number")
  @Key("devSerialNumber")
  String devSerialNumber();

  /**
   * Translated "Status Information".
   * 
   * @return translated "Status Information"
   */
  @DefaultStringValue("Status Information")
  @Key("devStat")
  String devStat();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultStringValue("Status")
  @Key("devStatus")
  String devStatus();

  /**
   * Translated "Software Information".
   * 
   * @return translated "Software Information"
   */
  @DefaultStringValue("Software Information")
  @Key("devSw")
  String devSw();

  /**
   * Translated "Tags".
   * 
   * @return translated "Tags"
   */
  @DefaultStringValue("Tags")
  @Key("devTagAttributes")
  String devTagAttributes();

  /**
   * Translated "Total Memory".
   * 
   * @return translated "Total Memory"
   */
  @DefaultStringValue("Total Memory")
  @Key("devTotalMemory")
  String devTotalMemory();

  /**
   * Translated "Uptime".
   * 
   * @return translated "Uptime"
   */
  @DefaultStringValue("Uptime")
  @Key("devUptime")
  String devUptime();

  /**
   * Translated "Display Name must be minimum 3 char long and not have whitespace at the beginning or at the end .".
   * 
   * @return translated "Display Name must be minimum 3 char long and not have whitespace at the beginning or at the end ."
   */
  @DefaultStringValue("Display Name must be minimum 3 char long and not have whitespace at the beginning or at the end .")
  @Key("deviceFormDisplayNameValidationMessage")
  String deviceFormDisplayNameValidationMessage();

  /**
   * Translated "Tag Name must be 3 to 255 char long and could include chars, numbers and - _ @ # ! $ % ^ & * + = ? < > ".
   * 
   * @return translated "Tag Name must be 3 to 255 char long and could include chars, numbers and - _ @ # ! $ % ^ & * + = ? < > "
   */
  @DefaultStringValue("Tag Name must be 3 to 255 char long and could include chars, numbers and - _ @ # ! $ % ^ & * + = ? < > ")
  @Key("deviceFormTagNameValidationMessage")
  String deviceFormTagNameValidationMessage();

  /**
   * Translated "Device ID".
   * 
   * @return translated "Device ID"
   */
  @DefaultStringValue("Device ID")
  @Key("deviceId")
  String deviceId();

  /**
   * Translated "This value cannot be equals or lower of the current job retries.".
   * 
   * @return translated "This value cannot be equals or lower of the current job retries."
   */
  @DefaultStringValue("This value cannot be equals or lower of the current job retries.")
  @Key("deviceJobFormMaxRetriesMin")
  String deviceJobFormMaxRetriesMin();

  /**
   * Translated "Please select at least one tag or one client id as target of this job.".
   * 
   * @return translated "Please select at least one tag or one client id as target of this job."
   */
  @DefaultStringValue("Please select at least one tag or one client id as target of this job.")
  @Key("deviceJobFormTarget")
  String deviceJobFormTarget();

  /**
   * Translated "Accept-Encoding".
   * 
   * @return translated "Accept-Encoding"
   */
  @DefaultStringValue("Accept-Encoding")
  @Key("deviceProfileFormAcceptEncoding")
  String deviceProfileFormAcceptEncoding();

  /**
   * Translated "Account ID".
   * 
   * @return translated "Account ID"
   */
  @DefaultStringValue("Account ID")
  @Key("deviceProfileFormAccountId")
  String deviceProfileFormAccountId();

  /**
   * Translated "Address".
   * 
   * @return translated "Address"
   */
  @DefaultStringValue("Address")
  @Key("deviceProfileFormAddress")
  String deviceProfileFormAddress();

  /**
   * Translated "Application Identifiers".
   * 
   * @return translated "Application Identifiers"
   */
  @DefaultStringValue("Application Identifiers")
  @Key("deviceProfileFormApplicationIdentifiers")
  String deviceProfileFormApplicationIdentifiers();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultStringValue("Status")
  @Key("deviceProfileFormGwtDeviceStatus")
  String deviceProfileFormGwtDeviceStatus();

  /**
   * Translated "Last Event Date".
   * 
   * @return translated "Last Event Date"
   */
  @DefaultStringValue("Last Event Date")
  @Key("deviceProfileFormLastEventDate")
  String deviceProfileFormLastEventDate();

  /**
   * Translated "Last Event Message".
   * 
   * @return translated "Last Event Message"
   */
  @DefaultStringValue("Last Event Message")
  @Key("deviceProfileFormLastEventMessage")
  String deviceProfileFormLastEventMessage();

  /**
   * Translated "Last Event Type".
   * 
   * @return translated "Last Event Type"
   */
  @DefaultStringValue("Last Event Type")
  @Key("deviceProfileFormLastEventType")
  String deviceProfileFormLastEventType();

  /**
   * Translated "Device Connection Options".
   * 
   * @return translated "Device Connection Options"
   */
  @DefaultStringValue("Device Connection Options")
  @Key("deviceSecurityInfo")
  String deviceSecurityInfo();

  /**
   * Translated "Name must be at least 3 characters and can contain alphanumeric characters combined with dash.".
   * 
   * @return translated "Name must be at least 3 characters and can contain alphanumeric characters combined with dash."
   */
  @DefaultStringValue("Name must be at least 3 characters and can contain alphanumeric characters combined with dash.")
  @Key("device_client_idRegexMsg")
  String device_client_idRegexMsg();

  /**
   * Translated "Device Client ID is required".
   * 
   * @return translated "Device Client ID is required"
   */
  @DefaultStringValue("Device Client ID is required")
  @Key("device_client_idRequiredMsg")
  String device_client_idRequiredMsg();

  /**
   * Translated "Must be at least 3 characters and can contain alphanumeric characters combined with dash.".
   * 
   * @return translated "Must be at least 3 characters and can contain alphanumeric characters combined with dash."
   */
  @DefaultStringValue("Must be at least 3 characters and can contain alphanumeric characters combined with dash.")
  @Key("device_client_idToolTipMsg")
  String device_client_idToolTipMsg();

  /**
   * Translated "Double".
   * 
   * @return translated "Double"
   */
  @DefaultStringValue("Double")
  @Key("doubleParameterLabelKey")
  String doubleParameterLabelKey();

  /**
   * Translated "This value is already taken. Please specify another value. ".
   * 
   * @return translated "This value is already taken. Please specify another value. "
   */
  @DefaultStringValue("This value is already taken. Please specify another value. ")
  @Key("duplicateValue")
  String duplicateValue();

  /**
   * Translated "Invalid provided value. Must be a number or '0 - Disabled'.".
   * 
   * @return translated "Invalid provided value. Must be a number or '0 - Disabled'."
   */
  @DefaultStringValue("Invalid provided value. Must be a number or '0 - Disabled'.")
  @Key("editableComboRegexMsg")
  String editableComboRegexMsg();

  /**
   * Translated "Body Format".
   * 
   * @return translated "Body Format"
   */
  @DefaultStringValue("Body Format")
  @Key("emailFormatParameterLabelKey")
  String emailFormatParameterLabelKey();

  /**
   * Translated "The format of the mail body".
   * 
   * @return translated "The format of the mail body"
   */
  @DefaultStringValue("The format of the mail body")
  @Key("emailFormatParameterTooltipKey")
  String emailFormatParameterTooltipKey();

  /**
   * Translated "Email should be in the format of username@domain.com.".
   * 
   * @return translated "Email should be in the format of username@domain.com."
   */
  @DefaultStringValue("Email should be in the format of username@domain.com.")
  @Key("emailRegexMsg")
  String emailRegexMsg();

  /**
   * Translated "Rule Action to send an Email message to IFTTT service.".
   * 
   * @return translated "Rule Action to send an Email message to IFTTT service."
   */
  @DefaultStringValue("Rule Action to send an Email message to IFTTT service.")
  @Key("emailRuleActionDescriptionKey")
  String emailRuleActionDescriptionKey();

  /**
   * Translated "ifttt".
   * 
   * @return translated "ifttt"
   */
  @DefaultStringValue("ifttt")
  @Key("emailRuleActionIconUrl")
  String emailRuleActionIconUrl();

  /**
   * Translated "Email".
   * 
   * @return translated "Email"
   */
  @DefaultStringValue("Email")
  @Key("emailRuleActionLabelKey")
  String emailRuleActionLabelKey();

  /**
   * Translated "Expiration Date".
   * 
   * @return translated "Expiration Date"
   */
  @DefaultStringValue("Expiration Date")
  @Key("expirationDate")
  String expirationDate();

  /**
   * Translated "Exponent".
   * 
   * @return translated "Exponent"
   */
  @DefaultStringValue("Exponent")
  @Key("exponent")
  String exponent();

  /**
   * Translated "Float".
   * 
   * @return translated "Float"
   */
  @DefaultStringValue("Float")
  @Key("floatParameterLabelKey")
  String floatParameterLabelKey();

  /**
   * Translated "Format".
   * 
   * @return translated "Format"
   */
  @DefaultStringValue("Format")
  @Key("format")
  String format();

  /**
   * Translated "Post body content type".
   * 
   * @return translated "Post body content type"
   */
  @DefaultStringValue("Post body content type")
  @Key("formatParameterLabelKey")
  String formatParameterLabelKey();

  /**
   * Translated "Data format used to send the message to the rest endpoint".
   * 
   * @return translated "Data format used to send the message to the rest endpoint"
   */
  @DefaultStringValue("Data format used to send the message to the rest endpoint")
  @Key("formatParameterTooltipKey")
  String formatParameterTooltipKey();

  /**
   * Translated "Post body content".
   * 
   * @return translated "Post body content"
   */
  @DefaultStringValue("Post body content")
  @Key("forwardParameterLabelKey")
  String forwardParameterLabelKey();

  /**
   * Translated "Choose \"rule triggering message\" if you want to forward the original message. NOTE: Choosing rule triggering message all the fields in Custom message metrics box will be ignored.".
   * 
   * @return translated "Choose \"rule triggering message\" if you want to forward the original message. NOTE: Choosing rule triggering message all the fields in Custom message metrics box will be ignored."
   */
  @DefaultStringValue("Choose \"rule triggering message\" if you want to forward the original message. NOTE: Choosing rule triggering message all the fields in Custom message metrics box will be ignored.")
  @Key("forwardParameterTooltipKey")
  String forwardParameterTooltipKey();

  /**
   * Translated "Twilio Phone Number".
   * 
   * @return translated "Twilio Phone Number"
   */
  @DefaultStringValue("Twilio Phone Number")
  @Key("fromParameterLabelKey")
  String fromParameterLabelKey();

  /**
   * Translated "A Twilio phone number must be bought before starting sending SMS.".
   * 
   * @return translated "A Twilio phone number must be bought before starting sending SMS."
   */
  @DefaultStringValue("A Twilio phone number must be bought before starting sending SMS.")
  @Key("fromParameterTooltipKey")
  String fromParameterTooltipKey();

  /**
   * Translated "Address".
   * 
   * @return translated "Address"
   */
  @DefaultStringValue("Address")
  @Key("gpsAddress")
  String gpsAddress();

  /**
   * Translated "Altitude".
   * 
   * @return translated "Altitude"
   */
  @DefaultStringValue("Altitude")
  @Key("gpsAlt")
  String gpsAlt();

  /**
   * Translated "GPS Information".
   * 
   * @return translated "GPS Information"
   */
  @DefaultStringValue("GPS Information")
  @Key("gpsInfo")
  String gpsInfo();

  /**
   * Translated "Latitude".
   * 
   * @return translated "Latitude"
   */
  @DefaultStringValue("Latitude")
  @Key("gpsLat")
  String gpsLat();

  /**
   * Translated "Longitude".
   * 
   * @return translated "Longitude"
   */
  @DefaultStringValue("Longitude")
  @Key("gpsLong")
  String gpsLong();

  /**
   * Translated "Timestamp".
   * 
   * @return translated "Timestamp"
   */
  @DefaultStringValue("Timestamp")
  @Key("gpsTimestamp")
  String gpsTimestamp();

  /**
   * Translated "Health Check Interval (mins)".
   * 
   * @return translated "Health Check Interval (mins)"
   */
  @DefaultStringValue("Health Check Interval (mins)")
  @Key("healthCheckInterval")
  String healthCheckInterval();

  /**
   * Translated "IFTTT ".
   * 
   * @return translated "IFTTT "
   */
  @DefaultStringValue("IFTTT ")
  @Key("iftttemailRuleActionLabelKey")
  String iftttemailRuleActionLabelKey();

  /**
   * Translated "IFTTT Account Email".
   * 
   * @return translated "IFTTT Account Email"
   */
  @DefaultStringValue("IFTTT Account Email")
  @Key("iftttfromParameterLabelKey")
  String iftttfromParameterLabelKey();

  /**
   * Translated "Email of the IFTTT account holder (This account must be a Gmail account as smtp used is Gmail smtp).".
   * 
   * @return translated "Email of the IFTTT account holder (This account must be a Gmail account as smtp used is Gmail smtp)."
   */
  @DefaultStringValue("Email of the IFTTT account holder (This account must be a Gmail account as smtp used is Gmail smtp).")
  @Key("iftttfromParameterTooltipKey")
  String iftttfromParameterTooltipKey();

  /**
   * Translated "IFTTT Email Password".
   * 
   * @return translated "IFTTT Email Password"
   */
  @DefaultStringValue("IFTTT Email Password")
  @Key("iftttpasswordParameterLabelKey")
  String iftttpasswordParameterLabelKey();

  /**
   * Translated "Enter the password for the email of the IFTTT account holder.".
   * 
   * @return translated "Enter the password for the email of the IFTTT account holder."
   */
  @DefaultStringValue("Enter the password for the email of the IFTTT account holder.")
  @Key("iftttpasswordParameterTooltipKey")
  String iftttpasswordParameterTooltipKey();

  /**
   * Translated "SMTP port".
   * 
   * @return translated "SMTP port"
   */
  @DefaultStringValue("SMTP port")
  @Key("iftttsmtpportParameterLabelKey")
  String iftttsmtpportParameterLabelKey();

  /**
   * Translated "Port of this SMTP server (should be 25, 465 (ssl), or 587(tls))".
   * 
   * @return translated "Port of this SMTP server (should be 25, 465 (ssl), or 587(tls))"
   */
  @DefaultStringValue("Port of this SMTP server (should be 25, 465 (ssl), or 587(tls))")
  @Key("iftttsmtpportParameterTooltipKey")
  String iftttsmtpportParameterTooltipKey();

  /**
   * Translated "SMTP server".
   * 
   * @return translated "SMTP server"
   */
  @DefaultStringValue("SMTP server")
  @Key("iftttsmtpserverParameterLabelKey")
  String iftttsmtpserverParameterLabelKey();

  /**
   * Translated "Address of the SMTP server to use for the email account associated with the IFTTT account".
   * 
   * @return translated "Address of the SMTP server to use for the email account associated with the IFTTT account"
   */
  @DefaultStringValue("Address of the SMTP server to use for the email account associated with the IFTTT account")
  @Key("iftttsmtpserverParameterTooltipKey")
  String iftttsmtpserverParameterTooltipKey();

  /**
   * Translated "Index Metrics By".
   * 
   * @return translated "Index Metrics By"
   */
  @DefaultStringValue("Index Metrics By")
  @Key("indexMetricsBy")
  String indexMetricsBy();

  /**
   * Translated "Index Received Data By".
   * 
   * @return translated "Index Received Data By"
   */
  @DefaultStringValue("Index Received Data By")
  @Key("indexReceivedDataBy")
  String indexReceivedDataBy();

  /**
   * Translated "Integer".
   * 
   * @return translated "Integer"
   */
  @DefaultStringValue("Integer")
  @Key("intParameterLabelKey")
  String intParameterLabelKey();

  /**
   * Translated "This value cannot be null. ".
   * 
   * @return translated "This value cannot be null. "
   */
  @DefaultStringValue("This value cannot be null. ")
  @Key("invalidNullValue")
  String invalidNullValue();

  /**
   * Translated "Is Certification Authority".
   * 
   * @return translated "Is Certification Authority"
   */
  @DefaultStringValue("Is Certification Authority")
  @Key("isCa")
  String isCa();

  /**
   * Translated "Is Default".
   * 
   * @return translated "Is Default"
   */
  @DefaultStringValue("Is Default")
  @Key("isDefault")
  String isDefault();

  /**
   * Translated "Issuer".
   * 
   * @return translated "Issuer"
   */
  @DefaultStringValue("Issuer")
  @Key("issuer")
  String issuer();

  /**
   * Translated "Country Name".
   * 
   * @return translated "Country Name"
   */
  @DefaultStringValue("Country Name")
  @Key("issuerC")
  String issuerC();

  /**
   * Translated "Subject Name".
   * 
   * @return translated "Subject Name"
   */
  @DefaultStringValue("Subject Name")
  @Key("issuerCN")
  String issuerCN();

  /**
   * Translated "Locality Name".
   * 
   * @return translated "Locality Name"
   */
  @DefaultStringValue("Locality Name")
  @Key("issuerL")
  String issuerL();

  /**
   * Translated "Organization Name".
   * 
   * @return translated "Organization Name"
   */
  @DefaultStringValue("Organization Name")
  @Key("issuerO")
  String issuerO();

  /**
   * Translated "Organizational Unit Name".
   * 
   * @return translated "Organizational Unit Name"
   */
  @DefaultStringValue("Organizational Unit Name")
  @Key("issuerOrganizationalUnitName")
  String issuerOrganizationalUnitName();

  /**
   * Translated "State or Province Name".
   * 
   * @return translated "State or Province Name"
   */
  @DefaultStringValue("State or Province Name")
  @Key("issuerST")
  String issuerST();

  /**
   * Translated "Large Text".
   * 
   * @return translated "Large Text"
   */
  @DefaultStringValue("Large Text")
  @Key("largeText")
  String largeText();

  /**
   * Translated "Large Text".
   * 
   * @return translated "Large Text"
   */
  @DefaultStringValue("Large Text")
  @Key("largeTextParameterLabelKey")
  String largeTextParameterLabelKey();

  /**
   * Translated "Large Text Tooltip".
   * 
   * @return translated "Large Text Tooltip"
   */
  @DefaultStringValue("Large Text Tooltip")
  @Key("largeTextTooltip")
  String largeTextTooltip();

  /**
   * Translated "Cannot delete the last Administrator of an Account or remove its Administrator Role.".
   * 
   * @return translated "Cannot delete the last Administrator of an Account or remove its Administrator Role."
   */
  @DefaultStringValue("Cannot delete the last Administrator of an Account or remove its Administrator Role.")
  @Key("lastAdministrator")
  String lastAdministrator();

  /**
   * Translated "Login Attempts Reset After".
   * 
   * @return translated "Login Attempts Reset After"
   */
  @DefaultStringValue("Login Attempts Reset After")
  @Key("lockoutAttemptsResetAfter")
  String lockoutAttemptsResetAfter();

  /**
   * Translated "Enabled?".
   * 
   * @return translated "Enabled?"
   */
  @DefaultStringValue("Enabled?")
  @Key("lockoutEnabled")
  String lockoutEnabled();

  /**
   * Translated "Lockout Policy Info".
   * 
   * @return translated "Lockout Policy Info"
   */
  @DefaultStringValue("Lockout Policy Info")
  @Key("lockoutInfo")
  String lockoutInfo();

  /**
   * Translated "Lockout Duration".
   * 
   * @return translated "Lockout Duration"
   */
  @DefaultStringValue("Lockout Duration")
  @Key("lockoutLockDuration")
  String lockoutLockDuration();

  /**
   * Translated "Max Number Of Login Attempts".
   * 
   * @return translated "Max Number Of Login Attempts"
   */
  @DefaultStringValue("Max Number Of Login Attempts")
  @Key("lockoutMaxNumberOfFailures")
  String lockoutMaxNumberOfFailures();

  /**
   * Translated "Long".
   * 
   * @return translated "Long"
   */
  @DefaultStringValue("Long")
  @Key("longParameterLabelKey")
  String longParameterLabelKey();

  /**
   * Translated "Large (m1.large)".
   * 
   * @return translated "Large (m1.large)"
   */
  @DefaultStringValue("Large (m1.large)")
  @Key("m1_large")
  String m1_large();

  /**
   * Translated "Medium (m1.medium)".
   * 
   * @return translated "Medium (m1.medium)"
   */
  @DefaultStringValue("Medium (m1.medium)")
  @Key("m1_medium")
  String m1_medium();

  /**
   * Translated "Small (m1.small)".
   * 
   * @return translated "Small (m1.small)"
   */
  @DefaultStringValue("Small (m1.small)")
  @Key("m1_small")
  String m1_small();

  /**
   * Translated "Extra Large (m1.xlarge)".
   * 
   * @return translated "Extra Large (m1.xlarge)"
   */
  @DefaultStringValue("Extra Large (m1.xlarge)")
  @Key("m1_xlarge")
  String m1_xlarge();

  /**
   * Translated "2-Extra Large with SSD Storage (m3.2xlarge)".
   * 
   * @return translated "2-Extra Large with SSD Storage (m3.2xlarge)"
   */
  @DefaultStringValue("2-Extra Large with SSD Storage (m3.2xlarge)")
  @Key("m3_2xlarge")
  String m3_2xlarge();

  /**
   * Translated "Large with SSD Storage (m3.large)".
   * 
   * @return translated "Large with SSD Storage (m3.large)"
   */
  @DefaultStringValue("Large with SSD Storage (m3.large)")
  @Key("m3_large")
  String m3_large();

  /**
   * Translated "Medium with SSD Storage (m3.medium)".
   * 
   * @return translated "Medium with SSD Storage (m3.medium)"
   */
  @DefaultStringValue("Medium with SSD Storage (m3.medium)")
  @Key("m3_medium")
  String m3_medium();

  /**
   * Translated "Extra Large with SSD Storage (m3.xlarge)".
   * 
   * @return translated "Extra Large with SSD Storage (m3.xlarge)"
   */
  @DefaultStringValue("Extra Large with SSD Storage (m3.xlarge)")
  @Key("m3_xlarge")
  String m3_xlarge();

  /**
   * Translated "Message".
   * 
   * @return translated "Message"
   */
  @DefaultStringValue("Message")
  @Key("messageParameterLabelKey")
  String messageParameterLabelKey();

  /**
   * Translated "A message explaining the Alert.".
   * 
   * @return translated "A message explaining the Alert."
   */
  @DefaultStringValue("A message explaining the Alert.")
  @Key("messageParameterTooltipKey")
  String messageParameterTooltipKey();

  /**
   * Translated "Method".
   * 
   * @return translated "Method"
   */
  @DefaultStringValue("Method")
  @Key("methodParameterLabelKey")
  String methodParameterLabelKey();

  /**
   * Translated "The HTTP method to be used to perform the REST call. If GET is selected, the metrics parameter is ignored and only the query parameters specified in the URL will be sent. If POST is selected, depending on the \"post body content\" field selection, the rule triggering message will be forwarded or the metrics parameters will be used to compose an EdcMessage and it will be sent as the body of the request.  ".
   * 
   * @return translated "The HTTP method to be used to perform the REST call. If GET is selected, the metrics parameter is ignored and only the query parameters specified in the URL will be sent. If POST is selected, depending on the \"post body content\" field selection, the rule triggering message will be forwarded or the metrics parameters will be used to compose an EdcMessage and it will be sent as the body of the request.  "
   */
  @DefaultStringValue("The HTTP method to be used to perform the REST call. If GET is selected, the metrics parameter is ignored and only the query parameters specified in the URL will be sent. If POST is selected, depending on the \"post body content\" field selection, the rule triggering message will be forwarded or the metrics parameters will be used to compose an EdcMessage and it will be sent as the body of the request.  ")
  @Key("methodParameterTooltipKey")
  String methodParameterTooltipKey();

  /**
   * Translated "Name".
   * 
   * @return translated "Name"
   */
  @DefaultStringValue("Name")
  @Key("metricsNameParameterLabelKey")
  String metricsNameParameterLabelKey();

  /**
   * Translated "Custom message metrics".
   * 
   * @return translated "Custom message metrics"
   */
  @DefaultStringValue("Custom message metrics")
  @Key("metricsParameterLabelKey")
  String metricsParameterLabelKey();

  /**
   * Translated "Type".
   * 
   * @return translated "Type"
   */
  @DefaultStringValue("Type")
  @Key("metricsTypeParameterLabelKey")
  String metricsTypeParameterLabelKey();

  /**
   * Translated "Value".
   * 
   * @return translated "Value"
   */
  @DefaultStringValue("Value")
  @Key("metricsValueParameterLabelKey")
  String metricsValueParameterLabelKey();

  /**
   * Translated "ICCID".
   * 
   * @return translated "ICCID"
   */
  @DefaultStringValue("ICCID")
  @Key("modemIccid")
  String modemIccid();

  /**
   * Translated "IMEI".
   * 
   * @return translated "IMEI"
   */
  @DefaultStringValue("IMEI")
  @Key("modemImei")
  String modemImei();

  /**
   * Translated "IMSI".
   * 
   * @return translated "IMSI"
   */
  @DefaultStringValue("IMSI")
  @Key("modemImsi")
  String modemImsi();

  /**
   * Translated "Modem Information".
   * 
   * @return translated "Modem Information"
   */
  @DefaultStringValue("Modem Information")
  @Key("modemInfo")
  String modemInfo();

  /**
   * Translated "Updated By".
   * 
   * @return translated "Updated By"
   */
  @DefaultStringValue("Updated By")
  @Key("modifiedBy")
  String modifiedBy();

  /**
   * Translated "Update Date".
   * 
   * @return translated "Update Date"
   */
  @DefaultStringValue("Update Date")
  @Key("modifiedOn")
  String modifiedOn();

  /**
   * Translated "Modulus".
   * 
   * @return translated "Modulus"
   */
  @DefaultStringValue("Modulus")
  @Key("modulus")
  String modulus();

  /**
   * Translated "MQTT".
   * 
   * @return translated "MQTT"
   */
  @DefaultStringValue("MQTT")
  @Key("mqttRuleActionLabelKey")
  String mqttRuleActionLabelKey();

  /**
   * Translated "Health Check Mqtt Threshold (ms)".
   * 
   * @return translated "Health Check Mqtt Threshold (ms)"
   */
  @DefaultStringValue("Health Check Mqtt Threshold (ms)")
  @Key("mqttThreshold")
  String mqttThreshold();

  /**
   * Translated "Mutual Authentication SSL".
   * 
   * @return translated "Mutual Authentication SSL"
   */
  @DefaultStringValue("Mutual Authentication SSL")
  @Key("mutualSslConnection")
  String mutualSslConnection();

  /**
   * Translated "Name".
   * 
   * @return translated "Name"
   */
  @DefaultStringValue("Name")
  @Key("name")
  String name();

  /**
   * Translated "Name must be at least 3 characters and can contain alphanumeric characters combined with dash and/or underscore.".
   * 
   * @return translated "Name must be at least 3 characters and can contain alphanumeric characters combined with dash and/or underscore."
   */
  @DefaultStringValue("Name must be at least 3 characters and can contain alphanumeric characters combined with dash and/or underscore.")
  @Key("nameRegexMsg")
  String nameRegexMsg();

  /**
   * Translated "Name is required.".
   * 
   * @return translated "Name is required."
   */
  @DefaultStringValue("Name is required.")
  @Key("nameRequiredMsg")
  String nameRequiredMsg();

  /**
   * Translated "Must be at least 3 characters and can contain alphanumeric characters combined with dash and/or underscore.".
   * 
   * @return translated "Must be at least 3 characters and can contain alphanumeric characters combined with dash and/or underscore."
   */
  @DefaultStringValue("Must be at least 3 characters and can contain alphanumeric characters combined with dash and/or underscore.")
  @Key("nameToolTipMsg")
  String nameToolTipMsg();

  /**
   * Translated "Name must be at least 3 characters and can contain alphanumeric characters combined with dash, underscore and/or space.".
   * 
   * @return translated "Name must be at least 3 characters and can contain alphanumeric characters combined with dash, underscore and/or space."
   */
  @DefaultStringValue("Name must be at least 3 characters and can contain alphanumeric characters combined with dash, underscore and/or space.")
  @Key("name_spaceRegexMsg")
  String name_spaceRegexMsg();

  /**
   * Translated "Name is required.".
   * 
   * @return translated "Name is required."
   */
  @DefaultStringValue("Name is required.")
  @Key("name_spaceRequiredMsg")
  String name_spaceRequiredMsg();

  /**
   * Translated "Must be at least 3 characters and can contain alphanumeric characters combined with dash, underscore and/or space.".
   * 
   * @return translated "Must be at least 3 characters and can contain alphanumeric characters combined with dash, underscore and/or space."
   */
  @DefaultStringValue("Must be at least 3 characters and can contain alphanumeric characters combined with dash, underscore and/or space.")
  @Key("name_spaceToolTipMsg")
  String name_spaceToolTipMsg();

  /**
   * Translated "Interface".
   * 
   * @return translated "Interface"
   */
  @DefaultStringValue("Interface")
  @Key("netConnIf")
  String netConnIf();

  /**
   * Translated "IP Address".
   * 
   * @return translated "IP Address"
   */
  @DefaultStringValue("IP Address")
  @Key("netConnIp")
  String netConnIp();

  /**
   * Translated "Network Information".
   * 
   * @return translated "Network Information"
   */
  @DefaultStringValue("Network Information")
  @Key("netInfo")
  String netInfo();

  /**
   * Translated "None".
   * 
   * @return translated "None"
   */
  @DefaultStringValue("None")
  @Key("none")
  String none();

  /**
   * Translated "Expiration Date".
   * 
   * @return translated "Expiration Date"
   */
  @DefaultStringValue("Expiration Date")
  @Key("notAfter")
  String notAfter();

  /**
   * Translated "Start Date".
   * 
   * @return translated "Start Date"
   */
  @DefaultStringValue("Start Date")
  @Key("notBefore")
  String notBefore();

  /**
   * Translated "Additional Informations     ".
   * 
   * @return translated "Additional Informations     "
   */
  @DefaultStringValue("Additional Informations     ")
  @Key("note")
  String note();

  /**
   * Translated "Double".
   * 
   * @return translated "Double"
   */
  @DefaultStringValue("Double")
  @Key("numberDouble")
  String numberDouble();

  /**
   * Translated "Double Tooltip".
   * 
   * @return translated "Double Tooltip"
   */
  @DefaultStringValue("Double Tooltip")
  @Key("numberDoubleTooltip")
  String numberDoubleTooltip();

  /**
   * Translated "Float".
   * 
   * @return translated "Float"
   */
  @DefaultStringValue("Float")
  @Key("numberFloat")
  String numberFloat();

  /**
   * Translated "Float Tooltip".
   * 
   * @return translated "Float Tooltip"
   */
  @DefaultStringValue("Float Tooltip")
  @Key("numberFloatTooltip")
  String numberFloatTooltip();

  /**
   * Translated "Integer".
   * 
   * @return translated "Integer"
   */
  @DefaultStringValue("Integer")
  @Key("numberInteger")
  String numberInteger();

  /**
   * Translated "Integer Tooltip".
   * 
   * @return translated "Integer Tooltip"
   */
  @DefaultStringValue("Integer Tooltip")
  @Key("numberIntegerTooltip")
  String numberIntegerTooltip();

  /**
   * Translated "Long".
   * 
   * @return translated "Long"
   */
  @DefaultStringValue("Long")
  @Key("numberLong")
  String numberLong();

  /**
   * Translated "Long Tooltip".
   * 
   * @return translated "Long Tooltip"
   */
  @DefaultStringValue("Long Tooltip")
  @Key("numberLongTooltip")
  String numberLongTooltip();

  /**
   * Translated "Number of Child".
   * 
   * @return translated "Number of Child"
   */
  @DefaultStringValue("Number of Child")
  @Key("numberOfChild")
  String numberOfChild();

  /**
   * Translated "Number of Devices".
   * 
   * @return translated "Number of Devices"
   */
  @DefaultStringValue("Number of Devices")
  @Key("numberOfDevices")
  String numberOfDevices();

  /**
   * Translated "Number of Rules".
   * 
   * @return translated "Number of Rules"
   */
  @DefaultStringValue("Number of Rules")
  @Key("numberOfRules")
  String numberOfRules();

  /**
   * Translated "Number of VPN Connections".
   * 
   * @return translated "Number of VPN Connections"
   */
  @DefaultStringValue("Number of VPN Connections")
  @Key("numberOfVpn")
  String numberOfVpn();

  /**
   * Translated "Numbers".
   * 
   * @return translated "Numbers"
   */
  @DefaultStringValue("Numbers")
  @Key("numbersParameterLabelKey")
  String numbersParameterLabelKey();

  /**
   * Translated "Comma-separated list of recipients' phone number. Acceptable phone numbers are: +14155551212, (415) 555-1212 or 415-555-1212. ".
   * 
   * @return translated "Comma-separated list of recipients' phone number. Acceptable phone numbers are: +14155551212, (415) 555-1212 or 415-555-1212. "
   */
  @DefaultStringValue("Comma-separated list of recipients' phone number. Acceptable phone numbers are: +14155551212, (415) 555-1212 or 415-555-1212. ")
  @Key("numbersParameterTooltipKey")
  String numbersParameterTooltipKey();

  /**
   * Translated "Address 1".
   * 
   * @return translated "Address 1"
   */
  @DefaultStringValue("Address 1")
  @Key("organizationAddress1")
  String organizationAddress1();

  /**
   * Translated "Address 2".
   * 
   * @return translated "Address 2"
   */
  @DefaultStringValue("Address 2")
  @Key("organizationAddress2")
  String organizationAddress2();

  /**
   * Translated "City".
   * 
   * @return translated "City"
   */
  @DefaultStringValue("City")
  @Key("organizationCity")
  String organizationCity();

  /**
   * Translated "Country".
   * 
   * @return translated "Country"
   */
  @DefaultStringValue("Country")
  @Key("organizationCountry")
  String organizationCountry();

  /**
   * Translated "Email".
   * 
   * @return translated "Email"
   */
  @DefaultStringValue("Email")
  @Key("organizationEmail")
  String organizationEmail();

  /**
   * Translated "Organization Information".
   * 
   * @return translated "Organization Information"
   */
  @DefaultStringValue("Organization Information")
  @Key("organizationInfo")
  String organizationInfo();

  /**
   * Translated "Organization Name".
   * 
   * @return translated "Organization Name"
   */
  @DefaultStringValue("Organization Name")
  @Key("organizationName")
  String organizationName();

  /**
   * Translated "Reference Person".
   * 
   * @return translated "Reference Person"
   */
  @DefaultStringValue("Reference Person")
  @Key("organizationPersonName")
  String organizationPersonName();

  /**
   * Translated "Phone Number".
   * 
   * @return translated "Phone Number"
   */
  @DefaultStringValue("Phone Number")
  @Key("organizationPhoneNumber")
  String organizationPhoneNumber();

  /**
   * Translated "State/Province".
   * 
   * @return translated "State/Province"
   */
  @DefaultStringValue("State/Province")
  @Key("organizationState")
  String organizationState();

  /**
   * Translated "Zip/Post Code".
   * 
   * @return translated "Zip/Post Code"
   */
  @DefaultStringValue("Zip/Post Code")
  @Key("organizationZip")
  String organizationZip();

  /**
   * Translated "Password values do not match.".
   * 
   * @return translated "Password values do not match."
   */
  @DefaultStringValue("Password values do not match.")
  @Key("passwordDoesNotMatch")
  String passwordDoesNotMatch();

  /**
   * Translated "Password".
   * 
   * @return translated "Password"
   */
  @DefaultStringValue("Password")
  @Key("passwordParameterLabelKey")
  String passwordParameterLabelKey();

  /**
   * Translated "Password  to be used if the endpoint is protected by HTTP Basic Authentication.".
   * 
   * @return translated "Password  to be used if the endpoint is protected by HTTP Basic Authentication."
   */
  @DefaultStringValue("Password  to be used if the endpoint is protected by HTTP Basic Authentication.")
  @Key("passwordParameterTooltipKey")
  String passwordParameterTooltipKey();

  /**
   * Translated "Passwords must be at least 12 characters and contain at least one lower case letter, one upper case letter, one digit and one special character.".
   * 
   * @return translated "Passwords must be at least 12 characters and contain at least one lower case letter, one upper case letter, one digit and one special character."
   */
  @DefaultStringValue("Passwords must be at least 12 characters and contain at least one lower case letter, one upper case letter, one digit and one special character.")
  @Key("passwordRegexMsg")
  String passwordRegexMsg();

  /**
   * Translated "Password is required.".
   * 
   * @return translated "Password is required."
   */
  @DefaultStringValue("Password is required.")
  @Key("passwordRequiredMsg")
  String passwordRequiredMsg();

  /**
   * Translated "Must be at least 12 characters and contain at least one lower case letter, one upper case letter, one digit and one special character.".
   * 
   * @return translated "Must be at least 12 characters and contain at least one lower case letter, one upper case letter, one digit and one special character."
   */
  @DefaultStringValue("Must be at least 12 characters and contain at least one lower case letter, one upper case letter, one digit and one special character.")
  @Key("passwordToolTipMsg")
  String passwordToolTipMsg();

  /**
   * Translated "Installation Status".
   * 
   * @return translated "Installation Status"
   */
  @DefaultStringValue("Installation Status")
  @Key("pkiAllowedCertificateStatus")
  String pkiAllowedCertificateStatus();

  /**
   * Translated "Dnssec Certificate Status".
   * 
   * @return translated "Dnssec Certificate Status"
   */
  @DefaultStringValue("Dnssec Certificate Status")
  @Key("pkiDnssecStatus")
  String pkiDnssecStatus();

  /**
   * Translated "Certificate Status on external platform".
   * 
   * @return translated "Certificate Status on external platform"
   */
  @DefaultStringValue("Certificate Status on external platform")
  @Key("pkiExternalStatus")
  String pkiExternalStatus();

  /**
   * Translated "Certificate Informations".
   * 
   * @return translated "Certificate Informations"
   */
  @DefaultStringValue("Certificate Informations")
  @Key("pkiInfo")
  String pkiInfo();

  /**
   * Translated "Certificate Issuer".
   * 
   * @return translated "Certificate Issuer"
   */
  @DefaultStringValue("Certificate Issuer")
  @Key("pkiIssuer")
  String pkiIssuer();

  /**
   * Translated "Certificate Status".
   * 
   * @return translated "Certificate Status"
   */
  @DefaultStringValue("Certificate Status")
  @Key("pkiStatus")
  String pkiStatus();

  /**
   * Translated "Certificate Subject".
   * 
   * @return translated "Certificate Subject"
   */
  @DefaultStringValue("Certificate Subject")
  @Key("pkiSubject")
  String pkiSubject();

  /**
   * Translated "Validity Status".
   * 
   * @return translated "Validity Status"
   */
  @DefaultStringValue("Validity Status")
  @Key("pkiValidityStatus")
  String pkiValidityStatus();

  /**
   * Translated "Private Key".
   * 
   * @return translated "Private Key"
   */
  @DefaultStringValue("Private Key")
  @Key("privateKey")
  String privateKey();

  /**
   * Translated "ESF Properties".
   * 
   * @return translated "ESF Properties"
   */
  @DefaultStringValue("ESF Properties")
  @Key("propsEsf")
  String propsEsf();

  /**
   * Translated "System Properties".
   * 
   * @return translated "System Properties"
   */
  @DefaultStringValue("System Properties")
  @Key("propsSys")
  String propsSys();

  /**
   * Translated "Active On".
   * 
   * @return translated "Active On"
   */
  @DefaultStringValue("Active On")
  @Key("provActivatesOn")
  String provActivatesOn();

  /**
   * Translated "Activation Key".
   * 
   * @return translated "Activation Key"
   */
  @DefaultStringValue("Activation Key")
  @Key("provActivationKey")
  String provActivationKey();

  /**
   * Translated "Target Client ID".
   * 
   * @return translated "Target Client ID"
   */
  @DefaultStringValue("Target Client ID")
  @Key("provClientId")
  String provClientId();

  /**
   * Translated "Created By".
   * 
   * @return translated "Created By"
   */
  @DefaultStringValue("Created By")
  @Key("provCreatedBy")
  String provCreatedBy();

  /**
   * Translated "Created On".
   * 
   * @return translated "Created On"
   */
  @DefaultStringValue("Created On")
  @Key("provCreatedOn")
  String provCreatedOn();

  /**
   * Translated "Provisioned Credentials Tight".
   * 
   * @return translated "Provisioned Credentials Tight"
   */
  @DefaultStringValue("Provisioned Credentials Tight")
  @Key("provCredentialsTight")
  String provCredentialsTight();

  /**
   * Translated "Expires On".
   * 
   * @return translated "Expires On"
   */
  @DefaultStringValue("Expires On")
  @Key("provExpiresOn")
  String provExpiresOn();

  /**
   * Translated "Provision Properties".
   * 
   * @return translated "Provision Properties"
   */
  @DefaultStringValue("Provision Properties")
  @Key("provProperties")
  String provProperties();

  /**
   * Translated "Provision Max Attempts".
   * 
   * @return translated "Provision Max Attempts"
   */
  @DefaultStringValue("Provision Max Attempts")
  @Key("provProvMaxAttempts")
  String provProvMaxAttempts();

  /**
   * Translated "Current Attempts".
   * 
   * @return translated "Current Attempts"
   */
  @DefaultStringValue("Current Attempts")
  @Key("provProvRetryCount")
  String provProvRetryCount();

  /**
   * Translated "Provision Status".
   * 
   * @return translated "Provision Status"
   */
  @DefaultStringValue("Provision Status")
  @Key("provProvStatus")
  String provProvStatus();

  /**
   * Translated "Status".
   * 
   * @return translated "Status"
   */
  @DefaultStringValue("Status")
  @Key("provStatus")
  String provStatus();

  /**
   * Translated "Username".
   * 
   * @return translated "Username"
   */
  @DefaultStringValue("Username")
  @Key("provUsername")
  String provUsername();

  /**
   * Translated "EDC Internal RMI Gateway".
   * 
   * @return translated "EDC Internal RMI Gateway"
   */
  @DefaultStringValue("EDC Internal RMI Gateway")
  @Key("providerGateway")
  String providerGateway();

  /**
   * Translated "EDC Internal RMI Gateway IPv4 Address".
   * 
   * @return translated "EDC Internal RMI Gateway IPv4 Address"
   */
  @DefaultStringValue("EDC Internal RMI Gateway IPv4 Address")
  @Key("providerGatewayIP")
  String providerGatewayIP();

  /**
   * Translated "Provider Platform Inbound URL".
   * 
   * @return translated "Provider Platform Inbound URL"
   */
  @DefaultStringValue("Provider Platform Inbound URL")
  @Key("providerInbound")
  String providerInbound();

  /**
   * Translated "Provider Platform Inbound Password".
   * 
   * @return translated "Provider Platform Inbound Password"
   */
  @DefaultStringValue("Provider Platform Inbound Password")
  @Key("providerInboundPassword")
  String providerInboundPassword();

  /**
   * Translated "Provider Platform Inbound Username".
   * 
   * @return translated "Provider Platform Inbound Username"
   */
  @DefaultStringValue("Provider Platform Inbound Username")
  @Key("providerInboundUser")
  String providerInboundUser();

  /**
   * Translated "SIM Card Provider Name".
   * 
   * @return translated "SIM Card Provider Name"
   */
  @DefaultStringValue("SIM Card Provider Name")
  @Key("providerName")
  String providerName();

  /**
   * Translated "EDC Gateway Outbound URL".
   * 
   * @return translated "EDC Gateway Outbound URL"
   */
  @DefaultStringValue("EDC Gateway Outbound URL")
  @Key("providerOutbound")
  String providerOutbound();

  /**
   * Translated "EDC Gateway Outbound Password".
   * 
   * @return translated "EDC Gateway Outbound Password"
   */
  @DefaultStringValue("EDC Gateway Outbound Password")
  @Key("providerOutboundPassword")
  String providerOutboundPassword();

  /**
   * Translated "EDC Gateway Outbound Username".
   * 
   * @return translated "EDC Gateway Outbound Username"
   */
  @DefaultStringValue("EDC Gateway Outbound Username")
  @Key("providerOutboundUser")
  String providerOutboundUser();

  /**
   * Translated "Provision Request Info".
   * 
   * @return translated "Provision Request Info"
   */
  @DefaultStringValue("Provision Request Info")
  @Key("provisionInfo")
  String provisionInfo();

  /**
   * Translated "Public Key Informations".
   * 
   * @return translated "Public Key Informations"
   */
  @DefaultStringValue("Public Key Informations")
  @Key("publicKeyInfo")
  String publicKeyInfo();

  /**
   * Translated "Memory Optimized 2-Extra Large (r3.2xlarge)".
   * 
   * @return translated "Memory Optimized 2-Extra Large (r3.2xlarge)"
   */
  @DefaultStringValue("Memory Optimized 2-Extra Large (r3.2xlarge)")
  @Key("r3.2xlarge")
  String r3_2xlarge();

  /**
   * Translated "Memory Optimized 4-Extra Large (r3.4xlarge)".
   * 
   * @return translated "Memory Optimized 4-Extra Large (r3.4xlarge)"
   */
  @DefaultStringValue("Memory Optimized 4-Extra Large (r3.4xlarge)")
  @Key("r3.4xlarge")
  String r3_4xlarge();

  /**
   * Translated "Memory Optimized 8-Extra Large (r3.8xlarge)".
   * 
   * @return translated "Memory Optimized 8-Extra Large (r3.8xlarge)"
   */
  @DefaultStringValue("Memory Optimized 8-Extra Large (r3.8xlarge)")
  @Key("r3.8xlarge")
  String r3_8xlarge();

  /**
   * Translated "Memory Optimized Large (r3.large)".
   * 
   * @return translated "Memory Optimized Large (r3.large)"
   */
  @DefaultStringValue("Memory Optimized Large (r3.large)")
  @Key("r3.large")
  String r3_large();

  /**
   * Translated "Memory Optimized Extra Large (r3.xlarge)".
   * 
   * @return translated "Memory Optimized Extra Large (r3.xlarge)"
   */
  @DefaultStringValue("Memory Optimized Extra Large (r3.xlarge)")
  @Key("r3.xlarge")
  String r3_xlarge();

  /**
   * Translated "Health Rest Command Threshold (ms)".
   * 
   * @return translated "Health Rest Command Threshold (ms)"
   */
  @DefaultStringValue("Health Rest Command Threshold (ms)")
  @Key("restCommandThreshold")
  String restCommandThreshold();

  /**
   * Translated "REST".
   * 
   * @return translated "REST"
   */
  @DefaultStringValue("REST")
  @Key("restRuleActionLabelKey")
  String restRuleActionLabelKey();

  /**
   * Translated "Health Check Rest Threshold (ms)".
   * 
   * @return translated "Health Check Rest Threshold (ms)"
   */
  @DefaultStringValue("Health Check Rest Threshold (ms)")
  @Key("restThreshold")
  String restThreshold();

  /**
   * Translated "Rich Text".
   * 
   * @return translated "Rich Text"
   */
  @DefaultStringValue("Rich Text")
  @Key("richText")
  String richText();

  /**
   * Translated "Rich Text Tooltip".
   * 
   * @return translated "Rich Text Tooltip"
   */
  @DefaultStringValue("Rich Text Tooltip")
  @Key("richTextTooltip")
  String richTextTooltip();

  /**
   * Translated "Serial Number".
   * 
   * @return translated "Serial Number"
   */
  @DefaultStringValue("Serial Number")
  @Key("serial")
  String serial();

  /**
   * Translated "Service Plan Information".
   * 
   * @return translated "Service Plan Information"
   */
  @DefaultStringValue("Service Plan Information")
  @Key("servicePlanInfo")
  String servicePlanInfo();

  /**
   * Translated "Severity".
   * 
   * @return translated "Severity"
   */
  @DefaultStringValue("Severity")
  @Key("severityParameterLabelKey")
  String severityParameterLabelKey();

  /**
   * Translated "Must be 'CRITICAL' or 'WARNING' or 'INFO'.".
   * 
   * @return translated "Must be 'CRITICAL' or 'WARNING' or 'INFO'."
   */
  @DefaultStringValue("Must be 'CRITICAL' or 'WARNING' or 'INFO'.")
  @Key("severityParameterTooltipKey")
  String severityParameterTooltipKey();

  /**
   * Translated "Session Active".
   * 
   * @return translated "Session Active"
   */
  @DefaultStringValue("Session Active")
  @Key("simActive")
  String simActive();

  /**
   * Translated "Connection Status".
   * 
   * @return translated "Connection Status"
   */
  @DefaultStringValue("Connection Status")
  @Key("simConnectionStatus")
  String simConnectionStatus();

  /**
   * Translated "Detected IMEI".
   * 
   * @return translated "Detected IMEI"
   */
  @DefaultStringValue("Detected IMEI")
  @Key("simDetectedImei")
  String simDetectedImei();

  /**
   * Translated "Detected IMEI timestamp".
   * 
   * @return translated "Detected IMEI timestamp"
   */
  @DefaultStringValue("Detected IMEI timestamp")
  @Key("simDetectedImeiTimestamp")
  String simDetectedImeiTimestamp();

  /**
   * Translated "SIM Card Group".
   * 
   * @return translated "SIM Card Group"
   */
  @DefaultStringValue("SIM Card Group")
  @Key("simGroup")
  String simGroup();

  /**
   * Translated "ICCID Code".
   * 
   * @return translated "ICCID Code"
   */
  @DefaultStringValue("ICCID Code")
  @Key("simIccid")
  String simIccid();

  /**
   * Translated "IMEI Code".
   * 
   * @return translated "IMEI Code"
   */
  @DefaultStringValue("IMEI Code")
  @Key("simImei")
  String simImei();

  /**
   * Translated "IMSI Code".
   * 
   * @return translated "IMSI Code"
   */
  @DefaultStringValue("IMSI Code")
  @Key("simImsi")
  String simImsi();

  /**
   * Translated "1. SIM Card Status".
   * 
   * @return translated "1. SIM Card Status"
   */
  @DefaultStringValue("1. SIM Card Status")
  @Key("simInfo")
  String simInfo();

  /**
   * Translated "IP Address".
   * 
   * @return translated "IP Address"
   */
  @DefaultStringValue("IP Address")
  @Key("simIpAddress")
  String simIpAddress();

  /**
   * Translated "SIM IPv4 Address".
   * 
   * @return translated "SIM IPv4 Address"
   */
  @DefaultStringValue("SIM IPv4 Address")
  @Key("simIpv4Address")
  String simIpv4Address();

  /**
   * Translated "Latitude".
   * 
   * @return translated "Latitude"
   */
  @DefaultStringValue("Latitude")
  @Key("simLatitude")
  String simLatitude();

  /**
   * Translated "Longitude".
   * 
   * @return translated "Longitude"
   */
  @DefaultStringValue("Longitude")
  @Key("simLongitude")
  String simLongitude();

  /**
   * Translated "Last SIM Update".
   * 
   * @return translated "Last SIM Update"
   */
  @DefaultStringValue("Last SIM Update")
  @Key("simModifiedOn")
  String simModifiedOn();

  /**
   * Translated "4. SIM Card Geographical position".
   * 
   * @return translated "4. SIM Card Geographical position"
   */
  @DefaultStringValue("4. SIM Card Geographical position")
  @Key("simPosition")
  String simPosition();

  /**
   * Translated "Last Mo SMS Status".
   * 
   * @return translated "Last Mo SMS Status"
   */
  @DefaultStringValue("Last Mo SMS Status")
  @Key("simProvLastSmsMoStatus")
  String simProvLastSmsMoStatus();

  /**
   * Translated "Last Mo SMS Timestamp".
   * 
   * @return translated "Last Mo SMS Timestamp"
   */
  @DefaultStringValue("Last Mo SMS Timestamp")
  @Key("simProvLastSmsMoTimestamp")
  String simProvLastSmsMoTimestamp();

  /**
   * Translated "Last Mt SMS Status".
   * 
   * @return translated "Last Mt SMS Status"
   */
  @DefaultStringValue("Last Mt SMS Status")
  @Key("simProvLastSmsMtStatus")
  String simProvLastSmsMtStatus();

  /**
   * Translated "Last Mo SMS Status Timestamp".
   * 
   * @return translated "Last Mo SMS Status Timestamp"
   */
  @DefaultStringValue("Last Mo SMS Status Timestamp")
  @Key("simProvLastSmsMtStatusTimestamp")
  String simProvLastSmsMtStatusTimestamp();

  /**
   * Translated "Last Mt SMS Timestamp".
   * 
   * @return translated "Last Mt SMS Timestamp"
   */
  @DefaultStringValue("Last Mt SMS Timestamp")
  @Key("simProvLastSmsMtTimestamp")
  String simProvLastSmsMtTimestamp();

  /**
   * Translated "Last Wakeup SMS Request Timestamp".
   * 
   * @return translated "Last Wakeup SMS Request Timestamp"
   */
  @DefaultStringValue("Last Wakeup SMS Request Timestamp")
  @Key("simProvLastWakeupRequestTimestamp")
  String simProvLastWakeupRequestTimestamp();

  /**
   * Translated "Last Wakeup SMS Request Type".
   * 
   * @return translated "Last Wakeup SMS Request Type"
   */
  @DefaultStringValue("Last Wakeup SMS Request Type")
  @Key("simProvLastWakeupRequestType")
  String simProvLastWakeupRequestType();

  /**
   * Translated "Last Wakeup Status".
   * 
   * @return translated "Last Wakeup Status"
   */
  @DefaultStringValue("Last Wakeup Status")
  @Key("simProvLastWakeupStatus")
  String simProvLastWakeupStatus();

  /**
   * Translated "Last Wakeup Status Timestamp ".
   * 
   * @return translated "Last Wakeup Status Timestamp "
   */
  @DefaultStringValue("Last Wakeup Status Timestamp ")
  @Key("simProvLastWakeupStatusTimestamp")
  String simProvLastWakeupStatusTimestamp();

  /**
   * Translated "Bytes In".
   * 
   * @return translated "Bytes In"
   */
  @DefaultStringValue("Bytes In")
  @Key("simProvSessionBytesIn")
  String simProvSessionBytesIn();

  /**
   * Translated "Bytes On".
   * 
   * @return translated "Bytes On"
   */
  @DefaultStringValue("Bytes On")
  @Key("simProvSessionBytesOut")
  String simProvSessionBytesOut();

  /**
   * Translated "Authentication Failure Reason".
   * 
   * @return translated "Authentication Failure Reason"
   */
  @DefaultStringValue("Authentication Failure Reason")
  @Key("simProvSessionLastAuthFailureReason")
  String simProvSessionLastAuthFailureReason();

  /**
   * Translated "Authentication Failure Timestamp".
   * 
   * @return translated "Authentication Failure Timestamp"
   */
  @DefaultStringValue("Authentication Failure Timestamp")
  @Key("simProvSessionLastAuthFailureTimestamp")
  String simProvSessionLastAuthFailureTimestamp();

  /**
   * Translated "Cell Id".
   * 
   * @return translated "Cell Id"
   */
  @DefaultStringValue("Cell Id")
  @Key("simProvSessionLastCellId")
  String simProvSessionLastCellId();

  /**
   * Translated "Last Data Transmitted Timestamp".
   * 
   * @return translated "Last Data Transmitted Timestamp"
   */
  @DefaultStringValue("Last Data Transmitted Timestamp")
  @Key("simProvSessionLastDataTransmittedTimestamp")
  String simProvSessionLastDataTransmittedTimestamp();

  /**
   * Translated "Notification Failure Reason".
   * 
   * @return translated "Notification Failure Reason"
   */
  @DefaultStringValue("Notification Failure Reason")
  @Key("simProvSessionLastNotificationFailureReason")
  String simProvSessionLastNotificationFailureReason();

  /**
   * Translated "Notification Failure Timestamp".
   * 
   * @return translated "Notification Failure Timestamp"
   */
  @DefaultStringValue("Notification Failure Timestamp")
  @Key("simProvSessionLastNotificationFailureTimestamp")
  String simProvSessionLastNotificationFailureTimestamp();

  /**
   * Translated "Session End Timestamp".
   * 
   * @return translated "Session End Timestamp"
   */
  @DefaultStringValue("Session End Timestamp")
  @Key("simProvSessionLastTerminatedTimestamp")
  String simProvSessionLastTerminatedTimestamp();

  /**
   * Translated "Last non-zero Packet Data Timestamp".
   * 
   * @return translated "Last non-zero Packet Data Timestamp"
   */
  @DefaultStringValue("Last non-zero Packet Data Timestamp")
  @Key("simProvSessionPreviousNormalTimestamp")
  String simProvSessionPreviousNormalTimestamp();

  /**
   * Translated "SIM Card Provider".
   * 
   * @return translated "SIM Card Provider"
   */
  @DefaultStringValue("SIM Card Provider")
  @Key("simProvider")
  String simProvider();

  /**
   * Translated "SIM Card Provider Informations".
   * 
   * @return translated "SIM Card Provider Informations"
   */
  @DefaultStringValue("SIM Card Provider Informations")
  @Key("simProviderInfo")
  String simProviderInfo();

  /**
   * Translated "Provisioning Timestamp".
   * 
   * @return translated "Provisioning Timestamp"
   */
  @DefaultStringValue("Provisioning Timestamp")
  @Key("simProvisionOn")
  String simProvisionOn();

  /**
   * Translated "Provisioning Status".
   * 
   * @return translated "Provisioning Status"
   */
  @DefaultStringValue("Provisioning Status")
  @Key("simProvisionStatus")
  String simProvisionStatus();

  /**
   * Translated "SIM Provider Provisioning Informations".
   * 
   * @return translated "SIM Provider Provisioning Informations"
   */
  @DefaultStringValue("SIM Provider Provisioning Informations")
  @Key("simProvisioning")
  String simProvisioning();

  /**
   * Translated "SIM RAG Status".
   * 
   * @return translated "SIM RAG Status"
   */
  @DefaultStringValue("SIM RAG Status")
  @Key("simRagStatus")
  String simRagStatus();

  /**
   * Translated "3. SMS Communication Informations".
   * 
   * @return translated "3. SMS Communication Informations"
   */
  @DefaultStringValue("3. SMS Communication Informations")
  @Key("simSMS")
  String simSMS();

  /**
   * Translated "2. SIM Card Last Communication Sessions Info".
   * 
   * @return translated "2. SIM Card Last Communication Sessions Info"
   */
  @DefaultStringValue("2. SIM Card Last Communication Sessions Info")
  @Key("simSession")
  String simSession();

  /**
   * Translated "Session Start Timestamp".
   * 
   * @return translated "Session Start Timestamp"
   */
  @DefaultStringValue("Session Start Timestamp")
  @Key("simStartedTimestamp")
  String simStartedTimestamp();

  /**
   * Translated "SIM Status".
   * 
   * @return translated "SIM Status"
   */
  @DefaultStringValue("SIM Status")
  @Key("simStatus")
  String simStatus();

  /**
   * Translated "SIM Provider Platform Synchronization Informations".
   * 
   * @return translated "SIM Provider Platform Synchronization Informations"
   */
  @DefaultStringValue("SIM Provider Platform Synchronization Informations")
  @Key("simSync")
  String simSync();

  /**
   * Translated "Synchronization Logging Info".
   * 
   * @return translated "Synchronization Logging Info"
   */
  @DefaultStringValue("Synchronization Logging Info")
  @Key("simSyncLog")
  String simSyncLog();

  /**
   * Translated "Synchronization Timestamp".
   * 
   * @return translated "Synchronization Timestamp"
   */
  @DefaultStringValue("Synchronization Timestamp")
  @Key("simSyncOn")
  String simSyncOn();

  /**
   * Translated "Synchronization Status".
   * 
   * @return translated "Synchronization Status"
   */
  @DefaultStringValue("Synchronization Status")
  @Key("simSyncStatus")
  String simSyncStatus();

  /**
   * Translated "Last Data Packet Timestamp".
   * 
   * @return translated "Last Data Packet Timestamp"
   */
  @DefaultStringValue("Last Data Packet Timestamp")
  @Key("simUpdatedTimestamp")
  String simUpdatedTimestamp();

  /**
   * Translated "Simple (No SSL)".
   * 
   * @return translated "Simple (No SSL)"
   */
  @DefaultStringValue("Simple (No SSL)")
  @Key("simpleConnection")
  String simpleConnection();

  /**
   * Translated "Name must be at least 3 characters and can contain alphanumeric characters combined with dash.".
   * 
   * @return translated "Name must be at least 3 characters and can contain alphanumeric characters combined with dash."
   */
  @DefaultStringValue("Name must be at least 3 characters and can contain alphanumeric characters combined with dash.")
  @Key("simple_nameRegexMsg")
  String simple_nameRegexMsg();

  /**
   * Translated "Name is required.".
   * 
   * @return translated "Name is required."
   */
  @DefaultStringValue("Name is required.")
  @Key("simple_nameRequiredMsg")
  String simple_nameRequiredMsg();

  /**
   * Translated "Must be at least 3 characters and can contain alphanumeric characters combined with dash.".
   * 
   * @return translated "Must be at least 3 characters and can contain alphanumeric characters combined with dash."
   */
  @DefaultStringValue("Must be at least 3 characters and can contain alphanumeric characters combined with dash.")
  @Key("simple_nameToolTipMsg")
  String simple_nameToolTipMsg();

  /**
   * Translated "Twilio SMS".
   * 
   * @return translated "Twilio SMS"
   */
  @DefaultStringValue("Twilio SMS")
  @Key("smsRuleActionLabelKey")
  String smsRuleActionLabelKey();

  /**
   * Translated "Seeded Snapshot".
   * 
   * @return translated "Seeded Snapshot"
   */
  @DefaultStringValue("Seeded Snapshot")
  @Key("snapSeeded")
  String snapSeeded();

  /**
   * Translated "Server Authentication SSL".
   * 
   * @return translated "Server Authentication SSL"
   */
  @DefaultStringValue("Server Authentication SSL")
  @Key("sslConnection")
  String sslConnection();

  /**
   * Translated "Subject".
   * 
   * @return translated "Subject"
   */
  @DefaultStringValue("Subject")
  @Key("subject")
  String subject();

  /**
   * Translated "Country Name".
   * 
   * @return translated "Country Name"
   */
  @DefaultStringValue("Country Name")
  @Key("subjectC")
  String subjectC();

  /**
   * Translated "Subject Name".
   * 
   * @return translated "Subject Name"
   */
  @DefaultStringValue("Subject Name")
  @Key("subjectCN")
  String subjectCN();

  /**
   * Translated "Locality Name".
   * 
   * @return translated "Locality Name"
   */
  @DefaultStringValue("Locality Name")
  @Key("subjectL")
  String subjectL();

  /**
   * Translated "Organization Name".
   * 
   * @return translated "Organization Name"
   */
  @DefaultStringValue("Organization Name")
  @Key("subjectO")
  String subjectO();

  /**
   * Translated "Organizational Unit Name".
   * 
   * @return translated "Organizational Unit Name"
   */
  @DefaultStringValue("Organizational Unit Name")
  @Key("subjectOrganizationalUnitName")
  String subjectOrganizationalUnitName();

  /**
   * Translated "Subject".
   * 
   * @return translated "Subject"
   */
  @DefaultStringValue("Subject")
  @Key("subjectParameterLabelKey")
  String subjectParameterLabelKey();

  /**
   * Translated "Email Subject".
   * 
   * @return translated "Email Subject"
   */
  @DefaultStringValue("Email Subject")
  @Key("subjectParameterTooltipKey")
  String subjectParameterTooltipKey();

  /**
   * Translated "State or Province Name".
   * 
   * @return translated "State or Province Name"
   */
  @DefaultStringValue("State or Province Name")
  @Key("subjectST")
  String subjectST();

  /**
   * Translated "Micro (t1.micro)".
   * 
   * @return translated "Micro (t1.micro)"
   */
  @DefaultStringValue("Micro (t1.micro)")
  @Key("t1_micro")
  String t1_micro();

  /**
   * Translated "Tag".
   * 
   * @return translated "Tag"
   */
  @DefaultStringValue("Tag")
  @Key("tagParameterLabelKey")
  String tagParameterLabelKey();

  /**
   * Translated "Hashtag # prepended to the subject that will fire the IFTTT trigger (example: #edcalert).".
   * 
   * @return translated "Hashtag # prepended to the subject that will fire the IFTTT trigger (example: #edcalert)."
   */
  @DefaultStringValue("Hashtag # prepended to the subject that will fire the IFTTT trigger (example: #edcalert).")
  @Key("tagParameterTooltipKey")
  String tagParameterTooltipKey();

  /**
   * Translated "Testing".
   * 
   * @return translated "Testing"
   */
  @DefaultStringValue("Testing")
  @Key("testRuleActionLabelKey")
  String testRuleActionLabelKey();

  /**
   * Translated "Text".
   * 
   * @return translated "Text"
   */
  @DefaultStringValue("Text")
  @Key("text")
  String text();

  /**
   * Translated "Text".
   * 
   * @return translated "Text"
   */
  @DefaultStringValue("Text")
  @Key("textParameterLabelKey")
  String textParameterLabelKey();

  /**
   * Translated "Text Tooltip".
   * 
   * @return translated "Text Tooltip"
   */
  @DefaultStringValue("Text Tooltip")
  @Key("textTooltip")
  String textTooltip();

  /**
   * Translated "Time".
   * 
   * @return translated "Time"
   */
  @DefaultStringValue("Time")
  @Key("time")
  String time();

  /**
   * Translated "Time".
   * 
   * @return translated "Time"
   */
  @DefaultStringValue("Time")
  @Key("timeParameterLabelKey")
  String timeParameterLabelKey();

  /**
   * Translated "Time Tooltip".
   * 
   * @return translated "Time Tooltip"
   */
  @DefaultStringValue("Time Tooltip")
  @Key("timeTooltip")
  String timeTooltip();

  /**
   * Translated "To".
   * 
   * @return translated "To"
   */
  @DefaultStringValue("To")
  @Key("toParameterLabelKey")
  String toParameterLabelKey();

  /**
   * Translated "Recipient should be in the format of username@domain.com. Comma is used to separate multiple recipients. An alternative format is $emailTo.".
   * 
   * @return translated "Recipient should be in the format of username@domain.com. Comma is used to separate multiple recipients. An alternative format is $emailTo."
   */
  @DefaultStringValue("Recipient should be in the format of username@domain.com. Comma is used to separate multiple recipients. An alternative format is $emailTo.")
  @Key("toParameterRegexKey")
  String toParameterRegexKey();

  /**
   * Translated "Comma-separated list of recipients' email address or $emailTo.".
   * 
   * @return translated "Comma-separated list of recipients' email address or $emailTo."
   */
  @DefaultStringValue("Comma-separated list of recipients' email address or $emailTo.")
  @Key("toParameterTooltipKey")
  String toParameterTooltipKey();

  /**
   * Translated "Topic".
   * 
   * @return translated "Topic"
   */
  @DefaultStringValue("Topic")
  @Key("topicParameterLabelKey")
  String topicParameterLabelKey();

  /**
   * Translated "MQTT topic must start with either $EDC/$account/ or $account/RulesAssistant/. MQTT wildcards '#' and '+' are not allowed.".
   * 
   * @return translated "MQTT topic must start with either $EDC/$account/ or $account/RulesAssistant/. MQTT wildcards '#' and '+' are not allowed."
   */
  @DefaultStringValue("MQTT topic must start with either $EDC/$account/ or $account/RulesAssistant/. MQTT wildcards '#' and '+' are not allowed.")
  @Key("topicParameterRegexKey")
  String topicParameterRegexKey();

  /**
   * Translated "MQTT full topic name. Name must start with either $EDC/$account/ or $account/RulesAssistant/.".
   * 
   * @return translated "MQTT full topic name. Name must start with either $EDC/$account/ or $account/RulesAssistant/."
   */
  @DefaultStringValue("MQTT full topic name. Name must start with either $EDC/$account/ or $account/RulesAssistant/.")
  @Key("topicParameterTooltipKey")
  String topicParameterTooltipKey();

  /**
   * Translated "Total Data Usage (MB/m)".
   * 
   * @return translated "Total Data Usage (MB/m)"
   */
  @DefaultStringValue("Total Data Usage (MB/m)")
  @Key("totalDataUsage")
  String totalDataUsage();

  /**
   * Translated "TTL".
   * 
   * @return translated "TTL"
   */
  @DefaultStringValue("TTL")
  @Key("ttl")
  String ttl();

  /**
   * Translated "Twitter".
   * 
   * @return translated "Twitter"
   */
  @DefaultStringValue("Twitter")
  @Key("twitterRuleActionLabelKey")
  String twitterRuleActionLabelKey();

  /**
   * Translated "Type".
   * 
   * @return translated "Type"
   */
  @DefaultStringValue("Type")
  @Key("type")
  String type();

  /**
   * Translated "Url".
   * 
   * @return translated "Url"
   */
  @DefaultStringValue("Url")
  @Key("urlParameterLabelKey")
  String urlParameterLabelKey();

  /**
   * Translated "Invalid URL syntax.".
   * 
   * @return translated "Invalid URL syntax."
   */
  @DefaultStringValue("Invalid URL syntax.")
  @Key("urlParameterRegexKey")
  String urlParameterRegexKey();

  /**
   * Translated "The URL of the endpoint to be invoked. HTTP/HTTPs are the allowed schemes; for HTTPs, the endpoint should use a certificate signed a trusted authorized recognized by default by Java. The URL can contain query parameters and it can be parametrized using the names of the tokens selected in the Rule's statement.".
   * 
   * @return translated "The URL of the endpoint to be invoked. HTTP/HTTPs are the allowed schemes; for HTTPs, the endpoint should use a certificate signed a trusted authorized recognized by default by Java. The URL can contain query parameters and it can be parametrized using the names of the tokens selected in the Rule's statement."
   */
  @DefaultStringValue("The URL of the endpoint to be invoked. HTTP/HTTPs are the allowed schemes; for HTTPs, the endpoint should use a certificate signed a trusted authorized recognized by default by Java. The URL can contain query parameters and it can be parametrized using the names of the tokens selected in the Rule's statement.")
  @Key("urlParameterTooltipKey")
  String urlParameterTooltipKey();

  /**
   * Translated "us-east-1a".
   * 
   * @return translated "us-east-1a"
   */
  @DefaultStringValue("us-east-1a")
  @Key("us_east_1a")
  String us_east_1a();

  /**
   * Translated "us-east-1b".
   * 
   * @return translated "us-east-1b"
   */
  @DefaultStringValue("us-east-1b")
  @Key("us_east_1b")
  String us_east_1b();

  /**
   * Translated "us-east-1c".
   * 
   * @return translated "us-east-1c"
   */
  @DefaultStringValue("us-east-1c")
  @Key("us_east_1c")
  String us_east_1c();

  /**
   * Translated "us-east-1d".
   * 
   * @return translated "us-east-1d"
   */
  @DefaultStringValue("us-east-1d")
  @Key("us_east_1d")
  String us_east_1d();

  /**
   * Translated "us-east-1e".
   * 
   * @return translated "us-east-1e"
   */
  @DefaultStringValue("us-east-1e")
  @Key("us_east_1e")
  String us_east_1e();

  /**
   * Translated "User ID".
   * 
   * @return translated "User ID"
   */
  @DefaultStringValue("User ID")
  @Key("userId")
  String userId();

  /**
   * Translated "Username".
   * 
   * @return translated "Username"
   */
  @DefaultStringValue("Username")
  @Key("usernameParameterLabelKey")
  String usernameParameterLabelKey();

  /**
   * Translated "Username to be used if the endpoint is protected by HTTP Basic Authentication. ".
   * 
   * @return translated "Username to be used if the endpoint is protected by HTTP Basic Authentication. "
   */
  @DefaultStringValue("Username to be used if the endpoint is protected by HTTP Basic Authentication. ")
  @Key("usernameParameterTooltipKey")
  String usernameParameterTooltipKey();

  /**
   * Translated "Version".
   * 
   * @return translated "Version"
   */
  @DefaultStringValue("Version")
  @Key("version")
  String version();
}
