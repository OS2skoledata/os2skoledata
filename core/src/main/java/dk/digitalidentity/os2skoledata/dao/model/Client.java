package dk.digitalidentity.os2skoledata.dao.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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