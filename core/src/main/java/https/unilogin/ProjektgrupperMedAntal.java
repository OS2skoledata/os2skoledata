
package https.unilogin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProjektgrupperMedAntal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProjektgrupperMedAntal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="projektgruppeMedAntal" type="{https://unilogin.dk}ProjektgruppeMedAntal" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjektgrupperMedAntal", propOrder = {
    "projektgruppeMedAntal"
})
public class ProjektgrupperMedAntal {

    protected List<ProjektgruppeMedAntal> projektgruppeMedAntal;

    /**
     * Gets the value of the projektgruppeMedAntal property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the projektgruppeMedAntal property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProjektgruppeMedAntal().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProjektgruppeMedAntal }
     * 
     * 
     */
    public List<ProjektgruppeMedAntal> getProjektgruppeMedAntal() {
        if (projektgruppeMedAntal == null) {
            projektgruppeMedAntal = new ArrayList<ProjektgruppeMedAntal>();
        }
        return this.projektgruppeMedAntal;
    }

}
