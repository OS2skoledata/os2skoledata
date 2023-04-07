
package https.unilogin_dk.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 *                 Telefonnummer med oplysning om, hvorvidt telefonnummeret er beskyttet.
 *                 Hvis telefonnummeret er beskyttet, er kun attributten sat, mens elementet
 *                 er tomt.
 *                 Anvendes i fuld eksport. Se til phoneNumberWithProtectionInfo for import.
 *             
 * 
 * <p>Java class for phoneNumberProtectable complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="phoneNumberProtectable"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;https://unilogin.dk/data&gt;phoneNumberOrEmpty"&gt;
 *       &lt;attribute name="protected" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "phoneNumberProtectable", propOrder = {
    "value"
})
@XmlSeeAlso({
    PhoneNumberWithProtectionInfo.class
})
public class PhoneNumberProtectable {

    @XmlValue
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String value;
    @XmlAttribute(name = "protected", required = true)
    protected boolean _protected;

    /**
     * 
     *                 Telefonnummer eller tom.
     *                 Anvendes i phoneNumberComplexHideable og phoneNumberComplex, hvor sidstnævnte ikke
     *                 tillader en tom værdi (se til disse typer).
     * 
     *                 Formatet kræver blot, at feltet et sted ligner et telefonnummer, hvilket forstås som
     *                 mindst seks cifre (af hensyn til grønlandske og færøske telefonnumre indtastet uden
     *                 landekode). De seks cifre må være blandet med mellemrum, bindestreger og parenteser.
     *                 Feltet må indeholde alt undtagen tal før de seks cifre. Efter de seks cifre må
     *                 feltet indeholde alt.
     * 
     *                 Før eventuel automatisk anvendelse fra eksport bør man derfor gøre følgende (med
     *                 forslag til Perl reg.exp.):
     *                 1.  Reducer til tegn, der matcher tegn fra et telefontastatur.
     *                     s/[^0-9+#*]//g
     *                 2.  Fjern indledende # og *.
     *                     s/^[#*]+//
     *                 3.  Erstat eventuel indledende "+" med "00".
     *                     s/^\+/00/
     *                 4.  Fjern "+" (der kan være flere end landekodepræfikset, hvis det f.eks. er brugt
     *                     til at markere omstillingskode)
     *                     s/\+//g
     *                 5.  Reducer eventuelt til de første X cifre, afhængigt af postnummer og landekode
     *                     (postnummer af hensyn til grønlandske og færøske numre). Det fulde nummer vil i
     *                     visse tilfælde indeholde omstillingskode eller endnu et telefonnummer.
     *                     Hvis postnummeret er fra Grønland eller Færøerne:
     *                     s/^(?:0029[89])?([0-9]{6}).*<!---->/\1/
     *                     Ellers, tag de første seks cifre fra grønlandske og færøske numre, alle cifre
     *                     fra udenlandske numre eller otte cifre fra danske numre:
     *                     s/^(?:0029[89]([0-9]{6})|(00(?!45).*)|(?:0045)?([0-9]{8})).*<!---->/\1\2\3/
     *             
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the protected property.
     * 
     */
    public boolean isProtected() {
        return _protected;
    }

    /**
     * Sets the value of the protected property.
     * 
     */
    public void setProtected(boolean value) {
        this._protected = value;
    }

}
