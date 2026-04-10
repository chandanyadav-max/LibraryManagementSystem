package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.stage.Stage;

public class Dashboard {

    private BorderPane root = new BorderPane();

    public Dashboard(Stage stage){

        VBox side = new VBox(10);
        side.setPadding(new Insets(20));
        side.setPrefWidth(200);
        side.setStyle("-fx-background-color:linear-gradient(to bottom,#0f2027,#203a43,#2c5364);");

        Button books = navBtn("Books");
        Button members = navBtn("Members");
        Button issue = navBtn("Issue/Return");
        Button logout = navBtn("Logout");

        side.getChildren().addAll(books,members,issue,logout);
        root.setLeft(side);

        BookView book = new BookView();
        MemberView member = new MemberView();
        IssueView issueView = new IssueView();

        root.setCenter(book.getView());

        books.setOnAction(e-> root.setCenter(book.getView()));
        members.setOnAction(e-> root.setCenter(member.getView()));
        issue.setOnAction(e-> root.setCenter(issueView.getView()));

        // LOGOUT
        logout.setOnAction(e -> {
            LoginView login = new LoginView(stage);
            stage.getScene().setRoot(login.getView());
        });
    }

    private Button navBtn(String t){
        Button b = new Button(t);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color:transparent;-fx-text-fill:white;-fx-font-size:14;");
        return b;
    }

    public BorderPane getView(){ return root; }
}