package https.wsieksport_unilogin_dk.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.3.3
 * 2022-09-26T13:01:48.435+02:00
 * Generated source version: 3.3.3
 *
 */
@WebService(targetNamespace = "https://wsieksport.unilogin.dk/ws", name = "WsiEksportPortType")
@XmlSeeAlso({https.unilogin.ObjectFactory.class, https.wsieksport_unilogin_dk.eksport.full.ObjectFactory.class, https.wsieksport_unilogin_dk.eksport.medium.ObjectFactory.class, https.wsieksport_unilogin_dk.eksport.small.ObjectFactory.class, ObjectFactory.class, https.unilogin_dk.data.transitional.ObjectFactory.class, https.wsieksport_unilogin_dk.eksport.fullmyndighed.ObjectFactory.class, https.wsieksport_unilogin_dk.eksport.ObjectFactory.class, https.unilogin_dk.data.ObjectFactory.class})
public interface WsiEksportPortType {

    /**
     * Ping webservicen med simpelt kald. Returnerer "HelloWorld" samt tidspunkt fra databasen hvis webservicen og databasen svarer.
     */
    @WebMethod(action = "https://wsieksport.unilogin.dk/helloWorldWithDB")
    @RequestWrapper(localName = "helloWorldWithDB", targetNamespace = "https://unilogin.dk", className = "https.unilogin.NoArgs")
    @ResponseWrapper(localName = "helloWorldWithDBResponse", targetNamespace = "https://unilogin.dk", className = "https.unilogin.HelloWorldResponse")
    @WebResult(name = "helloWorldResult", targetNamespace = "https://unilogin.dk")
    public java.lang.String helloWorldWithDB()
;

    /**
     * Ping webservicen med simpelt kald. Returnerer "HelloWorldWithDBAndCredentials" hvis webservicen svarer og brugernavn/password er korrekt angivet.
     */
    @WebMethod(action = "https://wsieksport.unilogin.dk/helloWorldWithDBAndCredentials")
    @RequestWrapper(localName = "helloWorldWithDBAndCredentials", targetNamespace = "https://unilogin.dk", className = "https.unilogin.Credentials")
    @ResponseWrapper(localName = "helloWorldWithDBAndCredentialsResponse", targetNamespace = "https://unilogin.dk", className = "https.unilogin.HelloWorldResponse")
    @WebResult(name = "helloWorldResult", targetNamespace = "https://unilogin.dk")
    public java.lang.String helloWorldWithDBAndCredentials(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword
    ) throws AuthentificationFault;

