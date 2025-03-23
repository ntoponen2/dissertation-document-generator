package ru.ntoponen.dissertationdocumentgenerator.domain;

import lombok.Data;

/**
 * Информация о высшем образовании
 */
@Data
public class Graduation {
    /**
     * Год окончания ВУЗа
     */
    private Integer year;
    /**
     * Окончил с отличием? true -- да, false -- нет
     */
    private Boolean withHonours;
    /**
     * Наименование ВУЗа
     */
    private String university;
    /**
     * Специальность
     */
    private String major;
}
