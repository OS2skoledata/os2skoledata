
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProjektgruppeMedAntal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProjektgruppeMedAntal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="projektGruppe" type="{https://unilogin.dk}Projektgruppe"/&gt;
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
@XmlType(name = "ProjektgruppeMedAntal", propOrder = {
    "projektGruppe",
    "antal"
})
public class ProjektgruppeMedAntal {

    @XmlElement(required = true)
    protected Projektgruppe projektGruppe;
    protected int antal;

    /**
     * Gets the value of the projektGruppe property.
     * 
     * @return
     *     possible object is
     *     {@link Projektgruppe }
     *     
     */
    public Projektgruppe getProjektGruppe() {
        return projektGruppe;
    }

    /**
     * Sets the value of the projektGruppe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Projektgruppe }
     *     
     */
    public void setProjektGruppe(Projektgruppe value) {
        this.projektGruppe = value;
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
