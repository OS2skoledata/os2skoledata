CREATE TABLE password_setting (
   id                   BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   grade_group          VARCHAR(255) NOT NULL,
   min_length           BIGINT NOT NULL default 8,
   complex_password     BOOLEAN NOT NULL default false
);