package org.cip4.schema.xjdf

import javax.xml.XMLConstants
import javax.xml.namespace.NamespaceContext

class NamespaceContextImpl implements NamespaceContext {

    String getNamespaceURI(String prefix) {
        return prefix == "xs" ? XMLConstants.W3C_XML_SCHEMA_NS_URI : null;
    }

    Iterator getPrefixes(String val) {
        return null;
    }

    String getPrefix(String uri) {
        return null;
    }

}
