package dk.digitalidentity.os2skoledata.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "settings")
@Getter
@Setter
public class Setting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "setting_key")
	private String key;

	@Column(name = "setting_value")
	private String value;
}
