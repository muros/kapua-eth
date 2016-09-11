CREATE TABLE sys_configuration (
  scope_id          		 BIGINT(21) 	  UNSIGNED,
  id                         BIGINT(21) 	  UNSIGNED NOT NULL,
  pid						 VARCHAR(255) 	  NOT NULL,
  configurations			 TEXT,
  created_on                 TIMESTAMP(3) 	  DEFAULT 0,
  created_by                 BIGINT(21) 	  UNSIGNED NOT NULL,
  modified_on                TIMESTAMP(3) 	  NOT NULL,
  modified_by                BIGINT(21) 	  UNSIGNED NOT NULL,
  optlock                    INT UNSIGNED,
  attributes				 TEXT,
  properties                 TEXT,
  PRIMARY KEY  (id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX idx_configurationScopeId ON sys_configuration (scope_id);