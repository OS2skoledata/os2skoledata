package dk.digitalidentity.os2skoledata.datatables.dao;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import dk.digitalidentity.os2skoledata.datatables.dao.model.GridModificationHistory;

public interface GridModificationHistoryDatatableDao extends DataTablesRepository<GridModificationHistory, Long> {

}