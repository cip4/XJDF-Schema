package org.cip4.schema.xjdf

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Node;

import static org.junit.jupiter.api.Assertions.*;

class FieldsOfTypeNmtokenTest {

    private XsdReader xsdReader;

    @BeforeEach
    void setUp() throws Exception {
        xsdReader = new XsdReader();
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "DeviceID", "ExternalID", "JobID", "JobPartID", "ModuleID", "NextQueueEntryID", "PersonalID",
            "PipeID", "PrevQueueEntryID", "ProductionPathID", "ProjectID", "QueueEntryID", "RelatedJobID",
            "RelatedJobPartID", "SenderID", "Unit"
        ])
    void allFieldsOfWithNameJobPartIDShouldHaveTypeNMTOKEN(String fieldName) throws Exception {
        List<Node> elements = xsdReader.evaluateNodeList(String.format("//xs:attribute[@name='%s']", fieldName));

        for (Node elementNode : elements) {
            assertEquals(
                "xs:NMTOKEN",
                elementNode.getAttributes().getNamedItem("type").getNodeValue(),
                String.format("All attributes in XJDF with a name '%s' SHALL be have type NMTOKEN.", fieldName)
            );
        }
    }
}
