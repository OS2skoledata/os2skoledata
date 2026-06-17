
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.full.InstitutionPersonFull;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed.InstitutionPersonFullMyndighed;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.medium.InstitutionPersonMedium;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for institutionPersonExportFullBase complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="institutionPersonExportFullBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}institutionPersonExportBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/common/3}LocalPersonId" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionPersonExportFullBase", propOrder = {
    "localPersonId"
})
@XmlSeeAlso({
    InstitutionPersonFullMyndighed.class,
    InstitutionPersonFull.class,
    InstitutionPersonMedium.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class InstitutionPersonExportFullBase
    extends InstitutionPersonExportBase
{

    /**
     * Elementet er valgfrit, da det ikke findes i gamle importer og manuelt oprettede brugere.
     * 
     */
    @XmlElement(name = "LocalPersonId", namespace = "https://brugerdatabasen.stil.dk/bpi/common/3")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String localPersonId;

    /**
     * Elementet er valgfrit, da det ikke findes i gamle importer og manuelt oprettede brugere.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getLocalPersonId() {
        return localPersonId;
    }

    /**
     * Sets the value of the localPersonId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getLocalPersonId()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setLocalPersonId(String value) {
        this.localPersonId = value;
    }

}
