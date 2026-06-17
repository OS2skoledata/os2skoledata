package dk.digitalidentity.os2skoledata.dao.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.FolderOrGroup;
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
import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "google_workspace_class_folder")
public class GoogleWorkspaceClassFolderOrGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private LocalDate created;

	@Column(nullable = false)
	private int level;

	@Column(nullable = false)
	private String googleWorkspaceId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private FolderOrGroup type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private DBGroup group;

}
