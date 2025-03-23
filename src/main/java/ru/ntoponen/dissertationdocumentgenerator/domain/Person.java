package ru.ntoponen.dissertationdocumentgenerator.domain;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.NameType;
import lombok.Data;

import java.time.LocalDate;

import static ru.ntoponen.dissertationdocumentgenerator.app.impl.StringCaseMorpher.morph;

/**
 * Данные человека
 */
@Data
public class Person {
    /**
     * Фамилия
     */
    private String lastName;
    /**
     * Имя
     */
    private String firstName;
    /**
     * Отчество
     */
    private String secondName;
    /**
     * Дата рождения
     */
    private LocalDate birthDate;
    /**
     * Пол
     */
    private Gender gender;
    /**
     * Гражданство (в формате "гражданин Российской федерации")
     */
    private String citizenship;

    public String getFullName() {
        return this.lastName + " " + this.firstName + " " + this.secondName;
    }

    public String getFullNameGenitive() {
        com.github.petrovich4j.Gender morpherGender = this.gender.getMorpherGender();
        String lastNameGenitive = morph(this.lastName, NameType.LastName, morpherGender, Case.Genitive);
        String firstNameGenitive = morph(this.firstName, NameType.FirstName, morpherGender, Case.Genitive);
        String secondNameGenitive = morph(this.secondName, NameType.PatronymicName, morpherGender, Case.Genitive);
        return lastNameGenitive + " " + firstNameGenitive + " " + secondNameGenitive;
    }

    public String getFullNameDative() {
        com.github.petrovich4j.Gender morpherGender = this.gender.getMorpherGender();
        String lastNameDative = morph(this.lastName, NameType.LastName, morpherGender, Case.Dative);
        String firstNameDative = morph(this.firstName, NameType.FirstName, morpherGender, Case.Dative);
        String secondNameDative = morph(this.secondName, NameType.PatronymicName, morpherGender, Case.Dative);
        return lastNameDative + " " + firstNameDative + " " + secondNameDative;
    }

    public int getBirthYear() {
        return this.birthDate.getYear();
    }
}
