
package https.wsieksport_unilogin_dk.eksport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import https.unilogin_dk.data.transitional.UniLoginFull;
import https.wsieksport_unilogin_dk.eksport.full.InstitutionPersonFull;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionPersonFullMyndighed;
import https.wsieksport_unilogin_dk.eksport.medium.InstitutionPersonMedium;


/**
 * <p>Java class for institutionPersonExportFullBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="institutionPersonExportFullBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport}institutionPersonExportBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://unilogin.dk/data}LocalPersonId" minOccurs="0"/&gt;
 *         &lt;element name="UNILogin" type="{https://unilogin.dk/data/transitional}uniLoginFull"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionPersonExportFullBase", propOrder = {
    "localPersonId",
    "uniLogin"
})
@XmlSeeAlso({
    InstitutionPersonMedium.class,
    InstitutionPersonFullMyndighed.class,
    InstitutionPersonFull.class
})
public class InstitutionPersonExportFullBase
    extends InstitutionPersonExportBase
{

    @XmlElement(name = "LocalPersonId", namespace = "https://unilogin.dk/data")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String localPersonId;
    @XmlElement(name = "UNILogin", required = true)
    protected UniLoginFull uniLogin;

    /**
     * 
     *                                 Elementet er valgfrit, da det ikke findes i gamle importer og manuelt oprettede brugere.
     *                             
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
     */
    public void setLocalPersonId(String value) {
        this.localPersonId = value;
    }

    /**
     * Gets the value of the uniLogin property.
     * 
     * @return
     *     possible object is
     *     {@link UniLoginFull }
     *     
     */
    public UniLoginFull getUNILogin() {
        return uniLogin;
    }

    /**
     * Sets the value of the uniLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link UniLoginFull }
     *     
     */
    public void setUNILogin(UniLoginFull value) {
        this.uniLogin = value;
    }

}
