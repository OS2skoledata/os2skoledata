
package https.wsieksport_unilogin_dk.eksport.full;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.unilogin_dk.data.Extern;
import https.unilogin_dk.data.transitional.Employee;
import https.wsieksport_unilogin_dk.eksport.InstitutionPersonExportFullBase;


/**
 * <p>Java class for institutionPersonFull complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="institutionPersonFull"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport}institutionPersonExportFullBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Person" type="{https://wsieksport.unilogin.dk/eksport/full}personFull" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="Student" type="{https://wsieksport.unilogin.dk/eksport/full}studentFull"/&gt;
 *           &lt;element ref="{https://unilogin.dk/data/transitional}Employee"/&gt;
 *           &lt;element ref="{https://unilogin.dk/data}Extern"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionPersonFull", propOrder = {
    "person",
    "student",
    "employee",
    "extern"
})
public class InstitutionPersonFull
    extends InstitutionPersonExportFullBase
{

    @XmlElement(name = "Person")
    protected PersonFull person;
    @XmlElement(name = "Student")
    protected StudentFull student;
    @XmlElement(name = "Employee", namespace = "https://unilogin.dk/data/transitional")
    protected Employee employee;
    @XmlElement(name = "Extern", namespace = "https://unilogin.dk/data")
    protected Extern extern;

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link PersonFull }
     *     
     */
    public PersonFull getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonFull }
     *     
     */
    public void setPerson(PersonFull value) {
        this.person = value;
    }

    /**
     * Gets the value of the student property.
     * 
     * @return
     *     possible object is
     *     {@link StudentFull }
     *     
     */
    public StudentFull getStudent() {
        return student;
    }

    /**
     * Sets the value of the student property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudentFull }
     *     
     */
    public void setStudent(StudentFull value) {
        this.student = value;
    }

    /**
     * Gets the value of the employee property.
     * 
     * @return
     *     possible object is
     *     {@link Employee }
     *     
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets the value of the employee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Employee }
     *     
     */
    public void setEmployee(Employee value) {
        this.employee = value;
    }

    /**
     * Gets the value of the extern property.
     * 
     * @return
     *     possible object is
     *     {@link Extern }
     *     
     */
    public Extern getExtern() {
        return extern;
    }

    /**
     * Sets the value of the extern property.
     * 
     * @param value
     *     allowed object is
     *     {@link Extern }
     *     
     */
    public void setExtern(Extern value) {
        this.extern = value;
    }

}
