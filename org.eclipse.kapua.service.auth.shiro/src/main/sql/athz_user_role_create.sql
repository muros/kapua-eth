CREATE TABLE athz_user_role (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  user_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  
  PRIMARY KEY (id),
  UNIQUE INDEX idx_permissionScopeId (scope_id, user_id)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;