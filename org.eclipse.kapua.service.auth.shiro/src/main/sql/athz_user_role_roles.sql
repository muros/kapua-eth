CREATE TABLE athz_user_role_roles (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  user_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  role_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  
  
  PRIMARY KEY (scope_id, user_id, role_id)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;