
package dk.stil.brugerdatabasen.bpi.wsieksport._7;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AktoerTypeFilterFuld complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AktoerTypeFilterFuld"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7}AktoerTypeFilter"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="inkluderKontaktpersoner" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AktoerTypeFilterFuld", propOrder = {
    "inkluderKontaktpersoner"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class AktoerTypeFilterFuld
    extends AktoerTypeFilter
{

    @XmlElement(defaultValue = "true")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected boolean inkluderKontaktpersoner;

    /**
     * Gets the value of the inkluderKontaktpersoner property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public boolean isInkluderKontaktpersoner() {
        return inkluderKontaktpersoner;
    }

    /**
     * Sets the value of the inkluderKontaktpersoner property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setInkluderKontaktpersoner(boolean value) {
        this.inkluderKontaktpersoner = value;
    }

}
