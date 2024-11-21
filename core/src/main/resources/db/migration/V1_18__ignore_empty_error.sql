ALTER TABLE institution ADD COLUMN ignore_empty_error BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE institution_aud ADD COLUMN ignore_empty_error BOOLEAN NULL;