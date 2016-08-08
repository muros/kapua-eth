INSERT INTO `usr_user` (`scope_id`, `id`, `name`, `created_on`, `created_by`, `modified_on`, `modified_by`, `status`, `display_name`, `email`, `phone_number`, `optlock`, `attributes`, `properties`) 
		VALUES (1, 1, 'kapua-sys',    UTC_TIMESTAMP(), 1, UTC_TIMESTAMP(), 1, 'ENABLED', 'Kapua Sysadmin', 'kapua-sys@eclipse.org',    '+1 555 123 4567', 0, NULL, NULL),
		       (1, 2, 'kapua-broker', UTC_TIMESTAMP(), 1, UTC_TIMESTAMP(), 1, 'ENABLED', 'Kapua Broker',   'kapua-broker@eclipse.org', '+1 555 123 4567', 0, NULL, NULL);
