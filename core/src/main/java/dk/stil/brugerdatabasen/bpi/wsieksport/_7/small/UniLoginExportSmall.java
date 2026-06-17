
package dk.stil.brugerdatabasen.bpi.wsieksport._7.small;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for uniLoginExportSmall complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="uniLoginExportSmall"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/small}uniLoginExportSmallBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Institution" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/small}institutionSmall"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportSmall", propOrder = {
    "institution"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class UniLoginExportSmall
    extends UniLoginExportSmallBase
{

    @XmlElement(name = "Institution", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected InstitutionSmall institution;

    /**
     * Gets the value of the institution property.
     * 
     * @return
     *     possible object is
     *     {@link InstitutionSmall }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setInstitution(InstitutionSmall value) {
        this.institution = value;
    }

}
