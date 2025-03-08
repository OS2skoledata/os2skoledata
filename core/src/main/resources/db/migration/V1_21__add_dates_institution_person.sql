ALTER TABLE institutionperson
    ADD stil_created datetime NULL,
    ADD stil_deleted datetime NULL,
    ADD ad_created datetime NULL,
    ADD ad_deactivated datetime NULL,
    ADD gw_created datetime NULL,
    ADD gw_deactivated datetime NULL,
    ADD azure_created datetime NULL,
    ADD azure_deactivated datetime NULL;

ALTER TABLE institutionperson_aud
    ADD stil_created datetime NULL,
    ADD stil_deleted datetime NULL,
    ADD ad_created datetime NULL,
    ADD ad_deactivated datetime NULL,
    ADD gw_created datetime NULL,
    ADD gw_deactivated datetime NULL,
    ADD azure_created datetime NULL,
    ADD azure_deactivated datetime NULL;

UPDATE institutionperson
SET stil_deleted = NOW()
WHERE deleted = 1;

CREATE INDEX idx_revtstmp ON revinfo (revtstmp);