
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Type for UNI•Login-oplysninger i fuld og medium eksport og returværdi i import.
 *                 I lille eksport anvendes den minimale type.
 *             
 * 
 * <p>Java class for uniLoginFull complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uniLoginFull"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data}uniLoginMini"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://unilogin.dk/data}InitialPassword"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}CivilRegistrationNumber"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}PasswordState"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginFull", propOrder = {
    "initialPassword",
    "civilRegistrationNumber",
    "passwordState"
})
public class UniLoginFull
    extends UniLoginMini
{

    @XmlElement(name = "InitialPassword", required = true)
    protected String initialPassword;
    @XmlElement(name = "CivilRegistrationNumber", required = true)
    protected String civilRegistrationNumber;
    @XmlElement(name = "PasswordState", required = true)
    @XmlSchemaType(name = "string")
    protected PasswordState passwordState;

    /**
     * Gets the value of the initialPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInitialPassword() {
        return initialPassword;
    }

    /**
     * Sets the value of the initialPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInitialPassword(String value) {
        this.initialPassword = value;
    }

    /**
     * Gets the value of the civilRegistrationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCivilRegistrationNumber() {
        return civilRegistrationNumber;
    }

    /**
     * Sets the value of the civilRegistrationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCivilRegistrationNumber(String value) {
        this.civilRegistrationNumber = value;
    }

    /**
     * Gets the value of the passwordState property.
     * 
     * @return
     *     possible object is
     *     {@link PasswordState }
     *     
     */
    public PasswordState getPasswordState() {
        return passwordState;
    }

    /**
     * Sets the value of the passwordState property.
     * 
     * @param value
     *     allowed object is
     *     {@link PasswordState }
     *     
     */
    public void setPasswordState(PasswordState value) {
        this.passwordState = value;
    }

}
