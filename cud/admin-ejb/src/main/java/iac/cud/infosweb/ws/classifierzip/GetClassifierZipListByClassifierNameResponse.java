
package iac.cud.infosweb.ws.classifierzip;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getClassifierZipListByClassifierNameResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getClassifierZipListByClassifierNameResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://ws.iac.spb.ru/ClassifierZip}responseElement54" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getClassifierZipListByClassifierNameResponseCUD", propOrder = {
    "_return"
})
 public class GetClassifierZipListByClassifierNameResponse {

    @XmlElement(name = "return")
    protected ResponseElement54 _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseElement54 }
     *     
     */
    public ResponseElement54 getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseElement54 }
     *     
     */
    public void setReturn(ResponseElement54 value) {
        this._return = value;
    }

}
