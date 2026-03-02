package lab.invoice;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders paginated invoice HTML via Playwright Java and emits PDF + page PNG previews.
 */
public final class InvoicePlaywrightExporter {

    private InvoicePlaywrightExporter() {
    }

    public static void export(String htmlFilePathOrUrl, Path outputDir) {
        try {
            Files.createDirectories(outputDir);
        } catch (IOException exception) {
            throw new RuntimeException("Cannot create output directory: " + outputDir, exception);
        }

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            try {
                BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                        .setViewportSize(794, 1123)
                        .setDeviceScaleFactor(2.0));
                Page page = context.newPage();

                String target = toTargetUrl(htmlFilePathOrUrl);
                page.navigate(target, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
                page.waitForFunction("() => !document.fonts || document.fonts.status === 'loaded'");

                Path pdfPath = outputDir.resolve("invoice.pdf");
                page.pdf(new Page.PdfOptions()
                        .setPath(pdfPath)
                        .setFormat("A4")
                        .setPrintBackground(true)
                        .setPreferCSSPageSize(true));

                Locator sheets = page.locator(".sheet");
                int count = sheets.count();
                List<String> imageFiles = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    String name = "invoice-page-" + (i + 1) + ".png";
                    Path imagePath = outputDir.resolve(name);
                    sheets.nth(i).screenshot(new Locator.ScreenshotOptions().setPath(imagePath));
                    imageFiles.add(name);
                }

                writeManifest(outputDir.resolve("manifest.json"), count, imageFiles);
            } finally {
                browser.close();
            }
        }
    }

    private static void writeManifest(Path manifestPath, int pageCount, List<String> images) {
        StringBuilder manifest = new StringBuilder();
        manifest.append("{\n");
        manifest.append("  \"pageCount\": ").append(pageCount).append(",\n");
        manifest.append("  \"pdf\": \"invoice.pdf\",\n");
        manifest.append("  \"images\": [");
        for (int i = 0; i < images.size(); i++) {
            if (i > 0) {
                manifest.append(", ");
            }
            manifest.append("\"").append(images.get(i)).append("\"");
        }
        manifest.append("],\n");
        manifest.append("  \"generatedAt\": \"").append(Instant.now()).append("\"\n");
        manifest.append("}\n");

        try {
            Files.writeString(manifestPath, manifest, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to write manifest", exception);
        }
    }

    private static String toTargetUrl(String value) {
        if (value.startsWith("http://") || value.startsWith("https://") || value.startsWith("file://")) {
            return value;
        }
        return Path.of(value).toAbsolutePath().toUri().toString();
    }
}
