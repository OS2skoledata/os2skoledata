
package https.wsieksport_unilogin_dk.eksport.medium;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uniLoginExportMedium complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uniLoginExportMedium"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport/medium}uniLoginExportMediumBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Institution" type="{https://wsieksport.unilogin.dk/eksport/medium}institutionMedium"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportMedium", propOrder = {
    "institution"
})
public class UniLoginExportMedium
    extends UniLoginExportMediumBase
{

    @XmlElement(name = "Institution", required = true)
    protected InstitutionMedium institution;

    /**
     * Gets the value of the institution property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionMedium }
     *     
     */
    public InstitutionMedium getInstitution() {
        return institution;
    }

    /**
     * Sets the value of the institution property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstitutionMedium }
     *     
     */
    public void setInstitution(InstitutionMedium value) {
        this.institution = value;
    }

}
