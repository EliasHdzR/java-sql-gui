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
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class IDEController {
    Analyzer analyzer = new Analyzer();
    
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
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^'\\\\]|\\\\.)*'";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<DATATYPE>" + DATATYPES_PATTERN + ")"
                    + "|(?<CONSTRAINT>" + CONSTRAINTS_PATTERN + ")"
                    + "|(?<FUNCTION>" + FUNCTIONS_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
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
        File selectedFile = chooser.showOpenDialog(null);

        if (selectedFile != null) {
            codeArea.replaceText(getFileContent(selectedFile));
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
    }

    @FXML
    protected void saveFileAs(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select SQL File");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL File", "*.sql"));
        File selectedFile = chooser.showSaveDialog(null);
    }

    @FXML
    protected void executeSelectedStatement(ActionEvent event) {
        String selection = codeArea.getSelectedText();

        try {
            analyzer.analyzeSyntax(selection);
        } catch (Exception e) {
            handleExceptions(e.getMessage());
        }
    }

    /**
     * Aquí mando toda la basura de mensajes de excepciones para que se impriman en el tab de logs
     * @param errorMessage
     */
    protected void handleExceptions(String errorMessage) {
        SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
        selectionModel.select(1);
        errorMessages.setText(errorMessage);
    }
}
