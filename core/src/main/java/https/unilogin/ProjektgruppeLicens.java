
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProjektgruppeLicens complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProjektgruppeLicens"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="projektgruppe" type="{https://unilogin.dk}Projektgruppe"/&gt;
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
@XmlType(name = "ProjektgruppeLicens", propOrder = {
    "projektgruppe",
    "licens"
})
public class ProjektgruppeLicens {

    @XmlElement(required = true)
    protected Projektgruppe projektgruppe;
    @XmlElement(required = true)
    protected Licens licens;

    /**
     * Gets the value of the projektgruppe property.
     * 
     * @return
     *     possible object is
     *     {@link Projektgruppe }
     *     
     */
    public Projektgruppe getProjektgruppe() {
        return projektgruppe;
    }

    /**
     * Sets the value of the projektgruppe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Projektgruppe }
     *     
     */
    public void setProjektgruppe(Projektgruppe value) {
        this.projektgruppe = value;
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
