
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Institutionstilknytning complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Institutionstilknytning"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="instnr" type="{https://unilogin.dk}Regnr"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="elev" type="{https://unilogin.dk}InstitutionstilknytningElev"/&gt;
 *           &lt;element name="ansat" type="{https://unilogin.dk}InstitutionstilknytningAnsat"/&gt;
 *           &lt;element name="ekstern" type="{https://unilogin.dk}InstitutionstilknytningEkstern"/&gt;
 *           &lt;element name="kontakt" type="{https://unilogin.dk}InstitutionstilknytningKontakt"/&gt;
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
@XmlType(name = "Institutionstilknytning", propOrder = {
    "instnr",
    "elev",
    "ansat",
    "ekstern",
    "kontakt"
})
public class Institutionstilknytning {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String instnr;
    protected InstitutionstilknytningElev elev;
    protected InstitutionstilknytningAnsat ansat;
    protected InstitutionstilknytningEkstern ekstern;
    protected InstitutionstilknytningKontakt kontakt;

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
     * Gets the value of the elev property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionstilknytningElev }
     *     
     */
    public InstitutionstilknytningElev getElev() {
        return elev;
    }

    /**
     * Sets the value of the elev property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstitutionstilknytningElev }
     *     
     */
    public void setElev(InstitutionstilknytningElev value) {
        this.elev = value;
    }

    /**
     * Gets the value of the ansat property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionstilknytningAnsat }
     *     
     */
    public InstitutionstilknytningAnsat getAnsat() {
        return ansat;
    }

    /**
     * Sets the value of the ansat property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstitutionstilknytningAnsat }
     *     
     */
    public void setAnsat(InstitutionstilknytningAnsat value) {
        this.ansat = value;
    }

    /**
     * Gets the value of the ekstern property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionstilknytningEkstern }
     *     
     */
    public InstitutionstilknytningEkstern getEkstern() {
        return ekstern;
    }

    /**
     * Sets the value of the ekstern property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstitutionstilknytningEkstern }
     *     
     */
    public void setEkstern(InstitutionstilknytningEkstern value) {
        this.ekstern = value;
    }

    /**
     * Gets the value of the kontakt property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionstilknytningKontakt }
     *     
     */
    public InstitutionstilknytningKontakt getKontakt() {
        return kontakt;
    }

    /**
     * Sets the value of the kontakt property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstitutionstilknytningKontakt }
     *     
     */
    public void setKontakt(InstitutionstilknytningKontakt value) {
        this.kontakt = value;
    }

}
