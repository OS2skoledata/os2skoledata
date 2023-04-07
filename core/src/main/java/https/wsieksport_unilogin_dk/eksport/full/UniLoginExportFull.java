
package https.wsieksport_unilogin_dk.eksport.full;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uniLoginExportFull complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uniLoginExportFull"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport/full}uniLoginExportFullBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Institution" type="{https://wsieksport.unilogin.dk/eksport/full}institutionFull"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportFull", propOrder = {
    "institution"
})
public class UniLoginExportFull
    extends UniLoginExportFullBase
{

    @XmlElement(name = "Institution", required = true)
    protected InstitutionFull institution;

    /**
     * Gets the value of the institution property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionFull }
     *     
     */
    public InstitutionFull getInstitution() {
        return institution;
    }

    /**
     * Sets the value of the institution property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstitutionFull }
     *     
     */
    public void setInstitution(InstitutionFull value) {
        this.institution = value;
    }

}
