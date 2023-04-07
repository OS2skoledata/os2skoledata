
package https.unilogin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Elevbrugertype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Elevbrugertype"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="elev"/&gt;
 *     &lt;enumeration value="stud"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Elevbrugertype")
@XmlEnum
public enum Elevbrugertype {

    @XmlEnumValue("elev")
    ELEV("elev"),
    @XmlEnumValue("stud")
    STUD("stud");
    private final String value;

    Elevbrugertype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Elevbrugertype fromValue(String v) {
        for (Elevbrugertype c: Elevbrugertype.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
