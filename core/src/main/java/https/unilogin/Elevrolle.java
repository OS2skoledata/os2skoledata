
package https.unilogin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Elevrolle.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Elevrolle"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Barn"/&gt;
 *     &lt;enumeration value="Elev"/&gt;
 *     &lt;enumeration value="Studerende"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Elevrolle")
@XmlEnum
public enum Elevrolle {

    @XmlEnumValue("Barn")
    BARN("Barn"),
    @XmlEnumValue("Elev")
    ELEV("Elev"),
    @XmlEnumValue("Studerende")
    STUDERENDE("Studerende");
    private final String value;

    Elevrolle(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Elevrolle fromValue(String v) {
        for (Elevrolle c: Elevrolle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
