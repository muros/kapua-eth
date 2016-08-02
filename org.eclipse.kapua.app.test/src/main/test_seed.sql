DROP TABLE IF EXISTS act_account;

DROP TABLE IF EXISTS usr_user;

DROP TABLE IF EXISTS atht_credential;

DROP TABLE IF EXISTS athz_permission;
DROP TABLE IF EXISTS athz_user_permission;

DROP TABLE IF EXISTS athz_role;
DROP TABLE IF EXISTS athz_role_permission;
DROP TABLE IF EXISTS athz_user_role;
DROP TABLE IF EXISTS athz_user_role_roles;

DROP TABLE IF EXISTS dvc_device;
DROP TABLE IF EXISTS dvc_device_event;
DROP TABLE IF EXISTS dvc_device_connection;

CREATE TABLE act_account (
  scope_id          		 BIGINT(21) 	  UNSIGNED,
  id                         BIGINT(21) 	  UNSIGNED NOT NULL,
  name                       VARCHAR(255) 	  NOT NULL,
  created_on                 TIMESTAMP(3) 	  DEFAULT 0,
  created_by                 BIGINT(21) 	  UNSIGNED NOT NULL,
  modified_on                TIMESTAMP(3)     NOT NULL,
  modified_by                BIGINT(21) 	  UNSIGNED NOT NULL,
  org_name                   VARCHAR(255) 	  NOT NULL,
  org_person_name            VARCHAR(255) 	  DEFAULT "",
  org_email                  VARCHAR(255) 	  NOT NULL,
  org_phone_number           VARCHAR(64),
  org_address_line_1         VARCHAR(255),
  org_address_line_2         VARCHAR(255),
  org_address_line_3         VARCHAR(255),
  org_zip_postcode           VARCHAR(255),
  org_city                   VARCHAR(255),
  org_state_province_county  VARCHAR(255),
  org_country                VARCHAR(255),
  parent_account_path        VARCHAR(64),
  optlock                    INT UNSIGNED,
  attributes				 TEXT,
  properties                 TEXT,
  PRIMARY KEY  (id),
  FOREIGN KEY (scope_id) REFERENCES act_account(id) ON DELETE RESTRICT,
  CONSTRAINT act_accountName UNIQUE (name),
  INDEX idx_accountScopeId (scope_id)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO act_account ( 
	`scope_id`,
	`id`,
	`name`,
	`created_on`,
	`created_by`,
	`modified_on`,
	`modified_by`,
	`org_name`,
	`org_person_name`,
	`org_email`,
	`org_phone_number`,
	`org_address_line_1`,
	`org_address_line_2`,
	`org_address_line_3`,
	`org_zip_postcode`,
	`org_city`,
	`org_state_province_county`,
	`org_country`,
	`parent_account_path`,
	`optlock`,
	`attributes`,
	`properties`) 
VALUES (NULL,
		1,
		'kapua-sys',
		UTC_TIMESTAMP(),
		1,
		UTC_TIMESTAMP(),
		1,
		'kapua-org',
		'Kapua Sysadmin',
		'kapua-sys@eclipse.org',
		'+1 555 123 4567',
		NULL,
		NULL,
		NULL,
		NULL,
		NULL,
		NULL,
        NULL,
		'\1',
		0,
		NULL,
		NULL);

CREATE TABLE athz_permission (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  user_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  domain					VARCHAR(64)   NOT NULL,
  action					VARCHAR(64),
  target_scope_id		    BIGINT(21),
  
  PRIMARY KEY (id),
  
  UNIQUE INDEX idx_permissionScopeId (scope_id, user_id, domain, action, target_scope_id)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `athz_permission` (`scope_id`, `id`, `created_on`, `created_by`, `user_id`, `domain`, `action`, `target_scope_id`) 
		VALUES ('1', '1',  UTC_TIMESTAMP(), '1', '1', 'account', NULL, NULL),
			   ('1', '2',  UTC_TIMESTAMP(), '1', '1', 'user', NULL, NULL),
			   ('1', '3',  UTC_TIMESTAMP(), '1', '1', 'device-event', NULL, NULL),
			   ('1', '4',  UTC_TIMESTAMP(), '1', '1', 'device-connection', NULL, NULL),
			   ('1', '5',  UTC_TIMESTAMP(), '1', '1', 'device', NULL, NULL),
			   ('1', '6',  UTC_TIMESTAMP(), '1', '1', 'data', NULL, NULL),
			   ('1', '7',  UTC_TIMESTAMP(), '1', '1', 'broker', NULL, NULL),
			   ('1', '8',  UTC_TIMESTAMP(), '1', '2', 'account', NULL, NULL),
			   ('1', '9',  UTC_TIMESTAMP(), '1', '2', 'user', NULL, NULL),
			   ('1', '11', UTC_TIMESTAMP(), '1', '2', 'device-event', NULL, NULL),
			   ('1', '12', UTC_TIMESTAMP(), '1', '2', 'device-connection', NULL, NULL),
			   ('1', '13', UTC_TIMESTAMP(), '1', '2', 'device', NULL, NULL),
			   ('1', '14', UTC_TIMESTAMP(), '1', '2', 'data', NULL, NULL),
			   ('1', '15', UTC_TIMESTAMP(), '1', '2', 'broker', NULL, NULL);
				
CREATE TABLE usr_user (
  scope_id             		BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  name               	    VARCHAR(255)  NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  modified_on            	TIMESTAMP(3),
  modified_by            	BIGINT(21)    UNSIGNED,
  status                 	VARCHAR(64)   NOT NULL DEFAULT 'ENABLED',
  display_name           	VARCHAR(255),
  email                  	VARCHAR(255),
  phone_number           	VARCHAR(64),
  optlock               	INT UNSIGNED,
  attributes             	TEXT,  
  properties             	TEXT,  
  PRIMARY KEY (id),
  CONSTRAINT usr_uc_name UNIQUE (name),
  INDEX idx_userScopeId (scope_id)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `usr_user` (`scope_id`, `id`, `name`, `created_on`, `created_by`, `modified_on`, `modified_by`, `status`, `display_name`, `email`, `phone_number`, `optlock`, `attributes`, `properties`) 
		VALUES (1, 1, 'kapua-sys',    UTC_TIMESTAMP(), 1, UTC_TIMESTAMP(), 1, 'ENABLED', 'Kapua Sysadmin', 'kapua-sys@eclipse.org',    '+1 555 123 4567', 0, NULL, NULL),
		       (1, 2, 'kapua-broker', UTC_TIMESTAMP(), 1, UTC_TIMESTAMP(), 1, 'ENABLED', 'Kapua Broker',   'kapua-broker@eclipse.org', '+1 555 123 4567', 0, NULL, NULL);

CREATE TABLE atht_credential (
  scope_id             		BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  modified_on            	TIMESTAMP(3),
  modified_by            	BIGINT(21)    UNSIGNED,
  
  user_id 					BIGINT(21) 	  UNSIGNED NOT NULL,
  credential_type			VARCHAR(64)	  NOT NULL,
  credential_key			VARCHAR(255)  NOT NULL,
  
  optlock               	INT UNSIGNED,
  attributes             	TEXT,  
  properties             	TEXT,  
  PRIMARY KEY (id),
  INDEX idx_userScopeId (scope_id),
  INDEX idx_scopeIduserId (scope_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO atht_credential (`scope_id`, `id`, `created_on`, `created_by`, `modified_on`, `modified_by`, `user_id`, `credential_type`, `credential_key`, `optlock`) 
		VALUES ('1', '1', '2016-07-01 14:24:01.000', '1', '2016-07-01 14:24:01.000', '1', '1', 'PASSWORD', '$2a$12$cIW.D14SAka9SnNPVQVMUOLy2CYOEXDhEZ2KEeAeoLJmHeciWeht.', '0'),
			   ('1', '2', '2016-07-01 14:24:01.000', '1', '2016-07-01 14:24:01.000', '1', '2', 'PASSWORD', '$2a$12$cIW.D14SAka9SnNPVQVMUOLy2CYOEXDhEZ2KEeAeoLJmHeciWeht.', '0');

CREATE TABLE athz_user_permission (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  user_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  domain					VARCHAR(64)   NOT NULL,
  action					VARCHAR(64),
  target_scope_id		    BIGINT(21),
  
  PRIMARY KEY (id),
  
  UNIQUE INDEX idx_permissionScopeId (scope_id, user_id, domain, action, target_scope_id)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO athz_user_permission (`scope_id`, `id`, `created_on`, `created_by`, `user_id`, `domain`) 
		VALUES 	('1', '1', '2016-07-29 15:01:11.000', '1', '1', 'account'),
			   	('1', '2', '2016-07-29 15:01:11.000', '1', '1', 'user'),
				('1', '3', '2016-07-29 15:01:11.000', '1', '1', 'device-event'),
				('1', '4', '2016-07-29 15:01:11.000', '1', '1', 'device-connection'),
				('1', '5', '2016-07-29 15:01:11.000', '1', '1', 'device'),
				('1', '6', '2016-07-29 15:01:11.000', '1', '1', 'data'),
				('1', '7', '2016-07-29 15:01:11.000', '1', '1', 'broker'),
				('1', '8', '2016-07-29 15:01:11.000', '1', '1', 'credential'),
				('1', '9', '2016-07-29 15:01:11.000', '1', '1', 'role'),
				('1', '10', '2016-07-29 15:01:11.000', '1', '1', 'user_permission'),
				
				('1', '101', '2016-07-29 15:01:11.000', '1', '2', 'account'),
				('1', '102', '2016-07-29 15:01:11.000', '1', '2', 'user'),
				('1', '103', '2016-07-29 15:01:11.000', '1', '2', 'device-event'),
				('1', '104', '2016-07-29 15:01:11.000', '1', '2', 'device-connection'),
				('1', '105', '2016-07-29 15:01:11.000', '1', '2', 'device'),
				('1', '106', '2016-07-29 15:01:11.000', '1', '2', 'data'),
				('1', '107', '2016-07-29 15:01:11.000', '1', '2', 'broker'),
				('1', '108', '2016-07-29 15:01:11.000', '1', '2', 'credential'),
				('1', '109', '2016-07-29 15:01:11.000', '1', '2', 'role'),
				('1', '110', '2016-07-29 15:01:11.000', '1', '2', 'user_permission');
			   
CREATE TABLE athz_role (
  scope_id             		BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  name 						VARCHAR(255)  NOT NULL,
  
  PRIMARY KEY (id),
  
  UNIQUE INDEX idx_roleName (scope_id, name)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE athz_role_permission (
  scope_id             		BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,

  role_id             	    BIGINT(21) 	  UNSIGNED,
  domain					VARCHAR(64)   NOT NULL,
  action					VARCHAR(64),
  target_scope_id		    BIGINT(21),
  
  PRIMARY KEY (id),
  UNIQUE INDEX idx_permissionScopeId (role_id, domain, action, target_scope_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
				
CREATE TABLE athz_user_role (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  user_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  
  PRIMARY KEY (id),
  UNIQUE INDEX idx_permissionScopeId (scope_id, user_id)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE athz_user_role_roles (
  user_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  role_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  
  PRIMARY KEY (user_id, role_id)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE dvc_device (
  scope_id             	    BIGINT(21) 	    UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	    UNSIGNED NOT NULL,
  client_id                 VARCHAR(255)    BINARY NOT NULL,
  created_on             	TIMESTAMP(3)    NULL,
  created_by             	BIGINT(21)      UNSIGNED NOT NULL,
  modified_on            	TIMESTAMP       NULL,
  modified_by            	BIGINT(21)      UNSIGNED NOT NULL,
  status                 	VARCHAR(64)     NOT NULL DEFAULT 'ENABLED',
  display_name              VARCHAR(255), 
  last_event_on             TIMESTAMP(3)    NULL DEFAULT NULL,
  last_event_type           VARCHAR(255),
  serial_number             VARCHAR(255),
  model_id                  VARCHAR(255),
  imei                      VARCHAR(24),
  imsi                      VARCHAR(15),
  iccid                     VARCHAR(22),
  bios_version              VARCHAR(255),
  firmware_version          VARCHAR(255),
  os_version                VARCHAR(255),
  jvm_version               VARCHAR(255),
  osgi_version              VARCHAR(255),
  app_framework_version     VARCHAR(255),
  app_identifiers           VARCHAR(1024),
  accept_encoding           VARCHAR(255),
  gps_longitude             DECIMAL(11,8),
  gps_latitude              DECIMAL(11,8),
  custom_attribute_1        VARCHAR(255),
  custom_attribute_2        VARCHAR(255),
  custom_attribute_3        VARCHAR(255),
  custom_attribute_4        VARCHAR(255),
  custom_attribute_5        VARCHAR(255),
  credentials_mode          VARCHAR(64)   NOT NULL DEFAULT "INHERITED",
  preferred_user_id			BIGINT(21)    DEFAULT 0,
  optlock                   INT UNSIGNED,
  attributes             	TEXT,  
  properties             	TEXT,   
  PRIMARY KEY (scope_id, id),   -- primary key needs to include the partitioning key
  CONSTRAINT uc_clientId UNIQUE (scope_id, client_id),
  CONSTRAINT uc_imei UNIQUE (scope_id, imei),
  CONSTRAINT uc_imsi UNIQUE (scope_id, imsi),
  CONSTRAINT uc_iccid UNIQUE (scope_id, iccid),
  INDEX idx_serialNumber (scope_id, serial_number),
  INDEX idx_displayName (scope_id, display_name),
  INDEX idx_status_id (scope_id, status, client_id),
  INDEX idx_status_dn (scope_id, status, display_name),
  INDEX idx_status_le (scope_id, status, last_event_on),
  INDEX idx_model_id (scope_id, model_id, client_id),
  INDEX idx_model_dn (scope_id, model_id, display_name),
  INDEX idx_model_le (scope_id, model_id, last_event_on),
  INDEX idx_esf_id (scope_id, app_framework_version, client_id),
  INDEX idx_esf_dn (scope_id, app_framework_version, display_name),
  INDEX idx_esf_le (scope_id, app_framework_version, last_event_on),
  INDEX idx_app_id (scope_id, app_identifiers(255), client_id),
  INDEX idx_app_dn (scope_id, app_identifiers(255), display_name),
  INDEX idx_app_le (scope_id, app_identifiers(255), last_event_on),
  INDEX idx_c1_id (scope_id, custom_attribute_1, client_id),
  INDEX idx_c1_dn (scope_id, custom_attribute_1, display_name),
  INDEX idx_c1_le (scope_id, custom_attribute_1, last_event_on),
  INDEX idx_c2_id (scope_id, custom_attribute_2, client_id),
  INDEX idx_c2_dn (scope_id, custom_attribute_2, display_name),
  INDEX idx_c2_le (scope_id, custom_attribute_2, last_event_on),
  INDEX idx_preferred_user_id (scope_id, preferred_user_id)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 PARTITION BY HASH(scope_id) PARTITIONS 64;

CREATE TABLE dvc_device_event (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NULL NOT NULL DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  device_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  received_on				TIMESTAMP(3)  NULL NOT NULL DEFAULT 0,
  sent_on					TIMESTAMP(3)  NULL DEFAULT NULL,
  event_type				VARCHAR(255)  NOT NULL,
  event_message				TEXT,
  pos_longitude				DECIMAL(11,8),
  pos_latitude 	            DECIMAL(11,8),
  pos_altitude              DECIMAL(11,8),
  pos_precision 			DECIMAL(11,8),
  pos_heading				DECIMAL(11,8),
  pos_speed                 DECIMAL(11,8),
  pos_timestamp             TIMESTAMP(3)  NULL DEFAULT 0,
  pos_satellites			INT,
  pos_status				INT,
  attributes				 TEXT,
  properties                 TEXT,

  PRIMARY KEY (scope_id, id),   -- primary key needs to include the partitioning key
  INDEX idx_connection_status_id (scope_id, device_id, event_type)
 
) ENGINE=InnoDB DEFAULT CHARSET=utf8 PARTITION BY HASH(scope_id) PARTITIONS 64;

CREATE TABLE dvc_device_connection (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NULL NOT NULL DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  modified_on            	TIMESTAMP(3)  NULL NOT NULL,
  modified_by            	BIGINT(21)    UNSIGNED NOT NULL,  
  connection_status		    VARCHAR(20)   NOT NULL,
  client_id					VARCHAR(255)  NOT NULL,
  user_id        			BIGINT(21)    UNSIGNED NOT NULL,  
  protocol       			VARCHAR(64),
  client_ip      			VARCHAR(255),
  server_ip      			VARCHAR(255), 
  optlock                   INT UNSIGNED,
  attributes				 TEXT,
  properties                 TEXT,

  PRIMARY KEY (scope_id, id),   -- primary key needs to include the partitioning key
  INDEX idx_connection_status_id (scope_id, id, connection_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 PARTITION BY HASH(scope_id) PARTITIONS 64;