
package dk.stil.brugerdatabasen.bpi.wsieksport._7.medium;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.UniLoginExportBase;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for uniLoginExportMediumBase complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="uniLoginExportMediumBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}uniLoginExportBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}ImportSource" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="accessLevel" use="required" type="{http://www.w3.org/2001/XMLSchema}token" fixed="medium" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportMediumBase")
@XmlSeeAlso({
    UniLoginExportMedium.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class UniLoginExportMediumBase
    extends UniLoginExportBase
{


}
