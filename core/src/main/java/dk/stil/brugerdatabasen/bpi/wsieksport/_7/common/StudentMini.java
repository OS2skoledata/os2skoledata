
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import java.util.ArrayList;
import java.util.List;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.full.StudentFull;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed.StudentFullMyndighed;
import dk.stil.brugerdatabasen.common.v3.Elevrolle;
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
 * Minimal basistype for student.
 *                 Elevoplysninger. Anvendes i institutionPerson.
 *                 I import og fuld eksport tilføjes ContactPerson.
 * 
 * &lt;p&gt;Java class for studentMini complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="studentMini"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Role" type="{https://brugerdatabasen.stil.dk/bpi/common/3}Elevrolle"/&gt;
 *         &lt;element name="StudentNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{https://brugerdatabasen.stil.dk/bpi/common/3}NonEmptyToken"&gt;
 *               &lt;maxLength value="26"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Level" type="{https://brugerdatabasen.stil.dk/bpi/common/3}trin"/&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}Location" minOccurs="0"/&gt;
 *         &lt;element name="MainGroupId" type="{https://brugerdatabasen.stil.dk/bpi/common/3}Gruppekode"/&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/common/3}GroupId" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "studentMini", propOrder = {
    "role",
    "studentNumber",
    "level",
    "location",
    "mainGroupId",
    "groupId"
})
@XmlSeeAlso({
    StudentFullMyndighed.class,
    StudentFull.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class StudentMini {

    @XmlElement(name = "Role", required = true)
    @XmlSchemaType(name = "string")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected Elevrolle role;
    /**
     * Elevens studienummer fra lokalt administrativt system.
     * 
     */
    @XmlElement(name = "StudentNumber")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String studentNumber;
    /**
     * Elevens trin.
     * 
     */
    @XmlElement(name = "Level", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String level;
    @XmlElement(name = "Location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String location;
    /**
     * Elevens hovedgruppe.
     *                         For grundskoler er hovedgruppen elevens klasse, og for andre
     *                         institutioner kan den for eksempel være studieretning eller stamhold.
     *                         Feltet skal indeholde gruppens unikke id på institutionen (GroupId)
     *                         og må kun referere til grupper med groupType "Hovedgruppe".
     *                         Bemærk, at dette krav ikke kan garanteres at være overholdt i eksporten
     *                         pga. gamle importer og manuelt tilknyttede brugere.
     * 
     */
    @XmlElement(name = "MainGroupId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String mainGroupId;
    /**
     * Yderligere grupper, som eleven er tilknyttet, ud over tilknytningen via MainGroupId.
     * 
     */
    @XmlElement(name = "GroupId", namespace = "https://brugerdatabasen.stil.dk/bpi/common/3")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected List<String> groupId;

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link Elevrolle }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public Elevrolle getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link Elevrolle }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setRole(Elevrolle value) {
        this.role = value;
    }

    /**
     * Elevens studienummer fra lokalt administrativt system.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getStudentNumber() {
        return studentNumber;
    }

    /**
     * Sets the value of the studentNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getStudentNumber()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setStudentNumber(String value) {
        this.studentNumber = value;
    }

    /**
     * Elevens trin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getLevel()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setLevel(String value) {
        this.level = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Elevens hovedgruppe.
     *                         For grundskoler er hovedgruppen elevens klasse, og for andre
     *                         institutioner kan den for eksempel være studieretning eller stamhold.
     *                         Feltet skal indeholde gruppens unikke id på institutionen (GroupId)
     *                         og må kun referere til grupper med groupType "Hovedgruppe".
     *                         Bemærk, at dette krav ikke kan garanteres at være overholdt i eksporten
     *                         pga. gamle importer og manuelt tilknyttede brugere.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getMainGroupId() {
        return mainGroupId;
    }

    /**
     * Sets the value of the mainGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getMainGroupId()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setMainGroupId(String value) {
        this.mainGroupId = value;
    }

    /**
     * Yderligere grupper, som eleven er tilknyttet, ud over tilknytningen via MainGroupId.
     * 
     * Gets the value of the groupId property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the groupId property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getGroupId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * </p>
     * 
     * 
     * @return
     *     The value of the groupId property.
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public List<String> getGroupId() {
        if (groupId == null) {
            groupId = new ArrayList<>();
        }
        return this.groupId;
    }

}
