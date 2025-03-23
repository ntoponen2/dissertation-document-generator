package ru.ntoponen.dissertationdocumentgenerator.domain;

import lombok.Data;

/**
 * Учреждение
 */
@Data
public class Institution {
    /**
     * Кафедра
     */
    private String department;
    /**
     * Факультет
     */
    private String faculty;
}
