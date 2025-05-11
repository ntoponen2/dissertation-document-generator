package ru.ntoponen.dissertationdocumentgenerator;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class AbstractGeneratorTest {
    void compareFilesAsStrings(String expectedPath, String actualPath) throws Exception {
        String expectedText = extractTextFromDocx(expectedPath);
        String actualText = extractTextFromDocx(actualPath);
        assertEquals(expectedText.trim(), actualText.trim(), "content doesn't match");
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private String extractTextFromDocx(String filePath) throws Exception {
        try (InputStream is = Files.newInputStream(Paths.get(Objects.requireNonNull(getClass().getResource(filePath)).toURI()));
             XWPFDocument doc = new XWPFDocument(is)) {

            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            return paragraphs.stream()
                .map(XWPFParagraph::getText)
                .collect(Collectors.joining("\n"));
        }
    }
}
