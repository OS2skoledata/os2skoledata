
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Kontaktperson complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Kontaktperson"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="instnr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="brugerid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="navn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="relation" type="{https://unilogin.dk}Kontaktpersonsrelation"/&gt;
 *         &lt;element name="myndighed" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="verifikation" type="{https://unilogin.dk}Verifikationsniveau"/&gt;
 *         &lt;element name="adgangsniveau" type="{https://unilogin.dk}KontaktpersonAdgangsniveau"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Kontaktperson", propOrder = {
    "instnr",
    "brugerid",
    "navn",
    "relation",
    "myndighed",
    "verifikation",
    "adgangsniveau"
})
public class Kontaktperson {

    @XmlElement(required = true)
    protected String instnr;
    @XmlElement(required = true)
    protected String brugerid;
    @XmlElement(required = true)
    protected String navn;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Kontaktpersonsrelation relation;
    protected boolean myndighed;
    protected int verifikation;
    protected int adgangsniveau;

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
     * Gets the value of the brugerid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrugerid() {
        return brugerid;
    }

    /**
     * Sets the value of the brugerid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrugerid(String value) {
        this.brugerid = value;
    }

    /**
     * Gets the value of the navn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNavn() {
        return navn;
    }

    /**
     * Sets the value of the navn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNavn(String value) {
        this.navn = value;
    }

    /**
     * Gets the value of the relation property.
     * 
     * @return
     *     possible object is
     *     {@link Kontaktpersonsrelation }
     *     
     */
    public Kontaktpersonsrelation getRelation() {
        return relation;
    }

    /**
     * Sets the value of the relation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Kontaktpersonsrelation }
     *     
     */
    public void setRelation(Kontaktpersonsrelation value) {
        this.relation = value;
    }

    /**
     * Gets the value of the myndighed property.
     * 
     */
    public boolean isMyndighed() {
        return myndighed;
    }

    /**
     * Sets the value of the myndighed property.
     * 
     */
    public void setMyndighed(boolean value) {
        this.myndighed = value;
    }

    /**
     * Gets the value of the verifikation property.
     * 
     */
    public int getVerifikation() {
        return verifikation;
    }

    /**
     * Sets the value of the verifikation property.
     * 
     */
    public void setVerifikation(int value) {
        this.verifikation = value;
    }

    /**
     * Gets the value of the adgangsniveau property.
     * 
     */
    public int getAdgangsniveau() {
        return adgangsniveau;
    }

    /**
     * Sets the value of the adgangsniveau property.
     * 
     */
    public void setAdgangsniveau(int value) {
        this.adgangsniveau = value;
    }

}
