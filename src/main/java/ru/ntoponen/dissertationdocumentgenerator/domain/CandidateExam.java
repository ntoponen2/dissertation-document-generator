package ru.ntoponen.dissertationdocumentgenerator.domain;

import lombok.Data;

/**
 * Результаты экзаменов кандидата
 */
@Data
public class CandidateExam {
    /**
     * Год
     */
    private int year;
    /**
     * Учреждение
     */
    private String institution;
}
