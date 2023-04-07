
package https.unilogin_dk.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.wsieksport_unilogin_dk.eksport.full.InstitutionFull;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionFullMyndighed;
import https.wsieksport_unilogin_dk.eksport.medium.InstitutionMedium;
import https.wsieksport_unilogin_dk.eksport.small.InstitutionSmall;


/**
 * 
 *                 Den egentlige basistype for institution, der anvendes i import og eksport.
 *                 Tilføjes: InstitutionPerson (jf. institutionBase)
 *             
 * 
 * <p>Java class for institutionWithGroupsBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="institutionWithGroupsBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data}institutionBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Group" type="{https://unilogin.dk/data}group" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionWithGroupsBase", propOrder = {
    "group"
})
@XmlSeeAlso({
    InstitutionMedium.class,
    InstitutionFullMyndighed.class,
    InstitutionSmall.class,
    InstitutionFull.class
})
public class InstitutionWithGroupsBase
    extends InstitutionBase
{

    @XmlElement(name = "Group")
    protected List<Group> group;

    /**
     * Gets the value of the group property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the group property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Group }
     * 
     * 
     */
    public List<Group> getGroup() {
        if (group == null) {
            group = new ArrayList<Group>();
        }
        return this.group;
    }

}
