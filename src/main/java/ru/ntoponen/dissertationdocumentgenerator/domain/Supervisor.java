package ru.ntoponen.dissertationdocumentgenerator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Научный руководитель
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Supervisor extends Person {
    /**
     * Ученая степень
     */
    private String degree;
    /**
     * Ученое звание
     */
    private String title;
    /**
     * Должность
     */
    private String position;
    /**
     * Место работы
     */
    private String placeOfWork;

    // TODO: сделать метод getShortSignature: сокращенная степень + Фамилия И.О.
}
