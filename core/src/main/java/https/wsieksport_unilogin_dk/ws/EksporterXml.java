
package https.wsieksport_unilogin_dk.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.unilogin.Credentials;


/**
 * <p>Java class for EksporterXml complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EksporterXml"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk}Credentials"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="instnr" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EksporterXml", propOrder = {
    "instnr"
})
public class EksporterXml
    extends Credentials
{

    @XmlElement(required = true)
    protected String instnr;

    /**
     * Gets the value of the instnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
    public void setInstnr(String value) {
        this.instnr = value;
    }

}
