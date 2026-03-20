package view;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controller.BookController;
import model.Book;
import util.StyleManager;

public class ManageBooksView {

    private final Stage stage;
    private final BookController bookCtrl = BookController.getInstance();

    private VBox root;
    private TableView<Book> table;
    private TextField searchField;

    public ManageBooksView(Stage stage) {
        this.stage = stage;
        build();
    }

    public Node getRoot() { return root; }
    public void refresh() {
        table.setItems(bookCtrl.getAllBooks());
        table.refresh();
    }

    private void build() {
        root = new VBox(0);
        root.getStyleClass().add("content-area");

//        root.getChildren().addAll(buildHeader(), buildToolbar(), buildTableSection());
//        VBox.setVgrow(buildTableSection(), Priority.ALWAYS);
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("content-header");
        header.setAlignment(Pos.CENTER_LEFT);
        VBox t = new VBox(3);
        t.getChildren().addAll(
                labelOf("Manage Books", "page-title"),
                labelOf("Add, edit, and remove books from the catalogue", "page-subtitle"));
        header.getChildren().add(t);
        return header;
    }

    private HBox buildToolbar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(16, 28, 12, 28));
        bar.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("🔍  Search title, author, ISBN…");
        searchField.getStyleClass().addAll("search-box");
        searchField.setPrefWidth(260);
        searchField.textProperty().addListener((obs, o, n) -> applySearch(n));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("➕  Add Book");
        addBtn.getStyleClass().add("primary-btn");
        addBtn.setOnAction(e -> showBookDialog(null));

        Button editBtn = new Button("✏  Edit");
        editBtn.getStyleClass().add("warning-btn");
        editBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showInfo("Select a book to edit."); return; }
            showBookDialog(selected);
        });

        Button deleteBtn = new Button("🗑  Delete");
        deleteBtn.getStyleClass().add("danger-btn");
        deleteBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showInfo("Select a book to delete."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete \"" + selected.getTitle() + "\"?", ButtonType.YES, ButtonType.CANCEL);
            confirm.setTitle("Confirm Delete"); confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) bookCtrl.deleteBook(selected);
            });
        });

        bar.getChildren().addAll(searchField, spacer, addBtn, editBtn, deleteBtn);
        return bar;
    }

