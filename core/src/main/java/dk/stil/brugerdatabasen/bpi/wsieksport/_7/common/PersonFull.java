
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed.PersonFullMyndighed;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for personFull complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="personFull"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}personMedium"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}Address" minOccurs="0"/&gt;
 *         &lt;element name="HomePhoneNumber" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}phoneNumberProtectable" minOccurs="0"/&gt;
 *         &lt;element name="WorkPhoneNumber" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}phoneNumberProtectable" minOccurs="0"/&gt;
 *         &lt;element name="MobilePhoneNumber" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}phoneNumberProtectable" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="protected" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personFull", propOrder = {
    "address",
    "homePhoneNumber",
    "workPhoneNumber",
    "mobilePhoneNumber"
})
@XmlSeeAlso({
    PersonFullMyndighed.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class PersonFull
    extends PersonMedium
{

    /**
     * Adresse.
     * 
     */
    @XmlElement(name = "Address")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected Address address;
    /**
     * Hjemmetelefon.
     * 
     */
    @XmlElement(name = "HomePhoneNumber")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected PhoneNumberProtectable homePhoneNumber;
    /**
     * Arbejdstelefon
     * 
     */
    @XmlElement(name = "WorkPhoneNumber")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected PhoneNumberProtectable workPhoneNumber;
    /**
     * Mobiltelefon
     * 
     */
    @XmlElement(name = "MobilePhoneNumber")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected PhoneNumberProtectable mobilePhoneNumber;
    @XmlAttribute(name = "protected", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected boolean _protected;

    /**
     * Adresse.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     * @see #getAddress()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setAddress(Address value) {
        this.address = value;
    }

    /**
     * Hjemmetelefon.
     * 
     * @return
     *     possible object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getHomePhoneNumber()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setHomePhoneNumber(PhoneNumberProtectable value) {
        this.homePhoneNumber = value;
    }

    /**
     * Arbejdstelefon
     * 
     * @return
     *     possible object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getWorkPhoneNumber()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setWorkPhoneNumber(PhoneNumberProtectable value) {
        this.workPhoneNumber = value;
    }

    /**
     * Mobiltelefon
     * 
     * @return
     *     possible object is
     *     {@link PhoneNumberProtectable }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getMobilePhoneNumber()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setMobilePhoneNumber(PhoneNumberProtectable value) {
        this.mobilePhoneNumber = value;
    }

    /**
     * Gets the value of the protected property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public boolean isProtected() {
        return _protected;
    }

    /**
     * Sets the value of the protected property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setProtected(boolean value) {
        this._protected = value;
    }

}
