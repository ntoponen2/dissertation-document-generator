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
    private final int year;
    /**
     * Учреждение
     */
    private final String institution;
}
