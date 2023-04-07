
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Serie complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Serie"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="seriekode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="serienavn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Serie", propOrder = {
    "seriekode",
    "serienavn"
})
public class Serie {

    @XmlElement(required = true)
    protected String seriekode;
    @XmlElement(required = true)
    protected String serienavn;

    /**
     * Gets the value of the seriekode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeriekode() {
        return seriekode;
    }

    /**
     * Sets the value of the seriekode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeriekode(String value) {
        this.seriekode = value;
    }

    /**
     * Gets the value of the serienavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerienavn() {
        return serienavn;
    }

    /**
     * Sets the value of the serienavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerienavn(String value) {
        this.serienavn = value;
    }

}