    @WebMethod(action = "https://wsieksport.unilogin.dk/eksporterXmlFuld")
    @RequestWrapper(localName = "eksporterXmlFuld", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.wsieksport_unilogin_dk.ws.EksporterXml")
    @ResponseWrapper(localName = "eksporterXmlFuldResponse", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.wsieksport_unilogin_dk.ws.EksporterXmlResponseFull")
    @WebResult(name = "xml", targetNamespace = "https://wsieksport.unilogin.dk/ws")
    public https.wsieksport_unilogin_dk.ws.EksporterXmlResponseFull.Xml eksporterXmlFuld(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword,
        @WebParam(name = "instnr", targetNamespace = "https://wsieksport.unilogin.dk/ws")
        java.lang.String instnr
    ) throws AuthentificationFault;

    /**
     * Ping webservicen med simpelt kald. Returnerer "HelloWorld" hvis webservicen svarer.
     */
    @WebMethod(action = "https://wsieksport.unilogin.dk/helloWorld")
    @RequestWrapper(localName = "helloWorld", targetNamespace = "https://unilogin.dk", className = "https.unilogin.NoArgs")
    @ResponseWrapper(localName = "helloWorldResponse", targetNamespace = "https://unilogin.dk", className = "https.unilogin.HelloWorldResponse")
    @WebResult(name = "helloWorldResult", targetNamespace = "https://unilogin.dk")
    public java.lang.String helloWorld()
;

    @WebMethod(action = "https://wsieksport.unilogin.dk/eksporterXmlFuldMyndighed")
    @RequestWrapper(localName = "eksporterXmlFuldMyndighed", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.wsieksport_unilogin_dk.ws.EksporterXml")
    @ResponseWrapper(localName = "eksporterXmlFuldMyndighedResponse", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.wsieksport_unilogin_dk.ws.EksporterXmlResponseFullMyndighed")
    @WebResult(name = "xml", targetNamespace = "https://wsieksport.unilogin.dk/ws")
    public https.wsieksport_unilogin_dk.ws.EksporterXmlResponseFullMyndighed.Xml eksporterXmlFuldMyndighed(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword,
        @WebParam(name = "instnr", targetNamespace = "https://wsieksport.unilogin.dk/ws")
        java.lang.String instnr
    ) throws AuthentificationFault;

    /**
     * Returnerer en liste af institutionsnumre, hvor der er lavet en dataaftale til lille eksport
     */
    @WebMethod(action = "https://wsieksport.unilogin.dk/hentDataAftalerLille")
    @RequestWrapper(localName = "hentDataAftalerLille", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.unilogin.Credentials")
    @ResponseWrapper(localName = "hentDataAftalerLilleResponse", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.unilogin.HentDataAftalerResponse")
    @WebResult(name = "regnr", targetNamespace = "https://unilogin.dk")
    public java.util.List<java.lang.String> hentDataAftalerLille(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword
    ) throws AuthentificationFault;

    /**
     * Returnerer en liste af institutionsnumre, hvor der er lavet en dataaftale til fuld eksport
     */
    @WebMethod(action = "https://wsieksport.unilogin.dk/hentDataAftalerFuldMyndighed")
    @RequestWrapper(localName = "hentDataAftalerFuldMyndighed", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.unilogin.Credentials")
    @ResponseWrapper(localName = "hentDataAftalerFuldMyndighedResponse", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.unilogin.HentDataAftalerResponse")
    @WebResult(name = "regnr", targetNamespace = "https://unilogin.dk")
    public java.util.List<java.lang.String> hentDataAftalerFuldMyndighed(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword
    ) throws AuthentificationFault;

    @WebMethod(action = "https://wsieksport.unilogin.dk/eksporterXmlMellem")
    @RequestWrapper(localName = "eksporterXmlMellem", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.wsieksport_unilogin_dk.ws.EksporterXml")
    @ResponseWrapper(localName = "eksporterXmlMellemResponse", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.wsieksport_unilogin_dk.ws.EksporterXmlResponseMedium")
    @WebResult(name = "xml", targetNamespace = "https://wsieksport.unilogin.dk/ws")
    public https.wsieksport_unilogin_dk.ws.EksporterXmlResponseMedium.Xml eksporterXmlMellem(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword,
        @WebParam(name = "instnr", targetNamespace = "https://wsieksport.unilogin.dk/ws")
        java.lang.String instnr
    ) throws AuthentificationFault;

    /**
     * Returnerer en liste af institutionsnumre, hvor der er lavet en dataaftale til mellem eksport
     */
    @WebMethod(action = "https://wsieksport.unilogin.dk/hentDataAftalerMellem")
    @RequestWrapper(localName = "hentDataAftalerMellem", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.unilogin.Credentials")
    @ResponseWrapper(localName = "hentDataAftalerMellemResponse", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.unilogin.HentDataAftalerResponse")
    @WebResult(name = "regnr", targetNamespace = "https://unilogin.dk")
    public java.util.List<java.lang.String> hentDataAftalerMellem(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword
    ) throws AuthentificationFault;

    @WebMethod(action = "https://wsieksport.unilogin.dk/eksporterXmlLille")
    @RequestWrapper(localName = "eksporterXmlLille", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.wsieksport_unilogin_dk.ws.EksporterXml")
    @ResponseWrapper(localName = "eksporterXmlLilleResponse", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.wsieksport_unilogin_dk.ws.EksporterXmlResponseSmall")
    @WebResult(name = "xml", targetNamespace = "https://wsieksport.unilogin.dk/ws")
    public https.wsieksport_unilogin_dk.ws.EksporterXmlResponseSmall.Xml eksporterXmlLille(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword,
        @WebParam(name = "instnr", targetNamespace = "https://wsieksport.unilogin.dk/ws")
        java.lang.String instnr
    ) throws AuthentificationFault;

    /**
     * Returnerer en liste af institutionsnumre, hvor der er lavet en dataaftale til fuld eksport
     */
    @WebMethod(action = "https://wsieksport.unilogin.dk/hentDataAftalerFuld")
    @RequestWrapper(localName = "hentDataAftalerFuld", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.unilogin.Credentials")
    @ResponseWrapper(localName = "hentDataAftalerFuldResponse", targetNamespace = "https://wsieksport.unilogin.dk/ws", className = "https.unilogin.HentDataAftalerResponse")
    @WebResult(name = "regnr", targetNamespace = "https://unilogin.dk")
    public java.util.List<java.lang.String> hentDataAftalerFuld(

        @WebParam(name = "wsBrugerid", targetNamespace = "https://unilogin.dk")
        java.lang.String wsBrugerid,
        @WebParam(name = "wsPassword", targetNamespace = "https://unilogin.dk")
        java.lang.String wsPassword
    ) throws AuthentificationFault;
}
