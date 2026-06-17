package dk.digitalidentity.os2skoledata.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.digitalidentity.os2skoledata.dao.model.Affiliation;
import dk.digitalidentity.os2skoledata.dao.model.DBAddress;
import dk.digitalidentity.os2skoledata.dao.model.DBContactPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployee;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExtern;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBPhoneNumber;
import dk.digitalidentity.os2skoledata.dao.model.DBRole;
import dk.digitalidentity.os2skoledata.dao.model.DBStudent;
import dk.digitalidentity.os2skoledata.dao.model.DBStudentGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBUniLogin;
import dk.digitalidentity.os2skoledata.dao.model.enums.EntityType;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class AuditDiffService {

	@Autowired
	private EntityManager entityManager;

	private final ObjectMapper diffMapper;

	public record DiffResult(String left, String right) {}

	public AuditDiffService() {
		diffMapper = new ObjectMapper();
		diffMapper.getFactory().setStreamWriteConstraints(
				StreamWriteConstraints.builder().maxNestingDepth(50).build()
		);
		diffMapper.registerModule(new JavaTimeModule());
		diffMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		diffMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		diffMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		// Mix-ins to break circular references during serialization
		diffMapper.addMixIn(DBGroup.class, GroupDiffMixin.class);
		diffMapper.addMixIn(DBInstitutionPerson.class, InstitutionPersonDiffMixin.class);
		diffMapper.addMixIn(DBInstitution.class, InstitutionDiffMixin.class);
		diffMapper.addMixIn(DBContactPerson.class, ContactPersonDiffMixin.class);
		diffMapper.addMixIn(DBStudent.class, StudentDiffMixin.class);
		diffMapper.addMixIn(DBEmployee.class, EmployeeDiffMixin.class);
		diffMapper.addMixIn(DBExtern.class, ExternDiffMixin.class);
		diffMapper.addMixIn(DBExternGroupId.class, ExternGroupIdDiffMixin.class);
		diffMapper.addMixIn(DBEmployeeGroupId.class, EmployeeGroupIdDiffMixin.class);
		diffMapper.addMixIn(DBStudentGroupId.class, StudentGroupIdDiffMixin.class);
		diffMapper.addMixIn(DBRole.class, RoleDiffMixin.class);
		diffMapper.addMixIn(DBPerson.class, PersonDiffMixin.class);
		diffMapper.addMixIn(Affiliation.class, AffiliationDiffMixin.class);
	}

	// Exclude back-reference to parent institution and non-audited collection
	@JsonIgnoreProperties({"institution", "googleWorkspaceClassFoldersAndGroups"})
	abstract static class GroupDiffMixin {}

	// Exclude back-reference to institution (context is already in the modification history row)
	@JsonIgnoreProperties({"institution"})
	abstract static class InstitutionPersonDiffMixin {}

	// Exclude large/circular collections that are not useful for the diff
	@JsonIgnoreProperties({"institutionPersons", "changeProposal", "integrationGroupIdentifierMappings", "azureEmployeeTeamAdmin"})
	abstract static class InstitutionDiffMixin {}

	// Break student <-> contactPerson cycle
	@JsonIgnoreProperties({"student"})
	abstract static class ContactPersonDiffMixin {}

	// Break any other back-references from student (add more if needed)
	@JsonIgnoreProperties({"institutionPerson"})
	abstract static class StudentDiffMixin {}

	@JsonIgnoreProperties({"institutionPerson"})
	abstract static class EmployeeDiffMixin {}

	@JsonIgnoreProperties({"institutionPerson"})
	abstract static class ExternDiffMixin {}

	@JsonIgnoreProperties({"extern"})
	abstract static class ExternGroupIdDiffMixin {}

	@JsonIgnoreProperties({"employee"})
	abstract static class EmployeeGroupIdDiffMixin {}

	@JsonIgnoreProperties({"student"})
	abstract static class StudentGroupIdDiffMixin {}

	@JsonIgnoreProperties({"employee"})
	abstract static class RoleDiffMixin {}

	@JsonIgnoreProperties({"affiliations"})
	abstract static class PersonDiffMixin {}

	@JsonIgnoreProperties({"person"})
	abstract static class AffiliationDiffMixin {}

	@Transactional(readOnly = true)
	public DiffResult getDiff(long entityId, EntityType entityType, long rev) {
		try {
			Class<?> entityClass = getEntityClass(entityType);
			AuditReader reader = AuditReaderFactory.get(entityManager);

			// Find all revisions for this entity
			List<Number> revisions = reader.getRevisions(entityClass, entityId);

			int currentIndex = -1;
			for (int i = 0; i < revisions.size(); i++) {
				if (revisions.get(i).longValue() == rev) {
					currentIndex = i;
					break;
				}
			}

			if (currentIndex < 0) {
				log.warn("Revision {} not found for entity {} with id {}", rev, entityType, entityId);
				return null;
			}

			Object current = reader.find(entityClass, entityId, rev);
			loadSubEntitiesFromAud(current, reader, rev, entityType);
			String rightJson = diffMapper.writerWithDefaultPrettyPrinter().writeValueAsString(current);

			String leftJson = "{}";
			if (currentIndex > 0) {
				Number previousRev = revisions.get(currentIndex - 1);
				Object previous = reader.find(entityClass, entityId, previousRev.longValue());
				loadSubEntitiesFromAud(previous, reader, previousRev.longValue(), entityType);
				leftJson = diffMapper.writerWithDefaultPrettyPrinter().writeValueAsString(previous);
			}

			return new DiffResult(leftJson, rightJson);
		}
		catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains("nesting depth")) {
				log.error("Circular reference detected for entity {} with id {} at rev {} - {}", entityType, entityId, rev, e.getMessage());
			} else {
				log.error("Failed to compute diff for entity {} with id {} at rev {}", entityType, entityId, rev, e);
			}
			return null;
		}
	}

	private Class<?> getEntityClass(EntityType entityType) {
		return switch (entityType) {
			case INSTITUTION -> DBInstitution.class;
			case INSTITUTION_PERSON -> DBInstitutionPerson.class;
			case GROUP -> DBGroup.class;
		};
	}

	private void loadSubEntitiesFromAud(Object entity, AuditReader reader, long rev, EntityType entityType) {
		if (entity == null) {
			return;
		}

		switch (entityType) {
			case INSTITUTION_PERSON -> {
				DBInstitutionPerson ip = (DBInstitutionPerson) entity;

				ip.setPerson(safeFindAud(reader, DBPerson.class, getIdFromProxy(ip.getPerson()), rev));
				ip.setUniLogin(safeFindAud(reader, DBUniLogin.class, getIdFromProxy(ip.getUniLogin()), rev));
				ip.setEmployee(safeFindAud(reader, DBEmployee.class, getIdFromProxy(ip.getEmployee()), rev));
				ip.setStudent(safeFindAud(reader, DBStudent.class, getIdFromProxy(ip.getStudent()), rev));
				ip.setExtern(safeFindAud(reader, DBExtern.class, getIdFromProxy(ip.getExtern()), rev));

				if (ip.getPerson() != null) {
					DBPerson person = ip.getPerson();
					person.setAddress(safeFindAud(reader, DBAddress.class, getIdFromProxy(person.getAddress()), rev));
					person.setHomePhoneNumber(safeFindAud(reader, DBPhoneNumber.class, getIdFromProxy(person.getHomePhoneNumber()), rev));
					person.setMobilePhoneNumber(safeFindAud(reader, DBPhoneNumber.class, getIdFromProxy(person.getMobilePhoneNumber()), rev));
					person.setWorkPhoneNumber(safeFindAud(reader, DBPhoneNumber.class, getIdFromProxy(person.getWorkPhoneNumber()), rev));
				}

				if (ip.getEmployee() != null) {
					safeInitializeCollection(ip.getEmployee().getGroupIds());
					safeInitializeCollection(ip.getEmployee().getRoles());
				}
				if (ip.getStudent() != null) {
					safeInitializeCollection(ip.getStudent().getGroupIds());
					safeInitializeCollection(ip.getStudent().getContactPersons());
				}
				if (ip.getExtern() != null) {
					safeInitializeCollection(ip.getExtern().getGroupIds());
				}
			}
			case INSTITUTION -> {
				DBInstitution inst = (DBInstitution) entity;
				safeInitializeCollection(inst.getGroups());
			}
			case GROUP -> {}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T safeFindAud(AuditReader reader, Class<T> clazz, Long id, long rev) {
		if (id == null) {
			return null;
		}
		try {
			return reader.find(clazz, id, rev);
		} catch (Exception e) {
			log.debug("Could not load {} with id {} at rev {}: {}", clazz.getSimpleName(), id, rev, e.getMessage());
			return null;
		}
	}

	private void safeInitializeCollection(Object collection) {
		try {
			if (collection != null) {
				Hibernate.initialize(collection);
			}
		} catch (Exception e) {
			log.debug("Could not initialize collection: {}", e.getMessage());
		}
	}

	private Long getIdFromProxy(Object proxy) {
		if (proxy == null) {
			return null;
		}
		if (proxy instanceof HibernateProxy hibernateProxy) {
			return (Long) hibernateProxy.getHibernateLazyInitializer().getIdentifier();
		}
		try {
			return (Long) proxy.getClass().getMethod("getId").invoke(proxy);
		} catch (Exception e) {
			return null;
		}
	}

}