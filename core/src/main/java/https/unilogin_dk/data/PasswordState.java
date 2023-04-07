
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for passwordState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="passwordState"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="valid"/&gt;
 *     &lt;enumeration value="changed"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "passwordState")
@XmlEnum
public enum PasswordState {

    @XmlEnumValue("valid")
    VALID("valid"),
    @XmlEnumValue("changed")
    CHANGED("changed");
    private final String value;

    PasswordState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PasswordState fromValue(String v) {
        for (PasswordState c: PasswordState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
