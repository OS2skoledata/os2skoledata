CREATE TABLE revinfo (
  rev                          BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  revtstmp                     BIGINT DEFAULT NULL
);

CREATE TABLE address (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  country                      VARCHAR(255) NULL,
  country_code                 VARCHAR(255) NULL,
  municipality_code            VARCHAR(255) NULL,
  municipality_name            VARCHAR(255) NULL,
  postal_code                  VARCHAR(255) NULL,
  postal_district              VARCHAR(255) NULL,
  street_address               VARCHAR(255) NULL
);

CREATE TABLE address_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  country                      VARCHAR(255) DEFAULT NULL,
  country_code                 VARCHAR(255) DEFAULT NULL,
  municipality_code            VARCHAR(255) DEFAULT NULL,
  municipality_name            VARCHAR(255) DEFAULT NULL,
  postal_code                  VARCHAR(255) DEFAULT NULL,
  postal_district              VARCHAR(255) DEFAULT NULL,
  street_address               VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT address_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE phonenumber (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  protected                    BOOLEAN NOT NULL,
  value                        VARCHAR(255) DEFAULT NULL
);

CREATE TABLE phonenumber_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  protected                    BOOLEAN DEFAULT NULL,
  value                        VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT phonenumber_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE person (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  protected                    BOOLEAN NOT NULL,
  alias_family_name            VARCHAR(255) DEFAULT NULL,
  alias_first_name             VARCHAR(255) DEFAULT NULL,
  birth_date                   DATE DEFAULT NULL,
  civil_registration_number    VARCHAR(255) DEFAULT NULL,
  email_address                VARCHAR(255) DEFAULT NULL,
  family_name                  VARCHAR(255) NOT NULL,
  first_name                   VARCHAR(255) NOT NULL,
  gender                       VARCHAR(255) DEFAULT NULL,
  photo_id                     VARCHAR(255) DEFAULT NULL,
  verification_level           BIGINT NOT NULL,
  address_id                   BIGINT DEFAULT NULL,
  home_phone_number_id         BIGINT DEFAULT NULL,
  mobile_phone_number_id       BIGINT DEFAULT NULL,
  work_phone_number_id         BIGINT DEFAULT NULL,

  CONSTRAINT fk_person_address FOREIGN KEY (address_id) REFERENCES address (id),
  CONSTRAINT fk_person_home_phone_number FOREIGN KEY (home_phone_number_id) REFERENCES phonenumber (id),
  CONSTRAINT fk_person_mobile_phone_number FOREIGN KEY (mobile_phone_number_id) REFERENCES phonenumber (id),
  CONSTRAINT fk_person_work_phone_number FOREIGN KEY (work_phone_number_id) REFERENCES phonenumber (id)
);

CREATE TABLE person_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  protected                    BOOLEAN DEFAULT NULL,
  alias_family_name            VARCHAR(255) DEFAULT NULL,
  alias_first_name             VARCHAR(255) DEFAULT NULL,
  birth_date                   DATE DEFAULT NULL,
  civil_registration_number    VARCHAR(255) DEFAULT NULL,
  email_address                VARCHAR(255) DEFAULT NULL,
  family_name                  VARCHAR(255) DEFAULT NULL,
  first_name                   VARCHAR(255) DEFAULT NULL,
  gender                       VARCHAR(255) DEFAULT NULL,
  photo_id                     VARCHAR(255) DEFAULT NULL,
  verification_level           BIGINT DEFAULT NULL,
  address_id                   BIGINT DEFAULT NULL,
  home_phone_number_id         BIGINT DEFAULT NULL,
  mobile_phone_number_id       BIGINT DEFAULT NULL,
  work_phone_number_id         BIGINT DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT person_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE student (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  level                        VARCHAR(255) DEFAULT NULL,
  location                     VARCHAR(255) DEFAULT NULL,
  main_group_id                VARCHAR(255) DEFAULT NULL,
  role                         VARCHAR(255) DEFAULT NULL,
  student_number               VARCHAR(255) DEFAULT NULL
);

CREATE TABLE student_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  level                        VARCHAR(255) NOT NULL,
  location                     VARCHAR(255) DEFAULT NULL,
  main_group_id                VARCHAR(255) NOT NULL,
  role                         VARCHAR(255) NOT NULL,
  student_number               VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT student_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE student_groupid (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  group_id                     VARCHAR(255) NOT NULL,
  student_id                   BIGINT NOT NULL,

  CONSTRAINT fk_student_group_student FOREIGN KEY (student_id) REFERENCES student (id)
);

