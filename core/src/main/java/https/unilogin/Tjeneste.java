
package https.unilogin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Tjeneste complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Tjeneste"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="udbydernr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="serienavn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="seriekode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tjenestekode" type="{https://unilogin.dk}Token75char"/&gt;
 *         &lt;element name="tjenestenavn" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="matplatid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tjeneste", propOrder = {
    "udbydernr",
    "serienavn",
    "seriekode",
    "tjenestekode",
    "tjenestenavn",
    "url",
    "matplatid"
})
public class Tjeneste {

    @XmlElement(required = true)
    protected String udbydernr;
    @XmlElement(required = true)
    protected String serienavn;
    @XmlElement(required = true)
    protected String seriekode;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String tjenestekode;
    @XmlElement(required = true)
    protected String tjenestenavn;
    @XmlElement(required = true)
    protected String url;
    protected String matplatid;

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
     * Gets the value of the tjenestekode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTjenestekode() {
        return tjenestekode;
    }

    /**
     * Sets the value of the tjenestekode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTjenestekode(String value) {
        this.tjenestekode = value;
    }

    /**
     * Gets the value of the tjenestenavn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTjenestenavn() {
        return tjenestenavn;
    }

    /**
     * Sets the value of the tjenestenavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTjenestenavn(String value) {
        this.tjenestenavn = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the matplatid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatplatid() {
        return matplatid;
    }

    /**
     * Sets the value of the matplatid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatplatid(String value) {
        this.matplatid = value;
    }

}
