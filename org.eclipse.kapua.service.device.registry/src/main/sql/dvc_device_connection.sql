CREATE TABLE dvc_device_connection (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP     DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  modified_on            	TIMESTAMP     NOT NULL,
  modified_by            	BIGINT(21)    UNSIGNED NOT NULL,  
  connection_status		    VARCHAR(12)   NOT NULL,
  connection_user_id        BIGINT(21)    UNSIGNED NOT NULL,  
  connection_protocol       VARCHAR(255),
  client_connection_ip      VARCHAR(255),
  server_connection_ip      VARCHAR(255), 
  optlock                   INT UNSIGNED,
  PRIMARY KEY (scope_id, id),   -- primary key needs to include the partitioning key
  INDEX idx_connection_status_id (scope_id, id, connection_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 PARTITION BY HASH(scope_id) PARTITIONS 64;
