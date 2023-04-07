
package https.wsieksport_unilogin_dk.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.wsieksport_unilogin_dk.eksport.medium.UniLoginExportMedium;


/**
 * <p>Java class for EksporterXmlResponseMedium complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EksporterXmlResponseMedium"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="xml"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element ref="{https://wsieksport.unilogin.dk/eksport/medium}UNILoginExportMedium"/&gt;
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
@XmlType(name = "EksporterXmlResponseMedium", propOrder = {
    "xml"
})
public class EksporterXmlResponseMedium {

    @XmlElement(required = true)
    protected EksporterXmlResponseMedium.Xml xml;

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     {@link EksporterXmlResponseMedium.Xml }
     *     
     */
    public EksporterXmlResponseMedium.Xml getXml() {
        return xml;
    }

    /**
     * Sets the value of the xml property.
     * 
     * @param value
     *     allowed object is
     *     {@link EksporterXmlResponseMedium.Xml }
     *     
     */
    public void setXml(EksporterXmlResponseMedium.Xml value) {
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
     *         &lt;element ref="{https://wsieksport.unilogin.dk/eksport/medium}UNILoginExportMedium"/&gt;
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
        "uniLoginExportMedium"
    })
    public static class Xml {

        @XmlElement(name = "UNILoginExportMedium", namespace = "https://wsieksport.unilogin.dk/eksport/medium", required = true)
        protected UniLoginExportMedium uniLoginExportMedium;

        /**
         * Gets the value of the uniLoginExportMedium property.
         * 
         * @return
         *     possible object is
         *     {@link UniLoginExportMedium }
         *     
         */
        public UniLoginExportMedium getUNILoginExportMedium() {
            return uniLoginExportMedium;
        }

        /**
         * Sets the value of the uniLoginExportMedium property.
         * 
         * @param value
         *     allowed object is
         *     {@link UniLoginExportMedium }
         *     
         */
        public void setUNILoginExportMedium(UniLoginExportMedium value) {
            this.uniLoginExportMedium = value;
        }

    }

}
