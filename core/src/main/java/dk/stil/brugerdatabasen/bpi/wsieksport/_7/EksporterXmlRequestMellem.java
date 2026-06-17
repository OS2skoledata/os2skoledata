
package dk.stil.brugerdatabasen.bpi.wsieksport._7;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for EksporterXmlRequestMellem complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="EksporterXmlRequestMellem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="instnr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="filtre" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7}FiltreMellem" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EksporterXmlRequestMellem", propOrder = {
    "instnr",
    "filtre"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class EksporterXmlRequestMellem {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String instnr;
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected FiltreMellem filtre;

    /**
     * Gets the value of the instnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setInstnr(String value) {
        this.instnr = value;
    }

    /**
     * Gets the value of the filtre property.
     * 
     * @return
     *     possible object is
     *     {@link FiltreMellem }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public FiltreMellem getFiltre() {
        return filtre;
    }

    /**
     * Sets the value of the filtre property.
     * 
     * @param value
     *     allowed object is
     *     {@link FiltreMellem }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setFiltre(FiltreMellem value) {
        this.filtre = value;
    }

}
