package ru.ntoponen.dissertationdocumentgenerator.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/**
 * Данные диссертационного совета
 */
@Data
public class DissertationBoard {
    private String pds;
    private BoardMember chairman;
    private List<BoardMember> deputyChairmen;
    private BoardMember secretary;
    private List<BoardMember> members;

    public String getMembers() {
        return String.join(", ", members.stream().map(Person::getFullName).toList());
    }

    public String getMembersShort() {
        return String.join(", ", Stream.concat(deputyChairmen.stream(), members.stream()).map(BoardMember::getShort).toList());
    }

    public int getCountMembers() {
        return deputyChairmen.size() + members.size() + 2;
    }

    public DissertationBoard addDeputyChairman(BoardMember deputyChairman) {
        if (isNull(deputyChairmen)) {
            deputyChairmen = new ArrayList<>();
        }
        deputyChairmen.add(deputyChairman);
        return this;
    }

    public DissertationBoard addMember(BoardMember member) {
        if (isNull(members)) {
            members = new ArrayList<>();
        }
        members.add(member);
        return this;
    }
}
