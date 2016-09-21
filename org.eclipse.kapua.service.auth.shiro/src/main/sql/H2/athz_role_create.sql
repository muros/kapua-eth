CREATE TABLE athz_role (
  scope_id             		BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  name 						VARCHAR(255)  NOT NULL,
  
  PRIMARY KEY (id)

) DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX idx_role_name ON athz_role (scope_id, name);