
package dk.stil.brugerdatabasen.bpi.wsieksport._7.full;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.Employee;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.Extern;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.InstitutionPersonExportFullBase;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.PersonFull;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for institutionPersonFull complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="institutionPersonFull"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}institutionPersonExportFullBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Person" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}personFull" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="Student" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/full}studentFull"/&gt;
 *           &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}Employee"/&gt;
 *           &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}Extern"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
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
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class InstitutionPersonFull
    extends InstitutionPersonExportFullBase
{

    /**
     * Elementet er valgfrit, da det ikke findes på brugere oprettet manuelt uden for
     *                                 importsystemet.
     * 
     */
    @XmlElement(name = "Person")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected PersonFull person;
    /**
     * Elevoplysninger om personen.
     * 
     */
    @XmlElement(name = "Student")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected StudentFull student;
    @XmlElement(name = "Employee", namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected Employee employee;
    /**
     * Oplysninger om ekstern person.
     * 
     */
    @XmlElement(name = "Extern", namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected Extern extern;

    /**
     * Elementet er valgfrit, da det ikke findes på brugere oprettet manuelt uden for
     *                                 importsystemet.
     * 
     * @return
     *     possible object is
     *     {@link PersonFull }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getPerson()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setPerson(PersonFull value) {
        this.person = value;
    }

    /**
     * Elevoplysninger om personen.
     * 
     * @return
     *     possible object is
     *     {@link StudentFull }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getStudent()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setEmployee(Employee value) {
        this.employee = value;
    }

    /**
     * Oplysninger om ekstern person.
     * 
     * @return
     *     possible object is
     *     {@link Extern }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getExtern()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setExtern(Extern value) {
        this.extern = value;
    }

}
