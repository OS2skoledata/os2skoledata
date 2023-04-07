
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Licens complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Licens"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tjeneste" type="{https://unilogin.dk}Tjeneste"/&gt;
 *         &lt;element name="fraDato" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="tilDato" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Licens", propOrder = {
    "tjeneste",
    "fraDato",
    "tilDato"
})
public class Licens {

    @XmlElement(required = true)
    protected Tjeneste tjeneste;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fraDato;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar tilDato;

    /**
     * Gets the value of the tjeneste property.
     * 
     * @return
     *     possible object is
     *     {@link Tjeneste }
     *     
     */
    public Tjeneste getTjeneste() {
        return tjeneste;
    }

    /**
     * Sets the value of the tjeneste property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tjeneste }
     *     
     */
    public void setTjeneste(Tjeneste value) {
        this.tjeneste = value;
    }

    /**
     * Gets the value of the fraDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFraDato() {
        return fraDato;
    }

    /**
     * Sets the value of the fraDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFraDato(XMLGregorianCalendar value) {
        this.fraDato = value;
    }

    /**
     * Gets the value of the tilDato property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTilDato() {
        return tilDato;
    }

    /**
     * Sets the value of the tilDato property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTilDato(XMLGregorianCalendar value) {
        this.tilDato = value;
    }

}
