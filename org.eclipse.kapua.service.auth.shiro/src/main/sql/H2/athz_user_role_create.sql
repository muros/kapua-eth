CREATE TABLE athz_user_role (
  scope_id             	    BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  DEFAULT 0,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  user_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  
  PRIMARY KEY (id)
  
) DEFAULT CHARSET=utf8;

CREATE INDEX idx_user_role_scope_id ON athz_user_role (scope_id, user_id);