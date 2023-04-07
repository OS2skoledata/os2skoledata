
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GruppeLicens complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GruppeLicens"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gruppe" type="{https://unilogin.dk}Gruppe"/&gt;
 *         &lt;element name="licens" type="{https://unilogin.dk}Licens"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GruppeLicens", propOrder = {
    "gruppe",
    "licens"
})
public class GruppeLicens {

    @XmlElement(required = true)
    protected Gruppe gruppe;
    @XmlElement(required = true)
    protected Licens licens;

    /**
     * Gets the value of the gruppe property.
     * 
     * @return
     *     possible object is
     *     {@link Gruppe }
     *     
     */
    public Gruppe getGruppe() {
        return gruppe;
    }

    /**
     * Sets the value of the gruppe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gruppe }
     *     
     */
    public void setGruppe(Gruppe value) {
        this.gruppe = value;
    }

    /**
     * Gets the value of the licens property.
     * 
     * @return
     *     possible object is
     *     {@link Licens }
     *     
     */
    public Licens getLicens() {
        return licens;
    }

    /**
     * Sets the value of the licens property.
     * 
     * @param value
     *     allowed object is
     *     {@link Licens }
     *     
     */
    public void setLicens(Licens value) {
        this.licens = value;
    }

}
