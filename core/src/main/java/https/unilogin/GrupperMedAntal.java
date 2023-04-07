
package https.unilogin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GrupperMedAntal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GrupperMedAntal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gruppeMedAntal" type="{https://unilogin.dk}GruppeMedAntal" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GrupperMedAntal", propOrder = {
    "gruppeMedAntal"
})
public class GrupperMedAntal {

    protected List<GruppeMedAntal> gruppeMedAntal;

    /**
     * Gets the value of the gruppeMedAntal property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gruppeMedAntal property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGruppeMedAntal().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GruppeMedAntal }
     * 
     * 
     */
    public List<GruppeMedAntal> getGruppeMedAntal() {
        if (gruppeMedAntal == null) {
            gruppeMedAntal = new ArrayList<GruppeMedAntal>();
        }
        return this.gruppeMedAntal;
    }

}
