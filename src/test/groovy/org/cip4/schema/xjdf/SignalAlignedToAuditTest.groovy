package org.cip4.schema.xjdf

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node

import static org.cip4.schema.xjdf.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignalAlignedToAuditTest {

    private XsdReader xsdReader;
    private final List<String> signalOnlyAttributes = Arrays.asList("ChannelMode");

    @BeforeEach
    void setUp() throws Exception {
        xsdReader = new XsdReader();
    }

    @Test
    void signalNotificationAndAuditNotificationAreAligned() throws Exception {
        Collection<Node> signalProperties = removeSignalOnlyAttributes(
            xsdReader.complexTypeProperties("SignalNotification")
        );
        Collection<Node> auditProperties = xsdReader.complexTypeProperties("AuditNotification");

        assertTrue(signalProperties.size() > 0);
        assertNodeCollectionEquals(signalProperties, auditProperties);
    }

    @Test
    @Disabled("XJDF-1020")
    void signalResourceAndAuditResourceAreAligned() throws Exception {
        Collection<Node> signalResourceProperties = removeSignalOnlyAttributes(
            xsdReader.complexTypeProperties("SignalResource")
        );
        Collection<Node> auditResourceProperties = xsdReader.complexTypeProperties("AuditResource");

        assertTrue(signalResourceProperties.size() > 0);
        assertNodeCollectionEquals(signalResourceProperties, auditResourceProperties);
    }

    @Test
    void signalStatusAndAuditStatusAreAligned() throws Exception {
        Collection<Node> signalResourceProperties = removeSignalOnlyAttributes(
            xsdReader.complexTypeProperties("SignalStatus")
        );
        Collection<Node> auditResourceProperties = xsdReader.complexTypeProperties("AuditStatus");

        assertTrue(signalResourceProperties.size() > 0);
        assertNodeCollectionEquals(signalResourceProperties, auditResourceProperties);
    }

    private Collection<Node> removeSignalOnlyAttributes(Collection<Node> nodes) {
        for (Iterator<Node> iterator = nodes.iterator(); iterator.hasNext(); ) {
            Node node = iterator.next();
            if (node.getLocalName().equals("attribute") &&
                signalOnlyAttributes.contains(node.getAttributes().getNamedItem("name").getNodeValue())) {
                iterator.remove();
            }
        }
        return nodes;
    }

}
