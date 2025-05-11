package ru.ntoponen.dissertationdocumentgenerator.adapter.jsoup;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GetHtmlDocumentOutboundImplTest {
    private final GetHtmlDocumentOutboundImpl htmlFetcher = new GetHtmlDocumentOutboundImpl();

    @Test
    void testValidHtmlUrlReturnsDocument() {
        Document doc = htmlFetcher.getDocumentByUrl("https://example.com");

        assertNotNull(doc);
    }

    @Test
    void testInvalidUrlReturnsNull() {
        Document doc = htmlFetcher.getDocumentByUrl("http://invalid/");

        assertNull(doc);
    }
}
