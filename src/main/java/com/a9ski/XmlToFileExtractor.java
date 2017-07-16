package com.a9ski;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlToFileExtractor {
	
	private enum State {
		NONE,
		FILE_CONTENT,
		FILE_NAME,
		MIME_TYPE
	};
	
	
	public XmlToFileExtractor() {
	}
	
	public File exportFile(File xmlFile) throws SAXException, IOException, ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		final StringBuilder fileContentSb = new StringBuilder();
		final StringBuilder fileNameSb = new StringBuilder();
	
		DefaultHandler handler = new DefaultHandler() {
			private State state = State.NONE;
			
			private boolean isTag(String qName, String tag) {
				return (qName.equalsIgnoreCase(tag) || qName.toLowerCase().endsWith(":" + tag));
			}
			
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				super.startElement(uri, localName, qName, attributes);
				if (isTag(qName, "file")) {
					state = State.FILE_CONTENT;
				} else if (isTag(qName, "file_name")) {
					state = State.FILE_NAME;
				}
			}
			
			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				super.endElement(uri, localName, qName);
				if (isTag(qName, "file")) {
					state = State.NONE;
				} else if (isTag(qName, "file_name")) {
					state = State.NONE;
				}
			}
			
			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
	            if (state == State.FILE_CONTENT) {
					appendCharacters(fileContentSb, ch, start, length);
	            } else if (state == State.FILE_NAME) {
	            	appendCharacters(fileNameSb, ch, start, length);
	            }
			}

			private void appendCharacters(final StringBuilder fileContentSb, char[] ch, int start, int length) {
				String content = new String(ch, start, length);
				content = content.replace("\r", "").replace("\n", "");
				fileContentSb.append(content);
			}
		};

		saxParser.parse(xmlFile, handler);
		
		final String fileName = fileNameSb.toString();
		final File exportedFile;
		if (StringUtils.isNotBlank(fileName)) {
			exportedFile = new File(xmlFile.getParentFile(), fileName);
			final byte[] data = Base64.decodeBase64(fileContentSb.toString());
			FileUtils.writeByteArrayToFile(exportedFile, data);
		} else {
			exportedFile = null;
		}
		return exportedFile;
	}
	
	
	
}
