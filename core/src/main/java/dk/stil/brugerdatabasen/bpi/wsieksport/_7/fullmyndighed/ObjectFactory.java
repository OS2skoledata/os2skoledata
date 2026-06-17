
package dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed;

import javax.xml.namespace.QName;
import jakarta.annotation.Generated;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed package. 
 * <p>An ObjectFactory allows you to programmatically 
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
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class ObjectFactory {

    private static final QName _UNILoginExportFullMyndighed_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/fullmyndighed", "UNILoginExportFullMyndighed");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniLoginExportFullMyndighed }
     * 
     * @return
     *     the new instance of {@link UniLoginExportFullMyndighed }
     */
    public UniLoginExportFullMyndighed createUniLoginExportFullMyndighed() {
        return new UniLoginExportFullMyndighed();
    }

    /**
     * Create an instance of {@link UniLoginExportFullMyndighedBase }
     * 
     * @return
     *     the new instance of {@link UniLoginExportFullMyndighedBase }
     */
    public UniLoginExportFullMyndighedBase createUniLoginExportFullMyndighedBase() {
        return new UniLoginExportFullMyndighedBase();
    }

    /**
     * Create an instance of {@link InstitutionFullMyndighed }
     * 
     * @return
     *     the new instance of {@link InstitutionFullMyndighed }
     */
    public InstitutionFullMyndighed createInstitutionFullMyndighed() {
        return new InstitutionFullMyndighed();
    }

    /**
     * Create an instance of {@link InstitutionPersonFullMyndighed }
     * 
     * @return
     *     the new instance of {@link InstitutionPersonFullMyndighed }
     */
    public InstitutionPersonFullMyndighed createInstitutionPersonFullMyndighed() {
        return new InstitutionPersonFullMyndighed();
    }

    /**
     * Create an instance of {@link PersonFullMyndighed }
     * 
     * @return
     *     the new instance of {@link PersonFullMyndighed }
     */
    public PersonFullMyndighed createPersonFullMyndighed() {
        return new PersonFullMyndighed();
    }

    /**
     * Create an instance of {@link StudentFullMyndighed }
     * 
     * @return
     *     the new instance of {@link StudentFullMyndighed }
     */
    public StudentFullMyndighed createStudentFullMyndighed() {
        return new StudentFullMyndighed();
    }

    /**
     * Create an instance of {@link ContactPersonFullMyndighed }
     * 
     * @return
     *     the new instance of {@link ContactPersonFullMyndighed }
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
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/fullmyndighed", name = "UNILoginExportFullMyndighed")
    public JAXBElement<UniLoginExportFullMyndighed> createUNILoginExportFullMyndighed(UniLoginExportFullMyndighed value) {
        return new JAXBElement<>(_UNILoginExportFullMyndighed_QNAME, UniLoginExportFullMyndighed.class, null, value);
    }

}
