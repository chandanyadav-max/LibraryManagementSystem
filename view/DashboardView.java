package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import controller.BookController;
import controller.MemberController;
import controller.TransactionController;
import model.Transaction;
import storage.DataStore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardView {

    private final BookController        bookCtrl   = BookController.getInstance();
    private final MemberController      memberCtrl = MemberController.getInstance();
    private final TransactionController txCtrl     = TransactionController.getInstance();

    private VBox root;

    // Stat labels (updated on refresh)
    private Label totalBooksNum, totalMembersNum, activeLoansNum, overdueNum;

    // Recent transactions table
    private TableView<Transaction> recentTable;

    public DashboardView() {
        build();
    }

    public Node getRoot()  { return root; }
    public void refresh()  {
        totalBooksNum.setText(String.valueOf(bookCtrl.countTotal()));
        totalMembersNum.setText(String.valueOf(memberCtrl.countTotal()));
        activeLoansNum.setText(String.valueOf(txCtrl.countActive()));
        overdueNum.setText(String.valueOf(txCtrl.countOverdue()));
        recentTable.setItems(txCtrl.getActiveTransactions());
        recentTable.refresh();
    }

    private void build() {
        root = new VBox(0);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = buildHeader();

        // Scrollable body
        VBox body = new VBox(24);
        body.setPadding(new Insets(28));

        // Stat cards row
        HBox statsRow = buildStatsRow();
        HBox.setHgrow(statsRow, Priority.ALWAYS);

        // Recent transactions
        VBox recentBox = buildRecentTransactions();

        body.getChildren().addAll(statsRow, recentBox);

        ScrollPane scroll = new ScrollPane(body);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("content-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(header, scroll);
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("content-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3);
        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        Label sub = new Label(now);
        sub.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, sub);

        header.getChildren().add(titleBox);
        return header;
    }

    private HBox buildStatsRow() {
        HBox row = new HBox(16);
        row.setFillHeight(true);

        totalBooksNum   = new Label(String.valueOf(bookCtrl.countTotal()));
        totalMembersNum = new Label(String.valueOf(memberCtrl.countTotal()));
        activeLoansNum  = new Label(String.valueOf(txCtrl.countActive()));
        overdueNum      = new Label(String.valueOf(txCtrl.countOverdue()));

        VBox c1 = statCard("stat-blue",  "📚", totalBooksNum,   "Total Books");
        VBox c2 = statCard("stat-green", "👥", totalMembersNum, "Registered Members");
        VBox c3 = statCard("stat-amber", "📋", activeLoansNum,  "Active Loans");
        VBox c4 = statCard("stat-red",   "⚠", overdueNum,      "Overdue Books");

        for (VBox c : new VBox[]{c1, c2, c3, c4}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            row.getChildren().add(c);
        }
        return row;
    }

    private VBox statCard(String colorClass, String icon, Label numLabel, String labelText) {
        VBox card = new VBox(12);
        card.getStyleClass().addAll("stat-card", colorClass);

        // Icon box
        StackPane iconBox = new StackPane();
        iconBox.getStyleClass().add("stat-icon-box");
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size:20px;");
        iconBox.getChildren().add(iconLbl);

        // Number
        numLabel.getStyleClass().add("stat-number");

        // Label
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("stat-label");

        card.getChildren().addAll(iconBox, numLabel, lbl);
        return card;
    }

    private VBox buildRecentTransactions() {
        VBox box = new VBox(14);

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Active Loans");
        title.getStyleClass().add("section-title");
        titleRow.getChildren().add(title);

        recentTable = new TableView<>();
        recentTable.getStyleClass().add("panel-card");
        recentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        recentTable.setPrefHeight(300);
        recentTable.setPlaceholder(new Label("No active loans"));

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("Loan ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getTransactionID()).asObject());
        idCol.setPrefWidth(80);

        TableColumn<Transaction, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(d -> {
            model.Member m = txCtrl.findMember(d.getValue().getMemberID());
            return new javafx.beans.property.SimpleStringProperty(m != null ? m.getName() : "Unknown");
        });

        TableColumn<Transaction, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(d -> {
            model.Book b = txCtrl.findBook(d.getValue().getBookID());
            return new javafx.beans.property.SimpleStringProperty(b != null ? b.getTitle() : "Unknown");
        });

        TableColumn<Transaction, String> issueDateCol = new TableColumn<>("Issue Date");
        issueDateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIssueDateStr()));

        TableColumn<Transaction, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDueDateStr()));

        TableColumn<Transaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null); return;
                }
                Transaction t = (Transaction) getTableRow().getItem();
                String s = t.getDisplayStatus();
                Label badge = new Label(s);
                badge.getStyleClass().addAll("badge", "badge-" + s.toLowerCase());
                setGraphic(badge);
                setText(null);
            }
        });

        // Highlight overdue rows
        recentTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Transaction t, boolean empty) {
                super.updateItem(t, empty);
                if (!empty && t != null && t.isOverdue()) {
                    setStyle("-fx-background-color: #FFF5F5;");
                } else {
                    setStyle("");
                }
            }
        });

        recentTable.getColumns().addAll(idCol, memberCol, bookCol, issueDateCol, dueDateCol, statusCol);
        recentTable.setItems(txCtrl.getActiveTransactions());

        box.getChildren().addAll(titleRow, recentTable);
        return box;
    }
}