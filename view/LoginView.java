package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import controller.AuthController;
import model.Librarian;
import util.StyleManager;

public class LoginView {

    private final Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // ── Card ───────────────────────────────────────────────────
        VBox card = new VBox(0);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(420);

        // Logo / Title area
        VBox titleBox = new VBox(6);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 28, 0));

        // Icon circle
        StackPane icon = new StackPane();
        icon.setStyle("-fx-background-color:#1E2D5A; -fx-background-radius:30px; " +
                "-fx-min-width:56px; -fx-max-width:56px; -fx-min-height:56px; -fx-max-height:56px;");
        Label iconLbl = new Label("📚");
        iconLbl.setStyle("-fx-font-size:22px;");
        icon.getChildren().add(iconLbl);

        Label title = new Label("Library Management System");
        title.getStyleClass().add("login-title");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(300);
        title.setWrapText(true);

        Label subtitle = new Label("University of Bedfordshire");
        subtitle.getStyleClass().add("login-subtitle");

        titleBox.getChildren().addAll(icon, title, subtitle);

        // Separator
        Separator sep = new Separator();
        sep.setPadding(new Insets(0, 0, 20, 0));

        // Form
        VBox form = new VBox(6);

        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("field-label");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        userField.setPrefHeight(40);
        userField.setText("admin"); // pre-fill for convenience

        VBox.setMargin(userLabel, new Insets(0, 0, 0, 0));

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("field-label");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.setPrefHeight(40);
        passField.setText("admin123");

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("login-error");
        errorLabel.setWrapText(true);

        VBox.setMargin(passLabel, new Insets(10, 0, 0, 0));
        VBox.setMargin(errorLabel, new Insets(4, 0, 0, 0));

        form.getChildren().addAll(userLabel, userField, passLabel, passField, errorLabel);

        // Login button
        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("primary-btn");
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(42);
        loginBtn.setStyle(loginBtn.getStyle() + " -fx-font-size:14px; -fx-background-radius:8px;");
        VBox.setMargin(loginBtn, new Insets(18, 0, 0, 0));

        Label version = new Label("v1.0.0  •  Chandan Yadav  •  2439477");
        version.getStyleClass().add("login-version");
        version.setAlignment(Pos.CENTER);
        VBox.setMargin(version, new Insets(18, 0, 0, 0));

        card.getChildren().addAll(titleBox, sep, form, loginBtn, version);

        // ── Login action ───────────────────────────────────────────
        Runnable doLogin = () -> {
            String username = userField.getText().trim();
            String password = passField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both username and password.");
                return;
            }

            Librarian librarian = AuthController.getInstance().validateLogin(username, password);
            if (librarian != null) {
                new MainView(stage, librarian).show();
            } else {
                errorLabel.setText("Invalid username or password. Please try again.");
                passField.clear();
                passField.requestFocus();
            }
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passField.setOnAction(e -> doLogin.run());
        userField.setOnAction(e -> passField.requestFocus());

        // ── Root ──────────────────────────────────────────────────
        StackPane root = new StackPane(card);
        root.getStyleClass().add("login-root");
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 1100, 700);
        StyleManager.apply(scene);

        stage.setScene(scene);
        stage.show();

        userField.requestFocus();
        userField.selectAll();
    }
}