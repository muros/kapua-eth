INSERT INTO athz_user_permission (`scope_id`, `id`, `created_on`, `created_by`, `user_id`, `domain`) 
		VALUES 	
				-- kapua_sys permissions
				('1', '1',  UTC_TIMESTAMP(), '1', '1', 'account'),
			   	('1', '2', UTC_TIMESTAMP(), '1', '1', 'user'),
				('1', '3',  UTC_TIMESTAMP(), '1', '1', 'device_event'),
				('1', '4',  UTC_TIMESTAMP(), '1', '1', 'device_connection'),
				('1', '5', UTC_TIMESTAMP(), '1', '1', 'device'),
				('1', '6', UTC_TIMESTAMP(), '1', '1', 'data'),
				('1', '7', UTC_TIMESTAMP(), '1', '1', 'broker'),
				('1', '8', UTC_TIMESTAMP(), '1', '1', 'credential'),
				('1', '9', UTC_TIMESTAMP(), '1', '1', 'role'),
				('1', '10', UTC_TIMESTAMP(), '1', '1', 'user_permission'),				
				('1', '11', UTC_TIMESTAMP(), '1', '1', 'device_lifecycle'),
			    ('1', '12', UTC_TIMESTAMP(), '1', '1', 'device_management'),
			   
			    -- kapua_broker permissions
				('1', '101', UTC_TIMESTAMP(), '1', '2', 'broker');
