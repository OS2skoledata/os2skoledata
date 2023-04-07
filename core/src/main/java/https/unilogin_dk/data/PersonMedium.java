
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * 
 *                 Mellemstor udgave af persondata. Anvendes direkte i medium eksport.
 *                 Basistype for personFull; se nedenfor.
 *             
 * 
 * <p>Java class for personMedium complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="personMedium"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data}personMini"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://unilogin.dk/data}CivilRegistrationNumber" minOccurs="0"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}EmailAddress" minOccurs="0"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}BirthDate" minOccurs="0"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}Gender" minOccurs="0"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}PhotoId" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personMedium", propOrder = {
    "civilRegistrationNumber",
    "emailAddress",
    "birthDate",
    "gender",
    "photoId"
})
@XmlSeeAlso({
    PersonFullBase.class
})
public class PersonMedium
    extends PersonMini
{

    @XmlElement(name = "CivilRegistrationNumber")
    protected String civilRegistrationNumber;
    @XmlElement(name = "EmailAddress")
    protected String emailAddress;
    @XmlElement(name = "BirthDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthDate;
    @XmlElement(name = "Gender")
    @XmlSchemaType(name = "string")
    protected Gender gender;
    @XmlElement(name = "PhotoId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String photoId;

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
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link Gender }
     *     
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gender }
     *     
     */
    public void setGender(Gender value) {
        this.gender = value;
    }

    /**
     * Gets the value of the photoId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhotoId() {
        return photoId;
    }

    /**
     * Sets the value of the photoId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhotoId(String value) {
        this.photoId = value;
    }

}
