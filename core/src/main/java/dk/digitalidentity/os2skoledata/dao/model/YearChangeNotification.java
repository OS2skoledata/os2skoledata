package dk.digitalidentity.os2skoledata.dao.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "year_change_notifications")
@Getter
@Setter
public class YearChangeNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String institutionNumber;

	@Column(nullable = false)
	private String institutionName;

	@Column(nullable = false)
	private LocalDate yearChangeDate;

	@Column(nullable = false)
	private LocalDateTime yearChangeTimestamp;

	@Column(nullable = false)
	private String oldSchoolYear;

	@Column(nullable = false)
	private String newSchoolYear;

	@Column(nullable = false)
	private boolean initialNotificationSent = false;

	@Column
	private LocalDateTime initialNotificationSentAt;

	@Column(nullable = false)
	private boolean reminderSent = false;

	@Column
	private LocalDateTime reminderSentAt;

	@Column(nullable = false)
	private boolean resolved = false;

	@Column
	private LocalDateTime resolvedAt;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof YearChangeNotification that)) return false;
		return Objects.equals(institutionNumber, that.institutionNumber) &&
				Objects.equals(yearChangeDate, that.yearChangeDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(institutionNumber, yearChangeDate);
	}
}