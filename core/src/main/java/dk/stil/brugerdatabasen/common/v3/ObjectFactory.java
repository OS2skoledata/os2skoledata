
package dk.stil.brugerdatabasen.common.v3;

import javax.xml.namespace.QName;
import jakarta.annotation.Generated;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.stil.brugerdatabasen.common.v3 package. 
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

    private static final QName _HelloWorld_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "helloWorld");
    private static final QName _HelloWorldResponse_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "helloWorldResponse");
    private static final QName _HelloWorldWithCertificate_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "helloWorldWithCertificate");
    private static final QName _HelloWorldWithCertificateResponse_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "helloWorldWithCertificateResponse");
    private static final QName _HelloWorldWithHeaderCredentials_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "helloWorldWithHeaderCredentials");
    private static final QName _HelloWorldWithHeaderCredentialsResponse_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "helloWorldWithHeaderCredentialsResponse");
    private static final QName _AuthentificationError_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "authentificationError");
    private static final QName _HentDataAftaler_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "hentDataAftaler");
    private static final QName _HentDataAftalerResponse_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "hentDataAftalerResponse");
    private static final QName _UdbydersystemId_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "UdbydersystemId");
    private static final QName _GroupId_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "GroupId");
    private static final QName _LocalPersonId_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/common/3", "LocalPersonId");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.stil.brugerdatabasen.common.v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NoArgs }
     * 
     * @return
     *     the new instance of {@link NoArgs }
     */
    public NoArgs createNoArgs() {
        return new NoArgs();
    }

    /**
     * Create an instance of {@link HelloWorldResponse }
     * 
     * @return
     *     the new instance of {@link HelloWorldResponse }
     */
    public HelloWorldResponse createHelloWorldResponse() {
        return new HelloWorldResponse();
    }

    /**
     * Create an instance of {@link AuthentificationError }
     * 
     * @return
     *     the new instance of {@link AuthentificationError }
     */
    public AuthentificationError createAuthentificationError() {
        return new AuthentificationError();
    }

    /**
     * Create an instance of {@link HentDataAftalerResponse }
     * 
     * @return
     *     the new instance of {@link HentDataAftalerResponse }
     */
    public HentDataAftalerResponse createHentDataAftalerResponse() {
        return new HentDataAftalerResponse();
    }

    /**
     * Create an instance of {@link UdbydersystemIdType }
     * 
     * @return
     *     the new instance of {@link UdbydersystemIdType }
     */
    public UdbydersystemIdType createUdbydersystemIdType() {
        return new UdbydersystemIdType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NoArgs }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NoArgs }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "helloWorld")
    public JAXBElement<NoArgs> createHelloWorld(NoArgs value) {
        return new JAXBElement<>(_HelloWorld_QNAME, NoArgs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HelloWorldResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HelloWorldResponse }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "helloWorldResponse")
    public JAXBElement<HelloWorldResponse> createHelloWorldResponse(HelloWorldResponse value) {
        return new JAXBElement<>(_HelloWorldResponse_QNAME, HelloWorldResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NoArgs }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NoArgs }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "helloWorldWithCertificate")
    public JAXBElement<NoArgs> createHelloWorldWithCertificate(NoArgs value) {
        return new JAXBElement<>(_HelloWorldWithCertificate_QNAME, NoArgs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HelloWorldResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HelloWorldResponse }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "helloWorldWithCertificateResponse")
    public JAXBElement<HelloWorldResponse> createHelloWorldWithCertificateResponse(HelloWorldResponse value) {
        return new JAXBElement<>(_HelloWorldWithCertificateResponse_QNAME, HelloWorldResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NoArgs }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NoArgs }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "helloWorldWithHeaderCredentials")
    public JAXBElement<NoArgs> createHelloWorldWithHeaderCredentials(NoArgs value) {
        return new JAXBElement<>(_HelloWorldWithHeaderCredentials_QNAME, NoArgs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HelloWorldResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HelloWorldResponse }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "helloWorldWithHeaderCredentialsResponse")
    public JAXBElement<HelloWorldResponse> createHelloWorldWithHeaderCredentialsResponse(HelloWorldResponse value) {
        return new JAXBElement<>(_HelloWorldWithHeaderCredentialsResponse_QNAME, HelloWorldResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthentificationError }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link AuthentificationError }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "authentificationError")
    public JAXBElement<AuthentificationError> createAuthentificationError(AuthentificationError value) {
        return new JAXBElement<>(_AuthentificationError_QNAME, AuthentificationError.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NoArgs }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link NoArgs }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "hentDataAftaler")
    public JAXBElement<NoArgs> createHentDataAftaler(NoArgs value) {
        return new JAXBElement<>(_HentDataAftaler_QNAME, NoArgs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "hentDataAftalerResponse")
    public JAXBElement<HentDataAftalerResponse> createHentDataAftalerResponse(HentDataAftalerResponse value) {
        return new JAXBElement<>(_HentDataAftalerResponse_QNAME, HentDataAftalerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UdbydersystemIdType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UdbydersystemIdType }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "UdbydersystemId")
    public JAXBElement<UdbydersystemIdType> createUdbydersystemId(UdbydersystemIdType value) {
        return new JAXBElement<>(_UdbydersystemId_QNAME, UdbydersystemIdType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "GroupId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createGroupId(String value) {
        return new JAXBElement<>(_GroupId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/common/3", name = "LocalPersonId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLocalPersonId(String value) {
        return new JAXBElement<>(_LocalPersonId_QNAME, String.class, null, value);
    }

}
