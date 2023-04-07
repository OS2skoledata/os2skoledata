package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBCountryCode;
import https.unilogin_dk.data.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String country;
	
	@Column
	@Enumerated(EnumType.STRING)
	private DBCountryCode countryCode;

	@Column
	private String municipalityCode;
	
	@Column
	private String municipalityName;
	
	@Column
	private String postalCode;
	
	@Column
	private String postalDistrict;
	
	@Column
	private String streetAddress;

	public boolean apiEquals(Address address) {
		if (address == null) {
			return false;
		}

		if (!Objects.equals(this.country, address.getCountry())) {
			log.debug("DBAddress: Not equals on 'location' for " + this.id);
			return false;
		}

		if ((this.countryCode == null && address.getCountryCode() != null) ||
			(this.countryCode != null && address.getCountryCode() == null) ||
			(this.countryCode != null && address.getCountryCode() != null && !Objects.equals(this.countryCode.name(), address.getCountryCode().name()))) {

			log.debug("DBPerson: Not equals on 'countryCode' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.municipalityCode, address.getMunicipalityCode())) {
			log.debug("DBAddress: Not equals on 'municipalityCode' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.municipalityName, address.getMunicipalityName())) {
			log.debug("DBAddress: Not equals on 'municipalityName' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.postalCode, address.getPostalCode())) {
			log.debug("DBAddress: Not equals on 'postalCode' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.postalDistrict, address.getPostalDistrict())) {
			log.debug("DBAddress: Not equals on 'postalDistrict' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.streetAddress, address.getStreetAddress())) {
			log.debug("DBAddress: Not equals on 'streetAddress' for " + this.id);
			return false;
		}

		return true;
	}

	public void copyFields(Address address) {
		if (address == null) {
			return;
		}
		
		this.country = address.getCountry();
		
		if (address.getCountryCode() != null) {
			this.countryCode = DBCountryCode.from(address.getCountryCode());
		}
		else {
			this.countryCode = null;
		}
		
		this.municipalityCode = address.getMunicipalityCode();
		this.municipalityName = address.getMunicipalityName();
		this.postalCode = address.getPostalCode();
		this.postalDistrict = address.getPostalDistrict();
		this.streetAddress = address.getStreetAddress();
	}

}
