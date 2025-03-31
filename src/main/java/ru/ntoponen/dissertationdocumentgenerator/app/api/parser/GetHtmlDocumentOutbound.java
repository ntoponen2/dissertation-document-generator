package ru.ntoponen.dissertationdocumentgenerator.app.api.parser;

import org.jsoup.nodes.Document;

public interface GetHtmlDocumentOutbound {
    /**
     * Получить ${@link Document} по ссылке
     *
     * @param url ссылка для получения страницы
     * @return страница, полученная по ссылке
     */
    Document getDocumentByUrl(String url);
}
