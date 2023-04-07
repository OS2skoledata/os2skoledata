
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Base for fuld udgave af persondata.
 *                 Anvendes i import og fuld eksport.
 *                 Tilføjes: HomePhoneNumber, WorkPhoneNumber, MobilePhoneNumber
 *                 (De tre er ikke defineret ved hjælpe af elementreferencer, fordi de
 *                 skal være nillable i eksport.)
 *                 I importskemaet defineres nogle elementer som obligatoriske, mens
 *                 de i eksport kan mangle pga. manglende data i ældre importer eller
 *                 manuelt oprettede brugere.
 *                 Desuden har import oplysninger om alias ved navnebeskyttelse.
 *             
 * 
 * <p>Java class for personFullBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="personFullBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data}personMedium"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://unilogin.dk/data}Address" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="protected" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="verificationLevel" use="required" type="{https://unilogin.dk}Verifikationsniveau" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personFullBase", propOrder = {
    "address"
})
public class PersonFullBase
    extends PersonMedium
{

    @XmlElement(name = "Address")
    protected Address address;
    @XmlAttribute(name = "protected", required = true)
    protected boolean _protected;
    @XmlAttribute(name = "verificationLevel", required = true)
    protected int verificationLevel;

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link Address }
     *     
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link Address }
     *     
     */
    public void setAddress(Address value) {
        this.address = value;
    }

    /**
     * Gets the value of the protected property.
     * 
     */
    public boolean isProtected() {
        return _protected;
    }

    /**
     * Sets the value of the protected property.
     * 
     */
    public void setProtected(boolean value) {
        this._protected = value;
    }

    /**
     * Gets the value of the verificationLevel property.
     * 
     */
    public int getVerificationLevel() {
        return verificationLevel;
    }

    /**
     * Sets the value of the verificationLevel property.
     * 
     */
    public void setVerificationLevel(int value) {
        this.verificationLevel = value;
    }

}
