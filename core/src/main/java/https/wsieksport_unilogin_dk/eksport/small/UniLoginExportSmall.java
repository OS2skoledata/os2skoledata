
package https.wsieksport_unilogin_dk.eksport.small;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uniLoginExportSmall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uniLoginExportSmall"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport/small}uniLoginExportSmallBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Institution" type="{https://wsieksport.unilogin.dk/eksport/small}institutionSmall"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportSmall", propOrder = {
    "institution"
})
public class UniLoginExportSmall
    extends UniLoginExportSmallBase
{

    @XmlElement(name = "Institution", required = true)
    protected InstitutionSmall institution;

    /**
     * Gets the value of the institution property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionSmall }
     *     
     */
    public InstitutionSmall getInstitution() {
        return institution;
    }

    /**
     * Sets the value of the institution property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstitutionSmall }
     *     
     */
    public void setInstitution(InstitutionSmall value) {
        this.institution = value;
    }

}
