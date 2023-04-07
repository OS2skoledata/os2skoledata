
package https.unilogin;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProduktId.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ProduktId"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Givadgang"/&gt;
 *     &lt;enumeration value="Karakterdatabasen"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ProduktId")
@XmlEnum
public enum ProduktId {

    @XmlEnumValue("Givadgang")
    GIVADGANG("Givadgang"),
    @XmlEnumValue("Karakterdatabasen")
    KARAKTERDATABASEN("Karakterdatabasen");
    private final String value;

    ProduktId(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProduktId fromValue(String v) {
        for (ProduktId c: ProduktId.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
