DROP VIEW IF EXISTS view_datatables_modification_history;

CREATE OR REPLACE VIEW view_datatables_modification_history AS SELECT
    m.id,
    DATE_FORMAT(m.tts, "%Y/%m/%d %H:%i:%s") as tts,
    m.institution_name,
    m.institution_id,
    m.entity_type,
    m.entity_id,
    m.entity_name,
    m.event_type,
    m.groups,
    m.rev,
    m.uni_id,
    m.username,
    m.entity_role
  FROM modification_history m