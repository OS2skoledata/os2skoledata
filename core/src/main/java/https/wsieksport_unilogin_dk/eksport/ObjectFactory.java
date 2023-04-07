
package https.wsieksport_unilogin_dk.eksport;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.wsieksport_unilogin_dk.eksport package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.wsieksport_unilogin_dk.eksport
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ImportSource }
     * 
     */
    public ImportSource createImportSource() {
        return new ImportSource();
    }

    /**
     * Create an instance of {@link UniLoginExportBase }
     * 
     */
    public UniLoginExportBase createUniLoginExportBase() {
        return new UniLoginExportBase();
    }

    /**
     * Create an instance of {@link InstitutionPersonExportBase }
     * 
     */
    public InstitutionPersonExportBase createInstitutionPersonExportBase() {
        return new InstitutionPersonExportBase();
    }

    /**
     * Create an instance of {@link InstitutionPersonExportFullBase }
     * 
     */
    public InstitutionPersonExportFullBase createInstitutionPersonExportFullBase() {
        return new InstitutionPersonExportFullBase();
    }

    /**
     * Create an instance of {@link ContactPersonExportBase }
     * 
     */
    public ContactPersonExportBase createContactPersonExportBase() {
        return new ContactPersonExportBase();
    }

}
