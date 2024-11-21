CREATE TABLE password_change_queue (
   id                          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   username                    VARCHAR(255) NOT NULL,
   password                    VARCHAR(255) NOT NULL,
   tts                         datetime NOT NULL,
   status                      VARCHAR(255) NOT NULL,
   message                     TEXT NULL
);

CREATE TABLE auditlogs (
   id                          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   tts                         datetime NOT NULL,
   ip_address                  VARCHAR(255) NOT NULL,
   person_name                 VARCHAR(255) NULL,
   person_username             VARCHAR(255) NULL,
   performer_username          VARCHAR(255) NULL,
   performer_name              VARCHAR(255) NULL,
   log_action                  VARCHAR(255) NOT NULL,
   details                     TEXT NULL
);