package ru.spb.iac.cud.uarm.web.context.user;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.ContentInfo;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.DigestAlgorithmIdentifier;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.SignedData;
import ru.CryptoPro.JCP.params.OID;
import ru.CryptoPro.JCP.tools.Decoder;
import ru.spb.iac.cud.uarm.ejb.context.user.UserManagerEJB;
import ru.spb.iac.cud.uarm.ejb.entity.AcUsersCertBssT;
import ru.spb.iac.cud.uarm.util.CUDUserConsoleConstants;

import com.objsys.asn1j.runtime.Asn1BerDecodeBuffer;
import com.objsys.asn1j.runtime.Asn1BerEncodeBuffer;

@WebServlet("/WebCertAction")
@ManagedBean(name="webCertActionBean")
@RequestScoped
 public class WebCertAction extends HttpServlet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebCertAction.class);
	
	@EJB(beanName = "CUDUserConsole-ejb.jar#UserManagerEJB")
	private UserManagerEJB userManagerEJB;
	
	private static final long serialVersionUID = 1L;

	
	public static final String STR_CMS_OID_SIGNED = "1.2.840.113549.1.7.2";
	public static final String DIGEST_OID = JCP.GOST_DIGEST_OID;
	
	private static String alias_root="уцспбгуп«спбиац».crt";
	
	private String root_sn = null;
    
	private static KeyStore keyStore = null;
	
	private static String cert_store_url;
	
    public WebCertAction() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init(ServletConfig config) throws ServletException {
		try{
			
			if(cert_store_url==null){
				cert_store_url = config.getServletContext().getInitParameter("cert_store_url");
			}
			
			if(cert_store_url==null){
				throw new Exception("cert_store_url is not set!!!");
			}
			
			 
			
		}catch(Exception e){
			 LOGGER.error("WebCertAction:init:error:"+e);
		} 
	}
    
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String success = "false";
		String signatureValue=null;
		
		String destination = null;
		
		try{
			
		
			signatureValue = request.getParameter("signatureValue");
			
			
			if(signatureValue!=null){
			
		       X509Certificate userCertX =validate(signatureValue);
		      
		      
		      LOGGER.debug("WebCertAction:service:user_cert:"+(userCertX  !=null));
		      
		      if(userCertX  !=null){
		    	 
		    	  LOGGER.debug("WebCertAction:service:userManagerEJB:"+(userManagerEJB==null));
		    	  
			  	
		    	if (addUserCert(userCertX, request)==1){
		  	       success="true"; 
		       	}
			
					    
			  }
			}
		}catch(Exception e4){
		    LOGGER.error("WebCertAction:error4:"+e4.getMessage());
		}
		
		
		 if("true".equals(success)){
			 
			 destination = request.getContextPath()+"/context/profile/cert/list.xhtml?pageItem=profile_cert";
			 
		 }else{
			 
			 destination = request.getContextPath()+"/context/profile/cert/create/list.xhtml?pageItem=profile_cert&error=cert_exist";
			
		 }
		 
		 location_href(response, destination);
		 
	}

    private void location_href(HttpServletResponse response, String destination) {
    	
		try {

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			common(response);
			StringBuilder builder = new StringBuilder();

			builder.append("<HTML>");
			builder.append("<HEAD>");
			builder.append("<TITLE>HTTP Response</TITLE>");
			builder.append("</HEAD>");
			builder.append("<BODY>");

			builder.append("</BODY>");
			
			builder.append("<script>");
			builder.append("//<![CDATA[ \n");
			
			builder.append("window.parent.location.href=");
			builder.append("\""+destination+"\";");
			builder.append("\n //]]>");
			builder.append("</script>");
			
			builder.append("</HTML>");

			String str = builder.toString();

			LOGGER.debug("WebCertAction:location_href:" + str);

			out.println(str);
			out.close();

		} catch (Exception e) {
			LOGGER.error("WebCertAction:location_href:error:", e);
		}
	}
    
    
	private static void common( HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, no-store");
	}

 
