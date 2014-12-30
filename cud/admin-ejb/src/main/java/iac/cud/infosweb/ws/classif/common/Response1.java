
package iac.cud.infosweb.ws.classif.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Response1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Response1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RegNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Fullname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CurrentVersionNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CurDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Response1", propOrder = {
    "regNumber",
    "fullname",
    "currentVersionNumber",
    "curDate"
})
 public class Response1 {

    @XmlElement(name = "RegNumber")
    protected int regNumber;
    @XmlElement(name = "Fullname", required = true)
    protected String fullname;
    @XmlElement(name = "CurrentVersionNumber")
    protected int currentVersionNumber;
    @XmlElement(name = "CurDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar curDate;

    /**
     * Gets the value of the regNumber property.
     * 
     */
    public int getRegNumber() {
        return regNumber;
    }

    /**
     * Sets the value of the regNumber property.
     * 
     */
    public void setRegNumber(int value) {
        this.regNumber = value;
    }

    /**
     * Gets the value of the fullname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Sets the value of the fullname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullname(String value) {
        this.fullname = value;
    }

    /**
     * Gets the value of the currentVersionNumber property.
     * 
     */
    public int getCurrentVersionNumber() {
        return currentVersionNumber;
    }

    /**
     * Sets the value of the currentVersionNumber property.
     * 
     */
    public void setCurrentVersionNumber(int value) {
        this.currentVersionNumber = value;
    }

    /**
     * Gets the value of the curDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCurDate() {
        return curDate;
    }

    /**
     * Sets the value of the curDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCurDate(XMLGregorianCalendar value) {
        this.curDate = value;
    }

}
