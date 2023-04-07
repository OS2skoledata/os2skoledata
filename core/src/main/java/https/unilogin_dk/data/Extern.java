
package https.unilogin_dk.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import https.unilogin.Eksternrolle;


/**
 * 
 *                 Type til en ekstern person. Anvendes i InstitutionPerson i import og eksport.
 *             
 * 
 * <p>Java class for extern complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="extern"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Role" type="{https://unilogin.dk}Eksternrolle"/&gt;
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
@XmlType(name = "extern", propOrder = {
    "role",
    "groupId"
})
public class Extern {

    @XmlElement(name = "Role", required = true)
    @XmlSchemaType(name = "string")
    protected Eksternrolle role;
    @XmlElement(name = "GroupId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> groupId;

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link Eksternrolle }
     *     
     */
    public Eksternrolle getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link Eksternrolle }
     *     
     */
    public void setRole(Eksternrolle value) {
        this.role = value;
    }

    /**
     * De grupper, som den eksterne person er tilknyttet.Gets the value of the groupId property.
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
