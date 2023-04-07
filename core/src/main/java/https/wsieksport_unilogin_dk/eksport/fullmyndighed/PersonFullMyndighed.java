
package https.wsieksport_unilogin_dk.eksport.fullmyndighed;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import https.wsieksport_unilogin_dk.eksport.full.PersonFull;


/**
 * <p>Java class for personFullMyndighed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="personFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport/full}personFull"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AliasFirstName" type="{https://unilogin.dk/data}name" minOccurs="0"/&gt;
 *         &lt;element name="AliasFamilyName" type="{https://unilogin.dk/data}familyName" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personFullMyndighed", propOrder = {
    "aliasFirstName",
    "aliasFamilyName"
})
public class PersonFullMyndighed
    extends PersonFull
{

    @XmlElement(name = "AliasFirstName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String aliasFirstName;
    @XmlElement(name = "AliasFamilyName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String aliasFamilyName;

    /**
     * Gets the value of the aliasFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
     */
    public void setAliasFirstName(String value) {
        this.aliasFirstName = value;
    }

    /**
     * Gets the value of the aliasFamilyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
     */
    public void setAliasFamilyName(String value) {
        this.aliasFamilyName = value;
    }

}
