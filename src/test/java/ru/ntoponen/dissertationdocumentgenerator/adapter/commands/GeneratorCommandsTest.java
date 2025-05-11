package ru.ntoponen.dissertationdocumentgenerator.adapter.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import ru.ntoponen.dissertationdocumentgenerator.app.impl.FieldValueExtractor;
import ru.ntoponen.dissertationdocumentgenerator.app.impl.FileReaderImpl;
import ru.ntoponen.dissertationdocumentgenerator.app.impl.generate.FileGeneratorImpl;
import ru.ntoponen.dissertationdocumentgenerator.app.impl.parser.DissertationBoardGetterImpl;
import ru.ntoponen.dissertationdocumentgenerator.domain.DissertationBoard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneratorCommandsTest {
    @Mock
    private DissertationBoardGetterImpl dissertationBoardGetter;

    private GeneratorCommands generatorCommands;
    private Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        // Временная директория для теста
        tempDir = Files.createTempDirectory("docgen-e2e");

        // Копируем JSON-файл из resources
        ClassPathResource resource = new ClassPathResource("template.json");
        Path inputJson = tempDir.resolve("template.json");
        Files.copy(resource.getInputStream(), inputJson, StandardCopyOption.REPLACE_EXISTING);

        // Настраиваем текущую рабочую директорию на tempDir
        System.setProperty("user.dir", tempDir.toString());

        // Создаём настоящие компоненты
        var reader = new FileReaderImpl(new DefaultResourceLoader());
        var generator = new FileGeneratorImpl(new FieldValueExtractor(), dissertationBoardGetter);
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(dissertationBoardGetter.getDissertationBoard()).thenReturn(new DissertationBoard());

        generatorCommands = new GeneratorCommands(reader, generator, objectMapper);
    }

    @Test
    void testGenerateCreatesZipWithValidDocx() throws Exception {
        String result = generatorCommands.generate("template.json");

        Path zipPath = tempDir.resolve("output.zip");
        assertTrue(Files.exists(zipPath), "output.zip должен быть создан");
        boolean found = false;
        StringBuilder textFromDoc = new StringBuilder();
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".docx")) {
                    found = true;
                    Path docxTemp = tempDir.resolve("test.docx");
                    Files.copy(zis, docxTemp, StandardCopyOption.REPLACE_EXISTING);
                    try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(docxTemp))) {
                        textFromDoc.append(
                            doc.getParagraphs().stream()
                                .map(XWPFParagraph::getText)
                                .collect(Collectors.joining(" "))
                        );
                    }
                }
            }
        }
        assertTrue(found, "В ZIP должен быть хотя бы один .docx файл");
        assertEquals("Привет, Иван!", textFromDoc.toString());
        assertTrue(result.contains("Иван"),
            "Метод должен возвращать информацию об успешно прочитанных данных");
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(java.io.File::delete);
    }
}