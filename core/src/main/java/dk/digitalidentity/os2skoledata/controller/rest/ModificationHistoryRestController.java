package dk.digitalidentity.os2skoledata.controller.rest;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;
import javax.validation.Valid;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.InstitutionModificationHistoryOffset;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.datatables.dao.GridModificationHistoryDatatableDao;
import dk.digitalidentity.os2skoledata.datatables.dao.model.GridModificationHistory;
import dk.digitalidentity.os2skoledata.service.ClientService;
import lombok.extern.slf4j.Slf4j;

@RequireAdministratorRole
@Slf4j
@RestController
public class ModificationHistoryRestController {

	@Autowired
	private GridModificationHistoryDatatableDao modificationHistoryDatatableDao;

	@Autowired
	private ClientService clientService;

	@Autowired
	private InstitutionService institutionService;

	@SuppressWarnings("unchecked")
	@PostMapping("/rest/modificationhistory/list/{clientId}")
	public DataTablesOutput<GridModificationHistory> list(@Valid @RequestBody DataTablesInput input, @PathVariable("clientId") long clientId, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			DataTablesOutput<GridModificationHistory> error = new DataTablesOutput<>();
			error.setError(bindingResult.toString());

			return error;
		}

		Client client = clientService.findById(clientId);
		if (client == null) {
			// log a warning for clientIds except 0 that we use to initialize empty datatable
			if (clientId != 0) {
				log.warn("Client for id: " + clientId + " not found.");
			}
			return new DataTablesOutput<>();
		}

		List<DBInstitution> institutions = institutionService.findAll();
		Specification<GridModificationHistory> specification = (Specification<GridModificationHistory>) (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			for (DBInstitution institution : institutions) {
				InstitutionModificationHistoryOffset match = client.getInstitutionModificationHistoryOffsets().stream().filter(i -> i.getInstitution().getId() == institution.getId()).findAny().orElse(null);
				long offset = 0;
				if (match != null) {
					offset = match.getModificationHistoryOffset();
				}

				Predicate predicateForId = criteriaBuilder.equal(root.get("institutionId"), institution.getId());
				Predicate predicateForOffset = criteriaBuilder.gt(root.get("id").as(Long.class), offset);
				Predicate predicateForInstitution = criteriaBuilder.and(predicateForId, predicateForOffset);
				predicates.add(predicateForInstitution);
			}

			Predicate finalPredicate = criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()]));

			return finalPredicate;
		};


		DataTablesOutput<?> result = modificationHistoryDatatableDao.findAll(input, specification);


		DataTablesOutput<GridModificationHistory> output = new DataTablesOutput<>();
		output.setDraw(result.getDraw());
		output.setRecordsFiltered(result.getRecordsFiltered());
		output.setRecordsTotal(result.getRecordsTotal());
		output.setError(result.getError());
		output.setData((List<GridModificationHistory>) result.getData());

		return output;
	}
}
