
package https.unilogin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ProduktInstitution complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProduktInstitution"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="produkt" type="{https://unilogin.dk}ProduktId"/&gt;
 *         &lt;element name="instnr" type="{https://unilogin.dk}Regnr" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProduktInstitution", propOrder = {
    "produkt",
    "instnr"
})
public class ProduktInstitution {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected ProduktId produkt;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected List<String> instnr;

    /**
     * Gets the value of the produkt property.
     * 
     * @return
     *     possible object is
     *     {@link ProduktId }
     *     
     */
    public ProduktId getProdukt() {
        return produkt;
    }

    /**
     * Sets the value of the produkt property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProduktId }
     *     
     */
    public void setProdukt(ProduktId value) {
        this.produkt = value;
    }

    /**
     * Gets the value of the instnr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the instnr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstnr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getInstnr() {
        if (instnr == null) {
            instnr = new ArrayList<String>();
        }
        return this.instnr;
    }

}
