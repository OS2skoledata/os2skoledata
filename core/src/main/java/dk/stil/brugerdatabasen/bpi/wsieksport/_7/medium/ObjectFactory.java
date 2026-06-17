
package dk.stil.brugerdatabasen.bpi.wsieksport._7.medium;

import javax.xml.namespace.QName;
import jakarta.annotation.Generated;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.stil.brugerdatabasen.bpi.wsieksport._7.medium package. 
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

    private static final QName _UNILoginExportMedium_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/medium", "UNILoginExportMedium");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.stil.brugerdatabasen.bpi.wsieksport._7.medium
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniLoginExportMedium }
     * 
     * @return
     *     the new instance of {@link UniLoginExportMedium }
     */
    public UniLoginExportMedium createUniLoginExportMedium() {
        return new UniLoginExportMedium();
    }

    /**
     * Create an instance of {@link UniLoginExportMediumBase }
     * 
     * @return
     *     the new instance of {@link UniLoginExportMediumBase }
     */
    public UniLoginExportMediumBase createUniLoginExportMediumBase() {
        return new UniLoginExportMediumBase();
    }

    /**
     * Create an instance of {@link InstitutionMedium }
     * 
     * @return
     *     the new instance of {@link InstitutionMedium }
     */
    public InstitutionMedium createInstitutionMedium() {
        return new InstitutionMedium();
    }

    /**
     * Create an instance of {@link InstitutionPersonMedium }
     * 
     * @return
     *     the new instance of {@link InstitutionPersonMedium }
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
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/medium", name = "UNILoginExportMedium")
    public JAXBElement<UniLoginExportMedium> createUNILoginExportMedium(UniLoginExportMedium value) {
        return new JAXBElement<>(_UNILoginExportMedium_QNAME, UniLoginExportMedium.class, null, value);
    }

}
