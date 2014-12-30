
package iac.cud.infosweb.ws.classif.common;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.spb.iac.ws.common package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.spb.iac.ws.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MessageInfo }
     * 
     */
    public MessageInfo createMessageInfo() {
        return new MessageInfo();
    }

    /**
     * Create an instance of {@link Response1 }
     * 
     */
    public Response1 createResponse1() {
        return new Response1();
    }

    /**
     * Create an instance of {@link Response2 }
     * 
     */
    public Response2 createResponse2() {
        return new Response2();
    }

    /**
     * Create an instance of {@link Response4 }
     * 
     */
    public Response4 createResponse4() {
        return new Response4();
    }

}
