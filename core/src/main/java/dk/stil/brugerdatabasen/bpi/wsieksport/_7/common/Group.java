
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import javax.xml.datatype.XMLGregorianCalendar;
import dk.stil.brugerdatabasen.common.v3.ImportGroupType;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Grupper bruges til samling af personer på en institution. Både elever og lærere kan tilknyttes grupper.
 *                 En gruppe kan f.eks. være klasser i grundskolen, hold, fag, studiegrupper eller lærerteams.
 *                 Der er i gruppebegrebet ingen begrænsninger, i forhold til hvad den enkelte institution vil bruge det
 *                 til.
 *                 Grupper kan oprettes både eksplicit og implicit. Group-elementet bruges til eksplicit oprettelse af
 *                 grupper i forbindelse med import og alle attributter på gruppen kan udfyldes.
 *                 Hvis en gruppe refereres fra personer uden at den er oprettet med Group elementet, foretager UNI•Login
 *                 en implicit oprettelse af gruppen med blot et GroupId.
 * 
 * &lt;p&gt;Java class for group complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="group"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GroupId" type="{https://brugerdatabasen.stil.dk/bpi/common/3}Gruppekode"/&gt;
 *         &lt;element name="GroupName" type="{https://brugerdatabasen.stil.dk/bpi/common/3}Token100char" minOccurs="0"/&gt;
 *         &lt;element name="GroupType" type="{https://brugerdatabasen.stil.dk/bpi/common/3}importGroupType"/&gt;
 *         &lt;element name="GroupLevel" type="{https://brugerdatabasen.stil.dk/bpi/common/3}trin" minOccurs="0"/&gt;
 *         &lt;element name="Line" type="{https://brugerdatabasen.stil.dk/bpi/common/3}Token40char" minOccurs="0"/&gt;
 *         &lt;element name="FromDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="ToDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "group", propOrder = {
    "groupId",
    "groupName",
    "groupType",
    "groupLevel",
    "line",
    "fromDate",
    "toDate"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class Group {

    /**
     * Gruppeid/holdID/fagkode som er unikt på institutionen. For eksempel "4a", "Tysk I" eller
     *                         "Dansklærere".
     *                         GroupId vises i brugerdministrationen og bruges ligeledes i forbindelse med tildeling af for
     *                         eksempel licenser.
     *                         Elever og lærere kan tilknyttes gruppen ved at angive MainGroupId eller GroupId på personen.
     * 
     */
    @XmlElement(name = "GroupId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String groupId;
    /**
     * Alias for GroupId, hvis man ønsker et supplerende navn til gruppen.
     *                         For eksempel, hvis 0b også kaldes ”Solsikken”, kan det angives her.
     *                         Hvis det er en implicit oprettelse sættes GroupName lig med GroupId.
     * 
     */
    @XmlElement(name = "GroupName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String groupName;
    /**
     * Typen af gruppe der oprettes.
     * 
     */
    @XmlElement(name = "GroupType", required = true)
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected ImportGroupType groupType;
    /**
     * Stamklassens/hovedgruppens trin.
     *                         Skal angives for grupper med groupType "Hovedgruppe" og kun da.
     * 
     */
    @XmlElement(name = "GroupLevel")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String groupLevel;
    /**
     * Spor, linje, studieretning eller lignende for klassen/stamholdet.
     *                         For grundskoler f.eks. "B".
     * 
     */
    @XmlElement(name = "Line")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String line;
    /**
     * Gruppens startdato.
     * 
     */
    @XmlElement(name = "FromDate")
    @XmlSchemaType(name = "date")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected XMLGregorianCalendar fromDate;
    /**
     * Gruppens slutdato.
     * 
     */
    @XmlElement(name = "ToDate")
    @XmlSchemaType(name = "date")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected XMLGregorianCalendar toDate;

    /**
     * Gruppeid/holdID/fagkode som er unikt på institutionen. For eksempel "4a", "Tysk I" eller
     *                         "Dansklærere".
     *                         GroupId vises i brugerdministrationen og bruges ligeledes i forbindelse med tildeling af for
     *                         eksempel licenser.
     *                         Elever og lærere kan tilknyttes gruppen ved at angive MainGroupId eller GroupId på personen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getGroupId()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setGroupId(String value) {
        this.groupId = value;
    }

    /**
     * Alias for GroupId, hvis man ønsker et supplerende navn til gruppen.
     *                         For eksempel, hvis 0b også kaldes ”Solsikken”, kan det angives her.
     *                         Hvis det er en implicit oprettelse sættes GroupName lig med GroupId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getGroupName()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setGroupName(String value) {
        this.groupName = value;
    }

    /**
     * Typen af gruppe der oprettes.
     * 
     * @return
     *     possible object is
     *     {@link ImportGroupType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public ImportGroupType getGroupType() {
        return groupType;
    }

    /**
     * Sets the value of the groupType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportGroupType }
     *     
     * @see #getGroupType()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setGroupType(ImportGroupType value) {
        this.groupType = value;
    }

    /**
     * Stamklassens/hovedgruppens trin.
     *                         Skal angives for grupper med groupType "Hovedgruppe" og kun da.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getGroupLevel() {
        return groupLevel;
    }

    /**
     * Sets the value of the groupLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getGroupLevel()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setGroupLevel(String value) {
        this.groupLevel = value;
    }

    /**
     * Spor, linje, studieretning eller lignende for klassen/stamholdet.
     *                         For grundskoler f.eks. "B".
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getLine()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setLine(String value) {
        this.line = value;
    }

    /**
     * Gruppens startdato.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public XMLGregorianCalendar getFromDate() {
        return fromDate;
    }

    /**
     * Sets the value of the fromDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     * @see #getFromDate()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setFromDate(XMLGregorianCalendar value) {
        this.fromDate = value;
    }

    /**
     * Gruppens slutdato.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public XMLGregorianCalendar getToDate() {
        return toDate;
    }

    /**
     * Sets the value of the toDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     * @see #getToDate()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setToDate(XMLGregorianCalendar value) {
        this.toDate = value;
    }

}