private X509Certificate validate(String message){
	
	try{
	
	final Decoder decoder = new Decoder();
    final byte[] enc =
            decoder.decodeBuffer(new ByteArrayInputStream(message.getBytes()));
  
  LOGGER.debug("main:04");
  if(enc!=null){
     
   }
  
    return CMSVerify(enc,null, "12345".getBytes());
	}catch(Exception e){
		LOGGER.error("decode:error:"+e);
	}
	return null;
		
}

public X509Certificate CMSVerify(byte[] buffer, Certificate[] certs, byte[] data)
        throws Exception {
    //clear buffers fo logs
	
	 
   final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(buffer);
    
    
    final ContentInfo all = new ContentInfo();
    
   
    all.decode(asnBuf);
    
   
    
    if (!new OID(STR_CMS_OID_SIGNED).eq(all.contentType.value)) {
		throw new Exception("Not supported");
	}
    final SignedData cms = (SignedData) all.content;
   
 
   
    OID digestOid = null;
    final DigestAlgorithmIdentifier digestAlgorithmIdentifier =
            new DigestAlgorithmIdentifier(new OID(DIGEST_OID).value);
    for (int i = 0; i < cms.digestAlgorithms.elements.length; i++) {
        if (cms.digestAlgorithms.elements[i].algorithm
                .equals(digestAlgorithmIdentifier.algorithm)) {
            digestOid =
                    new OID(cms.digestAlgorithms.elements[i].algorithm.value);
            break;
        }
    }
    if (digestOid == null) {
		throw new Exception("Unknown digest");
	}
     
    
    if (cms.certificates != null) {
     	
        //Проверка на вложенных сертификатах
        for (int i = 0; i < cms.certificates.elements.length; i++) {
            final Asn1BerEncodeBuffer encBuf = new Asn1BerEncodeBuffer();
            cms.certificates.elements[i].encode(encBuf);

            final CertificateFactory cf =
                    CertificateFactory.getInstance("X.509");
            final X509Certificate cert =
                    (X509Certificate) cf
                            .generateCertificate(encBuf.getInputStream());

            
             
          
           
          
          if(root_sn()!=null&&!root_sn().equals(dec_to_hex(cert.getSerialNumber()))){
        	
        
 		   
            if(chain_check(cert)){
            	
              return cert;
             }
         }  
            

        }
    }

    return null;
}

public static boolean chain_check(Certificate pcert) {
	// TODO Auto-generated method stub
	boolean result = false;
	
	try{
		
		if(keyStore==null) {
		keyStore = KeyStore.getInstance("CertStore", "JCP");
		
		keyStore.load(new FileInputStream(cert_store_url), "Access_Control".toCharArray());
		
		}
		
	
	 
	  
	  Enumeration aliases = keyStore.aliases();
	 while (aliases.hasMoreElements()) {
	  String alias = (String) aliases.nextElement();
	 
	  if (keyStore.isCertificateEntry(alias)) {
	 
	  }
	  }
	  
	  Certificate tr = keyStore.getCertificate(alias_root);
	  Certificate crt=pcert;
	  
	 
	  
	   
	   final Certificate[] certs = new Certificate[2];
	    certs[0] = crt;
	    certs[1] = tr;  //root

	    final Set trust = new HashSet(0);
	    trust.add(new TrustAnchor((X509Certificate) tr, null));

	    final List cert = new ArrayList(0);
	    for (int i = 0; i < certs.length; i++) {
			cert.add(certs[i]);
		}

	    //Параметры
	    final PKIXBuilderParameters cpp = new PKIXBuilderParameters(trust, null);
	    cpp.setSigProvider(null);
	    final CollectionCertStoreParameters par =
	            new CollectionCertStoreParameters(cert);
	    final CertStore store = CertStore.getInstance("Collection", par);
	    cpp.addCertStore(store);
	    final X509CertSelector selector = new X509CertSelector();
	    selector.setCertificate((X509Certificate) crt);
	    cpp.setTargetCertConstraints(selector);

	    //Сертификаты (CertPath)
	    //1)просто из списка сертификатов (в правильном порядке)
	    //final CertificateFactory cf = CertificateFactory.getInstance("X509");
	    //final CertPath cp = cf.generateCertPath(cert);

	    //2) построение цепочки
	    //а) с проверкой crl
	    //cpp/.setRevocationEnabled(true);
	    //для использования расширения сертификата CRL Distribution Points
	    //установить System/.setProperty/("com.sun.security.enableCRLDP", "true");
	    //или System/.setProperty/("com.ibm.security.enableCRLDP"/, "true")/

	    //б) без проверки crl
	    cpp.setRevocationEnabled(false);
	    final PKIXCertPathBuilderResult res =
	            (PKIXCertPathBuilderResult) CertPathBuilder.
	                    getInstance("PKIX").build(cpp);
	    final CertPath cp = res.getCertPath();

	    //Проверка
	    final CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
	    cpp.setRevocationEnabled(false);
	    cpv.validate(cp, cpp);
	    
	    result = true;
	  
	  
	}catch(Exception e){
		LOGGER.error("error:"+e);
	}
	
	return result;
}

