CREATE TABLE athz_user_role_roles (
  user_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  role_id					BIGINT(21) 	  UNSIGNED NOT NULL,
  
  PRIMARY KEY (user_id, role_id)
  
) DEFAULT CHARSET=utf8;
