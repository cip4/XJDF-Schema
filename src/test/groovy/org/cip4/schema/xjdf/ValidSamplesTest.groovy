package org.cip4.schema.xjdf

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import javax.xml.XMLConstants
import jakarta.xml.bind.ValidationException
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths;

class ValidSamplesTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(
                new StreamSource(
                        Files.newInputStream(Paths.get('xjdf.xsd'))
                )
        );

        validator = schema.newValidator();
    }

    @ParameterizedTest
    @MethodSource("validXjdfs")
    void productGetChildrenRawIsEmptyByDefault(Path xjdfPath) {
        Source xmlSource = new StreamSource(
                Files.newInputStream(
                        xjdfPath
                )
        );

        try {
            validator.validate(xmlSource)
        } catch (ValidationException e) {
            throw new AssertionError(
                    String.format(
                            "Sample %s is invalid:\n%s",
                            xjdfPath,
                            e.getMessage()
                    ),
                    e
            )
        }
    }

    static Iterable<Path> validXjdfs() {
        Files.newDirectoryStream(
                Paths.get(ValidSamplesTest.getResource("valid").toURI()),
                "*.xjdf"
        )
    }
}