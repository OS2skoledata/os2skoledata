
package https.wsieksport_unilogin_dk.eksport;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import https.wsieksport_unilogin_dk.eksport.full.UniLoginExportFullBase;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.UniLoginExportFullMyndighedBase;
import https.wsieksport_unilogin_dk.eksport.medium.UniLoginExportMediumBase;
import https.wsieksport_unilogin_dk.eksport.small.UniLoginExportSmallBase;


/**
 * <p>Java class for uniLoginExportBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uniLoginExportBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://wsieksport.unilogin.dk/eksport}ImportSource" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="exportDateTime" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="accessLevel" use="required" type="{http://www.w3.org/2001/XMLSchema}token" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportBase", propOrder = {
    "importSource"
})
@XmlSeeAlso({
    UniLoginExportMediumBase.class,
    UniLoginExportFullMyndighedBase.class,
    UniLoginExportSmallBase.class,
    UniLoginExportFullBase.class
})
public class UniLoginExportBase {

    @XmlElement(name = "ImportSource")
    protected List<ImportSource> importSource;
    @XmlAttribute(name = "exportDateTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar exportDateTime;
    @XmlAttribute(name = "accessLevel", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String accessLevel;

    /**
     * Gets the value of the importSource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the importSource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImportSource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImportSource }
     * 
     * 
     */
    public List<ImportSource> getImportSource() {
        if (importSource == null) {
            importSource = new ArrayList<ImportSource>();
        }
        return this.importSource;
    }

    /**
     * Gets the value of the exportDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExportDateTime() {
        return exportDateTime;
    }

    /**
     * Sets the value of the exportDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExportDateTime(XMLGregorianCalendar value) {
        this.exportDateTime = value;
    }

    /**
     * Gets the value of the accessLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessLevel() {
        return accessLevel;
    }

    /**
     * Sets the value of the accessLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessLevel(String value) {
        this.accessLevel = value;
    }

}
