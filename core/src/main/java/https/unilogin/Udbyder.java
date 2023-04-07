
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Udbyder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Udbyder"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="udbydernr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="udbydernavn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Udbyder", propOrder = {
    "udbydernr",
    "udbydernavn"
})
public class Udbyder {

    @XmlElement(required = true)
    protected String udbydernr;
    @XmlElement(required = true)
    protected String udbydernavn;

    /**
     * Gets the value of the udbydernr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUdbydernr() {
        return udbydernr;
    }

    /**
     * Sets the value of the udbydernr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUdbydernr(String value) {
        this.udbydernr = value;
    }

    /**
     * Gets the value of the udbydernavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUdbydernavn() {
        return udbydernavn;
    }

    /**
     * Sets the value of the udbydernavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUdbydernavn(String value) {
        this.udbydernavn = value;
    }

}
