
package dk.stil.brugerdatabasen.common.v3;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for importGroupType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
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
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "importGroupType")
@XmlEnum
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
    public static ImportGroupType fromValue(String v) {
        for (ImportGroupType c: ImportGroupType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
