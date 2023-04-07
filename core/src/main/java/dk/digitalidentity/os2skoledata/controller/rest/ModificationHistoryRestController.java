package dk.digitalidentity.os2skoledata.controller.rest;

import java.util.List;

import javax.validation.Valid;

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

@Slf4j
@RestController
public class ModificationHistoryRestController {

	@Autowired
	private GridModificationHistoryDatatableDao modificationHistoryDatatableDao;

	@Autowired
	private ClientService clientService;

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

		long offset = client.getModificationHistoryOffset();
		
		DataTablesOutput<?> result = modificationHistoryDatatableDao.findAll(input, getByIdGreaterThan(offset));

		DataTablesOutput<GridModificationHistory> output = new DataTablesOutput<>();
		output.setDraw(result.getDraw());
		output.setRecordsFiltered(result.getRecordsFiltered());
		output.setRecordsTotal(result.getRecordsTotal());
		output.setError(result.getError());
		output.setData((List<GridModificationHistory>) result.getData());

		return output;
	}
	
	private Specification<GridModificationHistory> getByIdGreaterThan(long offset) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.gt(root.get("id").as(Long.class), offset);
	}
}
