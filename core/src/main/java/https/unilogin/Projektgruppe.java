
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Projektgruppe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Projektgruppe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ejernr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="projektgruppekode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="projektgruppenavn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fradato" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="tildato" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Projektgruppe", propOrder = {
    "ejernr",
    "projektgruppekode",
    "projektgruppenavn",
    "fradato",
    "tildato"
})
public class Projektgruppe {

    @XmlElement(required = true)
    protected String ejernr;
    @XmlElement(required = true)
    protected String projektgruppekode;
    @XmlElement(required = true)
    protected String projektgruppenavn;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fradato;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar tildato;

    /**
     * Gets the value of the ejernr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEjernr() {
        return ejernr;
    }

    /**
     * Sets the value of the ejernr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEjernr(String value) {
        this.ejernr = value;
    }

    /**
     * Gets the value of the projektgruppekode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjektgruppekode() {
        return projektgruppekode;
    }

    /**
     * Sets the value of the projektgruppekode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjektgruppekode(String value) {
        this.projektgruppekode = value;
    }

    /**
     * Gets the value of the projektgruppenavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjektgruppenavn() {
        return projektgruppenavn;
    }

    /**
     * Sets the value of the projektgruppenavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjektgruppenavn(String value) {
        this.projektgruppenavn = value;
    }

    /**
     * Gets the value of the fradato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFradato() {
        return fradato;
    }

    /**
     * Sets the value of the fradato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFradato(XMLGregorianCalendar value) {
        this.fradato = value;
    }

    /**
     * Gets the value of the tildato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTildato() {
        return tildato;
    }

    /**
     * Sets the value of the tildato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTildato(XMLGregorianCalendar value) {
        this.tildato = value;
    }

}
