
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Elev complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Elev"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rolle" type="{https://unilogin.dk}Elevrolle"/&gt;
 *         &lt;element name="hovedgruppeid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="hovedgruppenavn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="elevtrin" type="{https://unilogin.dk}Trin"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Elev", propOrder = {
    "rolle",
    "hovedgruppeid",
    "hovedgruppenavn",
    "elevtrin"
})
public class Elev {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Elevrolle rolle;
    @XmlElement(required = true)
    protected String hovedgruppeid;
    @XmlElement(required = true)
    protected String hovedgruppenavn;
    @XmlElement(required = true)
    protected String elevtrin;

    /**
     * Gets the value of the rolle property.
     * 
     * @return
     *     possible object is
     *     {@link Elevrolle }
     *     
     */
    public Elevrolle getRolle() {
        return rolle;
    }

    /**
     * Sets the value of the rolle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Elevrolle }
     *     
     */
    public void setRolle(Elevrolle value) {
        this.rolle = value;
    }

    /**
     * Gets the value of the hovedgruppeid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHovedgruppeid() {
        return hovedgruppeid;
    }

    /**
     * Sets the value of the hovedgruppeid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHovedgruppeid(String value) {
        this.hovedgruppeid = value;
    }

    /**
     * Gets the value of the hovedgruppenavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHovedgruppenavn() {
        return hovedgruppenavn;
    }

    /**
     * Sets the value of the hovedgruppenavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHovedgruppenavn(String value) {
        this.hovedgruppenavn = value;
    }

    /**
     * Gets the value of the elevtrin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElevtrin() {
        return elevtrin;
    }

    /**
     * Sets the value of the elevtrin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElevtrin(String value) {
        this.elevtrin = value;
    }

}
