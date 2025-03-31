package ru.ntoponen.dissertationdocumentgenerator.domain;

import static com.github.petrovich4j.Gender.Female;
import static com.github.petrovich4j.Gender.Male;

/**
 * Пол
 */
public enum Gender {
    MALE, FEMALE;

    public com.github.petrovich4j.Gender morpherGender() {
        return switch (this) {
            case MALE -> Male;
            case FEMALE -> Female;
        };
    }
}
