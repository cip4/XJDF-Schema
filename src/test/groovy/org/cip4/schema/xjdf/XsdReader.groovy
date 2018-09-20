package org.cip4.schema.xjdf;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory
import java.nio.file.Files
import java.nio.file.Paths;

class XsdReader {

    private Document schema;
    private XPath xPath;

    XsdReader() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        Files.newInputStream(Paths.get("xjdf.xsd")).withStream { inputStream ->
            schema = builder.parse(new InputSource(inputStream));
        }

        NamespaceContext ctx = new NamespaceContext() {
            String getNamespaceURI(String prefix) {
                return prefix == "xs" ? XMLConstants.W3C_XML_SCHEMA_NS_URI : null;
            }

            Iterator getPrefixes(String val) {
                return null;
            }

            String getPrefix(String uri) {
                return null;
            }
        };

        XPathFactory xPathFactory = XPathFactory.newInstance();
        xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(ctx);
    }

    Set<Node> complexTypeProperties(final String complexType) throws Exception {
        return typeProperties(
                (Node) xPath.evaluate(
                        String.format(
                                "//xs:complexType[@name='%s'] | //xs:complexType[../@name='%s']",
                                complexType,
                                complexType
                        ),
                        schema,
                        XPathConstants.NODE
                )
        );
    }

    Set<Node> typeProperties(Node complexType) {
        Set<Node> properties = new HashSet<>();
        properties.addAll(typeProperties(complexType, "element"));
        properties.addAll(typeProperties(complexType, "attribute"));
        return properties;
    }

    Collection<Node> typeProperties(Node complexType, String propertyType) {
        try {
            NodeList nodeList = (NodeList) xPath.evaluate(
                    String.format(".//xs:%s", propertyType),
                    complexType,
                    XPathConstants.NODESET
            );
            Set<Node> nodes = new HashSet<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); ++i) {
                nodes.add(nodeList.item(i));
            }

            String parentType = xPath.evaluate(".//xs:extension/@base", complexType);
            if (!"".equals(parentType) && !"string".equals(parentType)) {
                Node parentComplexTypeNode = (Node) xPath.evaluate(
                        String.format("//xs:complexType[@name='%s']", parentType),
                        schema,
                        XPathConstants.NODE
                );
                nodes.addAll(typeProperties(parentComplexTypeNode, propertyType));
            }
            return nodes;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    List<Node> evaluateNodeList(final String xPathExpression) throws XPathExpressionException {
        return nodeListToList((NodeList) xPath.evaluate(
                xPathExpression,
                schema,
                XPathConstants.NODESET
        ));
    }

    NodeList evaluateNodeList(final String xPathExpression, final Node parentNode)
            throws XPathExpressionException {
        return (NodeList) xPath.evaluate(
                xPathExpression,
                parentNode,
                XPathConstants.NODESET
        );
    }

    String localNameOfComplexType(final Node complexTypeNode) {
        try {
            return xPath.evaluate(
                    "@name | ../@name",
                    complexTypeNode
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Node> nodeListToList(NodeList nodeList) {
        List<Node> result = new ArrayList<>(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); ++i) {
            result.add(nodeList.item(i));
        }
        return result;
    }
}
