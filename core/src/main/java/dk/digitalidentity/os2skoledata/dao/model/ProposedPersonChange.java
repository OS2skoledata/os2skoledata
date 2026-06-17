package dk.digitalidentity.os2skoledata.dao.model;

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

import dk.digitalidentity.os2skoledata.dao.model.enums.PersonChangeType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proposed_person_change")
@Getter
@Setter
@NoArgsConstructor
@BatchSize(size = 100)
public class ProposedPersonChange {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "change_proposal_id", nullable = false)
	private InstitutionChangeProposal changeProposal;

	@Enumerated(EnumType.STRING)
	@Column
	private PersonChangeType changeType;

	@Column
	private String firstName;

	@Column
	private String familyName;

	@Column
	private String uniLoginUserId;

	@Column
	private String personType;
}