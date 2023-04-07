
package https.unilogin_dk.data.transitional;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.unilogin_dk.data.transitional package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EmailAddress_QNAME = new QName("https://unilogin.dk/data/transitional", "EmailAddress");
    private final static QName _CivilRegistrationNumber_QNAME = new QName("https://unilogin.dk/data/transitional", "CivilRegistrationNumber");
    private final static QName _Location_QNAME = new QName("https://unilogin.dk/data/transitional", "Location");
    private final static QName _Employee_QNAME = new QName("https://unilogin.dk/data/transitional", "Employee");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.unilogin_dk.data.transitional
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Employee }
     * 
     */
    public Employee createEmployee() {
        return new Employee();
    }

    /**
     * Create an instance of {@link PhoneNumberProtectable }
     * 
     */
    public PhoneNumberProtectable createPhoneNumberProtectable() {
        return new PhoneNumberProtectable();
    }

    /**
     * Create an instance of {@link PersonMedium }
     * 
     */
    public PersonMedium createPersonMedium() {
        return new PersonMedium();
    }

    /**
     * Create an instance of {@link PersonFullBase }
     * 
     */
    public PersonFullBase createPersonFullBase() {
        return new PersonFullBase();
    }

    /**
     * Create an instance of {@link UniLoginFull }
     * 
     */
    public UniLoginFull createUniLoginFull() {
        return new UniLoginFull();
    }

    /**
     * Create an instance of {@link StudentMini }
     * 
     */
    public StudentMini createStudentMini() {
        return new StudentMini();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data/transitional", name = "EmailAddress")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createEmailAddress(String value) {
        return new JAXBElement<String>(_EmailAddress_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data/transitional", name = "CivilRegistrationNumber")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createCivilRegistrationNumber(String value) {
        return new JAXBElement<String>(_CivilRegistrationNumber_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data/transitional", name = "Location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLocation(String value) {
        return new JAXBElement<String>(_Location_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Employee }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Employee }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data/transitional", name = "Employee")
    public JAXBElement<Employee> createEmployee(Employee value) {
        return new JAXBElement<Employee>(_Employee_QNAME, Employee.class, null, value);
    }

}
