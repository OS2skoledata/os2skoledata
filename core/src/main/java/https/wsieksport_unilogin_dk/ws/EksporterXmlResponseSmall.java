
package https.wsieksport_unilogin_dk.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.wsieksport_unilogin_dk.eksport.small.UniLoginExportSmall;


/**
 * <p>Java class for EksporterXmlResponseSmall complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EksporterXmlResponseSmall"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="xml"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element ref="{https://wsieksport.unilogin.dk/eksport/small}UNILoginExportSmall"/&gt;
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
@XmlType(name = "EksporterXmlResponseSmall", propOrder = {
    "xml"
})
public class EksporterXmlResponseSmall {

    @XmlElement(required = true)
    protected EksporterXmlResponseSmall.Xml xml;

    /**
     * Gets the value of the xml property.
     * 
     * @return
     *     possible object is
     *     {@link EksporterXmlResponseSmall.Xml }
     *     
     */
    public EksporterXmlResponseSmall.Xml getXml() {
        return xml;
    }

    /**
     * Sets the value of the xml property.
     * 
     * @param value
     *     allowed object is
     *     {@link EksporterXmlResponseSmall.Xml }
     *     
     */
    public void setXml(EksporterXmlResponseSmall.Xml value) {
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
     *         &lt;element ref="{https://wsieksport.unilogin.dk/eksport/small}UNILoginExportSmall"/&gt;
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
        "uniLoginExportSmall"
    })
    public static class Xml {

        @XmlElement(name = "UNILoginExportSmall", namespace = "https://wsieksport.unilogin.dk/eksport/small", required = true)
        protected UniLoginExportSmall uniLoginExportSmall;

        /**
         * Gets the value of the uniLoginExportSmall property.
         * 
         * @return
         *     possible object is
         *     {@link UniLoginExportSmall }
         *     
         */
        public UniLoginExportSmall getUNILoginExportSmall() {
            return uniLoginExportSmall;
        }

        /**
         * Sets the value of the uniLoginExportSmall property.
         * 
         * @param value
         *     allowed object is
         *     {@link UniLoginExportSmall }
         *     
         */
        public void setUNILoginExportSmall(UniLoginExportSmall value) {
            this.uniLoginExportSmall = value;
        }

    }

}
