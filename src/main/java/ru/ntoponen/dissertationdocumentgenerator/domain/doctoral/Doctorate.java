package ru.ntoponen.dissertationdocumentgenerator.domain.doctoral;

import lombok.Data;

import java.time.LocalDate;

/**
 * Докторантура
 */
@Data
public class Doctorate {
    /**
     * Дата начала
     */
    private LocalDate dateFrom;
    /**
     * Дата окончания
     */
    private LocalDate dateTo;
    /**
     * Кафедра
     */
    private String department;
    /**
     * Должность
     */
    private String position;
}
