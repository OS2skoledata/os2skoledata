ALTER TABLE institution ADD COLUMN non_stil_institution BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE institution_aud ADD COLUMN non_stil_institution BOOLEAN NULL;

ALTER TABLE institutionperson ADD COLUMN api_only BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE institutionperson_aud ADD COLUMN api_only BOOLEAN NULL;