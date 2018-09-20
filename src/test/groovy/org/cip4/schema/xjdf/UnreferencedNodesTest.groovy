package org.cip4.schema.xjdf

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory
import java.nio.file.Files
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UnreferencedNodesTest {

    private static Document XJDF_SCHEMA;
    private static XPath xPath;

    @BeforeEach
    void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        Files.newInputStream(Paths.get('xjdf.xsd')).withStream { inputStream ->
            XJDF_SCHEMA = builder.parse(new InputSource(inputStream))
        }

        XPathFactory xPathFactory = XPathFactory.newInstance();
        xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new NamespaceContextImpl());
    }

    @Test
    void unusedElements() throws Exception {
        NodeList elements = (NodeList) xPath.evaluate(
            "/xs:schema/xs:element[not(@substitutionGroup='ResourceType') "
                + "and not(@substitutionGroup='Message') "
                + "and not(@substitutionGroup='ProductIntent')]",
            XJDF_SCHEMA,
            XPathConstants.NODESET
        );

        for (int i = 0; i < elements.getLength(); i++) {
            Node elementNode = elements.item(i);
            String elementName = elementNode.getAttributes().getNamedItem("name").getNodeValue();
            assertTrue(
                isElementReferenced(elementNode),
                String.format("Element '%s' is defined but not referenced from another element.", elementName)
            );
        }
    }

    private boolean isElementReferenced(final Node elementNode) throws Exception {
        String elementName = elementNode.getAttributes().getNamedItem("name").getNodeValue();

        if (elementName.equals("XJDF") || elementName.equals("XJMF")) {
            return true;
        }

        NodeList matches = (NodeList) xPath.evaluate(
            String.format(
                "//xs:element[@ref='%s' or @substitutionGroup='%s']",
                elementName,
                elementName
            ), XJDF_SCHEMA, XPathConstants.NODESET
        );

        if (matches.getLength() > 0) {
            return true;
        }

        Node substitutionGroupNode = elementNode.getAttributes().getNamedItem("substitutionGroup");
        if (null != substitutionGroupNode) {
            NodeList parentNodes = (NodeList) xPath.evaluate(
                String.format("/xs:schema/xs:element[@name='%s']", substitutionGroupNode.getNodeValue()),
                XJDF_SCHEMA,
                XPathConstants.NODESET
            );
            for (int i = 0; i < parentNodes.getLength(); ++i) {
                if (isElementReferenced(parentNodes.item(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Test
    void unusedSimpleTypes() throws Exception {
        NodeList elements = (NodeList) xPath.evaluate(
            "/xs:schema/xs:simpleType",
            XJDF_SCHEMA,
            XPathConstants.NODESET
        );

        for (int i = 0; i < elements.getLength(); i++) {
            Node elementNode = elements.item(i);
            String elementName = elementNode.getAttributes().getNamedItem("name").getNodeValue();
            NodeList attributeUsages = (NodeList) xPath.evaluate(
                String.format(
                    "//xs:attribute[@type='%s' or .//xs:list/@itemType='%s']",
                    elementName,
                    elementName
                ), XJDF_SCHEMA, XPathConstants.NODESET
            );
            NodeList elementUsages = (NodeList) xPath.evaluate(
                String.format(
                    "//xs:element[@type='%s']",
                    elementName
                ), XJDF_SCHEMA, XPathConstants.NODESET
            );
            assertTrue(
                (attributeUsages.getLength() > 0) || (elementUsages.getLength() > 0),
                String.format("SimpleType '%s' is defined but not referenced from an element.", elementName)
            );
        }
    }

    @Test
    void unusedComplexTypes() throws Exception {
        NodeList elements = (NodeList) xPath.evaluate(
            "/xs:schema/xs:complexType[@Name!='SetType']",
            XJDF_SCHEMA,
            XPathConstants.NODESET
        );

        for (int i = 0; i < elements.getLength(); i++) {
            Node elementNode = elements.item(i);
            String elementName = elementNode.getAttributes().getNamedItem("name").getNodeValue();
            NodeList elementUsages = (NodeList) xPath.evaluate(
                String.format(
                    "//xs:element[@type='%s']",
                    elementName
                ), XJDF_SCHEMA, XPathConstants.NODESET
            );
            assertTrue(
                elementUsages.getLength() > 0,
                String.format("SimpleType '%s' is defined but not referenced from an element.", elementName)
            );
        }
    }

}
