
package dk.stil.brugerdatabasen.bpi.wsieksport._7.common;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.full.ContactPersonFull;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed.ContactPersonFullMyndighed;
import dk.stil.brugerdatabasen.common.v3.Kontaktpersonsrelation;
import jakarta.annotation.Generated;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Basistype for kontaktperson. Anvendes i student i fuld eksport.
 *                 Oplysninger om kontaktperson.
 * 
 * &lt;p&gt;Java class for contactPersonBase complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="contactPersonBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="relation" use="required" type="{https://brugerdatabasen.stil.dk/bpi/common/3}Kontaktpersonsrelation" /&gt;
 *       &lt;attribute name="childCustody" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="accessLevel" use="required" type="{https://brugerdatabasen.stil.dk/bpi/common/3}KontaktpersonAdgangsniveau" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactPersonBase")
@XmlSeeAlso({
    ContactPersonFullMyndighed.class,
    ContactPersonFull.class
})
@Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
public class ContactPersonBase {

    /**
     * Kontaktpersonens relation til eleven.
     * 
     */
    @XmlAttribute(name = "relation", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected Kontaktpersonsrelation relation;
    /**
     * Har personen forældremyndighed?
     * 
     */
    @XmlAttribute(name = "childCustody", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected boolean childCustody;
    /**
     * Niveau for adgang til personoplysninger om den tilknyttede elev.
     *                     NB! Hvis kontaktpersonen ikke er forældremyndighedsindehaver, skal hjemmel opnås
     *                     på anden vis.
     * 
     */
    @XmlAttribute(name = "accessLevel", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    protected int accessLevel;

    /**
     * Kontaktpersonens relation til eleven.
     * 
     * @return
     *     possible object is
     *     {@link Kontaktpersonsrelation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
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
     * @see #getRelation()
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setRelation(Kontaktpersonsrelation value) {
        this.relation = value;
    }

    /**
     * Har personen forældremyndighed?
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public boolean isChildCustody() {
        return childCustody;
    }

    /**
     * Sets the value of the childCustody property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setChildCustody(boolean value) {
        this.childCustody = value;
    }

    /**
     * Niveau for adgang til personoplysninger om den tilknyttede elev.
     *                     NB! Hvis kontaktpersonen ikke er forældremyndighedsindehaver, skal hjemmel opnås
     *                     på anden vis.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public int getAccessLevel() {
        return accessLevel;
    }

    /**
     * Sets the value of the accessLevel property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", comments = "JAXB RI v4.0.6", date = "2026-04-10T13:55:56+02:00")
    public void setAccessLevel(int value) {
        this.accessLevel = value;
    }

}
