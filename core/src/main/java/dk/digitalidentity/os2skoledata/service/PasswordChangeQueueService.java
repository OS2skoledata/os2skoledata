package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.PasswordChangeQueueDao;
import dk.digitalidentity.os2skoledata.dao.model.PasswordChangeQueue;
import dk.digitalidentity.os2skoledata.dao.model.enums.ReplicationStatus;
import dk.digitalidentity.os2skoledata.service.model.ADPasswordResponse.ADPasswordStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class PasswordChangeQueueService {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private PasswordChangeQueueDao passwordChangeQueueDao;

	@Autowired
	private ADPasswordService adPasswordService;

	private SecretKeySpec secretKey;

	public PasswordChangeQueue save(PasswordChangeQueue passwordChangeQueue) {
		return save(passwordChangeQueue, true);
	}

	public PasswordChangeQueue save(PasswordChangeQueue passwordChangeQueue, boolean deleteOldEntries) {
		// if the user tries to change password multiple times in a row, we only want to keep the latest - this
		// removes any attempts in the queue that is not already synchronized (which we need to keep for debugging purposes)

		if (deleteOldEntries) {
			List<PasswordChangeQueue> oldQueued = passwordChangeQueueDao.findByUsernameAndStatusNot(passwordChangeQueue.getUsername(), ReplicationStatus.SYNCHRONIZED);
			if (oldQueued != null && oldQueued.size() > 0) {
				passwordChangeQueueDao.deleteAll(oldQueued);
			}
		}

		return passwordChangeQueueDao.save(passwordChangeQueue);
	}

	public ADPasswordStatus attemptPasswordChangeFromUI(String username, String newPassword) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
		PasswordChangeQueue change = new PasswordChangeQueue(username, encryptPassword(newPassword));

		ADPasswordStatus status = adPasswordService.attemptPasswordReplication(change, true);
		switch (status) {
			// inform user through UI (but also save result in queue for debugging purposes)
			case FAILURE:
			case TECHNICAL_ERROR:
			case INSUFFICIENT_PERMISSION:
				// FINAL_ERROR prevent any retries on this
				change.setStatus(ReplicationStatus.FINAL_ERROR);
				save(change);
				break;

			case NOOP:
				log.error("Got a NOOP case here - that should not happen");
				break;

			// save result - so it is correctly logged to the queue
			case OK:
				save(change);
				break;

			// delay replication in case of a timeout
			case TIMEOUT:
				save(change);
				break;
		}

		return status;
	}

	public String encryptPassword(String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
		SecretKeySpec key = getKey(configuration.getStudentAdministration().getPasswordSecret());
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00});
		cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

		return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes("UTF-8")));
	}

	public String decryptPassword(String encryptedPassword) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
		SecretKeySpec key = getKey(configuration.getStudentAdministration().getPasswordSecret());
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00});
		cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
		return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedPassword)));
	}

	public List<PasswordChangeQueue> getByStatus(ReplicationStatus status) {
		return passwordChangeQueueDao.findByStatus(status);
	}

	public void delete(PasswordChangeQueue passwordChangeQueue) {
		passwordChangeQueueDao.delete(passwordChangeQueue);
	}

	public List<PasswordChangeQueue> getUnsynchronized() {
		return passwordChangeQueueDao.findByStatusNotIn(ReplicationStatus.SYNCHRONIZED, ReplicationStatus.FINAL_ERROR, ReplicationStatus.DO_NOT_REPLICATE);
	}

	private SecretKeySpec getKey(String myKey) {
		if (secretKey != null) {
			return secretKey;
		}

		byte[] key;
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			log.error("Error in generating key", e);
		}

		return secretKey;
	}
}