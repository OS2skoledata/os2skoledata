
package dk.stil.brugerdatabasen.bpi.wsieksport._7.full;

import javax.xml.namespace.QName;
import jakarta.annotation.Generated;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.stil.brugerdatabasen.bpi.wsieksport._7.full package. 
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

    private static final QName _UNILoginExportFull_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/full", "UNILoginExportFull");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.stil.brugerdatabasen.bpi.wsieksport._7.full
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniLoginExportFull }
     * 
     * @return
     *     the new instance of {@link UniLoginExportFull }
     */
    public UniLoginExportFull createUniLoginExportFull() {
        return new UniLoginExportFull();
    }

    /**
     * Create an instance of {@link UniLoginExportFullBase }
     * 
     * @return
     *     the new instance of {@link UniLoginExportFullBase }
     */
    public UniLoginExportFullBase createUniLoginExportFullBase() {
        return new UniLoginExportFullBase();
    }

    /**
     * Create an instance of {@link InstitutionFull }
     * 
     * @return
     *     the new instance of {@link InstitutionFull }
     */
    public InstitutionFull createInstitutionFull() {
        return new InstitutionFull();
    }

    /**
     * Create an instance of {@link InstitutionPersonFull }
     * 
     * @return
     *     the new instance of {@link InstitutionPersonFull }
     */
    public InstitutionPersonFull createInstitutionPersonFull() {
        return new InstitutionPersonFull();
    }

    /**
     * Create an instance of {@link StudentFull }
     * 
     * @return
     *     the new instance of {@link StudentFull }
     */
    public StudentFull createStudentFull() {
        return new StudentFull();
    }

    /**
     * Create an instance of {@link ContactPersonFull }
     * 
     * @return
     *     the new instance of {@link ContactPersonFull }
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
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/full", name = "UNILoginExportFull")
    public JAXBElement<UniLoginExportFull> createUNILoginExportFull(UniLoginExportFull value) {
        return new JAXBElement<>(_UNILoginExportFull_QNAME, UniLoginExportFull.class, null, value);
    }

}
