
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GruppeMedAntal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GruppeMedAntal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gruppe" type="{https://unilogin.dk}Gruppe"/&gt;
 *         &lt;element name="antal" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GruppeMedAntal", propOrder = {
    "gruppe",
    "antal"
})
public class GruppeMedAntal {

    @XmlElement(required = true)
    protected Gruppe gruppe;
    protected int antal;

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
     * Gets the value of the antal property.
     * 
     */
    public int getAntal() {
        return antal;
    }

    /**
     * Sets the value of the antal property.
     * 
     */
    public void setAntal(int value) {
        this.antal = value;
    }

}
