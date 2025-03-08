CREATE TABLE security_log (
  id                    BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tts                   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  client_id             VARCHAR(128) NOT NULL,
  clientname            VARCHAR(128) NOT NULL,
  method                VARCHAR(32) NOT NULL,
  request               VARCHAR(1024) NOT NULL,
  ip_address            VARCHAR(32) NOT NULL,
  status                BIGINT NOT NULL DEFAULT 0,
  processed_time        BIGINT NOT NULL DEFAULT 0,

  INDEX idx_security_log_tts (tts)
);
