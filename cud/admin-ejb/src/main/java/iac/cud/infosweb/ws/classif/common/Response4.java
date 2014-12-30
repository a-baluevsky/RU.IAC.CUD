
package iac.cud.infosweb.ws.classif.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Response4 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Response4">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ActualDoc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ZipDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Response4", propOrder = {
    "actualDoc",
    "zipDate"
})
 public class Response4 {

    @XmlElement(name = "ActualDoc")
    protected int actualDoc;
    @XmlElement(name = "ZipDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar zipDate;

    /**
     * Gets the value of the actualDoc property.
     * 
     */
    public int getActualDoc() {
        return actualDoc;
    }

    /**
     * Sets the value of the actualDoc property.
     * 
     */
    public void setActualDoc(int value) {
        this.actualDoc = value;
    }

    /**
     * Gets the value of the zipDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getZipDate() {
        return zipDate;
    }

    /**
     * Sets the value of the zipDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setZipDate(XMLGregorianCalendar value) {
        this.zipDate = value;
    }

}
