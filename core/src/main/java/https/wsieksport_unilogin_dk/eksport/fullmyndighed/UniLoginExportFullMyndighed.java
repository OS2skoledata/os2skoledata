
package https.wsieksport_unilogin_dk.eksport.fullmyndighed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uniLoginExportFullMyndighed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uniLoginExportFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport/fullmyndighed}uniLoginExportFullMyndighedBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Institution" type="{https://wsieksport.unilogin.dk/eksport/fullmyndighed}institutionFullMyndighed"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportFullMyndighed", propOrder = {
    "institution"
})
public class UniLoginExportFullMyndighed
    extends UniLoginExportFullMyndighedBase
{

    @XmlElement(name = "Institution", required = true)
    protected InstitutionFullMyndighed institution;

    /**
     * Gets the value of the institution property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionFullMyndighed }
     *     
     */
    public InstitutionFullMyndighed getInstitution() {
        return institution;
    }

    /**
     * Sets the value of the institution property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstitutionFullMyndighed }
     *     
     */
    public void setInstitution(InstitutionFullMyndighed value) {
        this.institution = value;
    }

}
