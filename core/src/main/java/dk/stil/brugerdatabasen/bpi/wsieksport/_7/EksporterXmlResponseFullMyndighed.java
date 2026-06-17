
package dk.stil.brugerdatabasen.bpi.wsieksport._7;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed.UniLoginExportFullMyndighed;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for EksporterXmlResponseFullMyndighed complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="EksporterXmlResponseFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="xml"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/fullmyndighed}UNILoginExportFullMyndighed"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EksporterXmlResponseFullMyndighed", propOrder = {
    "xml"
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class EksporterXmlResponseFullMyndighed {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected EksporterXmlResponseFullMyndighed.Xml xml;

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     {@link EksporterXmlResponseFullMyndighed.Xml }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public EksporterXmlResponseFullMyndighed.Xml getXml() {
        return xml;
    }

    /**
     * Sets the value of the xml property.
     * 
     * @param value
     *     allowed object is
     *     {@link EksporterXmlResponseFullMyndighed.Xml }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setXml(EksporterXmlResponseFullMyndighed.Xml value) {
        this.xml = value;
    }


    /**
     * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
     * 
     * &lt;pre&gt;{&#064;code
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/fullmyndighed}UNILoginExportFullMyndighed"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * }&lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "uniLoginExportFullMyndighed"
    })
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public static class Xml {

        @XmlElement(name = "UNILoginExportFullMyndighed", namespace = "https://brugerdatabasen.stil.dk/bpi/wsieksport/7/fullmyndighed", required = true)
        @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
        protected UniLoginExportFullMyndighed uniLoginExportFullMyndighed;

        /**
         * Gets the value of the uniLoginExportFullMyndighed property.
         * 
         * @return
         *     possible object is
         *     {@link UniLoginExportFullMyndighed }
         *     
         */
        @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
        public UniLoginExportFullMyndighed getUNILoginExportFullMyndighed() {
            return uniLoginExportFullMyndighed;
        }

        /**
         * Sets the value of the uniLoginExportFullMyndighed property.
         * 
         * @param value
         *     allowed object is
         *     {@link UniLoginExportFullMyndighed }
         *     
         */
        @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
        public void setUNILoginExportFullMyndighed(UniLoginExportFullMyndighed value) {
            this.uniLoginExportFullMyndighed = value;
        }

    }

}
