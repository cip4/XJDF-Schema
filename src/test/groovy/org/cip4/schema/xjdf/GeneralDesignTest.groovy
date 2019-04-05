package org.cip4.schema.xjdf

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

class GeneralDesignTest {

    private XsdReader xsdReader;

    @BeforeEach
    void setUp() throws Exception {
        xsdReader = new XsdReader();
    }


    @Test
    void allFieldsOfTypeIDMustBeNamedID() throws Exception {
        List<Node> elements = xsdReader.evaluateNodeList("//xs:attribute[@type='ID']");

        for (Node elementNode : elements) {
            Assertions.assertEquals(
                "ID",
                elementNode.getAttributes().getNamedItem("name").getNodeValue(),
                "All attributes in XJDF with a data type of ID SHALL be named ID."
            );
        }
    }

    @Test
    void allFieldsOfWithNameExternalIDShouldHaveTypeNMTOKEN() throws Exception {
        List<Node> elements = xsdReader.evaluateNodeList("//xs:attribute[@name='ExternalID']");

        for (Node elementNode : elements) {
            Assertions.assertEquals(
                "xs:NMTOKEN",
                elementNode.getAttributes().getNamedItem("type").getNodeValue(),
                "All attributes in XJDF with a name 'ExternalID' SHALL be have type NMTOKEN."
            );
        }
    }

    @Test
    void allFieldsOfWithNameCostCenterIDShouldHaveTypeNMTOKEN() throws Exception {
        List<Node> elements = xsdReader.evaluateNodeList("//xs:attribute[@name='CostCenterID']");

        for (Node elementNode : elements) {
            Assertions.assertEquals(
                "xs:NMTOKEN",
                elementNode.getAttributes().getNamedItem("type").getNodeValue(),
                "All attributes in XJDF with a name 'CostCenterID' SHALL be have type NMTOKEN."
            );
        }
    }

    @Test
    void allFieldsOfWithNameDeviceIDShouldHaveTypeNMTOKEN() throws Exception {
        List<Node> elements = xsdReader.evaluateNodeList("//xs:attribute[not(@type) and not(xs:simpleType)]");
        Assertions.assertEquals(Collections.EMPTY_LIST, elements);
    }

    @Test
    void allAttributesMustHaveAType() throws Exception {
        List<Node> elements = xsdReader.evaluateNodeList("//xs:attribute[not(@type) and not(xs:simpleType)]");

        Assertions.assertEquals(
            Collections.EMPTY_LIST,
            elements,
            "All attributes in XJDF SHALL have a @type."
        );
    }

    @Test
    void allAttributesMustHaveAUse() throws Exception {
        List<Node> elements = xsdReader.evaluateNodeList("//xs:attribute[not(@use)]");

        Assertions.assertEquals(
            Collections.EMPTY_LIST,
            elements,
            "All attributes in XJDF SHALL have a @use."
        );
    }

}
