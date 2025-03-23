package ru.ntoponen.dissertationdocumentgenerator.domain.doctoral;

import lombok.Data;
import ru.ntoponen.dissertationdocumentgenerator.domain.CandidateExam;
import ru.ntoponen.dissertationdocumentgenerator.domain.Graduation;
import ru.ntoponen.dissertationdocumentgenerator.domain.Institution;
import ru.ntoponen.dissertationdocumentgenerator.domain.Person;
import ru.ntoponen.dissertationdocumentgenerator.domain.Supervisor;

import java.util.List;

/**
 * Информация необходимая для генерации файлов по шаблонам для докторской степени
 */
@Data
public class DoctoralData {
    /**
     * Название работы
     */
    private String title;
    /**
     * Специальность(сти) по которым защищается соискатель
     */
    private List<String> majors;
    /**
     * Соискатель претендует на ученое звание
     */
    private String claimedDegree;
    /**
     * Учреждение, в котором проходит защита
     */
    private Institution institution;
    /**
     * Информация о соискателе
     */
    private Person candidate;
    /**
     * Информация о высшем образовании соискателя
     */
    private Graduation graduation;
    /**
     * Информация об ученой степени соискателя
     */
    private AcademicDegree academicDegree;
    /**
     * Докторантура
     */
    private Doctorate doctorate;
    /**
     * Результаты экзаменов кандидата
     */
    private CandidateExam candidateExam;
    /**
     * Научный руководитель
     */
    private Supervisor supervisor;

    public String getMajors() {
        return String.join(", ", majors);
    }
}
