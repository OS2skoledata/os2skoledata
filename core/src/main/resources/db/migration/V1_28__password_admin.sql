CREATE TABLE password_admin (
   id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   username VARCHAR(255) NOT NULL
);

CREATE TABLE password_admin_institutions (
   institution_id BIGINT NOT NULL,
   password_admin_id BIGINT NOT NULL,
   CONSTRAINT fk_password_admin_institutions_institution FOREIGN KEY (institution_id) REFERENCES institution(id) ON DELETE CASCADE,
   CONSTRAINT fk_password_admin_institutions_password_admin FOREIGN KEY (password_admin_id) REFERENCES password_admin(id) ON DELETE CASCADE
);