package dk.digitalidentity.os2skoledata.dao.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import https.unilogin_dk.data.Extern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "extern")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBExtern {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToMany(mappedBy = "extern", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DBExternGroupId> groupIds;

	@Column
	@Enumerated(EnumType.STRING)
	private DBExternalRoleType role;

	public boolean apiEquals(Extern extern) {
		if (extern == null) {
			return false;
		}
		
		if (!Objects.equals(this.getRole().toString(), extern.getRole().toString())) {
			log.debug("DBExtern: Not equals on 'role' for " + this.id);
			return false;
		}

		if (this.groupIds.size() != extern.getGroupId().size()) {
			log.debug("DBExtern: Not equals on 'groupIds' for " + this.id + " different collection size.");
			return false;
		}
		else {
			List<String> dbGroupIds = this.groupIds.stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			List<String> stilGroupIds = extern.getGroupId();
			
			Collections.sort(dbGroupIds);
			Collections.sort(stilGroupIds);
			
			if (!dbGroupIds.equals(stilGroupIds)) {
				log.debug("DBExtern: Not equals on 'groupIds' for " + this.id + " different collection values.");
				return false;
			}
		}

		return true;
	}

	public void copyFields(Extern extern) {
		if (extern == null) {
			return;
		}

		if (extern.getRole() != null) {
			this.role = DBExternalRoleType.from(extern.getRole());
		}

		// GroupIds
		if (this.groupIds == null && extern.getGroupId()!= null) {
			this.groupIds = new ArrayList<>();
			
			for (String groupId : extern.getGroupId()) {
				DBExternGroupId dbExternGroupId = new DBExternGroupId();
				dbExternGroupId.setGroupId(groupId);
				dbExternGroupId.setExtern(this);
				this.groupIds.add(dbExternGroupId);
			}
		}
		else if (this.groupIds != null && extern.getGroupId() != null) {
			List<String> existingGroupIds = this.groupIds.stream().map(g -> g.getGroupId()).collect(Collectors.toList());
			List<String> stilGroupIds = extern.getGroupId();
			
			for (String groupId : stilGroupIds) {
				if (!existingGroupIds.contains(groupId)) {
					DBExternGroupId newGroupIdMapping = new DBExternGroupId();
					newGroupIdMapping.setExtern(this);
					newGroupIdMapping.setGroupId(groupId);
					this.groupIds.add(newGroupIdMapping);
				}
			}
			
			for (String groupId : existingGroupIds) {
				if (!stilGroupIds.contains(groupId)) {
					this.groupIds.removeIf(r -> r.getGroupId().equals(groupId));
				}
			}
		}
		else if (this.groupIds != null && extern.getGroupId() == null) {
			this.groupIds.clear();
		}
	}
}
