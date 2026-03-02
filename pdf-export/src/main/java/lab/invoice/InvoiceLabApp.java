package lab.invoice;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.nio.file.Path;

/**
 * Minimal JavaFX host showing an invoice WebView editor and triggering HTML->PDF+PNG export.
 */
public class InvoiceLabApp extends Application {

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        // The editor surface can be replaced with your local invoice route.
        Path editorHtml = Path.of("web", "invoice-editor.html").toAbsolutePath();
        engine.load(editorHtml.toUri().toString());

        Button exportButton = new Button("Export PDF + PNG");
        exportButton.setOnAction(event -> {
            String renderedHtml = InvoiceWebViewExport.captureExportHtml(engine);
            InvoiceExportService.runPlaywrightExport(renderedHtml, Path.of("exports", "invoice-001"));
        });

        BorderPane root = new BorderPane();
        root.setTop(new ToolBar(exportButton));
        root.setCenter(webView);

        stage.setTitle("Invoice Lab");
        stage.setScene(new Scene(root, 1200, 900));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
