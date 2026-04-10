package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.scene.text.*;
import java.sql.*;
import util.DBConnection;

public class LoginView {

    private BorderPane root = new BorderPane();

    public LoginView(Stage stage){

        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(320);
        card.setPadding(new Insets(25));

        Label title = new Label("Library Login");
        title.setFont(Font.font("Segoe UI",FontWeight.BOLD,22));

        TextField email = new TextField();
        email.setPromptText("Email");
        styleField(email);

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");
        styleField(pass);

        Label msg = new Label();
        msg.setStyle("-fx-text-fill:red;");

        Button login = new Button("Login");
        login.setStyle(buttonStyle());
        login.setPrefWidth(200);

        login.setOnAction(e-> {
            String em = email.getText();
            String pw = pass.getText();
            
         // ADMIN LOGIN
            if(em.equals("admin") && pw.equals("admin123")){
                Dashboard dash = new Dashboard(stage); // FIXED
                stage.getScene().setRoot(dash.getView());
                return;
            }

            // MEMBER LOGIN
            try(Connection con = DBConnection.getConnection()){
                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM members WHERE email=? AND password=?");
                ps.setString(1,em);
                ps.setString(2,pw);
                ResultSet rs = ps.executeQuery();

                if(rs.next()){
                    MemberHome home = new MemberHome(rs.getInt("member_id"), stage); // FIXED
                    stage.getScene().setRoot(home.getView());
                }else{
                    msg.setText("Invalid Credentials");
                }

            }catch(Exception ex){
                ex.printStackTrace();
            }
        });

        card.getChildren().addAll(title,email,pass,login,msg);

        StackPane wrapper = new StackPane(card);
        wrapper.setStyle("-fx-background-color:linear-gradient(to bottom,#e3f2fd,#f5f9ff);");

        root.setCenter(wrapper);
    }

    private void styleField(TextField tf){
        tf.setStyle("-fx-background-radius:8;-fx-border-radius:8;-fx-border-color:#90caf9;-fx-padding:10;");
    }

    private String buttonStyle(){
        return "-fx-background-color:linear-gradient(to right,#1e3c72,#2a5298);-fx-text-fill:white;-fx-background-radius:8;-fx-padding:10;";
    }

    public BorderPane getView(){ return root; }
}