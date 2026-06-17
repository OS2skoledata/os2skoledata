package dk.digitalidentity.os2skoledata.dao.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.IntegrationType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Audited
@Entity
@Table(name="institution_group_identifier_mapping")
@Getter
@Setter
public class InstitutionGroupIdentifierMapping {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "institution_id")
	private DBInstitution institution;

	@Column
	private String groupKey;

	@Column
	private String groupIdentifier;

	@Column
	@Enumerated(EnumType.STRING)
	private IntegrationType integrationType;
}
