
package https.unilogin_dk.data.transitional;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import https.unilogin.Ansatrolle;
import https.unilogin_dk.data.EmployeeBase;


/**
 * Læreroplysninger. Anvendes i institutionPerson.
 * 
 * <p>Java class for employee complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="employee"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://unilogin.dk/data}employeeBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Role" type="{https://unilogin.dk}Ansatrolle" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}ShortName" minOccurs="0"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}Occupation" minOccurs="0"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data/transitional}Location" minOccurs="0"/&gt;
 *         &lt;element ref="{https://unilogin.dk/data}GroupId" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "employee", propOrder = {
    "role",
    "shortName",
    "occupation",
    "location",
    "groupId"
})
public class Employee
    extends EmployeeBase
{

    @XmlElement(name = "Role", required = true)
    @XmlSchemaType(name = "string")
    protected List<Ansatrolle> role;
    @XmlElement(name = "ShortName", namespace = "https://unilogin.dk/data")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String shortName;
    @XmlElement(name = "Occupation", namespace = "https://unilogin.dk/data")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String occupation;
    @XmlElement(name = "Location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String location;
    @XmlElement(name = "GroupId", namespace = "https://unilogin.dk/data")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> groupId;

    /**
     * Gets the value of the role property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the role property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRole().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Ansatrolle }
     * 
     * 
     */
    public List<Ansatrolle> getRole() {
        if (role == null) {
            role = new ArrayList<Ansatrolle>();
        }
        return this.role;
    }

    /**
     * Gets the value of the shortName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the value of the shortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Gets the value of the occupation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Sets the value of the occupation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccupation(String value) {
        this.occupation = value;
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
     * De grupper, som medarbejderen er tilknyttet.Gets the value of the groupId property.
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
