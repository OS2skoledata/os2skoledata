ALTER TABLE `group` ADD COLUMN group_google_workspace_email VARCHAR(64) NULL;
ALTER TABLE group_aud ADD COLUMN group_google_workspace_email VARCHAR(64) NULL;
ALTER TABLE institution ADD COLUMN employee_group_google_workspace_email VARCHAR(64) NULL;
ALTER TABLE institution_aud ADD COLUMN employee_group_google_workspace_email VARCHAR(64) NULL;