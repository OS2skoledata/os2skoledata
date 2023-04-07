
package https.unilogin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Kontaktpersonsrelation.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Kontaktpersonsrelation"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Mor"/&gt;
 *     &lt;enumeration value="Far"/&gt;
 *     &lt;enumeration value="Andet"/&gt;
 *     &lt;enumeration value="Officielt tilknyttet person"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "Kontaktpersonsrelation")
@XmlEnum
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

    public String value() {
        return value;
    }

    public static Kontaktpersonsrelation fromValue(String v) {
        for (Kontaktpersonsrelation c: Kontaktpersonsrelation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
