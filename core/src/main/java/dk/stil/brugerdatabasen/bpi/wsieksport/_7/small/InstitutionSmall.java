
package dk.stil.brugerdatabasen.bpi.wsieksport._7.small;

import java.util.ArrayList;
import java.util.List;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.InstitutionWithGroupsBase;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for institutionSmall complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="institutionSmall"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}institutionWithGroupsBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InstitutionPerson" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/small}institutionPersonSmall" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionSmall", propOrder = {
    "institutionPerson"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class InstitutionSmall
    extends InstitutionWithGroupsBase
{

    /**
     * Ansatte og elever på institutionen.
     * 
     */
    @XmlElement(name = "InstitutionPerson")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected List<InstitutionPersonSmall> institutionPerson;

    /**
     * Ansatte og elever på institutionen.
     * 
     * Gets the value of the institutionPerson property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the institutionPerson property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getInstitutionPerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InstitutionPersonSmall }
     * </p>
     * 
     * 
     * @return
     *     The value of the institutionPerson property.
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public List<InstitutionPersonSmall> getInstitutionPerson() {
        if (institutionPerson == null) {
            institutionPerson = new ArrayList<>();
        }
        return this.institutionPerson;
    }

}
