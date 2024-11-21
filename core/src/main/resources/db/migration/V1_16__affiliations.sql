CREATE TABLE affiliation (
   id                          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   employee_number             VARCHAR(255) NOT NULL,
   start_date                  DATE NULL,
   stop_date                   DATE NULL,
   person_id                   BIGINT NOT NULL,
   CONSTRAINT fk_affiliation_person FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE
);

CREATE TABLE affiliation_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  employee_number              VARCHAR(255) DEFAULT NULL,
  start_date                   DATE DEFAULT NULL,
  stop_date                    DATE DEFAULT NULL,
  person_id                    BIGINT DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT affiliation_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);