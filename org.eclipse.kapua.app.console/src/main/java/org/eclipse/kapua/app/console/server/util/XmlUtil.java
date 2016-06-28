/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.app.console.server.util;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.sax.SAXSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XmlUtil
{
    @SuppressWarnings("unused")
    private static final Logger            s_logger   = LoggerFactory.getLogger(XmlUtil.class);

    @SuppressWarnings("rawtypes")
    private static Map<Class, JAXBContext> s_contexts = new HashMap<Class, JAXBContext>();

    public static String marshal(Object object)
        throws JAXBException
    {
        StringWriter sw = new StringWriter();
        marshal(object, sw);
        return sw.toString();
    }

    @SuppressWarnings("rawtypes")
    public static void marshal(Object object, Writer w)
        throws JAXBException
    {
        Class clazz = object.getClass();
        JAXBContext context = s_contexts.get(clazz);
        if (context == null) {
            context = JAXBContext.newInstance(clazz);
            s_contexts.put(clazz, context);
        }

        ValidationEventCollector valEventHndlr = new ValidationEventCollector();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setSchema(null);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setEventHandler(valEventHndlr);

        try {
            marshaller.marshal(object, w);
        }
        catch (Exception e) {
            if (e instanceof JAXBException) {
                throw (JAXBException) e;
            }
            else {
                throw new MarshalException(e.getMessage(), e);
            }
        }

        if (valEventHndlr.hasEvents()) {
            for (ValidationEvent valEvent : valEventHndlr.getEvents()) {
                if (valEvent.getSeverity() != ValidationEvent.WARNING) {
                    // throw a new Marshall Exception if there is a parsing error
                    throw new MarshalException(valEvent.getMessage(), valEvent.getLinkedException());
                }
            }
        }
    }

    public static <T> T unmarshal(String s, Class<T> clazz)
        throws JAXBException, XMLStreamException, FactoryConfigurationError, SAXException
    {
        StringReader sr = new StringReader(s);
        return unmarshal(sr, clazz);
    }

    public static <T> T unmarshal(Reader sr, Class<T> clazz)
        throws JAXBException, XMLStreamException, FactoryConfigurationError, SAXException
    {
        return unmarshal(sr, clazz, null);
    }

    /**
     * Unmarshall method which injects the namespace URI provided
     * in all the elements before attempting the parsing.
     */
    public static <T> T unmarshal(String s, Class<T> clazz, String nsUri)
        throws JAXBException, XMLStreamException, FactoryConfigurationError, SAXException
    {
        StringReader sr = new StringReader(s);
        return unmarshal(sr, clazz, nsUri);
    }

    /**
     * Unmarshall method which injects the namespace URI provided
     * in all the elements before attempting the parsing.
     */
    public static <T> T unmarshal(Reader r, Class<T> clazz, String nsUri)
        throws JAXBException, XMLStreamException, FactoryConfigurationError, SAXException
    {
        JAXBContext context = s_contexts.get(clazz);
        if (context == null) {
            context = JAXBContext.newInstance(clazz);
            s_contexts.put(clazz, context);
        }

        ValidationEventCollector valEventHndlr = new ValidationEventCollector();
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(null);
        unmarshaller.setEventHandler(valEventHndlr);

        SAXSource saxSource = null;
        if (nsUri == null) {
            saxSource = new SAXSource(new InputSource(r));
        }
        else {
            boolean addNamespace = true;
            XMLReader reader = XMLReaderFactory.createXMLReader();
            XmlNamespaceFilter filter = new XmlNamespaceFilter(nsUri, addNamespace);
            filter.setParent(reader);
            saxSource = new SAXSource(filter, new InputSource(r));
        }

        JAXBElement<T> elem = null;
        try {
            elem = unmarshaller.unmarshal(saxSource, clazz);
        }
        catch (Exception e) {
            if (e instanceof JAXBException) {
                throw (JAXBException) e;
            }
            else {
                throw new UnmarshalException(e.getMessage(), e);
            }
        }

        if (valEventHndlr.hasEvents()) {
            for (ValidationEvent valEvent : valEventHndlr.getEvents()) {
                if (valEvent.getSeverity() != ValidationEvent.WARNING) {
                    // throw a new Unmarshall Exception if there is a parsing error
                    String msg = MessageFormat.format("Line {0}, Col: {1}: {2}",
                                                      valEvent.getLocator().getLineNumber(),
                                                      valEvent.getLocator().getColumnNumber(),
                                                      valEvent.getLinkedException() != null ? valEvent.getLinkedException().getMessage() : "");
                    throw new UnmarshalException(msg, valEvent.getLinkedException());
                }
            }
        }
        return elem.getValue();
    }

    public static Element findChildElement(Node node, QName qname)
    {
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {

            Node n = nl.item(i);

            boolean isElement = (n instanceof Element);
            boolean matchName = (qname.getLocalPart().equals(n.getLocalName()));
            boolean matchNsURI = qname.getNamespaceURI().isEmpty() ? (n.getNamespaceURI() == null || n.getNamespaceURI().isEmpty()) : qname.getNamespaceURI().equals(n.getNamespaceURI());

            if (isElement && matchName && matchNsURI) {
                return (Element) n;
            }
        }
        return null;
    }
}
