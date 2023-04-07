
package https.wsieksport_unilogin_dk.eksport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.unilogin_dk.data.ContactPersonBase;
import https.wsieksport_unilogin_dk.eksport.full.ContactPersonFull;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.ContactPersonFullMyndighed;


/**
 * <p>Java class for contactPersonExportBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactPersonExportBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{https://unilogin.dk/data}contactPersonBase"&gt;
 *       &lt;attribute name="accessLevel" use="required" type="{https://unilogin.dk}KontaktpersonAdgangsniveau" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactPersonExportBase")
@XmlSeeAlso({
    ContactPersonFullMyndighed.class,
    ContactPersonFull.class
})
public class ContactPersonExportBase
    extends ContactPersonBase
{


}
