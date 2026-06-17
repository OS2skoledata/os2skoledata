
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Type af CPR-nummer.
 * 
 * &lt;p&gt;Java class for cprType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="cprType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CPR_VERIFIED"/&gt;
 *     &lt;enumeration value="CPR_UNVERIFIED"/&gt;
 *     &lt;enumeration value="CPR_REPLACEMENT_ID"/&gt;
 *     &lt;enumeration value="CPR_UNKNOWN_TYPE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "cprType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public enum CprType {

    CPR_VERIFIED,
    CPR_UNVERIFIED,
    CPR_REPLACEMENT_ID,
    CPR_UNKNOWN_TYPE;

    public String value() {
        return name();
    }

    public static CprType fromValue(String v) {
        return valueOf(v);
    }

}
