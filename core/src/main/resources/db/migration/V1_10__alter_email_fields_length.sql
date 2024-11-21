ALTER TABLE `group` MODIFY COLUMN group_google_workspace_email VARCHAR(255) NULL;
ALTER TABLE institution MODIFY COLUMN employee_group_google_workspace_email VARCHAR(255) NULL;
ALTER TABLE group_aud MODIFY COLUMN group_google_workspace_email VARCHAR(255) NULL;
ALTER TABLE institution_aud MODIFY COLUMN employee_group_google_workspace_email VARCHAR(255) NULL;