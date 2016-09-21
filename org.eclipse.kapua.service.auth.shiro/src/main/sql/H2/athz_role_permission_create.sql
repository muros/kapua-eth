CREATE TABLE athz_role_permission (
  scope_id             		BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,

  role_id             	    BIGINT(21) 	  UNSIGNED,
  domain					VARCHAR(64)   NOT NULL,
  action					VARCHAR(64),
  target_scope_id		    BIGINT(21),
  
  PRIMARY KEY (id)

) DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX idx_role_permission_scope_id ON athz_role_permission (role_id, domain, action, target_scope_id);