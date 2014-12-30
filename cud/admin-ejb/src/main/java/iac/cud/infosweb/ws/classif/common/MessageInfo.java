
package iac.cud.infosweb.ws.classif.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FaultCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="FaultMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StartPosition" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="Diapason" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ItemsInMessage" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ItemsInResponse" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ItemStructure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageInfo", propOrder = {
    "faultCode",
    "faultMessage",
    "startPosition",
    "diapason",
    "itemsInMessage",
    "itemsInResponse",
    "itemStructure"
})
 public class MessageInfo {

    @XmlElement(name = "FaultCode")
    protected int faultCode;
    @XmlElement(name = "FaultMessage", required = true)
    protected String faultMessage;
    @XmlElement(name = "StartPosition")
    protected Integer startPosition;
    @XmlElement(name = "Diapason")
    protected Integer diapason;
    @XmlElement(name = "ItemsInMessage")
    protected Integer itemsInMessage;
    @XmlElement(name = "ItemsInResponse")
    protected Integer itemsInResponse;
    @XmlElement(name = "ItemStructure")
    protected String itemStructure;

    /**
     * Gets the value of the faultCode property.
     * 
     */
    public int getFaultCode() {
        return faultCode;
    }

    /**
     * Sets the value of the faultCode property.
     * 
     */
    public void setFaultCode(int value) {
        this.faultCode = value;
    }

    /**
     * Gets the value of the faultMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaultMessage() {
        return faultMessage;
    }

    /**
     * Sets the value of the faultMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaultMessage(String value) {
        this.faultMessage = value;
    }

    /**
     * Gets the value of the startPosition property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStartPosition() {
        return startPosition;
    }

    /**
     * Sets the value of the startPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStartPosition(Integer value) {
        this.startPosition = value;
    }

    /**
     * Gets the value of the diapason property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDiapason() {
        return diapason;
    }

    /**
     * Sets the value of the diapason property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDiapason(Integer value) {
        this.diapason = value;
    }

    /**
     * Gets the value of the itemsInMessage property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getItemsInMessage() {
        return itemsInMessage;
    }

    /**
     * Sets the value of the itemsInMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setItemsInMessage(Integer value) {
        this.itemsInMessage = value;
    }

    /**
     * Gets the value of the itemsInResponse property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getItemsInResponse() {
        return itemsInResponse;
    }

    /**
     * Sets the value of the itemsInResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setItemsInResponse(Integer value) {
        this.itemsInResponse = value;
    }

    /**
     * Gets the value of the itemStructure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemStructure() {
        return itemStructure;
    }

    /**
     * Sets the value of the itemStructure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemStructure(String value) {
        this.itemStructure = value;
    }

}
