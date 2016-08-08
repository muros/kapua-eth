CREATE TABLE athz_role (
  scope_id             		BIGINT(21) 	  UNSIGNED NOT NULL,
  id                     	BIGINT(21) 	  UNSIGNED NOT NULL,
  created_on             	TIMESTAMP(3)  NOT NULL,
  created_by             	BIGINT(21)    UNSIGNED NOT NULL,
  
  name 						VARCHAR(255)  NOT NULL,
  
  PRIMARY KEY (id),  
  UNIQUE INDEX idx_roleName (scope_id, name)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
