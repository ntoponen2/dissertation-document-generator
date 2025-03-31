package ru.ntoponen.dissertationdocumentgenerator.domain;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.NameType;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import static java.util.Objects.isNull;
import static ru.ntoponen.dissertationdocumentgenerator.app.impl.StringCaseMorpher.morph;

/**
 * Данные человека
 */
@Data
@Slf4j
@Accessors(chain = true)
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
        if (isNull(this.gender)) {
            log.warn("Cannot get genitive form of person {} because it's gender is null", this.lastName);
            return null;
        }
        com.github.petrovich4j.Gender morpherGender = this.gender.morpherGender();
        String lastNameGenitive = morph(this.lastName, NameType.LastName, morpherGender, Case.Genitive);
        String firstNameGenitive = morph(this.firstName, NameType.FirstName, morpherGender, Case.Genitive);
        String secondNameGenitive = morph(this.secondName, NameType.PatronymicName, morpherGender, Case.Genitive);
        return lastNameGenitive + " " + firstNameGenitive + " " + secondNameGenitive;
    }

    public String getFullNameDative() {
        if (isNull(this.gender)) {
            log.warn("Cannot get dative form of person {} because it's gender is null", this.lastName);
            return null;
        }
        com.github.petrovich4j.Gender morpherGender = this.gender.morpherGender();
        String lastNameDative = morph(this.lastName, NameType.LastName, morpherGender, Case.Dative);
        String firstNameDative = morph(this.firstName, NameType.FirstName, morpherGender, Case.Dative);
        String secondNameDative = morph(this.secondName, NameType.PatronymicName, morpherGender, Case.Dative);
        return lastNameDative + " " + firstNameDative + " " + secondNameDative;
    }

    public int getBirthYear() {
        return isNull(this.birthDate) ? 0 : this.birthDate.getYear();
    }
}
