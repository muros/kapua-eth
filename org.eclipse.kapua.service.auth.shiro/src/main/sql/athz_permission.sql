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