
package iac.cud.infosweb.ws.classifierzip;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.spb.iac.ws.classifierzip package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
 public class ObjectFactory {

    private static final  QName _GetClassifierZipListByClassifierNumber_QNAME = new QName("http://ws.iac.spb.ru/ClassifierZip", "getClassifierZipListByClassifierNumber");
    private static final  QName _GetClassifierZipListByClassifierNameResponse_QNAME = new QName("http://ws.iac.spb.ru/ClassifierZip", "getClassifierZipListByClassifierNameResponse");
    private static final  QName _GetClassifierByClassifierName_QNAME = new QName("http://ws.iac.spb.ru/ClassifierZip", "getClassifierByClassifierName");
    private static final  QName _GetClassifierZipListByClassifierNumberResponse_QNAME = new QName("http://ws.iac.spb.ru/ClassifierZip", "getClassifierZipListByClassifierNumberResponse");
    private static final  QName _GetClassifierByClassifierNumber_QNAME = new QName("http://ws.iac.spb.ru/ClassifierZip", "getClassifierByClassifierNumber");
    private static final  QName _GetClassifierByClassifierNumberResponse_QNAME = new QName("http://ws.iac.spb.ru/ClassifierZip", "getClassifierByClassifierNumberResponse");
    private static final  QName _GetClassifierZipListByClassifierName_QNAME = new QName("http://ws.iac.spb.ru/ClassifierZip", "getClassifierZipListByClassifierName");
    private static final  QName _GetClassifierByClassifierNameResponse_QNAME = new QName("http://ws.iac.spb.ru/ClassifierZip", "getClassifierByClassifierNameResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.spb.iac.ws.classifierzip
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetClassifierByClassifierNameResponse }
     * 
     */
    public GetClassifierByClassifierNameResponse createGetClassifierByClassifierNameResponse() {
        return new GetClassifierByClassifierNameResponse();
    }

    /**
     * Create an instance of {@link GetClassifierZipListByClassifierName }
     * 
     */
    public GetClassifierZipListByClassifierName createGetClassifierZipListByClassifierName() {
        return new GetClassifierZipListByClassifierName();
    }

    /**
     * Create an instance of {@link GetClassifierByClassifierNumberResponse }
     * 
     */
    public GetClassifierByClassifierNumberResponse createGetClassifierByClassifierNumberResponse() {
        return new GetClassifierByClassifierNumberResponse();
    }

    /**
     * Create an instance of {@link GetClassifierByClassifierNumber }
     * 
     */
    public GetClassifierByClassifierNumber createGetClassifierByClassifierNumber() {
        return new GetClassifierByClassifierNumber();
    }

    /**
     * Create an instance of {@link GetClassifierZipListByClassifierNumberResponse }
     * 
     */
    public GetClassifierZipListByClassifierNumberResponse createGetClassifierZipListByClassifierNumberResponse() {
        return new GetClassifierZipListByClassifierNumberResponse();
    }

    /**
     * Create an instance of {@link GetClassifierByClassifierName }
     * 
     */
    public GetClassifierByClassifierName createGetClassifierByClassifierName() {
        return new GetClassifierByClassifierName();
    }

    /**
     * Create an instance of {@link GetClassifierZipListByClassifierNameResponse }
     * 
     */
    public GetClassifierZipListByClassifierNameResponse createGetClassifierZipListByClassifierNameResponse() {
        return new GetClassifierZipListByClassifierNameResponse();
    }

    /**
     * Create an instance of {@link GetClassifierZipListByClassifierNumber }
     * 
     */
    public GetClassifierZipListByClassifierNumber createGetClassifierZipListByClassifierNumber() {
        return new GetClassifierZipListByClassifierNumber();
    }

    /**
     * Create an instance of {@link ResponseElement53 }
     * 
     */
    public ResponseElement53 createResponseElement53() {
        return new ResponseElement53();
    }

    /**
     * Create an instance of {@link ResponseElement52 }
     * 
     */
    public ResponseElement52 createResponseElement52() {
        return new ResponseElement52();
    }

    /**
     * Create an instance of {@link ResponseElement54 }
     * 
     */
    public ResponseElement54 createResponseElement54() {
        return new ResponseElement54();
    }

    /**
     * Create an instance of {@link Item51 }
     * 
     */
    public Item51 createItem51() {
        return new Item51();
    }

    /**
     * Create an instance of {@link ResponseElement51 }
     * 
     */
    public ResponseElement51 createResponseElement51() {
        return new ResponseElement51();
    }

    /**
     * Create an instance of {@link Item52 }
     * 
     */
    public Item52 createItem52() {
        return new Item52();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetClassifierZipListByClassifierNumber }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.iac.spb.ru/ClassifierZip", name = "getClassifierZipListByClassifierNumber")
    public JAXBElement<GetClassifierZipListByClassifierNumber> createGetClassifierZipListByClassifierNumber(GetClassifierZipListByClassifierNumber value) {
        return new JAXBElement<GetClassifierZipListByClassifierNumber>(_GetClassifierZipListByClassifierNumber_QNAME, GetClassifierZipListByClassifierNumber.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetClassifierZipListByClassifierNameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.iac.spb.ru/ClassifierZip", name = "getClassifierZipListByClassifierNameResponse")
    public JAXBElement<GetClassifierZipListByClassifierNameResponse> createGetClassifierZipListByClassifierNameResponse(GetClassifierZipListByClassifierNameResponse value) {
        return new JAXBElement<GetClassifierZipListByClassifierNameResponse>(_GetClassifierZipListByClassifierNameResponse_QNAME, GetClassifierZipListByClassifierNameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetClassifierByClassifierName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.iac.spb.ru/ClassifierZip", name = "getClassifierByClassifierName")
    public JAXBElement<GetClassifierByClassifierName> createGetClassifierByClassifierName(GetClassifierByClassifierName value) {
        return new JAXBElement<GetClassifierByClassifierName>(_GetClassifierByClassifierName_QNAME, GetClassifierByClassifierName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetClassifierZipListByClassifierNumberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.iac.spb.ru/ClassifierZip", name = "getClassifierZipListByClassifierNumberResponse")
    public JAXBElement<GetClassifierZipListByClassifierNumberResponse> createGetClassifierZipListByClassifierNumberResponse(GetClassifierZipListByClassifierNumberResponse value) {
        return new JAXBElement<GetClassifierZipListByClassifierNumberResponse>(_GetClassifierZipListByClassifierNumberResponse_QNAME, GetClassifierZipListByClassifierNumberResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetClassifierByClassifierNumber }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.iac.spb.ru/ClassifierZip", name = "getClassifierByClassifierNumber")
    public JAXBElement<GetClassifierByClassifierNumber> createGetClassifierByClassifierNumber(GetClassifierByClassifierNumber value) {
        return new JAXBElement<GetClassifierByClassifierNumber>(_GetClassifierByClassifierNumber_QNAME, GetClassifierByClassifierNumber.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetClassifierByClassifierNumberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.iac.spb.ru/ClassifierZip", name = "getClassifierByClassifierNumberResponse")
    public JAXBElement<GetClassifierByClassifierNumberResponse> createGetClassifierByClassifierNumberResponse(GetClassifierByClassifierNumberResponse value) {
        return new JAXBElement<GetClassifierByClassifierNumberResponse>(_GetClassifierByClassifierNumberResponse_QNAME, GetClassifierByClassifierNumberResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetClassifierZipListByClassifierName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.iac.spb.ru/ClassifierZip", name = "getClassifierZipListByClassifierName")
    public JAXBElement<GetClassifierZipListByClassifierName> createGetClassifierZipListByClassifierName(GetClassifierZipListByClassifierName value) {
        return new JAXBElement<GetClassifierZipListByClassifierName>(_GetClassifierZipListByClassifierName_QNAME, GetClassifierZipListByClassifierName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetClassifierByClassifierNameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.iac.spb.ru/ClassifierZip", name = "getClassifierByClassifierNameResponse")
    public JAXBElement<GetClassifierByClassifierNameResponse> createGetClassifierByClassifierNameResponse(GetClassifierByClassifierNameResponse value) {
        return new JAXBElement<GetClassifierByClassifierNameResponse>(_GetClassifierByClassifierNameResponse_QNAME, GetClassifierByClassifierNameResponse.class, null, value);
    }

}
