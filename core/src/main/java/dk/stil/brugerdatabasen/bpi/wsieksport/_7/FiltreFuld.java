
package dk.stil.brugerdatabasen.bpi.wsieksport._7;

import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for FiltreFuld complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="FiltreFuld"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="aktoertypeFilter" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7}AktoerTypeFilterFuld" minOccurs="0"/&gt;
 *         &lt;element name="klassetrinFilter" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7}KlassetrinFilter" minOccurs="0"/&gt;
 *         &lt;element name="cprFilter" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7}CprFilter" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FiltreFuld", propOrder = {
    "aktoertypeFilter",
    "klassetrinFilter",
    "cprFilter"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class FiltreFuld {

    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected AktoerTypeFilterFuld aktoertypeFilter;
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected KlassetrinFilter klassetrinFilter;
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected CprFilter cprFilter;

    /**
     * Gets the value of the aktoertypeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link AktoerTypeFilterFuld }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public AktoerTypeFilterFuld getAktoertypeFilter() {
        return aktoertypeFilter;
    }

    /**
     * Sets the value of the aktoertypeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link AktoerTypeFilterFuld }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setAktoertypeFilter(AktoerTypeFilterFuld value) {
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

    /**
     * Gets the value of the cprFilter property.
     * 
     * @return
     *     possible object is
     *     {@link CprFilter }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public CprFilter getCprFilter() {
        return cprFilter;
    }

    /**
     * Sets the value of the cprFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link CprFilter }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setCprFilter(CprFilter value) {
        this.cprFilter = value;
    }

}
