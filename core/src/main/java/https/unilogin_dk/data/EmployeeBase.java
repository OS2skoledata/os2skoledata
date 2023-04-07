
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Basistype for læreroplysninger.
 *                 Der anvendes nedarvning for at undgå at udskille Type som element
 *                 for at implementere overgangsskemaet.
 *             
 * 
 * <p>Java class for employeeBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="employeeBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "employeeBase")
@XmlSeeAlso({
    https.unilogin_dk.data.transitional.Employee.class,
    https.unilogin_dk.data.Employee.class
})
public class EmployeeBase {


}
