package lab.invoice;

import javafx.scene.web.WebEngine;

/**
 * Pulls the final export DOM from WebView.
 */
public final class InvoiceWebViewExport {

    private InvoiceWebViewExport() {
    }

    /**
     * Requires the editor page to expose window.getExportHtml() that returns fully paginated HTML.
     */
    public static String captureExportHtml(WebEngine webEngine) {
        Object value = webEngine.executeScript("window.getExportHtml ? window.getExportHtml() : document.documentElement.outerHTML");
        return value == null ? "" : value.toString();
    }
}
