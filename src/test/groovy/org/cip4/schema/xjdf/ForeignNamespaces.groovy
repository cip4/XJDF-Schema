package org.cip4.schema.xjdf

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import static org.junit.jupiter.api.Assertions.*;

class ForeignNamespaces {

    private XsdReader xsdReader;

    @BeforeEach
    void setUp() throws Exception {
        xsdReader = new XsdReader();
    }

    @Test
    void allowAttributesFromForeignNamespaceInAllElements() throws Exception {
        List<Node> complexTypes = xsdReader.evaluateNodeList("//xs:complexType");

        for (Node complexType : complexTypes) {
            Collection<Node> anyAttributeNodes = xsdReader.typeProperties(complexType, "anyAttribute");
            Assertions.assertEquals(
                1,
                anyAttributeNodes.size(),
                String.format(
                    "Complex type '%s' must contain exactly one xs:anyAttribute.",
                    xsdReader.localNameOfComplexType(complexType)
                )
            );
            Node anyAttributeNode = anyAttributeNodes.iterator().next();
            assertEquals(
                "##other",
                anyAttributeNode.getAttributes().getNamedItem("namespace").getNodeValue()
            );
            assertEquals(
                "lax",
                anyAttributeNode.getAttributes().getNamedItem("processContents").getNodeValue()
            );
        }
    }
}
