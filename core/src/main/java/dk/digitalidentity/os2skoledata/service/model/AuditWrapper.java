package dk.digitalidentity.os2skoledata.service.model;
import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditWrapper {
	private long id;
	private EventType changeType;
	private long rev;

	public AuditWrapper(ResultSet rs) throws SQLException {
		id = rs.getLong("id");
		this.rev = rs.getLong("rev");
		String revType = rs.getString("revtype");

		switch (revType) {
			case "0":
				changeType = EventType.CREATE;
				break;
			case "1":
				changeType = EventType.UPDATE;
				break;
			case "2":
				changeType = EventType.DELETE;
				break;
			default:
				throw new SQLException("Error occured while parsing revType.");
		}
	}
}
