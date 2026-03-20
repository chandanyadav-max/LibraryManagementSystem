package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import controller.MemberController;
import controller.TransactionController;
import model.Book;
import model.Member;
import model.Transaction;

public class ReturnBookView {

    private final MemberController      memberCtrl = MemberController.getInstance();
    private final TransactionController txCtrl     = TransactionController.getInstance();

    private VBox root;
    private ComboBox<Member> memberCombo;
    private TableView<Transaction> loanTable;
    private VBox   fineBox;
    private Label  fineLbl;
    private Button returnBtn;

    public ReturnBookView() { build(); }

    public Node getRoot() { return root; }

    public void refresh() {
        memberCombo.setItems(memberCtrl.getAllMembers());
        memberCombo.setValue(null);
        loanTable.getItems().clear();
        fineBox.setVisible(false);
        fineLbl.setText("");
    }

    private void build() {
        root = new VBox(0);
        root.getStyleClass().add("content-area");
        root.getChildren().addAll(buildHeader(), buildReturnPanel(), buildLoansTable());
        VBox.setVgrow(buildLoansTable(), Priority.ALWAYS);
    }

    private HBox buildHeader() {
        HBox h = new HBox();
        h.getStyleClass().add("content-header");
        h.setAlignment(Pos.CENTER_LEFT);
        VBox t = new VBox(3);
        t.getChildren().addAll(
                lbl("Return Book", "page-title"),
                lbl("Select a member to see their active loans, then process a return", "page-subtitle"));
        h.getChildren().add(t);
        return h;
    }

    private VBox buildReturnPanel() {
        VBox wrapper = new VBox(16);
        wrapper.setPadding(new Insets(24, 28, 20, 28));

        VBox card = new VBox(16);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 2);");

        Label cardTitle = new Label("Process Return");
        cardTitle.getStyleClass().add("section-title");

        // Member selection
        VBox memberSel = new VBox(6);
        memberSel.getChildren().add(lbl("Select Member", "form-label"));
        memberCombo = new ComboBox<>(memberCtrl.getAllMembers());
        memberCombo.setPromptText("— Choose a member —");
        memberCombo.setMaxWidth(400);
        memberCombo.setCellFactory(lv -> memberCell());
        memberCombo.setButtonCell(memberCell());
        memberCombo.setOnAction(e -> onMemberSelected());
        memberSel.getChildren().add(memberCombo);

        // Fine preview box (hidden initially)
        fineBox = new VBox(6);
        fineBox.setVisible(false);
        fineBox.setManaged(false);

        fineLbl = new Label("");
        fineBox.getChildren().add(fineLbl);

        // Return button
        returnBtn = new Button("Return Selected Book");
        returnBtn.getStyleClass().add("success-btn");
        returnBtn.setStyle(returnBtn.getStyle() + " -fx-font-size:13px; -fx-padding: 10 24;");
        returnBtn.setDisable(true);
        returnBtn.setOnAction(e -> handleReturn());

