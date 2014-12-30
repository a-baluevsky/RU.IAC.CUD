
package iac.cud.infosweb.ws.classif.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Response2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Response2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Facet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Fullname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ActualDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="DeactualDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ActualDoc" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="DeactualDoc" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Response2", propOrder = {
    "id",
    "facet",
    "code",
    "fullname",
    "actualDate",
    "deactualDate",
    "status",
    "actualDoc",
    "deactualDoc"
})
 public class Response2 {

    @XmlElement(name = "ID")
    protected int id;
    @XmlElement(name = "Facet")
    protected String facet;
    @XmlElement(name = "Code", required = true)
    protected String code;
    @XmlElement(name = "Fullname", required = true)
    protected String fullname;
    @XmlElement(name = "ActualDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar actualDate;
    @XmlElement(name = "DeactualDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar deactualDate;
    @XmlElement(name = "Status")
    protected String status;
    @XmlElement(name = "ActualDoc")
    protected Integer actualDoc;
    @XmlElement(name = "DeactualDoc")
    protected Integer deactualDoc;

    /**
     * Gets the value of the id property.
     * 
     */
    public int getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setID(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the facet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFacet() {
        return facet;
    }

    /**
     * Sets the value of the facet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFacet(String value) {
        this.facet = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
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
     * Gets the value of the actualDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getActualDate() {
        return actualDate;
    }

    /**
     * Sets the value of the actualDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setActualDate(XMLGregorianCalendar value) {
        this.actualDate = value;
    }

    /**
     * Gets the value of the deactualDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeactualDate() {
        return deactualDate;
    }

    /**
     * Sets the value of the deactualDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeactualDate(XMLGregorianCalendar value) {
        this.deactualDate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the actualDoc property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getActualDoc() {
        return actualDoc;
    }

    /**
     * Sets the value of the actualDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setActualDoc(Integer value) {
        this.actualDoc = value;
    }

    /**
     * Gets the value of the deactualDoc property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDeactualDoc() {
        return deactualDoc;
    }

    /**
     * Sets the value of the deactualDoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDeactualDoc(Integer value) {
        this.deactualDoc = value;
    }

}
