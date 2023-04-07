
package https.unilogin_dk.data.transitional;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import https.unilogin.Elevrolle;
import https.wsieksport_unilogin_dk.eksport.full.StudentFull;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.StudentFullMyndighed;


/**
 * 
 *                 Minimal basistype for student.
 *                 Elevoplysninger. Anvendes i institutionPerson.
 *                 I import og fuld eksport tilføjes ContactPerson.
 *             
 * 
 * <p>Java class for studentMini complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studentMini"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Role" type="{https://unilogin.dk}Elevrolle"/&gt;
 *         &lt;element name="StudentNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{https://unilogin.dk}NonEmptyToken"&gt;
 *               &lt;maxLength value="26"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Level" type="{https://unilogin.dk}Trin"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data/transitional}Location" minOccurs="0"/&gt;
 *         &lt;element name="MainGroupId" type="{https://unilogin.dk}Gruppekode"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}GroupId" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
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
public class StudentMini {

    @XmlElement(name = "Role", required = true)
    @XmlSchemaType(name = "string")
    protected Elevrolle role;
    @XmlElement(name = "StudentNumber")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String studentNumber;
    @XmlElement(name = "Level", required = true)
    protected String level;
    @XmlElement(name = "Location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String location;
    @XmlElement(name = "MainGroupId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String mainGroupId;
    @XmlElement(name = "GroupId", namespace = "https://unilogin.dk/data")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> groupId;

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link Elevrolle }
     *     
     */
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
    public void setRole(Elevrolle value) {
        this.role = value;
    }

    /**
     * Gets the value of the studentNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
     */
    public void setStudentNumber(String value) {
        this.studentNumber = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
     */
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
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the mainGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
     */
    public void setMainGroupId(String value) {
        this.mainGroupId = value;
    }

    /**
     * 
     *                         Yderligere grupper, som eleven er tilknyttet, ud over tilknytningen via MainGroupId.
     *                     Gets the value of the groupId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getGroupId() {
        if (groupId == null) {
            groupId = new ArrayList<String>();
        }
        return this.groupId;
    }

}
