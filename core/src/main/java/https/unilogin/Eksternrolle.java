
package https.unilogin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Eksternrolle.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Eksternrolle"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Praktikant"/&gt;
 *     &lt;enumeration value="Ekstern"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Eksternrolle")
@XmlEnum
public enum Eksternrolle {

    @XmlEnumValue("Praktikant")
    PRAKTIKANT("Praktikant"),
    @XmlEnumValue("Ekstern")
    EKSTERN("Ekstern");
    private final String value;

    Eksternrolle(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Eksternrolle fromValue(String v) {
        for (Eksternrolle c: Eksternrolle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
