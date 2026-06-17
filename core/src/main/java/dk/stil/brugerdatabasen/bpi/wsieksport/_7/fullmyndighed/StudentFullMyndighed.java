
package dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed;

import java.util.ArrayList;
import java.util.List;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.StudentMini;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for studentFullMyndighed complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="studentFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}studentMini"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ContactPerson" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/fullmyndighed}contactPersonFullMyndighed" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentFullMyndighed", propOrder = {
    "contactPerson"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class StudentFullMyndighed
    extends StudentMini
{

    /**
     * Kontaktperson.
     * 
     */
    @XmlElement(name = "ContactPerson")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected List<ContactPersonFullMyndighed> contactPerson;

    /**
     * Kontaktperson.
     * 
     * Gets the value of the contactPerson property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the contactPerson property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getContactPerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactPersonFullMyndighed }
     * </p>
     * 
     * 
     * @return
     *     The value of the contactPerson property.
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public List<ContactPersonFullMyndighed> getContactPerson() {
        if (contactPerson == null) {
            contactPerson = new ArrayList<>();
        }
        return this.contactPerson;
    }

}
