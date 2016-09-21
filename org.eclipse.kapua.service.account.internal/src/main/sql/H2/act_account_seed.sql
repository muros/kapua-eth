INSERT INTO `act_account` ( 
	`scope_id`,
	`id`,
	`name`,
	`created_on`,
	`created_by`,
	`modified_on`,
	`modified_by`,
	`org_name`,
	`org_person_name`,
	`org_email`,
	`org_phone_number`,
	`org_address_line_1`,
	`org_address_line_2`,
	`org_address_line_3`,
	`org_zip_postcode`,
	`org_city`,
	`org_state_province_county`,
	`org_country`,
	`parent_account_path`,
	`optlock`,
	`attributes`,
	`properties`) 
VALUES (NULL,
		1,
		'kapua-sys',
		CURRENT_TIMESTAMP(),
		1,
		CURRENT_TIMESTAMP(),
		1,
		'kapua-org',
		'Kapua Sysadmin',
		'kapua-sys@eclipse.org',
		'+1 555 123 4567',
		NULL,
		NULL,
		NULL,
		NULL,
		NULL,
		NULL,
        NULL,
		'\1',
		0,
		NULL,
		NULL);
