CREATE TABLE act_account (
  scope_id          		 BIGINT(21) 	  UNSIGNED,
  id                         BIGINT(21) 	  UNSIGNED NOT NULL,
  name                       VARCHAR(255) 	  NOT NULL,
  created_on                 TIMESTAMP(3) 	  DEFAULT 0,
  created_by                 BIGINT(21) 	  UNSIGNED NOT NULL,
  modified_on                TIMESTAMP(3)     NOT NULL,
  modified_by                BIGINT(21) 	  UNSIGNED NOT NULL,
  org_name                   VARCHAR(255) 	  NOT NULL,
  org_person_name            VARCHAR(255) 	  DEFAULT '',
  org_email                  VARCHAR(255) 	  NOT NULL,
  org_phone_number           VARCHAR(64),
  org_address_line_1         VARCHAR(255),
  org_address_line_2         VARCHAR(255),
  org_address_line_3         VARCHAR(255),
  org_zip_postcode           VARCHAR(255),
  org_city                   VARCHAR(255),
  org_state_province_county  VARCHAR(255),
  org_country                VARCHAR(255),
  parent_account_path        VARCHAR(64),
  optlock                    INT UNSIGNED,
  attributes				 TEXT,
  properties                 TEXT,
  
  PRIMARY KEY (id),
  FOREIGN KEY (scope_id) REFERENCES act_account(id) ON DELETE RESTRICT,
  CONSTRAINT act_accountName UNIQUE (name)
) DEFAULT CHARSET=utf8;

CREATE INDEX idx_account_scope_id ON act_account (scope_id);