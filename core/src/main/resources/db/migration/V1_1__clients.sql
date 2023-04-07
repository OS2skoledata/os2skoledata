CREATE TABLE client (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name                         VARCHAR(255) NOT NULL,
  api_key                      VARCHAR(36) NOT NULL,
  last_active                  TIMESTAMP NULL,
  modification_history_offset  BIGINT NOT NULL,
  paused                       TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE client_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  name                         VARCHAR(255) NULL,
  api_key                      VARCHAR(36) NULL,
  last_active                  TIMESTAMP NULL,
  modification_history_offset  BIGINT NULL,
  paused                       TINYINT(1) NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT client_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);
