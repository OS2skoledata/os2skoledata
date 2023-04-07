
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SvarResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SvarResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="svar" type="{https://unilogin.dk}Svar"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SvarResponse", propOrder = {
    "svar"
})
public class SvarResponse {

    @XmlElement(required = true)
    protected Svar svar;

    /**
     * Gets the value of the svar property.
     * 
     * @return
     *     possible object is
     *     {@link Svar }
     *     
     */
    public Svar getSvar() {
        return svar;
    }

    /**
     * Sets the value of the svar property.
     * 
     * @param value
     *     allowed object is
     *     {@link Svar }
     *     
     */
    public void setSvar(Svar value) {
        this.svar = value;
    }

}
