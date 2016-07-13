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
  INDEX idx_scopeIduserId (scope_id, user_id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
