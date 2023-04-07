
package https.unilogin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Instbruger complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Instbruger"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="instnr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="brugerid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="navn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="elev" type="{https://unilogin.dk}Elev"/&gt;
 *           &lt;element name="ansat" type="{https://unilogin.dk}Ansat"/&gt;
 *           &lt;element name="ekstern" type="{https://unilogin.dk}Ekstern"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="gruppe" type="{https://unilogin.dk}Gruppe" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Instbruger", propOrder = {
    "instnr",
    "brugerid",
    "navn",
    "elev",
    "ansat",
    "ekstern",
    "gruppe"
})
public class Instbruger {

    @XmlElement(required = true)
    protected String instnr;
    @XmlElement(required = true)
    protected String brugerid;
    @XmlElement(required = true)
    protected String navn;
    protected Elev elev;
    protected Ansat ansat;
    protected Ekstern ekstern;
    protected List<Gruppe> gruppe;

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
     *     {@link Elev }
     *     
     */
    public Elev getElev() {
        return elev;
    }

    /**
     * Sets the value of the elev property.
     * 
     * @param value
     *     allowed object is
     *     {@link Elev }
     *     
     */
    public void setElev(Elev value) {
        this.elev = value;
    }

    /**
     * Gets the value of the ansat property.
     * 
     * @return
     *     possible object is
     *     {@link Ansat }
     *     
     */
    public Ansat getAnsat() {
        return ansat;
    }

    /**
     * Sets the value of the ansat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Ansat }
     *     
     */
    public void setAnsat(Ansat value) {
        this.ansat = value;
    }

    /**
     * Gets the value of the ekstern property.
     * 
     * @return
     *     possible object is
     *     {@link Ekstern }
     *     
     */
    public Ekstern getEkstern() {
        return ekstern;
    }

    /**
     * Sets the value of the ekstern property.
     * 
     * @param value
     *     allowed object is
     *     {@link Ekstern }
     *     
     */
    public void setEkstern(Ekstern value) {
        this.ekstern = value;
    }

    /**
     * Gets the value of the gruppe property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gruppe property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGruppe().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Gruppe }
     * 
     * 
     */
    public List<Gruppe> getGruppe() {
        if (gruppe == null) {
            gruppe = new ArrayList<Gruppe>();
        }
        return this.gruppe;
    }

}