        card.getChildren().addAll(cardTitle, memberSel, returnBtn, fineBox);
        wrapper.getChildren().add(card);
        return wrapper;
    }

    private VBox buildLoansTable() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(0, 28, 28, 28));
        VBox.setVgrow(box, Priority.ALWAYS);

        Label title = new Label("Member's Active Loans");
        title.getStyleClass().add("section-title");

        loanTable = new TableView<>();
        loanTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        loanTable.getStyleClass().add("panel-card");
        loanTable.setPlaceholder(new Label("Select a member to see their loans"));
        VBox.setVgrow(loanTable, Priority.ALWAYS);

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("Loan ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getTransactionID()).asObject());
        idCol.setPrefWidth(80);

        TableColumn<Transaction, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(d -> {
            Book b = txCtrl.findBook(d.getValue().getBookID());
            return new SimpleStringProperty(b != null ? b.getTitle() : "—");
        });

        TableColumn<Transaction, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(d -> {
            Book b = txCtrl.findBook(d.getValue().getBookID());
            return new SimpleStringProperty(b != null ? b.getAuthor() : "—");
        });

        TableColumn<Transaction, String> issuedCol = new TableColumn<>("Issued");
        issuedCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getIssueDateStr()));

        TableColumn<Transaction, String> dueCol = new TableColumn<>("Due Date");
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

        TableColumn<Transaction, String> finePreviewCol = new TableColumn<>("Fine Preview");
        finePreviewCol.setCellValueFactory(d -> {
            double fine = d.getValue().calculateFine();
            return new SimpleStringProperty(fine > 0 ? String.format("£%.2f", fine) : "—");
        });
        finePreviewCol.setPrefWidth(100);

        // Highlight overdue rows
        loanTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Transaction t, boolean empty) {
                super.updateItem(t, empty);
                setStyle(!empty && t != null && t.isOverdue() ? "-fx-background-color: #FFF5F5;" : "");
            }
        });

        // Update fine preview and enable/disable button on selection
        loanTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            updateFinePreview(sel);
            returnBtn.setDisable(sel == null);
        });

        loanTable.getColumns().addAll(idCol, bookCol, authorCol, issuedCol, dueCol, statusCol, finePreviewCol);
        box.getChildren().addAll(title, loanTable);
        return box;
    }

    private void onMemberSelected() {
        Member m = memberCombo.getValue();
        fineBox.setVisible(false);
        fineBox.setManaged(false);
        returnBtn.setDisable(true);

        if (m == null) {
            loanTable.getItems().clear();
            return;
        }
        loanTable.setItems(txCtrl.getActiveForMember(m.getMemberID()));
        loanTable.getSelectionModel().clearSelection();
    }

    private void updateFinePreview(Transaction t) {
        if (t == null) {
            fineBox.setVisible(false);
            fineBox.setManaged(false);
            return;
        }

        double fine = t.calculateFine();
        fineBox.getStyleClass().removeAll("fine-box", "no-fine-box");
        fineLbl.getStyleClass().removeAll("fine-text-warn", "no-fine-text");

        if (fine > 0) {
            fineBox.getStyleClass().add("fine-box");
            fineLbl.getStyleClass().add("fine-text-warn");
            fineLbl.setText(String.format(
                    "⚠  This loan is overdue. A fine of £%.2f will be charged on return.", fine));
        } else {
            fineBox.getStyleClass().add("no-fine-box");
            fineLbl.getStyleClass().add("no-fine-text");
            fineLbl.setText("✔  No fine applicable — book is within the loan period.");
        }
        fineBox.setVisible(true);
        fineBox.setManaged(true);
    }

    private void handleReturn() {
        Transaction selected = loanTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Book book = txCtrl.findBook(selected.getBookID());
        String bookTitle = book != null ? book.getTitle() : "this book";

        double previewFine = selected.calculateFine();
        String msg = previewFine > 0
                ? String.format("Return \"%s\"?\n\nA fine of £%.2f will be recorded.", bookTitle, previewFine)
                : String.format("Return \"%s\"?\n\nNo fine applicable.", bookTitle);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.CANCEL);
        confirm.setTitle("Confirm Return");
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                double fine = txCtrl.returnBook(selected);

                String result = fine > 0
                        ? String.format("Book returned successfully.\nFine charged: £%.2f", fine)
                        : "Book returned successfully. No fine charged.";

                Alert done = new Alert(Alert.AlertType.INFORMATION, result, ButtonType.OK);
                done.setTitle("Return Processed");
                done.setHeaderText(null);
                done.showAndWait();

                // Refresh the view
                onMemberSelected();
                fineBox.setVisible(false);
                fineBox.setManaged(false);
                returnBtn.setDisable(true);
            }
        });
    }

    private ListCell<Member> memberCell() {
        return new ListCell<>() {
            @Override protected void updateItem(Member m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : "ID " + m.getMemberID() + "  —  " + m.getName());
            }
        };
    }

    private Label lbl(String text, String style) {
        Label l = new Label(text); l.getStyleClass().add(style); return l;
    }
}