CREATE TABLE student_groupid_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  group_id                     VARCHAR(255) DEFAULT NULL,
  student_id                   BIGINT DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT student_group_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE unilogin (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  civil_registration_number    VARCHAR(255) DEFAULT NULL,
  initial_password             VARCHAR(255) NOT NULL,
  name                         VARCHAR(255) DEFAULT NULL,
  password_state               VARCHAR(255) NOT NULL,
  user_id                      VARCHAR(255) NOT NULL
);

CREATE TABLE unilogin_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  civil_registration_number    VARCHAR(255) DEFAULT NULL,
  initial_password             VARCHAR(255) DEFAULT NULL,
  name                         VARCHAR(255) DEFAULT NULL,
  password_state               VARCHAR(255) DEFAULT NULL,
  user_id                      VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT unilogin_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE contact_person (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  access_level                 BIGINT NULL,
  cvr                          VARCHAR(255) NULL,
  pnr                          VARCHAR(255) NULL,
  relation                     VARCHAR(255) NOT NULL,
  person_id                    BIGINT NOT NULL,
  student_id                   BIGINT NULL,
  unilogin_id                  BIGINT NULL,
  child_custody                BOOLEAN NOT NULL,

  CONSTRAINT fk_contactperson_person FOREIGN KEY (person_id) REFERENCES person (id),
  CONSTRAINT fk_contactperson_student FOREIGN KEY (student_id) REFERENCES student (id),
  CONSTRAINT fk_contactperson_unilogin FOREIGN KEY (unilogin_id) REFERENCES unilogin (id)
);

CREATE TABLE contact_person_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  access_level                 BIGINT DEFAULT NULL,
  cvr                          VARCHAR(255) DEFAULT NULL,
  pnr                          VARCHAR(255) DEFAULT NULL,
  relation                     VARCHAR(255) DEFAULT NULL,
  person_id                    BIGINT DEFAULT NULL,
  student_id                   BIGINT DEFAULT NULL,
  unilogin_id                  BIGINT DEFAULT NULL,
  child_custody                BOOLEAN DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT contact_person_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE employee (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  location                     VARCHAR(255) DEFAULT NULL,
  occupation                   VARCHAR(255) DEFAULT NULL,
  short_name                   VARCHAR(255) DEFAULT NULL
);

CREATE TABLE employee_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  location                     VARCHAR(255) DEFAULT NULL,
  occupation                   VARCHAR(255) DEFAULT NULL,
  short_name                   VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT employee_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE employee_groupid (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  group_id VARCHAR(255) NOT NULL,
  employee_id BIGINT NOT NULL,

  CONSTRAINT fk_employee_groupid_employee FOREIGN KEY (employee_id) REFERENCES employee (id)
);

