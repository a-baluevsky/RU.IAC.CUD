/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package ru.spb.iac.cud.idp.web.handlers;

import java.net.URI;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.jboss.security.audit.AuditLevel;
import org.picketlink.common.constants.GeneralConstants;
import org.picketlink.common.constants.JBossSAMLURIConstants;
import org.picketlink.common.exceptions.ConfigurationException;
import org.picketlink.common.exceptions.ParsingException;
import org.picketlink.common.exceptions.ProcessingException;
import org.picketlink.config.federation.SPType;
import org.picketlink.identity.federation.api.saml.v2.request.SAML2Request;
import org.picketlink.identity.federation.api.saml.v2.response.SAML2Response;
import org.picketlink.identity.federation.core.audit.PicketLinkAuditEvent;
import org.picketlink.identity.federation.core.audit.PicketLinkAuditEventType;
import org.picketlink.identity.federation.core.audit.PicketLinkAuditHelper;
import org.picketlink.identity.federation.core.saml.v2.common.IDGenerator;
import org.picketlink.identity.federation.core.saml.v2.common.SAMLProtocolContext;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerRequest;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerRequest.GENERATE_REQUEST_TYPE;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2HandlerResponse;
import org.picketlink.identity.federation.core.saml.v2.util.XMLTimeUtil;
import org.picketlink.identity.federation.core.sts.PicketLinkCoreSTS;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAMLUtil;
import org.picketlink.identity.federation.saml.v2.SAML2Object;
import org.picketlink.identity.federation.saml.v2.assertion.AssertionType;
import org.picketlink.identity.federation.saml.v2.assertion.AuthnStatementType;
import org.picketlink.identity.federation.saml.v2.assertion.NameIDType;
import org.picketlink.identity.federation.saml.v2.assertion.StatementAbstractType;
import org.picketlink.identity.federation.saml.v2.protocol.LogoutRequestType;
import org.picketlink.identity.federation.saml.v2.protocol.ResponseType;
import org.picketlink.identity.federation.saml.v2.protocol.StatusCodeType;
import org.picketlink.identity.federation.saml.v2.protocol.StatusResponseType;
import org.picketlink.identity.federation.saml.v2.protocol.StatusType;
import org.picketlink.identity.federation.web.core.HTTPContext;
import org.picketlink.identity.federation.web.core.IdentityServer;
import org.picketlink.identity.federation.web.handlers.saml2.BaseSAML2Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import ru.spb.iac.cud.core.util.WebUtil;

