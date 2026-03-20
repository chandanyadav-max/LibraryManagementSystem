package view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import controller.MemberController;
import model.Member;
import util.StyleManager;

import java.time.LocalDate;

public class ManageMembersView {

    private final Stage stage;
    private final MemberController memberCtrl = MemberController.getInstance();

    private VBox root;
    private TableView<Member> table;
    private TextField searchField;

    public ManageMembersView(Stage stage) {
        this.stage = stage;
        build();
    }

    public Node getRoot() { return root; }
    public void refresh() {
        table.setItems(memberCtrl.getAllMembers());
        table.refresh();
    }

    private void build() {
        root = new VBox(0);
        root.getStyleClass().add("content-area");
        root.getChildren().addAll(buildHeader(), buildToolbar(), buildTableSection());
    }

    private HBox buildHeader() {
        HBox h = new HBox();
        h.getStyleClass().add("content-header");
        h.setAlignment(Pos.CENTER_LEFT);
        VBox t = new VBox(3);
        t.getChildren().addAll(
                lbl("Manage Members", "page-title"),
                lbl("Register new members and manage existing accounts", "page-subtitle"));
        h.getChildren().add(t);
        return h;
    }

    private HBox buildToolbar() {
        HBox bar = new HBox(10);
        bar.setPadding(new Insets(16, 28, 12, 28));
        bar.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("🔍  Search name, email, phone…");
        searchField.getStyleClass().add("search-box");
        searchField.setPrefWidth(260);
        searchField.textProperty().addListener((obs, o, n) -> {
            if (n == null || n.isBlank()) { table.setItems(memberCtrl.getAllMembers()); return; }
            String lc = n.toLowerCase();
            table.setItems(memberCtrl.getAllMembers().filtered(m ->
                    m.getName().toLowerCase().contains(lc) ||
                    m.getEmail().toLowerCase().contains(lc) ||
                    m.getPhone().toLowerCase().contains(lc)));
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("➕  Add Member");
        addBtn.getStyleClass().add("primary-btn");
        addBtn.setOnAction(e -> showDialog(null));

        Button editBtn = new Button("✏  Edit");
        editBtn.getStyleClass().add("warning-btn");
        editBtn.setOnAction(e -> {
            Member sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info("Select a member to edit."); return; }
            showDialog(sel);
        });

        Button deleteBtn = new Button("🗑  Delete");
        deleteBtn.getStyleClass().add("danger-btn");
        deleteBtn.setOnAction(e -> {
            Member sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { info("Select a member to delete."); return; }
            Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                    "Remove member \"" + sel.getName() + "\"?", ButtonType.YES, ButtonType.CANCEL);
            a.setHeaderText(null);
            a.showAndWait().ifPresent(bt -> { if (bt == ButtonType.YES) memberCtrl.deleteMember(sel); });
        });

        bar.getChildren().addAll(searchField, spacer, addBtn, editBtn, deleteBtn);
        return bar;
    }

    private VBox buildTableSection() {
        VBox box = new VBox(0);
        box.setPadding(new Insets(0, 28, 28, 28));
        VBox.setVgrow(box, Priority.ALWAYS);

        table = new TableView<>(memberCtrl.getAllMembers());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("panel-card");
        table.setPlaceholder(new Label("No members registered"));
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Member, Integer> idCol = new TableColumn<>("Member ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getMemberID()).asObject());
        idCol.setPrefWidth(90);

        TableColumn<Member, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));

        TableColumn<Member, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));
        phoneCol.setPrefWidth(110);

        TableColumn<Member, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));

        TableColumn<Member, String> regDateCol = new TableColumn<>("Registered");
        regDateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRegistrationDateFormatted()));
        regDateCol.setPrefWidth(110);

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, addressCol, regDateCol);
        box.getChildren().add(table);
        return box;
    }

    private void showDialog(Member memberToEdit) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle(memberToEdit == null ? "Add New Member" : "Edit Member");
        dialog.setResizable(false);

        HBox header = new HBox();
        header.getStyleClass().add("dialog-header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label hLbl = new Label(memberToEdit == null ? "Add New Member" : "Edit Member");
        hLbl.getStyleClass().add("dialog-title");
        header.getChildren().add(hLbl);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-body");
        grid.setHgap(14); grid.setVgap(10);
        grid.setMinWidth(500);

        TextField nameF    = field("Full name");
        TextField emailF   = field("Email address");
        TextField phoneF   = field("Phone number");
        TextField addressF = field("Residential address");

        if (memberToEdit != null) {
            nameF.setText(memberToEdit.getName());
            emailF.setText(memberToEdit.getEmail());
            phoneF.setText(memberToEdit.getPhone());
            addressF.setText(memberToEdit.getAddress());
        }

        addRow(grid, 0, "Full Name *", nameF);
        addRow(grid, 1, "Email *",     emailF);
        addRow(grid, 2, "Phone",       phoneF);
        addRow(grid, 3, "Address",     addressF);

        Label errLbl = new Label("");
        errLbl.getStyleClass().add("login-error");
        grid.add(errLbl, 0, 4, 2, 1);

        HBox footer = new HBox(10);
        footer.getStyleClass().add("dialog-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("secondary-btn");
        cancel.setOnAction(e -> dialog.close());

        Button save = new Button(memberToEdit == null ? "Add Member" : "Save Changes");
        save.getStyleClass().add("primary-btn");
        save.setOnAction(e -> {
            String name  = nameF.getText().trim();
            String email = emailF.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                errLbl.setText("Name and Email are required.");
                return;
            }
            if (memberToEdit == null) {
                int uid = memberCtrl.nextUserId();
                Member m = new Member(uid, name, email, "pass" + uid, uid,
                        phoneF.getText().trim(), addressF.getText().trim());
                m.setRegistrationDate(LocalDate.now());
                memberCtrl.addMember(m);
            } else {
                memberCtrl.updateMember(memberToEdit, name, email,
                        phoneF.getText().trim(), addressF.getText().trim());
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

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private void addRow(GridPane grid, int row, String label, Node field) {
        Label l = new Label(label); l.getStyleClass().add("form-label");
        grid.add(l, 0, row); grid.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private Label lbl(String text, String style) {
        Label l = new Label(text); l.getStyleClass().add(style); return l;
    }

    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }
}