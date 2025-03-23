package ru.ntoponen.dissertationdocumentgenerator.app.impl.generate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ntoponen.dissertationdocumentgenerator.app.api.generate.FileGenerator;
import ru.ntoponen.dissertationdocumentgenerator.app.impl.FieldValueExtractor;
import ru.ntoponen.dissertationdocumentgenerator.domain.doctoral.DoctoralData;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileGeneratorImpl implements FileGenerator {
    @Value(value = "${dissertation.document.generator.settings.templates.folder}")
    private String templatesFolder;

    private final FieldValueExtractor fieldValueExtractor;

    @Override
    public void generateDoctoral(DoctoralData data) throws IllegalAccessException, InvocationTargetException {
        Map<String, String> dataMap = fieldValueExtractor.getFieldValuesUsingGetters(data, null);

        try (FileOutputStream zipFileOutputStream = new FileOutputStream("out.zip");
             ZipOutputStream zipOutputStream = new ZipOutputStream(zipFileOutputStream);
             Stream<Path> templates = Files.list(Paths.get(Objects.requireNonNull(getClass().getResource(templatesFolder)).toURI()))) {
            templates.forEach(template -> generateForTemplate(template, dataMap, zipOutputStream));
        } catch (IOException | URISyntaxException e) {
            log.error("Error while generating files", e);
        }
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void generateForTemplate(Path template, Map<String, String> dataMap, ZipOutputStream zipOutputStream) {
        String templateName = template.getFileName().toString();
        try {
            if (templateName.endsWith(".doc")) {
                generateDoc(dataMap, zipOutputStream, templateName);
            } else if (templateName.endsWith(".docx")) {
                generateDocx(dataMap, zipOutputStream, templateName);
            } else {
                log.warn("Template of unknown type. Skipping generation of {}", templateName);
            }
        } catch (Exception e) {
            log.error("Error while generating file from template {}", templateName, e);
        }
    }

    private void generateDoc(Map<String, String> dataMap, ZipOutputStream zipOutputStream, String fileName) {
        try (InputStream inputStream = Objects.requireNonNull(getClass().getResourceAsStream(templatesFolder + "/" + fileName));
             POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream)) {
            HWPFDocument doc = new HWPFDocument(fileSystem);
            Range range = doc.getRange();
            Set<String> templateStrings = getTemplateStrings(range.text());
            log.debug("Template strings in {}: {}", fileName, templateStrings);

            templateStrings.forEach(templateString -> replaceStringInDoc(templateString, dataMap, range, fileName));

            addDocFileToArchive(doc, zipOutputStream, fileName);
            doc.close();
        } catch (Exception e) {
            log.error("Error while generating doc file {}", fileName, e);
        }
    }

    private void replaceStringInDoc(String templateString, Map<String, String> dataMap, Range range, String fileName) {
        log.debug("Replacing {} in {}", templateString, fileName);
        if (templateString.contains("?")) {
            String replacement = getReplacementFromTernary(templateString, dataMap);
            log.debug("Replacing ternary {} with {} in {}", templateString, replacement, fileName);
            range.replaceText(templateString, replacement);
        } else {
            String fieldName = templateString.substring(2, templateString.indexOf("}"));
            range.replaceText(templateString, dataMap.getOrDefault(fieldName, ""));
        }
    }

    private void addDocFileToArchive(HWPFDocument doc, ZipOutputStream zipOutputStream, String fileName) {
        try (ByteArrayOutputStream fileStream = new ByteArrayOutputStream()) {
            doc.write(fileStream);
            addFileToArchive(fileStream, zipOutputStream, fileName);
        } catch (IOException e) {
            log.error("Error while adding file {} to archive", fileName, e);
        }
    }

    private void generateDocx(Map<String, String> dataMap, ZipOutputStream zipOutputStream, String fileName) {
        try (InputStream inputStream = Objects.requireNonNull(getClass().getResourceAsStream(templatesFolder + "/" + fileName));
             XWPFDocument docx = new XWPFDocument(inputStream)) {
            replaceTextInParagraphs(docx.getParagraphs(), dataMap, fileName);
            for (XWPFTable tbl : docx.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        replaceTextInParagraphs(cell.getParagraphs(), dataMap, fileName);
                    }
                }
            }
            addDocxFileToArchive(docx, zipOutputStream, fileName);
        } catch (Exception e) {
            log.error("Error while generating docx file {}", fileName, e);
        }
    }

    private void replaceTextInParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> dataMap, String fileName) {
        paragraphs.forEach(paragraph -> replaceTextInParagraph(paragraph, dataMap, fileName));
    }

    private void replaceTextInParagraph(XWPFParagraph paragraph, Map<String, String> dataMap, String fileName) {
        Pattern pattern = Pattern.compile(".*\\$\\{(.*?)}.*");
        while (pattern.matcher(paragraph.getText()).find()) {
            log.debug("Computing paragraph: {}", paragraph.getText());
            TextSegment textSegment = paragraph.searchText("$", new PositionInParagraph());
            XWPFRun startRun = paragraph.getRuns().get(textSegment.getBeginRun());
            if (startRun != null) {
                String templateString = getTemplateStringFromDocx(paragraph, startRun, textSegment);
                if (templateString == null) {
                    return;
                }
                String updatedText = getReplaceStringInDocx(templateString, dataMap, fileName);
                startRun.setText(updatedText, 0);
            }
        }
    }

    private String getTemplateStringFromDocx(XWPFParagraph paragraph, XWPFRun startRun, TextSegment textSegment) {
        String startRunText = startRun.getText(0);
        log.debug("Start run text: {}", startRunText);
        String templateString = startRunText.substring(startRunText.indexOf("$"));
        log.debug("Template string at start: {}", templateString);
        if (!templateString.startsWith("${")) {
            XWPFRun secondRun = paragraph.getRuns().get(textSegment.getBeginRun() + 1);
            if (secondRun == null || !secondRun.getText(0).startsWith("{")) {
                return null;
            }
        }
        int runIndex = textSegment.getBeginRun() + 1;
        while (!templateString.matches("\\$\\{(.*?)}")) {
            XWPFRun run = paragraph.getRuns().get(runIndex);
            String runText = run.getText(0);
            log.debug("Run {} text: {}", runIndex, runText);
            if (runText.contains("}")) {
                int indexOfEnd = runText.indexOf("}");
                templateString = templateString.concat(runText.substring(0, indexOfEnd + 1));
                if (runText.length() == 1) {
                    paragraph.removeRun(runIndex);
                    runIndex--;
                } else {
                    run.setText(runText.substring(indexOfEnd + 1), 0);
                }

            } else {
                templateString = templateString.concat(runText);
                paragraph.removeRun(runIndex);
                runIndex--;
            }
            log.debug("Template string after {} run computation: {}", runIndex, templateString);
            runIndex++;
        }

        return templateString;
    }

    private String getReplaceStringInDocx(String templateString, Map<String, String> dataMap, String fileName) {
        String replacement = "";
        if (templateString.contains("?")) {
            replacement = getReplacementFromTernary(templateString, dataMap);
            log.debug("Replacing ternary {} with {} in {}", templateString, replacement, fileName);
        } else {
            String fieldName = templateString.substring(2, templateString.indexOf("}"));
            replacement = dataMap.getOrDefault(fieldName, "");
            log.debug("Replacing {} with {} in {}", templateString, replacement, fileName);
        }
        return replacement;
    }

    private void addDocxFileToArchive(XWPFDocument docx, ZipOutputStream zipOutputStream, String fileName) {
        try (ByteArrayOutputStream fileStream = new ByteArrayOutputStream()) {
            docx.write(fileStream);
            addFileToArchive(fileStream, zipOutputStream, fileName);
        } catch (IOException e) {
            log.error("Error while adding file {} to archive", fileName, e);
        }
    }

    private void addFileToArchive(ByteArrayOutputStream fileStream, ZipOutputStream zipOutputStream, String fileName) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(fileStream.toByteArray());
        zipOutputStream.closeEntry();
    }

    // ===================================================================================================================
    // = Shared
    // ===================================================================================================================

    private Set<String> getTemplateStrings(String text) {
        Pattern pattern = Pattern.compile("\\$\\{(.*?)}");
        Matcher matcher = pattern.matcher(text);

        Set<String> matches = new HashSet<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    /**
     * Получение значения из шаблона с тернарным оператором вида ${fieldValue=expectedValue ? valueIfEquals : valueIfNotEquals}
     */
    private String getReplacementFromTernary(String templateString, Map<String, String> dataMap) {
        String fieldName = templateString.substring(2, templateString.indexOf("="));
        String fieldValue = dataMap.get(fieldName);
        String expectedValue = templateString.substring(templateString.indexOf("=") + 1, templateString.indexOf("?")).trim();
        String replacementValue;
        if (fieldValue.equals(expectedValue)) {
            replacementValue = templateString.substring(templateString.indexOf("?") + 1, templateString.indexOf(":")).trim();
        } else {
            replacementValue = templateString.substring(templateString.indexOf(":") + 1, templateString.indexOf("}")).trim();
        }
        return replacementValue;
    }
}
