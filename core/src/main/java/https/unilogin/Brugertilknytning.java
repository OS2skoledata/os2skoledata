
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Brugertilknytning complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Brugertilknytning"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="instnr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="brugerid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="navn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="elev" type="{https://unilogin.dk}BrugertilknytningElev"/&gt;
 *           &lt;element name="ansat" type="{https://unilogin.dk}BrugertilknytningAnsat"/&gt;
 *           &lt;element name="ekstern" type="{https://unilogin.dk}BrugertilknytningEkstern"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Brugertilknytning", propOrder = {
    "instnr",
    "brugerid",
    "navn",
    "elev",
    "ansat",
    "ekstern"
})
public class Brugertilknytning {

    @XmlElement(required = true)
    protected String instnr;
    @XmlElement(required = true)
    protected String brugerid;
    @XmlElement(required = true)
    protected String navn;
    protected BrugertilknytningElev elev;
    protected BrugertilknytningAnsat ansat;
    protected BrugertilknytningEkstern ekstern;

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
     * Gets the value of the elev property.
     * 
     * @return
     *     possible object is
     *     {@link BrugertilknytningElev }
     *     
     */
    public BrugertilknytningElev getElev() {
        return elev;
    }

    /**
     * Sets the value of the elev property.
     * 
     * @param value
     *     allowed object is
     *     {@link BrugertilknytningElev }
     *     
     */
    public void setElev(BrugertilknytningElev value) {
        this.elev = value;
    }

    /**
     * Gets the value of the ansat property.
     * 
     * @return
     *     possible object is
     *     {@link BrugertilknytningAnsat }
     *     
     */
    public BrugertilknytningAnsat getAnsat() {
        return ansat;
    }

    /**
     * Sets the value of the ansat property.
     * 
     * @param value
     *     allowed object is
     *     {@link BrugertilknytningAnsat }
     *     
     */
    public void setAnsat(BrugertilknytningAnsat value) {
        this.ansat = value;
    }

    /**
     * Gets the value of the ekstern property.
     * 
     * @return
     *     possible object is
     *     {@link BrugertilknytningEkstern }
     *     
     */
    public BrugertilknytningEkstern getEkstern() {
        return ekstern;
    }

    /**
     * Sets the value of the ekstern property.
     * 
     * @param value
     *     allowed object is
     *     {@link BrugertilknytningEkstern }
     *     
     */
    public void setEkstern(BrugertilknytningEkstern value) {
        this.ekstern = value;
    }

}
