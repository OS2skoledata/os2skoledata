
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.full.UniLoginExportFullBase;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed.UniLoginExportFullMyndighedBase;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.medium.UniLoginExportMediumBase;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.small.UniLoginExportSmallBase;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for uniLoginExportBase complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="uniLoginExportBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}ImportSource" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="exportDateTime" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="accessLevel" use="required" type="{http://www.w3.org/2001/XMLSchema}token" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uniLoginExportBase", propOrder = {
    "importSource"
})
@XmlSeeAlso({
    UniLoginExportFullMyndighedBase.class,
    UniLoginExportFullBase.class,
    UniLoginExportMediumBase.class,
    UniLoginExportSmallBase.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class UniLoginExportBase {

    /**
     * Importkilder, der refereres til fra eksportens InstitutionPerson-objekter.
     * 
     */
    @XmlElement(name = "ImportSource")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected List<ImportSource> importSource;
    /**
     * Tidspunkt for eksport af data fra UNI•Login.
     * 
     */
    @XmlAttribute(name = "exportDateTime", required = true)
    @XmlSchemaType(name = "dateTime")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected XMLGregorianCalendar exportDateTime;
    /**
     * Eksportpakke. Fikseres i de nedarvede skemaer.
     * 
     */
    @XmlAttribute(name = "accessLevel", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String accessLevel;

    /**
     * Importkilder, der refereres til fra eksportens InstitutionPerson-objekter.
     * 
     * Gets the value of the importSource property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the importSource property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getImportSource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImportSource }
     * </p>
     * 
     * 
     * @return
     *     The value of the importSource property.
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public List<ImportSource> getImportSource() {
        if (importSource == null) {
            importSource = new ArrayList<>();
        }
        return this.importSource;
    }

    /**
     * Tidspunkt for eksport af data fra UNI•Login.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getExportDateTime()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setExportDateTime(XMLGregorianCalendar value) {
        this.exportDateTime = value;
    }

    /**
     * Eksportpakke. Fikseres i de nedarvede skemaer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getAccessLevel()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setAccessLevel(String value) {
        this.accessLevel = value;
    }

}
