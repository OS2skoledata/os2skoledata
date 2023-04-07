
package https.wsieksport_unilogin_dk.eksport.full;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.unilogin_dk.data.transitional.PersonFullBase;
import https.unilogin_dk.data.transitional.PhoneNumberProtectable;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.PersonFullMyndighed;


/**
 * <p>Java class for personFull complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="personFull"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data/transitional}personFullBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="HomePhoneNumber" type="{https://unilogin.dk/data/transitional}phoneNumberProtectable" minOccurs="0"/&gt;
 *         &lt;element name="WorkPhoneNumber" type="{https://unilogin.dk/data/transitional}phoneNumberProtectable" minOccurs="0"/&gt;
 *         &lt;element name="MobilePhoneNumber" type="{https://unilogin.dk/data/transitional}phoneNumberProtectable" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personFull", propOrder = {
    "homePhoneNumber",
    "workPhoneNumber",
    "mobilePhoneNumber"
})
@XmlSeeAlso({
    PersonFullMyndighed.class
})
public class PersonFull
    extends PersonFullBase
{

    @XmlElement(name = "HomePhoneNumber")
    protected PhoneNumberProtectable homePhoneNumber;
    @XmlElement(name = "WorkPhoneNumber")
    protected PhoneNumberProtectable workPhoneNumber;
    @XmlElement(name = "MobilePhoneNumber")
    protected PhoneNumberProtectable mobilePhoneNumber;

    /**
     * Gets the value of the homePhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    public PhoneNumberProtectable getHomePhoneNumber() {
        return homePhoneNumber;
    }

    /**
     * Sets the value of the homePhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    public void setHomePhoneNumber(PhoneNumberProtectable value) {
        this.homePhoneNumber = value;
    }

    /**
     * Gets the value of the workPhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    public PhoneNumberProtectable getWorkPhoneNumber() {
        return workPhoneNumber;
    }

    /**
     * Sets the value of the workPhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    public void setWorkPhoneNumber(PhoneNumberProtectable value) {
        this.workPhoneNumber = value;
    }

    /**
     * Gets the value of the mobilePhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    public PhoneNumberProtectable getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    /**
     * Sets the value of the mobilePhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    public void setMobilePhoneNumber(PhoneNumberProtectable value) {
        this.mobilePhoneNumber = value;
    }

}
