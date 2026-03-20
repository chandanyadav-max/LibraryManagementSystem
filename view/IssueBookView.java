package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import controller.BookController;
import controller.MemberController;
import controller.TransactionController;
import model.Book;
import model.Member;
import model.Transaction;

public class IssueBookView {

    private final int librarianID;
    private final MemberController      memberCtrl = MemberController.getInstance();
    private final BookController        bookCtrl   = BookController.getInstance();
    private final TransactionController txCtrl     = TransactionController.getInstance();

    private VBox root;
    private ComboBox<Member> memberCombo;
    private ComboBox<Book>   bookCombo;
    private Label            statusLbl;
    private TableView<Transaction> issuedTable;

    public IssueBookView(int librarianID) {
        this.librarianID = librarianID;
        build();
    }

    public Node getRoot() { return root; }

    public void refresh() {
        memberCombo.setItems(memberCtrl.getAllMembers());
        bookCombo.setItems(bookCtrl.getAvailableBooks());
        issuedTable.setItems(txCtrl.getActiveTransactions());
        issuedTable.refresh();
        statusLbl.setText("");
    }

    private void build() {
        root = new VBox(0);
        root.getStyleClass().add("content-area");
        root.getChildren().addAll(buildHeader(), buildIssueForm(), buildActiveTable());
        VBox.setVgrow(buildActiveTable(), Priority.ALWAYS);
    }

    private HBox buildHeader() {
        HBox h = new HBox();
        h.getStyleClass().add("content-header");
        h.setAlignment(Pos.CENTER_LEFT);
        VBox t = new VBox(3);
        t.getChildren().addAll(lbl("Issue Book", "page-title"),
                lbl("Select a member and an available book to issue", "page-subtitle"));
        h.getChildren().add(t);
        return h;
    }

    private VBox buildIssueForm() {
        VBox formWrapper = new VBox(16);
        formWrapper.setPadding(new Insets(24, 28, 20, 28));

        // Panel card
        VBox card = new VBox(18);
        card.getStyleClass().add("panel-card");
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-radius: 10px; -fx-background-color: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 2);");

        Label cardTitle = new Label("New Issue");
        cardTitle.getStyleClass().add("section-title");

        // Member selection
        VBox memberBox = new VBox(6);
        memberBox.getChildren().add(lbl("Select Member", "form-label"));
        memberCombo = new ComboBox<>(memberCtrl.getAllMembers());
        memberCombo.setPromptText("— Choose a member —");
        memberCombo.setMaxWidth(Double.MAX_VALUE);
        memberCombo.setCellFactory(lv -> memberCell());
        memberCombo.setButtonCell(memberCell());
        memberBox.getChildren().add(memberCombo);

        // Book selection
        VBox bookBox = new VBox(6);
        bookBox.getChildren().add(lbl("Select Available Book", "form-label"));
        bookCombo = new ComboBox<>(bookCtrl.getAvailableBooks());
        bookCombo.setPromptText("— Choose a book —");
        bookCombo.setMaxWidth(Double.MAX_VALUE);
        bookCombo.setCellFactory(lv -> bookCell());
        bookCombo.setButtonCell(bookCell());
        bookBox.getChildren().add(bookCombo);

        // Fields in one row
        HBox fieldsRow = new HBox(20);
        HBox.setHgrow(memberBox, Priority.ALWAYS);
        HBox.setHgrow(bookBox, Priority.ALWAYS);
        fieldsRow.getChildren().addAll(memberBox, bookBox);

        // Status label
        statusLbl = new Label("");
        statusLbl.setWrapText(true);

        // Issue button
        Button issueBtn = new Button("Issue Book");
        issueBtn.getStyleClass().add("primary-btn");
        issueBtn.setStyle(issueBtn.getStyle() + " -fx-font-size:13px; -fx-padding: 10 24;");
        issueBtn.setOnAction(e -> handleIssue());

        HBox btnRow = new HBox(14, issueBtn, statusLbl);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(cardTitle, fieldsRow, btnRow);
        formWrapper.getChildren().add(card);
        return formWrapper;
    }

    private VBox buildActiveTable() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(0, 28, 28, 28));
        VBox.setVgrow(box, Priority.ALWAYS);

        Label title = new Label("Currently Issued Books");
        title.getStyleClass().add("section-title");

        issuedTable = new TableView<>(txCtrl.getActiveTransactions());
        issuedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        issuedTable.getStyleClass().add("panel-card");
        issuedTable.setPlaceholder(new Label("No active loans"));
        VBox.setVgrow(issuedTable, Priority.ALWAYS);

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("Loan ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getTransactionID()).asObject());
        idCol.setPrefWidth(80);

        TableColumn<Transaction, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(d -> {
            Member m = txCtrl.findMember(d.getValue().getMemberID());
            return new SimpleStringProperty(m != null ? m.getName() : "—");
        });

        TableColumn<Transaction, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(d -> {
            Book b = txCtrl.findBook(d.getValue().getBookID());
            return new SimpleStringProperty(b != null ? b.getTitle() : "—");
        });

        TableColumn<Transaction, String> issuedCol = new TableColumn<>("Issued");
        issuedCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIssueDateStr()));

        TableColumn<Transaction, String> dueCol = new TableColumn<>("Due");
        dueCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDueDateStr()));

        TableColumn<Transaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null); return;
                }
                String s = ((Transaction) getTableRow().getItem()).getDisplayStatus();
                Label badge = new Label(s);
                badge.getStyleClass().addAll("badge", "badge-" + s.toLowerCase());
                setGraphic(badge); setText(null);
            }
        });

        issuedTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Transaction t, boolean empty) {
                super.updateItem(t, empty);
                setStyle(!empty && t != null && t.isOverdue() ? "-fx-background-color: #FFF5F5;" : "");
            }
        });

        issuedTable.getColumns().addAll(idCol, memberCol, bookCol, issuedCol, dueCol, statusCol);
        box.getChildren().addAll(title, issuedTable);
        return box;
    }

    private void handleIssue() {
        Member member = memberCombo.getValue();
        Book   book   = bookCombo.getValue();

        if (member == null || book == null) {
            showStatus("Please select both a member and a book.", false);
            return;
        }

        Transaction t = txCtrl.issueBook(member, book, librarianID);
        if (t != null) {
            showStatus("✔  Book issued! Loan ID: " + t.getTransactionID() +
                    "  |  Due: " + t.getDueDateStr(), true);
            memberCombo.setValue(null);
            bookCombo.setItems(bookCtrl.getAvailableBooks());
            bookCombo.setValue(null);
            issuedTable.setItems(txCtrl.getActiveTransactions());
        } else {
            showStatus("✘  Could not issue book — not available.", false);
        }
    }

    private void showStatus(String msg, boolean success) {
        statusLbl.setText(msg);
        statusLbl.setStyle(success
                ? "-fx-text-fill: #065F46; -fx-font-weight: bold;"
                : "-fx-text-fill: #991B1B; -fx-font-weight: bold;");
    }

    // ── Cell factories ────────────────────────────────────────────────────
    private ListCell<Member> memberCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Member m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : "ID " + m.getMemberID() + "  —  " + m.getName());
            }
        };
    }

    private ListCell<Book> bookCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Book b, boolean empty) {
                super.updateItem(b, empty);
                if (empty || b == null) { setText(null); return; }
                setText(b.getTitle() + "  [" + b.getQuantity() + " available]");
            }
        };
    }

    private Label lbl(String text, String style) {
        Label l = new Label(text); l.getStyleClass().add(style); return l;
    }
}