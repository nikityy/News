package com.nikitagusarov.news;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by mac on 17/12/2016.
 */
public class RSSFeedParser {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";
    static final String IMAGE = "thumbnail";

    final private Feed feed;
    final private URL url;

    public RSSFeedParser(Feed feed) {
            this.feed = feed;
            this.url = feed.url;
    }

    public Feed readFeed() {
        feed.getMessages().clear();

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            RSSHandler rssHandler = new RSSHandler();
            xmlReader.setContentHandler(rssHandler);
            InputSource inputSource = new InputSource(url.openStream());
            xmlReader.parse(inputSource);
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return feed;
    }

    private class RSSHandler extends DefaultHandler {

        boolean isTitle = false;
        boolean isDescription = false;

        String title;
        String description;
        String imageURL;

        public void startElement(String uri, String localName, String qName,
                                 Attributes attrs) throws SAXException {
            if (localName.equals(ITEM)) {
                isTitle = false;
                isDescription = false;
            }

            if (localName.equals(TITLE)) {
                isTitle = true;
            }

            if (localName.equals(DESCRIPTION)) {
                isDescription = true;
            }

            if (localName.equals(IMAGE)) {
                imageURL = attrs.getValue("url");
            }
        }

        public void endElement(String namespaceURI, String localName,
                               String qName) throws SAXException {
            if (localName.equals(ITEM)) {
                feed.addItem(title, description, imageURL);
                title = null;
                description = null;
                imageURL = null;
            }
        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (isTitle) {
                title = new String(ch, start, length);
                isTitle = false;
            }

            if (isDescription) {
                description = new String(ch, start, length);
                isDescription = false;
            }
        }

    }

}
