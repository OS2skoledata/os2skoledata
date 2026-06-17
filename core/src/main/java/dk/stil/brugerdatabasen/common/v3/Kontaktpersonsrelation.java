
package dk.stil.brugerdatabasen.common.v3;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for Kontaktpersonsrelation&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="Kontaktpersonsrelation"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Mor"/&gt;
 *     &lt;enumeration value="Far"/&gt;
 *     &lt;enumeration value="Andet"/&gt;
 *     &lt;enumeration value="Officielt tilknyttet person"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "Kontaktpersonsrelation")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public enum Kontaktpersonsrelation {

    @XmlEnumValue("Mor")
    MOR("Mor"),
    @XmlEnumValue("Far")
    FAR("Far"),
    @XmlEnumValue("Andet")
    ANDET("Andet"),
    @XmlEnumValue("Officielt tilknyttet person")
    OFFICIELT_TILKNYTTET_PERSON("Officielt tilknyttet person");
    private final String value;

    Kontaktpersonsrelation(String v) {
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
    public static Kontaktpersonsrelation fromValue(String v) {
        for (Kontaktpersonsrelation c: Kontaktpersonsrelation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
