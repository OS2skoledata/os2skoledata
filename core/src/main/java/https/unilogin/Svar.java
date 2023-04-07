
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Svar complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Svar"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="reskode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="restekst" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Svar", propOrder = {
    "reskode",
    "restekst"
})
public class Svar {

    @XmlElement(required = true)
    protected String reskode;
    @XmlElement(required = true)
    protected String restekst;

    /**
     * Gets the value of the reskode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReskode() {
        return reskode;
    }

    /**
     * Sets the value of the reskode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReskode(String value) {
        this.reskode = value;
    }

    /**
     * Gets the value of the restekst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestekst() {
        return restekst;
    }

    /**
     * Sets the value of the restekst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestekst(String value) {
        this.restekst = value;
    }

}
