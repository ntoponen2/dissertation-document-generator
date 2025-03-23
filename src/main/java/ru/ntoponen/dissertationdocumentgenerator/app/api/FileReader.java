package ru.ntoponen.dissertationdocumentgenerator.app.api;

import org.springframework.core.io.Resource;

import java.io.IOException;

public interface FileReader {
    /**
     * Получить файл с наименованием из текущей директории
     *
     * @param fileName наименование файла
     * @throws java.io.IOException ошибка чтения файла
     */
    Resource getFileFromCurrentDirectory(String fileName) throws IOException;
}
