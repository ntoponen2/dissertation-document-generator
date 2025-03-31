package ru.ntoponen.dissertationdocumentgenerator.adapter.jsoup;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ru.ntoponen.dissertationdocumentgenerator.app.api.parser.GetHtmlDocumentOutbound;

@Slf4j
@Component
public class GetHtmlDocumentOutboundImpl implements GetHtmlDocumentOutbound {
    @Override
    public Document getDocumentByUrl(String url) {
        log.info("Getting document from page {}", url);
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (Exception e) {
            log.error("Error while getting document from {}", url, e);
        }
        return document;
    }
}
