package org.cip4.xjdf.samples

import groovy.io.FileType
import org.cip4.lib.xjdf.schema.XJDF
import org.cip4.lib.xjdf.validator.ValidationResult
import org.cip4.lib.xjdf.validator.XjdfValidator
import org.cip4.lib.xjdf.xml.XJdfParser
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class XjdfExtendedValidation {

    private static XjdfValidator validator;
    private static Locale defaultLocale
    private XJdfParser parser = new XJdfParser();

    static Collection<Arguments> data() {
        List<Arguments> files = new ArrayList<>();
        new File(XjdfExtendedValidation.class.getResource("/samples").getFile()).eachFileRecurse(FileType.FILES) { File file ->
            if (file.name.endsWith('.xjdf')&& !file.absolutePath.contains('further')) {
                files.add(Arguments.of(file))
            }
        }
        return files;
    }

    @BeforeAll
    public static void setUp() throws Exception {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US)
        validator = new XjdfValidator();
    }

    @AfterAll
    public static void tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @ParameterizedTest
    @MethodSource('data')
    public void testValidPackages(File xjdfFile) {
        XJDF xjdf
        try {
            xjdf = parser.parseStream(xjdfFile.newInputStream())
        } catch (ClassCastException e) {
            throw new AssertionError(
                    String.format(
                            "Sample %s could not be parsed as xjdf:\n%s",
                            xjdfFile.toString(),
                            e.getMessage()
                    ),
                    e
            );
        };
        ValidationResult result = validator.validate(xjdf);
        if (!result.isValid()) {
            throw new AssertionError(
                String.format(
                    "Sample %s is invalid:\n%s",
                    xjdfFile.toString(),
                    result.getViolations().collect { "* ${it}" }.join("\n")
                )
            )
        }
    }
}

