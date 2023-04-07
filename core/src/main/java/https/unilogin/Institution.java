
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Institution complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Institution"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="instnr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="instnavn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="typenavn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="type3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="type3navn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="adresse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bynavn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="postnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="telefonnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="faxnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mailadresse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="www" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="hovedinstitutionsnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="kommunenr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="kommune" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="admkommunenr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="admkommune" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="regionsnr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="region" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Institution", propOrder = {
    "instnr",
    "instnavn",
    "type",
    "typenavn",
    "type3",
    "type3Navn",
    "adresse",
    "bynavn",
    "postnr",
    "telefonnr",
    "faxnr",
    "mailadresse",
    "www",
    "hovedinstitutionsnr",
    "kommunenr",
    "kommune",
    "admkommunenr",
    "admkommune",
    "regionsnr",
    "region"
})
public class Institution {

    @XmlElement(required = true)
    protected String instnr;
    protected String instnavn;
    protected String type;
    protected String typenavn;
    protected String type3;
    @XmlElement(name = "type3navn")
    protected String type3Navn;
    protected String adresse;
    protected String bynavn;
    protected String postnr;
    protected String telefonnr;
    protected String faxnr;
    protected String mailadresse;
    protected String www;
    protected String hovedinstitutionsnr;
    protected String kommunenr;
    protected String kommune;
    protected String admkommunenr;
    protected String admkommune;
    protected String regionsnr;
    protected String region;

    /**
     * Gets the value of the instnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstnr() {
        return instnr;
    }

    /**
     * Sets the value of the instnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstnr(String value) {
        this.instnr = value;
    }

    /**
     * Gets the value of the instnavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstnavn() {
        return instnavn;
    }

    /**
     * Sets the value of the instnavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstnavn(String value) {
        this.instnavn = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the typenavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypenavn() {
        return typenavn;
    }

    /**
     * Sets the value of the typenavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypenavn(String value) {
        this.typenavn = value;
    }

    /**
     * Gets the value of the type3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType3() {
        return type3;
    }

    /**
     * Sets the value of the type3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType3(String value) {
        this.type3 = value;
    }

    /**
     * Gets the value of the type3Navn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType3Navn() {
        return type3Navn;
    }

    /**
     * Sets the value of the type3Navn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType3Navn(String value) {
        this.type3Navn = value;
    }

    /**
     * Gets the value of the adresse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdresse() {
        return adresse;
    }

    /**
     * Sets the value of the adresse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdresse(String value) {
        this.adresse = value;
    }

    /**
     * Gets the value of the bynavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBynavn() {
        return bynavn;
    }

    /**
     * Sets the value of the bynavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBynavn(String value) {
        this.bynavn = value;
    }

    /**
     * Gets the value of the postnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostnr() {
        return postnr;
    }

    /**
     * Sets the value of the postnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostnr(String value) {
        this.postnr = value;
    }

    /**
     * Gets the value of the telefonnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefonnr() {
        return telefonnr;
    }

    /**
     * Sets the value of the telefonnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefonnr(String value) {
        this.telefonnr = value;
    }

    /**
     * Gets the value of the faxnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaxnr() {
        return faxnr;
    }

    /**
     * Sets the value of the faxnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaxnr(String value) {
        this.faxnr = value;
    }

    /**
     * Gets the value of the mailadresse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailadresse() {
        return mailadresse;
    }

    /**
     * Sets the value of the mailadresse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailadresse(String value) {
        this.mailadresse = value;
    }

    /**
     * Gets the value of the www property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWww() {
        return www;
    }

    /**
     * Sets the value of the www property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWww(String value) {
        this.www = value;
    }

    /**
     * Gets the value of the hovedinstitutionsnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHovedinstitutionsnr() {
        return hovedinstitutionsnr;
    }

    /**
     * Sets the value of the hovedinstitutionsnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHovedinstitutionsnr(String value) {
        this.hovedinstitutionsnr = value;
    }

    /**
     * Gets the value of the kommunenr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKommunenr() {
        return kommunenr;
    }

    /**
     * Sets the value of the kommunenr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKommunenr(String value) {
        this.kommunenr = value;
    }

    /**
     * Gets the value of the kommune property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKommune() {
        return kommune;
    }

    /**
     * Sets the value of the kommune property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKommune(String value) {
        this.kommune = value;
    }

    /**
     * Gets the value of the admkommunenr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmkommunenr() {
        return admkommunenr;
    }

    /**
     * Sets the value of the admkommunenr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmkommunenr(String value) {
        this.admkommunenr = value;
    }

    /**
     * Gets the value of the admkommune property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmkommune() {
        return admkommune;
    }

    /**
     * Sets the value of the admkommune property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmkommune(String value) {
        this.admkommune = value;
    }

    /**
     * Gets the value of the regionsnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionsnr() {
        return regionsnr;
    }

    /**
     * Sets the value of the regionsnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionsnr(String value) {
        this.regionsnr = value;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegion(String value) {
        this.region = value;
    }

}
