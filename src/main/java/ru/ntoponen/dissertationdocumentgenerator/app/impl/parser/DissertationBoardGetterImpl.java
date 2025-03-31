package ru.ntoponen.dissertationdocumentgenerator.app.impl.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ntoponen.dissertationdocumentgenerator.app.api.parser.DissertationBoardGetter;
import ru.ntoponen.dissertationdocumentgenerator.app.api.parser.GetHtmlDocumentOutbound;
import ru.ntoponen.dissertationdocumentgenerator.domain.BoardMember;
import ru.ntoponen.dissertationdocumentgenerator.domain.DissertationBoard;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class DissertationBoardGetterImpl implements DissertationBoardGetter {
    @Value(value = "${dissertation.document.generator.settings.dissertationBoard.url}")
    private String dissertationBoardUrl;

    private final GetHtmlDocumentOutbound getHtmlDocumentOutbound;

    @Override
    public DissertationBoard getDissertationBoard() {
        Document document = getHtmlDocumentOutbound.getDocumentByUrl(dissertationBoardUrl);
        DissertationBoard dissertationBoard = new DissertationBoard();

        String fullPdsString = document.select("h1").get(0).text();
        dissertationBoard.setPds(fullPdsString.substring(fullPdsString.indexOf(" ") + 1));

        List<String> allMembers = document.select("ol li").stream()
            .map(Element::text)
            .toList();

        for (String memberString : allMembers) {
            if (memberString.contains("Председатель")) {
                mapRanked(memberString, dissertationBoard::setChairman);
            } else if (memberString.contains("Заместитель председателя")) {
                mapRanked(memberString, dissertationBoard::addDeputyChairman);
            } else if (memberString.contains("Ученый секретарь")) {
                mapRanked(memberString, dissertationBoard::setSecretary);
            } else {
                mapMember(memberString, dissertationBoard);
            }
        }

        return dissertationBoard;
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void mapRanked(String memberString, Consumer<BoardMember> rankedMemberSetter) {
        String degree = memberString.substring(memberString.indexOf("–") + 1, memberString.indexOf(",")).trim();

        int endOfTitleIndex = memberString.indexOf(" ", memberString.indexOf(",") + 2);
        String title = memberString.substring(memberString.indexOf(",") + 2, endOfTitleIndex).trim();

        String fullName = memberString.substring(endOfTitleIndex, memberString.indexOf("(")).trim();
        String[] fullNameSplit = fullName.split(" ");

        BoardMember member = new BoardMember()
            .setDegree(degree)
            .setTitle(title);
        member
            .setLastName(fullNameSplit[0])
            .setFirstName(fullNameSplit[1])
            .setSecondName(fullNameSplit[2]);

        rankedMemberSetter.accept(member);
    }

    private void mapMember(String memberString, DissertationBoard dissertationBoard) {
        String fullName = memberString.substring(0, memberString.indexOf("-")).trim();
        String[] fullNameSplit = fullName.split(" ");

        // В ranked и здесь разные разделители между должностью и степенью, и ФИО и степенью. В первом случае это "–", во втором "-"
        String degree = memberString.substring(memberString.indexOf("-") + 1, memberString.indexOf(",")).trim();

        String title =
            memberString.substring(memberString.indexOf(",") + 2, memberString.indexOf(" ", memberString.indexOf(",") + 2)).trim();

        BoardMember member = new BoardMember()
            .setDegree(degree)
            .setTitle(title);
        member
            .setLastName(fullNameSplit[0])
            .setFirstName(fullNameSplit[1])
            .setSecondName(fullNameSplit[2]);

        dissertationBoard.addMember(member);
    }
}
