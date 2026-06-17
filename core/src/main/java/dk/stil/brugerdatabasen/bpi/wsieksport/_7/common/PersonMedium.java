
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import javax.xml.datatype.XMLGregorianCalendar;
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
 * Mellemstor udgave af persondata. Anvendes direkte i medium eksport.
 *                 Basistype for personFull; se nedenfor.
 * 
 * &lt;p&gt;Java class for personMedium complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="personMedium"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}personMini"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}CivilRegistrationNumber" minOccurs="0"/&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}CivilRegistrationNumberType" minOccurs="0"/&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}EmailAddress" minOccurs="0"/&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}BirthDate" minOccurs="0"/&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}Gender" minOccurs="0"/&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}PhotoId" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personMedium", propOrder = {
    "civilRegistrationNumber",
    "civilRegistrationNumberType",
    "emailAddress",
    "birthDate",
    "gender",
    "photoId"
})
@XmlSeeAlso({
    PersonFull.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class PersonMedium
    extends PersonMini
{

    /**
     * Bliver skjult ved navne- og adressebeskyttelse i wsiEksportMellem
     * 
     */
    @XmlElement(name = "CivilRegistrationNumber")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String civilRegistrationNumber;
    /**
     * Type af CPR-nummer
     * 
     */
    @XmlElement(name = "CivilRegistrationNumberType")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected CprType civilRegistrationNumberType;
    /**
     * E-mail.
     * 
     */
    @XmlElement(name = "EmailAddress")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String emailAddress;
    /**
     * Fødselsdato.
     * 
     */
    @XmlElement(name = "BirthDate")
    @XmlSchemaType(name = "date")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected XMLGregorianCalendar birthDate;
    /**
     * Personens køn.
     * 
     */
    @XmlElement(name = "Gender")
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected Gender gender;
    /**
     * Benyttes til at navngive foto med.
     * 
     */
    @XmlElement(name = "PhotoId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String photoId;

    /**
     * Bliver skjult ved navne- og adressebeskyttelse i wsiEksportMellem
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getCivilRegistrationNumber()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setCivilRegistrationNumber(String value) {
        this.civilRegistrationNumber = value;
    }

    /**
     * Type af CPR-nummer
     * 
     * @return
     *     possible object is
     *     {@link CprType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public CprType getCivilRegistrationNumberType() {
        return civilRegistrationNumberType;
    }

    /**
     * Sets the value of the civilRegistrationNumberType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CprType }
     *     
     * @see #getCivilRegistrationNumberType()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setCivilRegistrationNumberType(CprType value) {
        this.civilRegistrationNumberType = value;
    }

    /**
     * E-mail.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getEmailAddress()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Fødselsdato.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getBirthDate()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Personens køn.
     * 
     * @return
     *     possible object is
     *     {@link Gender }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getGender()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setGender(Gender value) {
        this.gender = value;
    }

    /**
     * Benyttes til at navngive foto med.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getPhotoId()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setPhotoId(String value) {
        this.photoId = value;
    }

}
