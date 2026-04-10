package ui;

import dao.BookDAO;
import dao.IssueDAO;
import dao.MemberDAO;
import model.Book;
import model.Issue;
import model.Member;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class IssueView {

    private VBox root = new VBox(15);
    private TableView<Issue> table = new TableView<>();

    private ComboBox<Book> bookCombo = new ComboBox<>();
    private ComboBox<Member> memberCombo = new ComboBox<>();

    public IssueView() {
        root.setPadding(new Insets(20));

        // Buttons
        Button issueBtn = new Button("Issue Book");
        Button returnBtn = new Button("Return Book");

        // Load ComboBox data
        loadCombos();

        // Table columns
        TableColumn<Issue, String> colBook = new TableColumn<>("Book");
        colBook.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getBookTitle()));

        TableColumn<Issue, String> colMember = new TableColumn<>("Member");
        colMember.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMemberName()));

        TableColumn<Issue, String> colIssueDate = new TableColumn<>("Issue Date");
        colIssueDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getIssueDate().toString()));

        TableColumn<Issue, String> colReturnDate = new TableColumn<>("Return Date");
        colReturnDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getReturnDate() != null ? d.getValue().getReturnDate().toString() : ""
        ));

        table.getColumns().addAll(colBook, colMember, colIssueDate, colReturnDate);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Button actions
        issueBtn.setOnAction(e -> {
            try {
                Book selectedBook = bookCombo.getSelectionModel().getSelectedItem();
                Member selectedMember = memberCombo.getSelectionModel().getSelectedItem();
                if (selectedBook != null && selectedMember != null) {
                    IssueDAO.issue(selectedBook.getBookId(), selectedMember.getMemberId());
                    loadTable();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        returnBtn.setOnAction(e -> {
            try {
                Book selectedBook = bookCombo.getSelectionModel().getSelectedItem();
                Member selectedMember = memberCombo.getSelectionModel().getSelectedItem();
                if (selectedBook != null && selectedMember != null) {
                    IssueDAO.returnBook(selectedBook.getBookId(), selectedMember.getMemberId());
                    loadTable();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox controls = new HBox(10, bookCombo, memberCombo, issueBtn, returnBtn);
        root.getChildren().addAll(controls, table);

        loadTable();
    }

    private void loadCombos() {
        try {
            ObservableList<Book> books = FXCollections.observableArrayList(BookDAO.getAll());
            ObservableList<Member> members = FXCollections.observableArrayList(MemberDAO.getAll());

            bookCombo.setItems(books);
            bookCombo.setPromptText("Select Book");

            memberCombo.setItems(members);
            memberCombo.setPromptText("Select Member");

            // Show book title in ComboBox
            bookCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Book item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getTitle());
                }
            });
            bookCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Book item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getTitle());
                }
            });

            // Show member name in ComboBox
            memberCombo.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Member item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getName());
                }
            });
            memberCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Member item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getName());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTable() {
        try {
            ObservableList<Issue> data = FXCollections.observableArrayList(IssueDAO.getAll());
            table.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VBox getView() {
        return root;
    }
}