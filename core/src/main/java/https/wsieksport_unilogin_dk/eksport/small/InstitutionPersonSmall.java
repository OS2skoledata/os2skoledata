
package https.wsieksport_unilogin_dk.eksport.small;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.unilogin_dk.data.Extern;
import https.unilogin_dk.data.PersonMini;
import https.unilogin_dk.data.UniLoginMini;
import https.unilogin_dk.data.transitional.Employee;
import https.unilogin_dk.data.transitional.StudentMini;
import https.wsieksport_unilogin_dk.eksport.InstitutionPersonExportBase;


/**
 * <p>Java class for institutionPersonSmall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="institutionPersonSmall"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://wsieksport.unilogin.dk/eksport}institutionPersonExportBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="UNILogin" type="{https://unilogin.dk/data}uniLoginMini"/&gt;
 *         &lt;element name="Person" type="{https://unilogin.dk/data}personMini" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="Student" type="{https://unilogin.dk/data/transitional}studentMini"/&gt;
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
@XmlType(name = "institutionPersonSmall", propOrder = {
    "uniLogin",
    "person",
    "student",
    "employee",
    "extern"
})
public class InstitutionPersonSmall
    extends InstitutionPersonExportBase
{

    @XmlElement(name = "UNILogin", required = true)
    protected UniLoginMini uniLogin;
    @XmlElement(name = "Person")
    protected PersonMini person;
    @XmlElement(name = "Student")
    protected StudentMini student;
    @XmlElement(name = "Employee", namespace = "https://unilogin.dk/data/transitional")
    protected Employee employee;
    @XmlElement(name = "Extern", namespace = "https://unilogin.dk/data")
    protected Extern extern;

    /**
     * Gets the value of the uniLogin property.
     * 
     * @return
     *     possible object is
     *     {@link UniLoginMini }
     *     
     */
    public UniLoginMini getUNILogin() {
        return uniLogin;
    }

    /**
     * Sets the value of the uniLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link UniLoginMini }
     *     
     */
    public void setUNILogin(UniLoginMini value) {
        this.uniLogin = value;
    }

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link PersonMini }
     *     
     */
    public PersonMini getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonMini }
     *     
     */
    public void setPerson(PersonMini value) {
        this.person = value;
    }

    /**
     * Gets the value of the student property.
     * 
     * @return
     *     possible object is
     *     {@link StudentMini }
     *     
     */
    public StudentMini getStudent() {
        return student;
    }

    /**
     * Sets the value of the student property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudentMini }
     *     
     */
    public void setStudent(StudentMini value) {
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
