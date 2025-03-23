package ru.ntoponen.dissertationdocumentgenerator.app.api.generate;

import ru.ntoponen.dissertationdocumentgenerator.domain.doctoral.DoctoralData;

import java.lang.reflect.InvocationTargetException;

/**
 * Генератор файлов
 */
public interface FileGenerator {
    /**
     * Сгенерировать документы для докторской диссертации
     *
     * @param data данные для генерации
     * @throws IllegalAccessException ошибка получения данных с наименованием полей
     * @throws InvocationTargetException ошибка при вызове геттера
     */
    void generateDoctoral(DoctoralData data) throws IllegalAccessException, InvocationTargetException;
}
