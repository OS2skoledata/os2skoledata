
package https.unilogin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Ansatbrugertype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Ansatbrugertype"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="lærer"/&gt;
 *     &lt;enumeration value="tap"/&gt;
 *     &lt;enumeration value="pæd"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Ansatbrugertype")
@XmlEnum
public enum Ansatbrugertype {

    @XmlEnumValue("l\u00e6rer")
    LÆRER("l\u00e6rer"),
    @XmlEnumValue("tap")
    TAP("tap"),
    @XmlEnumValue("p\u00e6d")
    PÆD("p\u00e6d");
    private final String value;

    Ansatbrugertype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Ansatbrugertype fromValue(String v) {
        for (Ansatbrugertype c: Ansatbrugertype.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
