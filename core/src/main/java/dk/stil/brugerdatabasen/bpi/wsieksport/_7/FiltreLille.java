
package dk.stil.brugerdatabasen.bpi.wsieksport._7;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for FiltreLille complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="FiltreLille"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="aktoertypeFilter" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7}AktoerTypeFilter" minOccurs="0"/&gt;
 *         &lt;element name="klassetrinFilter" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7}KlassetrinFilter" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FiltreLille", propOrder = {
    "aktoertypeFilter",
    "klassetrinFilter"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class FiltreLille {

    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected AktoerTypeFilter aktoertypeFilter;
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected KlassetrinFilter klassetrinFilter;

    /**
     * Gets the value of the aktoertypeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link AktoerTypeFilter }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public AktoerTypeFilter getAktoertypeFilter() {
        return aktoertypeFilter;
    }

    /**
     * Sets the value of the aktoertypeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link AktoerTypeFilter }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setAktoertypeFilter(AktoerTypeFilter value) {
        this.aktoertypeFilter = value;
    }

    /**
     * Gets the value of the klassetrinFilter property.
     * 
     * @return
     *     possible object is
     *     {@link KlassetrinFilter }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public KlassetrinFilter getKlassetrinFilter() {
        return klassetrinFilter;
    }

    /**
     * Sets the value of the klassetrinFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link KlassetrinFilter }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setKlassetrinFilter(KlassetrinFilter value) {
        this.klassetrinFilter = value;
    }

}
