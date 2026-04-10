package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.collections.*;

import dao.IssueDAO;
import model.Issue;

public class MemberHome {

    private VBox root = new VBox(15);
    private TableView<Issue> table = new TableView<>();

    public MemberHome(int memberId, Stage stage){
        root.setPadding(new Insets(20));

        Button logout = new Button("Logout");

        logout.setOnAction(e -> {
            LoginView login = new LoginView(stage);
            stage.getScene().setRoot(login.getView());
        });

        Label title = new Label("My Issued Books");

        // COLUMNS
        TableColumn<Issue,String> col1 = new TableColumn("Book");
        col1.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getBookTitle()));

        TableColumn<Issue,String> col2 = new TableColumn("Issue Date");
        col2.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIssueDate().toString()));

        table.getColumns().addAll(col1,col2);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        load(memberId);

        root.getChildren().addAll(logout, title, table);
    }

    private void load(int memberId){
        try{
            ObservableList<Issue> data =
                    FXCollections.observableArrayList(IssueDAO.getByMember(memberId));
            table.setItems(data);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public VBox getView(){ return root; }
}