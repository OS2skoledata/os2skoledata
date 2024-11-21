ALTER TABLE institution ADD COLUMN bypass_too_few_people BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE institution_aud ADD COLUMN bypass_too_few_people BOOLEAN NULL;