
package https.wsieksport_unilogin_dk.eksport.medium;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.unilogin_dk.data.InstitutionWithGroupsBase;


/**
 * <p>Java class for institutionMedium complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="institutionMedium"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data}institutionWithGroupsBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InstitutionPerson" type="{https://wsieksport.unilogin.dk/eksport/medium}institutionPersonMedium" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionMedium", propOrder = {
    "institutionPerson"
})
public class InstitutionMedium
    extends InstitutionWithGroupsBase
{

    @XmlElement(name = "InstitutionPerson")
    protected List<InstitutionPersonMedium> institutionPerson;

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
     * {@link InstitutionPersonMedium }
     * 
     * 
     */
    public List<InstitutionPersonMedium> getInstitutionPerson() {
        if (institutionPerson == null) {
            institutionPerson = new ArrayList<InstitutionPersonMedium>();
        }
        return this.institutionPerson;
    }

}
