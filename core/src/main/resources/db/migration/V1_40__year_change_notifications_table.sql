CREATE TABLE year_change_notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    institution_number VARCHAR(20) NOT NULL,
    institution_name VARCHAR(255) NOT NULL,
    year_change_date DATE NOT NULL,
    year_change_timestamp DATETIME NOT NULL,
    old_school_year VARCHAR(20) NOT NULL,
    new_school_year VARCHAR(20) NOT NULL,
    initial_notification_sent BOOLEAN NOT NULL DEFAULT FALSE,
    initial_notification_sent_at DATETIME NULL,
    reminder_sent BOOLEAN NOT NULL DEFAULT FALSE,
    reminder_sent_at DATETIME NULL,
    resolved BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at DATETIME NULL,

    UNIQUE KEY uk_institution_year_change (institution_number, year_change_date)
)