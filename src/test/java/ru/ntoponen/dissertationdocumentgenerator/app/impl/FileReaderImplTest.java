package ru.ntoponen.dissertationdocumentgenerator.app.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileReaderImplTest {
    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private FileReaderImpl fileReader;

    @Test
    void testGetExistingFile() throws IOException {
        String fileName = "template.json";
        UrlResource mockResource = new UrlResource(Objects.requireNonNull(getClass().getResource("/" + fileName)));
        when(resourceLoader.getResource("file:" + fileName)).thenReturn(mockResource);

        Resource result = fileReader.getFileFromCurrentDirectory(fileName);

        assertNotNull(result);
        assertEquals(fileName, result.getFilename());
    }

    @Test
    void testGetNonExistentFile() {
        String fileName = "missing.json";
        when(resourceLoader.getResource("file:" + fileName)).thenReturn(new FileSystemResource(""));

        assertThrows(IOException.class, () ->
            fileReader.getFileFromCurrentDirectory(fileName)
        );
    }
}