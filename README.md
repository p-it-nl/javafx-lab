# JavaFX Invoice Export Lab

Reference implementation for this flow:

1. Author/edit invoice HTML in JavaFX `WebView`.
2. Export fully paginated HTML (`.sheet` per physical page).
3. Run headless Chromium via **Playwright Java** to generate:
   - `invoice.pdf` (print-ready)
   - `invoice-page-*.png` (read-only preview images)
   - `manifest.json`
4. Load preview images later in JavaFX without PDF viewer dependencies.

## Project layout

- `src/main/java/lab/invoice/InvoiceLabApp.java`: JavaFX host + export trigger.
- `src/main/java/lab/invoice/InvoiceWebViewExport.java`: Extracts export-ready HTML from `WebView`.
- `src/main/java/lab/invoice/InvoiceExportService.java`: Writes captured HTML and invokes Playwright Java exporter.
- `src/main/java/lab/invoice/InvoicePlaywrightExporter.java`: Playwright Java export pipeline.
- `web/invoice-editor.html`: Example invoice with explicit `.sheet` pagination.
- `web/invoice-print.css`: Print CSS (`@page`, break rules, no-split constraints).
- `web/preview-viewer.html`: Read-only PNG page preview viewer.

## Run

### JavaFX app

```bash
mvn javafx:run
```

On first run, Playwright Java will download its browser runtime automatically if needed.

## Notes on pagination correctness

- The DOM uses explicit `.sheet` page containers.
- `@page` and print breaks mirror physical paper size.
- `break-inside: avoid` protects totals/signature/critical rows.
- PNG previews are generated from the same `.sheet` elements used for PDF export.
