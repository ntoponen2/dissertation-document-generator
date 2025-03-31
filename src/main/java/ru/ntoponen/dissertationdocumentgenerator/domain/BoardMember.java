package ru.ntoponen.dissertationdocumentgenerator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;

/**
 * Член диссертационного совета
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BoardMember extends Person {
    /**
     * Ученая степень
     */
    private String degree;
    /**
     * Ученое звание
     */
    private String title;

    public String getShort() {
        String[] degreeSplit = this.degree.split(" ");
        String shortDegree = degreeSplit[0].charAt(0) + ".";
        String[] degreeMiddleSplit =
            Arrays.stream(degreeSplit[1].split("-")).map(string -> String.valueOf(string.charAt(0))).toArray(String[]::new);
        if (degreeMiddleSplit.length > 1) {
            shortDegree = shortDegree.concat(String.join(".-", degreeMiddleSplit) + ".");
        } else {
            shortDegree = shortDegree.concat(degreeMiddleSplit[0] + ".");
        }
        return shortDegree + "н. " + this.getLastName() + " " + this.getFirstName().charAt(0) + "." + this.getLastName().charAt(0) + ".";
    }
}
