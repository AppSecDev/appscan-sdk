/**
 * © Copyright IBM Corporation 2016.
 * © Copyright HCL Technologies Ltd. 2017. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.scanners.sast.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hcl.appscan.sdk.Messages;

/**
 * Helper class for constructing and writing a DOM document.
 */
public final class DOMWriter {

	private File m_file;
	private DocumentBuilder m_builder;
	private Document m_doc;
	private OutputStream m_stream;
	private Element m_current = null;

	/**
	 * Constructor.
	 * 
	 * @param file The file to write.
	 * @param builder A document builder.
	 * @param append If file exist, setting this to true appends to the file.
	 * @throws IOException If the file already exists, append is false, and the file could not be deleted.
	 * Or if file already exists, append is true, but there is an error parsing the existing file.
	 */
	public DOMWriter(File file, DocumentBuilder builder, boolean append) throws IOException {
		m_file = file;
		m_builder = builder;

		if (m_file.exists()) {

			if (append) {
				try {
					m_doc = builder.parse(file);
					m_current = m_doc.getDocumentElement();
					return;
				}
				catch (SAXException e) {
					throw new IOException(e);
				}
			}

			if (!m_file.delete())
				throw new IOException(Messages.getMessage("error.delete", m_file)); //$NON-NLS-1$
		}
		else
			m_file.getParentFile().mkdirs();

		m_doc = builder.newDocument();
	}

	/**
	 * Constructor.
	 * 
	 * @param file The file to write.
	 * @param builder A document builder.
	 * @throws IOException If the file already exists and could not be deleted.
	 */
	public DOMWriter(File file, DocumentBuilder builder) throws IOException {
		this(file, builder, false);
	}

	/**
	 * Constructor.
	 * 
	 * @param directory Directory to contain the written file.
	 * @param file The name of the file to write.
	 * @param builder A document builder.
	 * @throws IOException If the file already exists and could not be deleted.
	 */
	public DOMWriter(File directory, String file, DocumentBuilder builder) throws IOException {
		this(new File(directory, file), builder, false);
	}

	/**
	 * Constructor.
	 * @param stream The OutputStream to write to.
	 * @param builder A document builder.
	 */
	public DOMWriter(OutputStream stream,DocumentBuilder builder) {
		m_stream=stream;
		m_builder = builder;
		m_doc = builder.newDocument();
	}

	/**
	 * Create an attribute for the current element.
	 * 
	 * @param name The attribute name.
	 * @param value The attribute value.
	 */
	//@SuppressSecurityTrace
	public void setAttribute(String name, String value) {
		try {
			m_current.setAttribute(name, value);
		}
		catch (DOMException e) {
			if (e.code != DOMException.INVALID_CHARACTER_ERR)
				throw e;
		}
	}

	/**
	 * Append a document fragment to the current element.
	 * 
	 * @param fragment The document fragment to append.
	 */
	public void appendFragment(DocumentFragment fragment) {
		if (fragment != null) {
			Node newFragment = m_doc.importNode(fragment, true);
			m_current.appendChild(newFragment);
		}
	}

	/**
	 * Append the XML string as a document fragment to the current element.
	 * 
	 * @param xml The XML string.
	 * @throws IOException If any IO errors occur.
	 * @throws SAXException If any parse errors occur.
	 */
	public void appendFragment(String xml) throws IOException, SAXException {
		if (xml != null) {
			Document document = m_builder.parse(new InputSource(new StringReader(xml)));
			DocumentFragment fragment = document.createDocumentFragment();
			fragment.appendChild(document.getDocumentElement());
			appendFragment(fragment);
		}
	}

	/**
	 * Append a DOM node to the current element.
	 * 
	 * @param node The DOM node to append.
	 */
	public void appendNode(Node node) {
		if (node != null) {
			m_current.appendChild(node);
		}
	}

	/**
	 * Begin a new element.
	 * 
	 * @param name The name of the element.
	 */
	public void beginElement(String name) {
		Element child = m_doc.createElement(name);
		if (m_current == null)
			m_doc.appendChild(child);
		else
			m_current.appendChild(child);
		m_current = child;
	}

	/**
	 * Close off the current element.
	 */
	public void endElement() {
		Node parent = m_current.getParentNode();
		if (parent instanceof Element)
			m_current = (Element) parent;
	}

	/**
	 * Write some data and close off the element.  The data is treated as
	 * CData if {@code isCData} is true. An IllegalStateException is thrown 
	 * if the current element has child elements.
	 * 
	 * @param data The text.
	 * @param isCData True if the data is CData.
	 */
	public void endElement(String data, boolean isCData) {
		if (data != null) {
			if (m_current.hasChildNodes())
				throw new IllegalStateException(Messages.getMessage("error.dom.state")); //$NON-NLS-1$
			m_current.appendChild(isCData ? m_doc.createCDATASection(data) : m_doc.createTextNode(data));
		}
		endElement();
	}

	/**
	 * Write some text data and close off the element.  An
	 * IllegalStateException is thrown if the current element
	 * has child elements.
	 * 
	 * @param data The text.
	 */
	public void endElement(String data) {
		endElement(data, false);
	}

	/**
	 * Write the document to disk.
	 * 
	 * @param transformer A transformer.
	 * @throws TransformerException If an error occurs during the write operation.
	 */
	public void write(Transformer transformer) throws TransformerException {
		DOMSource source = new DOMSource(m_doc);
		boolean shouldCloseAfter = false;
		try  {
			StreamResult result;
			if (m_stream==null) {
				m_stream = new FileOutputStream(m_file);
				shouldCloseAfter = true;
			}
			result = new StreamResult(m_stream);
			transformer.transform(source, result);
		} catch (IOException e) {
			throw new TransformerException(e);
		} finally {
			if (m_stream!=null && shouldCloseAfter) {
				try {
					m_stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				m_stream = null;
			}
		}
	}

	/**
	 * Returns the Document object.
	 * 
	 * @return The Document object.
	 */
	public Document getDocument() {
		return m_doc;
	}
}
