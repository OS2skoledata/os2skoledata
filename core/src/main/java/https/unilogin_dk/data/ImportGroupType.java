
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for importGroupType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="importGroupType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Hovedgruppe"/&gt;
 *     &lt;enumeration value="Årgang"/&gt;
 *     &lt;enumeration value="Retning"/&gt;
 *     &lt;enumeration value="Hold"/&gt;
 *     &lt;enumeration value="SFO"/&gt;
 *     &lt;enumeration value="Team"/&gt;
 *     &lt;enumeration value="Andet"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "importGroupType")
@XmlEnum
public enum ImportGroupType {

    @XmlEnumValue("Hovedgruppe")
    HOVEDGRUPPE("Hovedgruppe"),
    @XmlEnumValue("\u00c5rgang")
    ÅRGANG("\u00c5rgang"),
    @XmlEnumValue("Retning")
    RETNING("Retning"),
    @XmlEnumValue("Hold")
    HOLD("Hold"),
    SFO("SFO"),
    @XmlEnumValue("Team")
    TEAM("Team"),
    @XmlEnumValue("Andet")
    ANDET("Andet");
    private final String value;

    ImportGroupType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ImportGroupType fromValue(String v) {
        for (ImportGroupType c: ImportGroupType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
