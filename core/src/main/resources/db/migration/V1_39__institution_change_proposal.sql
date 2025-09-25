ALTER TABLE institution DROP COLUMN too_few_people_error_message;

CREATE TABLE institution_change_proposal (
    id BIGINT NOT NULL AUTO_INCREMENT,
    institution_id BIGINT NOT NULL,
    created_date DATETIME,
    too_few_people_error_message TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_institution_change_proposal_institution FOREIGN KEY (institution_id) REFERENCES institution(id) ON DELETE CASCADE
);

CREATE TABLE proposed_person_change (
    id BIGINT NOT NULL AUTO_INCREMENT,
    change_proposal_id BIGINT NOT NULL,
    change_type VARCHAR(255),
    first_name VARCHAR(255),
    family_name VARCHAR(255),
    uni_login_user_id VARCHAR(255),
    person_type VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_proposed_person_change_change_proposal FOREIGN KEY (change_proposal_id) REFERENCES institution_change_proposal(id) ON DELETE CASCADE
);

CREATE INDEX idx_institution_change_proposal_institution_id ON institution_change_proposal(institution_id);
CREATE INDEX idx_institution_change_proposal_created_date ON institution_change_proposal(created_date);
CREATE INDEX idx_proposed_person_change_proposal_id ON proposed_person_change(change_proposal_id);
CREATE INDEX idx_proposed_person_change_type ON proposed_person_change(change_type);