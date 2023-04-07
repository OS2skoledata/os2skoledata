
package https.wsieksport_unilogin_dk.eksport.small;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.wsieksport_unilogin_dk.eksport.small package. 
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

    private final static QName _UNILoginExportSmall_QNAME = new QName("https://wsieksport.unilogin.dk/eksport/small", "UNILoginExportSmall");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.wsieksport_unilogin_dk.eksport.small
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniLoginExportSmall }
     * 
     */
    public UniLoginExportSmall createUniLoginExportSmall() {
        return new UniLoginExportSmall();
    }

    /**
     * Create an instance of {@link UniLoginExportSmallBase }
     * 
     */
    public UniLoginExportSmallBase createUniLoginExportSmallBase() {
        return new UniLoginExportSmallBase();
    }

    /**
     * Create an instance of {@link InstitutionSmall }
     * 
     */
    public InstitutionSmall createInstitutionSmall() {
        return new InstitutionSmall();
    }

    /**
     * Create an instance of {@link InstitutionPersonSmall }
     * 
     */
    public InstitutionPersonSmall createInstitutionPersonSmall() {
        return new InstitutionPersonSmall();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniLoginExportSmall }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UniLoginExportSmall }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/eksport/small", name = "UNILoginExportSmall")
    public JAXBElement<UniLoginExportSmall> createUNILoginExportSmall(UniLoginExportSmall value) {
        return new JAXBElement<UniLoginExportSmall>(_UNILoginExportSmall_QNAME, UniLoginExportSmall.class, null, value);
    }

}
