ALTER TABLE institution ADD COLUMN employee_azure_team_id VARCHAR(255) NULL;
ALTER TABLE institution_aud ADD COLUMN employee_azure_team_id VARCHAR(255) NULL;
ALTER TABLE institution ADD COLUMN azure_employee_team_admin_id BIGINT NULL;
ALTER TABLE institution_aud ADD COLUMN azure_employee_team_admin_id BIGINT NULL;
ALTER TABLE institution ADD CONSTRAINT azure_employee_team_admin FOREIGN KEY (azure_employee_team_admin_id) REFERENCES institutionperson (id);

ALTER TABLE `group` ADD COLUMN azure_team_id VARCHAR(255) NULL;
ALTER TABLE group_aud ADD COLUMN azure_team_id VARCHAR(255) NULL;