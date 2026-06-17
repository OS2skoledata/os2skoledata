
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.small.InstitutionPersonSmall;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Basistype for institutionPerson i eksport.
 * 
 * &lt;p&gt;Java class for institutionPersonExportBase complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="institutionPersonExportBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="source" use="required" type="{https://brugerdatabasen.stil.dk/bpi/wsieksport/7/common}source" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "institutionPersonExportBase")
@XmlSeeAlso({
    InstitutionPersonExportFullBase.class,
    InstitutionPersonSmall.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class InstitutionPersonExportBase {

    /**
     * Angiver importkilden for den pågældende person. Refererer til en ImportSource via dennes
     *                     source-attribut. Er "HUGO Brugeradministrationen" for manuelt oprettede brugere.
     * 
     */
    @XmlAttribute(name = "source", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected String source;

    /**
     * Angiver importkilden for den pågældende person. Refererer til en ImportSource via dennes
     *                     source-attribut. Er "HUGO Brugeradministrationen" for manuelt oprettede brugere.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getSource()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setSource(String value) {
        this.source = value;
    }

}
