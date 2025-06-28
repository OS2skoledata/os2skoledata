package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.GoogleWorkspaceClassFolderOrGroupDao;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.GoogleWorkspaceClassFolderOrGroup;
import dk.digitalidentity.os2skoledata.dao.model.enums.FolderOrGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GoogleWorkspaceClassFolderOrGroupService {

	@Autowired
	private GoogleWorkspaceClassFolderOrGroupDao googleWorkspaceClassFolderOrGroupDao;

	public List<GoogleWorkspaceClassFolderOrGroup> getGWClassFoldersAndGroupsForDeletion() {
		List<GoogleWorkspaceClassFolderOrGroup> result = new ArrayList<>();
		int currentYear = LocalDate.now().getYear();

		for (GoogleWorkspaceClassFolderOrGroup folder : getAll()) {
			int level = folder.getLevel();
			if (level < 0 || level > 10) {
				log.error("Detected invalid level: " + folder.getLevel() + " for googleWorkspaceClassFolder with id: " + folder.getId());
				continue;
			}

			int folderYear = folder.getCreated().getYear();
			int age = currentYear - folderYear;
			int threshold = 11 - level;

			if (age >= threshold) {
				result.add(folder);
			}
		}

		return result;
	}

	public void deleteGWClassFoldersAndGroupsPendingDeletion() {
		googleWorkspaceClassFolderOrGroupDao.deleteAll(getGWClassFoldersAndGroupsForDeletion());
	}

	public List<GoogleWorkspaceClassFolderOrGroup> getAll() {
		return googleWorkspaceClassFolderOrGroupDao.findAll();
	}

	public void create(FolderOrGroup folderOrGroup, DBGroup group, String value) {
		GoogleWorkspaceClassFolderOrGroup googleWorkspaceClassFolderOrGroup = new GoogleWorkspaceClassFolderOrGroup();
		googleWorkspaceClassFolderOrGroup.setCreated(LocalDate.now());
		googleWorkspaceClassFolderOrGroup.setType(folderOrGroup);
		googleWorkspaceClassFolderOrGroup.setGoogleWorkspaceId(value);
		googleWorkspaceClassFolderOrGroup.setGroup(group);
		googleWorkspaceClassFolderOrGroup.setLevel(getLevel(group));
		googleWorkspaceClassFolderOrGroupDao.save(googleWorkspaceClassFolderOrGroup);
	}

	private int getLevel(DBGroup group) {
		try {
			int level = Integer.parseInt(group.getGroupLevel());
			return level;
		} catch (Exception e) {
			return 0;
		}
	}
}
