package net.chiappone.util.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kurtis Chiappone
 * @date 10/9/2016
 */
public class XmlUtil {

    private static final Logger logger = LoggerFactory.getLogger( XmlUtil.class );

    /**
     * Returns a Document object from a given XML file.
     *
     * @param xml
     * @return Document
     */
    public static Document getDocumentFromXml( InputStream xml ) {

        Document doc = null;

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse( xml );
            doc.getDocumentElement().normalize();

        } catch ( Exception e ) {

            logger.error( "Error creating document from input stream: ", e );

        }

        return doc;

    }

    /**
     * Returns the first element value if there are multiple elements for this
     * node.
     *
     * @param xml
     * @param parentTag
     * @param elementTag
     * @return String
     */
    public static String getElementValue( InputStream xml, String parentTag, String elementTag ) {

        String toReturn = "";

        try {

            Document doc = getDocumentFromXml( xml );

            // Get all nodes under the parent node

            NodeList nodes = doc.getElementsByTagName( parentTag );

            for ( int i = 0; i < nodes.getLength(); i++ ) {

                Node currentNode = nodes.item( i );

                if ( currentNode.getNodeType() == Node.ELEMENT_NODE ) {

                    Element currentElement = (Element) currentNode;
                    NodeList childNodes = currentElement.getElementsByTagName( elementTag );

                    // Return the first node

                    toReturn = childNodes.item( 0 ).getChildNodes().item( 0 ).getNodeValue();

                }

            }

        } catch ( NullPointerException e ) {

            // A NPE will be thrown if the element tag does not exist. Just
            // return an empty String
            logger.debug( "Element tag doesn't exist: {}", elementTag );

        }

        return toReturn;

    }

    /**
     * This returns a list of all elements for the specified node.
     *
     * @param xml
     * @param parentTag
     * @param elementTag
     * @return List<String>
     */
    public static List<String> getElementValues( InputStream xml, String parentTag, String elementTag ) {

        List<String> toReturn = new ArrayList<String>();

        try {

            Document doc = getDocumentFromXml( xml );

            // Get all nodes under the parent node

            NodeList nodes = doc.getElementsByTagName( parentTag );

            for ( int i = 0; i < nodes.getLength(); i++ ) {

                Node currentNode = nodes.item( i );

                if ( currentNode.getNodeType() == Node.ELEMENT_NODE ) {

                    Element currentElement = (Element) currentNode;
                    NodeList nameNodeList = currentElement.getElementsByTagName( elementTag );

                    for ( int j = 0; j < nameNodeList.getLength(); j++ ) {

                        NodeList names = nameNodeList.item( j ).getChildNodes();
                        toReturn.add( names.item( 0 ).getNodeValue() );

                    }

                }

            }

        } catch ( NullPointerException e ) {

            // A NPE will be thrown if the element tag does not exist. Just
            // return an empty String
            logger.debug( "Element tag doesn't exist: {}", elementTag );

        }

        return toReturn;

    }

    /**
     * Finds the specified tag in the document and replaces the node value with
     * the given text replacement.
     *
     * @param doc
     * @param tag
     * @param replacement
     * @return Document
     */
    public static Document replaceNodeText( Document doc, String tag, String replacement ) {

        if ( doc != null ) {

            NodeList matchingTags = doc.getElementsByTagName( tag );

            for ( int i = 0; i < matchingTags.getLength(); i++ ) {

                Node node = matchingTags.item( i );
                NodeList nodes = node.getChildNodes();
                Text text = (Text) nodes.item( 0 );
                text.setData( replacement );

            }

        }

        return doc;

    }

    /**
     * Converts the given string to an XML Document.
     *
     * @param xml
     * @return Document
     */
    public static Document stringToXml( String xml ) {

        InputStream is = null;
        Document doc = null;

        try {

            is = new ByteArrayInputStream( xml.getBytes( "UTF-8" ) );
            doc = XmlUtil.getDocumentFromXml( is );

        } catch ( UnsupportedEncodingException e ) {

            logger.error( "Unsupported Encoding: ", e );

        } finally {

            try {

                if ( is != null ) {

                    is.close();

                }

            } catch ( Exception e ) {

                logger.debug( "Error closing input stream", e );

            }

        }

        return doc;

    }

    /**
     * Converts the given XML Document into a String.
     *
     * @param doc
     * @return String
     */
    public static String xmlToString( Document doc ) {

        try {

            Source source = new DOMSource( doc );
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult( stringWriter );
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform( source, result );
            return stringWriter.getBuffer().toString();

        } catch ( Exception e ) {

            logger.error( "Error converting XML to String: ", e );

        }

        return null;

    }

}
