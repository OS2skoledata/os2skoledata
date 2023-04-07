
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import https.unilogin.Kontaktpersonsrelation;
import https.wsieksport_unilogin_dk.eksport.ContactPersonExportBase;


/**
 * 
 *                 Basistype for contactPerson. Anvendes i student i import og fuld eksport.
 *                 Oplysninger om kontaktperson.
 *                 Tilføjes Person, samt UNILogin i eksport.
 *             
 * 
 * <p>Java class for contactPersonBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contactPersonBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="relation" use="required" type="{https://unilogin.dk}Kontaktpersonsrelation" /&gt;
 *       &lt;attribute name="childCustody" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="accessLevel" type="{https://unilogin.dk}KontaktpersonAdgangsniveau" /&gt;
 *       &lt;attribute name="cvr" type="{https://unilogin.dk}NonEmptyToken" /&gt;
 *       &lt;attribute name="pnr" type="{https://unilogin.dk}NonEmptyToken" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactPersonBase")
@XmlSeeAlso({
    ContactPersonExportBase.class
})
public class ContactPersonBase {

    @XmlAttribute(name = "relation", required = true)
    protected Kontaktpersonsrelation relation;
    @XmlAttribute(name = "childCustody", required = true)
    protected boolean childCustody;
    @XmlAttribute(name = "accessLevel")
    protected Integer accessLevel;
    @XmlAttribute(name = "cvr")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String cvr;
    @XmlAttribute(name = "pnr")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String pnr;

    /**
     * Gets the value of the relation property.
     * 
     * @return
     *     possible object is
     *     {@link Kontaktpersonsrelation }
     *     
     */
    public Kontaktpersonsrelation getRelation() {
        return relation;
    }

    /**
     * Sets the value of the relation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Kontaktpersonsrelation }
     *     
     */
    public void setRelation(Kontaktpersonsrelation value) {
        this.relation = value;
    }

    /**
     * Gets the value of the childCustody property.
     * 
     */
    public boolean isChildCustody() {
        return childCustody;
    }

    /**
     * Sets the value of the childCustody property.
     * 
     */
    public void setChildCustody(boolean value) {
        this.childCustody = value;
    }

    /**
     * Gets the value of the accessLevel property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAccessLevel() {
        return accessLevel;
    }

    /**
     * Sets the value of the accessLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAccessLevel(Integer value) {
        this.accessLevel = value;
    }

    /**
     * Gets the value of the cvr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCvr() {
        return cvr;
    }

    /**
     * Sets the value of the cvr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCvr(String value) {
        this.cvr = value;
    }

    /**
     * Gets the value of the pnr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPnr() {
        return pnr;
    }

    /**
     * Sets the value of the pnr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPnr(String value) {
        this.pnr = value;
    }

}
