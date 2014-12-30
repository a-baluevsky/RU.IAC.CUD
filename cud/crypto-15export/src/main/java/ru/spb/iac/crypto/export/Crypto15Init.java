package ru.spb.iac.crypto.export;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

 public class Crypto15Init {

	private static Log log = LogFactory.getLog(Crypto15Init.class);

	public static void init() {
		try {

			 

			System.setProperty("org.apache.xml.security.resource.config",
					"resource/jcp.xml");

			if(!org.apache.xml.security.Init.isInitialized()){
			  org.apache.xml.security.Init.init();
			}
			 

		} catch (Exception e) {
			log.error("Crypto15Init:init:error:", e);
		}
	}

	public static void fileInit() {

		 
		try {
			InputStream is = Crypto15Init.class.getResourceAsStream("/jcp.xml");

			if (is == null) {
				 
				return;
			}

			 

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature(
					"http://javax.xml.XMLConstants/feature/secure-processing",
					Boolean.TRUE.booleanValue());

			dbf.setNamespaceAware(true);
			dbf.setValidating(false);

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			Node config = doc.getFirstChild();
			while ((config != null)
					&& (!"Configuration".equals(config.getLocalName()))) {
				config = config.getNextSibling();
			}

			if (config == null) {
				log.error("Error in reading configuration file - Configuration element not found");
				return;
			}
			for (Node el = config.getFirstChild(); el != null; el = el
					.getNextSibling()) {
				if ((el == null) || (1 != el.getNodeType())) {
					continue;
				}
				String tag = el.getLocalName();
				if ("ResourceBundles".equals(tag)) {
					Element resource = (Element) el;

					Attr langAttr = resource
							.getAttributeNode("defaultLanguageCode");
					Attr countryAttr = resource
							.getAttributeNode("defaultCountryCode");
					String languageCode = langAttr == null ? null : langAttr
							.getNodeValue();

					String countryCode = countryAttr == null ? null
							: countryAttr.getNodeValue();

					I18n.init(languageCode, countryCode);
				}

				if ("CanonicalizationMethods".equals(tag)) {
					Element[] list = XMLUtils.selectNodes(el.getFirstChild(),
							"http://www.xmlsecurity.org/NS/#configuration",
							"CanonicalizationMethod");

					for (int i = 0; i < list.length; i++) {
						String URI = list[i].getAttributeNS(null, "URI");
						String JAVACLASS = list[i].getAttributeNS(null,
								"JAVACLASS");
						try {
							Canonicalizer.register(URI, JAVACLASS);
							if (log.isDebugEnabled()) {
								log.debug("Canonicalizer.register(" + URI
										+ ", " + JAVACLASS + ")");
							}
						} catch (ClassNotFoundException e) {
							Object[] exArgs = { URI, JAVACLASS };
							log.error(I18n.translate(
									"algorithm.classDoesNotExist", exArgs));
						} catch (Exception e1) {
							// здесь будут ощибки, что алгоритм уже
							// зарегистрирован
							// это нормально, поэтому чтобы не увеличивать лог
							// закомментируем
							 
						}
					}
				}

				if ("TransformAlgorithms".equals(tag)) {
					Element[] tranElem = XMLUtils.selectNodes(
							el.getFirstChild(),
							"http://www.xmlsecurity.org/NS/#configuration",
							"TransformAlgorithm");

					for (int i = 0; i < tranElem.length; i++) {
						String URI = tranElem[i].getAttributeNS(null, "URI");
						String JAVACLASS = tranElem[i].getAttributeNS(null,
								"JAVACLASS");
						try {
							Transform.register(URI, JAVACLASS);
							if (log.isDebugEnabled()) {
								log.debug("Transform.register(" + URI + ", "
										+ JAVACLASS + ")");
							}
						} catch (ClassNotFoundException e) {
							Object[] exArgs = { URI, JAVACLASS };

							log.error(I18n.translate(
									"algorithm.classDoesNotExist", exArgs));
						} catch (NoClassDefFoundError ex) {
							log.warn("Not able to found dependencies for algorithm, I'll keep working.");
						} catch (Exception e1) {
							// здесь будут ощибки, что алгоритм уже
							// зарегистрирован
							// это нормально, поэтому чтобы не увеличивать лог
							// закомментируем
							 
						}
					}
				}

				 
				if ("JCEAlgorithmMappings".equals(tag)) {

					 

					Node algorithmsNode = ((Element) el).getElementsByTagName(
							"Algorithms").item(0);
					if (algorithmsNode != null) {

						 

						Element[] algorithms = XMLUtils.selectNodes(
								algorithmsNode.getFirstChild(),
								"http://www.xmlsecurity.org/NS/#configuration",
								"Algorithm");

						for (int i = 0; i < algorithms.length; i++) {

							Element element = algorithms[i];
							String id = element.getAttribute("URI");

							 
							 

							JCEMapper.register(id, new JCEMapper.Algorithm(
									element));

							 

						}
					}
				}

				if ("SignatureAlgorithms".equals(tag)) {
					Element[] sigElems = XMLUtils.selectNodes(
							el.getFirstChild(),
							"http://www.xmlsecurity.org/NS/#configuration",
							"SignatureAlgorithm");

					for (int i = 0; i < sigElems.length; i++) {
						String URI = sigElems[i].getAttributeNS(null, "URI");
						String JAVACLASS = sigElems[i].getAttributeNS(null,
								"JAVACLASS");
						try {
							SignatureAlgorithm.register(URI, JAVACLASS);
							if (log.isDebugEnabled()) {
								log.debug("SignatureAlgorithm.register(" + URI
										+ ", " + JAVACLASS + ")");
							}
						} catch (ClassNotFoundException e) {
							Object[] exArgs = { URI, JAVACLASS };

							log.error(I18n.translate(
									"algorithm.classDoesNotExist", exArgs));
						} catch (Exception e1) {
							// здесь будут ощибки, что алгоритм уже
							// зарегистрирован
							// это нормально, поэтому чтобы не увеличивать лог
							// закомментируем
							 
						}
					}
				}

				if ("ResourceResolvers".equals(tag)) {
					Element[] resolverElem = XMLUtils.selectNodes(
							el.getFirstChild(),
							"http://www.xmlsecurity.org/NS/#configuration",
							"Resolver");

					for (int i = 0; i < resolverElem.length; i++) {
						String JAVACLASS = resolverElem[i].getAttributeNS(null,
								"JAVACLASS");

						String Description = resolverElem[i].getAttributeNS(
								null, "DESCRIPTION");

						if ((Description != null) && (Description.length() > 0)) {
							if (log.isDebugEnabled()) {
								log.debug("Register Resolver: " + JAVACLASS
										+ ": " + Description);
							}

						} else if (log.isDebugEnabled()) {
							log.debug("Register Resolver: " + JAVACLASS
									+ ": For unknown purposes");
						}

						try {
							ResourceResolver.register(JAVACLASS);
						} catch (Exception e) {
							log.warn(
									"Cannot register:"
											+ JAVACLASS
											+ " perhaps some needed jars are not installed",
									e);
						}

					}

				}

				if ("KeyResolver".equals(tag)) {
					Element[] resolverElem = XMLUtils.selectNodes(
							el.getFirstChild(),
							"http://www.xmlsecurity.org/NS/#configuration",
							"Resolver");

					List classNames = new ArrayList(resolverElem.length);
					for (int i = 0; i < resolverElem.length; i++) {
						String JAVACLASS = resolverElem[i].getAttributeNS(null,
								"JAVACLASS");

						String Description = resolverElem[i].getAttributeNS(
								null, "DESCRIPTION");

						if ((Description != null) && (Description.length() > 0)) {
							if (log.isDebugEnabled()) {
								log.debug("Register Resolver: " + JAVACLASS
										+ ": " + Description);
							}

						} else if (log.isDebugEnabled()) {
							log.debug("Register Resolver: " + JAVACLASS
									+ ": For unknown purposes");
						}

						classNames.add(JAVACLASS);
					}
					KeyResolver.registerClassNames(classNames);
				}

				if ("PrefixMappings".equals(tag)) {
					if (log.isDebugEnabled()) {
						log.debug("Now I try to bind prefixes:");
					}

					Element[] nl = XMLUtils.selectNodes(el.getFirstChild(),
							"http://www.xmlsecurity.org/NS/#configuration",
							"PrefixMapping");

					for (int i = 0; i < nl.length; i++) {
						String namespace = nl[i].getAttributeNS(null,
								"namespace");
						String prefix = nl[i].getAttributeNS(null, "prefix");
						if (log.isDebugEnabled()) {
							log.debug("Now I try to bind " + prefix + " to "
									+ namespace);
						}
						ElementProxy.setDefaultPrefix(namespace, prefix);
					}
				}
			}
		} catch (Exception e) {
			log.error("Crypto15Init:fileInit:error: ", e);
		}
	}

}
