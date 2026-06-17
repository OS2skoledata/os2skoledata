package dk.digitalidentity.os2skoledata.dao.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonFormat;

import dk.digitalidentity.os2skoledata.dao.model.enums.ClientAccessRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String name;

	@Column
	private String apiKey;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastFullSync;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastSeen;

	@Column
	private boolean paused;

	@Column
	@Enumerated(EnumType.STRING)
	private ClientAccessRole accessRole;

	@Column
	private boolean monitor;

	// Eager loading is important!
	@OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InstitutionModificationHistoryOffset> institutionModificationHistoryOffsets;

}