
package dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.PersonFull;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for personFullMyndighed complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="personFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}personFull"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AliasFirstName" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}name" minOccurs="0"/&gt;
 *         &lt;element name="AliasFamilyName" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}familyName" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personFullMyndighed", propOrder = {
    "aliasFirstName",
    "aliasFamilyName"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class PersonFullMyndighed
    extends PersonFull
{

    /**
     * Alias fornavn.
     * 
     */
    @XmlElement(name = "AliasFirstName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String aliasFirstName;
    /**
     * Alias efternavn.
     * 
     */
    @XmlElement(name = "AliasFamilyName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String aliasFamilyName;

    /**
     * Alias fornavn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getAliasFirstName() {
        return aliasFirstName;
    }

    /**
     * Sets the value of the aliasFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getAliasFirstName()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setAliasFirstName(String value) {
        this.aliasFirstName = value;
    }

    /**
     * Alias efternavn.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getAliasFamilyName() {
        return aliasFamilyName;
    }

    /**
     * Sets the value of the aliasFamilyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getAliasFamilyName()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setAliasFamilyName(String value) {
        this.aliasFamilyName = value;
    }

}
