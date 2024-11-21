CREATE TABLE student_password_change_configuration (
   id                   BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   `role`               VARCHAR(255) NOT NULL,
   type                 VARCHAR(255) NOT NULL,
   filter               VARCHAR(255) NULL
);