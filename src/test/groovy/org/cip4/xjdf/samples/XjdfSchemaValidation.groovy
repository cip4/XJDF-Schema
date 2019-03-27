package org.cip4.xjdf.samples

import org.cip4.lib.xjdf.xml.XJdfValidator
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import groovy.io.FileType

import javax.xml.XMLConstants
import javax.xml.bind.ValidationException
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

class XjdfSchemaValidation {

    private static Locale defaultLocale
    private static File xsdUri = new File("./xjdf.xsd");
    private static Validator validator

    static Collection<Arguments> data() {
        List<Arguments> files = new ArrayList<>();
        new File(XjdfSchemaValidation.class.getResource("/samples").getFile()).eachFileRecurse(FileType.FILES) { File file ->
            if (file.name.endsWith('.xjdf') && !file.absolutePath.contains('further')) {
                files.add(Arguments.of(file))
            }
        }
        return files;
    }

    @BeforeAll
    public static void setUp() throws Exception {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US)

        xsdUri.withInputStream { xsd ->
            validator = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new StreamSource(xsd))
                    .newValidator()
        }
    }

    @AfterAll
    public static void tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testInvalidPackages(File xjdfFile) {
        try {
            xjdfFile.withInputStream { xml ->
                validator.validate(new StreamSource(xml))
            }
        } catch (ValidationException e) {
            throw new AssertionError(
                    String.format(
                            "Sample %s is invalid:\n%s",
                            xjdfFile.toString(),
                            e.getMessage()
                    ),
                    e
            );
        }
    }
}

