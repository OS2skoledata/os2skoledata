
package https.wsieksport_unilogin_dk.eksport.fullmyndighed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.unilogin_dk.data.transitional.UniLoginFull;
import https.wsieksport_unilogin_dk.eksport.ContactPersonExportBase;


/**
 * <p>Java class for contactPersonFullMyndighed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactPersonFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport}contactPersonExportBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Person" type="{https://wsieksport.unilogin.dk/eksport/fullmyndighed}personFullMyndighed"/&gt;
 *         &lt;element name="UNILogin" type="{https://unilogin.dk/data/transitional}uniLoginFull" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactPersonFullMyndighed", propOrder = {
    "person",
    "uniLogin"
})
public class ContactPersonFullMyndighed
    extends ContactPersonExportBase
{

    @XmlElement(name = "Person", required = true)
    protected PersonFullMyndighed person;
    @XmlElement(name = "UNILogin")
    protected UniLoginFull uniLogin;

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link PersonFullMyndighed }
     *     
     */
    public PersonFullMyndighed getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonFullMyndighed }
     *     
     */
    public void setPerson(PersonFullMyndighed value) {
        this.person = value;
    }

    /**
     * Gets the value of the uniLogin property.
     * 
     * @return
     *     possible object is
     *     {@link UniLoginFull }
     *     
     */
    public UniLoginFull getUNILogin() {
        return uniLogin;
    }

    /**
     * Sets the value of the uniLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link UniLoginFull }
     *     
     */
    public void setUNILogin(UniLoginFull value) {
        this.uniLogin = value;
    }

}
