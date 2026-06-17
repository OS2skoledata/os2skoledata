
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import javax.xml.datatype.XMLGregorianCalendar;
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
 * generated in the dk.stil.brugerdatabasen.bpi.wsieksport._7.common package. 
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

    private static final QName _ShortName_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "ShortName");
    private static final QName _Occupation_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "Occupation");
    private static final QName _Location_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "Location");
    private static final QName _Name_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "Name");
    private static final QName _UserId_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "UserId");
    private static final QName _FirstName_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "FirstName");
    private static final QName _FamilyName_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "FamilyName");
    private static final QName _CivilRegistrationNumber_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "CivilRegistrationNumber");
    private static final QName _CivilRegistrationNumberType_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "CivilRegistrationNumberType");
    private static final QName _EmailAddress_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "EmailAddress");
    private static final QName _BirthDate_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "BirthDate");
    private static final QName _Gender_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "Gender");
    private static final QName _PhotoId_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "PhotoId");
    private static final QName _Address_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "Address");
    private static final QName _StreetAddress_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "StreetAddress");
    private static final QName _PostalCode_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "PostalCode");
    private static final QName _PostalDistrict_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "PostalDistrict");
    private static final QName _CountryCode_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "CountryCode");
    private static final QName _Country_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "Country");
    private static final QName _MunicipalityCode_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "MunicipalityCode");
    private static final QName _MunicipalityName_QNAME = new QName("https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", "MunicipalityName");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.stil.brugerdatabasen.bpi.wsieksport._7.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Extern }
     * 
     * @return
     *     the new instance of {@link Extern }
     */
    public Extern createExtern() {
        return new Extern();
    }

    /**
     * Create an instance of {@link Employee }
     * 
     * @return
     *     the new instance of {@link Employee }
     */
    public Employee createEmployee() {
        return new Employee();
    }

    /**
     * Create an instance of {@link ImportSource }
     * 
     * @return
     *     the new instance of {@link ImportSource }
     */
    public ImportSource createImportSource() {
        return new ImportSource();
    }

    /**
     * Create an instance of {@link Address }
     * 
     * @return
     *     the new instance of {@link Address }
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link UniLoginExportBase }
     * 
     * @return
     *     the new instance of {@link UniLoginExportBase }
     */
    public UniLoginExportBase createUniLoginExportBase() {
        return new UniLoginExportBase();
    }

    /**
     * Create an instance of {@link InstitutionPersonExportBase }
     * 
     * @return
     *     the new instance of {@link InstitutionPersonExportBase }
     */
    public InstitutionPersonExportBase createInstitutionPersonExportBase() {
        return new InstitutionPersonExportBase();
    }

    /**
     * Create an instance of {@link InstitutionPersonExportFullBase }
     * 
     * @return
     *     the new instance of {@link InstitutionPersonExportFullBase }
     */
    public InstitutionPersonExportFullBase createInstitutionPersonExportFullBase() {
        return new InstitutionPersonExportFullBase();
    }

    /**
     * Create an instance of {@link PersonMini }
     * 
     * @return
     *     the new instance of {@link PersonMini }
     */
    public PersonMini createPersonMini() {
        return new PersonMini();
    }

    /**
     * Create an instance of {@link PersonMedium }
     * 
     * @return
     *     the new instance of {@link PersonMedium }
     */
    public PersonMedium createPersonMedium() {
        return new PersonMedium();
    }

    /**
     * Create an instance of {@link PersonFull }
     * 
     * @return
     *     the new instance of {@link PersonFull }
     */
    public PersonFull createPersonFull() {
        return new PersonFull();
    }

    /**
     * Create an instance of {@link ContactPersonBase }
     * 
     * @return
     *     the new instance of {@link ContactPersonBase }
     */
    public ContactPersonBase createContactPersonBase() {
        return new ContactPersonBase();
    }

    /**
     * Create an instance of {@link StudentMini }
     * 
     * @return
     *     the new instance of {@link StudentMini }
     */
    public StudentMini createStudentMini() {
        return new StudentMini();
    }

    /**
     * Create an instance of {@link Group }
     * 
     * @return
     *     the new instance of {@link Group }
     */
    public Group createGroup() {
        return new Group();
    }

    /**
     * Create an instance of {@link InstitutionBase }
     * 
     * @return
     *     the new instance of {@link InstitutionBase }
     */
    public InstitutionBase createInstitutionBase() {
        return new InstitutionBase();
    }

    /**
     * Create an instance of {@link InstitutionWithGroupsBase }
     * 
     * @return
     *     the new instance of {@link InstitutionWithGroupsBase }
     */
    public InstitutionWithGroupsBase createInstitutionWithGroupsBase() {
        return new InstitutionWithGroupsBase();
    }

    /**
     * Create an instance of {@link PhoneNumberProtectable }
     * 
     * @return
     *     the new instance of {@link PhoneNumberProtectable }
     */
    public PhoneNumberProtectable createPhoneNumberProtectable() {
        return new PhoneNumberProtectable();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "ShortName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createShortName(String value) {
        return new JAXBElement<>(_ShortName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "Occupation")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createOccupation(String value) {
        return new JAXBElement<>(_Occupation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "Location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLocation(String value) {
        return new JAXBElement<>(_Location_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "Name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "UserId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createUserId(String value) {
        return new JAXBElement<>(_UserId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "FirstName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createFirstName(String value) {
        return new JAXBElement<>(_FirstName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "FamilyName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createFamilyName(String value) {
        return new JAXBElement<>(_FamilyName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "CivilRegistrationNumber")
    public JAXBElement<String> createCivilRegistrationNumber(String value) {
        return new JAXBElement<>(_CivilRegistrationNumber_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CprType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CprType }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "CivilRegistrationNumberType")
    public JAXBElement<CprType> createCivilRegistrationNumberType(CprType value) {
        return new JAXBElement<>(_CivilRegistrationNumberType_QNAME, CprType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "EmailAddress")
    public JAXBElement<String> createEmailAddress(String value) {
        return new JAXBElement<>(_EmailAddress_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "BirthDate")
    public JAXBElement<XMLGregorianCalendar> createBirthDate(XMLGregorianCalendar value) {
        return new JAXBElement<>(_BirthDate_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Gender }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Gender }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "Gender")
    public JAXBElement<Gender> createGender(Gender value) {
        return new JAXBElement<>(_Gender_QNAME, Gender.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "PhotoId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPhotoId(String value) {
        return new JAXBElement<>(_PhotoId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Address }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Address }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "Address")
    public JAXBElement<Address> createAddress(Address value) {
        return new JAXBElement<>(_Address_QNAME, Address.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "StreetAddress")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createStreetAddress(String value) {
        return new JAXBElement<>(_StreetAddress_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "PostalCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPostalCode(String value) {
        return new JAXBElement<>(_PostalCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "PostalDistrict")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPostalDistrict(String value) {
        return new JAXBElement<>(_PostalDistrict_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountryCode }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CountryCode }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "CountryCode")
    public JAXBElement<CountryCode> createCountryCode(CountryCode value) {
        return new JAXBElement<>(_CountryCode_QNAME, CountryCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "Country")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createCountry(String value) {
        return new JAXBElement<>(_Country_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "MunicipalityCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createMunicipalityCode(String value) {
        return new JAXBElement<>(_MunicipalityCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common", name = "MunicipalityName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createMunicipalityName(String value) {
        return new JAXBElement<>(_MunicipalityName_QNAME, String.class, null, value);
    }

}
