
package https.wsieksport_unilogin_dk.eksport.fullmyndighed;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.wsieksport_unilogin_dk.eksport.fullmyndighed package. 
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

    private final static QName _UNILoginExportFullMyndighed_QNAME = new QName("https://wsieksport.unilogin.dk/eksport/fullmyndighed", "UNILoginExportFullMyndighed");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.wsieksport_unilogin_dk.eksport.fullmyndighed
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniLoginExportFullMyndighed }
     * 
     */
    public UniLoginExportFullMyndighed createUniLoginExportFullMyndighed() {
        return new UniLoginExportFullMyndighed();
    }

    /**
     * Create an instance of {@link UniLoginExportFullMyndighedBase }
     * 
     */
    public UniLoginExportFullMyndighedBase createUniLoginExportFullMyndighedBase() {
        return new UniLoginExportFullMyndighedBase();
    }

    /**
     * Create an instance of {@link InstitutionFullMyndighed }
     * 
     */
    public InstitutionFullMyndighed createInstitutionFullMyndighed() {
        return new InstitutionFullMyndighed();
    }

    /**
     * Create an instance of {@link InstitutionPersonFullMyndighed }
     * 
     */
    public InstitutionPersonFullMyndighed createInstitutionPersonFullMyndighed() {
        return new InstitutionPersonFullMyndighed();
    }

    /**
     * Create an instance of {@link PersonFullMyndighed }
     * 
     */
    public PersonFullMyndighed createPersonFullMyndighed() {
        return new PersonFullMyndighed();
    }

    /**
     * Create an instance of {@link StudentFullMyndighed }
     * 
     */
    public StudentFullMyndighed createStudentFullMyndighed() {
        return new StudentFullMyndighed();
    }

    /**
     * Create an instance of {@link ContactPersonFullMyndighed }
     * 
     */
    public ContactPersonFullMyndighed createContactPersonFullMyndighed() {
        return new ContactPersonFullMyndighed();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniLoginExportFullMyndighed }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UniLoginExportFullMyndighed }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/eksport/fullmyndighed", name = "UNILoginExportFullMyndighed")
    public JAXBElement<UniLoginExportFullMyndighed> createUNILoginExportFullMyndighed(UniLoginExportFullMyndighed value) {
        return new JAXBElement<UniLoginExportFullMyndighed>(_UNILoginExportFullMyndighed_QNAME, UniLoginExportFullMyndighed.class, null, value);
    }

}
