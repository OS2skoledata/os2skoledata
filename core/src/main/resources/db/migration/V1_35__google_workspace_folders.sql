ALTER TABLE institution ADD institution_drive_google_workspace_id VARCHAR(255) NULL;

CREATE TABLE google_workspace_class_folder (
   id                    BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
   created               date NOT NULL,
   level                 BIGINT NOT NULL,
   google_workspace_id   VARCHAR(255) NOT NULL,
   group_id              BIGINT NULL,
   type                  VARCHAR(255) NOT NULL,
   CONSTRAINT FK_GOOGLEWORKSPACECLASSFOLDER_ON_GROUP FOREIGN KEY (group_id) REFERENCES `group` (id)
);

ALTER TABLE `group` ADD current_year_gw_group_identifier VARCHAR(255) NULL, ADD current_year_gw_folder_identifier VARCHAR(255) NULL;