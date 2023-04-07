package dk.digitalidentity.os2skoledata.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import dk.digitalidentity.os2skoledata.config.modules.InstitutionDTO;
import https.unilogin.Institution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.service.stil.StilService;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionFullMyndighed;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionPersonFullMyndighed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SyncService {

	@Autowired
	private StilService stilService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Transactional(rollbackOn = Exception.class)
	public void sync() {

		for (InstitutionDTO institutionDTO : configuration.getInstitutions()) {
			log.info("Synchronizing institution: " + institutionDTO);

			InstitutionFullMyndighed stilInstitution = stilService.getInstitution(institutionDTO.getInstitutionNumber());
			if (stilInstitution == null) {
				log.warn("Got no institution data - skipping: " + institutionDTO);
				continue;
			}

			DBInstitution dbInstitution = institutionService.findByInstitutionNumber(stilInstitution.getInstitutionNumber());
			if (dbInstitution == null) {
				dbInstitution = new DBInstitution();
				dbInstitution.copyFields(stilInstitution);
				dbInstitution.setType(institutionDTO.getType());

				log.info("Creating new institution: " + stilInstitution.getInstitutionName() + " / " + stilInstitution.getInstitutionNumber());
				institutionService.save(dbInstitution);
			}
			else {
				boolean changes = false;

				if (dbInstitution.isDeleted()) {
					log.info("Undeleting institution: " + dbInstitution.getId() + " " + dbInstitution.getInstitutionName());
					dbInstitution.setDeleted(false);
					changes = true;
				}

				if (!dbInstitution.apiEquals(stilInstitution)) {
					dbInstitution.copyFields(stilInstitution);
					changes = true;
				}

				if (!dbInstitution.getType().equals(institutionDTO.getType())) {
					dbInstitution.setType(institutionDTO.getType());
					changes = true;
				}

				if (changes) {
					log.info("Updating core data on institution: " + stilInstitution.getInstitutionName() + " / " + stilInstitution.getInstitutionNumber());
					institutionService.save(dbInstitution);
				}
			}

			for (InstitutionPersonFullMyndighed stilInstitutionPerson : stilInstitution.getInstitutionPerson()) {
				DBInstitutionPerson dbInstitutionPerson = institutionPersonService.findByLocalPersonIdIncludingDeleted(stilInstitutionPerson.getLocalPersonId());

				if (dbInstitutionPerson == null) {
					log.info("Creating institutionPerson: " + stilInstitutionPerson.getLocalPersonId());

					dbInstitutionPerson = new DBInstitutionPerson();
					dbInstitutionPerson.copyFields(stilInstitutionPerson);
					dbInstitutionPerson.setInstitution(dbInstitution);
					institutionPersonService.save(dbInstitutionPerson);
				}
				else {
					boolean changes = false;

					if (dbInstitutionPerson.isDeleted()) {
						log.info("Undeleting institutionPerson: " + dbInstitutionPerson.getId() + " " + dbInstitutionPerson.getLocalPersonId());
						dbInstitutionPerson.setDeleted(false);
						changes = true;
					}

					if (!dbInstitutionPerson.apiEquals(stilInstitutionPerson)) {
						dbInstitutionPerson.copyFields(stilInstitutionPerson);
					}

					if (changes) {
						log.info("Updating institutionPerson: " + dbInstitutionPerson.getId() + " " + dbInstitutionPerson.getLocalPersonId());
						institutionPersonService.save(dbInstitutionPerson);
					}
				}
			}

			// find institution persons to be deleted (from within the same institution)
			List<DBInstitutionPerson> toBeDeleted = institutionPersonService.findByInstitution(dbInstitution).stream()
					.filter(dbInstitutionPerson -> stilInstitution.getInstitutionPerson().stream()
							.noneMatch(stilInstitutionPerson -> Objects.equals(stilInstitutionPerson.getLocalPersonId(), dbInstitutionPerson.getLocalPersonId())))
					.collect(Collectors.toList());

			toBeDeleted.forEach(person -> {
				log.info("Deleting institutionPerson: " + person.getId() + " " + person.getLocalPersonId());
				person.setDeleted(true);
			});

			if (toBeDeleted.size() > 0) {
				institutionPersonService.saveAll(toBeDeleted);
			}
		}
	}
}
