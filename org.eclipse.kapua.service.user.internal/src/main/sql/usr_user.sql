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
