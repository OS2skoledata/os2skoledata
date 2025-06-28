package dk.digitalidentity.os2skoledata.dao.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.util.DateUtils;
import https.unilogin_dk.data.Group;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
// TODO: rename this table, it is anoying to have to quote it on all SQL, including from command-line
@Table(name = "`group`")
@Slf4j
public class DBGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@LastModifiedDate
	private LocalDateTime lastModified;

	@Column
	private boolean deleted;

	@Column
	private LocalDate fromDate;
	
	@Column
	private LocalDate toDate;

	@Column
	private String groupId;
	
	@Column
	private String groupLevel;
	
	@Column
	private String groupName;
	
	@Column
	@Enumerated(EnumType.STRING)
	private DBImportGroupType groupType;
	
	@Column
	private String line;

	@Column
	private String googleWorkspaceId;

	@Column
	private String driveGoogleWorkspaceId;

	@Column
	private String azureSecurityGroupId;

	@Column
	private String azureTeamId;

	@Column
	private String groupGoogleWorkspaceEmail;

	@Column
	private String groupOnlyStudentsGoogleWorkspaceEmail;

	@Column(name = "current_year_gw_group_identifier")
	private String currentYearGWGroupIdentifier;

	@Column(name = "current_year_gw_folder_identifier")
	private String currentYearGWFolderIdentifier;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "institution_id")
	private DBInstitution institution;

	@NotAudited
	@BatchSize(size = 100)
	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GoogleWorkspaceClassFolderOrGroup> googleWorkspaceClassFoldersAndGroups;

	public boolean apiEquals(Group group) {
		if (group == null) {
			return false;
		}

		if (!Objects.equals(this.fromDate, DateUtils.fromXMLGregorianCalendar(group.getFromDate()))) {
			log.debug("DBGroup: Not equals on 'fromDate' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.toDate, DateUtils.fromXMLGregorianCalendar(group.getToDate()))) {
			log.debug("DBGroup: Not equals on 'toDate' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.groupId, group.getGroupId())) {
			log.debug("DBGroup: Not equals on 'groupId' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.groupLevel, group.getGroupLevel())) {
			log.debug("DBGroup: Not equals on 'groupLevel' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.groupName, group.getGroupName())) {
			log.debug("DBGroup: Not equals on 'groupName' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.line, group.getLine())) {
			log.debug("DBGroup: Not equals on 'line' for " + this.id);
			return false;
		}

		if ((this.groupType == null && group.getGroupType() != null) ||
			(this.groupType != null && group.getGroupType() == null) ||
			(!Objects.equals(this.groupType.name(), group.getGroupType().name()))) {
			
			log.debug("DBGroup: Not equals on 'groupType' for " + this.id);
			return false;
		}

		return true;
	}

	public void copyFields(Group group) {
		if (group == null) {
			return;
		}
		
		this.fromDate = DateUtils.fromXMLGregorianCalendar(group.getFromDate());
		this.toDate = DateUtils.fromXMLGregorianCalendar(group.getToDate());
		this.groupId = group.getGroupId();
		this.groupLevel = group.getGroupLevel();
		this.groupName = group.getGroupName();
		
		if (group.getGroupType() != null) {
			this.groupType = DBImportGroupType.from(group.getGroupType());
		}

		this.line = group.getLine();
	}
}
