package edu.upvictoria.javasqlide.controllers;

import edu.upvictoria.poo.Analyzer;
import edu.upvictoria.poo.Column;
import edu.upvictoria.poo.Database;
import edu.upvictoria.poo.Table;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDEController {
    private final Analyzer analyzer = new Analyzer();
    private File file = null;

    @FXML
    private TreeView<String> dbFileView;
    @FXML
    private TabPane tabs;
    @FXML
    private TextArea errorMessages;
    @FXML
    private CodeArea codeArea;

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", Analyzer.getKeywords()) + ")\\b";
    private static final String DATATYPES_PATTERN = "\\b(" + String.join("|", Analyzer.getDataTypes()) + ")\\b";
    private static final String CONSTRAINTS_PATTERN = "\\b(" + String.join("|", Analyzer.getConstraints()) + ")\\b";
    private static final String FUNCTIONS_PATTERN = "\\b(" + String.join("|", Analyzer.getFunctions()) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String COMPARATORS_PATTERN = "\\b(" + String.join("|", Analyzer.getComparators()) + ")\\b";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<DATATYPE>" + DATATYPES_PATTERN + ")"
                    + "|(?<CONSTRAINT>" + CONSTRAINTS_PATTERN + ")"
                    + "|(?<FUNCTION>" + FUNCTIONS_PATTERN + ")"
                    + "|(?<COMPARATOR>" + COMPARATORS_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
    );

    @FXML
    protected void initialize() {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        // hace que el tab sean 4 magníficos espacios y no 8 asquerosos espacios
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                codeArea.replaceSelection("    ");
                event.consume();
            }
        });

        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
                });
        codeArea.getStyleClass().add("area");
    }

    @FXML
    protected void useDB(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Database");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File selectedDirectory = chooser.showDialog(null);

        if (selectedDirectory != null) {
            try {
                analyzer.analyzeSyntax("USE " + selectedDirectory.getAbsolutePath()+";");
                loadTree();
            } catch (Exception e) {
                handleExceptions(e.getMessage());
            }
        }
    }

    protected void loadTree() {
        Database database = analyzer.getDatabase();
        ArrayList<Table> tables = database.getTables();

        TreeItem<String> databaseItem = new TreeItem<>(database.getDbFile().getName(), new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/edu/upvictoria/javasqlide/images/database.png")))));
        databaseItem.setExpanded(true);

        for(Table table : tables) {
            TreeItem<String> tableItem = new TreeItem<>(table.getTableName(), new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/edu/upvictoria/javasqlide/images/table.png")))));
            tableItem.setExpanded(false);
            databaseItem.getChildren().add(tableItem);

            ArrayList<Column> columns = table.getColumns();
            for(Column column : columns) {
                TreeItem<String> columnItem = new TreeItem<>(column.getName(), new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/edu/upvictoria/javasqlide/images/column.png")))));
                tableItem.getChildren().add(columnItem);
            }
        }
        dbFileView.setRoot(databaseItem);
    }

    @FXML
    protected void openFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select SQL File");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL File", "*.sql"));
        file = chooser.showOpenDialog(null);

        if (file != null) {
            codeArea.replaceText(getFileContent(file));
        }
    }

    private String getFileContent(File file){
        StringBuilder text = new StringBuilder();
        Charset charset = StandardCharsets.UTF_8;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file, charset));
            String line;

            while((line = br.readLine()) != null){
                text.append(line);
                text.append("\n");
            }
        } catch (Exception e) {
            handleExceptions(e.getMessage());
        }

        return text.toString();
    }

    @FXML
    protected void saveFile(ActionEvent event) {
        if(file == null) {
            saveFileAs();
            return;
        }

        saveTextToFile(codeArea.getText(), file);
    }

    @FXML
    protected void saveFileAs() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save SQL File");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL File", "*.sql"));
        File selectedFile = chooser.showSaveDialog(null);

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".sql")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".sql");
            }
            saveTextToFile(codeArea.getText(), selectedFile);
        }
    }

    private void saveTextToFile(String content, File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println(content);
        } catch (IOException ex) {
            handleExceptions(ex.getMessage());
        }
    }

    @FXML
    protected void executeSelectedStatement(ActionEvent event) {
        String selection = codeArea.getSelectedText();

        try {
            analyzer.analyzeSyntax(selection);
        } catch (Exception e) {
            handleExceptions(e.getMessage());
        }

        if(file != null) {
            loadTree();
        }
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        text = text.toUpperCase();
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("DATATYPE") != null ? "datatype" :
                    matcher.group("CONSTRAINT") != null ? "constraint" :
                    matcher.group("FUNCTION") != null ? "function" :
                    matcher.group("COMPARATOR") != null ? "comparator" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Aquí mando toda la basura de mensajes de excepciones para que se impriman en el tab de logs
     * @param errorMessage
     */
    protected void handleExceptions(String errorMessage) {
        SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
        selectionModel.select(1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String log = errorMessages.getText();
        log = log + "\n" + dtf.format(now) + ": " + errorMessage;
        errorMessages.setText(log);
    }
}
