package ru.ntoponen.dissertationdocumentgenerator.app.api.parser;

import ru.ntoponen.dissertationdocumentgenerator.domain.DissertationBoard;

public interface DissertationBoardGetter {
    /**
     * Получить данные диссертационного совета
     *
     * @return данные диссертационного совета
     */
    DissertationBoard getDissertationBoard();
}
