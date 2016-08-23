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
  osgi_framework_version    VARCHAR(255),
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
