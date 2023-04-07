CREATE TABLE institution_google_workspace_group_mapping (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  group_key                    VARCHAR(255) NOT NULL,
  group_email                  VARCHAR(255) NOT NULL,
  institution_id               BIGINT NOT NULL,

  CONSTRAINT fk_institution_google_workspace_group_mapping_institution FOREIGN KEY (institution_id) REFERENCES institution (id)
);

CREATE TABLE institution_google_workspace_group_mapping_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  group_key                    VARCHAR(255) DEFAULT NULL,
  group_email                  VARCHAR(255) DEFAULT NULL,
  institution_id               BIGINT DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT institution_google_workspace_group_mapping_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);