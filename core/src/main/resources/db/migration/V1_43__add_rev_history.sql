ALTER TABLE modification_history ADD COLUMN rev BIGINT NULL;
ALTER TABLE modification_history ADD COLUMN uni_id VARCHAR(255) NULL;
ALTER TABLE modification_history ADD COLUMN username VARCHAR(255) NULL;
ALTER TABLE modification_history ADD COLUMN entity_role VARCHAR(255) NULL;

-- Backfill rev for existing INSTITUTION rows
UPDATE modification_history mh
SET mh.rev = (
    SELECT ia.rev
    FROM institution_aud ia
             JOIN revinfo ri ON ri.rev = ia.rev
    WHERE ia.id = mh.entity_id
      AND ri.revtstmp <= UNIX_TIMESTAMP(mh.tts) * 1000
    ORDER BY ia.rev DESC
    LIMIT 1
    )
WHERE mh.entity_type = 'INSTITUTION' AND mh.rev IS NULL;

-- Backfill rev for existing INSTITUTION_PERSON rows
UPDATE modification_history mh
SET mh.rev = (
    SELECT ia.rev
    FROM institutionperson_aud ia
             JOIN revinfo ri ON ri.rev = ia.rev
    WHERE ia.id = mh.entity_id
      AND ri.revtstmp <= UNIX_TIMESTAMP(mh.tts) * 1000
    ORDER BY ia.rev DESC
    LIMIT 1
    )
WHERE mh.entity_type = 'INSTITUTION_PERSON' AND mh.rev IS NULL;

-- Backfill rev for existing GROUP rows
UPDATE modification_history mh
SET mh.rev = (
    SELECT ga.rev
    FROM `group_aud` ga
             JOIN revinfo ri ON ri.rev = ga.rev
    WHERE ga.id = mh.entity_id
      AND ri.revtstmp <= UNIX_TIMESTAMP(mh.tts) * 1000
    ORDER BY ga.rev DESC
    LIMIT 1
    )
WHERE mh.entity_type = 'GROUP' AND mh.rev IS NULL;

UPDATE modification_history mh
    JOIN institutionperson ip ON ip.id = mh.entity_id
    LEFT JOIN unilogin ul ON ul.id = ip.unilogin_id
    SET mh.uni_id = ul.user_id,
        mh.username = ip.username,
        mh.entity_role = CASE
        WHEN ip.student_id IS NOT NULL THEN 'Elev'
        WHEN ip.employee_id IS NOT NULL THEN 'Medarbejder'
        WHEN ip.extern_id IS NOT NULL THEN 'Ekstern'
END
WHERE mh.entity_type = 'INSTITUTION_PERSON';