//    private VBox buildTableSection() {
//        VBox box = new VBox(0);
//        box.setPadding(new Insets(0, 28, 28, 28));
//        VBox.setVgrow(box, Priority.ALWAYS);
//
//        table = new TableView<>(bookCtrl.getAllBooks());
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        table.getStyleClass().add("panel-card");
//        table.setPlaceholder(new Label("No books found"));
//        VBox.setVgrow(table, Priority.ALWAYS);
//
//        // Columns
//        TableColumn<Book, Integer> idCol = col("ID", 60);
//        idCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getBookID()).asObject());
//
//        TableColumn<Book, String> isbnCol = col("ISBN", 130);
//        isbnCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIsbn()));
//
//        TableColumn<Book, String> titleCol = col("Title", 200);
//        titleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
//
//        TableColumn<Book, String> authorCol = col("Author", 150);
//        authorCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAuthor()));
//
//        TableColumn<Book, String> publisherCol = col("Publisher", 130);
//        publisherCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPublisher()));
//
//        TableColumn<Book, String> categoryCol = col("Category", 130);
//        categoryCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory()));
//
//        TableColumn<Book, Integer> qtyCol = col("Qty", 60);
//        qtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()).asObject());
//
//        TableColumn<Book, String> statusCol = col("Status", 100);
//        statusCol.setCellFactory(c -> new TableCell<>() {
//            @Override protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
//                    setGraphic(null); return;
//                }
//                Book b = (Book) getTableRow().getItem();
//                String s = b.getQuantity() > 0 ? "Available" : "Issued";
//                Label badge = new Label(s);
//                badge.getStyleClass().addAll("badge", "badge-" + s.toLowerCase());
//                setGraphic(badge); setText(null);
//            }
//        });
//
//        table.getColumns().addAll(idCol, isbnCol, titleCol, authorCol, publisherCol, categoryCol, qtyCol, statusCol);
//        box.getChildren().add(table);
//        return box;
//    }

    // ── Search ────────────────────────────────────────────────────────────
    private void applySearch(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            table.setItems(bookCtrl.getAllBooks());
        } else {
            String lc = keyword.toLowerCase();
            table.setItems(bookCtrl.getAllBooks().filtered(b ->
                    b.getTitle().toLowerCase().contains(lc) ||
                    b.getAuthor().toLowerCase().contains(lc) ||
                    b.getIsbn().toLowerCase().contains(lc) ||
                    b.getCategory().toLowerCase().contains(lc)));
        }
    }

    // ── Add / Edit Dialog ─────────────────────────────────────────────────
    private void showBookDialog(Book bookToEdit) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle(bookToEdit == null ? "Add New Book" : "Edit Book");
        dialog.setResizable(false);

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("dialog-header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label headerLbl = new Label(bookToEdit == null ? "Add New Book" : "Edit Book");
        headerLbl.getStyleClass().add("dialog-title");
        header.getChildren().add(headerLbl);

        // Body form
        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-body");
        grid.setHgap(14); grid.setVgap(10);
        grid.setMinWidth(500);

        String[] cats = {"Programming", "Software Engineering", "Database", "Networking", "Mathematics", "Science", "Other"};

        TextField    isbnF  = field("e.g. 978-0134685991");
        TextField    titleF = field("Book title");
        TextField    authF  = field("Author name(s)");
        TextField    pubF   = field("Publisher name");
        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll(cats);
        catBox.getSelectionModel().selectFirst();
        catBox.setMaxWidth(Double.MAX_VALUE);
        Spinner<Integer> qtySpinner = new Spinner<>(1, 999, 1);
        qtySpinner.setEditable(true);
        qtySpinner.setMaxWidth(Double.MAX_VALUE);

        if (bookToEdit != null) {
            isbnF.setText(bookToEdit.getIsbn());
            titleF.setText(bookToEdit.getTitle());
            authF.setText(bookToEdit.getAuthor());
            pubF.setText(bookToEdit.getPublisher());
            catBox.setValue(bookToEdit.getCategory());
            qtySpinner.getValueFactory().setValue(bookToEdit.getQuantity());
        }

        int r = 0;
        addRow(grid, r++, "ISBN *",      isbnF);
        addRow(grid, r++, "Title *",     titleF);
        addRow(grid, r++, "Author *",    authF);
        addRow(grid, r++, "Publisher",   pubF);
        addRow(grid, r++, "Category",    catBox);
        addRow(grid, r++, "Quantity",    qtySpinner);

        Label errLbl = new Label("");
        errLbl.getStyleClass().add("login-error");
        grid.add(errLbl, 0, r, 2, 1);

        // Footer buttons
        HBox footer = new HBox(10);
        footer.getStyleClass().add("dialog-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("secondary-btn");
        cancel.setOnAction(e -> dialog.close());

        Button save = new Button(bookToEdit == null ? "Add Book" : "Save Changes");
        save.getStyleClass().add("primary-btn");
        save.setOnAction(e -> {
            String isbn  = isbnF.getText().trim();
            String title = titleF.getText().trim();
            String auth  = authF.getText().trim();
            if (isbn.isEmpty() || title.isEmpty() || auth.isEmpty()) {
                errLbl.setText("ISBN, Title, and Author are required.");
                return;
            }
            if (bookToEdit == null) {
                Book newBook = new Book(bookCtrl.nextId(), isbn, title, auth,
                        pubF.getText().trim(), catBox.getValue(), qtySpinner.getValue());
                bookCtrl.addBook(newBook);
            } else {
                bookCtrl.updateBook(bookToEdit, isbn, title, auth,
                        pubF.getText().trim(), catBox.getValue(), qtySpinner.getValue());
                table.refresh();
            }
            dialog.close();
        });

        footer.getChildren().addAll(cancel, save);

        VBox dialogRoot = new VBox(0, header, grid, footer);
        dialogRoot.getStyleClass().add("dialog-root");

        Scene scene = new Scene(dialogRoot);
        StyleManager.apply(scene);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private <T> TableColumn<T, ?> col(String title, double width) {
        TableColumn<T, ?> c = new TableColumn<>(title);
        c.setPrefWidth(width); return c;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private void addRow(GridPane grid, int row, String label, Node field) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("form-label");
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private Label labelOf(String text, String style) {
        Label l = new Label(text);
        l.getStyleClass().add(style);
        return l;
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }
}