/**
 * SAML2 LogOut Profile
 *
 * @author Anil.Saldhana@redhat.com
 * @since Sep 17, 2009
 */
 public class CUDSAML2LogOutHandler extends BaseSAML2Handler {

    private final IDPLogOutHandler idp = new IDPLogOutHandler();

    private final SPLogOutHandler sp = new SPLogOutHandler();

    private static final Logger LOGGERSLF4J = LoggerFactory.getLogger(CUDSAML2LogOutHandler.class);
    
     public void generateSAMLRequest(SAML2HandlerRequest request, SAML2HandlerResponse response) throws ProcessingException {
        if (request.getTypeOfRequestToBeGenerated() == null) {
            return;
        }
        if (GENERATE_REQUEST_TYPE.LOGOUT != request.getTypeOfRequestToBeGenerated()) {
			return;
		}

        if (getType() == HANDLER_TYPE.IDP) {
            idp.generateSAMLRequest(request, response);
        } else {
            sp.generateSAMLRequest(request, response);
        }
    }

     public void handleRequestType(SAML2HandlerRequest request, SAML2HandlerResponse response) throws ProcessingException {
        if (request.getSAML2Object() instanceof LogoutRequestType == false) {
			return;
		}

        if (getType() == HANDLER_TYPE.IDP) {
            idp.handleRequestType(request, response);
        } else {
            sp.handleRequestType(request, response);
        }
    }

     public void handleStatusResponseType(SAML2HandlerRequest request, SAML2HandlerResponse response) throws ProcessingException {
        // we do not handle any ResponseType (authentication etc)
        if (request.getSAML2Object() instanceof ResponseType) {
			return;
		}

        if (request.getSAML2Object() instanceof StatusResponseType == false) {
			return;
		}

        if (getType() == HANDLER_TYPE.IDP) {
            idp.handleStatusResponseType(request, response);
        } else {
            sp.handleStatusResponseType(request, response);
        }
    }

    private class IDPLogOutHandler {
        public void generateSAMLRequest(SAML2HandlerRequest request, SAML2HandlerResponse response) throws ProcessingException {
        }

        public void handleStatusResponseType(SAML2HandlerRequest request, SAML2HandlerResponse response)
                throws ProcessingException {
        	
        	LOGGERSLF4J.debug("handleStatusResponseType:01");
        	
            // we got a logout response from a SP
            SAML2Object samlObject = request.getSAML2Object();
            StatusResponseType statusResponseType = (StatusResponseType) samlObject;

            HTTPContext httpContext = (HTTPContext) request.getContext();
            HttpServletRequest httpRequest = httpContext.getRequest();
            HttpSession httpSession = httpRequest.getSession(false);

            String relayState = request.getRelayState();

            ServletContext servletCtx = httpContext.getServletContext();
            IdentityServer server = (IdentityServer) servletCtx.getAttribute("IDENTITY_SERVER");

            if (server == null) {
				throw logger.samlHandlerIdentityServerNotFoundError();
			}

            String sessionID = httpSession.getId();

            String statusIssuer = statusResponseType.getIssuer().getValue();
            server.stack().deRegisterTransitParticipant(sessionID, statusIssuer);

            String nextParticipant = this.getParticipant(server, sessionID, relayState);
            if (nextParticipant == null || nextParticipant.equals(relayState)) {
                // we are done with logout - First ask STS to cancel the token
                AssertionType assertion = (AssertionType) httpSession.getAttribute(GeneralConstants.ASSERTION);
                if (assertion != null) {
                    PicketLinkCoreSTS sts = PicketLinkCoreSTS.instance();
                    SAMLProtocolContext samlProtocolContext = new SAMLProtocolContext();
                    samlProtocolContext.setIssuedAssertion(assertion);
                    sts.cancelToken(samlProtocolContext);
                    httpSession.removeAttribute(GeneralConstants.ASSERTION);
                }

                // TODO: check the in transit map for partial logouts

                try {
                    generateSuccessStatusResponseType(statusResponseType.getInResponseTo(), request, response, relayState);

                    boolean isPost = isPostBindingForResponse(server, relayState, request);
                    response.setPostBindingForResponse(isPost);
                } catch (Exception e) {
                    throw logger.processingError(e);
                }
                Map<String, Object> requestOptions = request.getOptions();
                PicketLinkAuditHelper auditHelper = (PicketLinkAuditHelper) requestOptions.get(GeneralConstants.AUDIT_HELPER);
                if (auditHelper != null) {
                    PicketLinkAuditEvent auditEvent = new PicketLinkAuditEvent(AuditLevel.INFO);
                    auditEvent.setWhoIsAuditing((String) requestOptions.get(GeneralConstants.CONTEXT_PATH));
                    auditEvent.setType(PicketLinkAuditEventType.INVALIDATE_HTTP_SESSION);
                    auditEvent.setHttpSessionID(httpSession.getId());
                    auditHelper.audit(auditEvent);
                }
                httpSession.invalidate(); // We are done with the logout interaction
            } else {
                // Put the participant in transit mode
                server.stack().registerTransitParticipant(sessionID, nextParticipant);

                boolean isPost = isPostBindingForResponse(server, nextParticipant, request);
                response.setPostBindingForResponse(isPost);

                // send logout request to participant with relaystate to orig
                response.setRelayState(relayState);

                response.setDestination(nextParticipant);

                SAML2Request saml2Request = new SAML2Request();
                try {
                    LogoutRequestType lort = saml2Request.createLogoutRequest(request.getIssuer().getValue());
                    response.setResultingDocument(saml2Request.convert(lort));
                    response.setSendRequest(true);
                } catch (Exception e) {
                    throw logger.processingError(e);
                }
            }
        }

        public void handleRequestType(SAML2HandlerRequest request, SAML2HandlerResponse response) throws ProcessingException {
           
        	LOGGERSLF4J.debug("handleRequestType:01");
        	
        	HTTPContext httpContext = (HTTPContext) request.getContext();
            HttpServletRequest httpServletRequest = httpContext.getRequest();
            HttpSession session = httpServletRequest.getSession(false);
            String sessionID = session.getId();

            String relayState = httpContext.getRequest().getParameter(GeneralConstants.RELAY_STATE);

            LogoutRequestType logOutRequest = (LogoutRequestType) request.getSAML2Object();
            String issuer = logOutRequest.getIssuer().getValue();
            try {
                SAML2Request saml2Request = new SAML2Request();

                ServletContext servletCtx = httpContext.getServletContext();
                IdentityServer server = (IdentityServer) servletCtx.getAttribute(GeneralConstants.IDENTITY_SERVER);

                if (server == null) {
					throw logger.samlHandlerIdentityServerNotFoundError();
				}

                LOGGERSLF4J.debug("handleRequestType:02:"+issuer);
                LOGGERSLF4J.debug("handleRequestType:03:"+relayState);
                
                String originalIssuer = (relayState == null) ? issuer : relayState;

                LOGGERSLF4J.debug("handleRequestType:04:"+originalIssuer);
                
             
                String[] participant_arr = getParticipantCUD(server, sessionID, originalIssuer);
                String participant = participant_arr[0];
                String logoutUrl = participant_arr[1];
                
                LOGGERSLF4J.debug("handleRequestType:05:"+participant);
                
                String logoutBackUrl = null;
                
                LogoutRequestType lr = (LogoutRequestType) request.getSAML2Object();
                
                URI destination = lr.getDestination();
                
                if(destination!=null){
                	logoutBackUrl=WebUtil.getTokenValue(destination.toString(), "logoutBackUrl");
                	
                	try{
                	 logoutBackUrl=URLDecoder.decode(logoutBackUrl , "UTF-8");
                	}catch(Exception e){
                		 LOGGERSLF4J.error("handleRequestType:decode:error:"+e);
                	}
                }
                
                LOGGERSLF4J.debug("handleRequestType:05_2:"+logoutBackUrl);
                
                 	
                    // All log out is done
                    session.invalidate();
                    server.stack().pop(sessionID);

                    //!!!
                    server.stack().removeSession(sessionID);
                    
                     generateSuccessStatusResponseType(logOutRequest.getID(), request, response, 
                    		logoutBackUrl!=null?logoutBackUrl:logoutUrl);
                    
                    boolean isPost = isPostBindingForResponse(server, "["+participant+"]"+logoutUrl, request);
                    response.setPostBindingForResponse(isPost);

                    response.setSendRequest(false);
                
            } catch (ParserConfigurationException pe) {
                throw logger.processingError(pe);
            } catch (ConfigurationException pe) {
                throw logger.processingError(pe);
            } 

            return;
        }

        private void generateSuccessStatusResponseType(String logOutRequestID, SAML2HandlerRequest request,
                                                       SAML2HandlerResponse response, String originalIssuer) throws ConfigurationException,
                ParserConfigurationException, ProcessingException {

            logger.trace("Generating Success Status Response for " + originalIssuer);

            StatusResponseType statusResponse = new StatusResponseType(IDGenerator.create("ID_"), XMLTimeUtil.getIssueInstant());

            // Status
            StatusType statusType = new StatusType();
            StatusCodeType statusCodeType = new StatusCodeType();
            statusCodeType.setValue(URI.create(JBossSAMLURIConstants.STATUS_SUCCESS.get()));
            statusType.setStatusCode(statusCodeType);

            statusResponse.setStatus(statusType);

            statusResponse.setInResponseTo(logOutRequestID);

            statusResponse.setIssuer(request.getIssuer());

            //!!!
            statusResponse.setDestination(originalIssuer);
            
            try {
                SAML2Response saml2Response = new SAML2Response();
                response.setResultingDocument(saml2Response.convert(statusResponse));
            } catch (ParsingException je) {
                throw logger.processingError(je);
            }

             
            response.setDestination(originalIssuer);
        }

        private String getParticipant(IdentityServer server, String sessionID, String originalRequestor) {
            int participants = server.stack().getParticipants(sessionID);

            String participant = originalRequestor;
            // Get a participant who is not equal to the original issuer of the logout request
            if (participants > 0) {
                do {
                    participant = server.stack().pop(sessionID);
                    --participants;
                    LOGGERSLF4J.debug("getParticipant:01:"+participant);
                } while (participants > 0 && participant.equals(originalRequestor));
            }

            return participant;
        }

        private String[] getParticipantCUD(IdentityServer server, String sessionID, String originalRequestor) {
            int participants = server.stack().getParticipants(sessionID);

            String participant = originalRequestor;
            
            String result[]=new String[2];
            
            // Get a participant who is not equal to the original issuer of the logout request
            if (participants > 0) {
                do {
                    participant = server.stack().pop(sessionID);
                    --participants;
                    LOGGERSLF4J.debug("getParticipant:01:"+participant);
              
                    result[0]=participant.split("]")[0].substring(1);
                    result[1]=participant.split("]")[1];
                    
                    LOGGERSLF4J.debug("getParticipant:02:"+result[0]);
					LOGGERSLF4J.debug("getParticipant:03:"+result[1]);
                    
                } while (participants > 0 && !result[0].equals(originalRequestor));
            }

         
            return result;
        }
        
        private boolean isPostBindingForResponse(IdentityServer server, String participant, SAML2HandlerRequest request) {
            Boolean isPostParticipant = server.stack().getBinding(participant);
            if (isPostParticipant == null) {
				isPostParticipant = Boolean.TRUE;
			}

            Boolean isStrictPostBindingForResponse = (Boolean) request.getOptions().get(
                    GeneralConstants.SAML_IDP_STRICT_POST_BINDING);
            if (isStrictPostBindingForResponse == null) {
				isStrictPostBindingForResponse = Boolean.FALSE;
			}

            return isPostParticipant || isStrictPostBindingForResponse;
        }
    }

    private class SPLogOutHandler {
        public void generateSAMLRequest(SAML2HandlerRequest request, SAML2HandlerResponse response) throws ProcessingException {
            // Generate the LogOut Request
            SAML2Request samlRequest = new SAML2Request();

            HTTPContext httpContext = (HTTPContext) request.getContext();
            HttpServletRequest httpRequest = httpContext.getRequest();
            Principal userPrincipal = (Principal) httpRequest.getSession().getAttribute(GeneralConstants.PRINCIPAL_ID);

            if (userPrincipal == null) {
                userPrincipal = httpRequest.getUserPrincipal();
            }

            if (userPrincipal == null) {
                throw logger.samlHandlerPrincipalNotFoundError();
            }

            try {
                LogoutRequestType lot = samlRequest.createLogoutRequest(request.getIssuer().getValue());

                NameIDType nameID = new NameIDType();
                nameID.setValue(userPrincipal.getName());
                lot.setNameID(nameID);

                SPType spConfiguration = (SPType) getProviderconfig();
                String logoutUrl = spConfiguration.getLogoutUrl();

                if (logoutUrl == null) {
                    logoutUrl = spConfiguration.getIdentityURL();
                }

                lot.setDestination(URI.create(logoutUrl));

                populateSessionIndex(httpRequest, lot);

                response.setResultingDocument(samlRequest.convert(lot));
                response.setSendRequest(true);
            } catch (Exception e) {
                throw logger.processingError(e);
            }
        }

        private void populateSessionIndex(HttpServletRequest httpRequest, LogoutRequestType lot) throws ProcessingException,
                ConfigurationException, ParsingException {
            Document currentAssertion = (Document) httpRequest.getSession().getAttribute(GeneralConstants.ASSERTION_SESSION_ATTRIBUTE_NAME);

            if (currentAssertion != null) {
                AssertionType assertionType = SAMLUtil.fromElement(currentAssertion.getDocumentElement());

                Set<StatementAbstractType> statements = assertionType.getStatements();

                for (StatementAbstractType statementAbstractType : statements) {
                    if (AuthnStatementType.class.isInstance(statementAbstractType)) {
                        AuthnStatementType authnStatement = (AuthnStatementType) statementAbstractType;

                        String sessionIndex = authnStatement.getSessionIndex();

                        if (sessionIndex != null) {
                            lot.addSessionIndex(sessionIndex);
                        }

                        break;
                    }
                }
            }
        }

        public void handleStatusResponseType(SAML2HandlerRequest request, SAML2HandlerResponse response)
                throws ProcessingException {
            // Handler a log out response from IDP
            StatusResponseType statusResponseType = (StatusResponseType) request.getSAML2Object();

            HTTPContext httpContext = (HTTPContext) request.getContext();
            HttpServletRequest servletRequest = httpContext.getRequest();
            HttpSession session = servletRequest.getSession(false);

            // TODO: Deal with partial logout report

            StatusType statusType = statusResponseType.getStatus();
            StatusCodeType statusCode = statusType.getStatusCode();
            URI statusCodeValueURI = statusCode.getValue();
           
            if(statusCodeValueURI != null){
                String statusCodeValue = statusCodeValueURI.toString();
                if(JBossSAMLURIConstants.STATUS_SUCCESS.get().equals(statusCodeValue)){
                   
                    session.invalidate();
                }
            }
        }

        public void handleRequestType(SAML2HandlerRequest request, SAML2HandlerResponse response) throws ProcessingException {
            SAML2Object samlObject = request.getSAML2Object();
            if (samlObject instanceof LogoutRequestType == false) {
				return;
			}
            //get the configuration to handle a logout request from idp and set the correct response location
            SPType spConfiguration = (SPType) getProviderconfig();

            LogoutRequestType logOutRequest = (LogoutRequestType) samlObject;
            HTTPContext httpContext = (HTTPContext) request.getContext();
            HttpServletRequest servletRequest = httpContext.getRequest();
            HttpSession session = servletRequest.getSession(false);

            String relayState = servletRequest.getParameter("RelayState");

            session.invalidate(); // Invalidate the current session at the SP

            // Generate a Logout Response
            StatusResponseType statusResponse = null;
            try {
                statusResponse = new StatusResponseType(IDGenerator.create("ID_"), XMLTimeUtil.getIssueInstant());
            } catch (ConfigurationException e) {
                throw logger.processingError(e);
            }

            // Status
            StatusType statusType = new StatusType();
            StatusCodeType statusCodeType = new StatusCodeType();
            statusCodeType.setValue(URI.create(JBossSAMLURIConstants.STATUS_SUCCESS.get()));
            statusType.setStatusCode(statusCodeType);

            statusResponse.setStatus(statusType);

            statusResponse.setInResponseTo(logOutRequest.getID());

            statusResponse.setIssuer(request.getIssuer());

            String logoutResponseLocation = spConfiguration.getLogoutResponseLocation();
            if(logoutResponseLocation == null) {
                response.setDestination(logOutRequest.getIssuer().getValue());
            } else {
                response.setDestination(logoutResponseLocation);
            }

            statusResponse.setDestination(response.getDestination());

            SAML2Response saml2Response = new SAML2Response();
            try {
                response.setResultingDocument(saml2Response.convert(statusResponse));
            } catch (Exception je) {
                throw logger.processingError(je);
            }

            response.setRelayState(relayState);
            response.setSendRequest(false);
        }
    }
}