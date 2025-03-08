CREATE TABLE ghost (
   id BIGINT AUTO_INCREMENT NOT NULL,
   username VARCHAR(255) NOT NULL,
   active_until date NOT NULL,
   CONSTRAINT pk_ghost PRIMARY KEY (id)
);