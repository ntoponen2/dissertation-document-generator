package ru.ntoponen.dissertationdocumentgenerator.app.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.ntoponen.dissertationdocumentgenerator.app.api.FileReader;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileReaderImpl implements FileReader {
    private final ResourceLoader resourceLoader;

    @Override
    public Resource getFileFromCurrentDirectory(String fileName) throws IOException {
        Resource file = resourceLoader.getResource("file:" + fileName);

        if (!file.exists()) {
            throw new IOException(String.format("File %s not found in current directory", fileName));
        }

        return file;
    }
}