private static String dec_to_hex(BigInteger bi) {
	
	String result = null;
	
	try
	{
	 result = bi.toString(16);
     
	}
	catch (NumberFormatException e)
	{
	     LOGGER.error("Error! tried to parse an invalid number format");
	}
	 return result;
}

public String root_sn() {
	// TODO Auto-generated method stub
	
	if(root_sn==null){
	
		 
		  
	try{
		
		if(keyStore==null) {
		  keyStore = KeyStore.getInstance("CertStore", "JCP");
		
		keyStore.load(new FileInputStream(cert_store_url), "Access_Control".toCharArray());
		}
		
	
	
	  
	  
	  X509Certificate tr = (X509Certificate)keyStore.getCertificate(alias_root);

	  root_sn = dec_to_hex(tr.getSerialNumber());
	  
	
	  
	  }catch(Exception e){
		  LOGGER.error("WebCertAction:root_sn:error:"+e);
		  }
	}
	
	return root_sn;
}

 public int addUserCert(X509Certificate userCertX, HttpServletRequest request) throws Exception { 
	
	InputStream inputStream = null;
	
	DateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	 
	int result = 0;
	try{
	
	   LOGGER.debug("WebCertAction:addUserCert:01");
	
	   HttpSession hs = request.getSession();
	   Long authUserID = (Long) hs.getAttribute(CUDUserConsoleConstants.authUserID);
	
	   String x509Cert = Base64.encode(userCertX.getEncoded());
       
       String serial = dec_to_hex(userCertX.getSerialNumber());
       
       if(userManagerEJB.certNumExistCrt(serial)){
    	   LOGGER.debug("WebCertAction:addUserCert:01_1:return;");
    	   return result;
       }
       
       AcUsersCertBssT userCert = new AcUsersCertBssT();
	   
	   userCert.setCertNum(serial);
	   userCert.setCertDate(df.format(userCertX.getNotAfter()));
	   
	   //!!!
	   //сохраняем именно не userCertX.getEncoded(),
	   //а x509Cert/.getBytes
	   userCert.setCertValue(x509Cert.getBytes("UTF-8"));
	   
	   String subject = userCertX.getSubjectDN().getName();
	   
	   LOGGER.debug("WebCertAction:addUserCert:02:"+subject);
	   
	   LdapName ldapDN = new LdapName(subject);
	   
	   for(Rdn rdn: ldapDN.getRdns()) {
		    LOGGER.debug(rdn.getType() + " -> " + rdn.getValue());
		    
		    if("CN".equals(rdn.getType())){
		    	userCert.setUserFio((String)rdn.getValue());
		    }else if("OU".equals(rdn.getType())){
		    	userCert.setDepName((String)rdn.getValue());
		    }else if("O".equals(rdn.getType())){
		    	userCert.setOrgName((String)rdn.getValue());
		    }
		    
		}
	   
	   userCert.setUpUserRaw(authUserID);
	   
	   userCert.setCreator(authUserID);
	   userCert.setCreated(new Date());
	   
	   
	   userManagerEJB.uploadCertFile(userCert);
	  
       
	   result=1;
   
       
	} catch(Exception e){
		 LOGGER.error("WebCertAction:addUserCert:error:"+e);
		 
		 result=-1;
	}finally{
	   try{
		   if(inputStream!=null){
			   inputStream.close();
		   }
	   }catch(Exception e){
		   
	   }
   }
	 return result;
}  


}
