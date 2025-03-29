package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.PasswordSettingDao;
import dk.digitalidentity.os2skoledata.dao.model.PasswordSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.GradeGroup;
import dk.digitalidentity.os2skoledata.service.model.enums.ChangePasswordResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PasswordSettingService {

	private final String WORDS_1 = "guld,hvid,gul,lilla,brun,rosa,sort,pink,stor,lang,fin,glad,god,klog,kort,rig,sjov,tung,let,tynd,varm,kold,lille,flot,ung,sund,lyst,vred,smuk,bleg";
	private final String WORDS_2 = "hest,ko,bil,and,mus,kat,dyne,elg,fisk,tyr,mink,orm,emu,los,pony,due,bord,glas,kniv,krus,lys,stol,pude,skab,hus,bog,klods,cykel,skovl,ged,dino,flue,gris,hare,hane,hund,kalv,lama,laks,myre,puma,reje,snog,ugle,saks,gibs,prop,smed,bad,bed,pap,abe,haj,gnu,grib,fugl,ibis,kudu,okse,kiwi,tun,kryb,thor";
	private final String WORDS_3 = "2,3,4,5,6,7,8,9";
	private final Random random = new Random();

	@Autowired
	private PasswordSettingDao passwordSettingDao;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private PasswordChangeQueueService passwordChangeQueueService;

	public record IndskolingPasswordWords(List<String> wordsColumn1, String word1, List<String> wordsColumn2, String word2, List<String> wordsColumn3, String word3) {}
	public IndskolingPasswordWords getPasswordWords() {
		List<String> wordsColumn1 = Arrays.asList(WORDS_1.split(","));
		List<String> wordsColumn2 = Arrays.asList(WORDS_2.split(","));
		List<String> wordsColumn3 = Arrays.asList(WORDS_3.split(","));
		Collections.shuffle(wordsColumn1);
		Collections.shuffle(wordsColumn2);
		Collections.shuffle(wordsColumn3);
		return new IndskolingPasswordWords(wordsColumn1, wordsColumn1.get(random.nextInt(wordsColumn1.size())), wordsColumn2, wordsColumn2.get(random.nextInt(wordsColumn2.size())), wordsColumn3, wordsColumn3.get(random.nextInt(wordsColumn3.size())));
	}

	public String generateEncryptedPasswordForIndskolingStudent() {
		if (configuration.getStudentAdministration().isSetIndskolingPasswordOnCreate()) {
			List<String> wordsColumn1 = Arrays.asList(WORDS_1.split(","));
			List<String> wordsColumn2 = Arrays.asList(WORDS_2.split(","));
			List<String> wordsColumn3 = Arrays.asList(WORDS_3.split(","));
			Collections.shuffle(wordsColumn1);
			Collections.shuffle(wordsColumn2);
			Collections.shuffle(wordsColumn3);

			String pw = wordsColumn1.get(random.nextInt(wordsColumn1.size())) + wordsColumn2.get(random.nextInt(wordsColumn2.size())) + wordsColumn3.get(random.nextInt(wordsColumn3.size()));

			try {
				return passwordChangeQueueService.encryptPassword(pw);
			}
			catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException |
					BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
				log.error("Failed to encrypt generated indskoling password");
			}
		}
		return null;
	}

	public List<PasswordSetting> getAllPasswordSettings() {
		List<PasswordSetting> allSettings = new ArrayList<>();
		for (GradeGroup value : GradeGroup.values()) {
			allSettings.add(getSettings(value));
		}
		return allSettings;
	}

	public PasswordSetting getPasswordSettingsForUsername(String username) {
		return getSettings(findGradeGroup(username));
	}

	public PasswordSetting getSettings(GradeGroup gradeGroup) {
		if (gradeGroup == null) {
			return null;
		}

		PasswordSetting passwordSetting = passwordSettingDao.findByGradeGroup(gradeGroup);

		if (passwordSetting == null) {
			PasswordSetting settings = new PasswordSetting();
			settings.setMinLength(8L);
			settings.setRequireComplexPassword(false);
			settings.setGradeGroup(gradeGroup);
			return settings;
		}

		return passwordSetting;
	}

	public PasswordSetting save(PasswordSetting passwordSetting) {
		return passwordSettingDao.save(passwordSetting);
	}

	public ChangePasswordResult validate(String username, String password) {
		if (username == null || username.isEmpty()) {
			log.warn("username is null!");
			return ChangePasswordResult.TECHNICAL_MISSING_PERSON;
		}

		if (!StringUtils.hasLength(password)) {
			log.warn("Password was null or empty");
			return ChangePasswordResult.TOO_SHORT;
		}

		GradeGroup gradeGroup = findGradeGroup(username);
		if (gradeGroup == null) {
			log.warn("No level found for student " + username);
			return ChangePasswordResult.TECHNICAL_NO_LEVEL;
		}

		PasswordSetting settings = getSettings(gradeGroup);

		if (password.length() < settings.getMinLength()) {
			return ChangePasswordResult.TOO_SHORT;
		}

		if (settings.isRequireComplexPassword()) {
			int failures = 0;

			if (!Pattern.compile("[a-zæøå]").matcher(password).find()) {
				failures++;
			}

			if (!Pattern.compile("[A-ZÆØÅ]").matcher(password).find()) {
				failures++;
			}

			if (!Pattern.compile("\\d").matcher(password).find()) {
				failures++;
			}

			// check for existence of special characters, with the exception that the Danish letters ÆØÅ does not count
			// as special characters. Other characters from other languages WILL count as special characters
			if (!Pattern.compile("[^\\wæøå\\d]", Pattern.CASE_INSENSITIVE).matcher(password).find()) {
				failures++;
			}

			// only one missing rule is allowed here
			if (failures > 1) {
				return ChangePasswordResult.NOT_COMPLEX;
			}
		}

		return ChangePasswordResult.OK;
	}

	public GradeGroup findGradeGroup(String username) {
		Integer level = institutionPersonService.getLevel(username);
		if (level == null) {
			return null;
		}

		return switch (level) {
			case 0, 1, 2, 3 -> GradeGroup.YOUNGEST;
			case 4, 5, 6 -> GradeGroup.MIDDLE;
			case 7, 8, 9, 10 -> GradeGroup.OLDEST;
			default -> null;
		};
	}
}