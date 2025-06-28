ALTER TABLE institutionperson ADD reserved_username VARCHAR(255) NULL;
ALTER TABLE institutionperson ADD primary_institution BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE unilogin MODIFY COLUMN initial_password varchar(255) NULL;

ALTER TABLE institutionperson_aud ADD reserved_username VARCHAR(255) NULL;
ALTER TABLE institutionperson_aud ADD primary_institution BOOLEAN NULL;