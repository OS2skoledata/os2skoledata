
package dk.stil.brugerdatabasen.bpi.wsieksport._7.small;

import javax.xml.namespace.QName;
import jakarta.annotation.Generated;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.stil.brugerdatabasen.bpi.wsieksport._7.small package. 
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

    private static final QName _UNILoginExportSmall_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/small", "UNILoginExportSmall");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.stil.brugerdatabasen.bpi.wsieksport._7.small
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UniLoginExportSmall }
     * 
     * @return
     *     the new instance of {@link UniLoginExportSmall }
     */
    public UniLoginExportSmall createUniLoginExportSmall() {
        return new UniLoginExportSmall();
    }

    /**
     * Create an instance of {@link UniLoginExportSmallBase }
     * 
     * @return
     *     the new instance of {@link UniLoginExportSmallBase }
     */
    public UniLoginExportSmallBase createUniLoginExportSmallBase() {
        return new UniLoginExportSmallBase();
    }

    /**
     * Create an instance of {@link InstitutionSmall }
     * 
     * @return
     *     the new instance of {@link InstitutionSmall }
     */
    public InstitutionSmall createInstitutionSmall() {
        return new InstitutionSmall();
    }

    /**
     * Create an instance of {@link InstitutionPersonSmall }
     * 
     * @return
     *     the new instance of {@link InstitutionPersonSmall }
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
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/small", name = "UNILoginExportSmall")
    public JAXBElement<UniLoginExportSmall> createUNILoginExportSmall(UniLoginExportSmall value) {
        return new JAXBElement<>(_UNILoginExportSmall_QNAME, UniLoginExportSmall.class, null, value);
    }

}
