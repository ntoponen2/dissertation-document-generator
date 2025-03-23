package ru.ntoponen.dissertationdocumentgenerator.app.impl;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import com.github.petrovich4j.NameType;
import com.github.petrovich4j.Petrovich;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringCaseMorpher {
    public static final Petrovich PETROVICH = new Petrovich();

    public static String morph(String string, NameType nameType, Gender gender, Case morphCase) {
        return PETROVICH.say(string, nameType, gender, morphCase);
    }
}
