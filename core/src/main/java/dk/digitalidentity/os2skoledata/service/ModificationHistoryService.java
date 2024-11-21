package dk.digitalidentity.os2skoledata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBPerson;
import dk.digitalidentity.os2skoledata.dao.model.ModificationHistory;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.EntityType;
import dk.digitalidentity.os2skoledata.service.model.AuditWrapper;
import dk.digitalidentity.os2skoledata.service.model.LatestModificationsWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ModificationHistoryService {
	private static final String maxQuery = "SELECT MAX(rev) FROM revinfo";
	private static final String insertQuery = "INSERT INTO modification_history (`tts`,`entity_id`,`entity_type`,`entity_name`,`event_type`, `institution_id`, `institution_name`, `groups`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String deleteQuery = "DELETE FROM modification_history WHERE tts < NOW() - INTERVAL ? DAY";
	private static final String getHeadQuery = "SELECT IFNULL(MAX(id),0) FROM modification_history;";
	
	@Qualifier("defaultTemplate")
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private SettingService settingService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private GroupService groupService;

	@Transactional(rollbackOn = Exception.class)
	public void processChanges() {
		LatestModificationsWrapper latestModificationObject = getLatestModifications();
		if (latestModificationObject != null && latestModificationObject.getLatestRevisionNumber() != null && latestModificationObject.getLatestRevisionNumber() > 0) {

			if (latestModificationObject.getInstitutionChanges() != null && latestModificationObject.getInstitutionChanges().size() > 0) {
				for (AuditWrapper institutionChange : latestModificationObject.getInstitutionChanges()) {
					DBInstitution institution = institutionService.findById(institutionChange.getId());
					if (institution == null) {
						log.warn("Unable to find institution with id " + institutionChange.getId());
						continue;
					}

					ModificationHistory modificationHistory = new ModificationHistory();
					modificationHistory.setEntityType(EntityType.INSTITUTION);
					modificationHistory.setEntityId(institutionChange.getId());
					modificationHistory.setTts(new Date());
					modificationHistory.setEventType(institutionChange.getChangeType());
					modificationHistory.setGroups(new ArrayList<>());

					if (institution != null) {
						modificationHistory.setEntityName(institution.getInstitutionName());
						modificationHistory.setInstitutionId(institution.getId());
						modificationHistory.setInstitutionName(institution.getInstitutionName());
					}

					insert(modificationHistory);
				}
			}

			if (latestModificationObject.getInstitutionPersonChanges() != null && latestModificationObject.getInstitutionPersonChanges().size() > 0) {
				for (AuditWrapper institutionPersonChange : latestModificationObject.getInstitutionPersonChanges()) {
					DBInstitutionPerson institutionPerson = institutionPersonService.findById(institutionPersonChange.getId());
					if (institutionPerson == null) {
						log.warn("Unable to find institutionPerson with id " + institutionPersonChange.getId());
						continue;
					}
					
					ModificationHistory modificationHistory = new ModificationHistory();
					modificationHistory.setEntityType(EntityType.INSTITUTION_PERSON);
					modificationHistory.setEntityId(institutionPersonChange.getId());
					modificationHistory.setTts(new Date());
					modificationHistory.setEventType(institutionPersonChange.getChangeType());
					modificationHistory.setGroups(new ArrayList<>());
					
					if (institutionPerson != null) {
						modificationHistory.setEntityName(DBPerson.getName(institutionPerson.getPerson()));
						modificationHistory.setInstitutionId(institutionPerson.getInstitution().getId());
						modificationHistory.setInstitutionName(institutionPerson.getInstitution().getInstitutionName());
						
						if (institutionPerson.getEmployee() != null) {
							modificationHistory.setGroups(institutionPerson.getEmployee().getGroupIds().stream()
									.map(gid -> gid.getGroupId())
									.collect(Collectors.toList()));
						}
						else if (institutionPerson.getExtern() != null) {
							modificationHistory.setGroups(institutionPerson.getExtern().getGroupIds().stream()
									.map(gid -> gid.getGroupId())
									.collect(Collectors.toList()));
						}
						else if (institutionPerson.getStudent() != null) {
							modificationHistory.setGroups(institutionPerson.getStudent().getGroupIds().stream()
									.map(gid -> gid.getGroupId())
									.collect(Collectors.toList()));
						}
					}

					insert(modificationHistory);
				}
			}

			if (latestModificationObject.getGroupChanges() != null && latestModificationObject.getGroupChanges().size() > 0) {
				for (AuditWrapper groupChange : latestModificationObject.getGroupChanges()) {
					DBGroup group = groupService.findById(groupChange.getId());
					if (group == null) {
						log.warn("Unable to find group with id " + groupChange.getId());
						continue;
					}
					
					ModificationHistory modificationHistory = new ModificationHistory();
					modificationHistory.setEntityType(EntityType.GROUP);
					modificationHistory.setEntityId(groupChange.getId());
					modificationHistory.setTts(new Date());
					modificationHistory.setEventType(groupChange.getChangeType());
					modificationHistory.setGroups(new ArrayList<>());
					
					if (group != null) {
						modificationHistory.setEntityName(group.getGroupName());
						modificationHistory.setInstitutionId(group.getInstitution().getId());
						modificationHistory.setInstitutionName(group.getInstitution().getInstitutionName());
					}

					insert(modificationHistory);
				}
			}

			updateLatestRevisionNumber(latestModificationObject.getLatestRevisionNumber());
		}
	}
	
	/**
	 * Returns LatestModificationsWrapper object that contains:</br>
	 * <ul>
	 * <li>latestRevisionNumber</li>
	 * <li>list of Person changes</li>
	 * <li>list of OrgUnit changes</li>
	 * </ul>
	 * if there are no changes returns null
	 */
	@SuppressWarnings("deprecation")
	public LatestModificationsWrapper getLatestModifications() {
		LatestModificationsWrapper result = new LatestModificationsWrapper();
		Long latestRevisionNumber = jdbcTemplate.queryForObject(maxQuery, Long.class);
		Long currentRevisionNumber = settingService.getLongValueByKey(CustomerSetting.LAST_READ_REVISION);
		
		if (latestRevisionNumber == null) {
			//no changes in the database
			return null;
		}
		
		if (currentRevisionNumber == null || currentRevisionNumber < latestRevisionNumber) {
			result.setLatestRevisionNumber(latestRevisionNumber);

			if (currentRevisionNumber == null) {
				currentRevisionNumber = 0l; // in sql query we cannot compare number to null.
			}

			// get all institution
			result.setInstitutionChanges(jdbcTemplate.query(getSelectSQL("institution_aud"), new Object[] { currentRevisionNumber, latestRevisionNumber }, (RowMapper<AuditWrapper>) (rs, rowNum) -> {
				return new AuditWrapper(rs);
			}));

			// get all institutionPerson
			result.setInstitutionPersonChanges(jdbcTemplate.query(getSelectSQL("institutionperson_aud"), new Object[] { currentRevisionNumber, latestRevisionNumber }, (RowMapper<AuditWrapper>) (rs, rowNum) -> {
				return new AuditWrapper(rs);
			}));

			// get all groups
			result.setGroupChanges(jdbcTemplate.query(getSelectSQL("group_aud"), new Object[] { currentRevisionNumber, latestRevisionNumber }, (RowMapper<AuditWrapper>) (rs, rowNum) -> {
				return new AuditWrapper(rs);
			}));

			return result;
		}

		return null;
	}

	public Long getHead() {
		return jdbcTemplate.queryForObject(getHeadQuery, Long.class);
	}

	private void insert(ModificationHistory modificationHistory) {
		jdbcTemplate.update(insertQuery,
				modificationHistory.getTts(),
				modificationHistory.getEntityId(),
				modificationHistory.getEntityType().toString(),
				modificationHistory.getEntityName(),
				modificationHistory.getEventType().toString(),
				modificationHistory.getInstitutionId(),
				modificationHistory.getInstitutionName(),
				String.join(",", modificationHistory.getGroups()));
	}

	public int removeModificationHistoryOlderThan(int days) {
		return jdbcTemplate.update(deleteQuery, days);
	}
	
	private String getSelectSQL(String tableName) {
		return "SELECT id, revtype FROM " + tableName + " WHERE " + tableName + ".rev > ? AND " + tableName + ".rev <= ?;";
	}
	
	private void updateLatestRevisionNumber(Long revNumber) {
		Setting setting = settingService.getByKey(CustomerSetting.LAST_READ_REVISION);
		setting.setValue(Long.toString(revNumber));
		settingService.save(setting);
	}
}