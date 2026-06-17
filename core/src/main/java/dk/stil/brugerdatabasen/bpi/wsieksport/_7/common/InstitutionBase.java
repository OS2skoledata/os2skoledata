
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

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
 * Basistype for institution uden grupper. Anvendes kun direkte i delete-skemaet,
 *                 mens andre anvender institutionWithGroupsBase.
 *                 Tilføjes: InstitutionPerson
 * 
 * &lt;p&gt;Java class for institutionBase complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="institutionBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InstitutionNumber" type="{https://brugerdatabasen.stil.dk/bpi/common/3}Regnr"/&gt;
 *         &lt;element name="InstitutionName" type="{https://brugerdatabasen.stil.dk/bpi/common/3}NonEmptyToken" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionBase", propOrder = {
    "institutionNumber",
    "institutionName"
})
@XmlSeeAlso({
    InstitutionWithGroupsBase.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class InstitutionBase {

    /**
     * Seks-tegns registreringsnummer, som identificerer en institution (kan indeholde både bogstaver og tal).
     * 
     */
    @XmlElement(name = "InstitutionNumber", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String institutionNumber;
    /**
     * Institutionsnavn. Kun information. Gemmes ikke i UNI•Login.
     * 
     */
    @XmlElement(name = "InstitutionName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String institutionName;

    /**
     * Seks-tegns registreringsnummer, som identificerer en institution (kan indeholde både bogstaver og tal).
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getInstitutionNumber() {
        return institutionNumber;
    }

    /**
     * Sets the value of the institutionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getInstitutionNumber()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setInstitutionNumber(String value) {
        this.institutionNumber = value;
    }

    /**
     * Institutionsnavn. Kun information. Gemmes ikke i UNI•Login.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * Sets the value of the institutionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getInstitutionName()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setInstitutionName(String value) {
        this.institutionName = value;
    }

}
