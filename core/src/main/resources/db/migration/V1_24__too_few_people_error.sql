ALTER TABLE institution ADD too_few_people_error_message VARCHAR(500) NULL;
ALTER TABLE institution ADD too_few_people_error_count BIGINT NULL DEFAULT 0;

ALTER TABLE institution_aud ADD too_few_people_error_message VARCHAR(500) NULL;
ALTER TABLE institution_aud ADD too_few_people_error_count BIGINT NULL;