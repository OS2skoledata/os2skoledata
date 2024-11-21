CREATE TABLE institution_modification_history_offset (
   id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
   institution_id               BIGINT NOT NULL,
   client_id                    BIGINT NOT NULL,
   modification_history_offset  BIGINT NOT NULL,

   CONSTRAINT fk_institution_modification_history_offset_institution FOREIGN KEY (institution_id) REFERENCES institution(id) ON DELETE CASCADE,
   CONSTRAINT fk_institution_modification_history_offset_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);

CREATE TABLE institution_modification_history_offset_aud (
   id                           BIGINT NOT NULL,
   rev                          BIGINT NOT NULL,
   revtype                      TINYINT(4) DEFAULT NULL,
   institution_id               BIGINT DEFAULT NULL,
   client_id                    BIGINT DEFAULT NULL,
   modification_history_offset  BIGINT DEFAULT NULL,

   PRIMARY KEY (id,rev),
   KEY rev (rev),
   CONSTRAINT institution_modification_history_offset_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

INSERT INTO institution_modification_history_offset (institution_id, client_id, modification_history_offset)
     SELECT i.id, c.id, c.modification_history_offset
     FROM institution i, client c;

ALTER TABLE client DROP COLUMN modification_history_offset;