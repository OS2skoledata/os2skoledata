
package https.wsieksport_unilogin_dk.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.UniLoginExportFullMyndighed;


/**
 * <p>Java class for EksporterXmlResponseFullMyndighed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EksporterXmlResponseFullMyndighed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="xml"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element ref="{https://wsieksport.unilogin.dk/eksport/fullmyndighed}UNILoginExportFullMyndighed"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EksporterXmlResponseFullMyndighed", propOrder = {
    "xml"
})
public class EksporterXmlResponseFullMyndighed {

    @XmlElement(required = true)
    protected EksporterXmlResponseFullMyndighed.Xml xml;

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     {@link EksporterXmlResponseFullMyndighed.Xml }
     *     
     */
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
    public void setXml(EksporterXmlResponseFullMyndighed.Xml value) {
        this.xml = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element ref="{https://wsieksport.unilogin.dk/eksport/fullmyndighed}UNILoginExportFullMyndighed"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "uniLoginExportFullMyndighed"
    })
    public static class Xml {

        @XmlElement(name = "UNILoginExportFullMyndighed", namespace = "https://wsieksport.unilogin.dk/eksport/fullmyndighed", required = true)
        protected UniLoginExportFullMyndighed uniLoginExportFullMyndighed;

        /**
         * Gets the value of the uniLoginExportFullMyndighed property.
         * 
         * @return
         *     possible object is
         *     {@link UniLoginExportFullMyndighed }
         *     
         */
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
        public void setUNILoginExportFullMyndighed(UniLoginExportFullMyndighed value) {
            this.uniLoginExportFullMyndighed = value;
        }

    }

}
