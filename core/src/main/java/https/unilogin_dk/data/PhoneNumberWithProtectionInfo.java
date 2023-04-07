
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Telefonnummer med oplysning om, hvorvidt telefonnummeret er beskyttet.
 *                 Anvendes i import.
 *             
 * 
 * <p>Java class for phoneNumberWithProtectionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="phoneNumberWithProtectionInfo"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;restriction base="&lt;https://unilogin.dk/data&gt;phoneNumberProtectable"&gt;
 *     &lt;/restriction&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "phoneNumberWithProtectionInfo")
public class PhoneNumberWithProtectionInfo
    extends PhoneNumberProtectable
{


}
