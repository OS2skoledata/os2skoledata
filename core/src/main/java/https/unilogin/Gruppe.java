
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Gruppe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Gruppe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="instnr" type="{https://unilogin.dk}Regnr"/&gt;
 *         &lt;element name="gruppeid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="gruppenavn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="gruppetype" type="{https://unilogin.dk}Gruppetype"/&gt;
 *         &lt;element name="gruppetrin" type="{https://unilogin.dk}Trin" minOccurs="0"/&gt;
 *         &lt;element name="fradato" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="tildato" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Gruppe", propOrder = {
    "instnr",
    "gruppeid",
    "gruppenavn",
    "gruppetype",
    "gruppetrin",
    "fradato",
    "tildato"
})
public class Gruppe {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String instnr;
    @XmlElement(required = true)
    protected String gruppeid;
    @XmlElement(required = true)
    protected String gruppenavn;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Gruppetype gruppetype;
    protected String gruppetrin;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fradato;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar tildato;

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
     * Gets the value of the gruppeid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGruppeid() {
        return gruppeid;
    }

    /**
     * Sets the value of the gruppeid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGruppeid(String value) {
        this.gruppeid = value;
    }

    /**
     * Gets the value of the gruppenavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGruppenavn() {
        return gruppenavn;
    }

    /**
     * Sets the value of the gruppenavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGruppenavn(String value) {
        this.gruppenavn = value;
    }

    /**
     * Gets the value of the gruppetype property.
     * 
     * @return
     *     possible object is
     *     {@link Gruppetype }
     *     
     */
    public Gruppetype getGruppetype() {
        return gruppetype;
    }

    /**
     * Sets the value of the gruppetype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gruppetype }
     *     
     */
    public void setGruppetype(Gruppetype value) {
        this.gruppetype = value;
    }

    /**
     * Gets the value of the gruppetrin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGruppetrin() {
        return gruppetrin;
    }

    /**
     * Sets the value of the gruppetrin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGruppetrin(String value) {
        this.gruppetrin = value;
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
