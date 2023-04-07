
package https.wsieksport_unilogin_dk.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import https.unilogin.Credentials;
import https.unilogin.HentDataAftalerResponse;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.wsieksport_unilogin_dk.ws package. 
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

    private final static QName _EksporterXmlLille_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "eksporterXmlLille");
    private final static QName _EksporterXmlLilleResponse_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "eksporterXmlLilleResponse");
    private final static QName _EksporterXmlMellem_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "eksporterXmlMellem");
    private final static QName _EksporterXmlMellemResponse_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "eksporterXmlMellemResponse");
    private final static QName _EksporterXmlFuld_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "eksporterXmlFuld");
    private final static QName _EksporterXmlFuldResponse_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "eksporterXmlFuldResponse");
    private final static QName _EksporterXmlFuldMyndighed_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "eksporterXmlFuldMyndighed");
    private final static QName _EksporterXmlFuldMyndighedResponse_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "eksporterXmlFuldMyndighedResponse");
    private final static QName _HentDataAftalerLille_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "hentDataAftalerLille");
    private final static QName _HentDataAftalerLilleResponse_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "hentDataAftalerLilleResponse");
    private final static QName _HentDataAftalerMellem_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "hentDataAftalerMellem");
    private final static QName _HentDataAftalerMellemResponse_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "hentDataAftalerMellemResponse");
    private final static QName _HentDataAftalerFuld_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "hentDataAftalerFuld");
    private final static QName _HentDataAftalerFuldResponse_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "hentDataAftalerFuldResponse");
    private final static QName _HentDataAftalerFuldMyndighed_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "hentDataAftalerFuldMyndighed");
    private final static QName _HentDataAftalerFuldMyndighedResponse_QNAME = new QName("https://wsieksport.unilogin.dk/ws", "hentDataAftalerFuldMyndighedResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.wsieksport_unilogin_dk.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EksporterXmlResponseFullMyndighed }
     * 
     */
    public EksporterXmlResponseFullMyndighed createEksporterXmlResponseFullMyndighed() {
        return new EksporterXmlResponseFullMyndighed();
    }

    /**
     * Create an instance of {@link EksporterXmlResponseFull }
     * 
     */
    public EksporterXmlResponseFull createEksporterXmlResponseFull() {
        return new EksporterXmlResponseFull();
    }

    /**
     * Create an instance of {@link EksporterXmlResponseMedium }
     * 
     */
    public EksporterXmlResponseMedium createEksporterXmlResponseMedium() {
        return new EksporterXmlResponseMedium();
    }

    /**
     * Create an instance of {@link EksporterXmlResponseSmall }
     * 
     */
    public EksporterXmlResponseSmall createEksporterXmlResponseSmall() {
        return new EksporterXmlResponseSmall();
    }

    /**
     * Create an instance of {@link EksporterXml }
     * 
     */
    public EksporterXml createEksporterXml() {
        return new EksporterXml();
    }

    /**
     * Create an instance of {@link EksporterXmlResponseFullMyndighed.Xml }
     * 
     */
    public EksporterXmlResponseFullMyndighed.Xml createEksporterXmlResponseFullMyndighedXml() {
        return new EksporterXmlResponseFullMyndighed.Xml();
    }

    /**
     * Create an instance of {@link EksporterXmlResponseFull.Xml }
     * 
     */
    public EksporterXmlResponseFull.Xml createEksporterXmlResponseFullXml() {
        return new EksporterXmlResponseFull.Xml();
    }

    /**
     * Create an instance of {@link EksporterXmlResponseMedium.Xml }
     * 
     */
    public EksporterXmlResponseMedium.Xml createEksporterXmlResponseMediumXml() {
        return new EksporterXmlResponseMedium.Xml();
    }

    /**
     * Create an instance of {@link EksporterXmlResponseSmall.Xml }
     * 
     */
    public EksporterXmlResponseSmall.Xml createEksporterXmlResponseSmallXml() {
        return new EksporterXmlResponseSmall.Xml();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EksporterXml }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EksporterXml }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "eksporterXmlLille")
    public JAXBElement<EksporterXml> createEksporterXmlLille(EksporterXml value) {
        return new JAXBElement<EksporterXml>(_EksporterXmlLille_QNAME, EksporterXml.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EksporterXmlResponseSmall }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EksporterXmlResponseSmall }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "eksporterXmlLilleResponse")
    public JAXBElement<EksporterXmlResponseSmall> createEksporterXmlLilleResponse(EksporterXmlResponseSmall value) {
        return new JAXBElement<EksporterXmlResponseSmall>(_EksporterXmlLilleResponse_QNAME, EksporterXmlResponseSmall.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EksporterXml }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EksporterXml }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "eksporterXmlMellem")
    public JAXBElement<EksporterXml> createEksporterXmlMellem(EksporterXml value) {
        return new JAXBElement<EksporterXml>(_EksporterXmlMellem_QNAME, EksporterXml.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EksporterXmlResponseMedium }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EksporterXmlResponseMedium }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "eksporterXmlMellemResponse")
    public JAXBElement<EksporterXmlResponseMedium> createEksporterXmlMellemResponse(EksporterXmlResponseMedium value) {
        return new JAXBElement<EksporterXmlResponseMedium>(_EksporterXmlMellemResponse_QNAME, EksporterXmlResponseMedium.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EksporterXml }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EksporterXml }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "eksporterXmlFuld")
    public JAXBElement<EksporterXml> createEksporterXmlFuld(EksporterXml value) {
        return new JAXBElement<EksporterXml>(_EksporterXmlFuld_QNAME, EksporterXml.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EksporterXmlResponseFull }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EksporterXmlResponseFull }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "eksporterXmlFuldResponse")
    public JAXBElement<EksporterXmlResponseFull> createEksporterXmlFuldResponse(EksporterXmlResponseFull value) {
        return new JAXBElement<EksporterXmlResponseFull>(_EksporterXmlFuldResponse_QNAME, EksporterXmlResponseFull.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EksporterXml }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EksporterXml }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "eksporterXmlFuldMyndighed")
    public JAXBElement<EksporterXml> createEksporterXmlFuldMyndighed(EksporterXml value) {
        return new JAXBElement<EksporterXml>(_EksporterXmlFuldMyndighed_QNAME, EksporterXml.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EksporterXmlResponseFullMyndighed }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link EksporterXmlResponseFullMyndighed }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "eksporterXmlFuldMyndighedResponse")
    public JAXBElement<EksporterXmlResponseFullMyndighed> createEksporterXmlFuldMyndighedResponse(EksporterXmlResponseFullMyndighed value) {
        return new JAXBElement<EksporterXmlResponseFullMyndighed>(_EksporterXmlFuldMyndighedResponse_QNAME, EksporterXmlResponseFullMyndighed.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Credentials }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Credentials }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "hentDataAftalerLille")
    public JAXBElement<Credentials> createHentDataAftalerLille(Credentials value) {
        return new JAXBElement<Credentials>(_HentDataAftalerLille_QNAME, Credentials.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "hentDataAftalerLilleResponse")
    public JAXBElement<HentDataAftalerResponse> createHentDataAftalerLilleResponse(HentDataAftalerResponse value) {
        return new JAXBElement<HentDataAftalerResponse>(_HentDataAftalerLilleResponse_QNAME, HentDataAftalerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Credentials }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Credentials }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "hentDataAftalerMellem")
    public JAXBElement<Credentials> createHentDataAftalerMellem(Credentials value) {
        return new JAXBElement<Credentials>(_HentDataAftalerMellem_QNAME, Credentials.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "hentDataAftalerMellemResponse")
    public JAXBElement<HentDataAftalerResponse> createHentDataAftalerMellemResponse(HentDataAftalerResponse value) {
        return new JAXBElement<HentDataAftalerResponse>(_HentDataAftalerMellemResponse_QNAME, HentDataAftalerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Credentials }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Credentials }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "hentDataAftalerFuld")
    public JAXBElement<Credentials> createHentDataAftalerFuld(Credentials value) {
        return new JAXBElement<Credentials>(_HentDataAftalerFuld_QNAME, Credentials.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "hentDataAftalerFuldResponse")
    public JAXBElement<HentDataAftalerResponse> createHentDataAftalerFuldResponse(HentDataAftalerResponse value) {
        return new JAXBElement<HentDataAftalerResponse>(_HentDataAftalerFuldResponse_QNAME, HentDataAftalerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Credentials }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Credentials }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "hentDataAftalerFuldMyndighed")
    public JAXBElement<Credentials> createHentDataAftalerFuldMyndighed(Credentials value) {
        return new JAXBElement<Credentials>(_HentDataAftalerFuldMyndighed_QNAME, Credentials.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HentDataAftalerResponse }{@code >}
     */
    @XmlElementDecl(namespace = "https://wsieksport.unilogin.dk/ws", name = "hentDataAftalerFuldMyndighedResponse")
    public JAXBElement<HentDataAftalerResponse> createHentDataAftalerFuldMyndighedResponse(HentDataAftalerResponse value) {
        return new JAXBElement<HentDataAftalerResponse>(_HentDataAftalerFuldMyndighedResponse_QNAME, HentDataAftalerResponse.class, null, value);
    }

}
