
package https.wsieksport_unilogin_dk.eksport.small;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.unilogin_dk.data.InstitutionWithGroupsBase;


/**
 * <p>Java class for institutionSmall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="institutionSmall"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data}institutionWithGroupsBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InstitutionPerson" type="{https://wsieksport.unilogin.dk/eksport/small}institutionPersonSmall" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionSmall", propOrder = {
    "institutionPerson"
})
public class InstitutionSmall
    extends InstitutionWithGroupsBase
{

    @XmlElement(name = "InstitutionPerson")
    protected List<InstitutionPersonSmall> institutionPerson;

    /**
     * Gets the value of the institutionPerson property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the institutionPerson property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstitutionPerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InstitutionPersonSmall }
     * 
     * 
     */
    public List<InstitutionPersonSmall> getInstitutionPerson() {
        if (institutionPerson == null) {
            institutionPerson = new ArrayList<InstitutionPersonSmall>();
        }
        return this.institutionPerson;
    }

}
