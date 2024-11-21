ALTER TABLE institution
   ADD student_institution_google_workspace_id VARCHAR(255) NULL,
   ADD employee_institution_google_workspace_id VARCHAR(255) NULL;

ALTER TABLE institution_aud
   ADD student_institution_google_workspace_id VARCHAR(255) NULL,
   ADD employee_institution_google_workspace_id VARCHAR(255) NULL;