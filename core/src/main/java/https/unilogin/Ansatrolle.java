
package https.unilogin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Ansatrolle.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Ansatrolle"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Lærer"/&gt;
 *     &lt;enumeration value="Pædagog"/&gt;
 *     &lt;enumeration value="Vikar"/&gt;
 *     &lt;enumeration value="Leder"/&gt;
 *     &lt;enumeration value="Ledelse"/&gt;
 *     &lt;enumeration value="TAP"/&gt;
 *     &lt;enumeration value="Konsulent"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Ansatrolle")
@XmlEnum
public enum Ansatrolle {

    @XmlEnumValue("L\u00e6rer")
    LÆRER("L\u00e6rer"),
    @XmlEnumValue("P\u00e6dagog")
    PÆDAGOG("P\u00e6dagog"),
    @XmlEnumValue("Vikar")
    VIKAR("Vikar"),
    @XmlEnumValue("Leder")
    LEDER("Leder"),
    @XmlEnumValue("Ledelse")
    LEDELSE("Ledelse"),
    TAP("TAP"),
    @XmlEnumValue("Konsulent")
    KONSULENT("Konsulent");
    private final String value;

    Ansatrolle(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Ansatrolle fromValue(String v) {
        for (Ansatrolle c: Ansatrolle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
