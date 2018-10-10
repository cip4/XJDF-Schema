package org.cip4.schema.xjdf

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Node

import static org.junit.jupiter.api.Assertions.*;

class AbstractTypes {

    private static XsdReader xsdReader;

    @BeforeAll
    static void setUp() throws Exception {
        xsdReader = new XsdReader();
    }

    @ParameterizedTest
    @MethodSource("abstractElements")
    void abstractElementsHaveAbstractComplexType(Node abstractElement) throws Exception {
        String elementName = abstractElement.getAttributes().getNamedItem("name").getTextContent()
        String typeName = abstractElement.getAttributes().getNamedItem("type").getTextContent()
        List<Node> complexTypes = xsdReader.evaluateNodeList('//xs:complexType[@name="' + typeName + '"]')
        assertNotNull(complexTypes)
        assertEquals(1, complexTypes.size())
        Node complexType = complexTypes.get(0)
        Node abstractAttr = complexType.getAttributes().getNamedItem('abstract')
        assertNotNull(abstractAttr, sprintf("Abstract Element '%s' references concrete complexType.", elementName))
        assertEquals("true", abstractAttr.getTextContent())
    }

    static List<Node> abstractElements() {
        xsdReader.evaluateNodeList('//xs:element[@abstract="true"]')
    }

}
