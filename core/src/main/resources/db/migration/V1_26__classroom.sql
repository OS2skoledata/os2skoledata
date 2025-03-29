CREATE TABLE classroom_admin (
   id                   BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   `role`               VARCHAR(255) NULL,
   username             VARCHAR(255) NULL
);

CREATE TABLE classroom_action_queue (
   id                    BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   created               datetime NOT NULL,
   performed             datetime NULL,
   action                VARCHAR(255) NOT NULL,
   status                VARCHAR(255) NOT NULL,
   username              VARCHAR(255) NULL,
   course_id             VARCHAR(255) NOT NULL,
   error_message         VARCHAR(255) NULL,
   requested_by_username VARCHAR(255) NOT NULL
);