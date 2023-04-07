
package https.wsieksport_unilogin_dk.eksport.medium;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.wsieksport_unilogin_dk.eksport.medium package. 
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

    private final static QName _UNILoginExportMedium_QNAME = new QName("https://wsieksport.unilogin.dk/eksport/medium", "UNILoginExportMedium");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.wsieksport_unilogin_dk.eksport.medium
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniLoginExportMedium }
     * 
     */
    public UniLoginExportMedium createUniLoginExportMedium() {
        return new UniLoginExportMedium();
    }

    /**
     * Create an instance of {@link UniLoginExportMediumBase }
     * 
     */
    public UniLoginExportMediumBase createUniLoginExportMediumBase() {
        return new UniLoginExportMediumBase();
    }

    /**
     * Create an instance of {@link InstitutionMedium }
     * 
     */
    public InstitutionMedium createInstitutionMedium() {
        return new InstitutionMedium();
    }

    /**
     * Create an instance of {@link InstitutionPersonMedium }
     * 
     */
    public InstitutionPersonMedium createInstitutionPersonMedium() {
        return new InstitutionPersonMedium();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UniLoginExportMedium }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UniLoginExportMedium }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/eksport/medium", name = "UNILoginExportMedium")
    public JAXBElement<UniLoginExportMedium> createUNILoginExportMedium(UniLoginExportMedium value) {
        return new JAXBElement<UniLoginExportMedium>(_UNILoginExportMedium_QNAME, UniLoginExportMedium.class, null, value);
    }

}
