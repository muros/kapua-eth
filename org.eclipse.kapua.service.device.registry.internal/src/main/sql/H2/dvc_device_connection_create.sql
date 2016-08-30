CREATE TABLE dvc_device_connection (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  modified_on            	TIMESTAMP(3)  NOT NULL,
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

  PRIMARY KEY (scope_id, id)   -- primary key needs to include the partitioning key
) DEFAULT CHARSET=utf8;

CREATE INDEX idx_connection_status_id ON dvc_device_connection (scope_id, id, connection_status);