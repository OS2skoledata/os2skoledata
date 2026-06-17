
package dk.stil.brugerdatabasen.common.v3;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for Ansatrolle&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
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
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "Ansatrolle")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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

    /**
     * Gets the value associated to the enum constant.
     * 
     * @return
     *     The value linked to the enum.
     */
    public String value() {
        return value;
    }

    /**
     * Gets the enum associated to the value passed as parameter.
     * 
     * @param v
     *     The value to get the enum from.
     * @return
     *     The enum which corresponds to the value, if it exists.
     * @throws IllegalArgumentException
     *     If no value matches in the enum declaration.
     */
    public static Ansatrolle fromValue(String v) {
        for (Ansatrolle c: Ansatrolle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
