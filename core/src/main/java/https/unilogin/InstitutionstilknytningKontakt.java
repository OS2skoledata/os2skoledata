
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InstitutionstilknytningKontakt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InstitutionstilknytningKontakt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rolle" type="{https://unilogin.dk}Kontaktpersonsrelation"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstitutionstilknytningKontakt", propOrder = {
    "rolle"
})
public class InstitutionstilknytningKontakt {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Kontaktpersonsrelation rolle;

    /**
     * Gets the value of the rolle property.
     * 
     * @return
     *     possible object is
     *     {@link Kontaktpersonsrelation }
     *     
     */
    public Kontaktpersonsrelation getRolle() {
        return rolle;
    }

    /**
     * Sets the value of the rolle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Kontaktpersonsrelation }
     *     
     */
    public void setRolle(Kontaktpersonsrelation value) {
        this.rolle = value;
    }

}
