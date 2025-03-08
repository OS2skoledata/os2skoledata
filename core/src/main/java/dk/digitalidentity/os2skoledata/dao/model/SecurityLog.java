package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SecurityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private Date tts;

	@Column(nullable = false)
	private String clientId;
	
	@Column(nullable = false)
	private String clientname;

	@Column(nullable = false)
	private String method;

	@Column(nullable = false)
	private String request;

	@Column(nullable = false)
	private String ipAddress;
	
	@Column
	private int status;

	@Column
	private long processedTime;
}
