ALTER TABLE client ADD monitor BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE client CHANGE last_active last_full_sync datetime;