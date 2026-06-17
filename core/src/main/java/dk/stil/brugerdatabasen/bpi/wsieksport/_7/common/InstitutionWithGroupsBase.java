
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import java.util.ArrayList;
import java.util.List;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.full.InstitutionFull;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed.InstitutionFullMyndighed;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.medium.InstitutionMedium;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.small.InstitutionSmall;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Den egentlige basistype for institution, der anvendes i import og eksport.
 *                 Tilføjes: InstitutionPerson (jf. institutionBase)
 * 
 * &lt;p&gt;Java class for institutionWithGroupsBase complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="institutionWithGroupsBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}institutionBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Group" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}group" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionWithGroupsBase", propOrder = {
    "group"
})
@XmlSeeAlso({
    InstitutionFullMyndighed.class,
    InstitutionFull.class,
    InstitutionMedium.class,
    InstitutionSmall.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class InstitutionWithGroupsBase
    extends InstitutionBase
{

    /**
     * Grupper på institutionen.
     * 
     */
    @XmlElement(name = "Group")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected List<Group> group;

    /**
     * Grupper på institutionen.
     * 
     * Gets the value of the group property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the group property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Group }
     * </p>
     * 
     * 
     * @return
     *     The value of the group property.
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public List<Group> getGroup() {
        if (group == null) {
            group = new ArrayList<>();
        }
        return this.group;
    }

}
