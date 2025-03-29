ALTER TABLE institution_google_workspace_group_mapping RENAME institution_group_identifier_mapping;
ALTER TABLE institution_google_workspace_group_mapping_aud RENAME institution_group_identifier_mapping_aud;

ALTER TABLE institution_group_identifier_mapping CHANGE group_email group_identifier VARCHAR(255) NOT NULL;
ALTER TABLE institution_group_identifier_mapping_aud CHANGE group_email group_identifier VARCHAR(255) NOT NULL;

ALTER TABLE institution_group_identifier_mapping ADD integration_type VARCHAR(255) NOT NULL DEFAULT 'GW';
ALTER TABLE institution_group_identifier_mapping_aud ADD integration_type VARCHAR(255) NULL;