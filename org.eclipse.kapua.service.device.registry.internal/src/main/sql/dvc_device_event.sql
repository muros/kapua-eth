CREATE TABLE dvc_device_event (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(6)  DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  device_id 				BIGINT(21)    UNSIGNED NOT NULL,
  received_on				TIMESTAMP(6)  DEFAULT 0,
  sent_on					TIMESTAMP(6)  DEFAULT 0,
  
  pos_longitude 			DECIMAL(11,8),
  pos_latitude 				DECIMAL(11,8),
  pos_altitude 				DECIMAL(11,8),
  pos_precision 			DECIMAL(11,8),
  pos_heading 				DECIMAL(11,8),
  pos_speed 				DECIMAL(11,8),
  pos_timestamp 			TIMESTAMP(6) NULL,
  pos_satellites			INT UNSIGNED,
  pos_status				INT UNSIGNED,
  
  
  resource					VARCHAR(64) NOT NULL,
  action					VARCHAR(64) NOT NULL,
  response_code				VARCHAR(16) NOT NULL,
  event_message				TEXT NOT NULL,
  
  PRIMARY KEY (scope_id, id),   -- primary key needs to include the partitioning key
  
  INDEX (scope_id, device_id, resource, received_on),
  INDEX (scope_id, device_id, received_on)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 PARTITION BY HASH(scope_id) PARTITIONS 64;
