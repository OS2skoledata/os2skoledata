
package https.wsieksport_unilogin_dk.eksport.full;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.wsieksport_unilogin_dk.eksport.full package. 
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

    private final static QName _UNILoginExportFull_QNAME = new QName("https://wsieksport.unilogin.dk/eksport/full", "UNILoginExportFull");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.wsieksport_unilogin_dk.eksport.full
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniLoginExportFull }
     * 
     */
    public UniLoginExportFull createUniLoginExportFull() {
        return new UniLoginExportFull();
    }

    /**
     * Create an instance of {@link UniLoginExportFullBase }
     * 
     */
    public UniLoginExportFullBase createUniLoginExportFullBase() {
        return new UniLoginExportFullBase();
    }

    /**
     * Create an instance of {@link InstitutionFull }
     * 
     */
    public InstitutionFull createInstitutionFull() {
        return new InstitutionFull();
    }

    /**
     * Create an instance of {@link InstitutionPersonFull }
     * 
     */
    public InstitutionPersonFull createInstitutionPersonFull() {
        return new InstitutionPersonFull();
    }

    /**
     * Create an instance of {@link PersonFull }
     * 
     */
    public PersonFull createPersonFull() {
        return new PersonFull();
    }

    /**
     * Create an instance of {@link StudentFull }
     * 
     */
    public StudentFull createStudentFull() {
        return new StudentFull();
    }

    /**
     * Create an instance of {@link ContactPersonFull }
     * 
     */
    public ContactPersonFull createContactPersonFull() {
        return new ContactPersonFull();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniLoginExportFull }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UniLoginExportFull }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/eksport/full", name = "UNILoginExportFull")
    public JAXBElement<UniLoginExportFull> createUNILoginExportFull(UniLoginExportFull value) {
        return new JAXBElement<UniLoginExportFull>(_UNILoginExportFull_QNAME, UniLoginExportFull.class, null, value);
    }

}
