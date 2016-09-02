CREATE TABLE dvc_device_event (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  device_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  received_on				TIMESTAMP(3)  NOT NULL DEFAULT 0,
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

  PRIMARY KEY (scope_id, id)   -- primary key needs to include the partitioning key
) CHARSET=utf8;

CREATE INDEX idx_device_event_id ON dvc_device_event (scope_id, device_id, event_type);
