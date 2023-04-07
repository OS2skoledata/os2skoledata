ALTER TABLE `group` ADD COLUMN azure_security_group_id VARCHAR(64) NULL;
ALTER TABLE group_aud ADD COLUMN azure_security_group_id VARCHAR(64) NULL;
ALTER TABLE institution ADD COLUMN employee_azure_security_group_id VARCHAR(64) NULL;
ALTER TABLE institution_aud ADD COLUMN employee_azure_security_group_id VARCHAR(64) NULL;
ALTER TABLE institution ADD COLUMN all_azure_security_group_id VARCHAR(64) NULL;
ALTER TABLE institution_aud ADD COLUMN all_azure_security_group_id VARCHAR(64) NULL;
ALTER TABLE institution ADD COLUMN student_azure_security_group_id VARCHAR(64) NULL;
ALTER TABLE institution_aud ADD COLUMN student_azure_security_group_id VARCHAR(64) NULL;