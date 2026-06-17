
package dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.ContactPersonBase;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for contactPersonFullMyndighed complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="contactPersonFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}contactPersonBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Person" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/fullmyndighed}personFullMyndighed"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactPersonFullMyndighed", propOrder = {
    "person"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class ContactPersonFullMyndighed
    extends ContactPersonBase
{

    /**
     * Personoplysninger på kontaktpersonen.
     * 
     */
    @XmlElement(name = "Person", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected PersonFullMyndighed person;

    /**
     * Personoplysninger på kontaktpersonen.
     * 
     * @return
     *     possible object is
     *     {@link PersonFullMyndighed }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getPerson()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setPerson(PersonFullMyndighed value) {
        this.person = value;
    }

}
