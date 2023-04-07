
package https.unilogin_dk.data;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.unilogin_dk.data package. 
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

    private final static QName _LocalPersonId_QNAME = new QName("https://unilogin.dk/data", "LocalPersonId");
    private final static QName _FirstName_QNAME = new QName("https://unilogin.dk/data", "FirstName");
    private final static QName _FamilyName_QNAME = new QName("https://unilogin.dk/data", "FamilyName");
    private final static QName _CivilRegistrationNumber_QNAME = new QName("https://unilogin.dk/data", "CivilRegistrationNumber");
    private final static QName _EmailAddress_QNAME = new QName("https://unilogin.dk/data", "EmailAddress");
    private final static QName _BirthDate_QNAME = new QName("https://unilogin.dk/data", "BirthDate");
    private final static QName _Gender_QNAME = new QName("https://unilogin.dk/data", "Gender");
    private final static QName _PhotoId_QNAME = new QName("https://unilogin.dk/data", "PhotoId");
    private final static QName _Address_QNAME = new QName("https://unilogin.dk/data", "Address");
    private final static QName _StreetAddress_QNAME = new QName("https://unilogin.dk/data", "StreetAddress");
    private final static QName _PostalCode_QNAME = new QName("https://unilogin.dk/data", "PostalCode");
    private final static QName _PostalDistrict_QNAME = new QName("https://unilogin.dk/data", "PostalDistrict");
    private final static QName _CountryCode_QNAME = new QName("https://unilogin.dk/data", "CountryCode");
    private final static QName _Country_QNAME = new QName("https://unilogin.dk/data", "Country");
    private final static QName _MunicipalityCode_QNAME = new QName("https://unilogin.dk/data", "MunicipalityCode");
    private final static QName _MunicipalityName_QNAME = new QName("https://unilogin.dk/data", "MunicipalityName");
    private final static QName _Employee_QNAME = new QName("https://unilogin.dk/data", "Employee");
    private final static QName _ShortName_QNAME = new QName("https://unilogin.dk/data", "ShortName");
    private final static QName _Occupation_QNAME = new QName("https://unilogin.dk/data", "Occupation");
    private final static QName _Location_QNAME = new QName("https://unilogin.dk/data", "Location");
    private final static QName _GroupId_QNAME = new QName("https://unilogin.dk/data", "GroupId");
    private final static QName _Extern_QNAME = new QName("https://unilogin.dk/data", "Extern");
    private final static QName _UserId_QNAME = new QName("https://unilogin.dk/data", "UserId");
    private final static QName _Name_QNAME = new QName("https://unilogin.dk/data", "Name");
    private final static QName _InitialPassword_QNAME = new QName("https://unilogin.dk/data", "InitialPassword");
    private final static QName _PasswordState_QNAME = new QName("https://unilogin.dk/data", "PasswordState");
    private final static QName _UniqueName_QNAME = new QName("https://unilogin.dk/data", "UniqueName");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.unilogin_dk.data
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Address }
     * 
     */
    public Address createAddress() {
        return new Address();
    }

    /**
     * Create an instance of {@link Employee }
     * 
     */
    public Employee createEmployee() {
        return new Employee();
    }

    /**
     * Create an instance of {@link Extern }
     * 
     */
    public Extern createExtern() {
        return new Extern();
    }

    /**
     * Create an instance of {@link InstitutionBase }
     * 
     */
    public InstitutionBase createInstitutionBase() {
        return new InstitutionBase();
    }

    /**
     * Create an instance of {@link InstitutionWithGroupsBase }
     * 
     */
    public InstitutionWithGroupsBase createInstitutionWithGroupsBase() {
        return new InstitutionWithGroupsBase();
    }

    /**
     * Create an instance of {@link Group }
     * 
     */
    public Group createGroup() {
        return new Group();
    }

    /**
     * Create an instance of {@link PersonMini }
     * 
     */
    public PersonMini createPersonMini() {
        return new PersonMini();
    }

    /**
     * Create an instance of {@link PersonMedium }
     * 
     */
    public PersonMedium createPersonMedium() {
        return new PersonMedium();
    }

    /**
     * Create an instance of {@link PersonFullBase }
     * 
     */
    public PersonFullBase createPersonFullBase() {
        return new PersonFullBase();
    }

    /**
     * Create an instance of {@link PhoneNumberProtectable }
     * 
     */
    public PhoneNumberProtectable createPhoneNumberProtectable() {
        return new PhoneNumberProtectable();
    }

    /**
     * Create an instance of {@link PhoneNumberWithProtectionInfo }
     * 
     */
    public PhoneNumberWithProtectionInfo createPhoneNumberWithProtectionInfo() {
        return new PhoneNumberWithProtectionInfo();
    }

    /**
     * Create an instance of {@link EmployeeBase }
     * 
     */
    public EmployeeBase createEmployeeBase() {
        return new EmployeeBase();
    }

    /**
     * Create an instance of {@link StudentMini }
     * 
     */
    public StudentMini createStudentMini() {
        return new StudentMini();
    }

    /**
     * Create an instance of {@link ContactPersonBase }
     * 
     */
    public ContactPersonBase createContactPersonBase() {
        return new ContactPersonBase();
    }

    /**
     * Create an instance of {@link UniLoginMini }
     * 
     */
    public UniLoginMini createUniLoginMini() {
        return new UniLoginMini();
    }

    /**
     * Create an instance of {@link UniLoginFull }
     * 
     */
    public UniLoginFull createUniLoginFull() {
        return new UniLoginFull();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "LocalPersonId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLocalPersonId(String value) {
        return new JAXBElement<String>(_LocalPersonId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "FirstName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createFirstName(String value) {
        return new JAXBElement<String>(_FirstName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "FamilyName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createFamilyName(String value) {
        return new JAXBElement<String>(_FamilyName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "CivilRegistrationNumber")
    public JAXBElement<String> createCivilRegistrationNumber(String value) {
        return new JAXBElement<String>(_CivilRegistrationNumber_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "EmailAddress")
    public JAXBElement<String> createEmailAddress(String value) {
        return new JAXBElement<String>(_EmailAddress_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "BirthDate")
    public JAXBElement<XMLGregorianCalendar> createBirthDate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_BirthDate_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Gender }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Gender }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "Gender")
    public JAXBElement<Gender> createGender(Gender value) {
        return new JAXBElement<Gender>(_Gender_QNAME, Gender.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "PhotoId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPhotoId(String value) {
        return new JAXBElement<String>(_PhotoId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Address }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Address }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "Address")
    public JAXBElement<Address> createAddress(Address value) {
        return new JAXBElement<Address>(_Address_QNAME, Address.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "StreetAddress")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createStreetAddress(String value) {
        return new JAXBElement<String>(_StreetAddress_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "PostalCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPostalCode(String value) {
        return new JAXBElement<String>(_PostalCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "PostalDistrict")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createPostalDistrict(String value) {
        return new JAXBElement<String>(_PostalDistrict_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountryCode }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CountryCode }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "CountryCode")
    public JAXBElement<CountryCode> createCountryCode(CountryCode value) {
        return new JAXBElement<CountryCode>(_CountryCode_QNAME, CountryCode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "Country")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createCountry(String value) {
        return new JAXBElement<String>(_Country_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "MunicipalityCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createMunicipalityCode(String value) {
        return new JAXBElement<String>(_MunicipalityCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "MunicipalityName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createMunicipalityName(String value) {
        return new JAXBElement<String>(_MunicipalityName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Employee }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Employee }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "Employee")
    public JAXBElement<Employee> createEmployee(Employee value) {
        return new JAXBElement<Employee>(_Employee_QNAME, Employee.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "ShortName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createShortName(String value) {
        return new JAXBElement<String>(_ShortName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "Occupation")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createOccupation(String value) {
        return new JAXBElement<String>(_Occupation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "Location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLocation(String value) {
        return new JAXBElement<String>(_Location_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "GroupId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createGroupId(String value) {
        return new JAXBElement<String>(_GroupId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Extern }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Extern }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "Extern")
    public JAXBElement<Extern> createExtern(Extern value) {
        return new JAXBElement<Extern>(_Extern_QNAME, Extern.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "UserId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createUserId(String value) {
        return new JAXBElement<String>(_UserId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "Name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "InitialPassword")
    public JAXBElement<String> createInitialPassword(String value) {
        return new JAXBElement<String>(_InitialPassword_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PasswordState }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link PasswordState }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "PasswordState")
    public JAXBElement<PasswordState> createPasswordState(PasswordState value) {
        return new JAXBElement<PasswordState>(_PasswordState_QNAME, PasswordState.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "https://unilogin.dk/data", name = "UniqueName")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createUniqueName(String value) {
        return new JAXBElement<String>(_UniqueName_QNAME, String.class, null, value);
    }

}
