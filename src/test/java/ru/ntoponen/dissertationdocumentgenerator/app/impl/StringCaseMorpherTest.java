package ru.ntoponen.dissertationdocumentgenerator.app.impl;

import com.github.petrovich4j.Case;
import com.github.petrovich4j.Gender;
import com.github.petrovich4j.NameType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringCaseMorpherTest {
    @Test
    void testInflectLastNameToGenitive() {
        String result = StringCaseMorpher.morph("Иванов", NameType.LastName, Gender.Male, Case.Genitive);
        assertEquals("Иванова", result);
    }

    @Test
    void testNullInputReturnsNull() {
        assertNull(StringCaseMorpher.morph(null, NameType.LastName, Gender.Male, Case.Genitive));
    }
}