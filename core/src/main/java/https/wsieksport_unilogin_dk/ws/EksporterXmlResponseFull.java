
package https.wsieksport_unilogin_dk.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.wsieksport_unilogin_dk.eksport.full.UniLoginExportFull;


/**
 * <p>Java class for EksporterXmlResponseFull complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EksporterXmlResponseFull"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="xml"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element ref="{https://wsieksport.unilogin.dk/eksport/full}UNILoginExportFull"/&gt;
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
@XmlType(name = "EksporterXmlResponseFull", propOrder = {
    "xml"
})
public class EksporterXmlResponseFull {

    @XmlElement(required = true)
    protected EksporterXmlResponseFull.Xml xml;

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     {@link EksporterXmlResponseFull.Xml }
     *     
     */
    public EksporterXmlResponseFull.Xml getXml() {
        return xml;
    }

    /**
     * Sets the value of the xml property.
     * 
     * @param value
     *     allowed object is
     *     {@link EksporterXmlResponseFull.Xml }
     *     
     */
    public void setXml(EksporterXmlResponseFull.Xml value) {
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
     *         &lt;element ref="{https://wsieksport.unilogin.dk/eksport/full}UNILoginExportFull"/&gt;
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
        "uniLoginExportFull"
    })
    public static class Xml {

        @XmlElement(name = "UNILoginExportFull", namespace = "https://wsieksport.unilogin.dk/eksport/full", required = true)
        protected UniLoginExportFull uniLoginExportFull;

        /**
         * Gets the value of the uniLoginExportFull property.
         * 
         * @return
         *     possible object is
         *     {@link UniLoginExportFull }
         *     
         */
        public UniLoginExportFull getUNILoginExportFull() {
            return uniLoginExportFull;
        }

        /**
         * Sets the value of the uniLoginExportFull property.
         * 
         * @param value
         *     allowed object is
         *     {@link UniLoginExportFull }
         *     
         */
        public void setUNILoginExportFull(UniLoginExportFull value) {
            this.uniLoginExportFull = value;
        }

    }

}
