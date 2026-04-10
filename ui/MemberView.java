package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.geometry.*;

import dao.MemberDAO;
import model.Member;

public class MemberView {

    private VBox root = new VBox(10);
    private TableView<Member> table = new TableView<>();

    public MemberView(){
        root.setPadding(new Insets(20));

        TextField name = new TextField(); name.setPromptText("Name");
        TextField email = new TextField(); email.setPromptText("Email");
        TextField pass = new TextField(); pass.setPromptText("Password");
        TextField address = new TextField(); address.setPromptText("Address");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill:red;");

        Button add = new Button("Add");

        add.setOnAction(e->{

            String n = name.getText();
            String em = email.getText();
            String pw = pass.getText();
            String ad = address.getText();

            // VALIDATION
            if(!n.matches("[a-zA-Z ]+")){
                msg.setText("Name must contain only letters");
                return;
            }

            if(!em.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
                msg.setText("Invalid email format");
                return;
            }

            if(pw.length() < 6){
                msg.setText("Password must be at least 6 characters");
                return;
            }

            if(ad.isEmpty()){
                msg.setText("Address required");
                return;
            }

            try{
                MemberDAO.add(new Member(0,n,em,pw,ad));
                msg.setText("Added successfully");

                // clear fields
                name.clear();
                email.clear();
                pass.clear();
                address.clear();

                load();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        });

        // TABLE
        TableColumn<Member,String> col1 = new TableColumn<>("Name");
        col1.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Member,String> col2 = new TableColumn<>("Email");
        col2.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Member,String> col3 = new TableColumn<>("Address");
        col3.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));

        table.getColumns().addAll(col1,col2,col3);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        root.getChildren().addAll(new HBox(10,name,email,pass,address,add), msg, table);

        load();
    }

    private void load(){
        try{
            ObservableList<Member> data = FXCollections.observableArrayList(MemberDAO.getAll());
            table.setItems(data);
        }catch(Exception e){e.printStackTrace();}
    }

    public VBox getView(){ return root; }
}