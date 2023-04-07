
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Elevbruger complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Elevbruger"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rolle" type="{https://unilogin.dk}Elevrolle"/&gt;
 *         &lt;element name="instnr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="brugerid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="navn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hovedgruppe" type="{https://unilogin.dk}Gruppe"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Elevbruger", propOrder = {
    "rolle",
    "instnr",
    "brugerid",
    "navn",
    "hovedgruppe"
})
public class Elevbruger {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Elevrolle rolle;
    @XmlElement(required = true)
    protected String instnr;
    @XmlElement(required = true)
    protected String brugerid;
    @XmlElement(required = true)
    protected String navn;
    @XmlElement(required = true)
    protected Gruppe hovedgruppe;

    /**
     * Gets the value of the rolle property.
     * 
     * @return
     *     possible object is
     *     {@link Elevrolle }
     *     
     */
    public Elevrolle getRolle() {
        return rolle;
    }

    /**
     * Sets the value of the rolle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Elevrolle }
     *     
     */
    public void setRolle(Elevrolle value) {
        this.rolle = value;
    }

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
     * Gets the value of the hovedgruppe property.
     * 
     * @return
     *     possible object is
     *     {@link Gruppe }
     *     
     */
    public Gruppe getHovedgruppe() {
        return hovedgruppe;
    }

    /**
     * Sets the value of the hovedgruppe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gruppe }
     *     
     */
    public void setHovedgruppe(Gruppe value) {
        this.hovedgruppe = value;
    }

}