CREATE TABLE employee_groupid_aud (
  id BIGINT NOT NULL,
  rev BIGINT NOT NULL,
  revtype TINYINT(4) DEFAULT NULL,
  employee_id BIGINT DEFAULT NULL,
  group_id VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT employee_groupid_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE extern (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  role                         VARCHAR(255) NOT NULL
);

CREATE TABLE extern_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  role                         VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT extern_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE extern_groupid (
  id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  extern_id BIGINT NOT NULL,
  group_id VARCHAR(255) NOT NULL,
  
  CONSTRAINT fk_extern_groupid_extern FOREIGN KEY (extern_id) REFERENCES extern (id)
);

CREATE TABLE extern_groupid_aud (
  id BIGINT NOT NULL,
  rev BIGINT NOT NULL,
  revtype TINYINT(4) DEFAULT NULL,
  extern_id BIGINT DEFAULT NULL,
  group_id VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT extern_groupid_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE institution (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  last_modified                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted                      TINYINT(1) NOT NULL DEFAULT 0,
  institution_name             VARCHAR(255) DEFAULT NULL,
  institution_number           VARCHAR(255) NOT NULL
);

CREATE TABLE institution_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  last_modified                TIMESTAMP NULL,
  deleted                      TINYINT(1) NULL,
  institution_name             VARCHAR(255) DEFAULT NULL,
  institution_number           VARCHAR(255) DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT institution_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE `group` (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  last_modified                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted                      TINYINT(1) NOT NULL DEFAULT 0,
  from_date                    DATE DEFAULT NULL,
  to_date                      DATE DEFAULT NULL,
  group_id                     VARCHAR(255) NOT NULL,
  group_level                  VARCHAR(255) DEFAULT NULL,
  group_name                   VARCHAR(255) DEFAULT NULL,
  group_type                   VARCHAR(255) NOT NULL,
  line                         VARCHAR(255) DEFAULT NULL,
  institution_id               BIGINT DEFAULT NULL,

  CONSTRAINT fk_group_institution FOREIGN KEY (institution_id) REFERENCES institution (id)
);

CREATE TABLE group_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  last_modified                TIMESTAMP NULL,
  deleted                      TINYINT(1) NULL,
  from_date                    DATE DEFAULT NULL,
  group_id                     VARCHAR(255) DEFAULT NULL,
  group_level                  VARCHAR(255) DEFAULT NULL,
  group_name                   VARCHAR(255) DEFAULT NULL,
  group_type                   VARCHAR(255) DEFAULT NULL,
  line                         VARCHAR(255) DEFAULT NULL,
  to_date                      DATE DEFAULT NULL,
  institution_id               BIGINT DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT group_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE institutionperson (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  last_modified                TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted                      TINYINT(1) NOT NULL DEFAULT 0,
  local_person_id              VARCHAR(255) DEFAULT NULL,
  source                       VARCHAR(255) NOT NULL,
  employee_id                  BIGINT DEFAULT NULL,
  extern_id                    BIGINT DEFAULT NULL,
  institution_id               BIGINT DEFAULT NULL,
  person_id                    BIGINT DEFAULT NULL,
  student_id                   BIGINT DEFAULT NULL,
  unilogin_id                  BIGINT NOT NULL,

  CONSTRAINT fk_institutionperson_institution FOREIGN KEY (institution_id) REFERENCES institution (id),
  CONSTRAINT fk_institutionperson_employee FOREIGN KEY (employee_id) REFERENCES employee (id),
  CONSTRAINT fk_institutionperson_extern FOREIGN KEY (extern_id) REFERENCES extern (id),
  CONSTRAINT fk_institutionperson_person FOREIGN KEY (person_id) REFERENCES person (id),
  CONSTRAINT fk_institutionperson_student FOREIGN KEY (student_id) REFERENCES student (id),
  CONSTRAINT fk_institutionperson_unilogin FOREIGN KEY (unilogin_id) REFERENCES unilogin (id)
);

CREATE TABLE institutionperson_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  last_modified                TIMESTAMP NULL,
  deleted                      TINYINT(1) NULL,
  local_person_id              VARCHAR(255) DEFAULT NULL,
  source                       VARCHAR(255) DEFAULT NULL,
  employee_id                  BIGINT DEFAULT NULL,
  extern_id                    BIGINT DEFAULT NULL,
  institution_id               BIGINT DEFAULT NULL,
  person_id                    BIGINT DEFAULT NULL,
  student_id                   BIGINT DEFAULT NULL,
  unilogin_id                  BIGINT DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT institutionperson_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE role (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  employee_role                VARCHAR(255) NOT NULL,
  employee_id                  BIGINT NOT NULL,
  
  CONSTRAINT fk_role_employee FOREIGN KEY (employee_id) REFERENCES employee (id)
);

CREATE TABLE role_aud (
  id                           BIGINT NOT NULL,
  rev                          BIGINT NOT NULL,
  revtype                      TINYINT(4) DEFAULT NULL,
  employee_role                VARCHAR(255) DEFAULT NULL,
  employee_id                  BIGINT DEFAULT NULL,

  PRIMARY KEY (id,rev),
  KEY rev (rev),
  CONSTRAINT role_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev)
);

CREATE TABLE modification_history (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  tts                          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  entity_id                    BIGINT NOT NULL,
  entity_type                  VARCHAR(64) NOT NULL,
  entity_name                  VARCHAR(64) NOT NULL,
  event_type                   VARCHAR(64) NOT NULL,
  institution_id               BIGINT NOT NULL,
  institution_name             VARCHAR(64) NOT NULL,
  groups                       TEXT NOT NULL
);

CREATE TABLE settings (
  id                           BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  setting_key                  VARCHAR(64) NOT NULL,
  setting_value                TEXT NOT NULL
);
