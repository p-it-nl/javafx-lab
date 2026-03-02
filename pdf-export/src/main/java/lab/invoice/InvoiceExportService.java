package lab.invoice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Uses Playwright Java to build invoice.pdf and invoice-page-*.png from paginated HTML.
 */
public final class InvoiceExportService {

    private InvoiceExportService() {
    }

    public static void runPlaywrightExport(String html, Path outputDir) {
        try {
            Files.createDirectories(outputDir);
            Path htmlFile = outputDir.resolve("invoice-print.html");
            Files.writeString(htmlFile, html, StandardCharsets.UTF_8);
            InvoicePlaywrightExporter.export(htmlFile.toString(), outputDir);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to export invoice", exception);
        }
    }
}
