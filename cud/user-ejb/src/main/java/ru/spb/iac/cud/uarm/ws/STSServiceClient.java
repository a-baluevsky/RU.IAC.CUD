package ru.spb.iac.cud.uarm.ws;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import mypackage.Configuration;

import org.apache.xml.security.transforms.Transforms;
import org.picketlink.common.constants.JBossSAMLURIConstants;
import org.picketlink.common.constants.WSTrustConstants;
import org.picketlink.common.exceptions.fed.WSTrustException;
import org.picketlink.common.util.Base64;
import org.picketlink.identity.federation.core.saml.v2.util.DocumentUtil;
import org.picketlink.identity.federation.core.wstrust.WSTrustUtil;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityToken;
import org.picketlink.identity.federation.core.wstrust.writers.WSTrustRequestWriter;
import org.picketlink.identity.federation.ws.trust.UseKeyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


 public class STSServiceClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(STSServiceClient.class);
	
	// используется только для setAppliesTo
	static String endpointURI = "https://acc.lan.iac.spb.ru:18443/CudServicesSTS/CUDSTS";
	
	//test-contur
	//"https://localhost:7443/CudServicesSTS/CUDSTS?wsdl";
	//+ в jboss-deployment-structure.xml
	
	static String wsdlLocationURI =Configuration.getStsService();
	
	
	  private static PublicKey publicKey = null;
	    
	   private static PrivateKey privateKey = null;
	    
	   private static  Certificate cert = null;
	
	   public String sign_verify_soap_transform_2sign() throws Exception {
			
			return DocumentUtil.asString(sign_verify_soap_transform_2sign(null));
		}
		
		public Document sign_verify_soap_transform_2sign(String onBehalfOfToken) throws Exception  {

			
		
	
	   // обёрнутая подпись usernametoken в header в soap
	   // с использования transform.
	   // теги transform добавляются в сигнатуру:
	/*   
	   <dsig:Signature xmlns:dsig='http://www.w3.org/2000/09/xmldsig#'>
	    <dsig:SignedInfo>
		     <dsig:Reference URI='#UsernameToken-1' xmlns:dsig='http://www.w3.org/2000/09/xmldsig#'>
				<dsig:Transforms xmlns:dsig='http://www.w3.org/2000/09/xmldsig#'>
					<dsig:Transform Algorithm='http://www.w3.org/2000/09/xmldsig#enveloped-signature' xmlns:dsig='http://www.w3.org/2000/09/xmldsig#'/>
	 				<dsig:Transform Algorithm='http://www.w3.org/2001/10/xml-exc-c14n#' xmlns:dsig='http://www.w3.org/2000/09/xmldsig#'/>
				</dsig:Transforms>
	*/
	   // при проверки подписи в логе перед Pre-digested input появляется 
	   // дополнительная информация про transform:
	/*   
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.DOMReference] (http-0.0.0.0-80-1) Data class name: ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheNodeSetData
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheTransform] (http-0.0.0.0-80-1) Created transform for algorithm: http://www.w3.org/2000/09/xmldsig#enveloped-signature
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheTransform] (http-0.0.0.0-80-1) Created transform for algorithm: http://www.w3.org/2000/09/xmldsig#enveloped-signature
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheTransform] (http-0.0.0.0-80-1) ApacheData = true
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheTransform] (http-0.0.0.0-80-1) ApacheData = true
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheCanonicalizer] (http-0.0.0.0-80-1) Created transform for algorithm: http://www.w3.org/2001/10/xml-exc-c14n#
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheCanonicalizer] (http-0.0.0.0-80-1) Created transform for algorithm: http://www.w3.org/2001/10/xml-exc-c14n#
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheCanonicalizer] (http-0.0.0.0-80-1) ApacheData = true
	   [ru.CryptoPro.JCPxml.dsig.internal.dom.ApacheCanonicalizer] (http-0.0.0.0-80-1) ApacheData = true
	   [ru.CryptoPro.JCPxml.dsig.internal.DigesterOutputStream] (http-0.0.0.0-80-1) Pre-digested input:
	   [ru.CryptoPro.JCPxml.dsig.internal.DigesterOutputStream] (http-0.0.0.0-80-1) Pre-digested input:  
	  */  
	
	/*	
	   сертификат имеет следующие расширения: 
       расширение "Использования ключа" включает в себя "Шифрование ключей" или "Согласование ключей"; 
       расширение "Улучшенный ключ" имеет значение "Проверка подлинности клиента".
     */
	
		LOGGER.debug("STSServiceClient:sign_verify_soap_transform_2sign:01+");	  
		
		final ThreadLocal<Dispatch<SOAPMessage>> dispatchLocal = new InheritableThreadLocal(); 
		try{
			
			
            System.setProperty("javax.net.debug", "all"); 
			System.setProperty("javax.net.ssl.trustStore", Configuration.getStorePath());
			System.setProperty("javax.net.ssl.trustStoreType", "HDImageStore");
			System.setProperty("javax.net.ssl.trustStorePassword", "Access_Control");
			
		    System.setProperty("javax.net.ssl.keyStore", "@");
			System.setProperty("javax.net.ssl.keyStoreType", "HDImageStore");
			System.setProperty("javax.net.ssl.keyStorePassword", "Access_Control");
			
		   //cxf2.7    
           //  System./setProper/ty("org/.apache.cxf./stax.allowInsecureParser", "true");
         
			/*
			//test-contur
			HostnameVeri/fier hv = new Host/nameVerifier() {
				publ/ic boolean verify(Strin/g urlHostName, SSLSession session) {
								/ LOGGER.in/fo("Warning: URL Host: "+ urlHostName+ " vs. " + session.getPeerHost());
								 return t/rue;
				  }
			};
			HttpsURLCon/nection.setDefau/ltHostnameVerifier(hv); 
           */
			
			String ServiceName = "CUDSTS";
			String PortName = "CUDSTSPort";
			
			
			char[] signingKeyPass="Access_Control".toCharArray();
			String signingAlias="cudvm_export";
		
			if(publicKey==null) {	
			KeyStore ks  = KeyStore.getInstance("HDImageStore", "JCP");
			ks.load(null, null);
			
			privateKey = (PrivateKey)ks.getKey(signingAlias, signingKeyPass);
			
			
			cert = ks.getCertificate(signingAlias);
		 	publicKey = cert.getPublicKey() ;
		 	
			}
				
			QName service = new QName("http://sts.services.cud.iac.spb.ru/", ServiceName);
			QName portName = new QName("http://sts.services.cud.iac.spb.ru/", PortName);
			
			
			URL wsdlLocation = new URL(wsdlLocationURI);
			
			Service jaxwsService = Service.create(wsdlLocation, service);
			
			//не работает с https - java7 - cxf
			//Service jaxw/sService = Serv/ice.crea/te(service);
			//jaxwsSer/vice.add/Port(portName, soapB/inding, endpointURI);
		
			//не нужен :
			//либо - Serv/ice.cre/ate(wsdlLocation, service) и нет addPort
			//либо - Servi/ce.cre/ate(service) и addPort
			//jaxwsSer/vice.add/Port(portName2,/ soapBinding, endpointURI);
			
		    Dispatch dispatch = jaxwsService.createDispatch(portName, SOAPMessage.class, Service.Mode.MESSAGE);
		    
		   
		  /*
		    //test-contur
		    HTTPConduit/ httpConduit = (HT/TPConduit)  ((org.apache.cxf.jaxws.DispatchImpl)dispatch).getClient().getConduit() ; 
			//HTTPCondu/it httpConduit = (HTTPConduit) ClientProxy.getClient(proxy).getConduit();
           	TLSClientP/arameters tlsCP = new TLSClientParameters();
			final SSLSocketF/actoryImpl sslFact = new SSLSocketFactoryImpl();
			tlsCP.setSS/LSocketFactory(sslFact);
			tlsCP.setDi/sabl/eCNCheck(true);
			httpCon/duit.setTls/ClientParameters(tlsCP);
			*/
		    
		    dispatchLocal.set(dispatch);
		    
		 
		    
		    MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		 
		    SOAPMessage sm = mf.createMessage();
		    SOAPPart soapPart = sm.getSOAPPart();
		    SOAPHeader header = sm.getSOAPHeader();
		    SOAPBody body = sm.getSOAPBody();
		   
		    
		    
		    soapPart.getEnvelope().addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
		    soapPart.getEnvelope().addNamespaceDeclaration("ds", "http://www.w3.org/2000/09/xmldsig#");
		
		 
		    body.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
		    body.setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id","Body");
		  
		    
		    header.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
		    header.setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id","Header");
		  
		    String system = "urn:sub-eis:employee:get";
		      
		
		    // при подписывании Pre-digested input показывает просто <wsse:Username/>,
		    // то есть подписывается именно <wsse:Username/>
		    // а когда мы перед отправкой вызываем parentNode.replaceChild, то элемент становится 
		    // <wsse:Username xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/><wsse:Password xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/>
		    // таким он и отправляется. 
		    // и на приёмной стороне при проверке подписи  Pre-digested input честно показывает 
		    //  <wsse:Username xmlns:wsse="http://..."/>, то есть проверяется именно <wsse:Username xmlns:wsse="http://..."/>
		    // и чтобы этого избежать нужно на SOAPElement использовать UsernameSOAP.addNamespaceDeclaration.
		    
		    
		    QName securityQN = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse");
			QName SystemTokenQN = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "UsernameToken", "wsse");
			
			
			QName systemQN = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Username", "wsse");
			QName timestampQN = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Timestamp", "wsu");
			QName createdQN = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Created", "wsu");
			
			
			SOAPElement securitySOAP = header.addChildElement(securityQN);
			
			securitySOAP.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
			securitySOAP.setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id","_id_sec");
			  
			
			//аутентификация системы
			SOAPElement systemTokenSOAP = securitySOAP.addChildElement(SystemTokenQN);
			SOAPElement SystemSOAP = systemTokenSOAP.addChildElement(systemQN);
			//обязательно
			SystemSOAP.addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
			//обязательна установка id аттрибута в элементе, который будем подписывать
			systemTokenSOAP.setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id", "SystemToken_1");
			SystemSOAP.addTextNode(system);
		
			
			//timestamp
            SOAPElement timestampSOAP = securitySOAP.addChildElement(timestampQN);
			SOAPElement createdSOAP = timestampSOAP.addChildElement(createdQN);
			
			createdSOAP.addTextNode(DatatypeFactory.newInstance()
			.newXMLGregorianCalendar(new GregorianCalendar()).toXMLFormat());
			
			
			
			
			
			//<env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"><env:Header wsu:Id="Header" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"><wsse:Security><wsse:UsernameToken wsu:Id="UsernameToken-1"><wsse:Username xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/><wsse:Password xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/></wsse:UsernameToken></wsse:Security></env:Header><env:Body wsu:Id="Body" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"/></env:Envelope>

			    
		   
			if(!org.apache.xml.security.Init.isInitialized()){
		      org.apache.xml.security.Init.init();
			}
		     Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();
		  
		     XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);

	    
			
			// Преобразования над блоком SignedInfo
			List<Transform> transformList = new ArrayList<Transform>();
			Transform transform = fac.newTransform(Transform.ENVELOPED, (XMLStructure) null);
			Transform transformC14N = fac.newTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS, (XMLStructure) null);
			transformList.add(transform);
			transformList.add(transformC14N);
		
		 
			
			
			 RequestSecurityToken request = new RequestSecurityToken();
			    
			    request.setContext("default-context");
			
			     //issue
			   request.setRequestType(URI.create("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue"));
			
			   
			     request.setTokenType(URI.create("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0"));
		
		if(onBehalfOfToken==null) {	     
			     
			    //add cert to RST for HOK
			 try{
			    	
			    	
			    
			      URI uri = new URI(WSTrustConstants.KEY_TYPE_PUBLIC); 
			      
			      request.setKeyType(uri);
			      
			     
			      
			      //!!!
			      Certificate cert_hok = cert;
			      
			         
			      byte[] value = Base64.encodeBytes(cert_hok.getEncoded()).getBytes();
			      
			     		  
	              UseKeyType useKeyType = new UseKeyType();

	              useKeyType.add(value);
			      
			      request.setUseKey(useKeyType);

			      
			    }catch(Exception e){
			    	 LOGGER.error("cud_sts:04:"+e);	
			    }
		}else{
			 //!!! OnBehalfOf
			 request.setOnBehalfOf(WSTrustUtil.createOnBehalfOfWithUsername(
						onBehalfOfToken, "ID_1"));
		}
		
			    //!!! setAppliesTo
			    request.setAppliesTo(WSTrustUtil.createAppliesTo(endpointURI));
			    
			
			    
			
				    
			    DOMSource requestSource = createSourceFromRequest(request);
			    body.addDocument((Document)requestSource.getNode());
			    

			    
		  
		   
			    //что подписывать
		   
		    
		        Reference ref1 = fac.newReference("#Header", fac.newDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411", null) 
		        		,transformList, null, null);
		        Reference ref2 = fac.newReference("#Body", fac.newDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411", null) 
		        		,transformList, null, null);
		        List<Reference> referenceList = new ArrayList<Reference>();
		        
		        referenceList.add(ref1);
		        referenceList.add(ref2);
		        
		  
		        
				SignedInfo si = fac.newSignedInfo( fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
						 								(C14NMethodParameterSpec) null),
						 						   fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411", null),
						 						  referenceList);

				KeyInfoFactory kif = fac.getKeyInfoFactory();
				
				KeyValue kv = kif.newKeyValue(publicKey);
				KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
				
			
				
			javax.xml.crypto.dsig.XMLSignature sig = fac.newXMLSignature(si, ki);
			
				//куда вставлять подпись
				DOMSignContext signContext = new DOMSignContext(privateKey, securitySOAP); 
				
				signContext.putNamespacePrefix(XMLSignature.XMLNS, "dsig");
				
				//фиксация аттрибута id в подписываемом элементе
				//место ответственное за факт появления Pre-digested input в логе
			
				signContext.setIdAttributeNS(body, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
				signContext.setIdAttributeNS(header, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
			
			
				
				    
		    sig.sign(signContext);
		    
		  //  Pre-digested input: <wsse:UsernameToken wsu:Id="UsernameToken-1"><wsse:Username xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"></wsse:Username><wsse:Password xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"></wsse:Password></wsse:UsernameToken>
		    
		    
		   
		    
		    // <wsse:UsernameToken xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="UsernameToken-1"><wsse:Username xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/><wsse:Password xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/><dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/><dsig:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411"/><dsig:Reference URI="#UsernameToken-1"><dsig:Transforms><dsig:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/><dsig:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/></dsig:Transforms><dsig:DigestMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#gostr3411"/><dsig:DigestValue>CjgZhMuDCKWfzsPLhwI6m72TuatCNeoaZP90a5etMEA=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>pwGfBx3dXoaFC0hnnaCH4ZAfBuPI8aAYXZq6vQEdY8AUoHeiELjuuim7RaBp8thnkJ5LtMjNm714l8A+CIp7Cg==</dsig:SignatureValue><dsig:KeyInfo><dsig:KeyValue><dsig:GOSTKeyValue><dsig:PublicKey>MGMwHAYGKoUDAgITMBIGByqFAwICJAAGByqFAwICHgEDQwAEQCunDBpRlzBS7ROt4BrFULCojjzROAkng89j1UOqRoIzpIdrgbHb5F5IxyxprcTPu2gDuCLhSvNCk5nEP5jdx80=</dsig:PublicKey></dsig:GOSTKeyValue></dsig:KeyValue></dsig:KeyInfo></dsig:Signature></wsse:UsernameToken>

		   
			 
			 //----------------------------------------------------------------
			 // проверка подписи
			 
           Node signatureNode1 = securitySOAP.getLastChild();	 
          
			 DOMValidateContext valContext1 = new DOMValidateContext(publicKey, signatureNode1);
			 valContext1.putNamespacePrefix(XMLSignature.XMLNS, "dsig");
			 
			    
			 valContext1.setIdAttributeNS(body, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
			 valContext1.setIdAttributeNS(header, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
				
			 
		    javax.xml.crypto.dsig.XMLSignature signature1 = fac.unmarshalXMLSignature(valContext1);
	        
		    	boolean result1 = signature1.validate(valContext1);
				 
		    	 LOGGER.debug("dispatch:011_5:"+result1);
		    	 
				 
		    	 
		    	// Pre-digested input: <wsse:UsernameToken wsu:Id="UsernameToken-1"><wsse:Username xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"></wsse:Username><wsse:Password xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"></wsse:Password></wsse:UsernameToken>

		    	 
		    	
		    	//----------------------------------------------------------------
				// подготовка soap - замена неподписанного элемента подписанным
			    	 
		    	
		    	 //<env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"><env:Header wsu:Id="Header" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"><wsse:Security><wsse:UsernameToken wsu:Id="UsernameToken-1"><wsse:Username xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/><wsse:Password xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"/><dsig:Signature xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"><dsig:SignedInfo><dsig:CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"/><dsig:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411" xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"/><dsig:Reference URI="#UsernameToken-1" xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"><dsig:Transforms xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"><dsig:Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature" xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"/><dsig:Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#" xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"/></dsig:Transforms><dsig:DigestMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#gostr3411" xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"/><dsig:DigestValue xmlns:dsig="http://www.w3.org/2000/09/xmldsig#">CjgZhMuDCKWfzsPLhwI6m72TuatCNeoaZP90a5etMEA=</dsig:DigestValue></dsig:Reference></dsig:SignedInfo><dsig:SignatureValue>pwGfBx3dXoaFC0hnnaCH4ZAfBuPI8aAYXZq6vQEdY8AUoHeiELjuuim7RaBp8thnkJ5LtMjNm714l8A+CIp7Cg==</dsig:SignatureValue><dsig:KeyInfo><dsig:KeyValue xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"><dsig:GOSTKeyValue xmlns:dsig="http://www.w3.org/2000/09/xmldsig#"><dsig:PublicKey xmlns:dsig="http://www.w3.org/2000/09/xmldsig#">MGMwHAYGKoUDAgITMBIGByqFAwICJAAGByqFAwICHgEDQwAEQCunDBpRlzBS7ROt4BrFULCojjzROAkng89j1UOqRoIzpIdrgbHb5F5IxyxprcTPu2gDuCLhSvNCk5nEP5jdx80=</dsig:PublicKey></dsig:GOSTKeyValue></dsig:KeyValue></dsig:KeyInfo></dsig:Signature></wsse:UsernameToken></wsse:Security></env:Header><env:Body wsu:Id="Body" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"><wst:RequestSecurityToken xmlns:wst="http://docs.oasis-open.org/ws-sx/ws-trust/200512"><wst:RequestType>http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue</wst:RequestType><wst:TokenType>http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0</wst:TokenType></wst:RequestSecurityToken></env:Body></env:Envelope>
  		    	
		    SOAPMessage reply = (SOAPMessage) ((Dispatch)dispatchLocal.get()).invoke(sm);
		    
		   
		    
		    //проверка сигнатуры ответа
		    
		    
		    SOAPHeader soapHeader_reply =  reply.getSOAPHeader();
		    SOAPBody soapBody_reply =  reply.getSOAPBody();
		    
		  //signature
	    	 NodeList signatureListReply = soapHeader_reply.getElementsByTagNameNS("*", "Signature");
	    	 
	    	 if(signatureListReply==null||signatureListReply.getLength()==0){
	    		 LOGGER.debug("TestServerCryptoSOAPHandler:handleMessage:02_1");
			        throw new Exception("This service requires <dsig:Signature>, which is missing!!!");
	    	 }
	    	 
	    	  PublicKey publicKeyReply = null;
	    	  
	    	 //X509Certificate
	    	 NodeList x509CertificateListReply = ((Element)signatureListReply.item(0)).getElementsByTagNameNS("*", "X509Certificate");
	    	 
	    	 if(x509CertificateListReply!=null&&x509CertificateListReply.getLength()>0){
	    		 LOGGER.debug("TestServerCryptoSOAPHandler:handleMessage:02_2");
			 
	    	 
          	 String x509CertValue = x509CertificateListReply.item(0).getTextContent();
	    	 
             byte [] byteX509Certificate = Base64.decode(x509CertValue);
	    	 
	    	 X509Certificate certReply = null;
	    	 
	    	 CertificateFactory cf = CertificateFactory.getInstance("X.509");
	         ByteArrayInputStream bais = new ByteArrayInputStream(byteX509Certificate);

	         while (bais.available() > 0) {
				certReply = (X509Certificate)cf.generateCertificate(bais);
			}
	         
	         
	         
	       
	         if(certReply!=null){
	        	 publicKeyReply=certReply.getPublicKey();
	         }

	        
	         
	    	 }
	    	 
	    	// 2-я попытка - GOSTKeyValue
	    	 if(publicKeyReply==null){
	    		 
		    	 NodeList GOSTKeyListReply = ((Element)signatureListReply.item(0)).getElementsByTagNameNS("*", "GOSTKeyValue");
		    	 
		    	 if(GOSTKeyListReply==null||GOSTKeyListReply.getLength()==0){
		    		 LOGGER.debug("TestServerCryptoSOAPHandler:handleMessage:02_3");
				        throw new Exception("This service requires <dsig:X509Certificate> or <dsig:GOSTKeyValue>, which is missing!!!");
		    	 }
		    	 
                 NodeList publicKeyListReply = ((Element)signatureListReply.item(0)).getElementsByTagNameNS("*", "PublicKey");
		    	 
		    	 if(publicKeyListReply==null||publicKeyListReply.getLength()==0){
		    		 LOGGER.debug("TestServerCryptoSOAPHandler:handleMessage:02_4");
				        throw new Exception("This service requires <dsig:PublicKey>, which is missing!!!");
		    	 }
		    	 
		    	 String base64PublKey_reply = publicKeyListReply.item(0).getTextContent();
    	    	 
    	    	 
		        
		    
		         byte [] bytePublKey_reply = Base64.decode(base64PublKey_reply);
		    
		         KeyFactory keyFactoryReply = KeyFactory.getInstance("GOST3410");
			     EncodedKeySpec publicKeySpecReply = new X509EncodedKeySpec(bytePublKey_reply);
			     publicKeyReply = keyFactoryReply.generatePublic(publicKeySpecReply);
	    	 }
	    	 
	    	 if(publicKeyReply==null){
		    	 
	    		    throw new Exception("Public key is null!!!");
	    	}
	    	 
		      Node signatureNode1Reply =signatureListReply.item(0);
	         
	         
	     
	         
	        DOMValidateContext valContext1_reply = new DOMValidateContext(publicKeyReply, signatureNode1Reply);
	        
	        valContext1_reply.putNamespacePrefix(XMLSignature.XMLNS, "dsig");
	        
	         
			   
			valContext1_reply.setIdAttributeNS(soapHeader_reply, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
			valContext1_reply.setIdAttributeNS(soapBody_reply, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id");
	  
			    
		       javax.xml.crypto.dsig.XMLSignature signature1_reply = fac.unmarshalXMLSignature(valContext1_reply);
	        
		    	boolean result1_reply = signature1_reply.validate(valContext1_reply);
				 
		    	 LOGGER.debug("STSServiceClient:sign_verify_soap_transform_2sign:70:validate:"+result1_reply);
	    	 
               NodeList assertionList = soapBody_reply.getElementsByTagNameNS(JBossSAMLURIConstants.ASSERTION_NSURI.get(), "Assertion");
		    	 
               if(assertionList==null||assertionList.getLength()==0){
		    		 LOGGER.debug("STSServiceClient:sign_verify_soap_transform_2sign:71");
				        throw new Exception("This service requires <Assertion>, which is missing!!!");
		    	 }
               
               Document newDocAssertion = DocumentUtil.createDocument();
               Node nodeAssertion = newDocAssertion.importNode(assertionList.item(0), true);
               newDocAssertion.appendChild(nodeAssertion);
               
            	
               return newDocAssertion;
		    	 
		}catch(Exception e){
			LOGGER.error("STSServiceClient:sign_verify_soap_transform_2sign:error:"+e);	  
			throw e;
		}
	}



	 private static DOMSource createSourceFromRequest(RequestSecurityToken request) throws WSTrustException {
		    try {
		      DOMResult result = new DOMResult(DocumentUtil.createDocument());
		      WSTrustRequestWriter writer = new WSTrustRequestWriter(result);
		      writer.write(request);
		      return new DOMSource(result.getNode());
		     } catch (Exception e) {
		    	 throw new WSTrustException(e);
		    }
		  
		  }
	 public static void propagateIDAttributeSetup(Node sourceNode, Element destElement)
	  {
	    NamedNodeMap nnm = sourceNode.getAttributes();
	    for (int i = 0; i < nnm.getLength(); i++) {
	      Attr attr = (Attr)nnm.item(i);
	      if (attr.isId()) {
	        destElement.setIdAttribute(attr.getName(), true);
	        break;
	      }
	    }
	  }
  
}
