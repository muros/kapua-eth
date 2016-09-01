CREATE TABLE dvc_device_event (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NULL NOT NULL DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  device_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  received_on				TIMESTAMP(3)  NULL NOT NULL DEFAULT 0,
  sent_on					TIMESTAMP(3)  NULL DEFAULT NULL,
  
  pos_longitude				DECIMAL(11,8),
  pos_latitude 	            DECIMAL(11,8),
  pos_altitude              DECIMAL(11,8),
  pos_precision 			DECIMAL(11,8),
  pos_heading				DECIMAL(11,8),
  pos_speed                 DECIMAL(11,8),
  pos_timestamp             TIMESTAMP(3)  NULL DEFAULT 0,
  pos_satellites			INT,
  pos_status				INT,
  
  resource					VARCHAR(255)  NOT NULL,
  action					VARCHAR(255)  NOT NULL,
  response_code				VARCHAR(255)  NOT NULL,
  event_message				TEXT,
 
  attributes				 TEXT,
  properties                 TEXT,

  PRIMARY KEY (scope_id, id),   -- primary key needs to include the partitioning key
  INDEX idx_connection_status_id (scope_id, device_id, resource, action)
 
) ENGINE=InnoDB DEFAULT CHARSET=utf8 PARTITION BY HASH(scope_id) PARTITIONS 64;
