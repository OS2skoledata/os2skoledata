
package https.wsieksport_unilogin_dk.eksport.small;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.wsieksport_unilogin_dk.eksport.UniLoginExportBase;


/**
 * <p>Java class for uniLoginExportSmallBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uniLoginExportSmallBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{https://wsieksport.unilogin.dk/eksport}uniLoginExportBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://wsieksport.unilogin.dk/eksport}ImportSource" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="accessLevel" use="required" type="{http://www.w3.org/2001/XMLSchema}token" fixed="small" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportSmallBase")
@XmlSeeAlso({
    UniLoginExportSmall.class
})
public class UniLoginExportSmallBase
    extends UniLoginExportBase
{


}
