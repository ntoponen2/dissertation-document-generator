package ru.ntoponen.dissertationdocumentgenerator.domain.doctoral;

import lombok.Data;

/**
 * Ученая степень
 */
@Data
public class AcademicDegree {
    private Integer year;
    /**
     * Наименование организации, где защищалась степень
     */
    private String institution;
    /**
     * Кандидат <sciences> наук
     */
    private String sciences;
    /**
     * Тема диссертации
     */
    private String topic;
}
