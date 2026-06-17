
package dk.stil.brugerdatabasen.bpi.wsieksport._7;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AktoerTypeFilter complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AktoerTypeFilter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="inkluderElever" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="inkluderMedarbejdere" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="inkluderEksterne" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AktoerTypeFilter", propOrder = {
    "inkluderElever",
    "inkluderMedarbejdere",
    "inkluderEksterne"
})
@XmlSeeAlso({
    AktoerTypeFilterFuld.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class AktoerTypeFilter {

    @XmlElement(defaultValue = "true")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected boolean inkluderElever;
    @XmlElement(defaultValue = "true")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected boolean inkluderMedarbejdere;
    @XmlElement(defaultValue = "true")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected boolean inkluderEksterne;

    /**
     * Gets the value of the inkluderElever property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public boolean isInkluderElever() {
        return inkluderElever;
    }

    /**
     * Sets the value of the inkluderElever property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setInkluderElever(boolean value) {
        this.inkluderElever = value;
    }

    /**
     * Gets the value of the inkluderMedarbejdere property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public boolean isInkluderMedarbejdere() {
        return inkluderMedarbejdere;
    }

    /**
     * Sets the value of the inkluderMedarbejdere property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setInkluderMedarbejdere(boolean value) {
        this.inkluderMedarbejdere = value;
    }

    /**
     * Gets the value of the inkluderEksterne property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public boolean isInkluderEksterne() {
        return inkluderEksterne;
    }

    /**
     * Sets the value of the inkluderEksterne property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setInkluderEksterne(boolean value) {
        this.inkluderEksterne = value;
    }

}
