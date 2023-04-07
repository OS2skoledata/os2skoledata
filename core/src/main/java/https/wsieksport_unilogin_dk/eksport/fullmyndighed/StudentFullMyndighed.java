
package https.wsieksport_unilogin_dk.eksport.fullmyndighed;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.unilogin_dk.data.transitional.StudentMini;


/**
 * <p>Java class for studentFullMyndighed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data/transitional}studentMini"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ContactPerson" type="{https://wsieksport.unilogin.dk/eksport/fullmyndighed}contactPersonFullMyndighed" maxOccurs="10" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentFullMyndighed", propOrder = {
    "contactPerson"
})
public class StudentFullMyndighed
    extends StudentMini
{

    @XmlElement(name = "ContactPerson")
    protected List<ContactPersonFullMyndighed> contactPerson;

    /**
     * Gets the value of the contactPerson property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contactPerson property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContactPerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactPersonFullMyndighed }
     * 
     * 
     */
    public List<ContactPersonFullMyndighed> getContactPerson() {
        if (contactPerson == null) {
            contactPerson = new ArrayList<ContactPersonFullMyndighed>();
        }
        return this.contactPerson;
    }

}
