package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Librarian;
import util.StyleManager;

import java.util.ArrayList;
import java.util.List;

public class MainView {

    private final Stage    stage;
    private final Librarian currentUser;

    private BorderPane root;
    private final List<Button> navButtons = new ArrayList<>();

    // Sub-views (lazily initialized, then cached)
    private DashboardView     dashboardView;
    private ManageBooksView   booksView;
    private ManageMembersView membersView;
    private IssueBookView     issueView;
    private ReturnBookView    returnView;

    public MainView(Stage stage, Librarian currentUser) {
        this.stage       = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        root = new BorderPane();

        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        navigate("dashboard", navButtons.get(0));

        Scene scene = new Scene(root, 1150, 730);
        StyleManager.apply(scene);

        stage.setScene(scene);
        stage.setTitle("Library Management System — " + currentUser.getName());
        stage.show();
    }

    // ── Sidebar ───────────────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.getStyleClass().add("sidebar");

        // Header
        VBox header = new VBox(4);
        header.getStyleClass().add("sidebar-header");

        Label appTitle = new Label("Library\nManagement System");
        appTitle.getStyleClass().add("sidebar-title");
        Label appSub = new Label("University of Bedfordshire");
        appSub.getStyleClass().add("sidebar-subtitle");

        header.getChildren().addAll(appTitle, appSub);

        // Divider
        Region div1 = new Region();
        div1.getStyleClass().add("sidebar-divider");

        // Nav section label
        Label navLabel = new Label("NAVIGATION");
        navLabel.getStyleClass().add("nav-section-label");

        // Nav buttons
        Button dashBtn   = navButton("🏠  Dashboard",       "dashboard");
        Button booksBtn  = navButton("📖  Manage Books",    "books");
        Button membersBtn= navButton("👥  Manage Members",  "members");

        Region div2 = new Region();
        div2.getStyleClass().add("sidebar-divider");
        VBox.setMargin(div2, new Insets(6, 0, 6, 0));

        Label txLabel = new Label("TRANSACTIONS");
        txLabel.getStyleClass().add("nav-section-label");

        Button issueBtn  = navButton("➕  Issue Book",      "issue");
        Button returnBtn = navButton("↩\uFE0F  Return Book",  "return");

        navButtons.addAll(List.of(dashBtn, booksBtn, membersBtn, issueBtn, returnBtn));

        VBox navBox = new VBox(0, navLabel, dashBtn, booksBtn, membersBtn,
                div2, txLabel, issueBtn, returnBtn);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Footer
        VBox footer = buildFooter();

        sidebar.getChildren().addAll(header, div1, navBox, spacer, footer);
        return sidebar;
    }

    private Button navButton(String text, String section) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> navigate(section, btn));
        return btn;
    }

    private VBox buildFooter() {
        VBox footer = new VBox(10);
        footer.getStyleClass().add("sidebar-footer");

        // Avatar + user info
        HBox userRow = new HBox(10);
        userRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar-circle");
        String initials = getInitials(currentUser.getName());
        Label initLbl = new Label(initials);
        initLbl.getStyleClass().add("avatar-initials");
        avatar.getChildren().add(initLbl);

        VBox info = new VBox(2);
        Label nameL = new Label(currentUser.getName());
        nameL.getStyleClass().add("user-name");
        Label roleL = new Label("Librarian  •  " + currentUser.getEmployeeID());
        roleL.getStyleClass().add("user-role");
        info.getChildren().addAll(nameL, roleL);

        userRow.getChildren().addAll(avatar, info);

        Button logoutBtn = new Button("Sign Out");
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to sign out?",
                    ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Sign Out");
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) new LoginView(stage).show();
            });
        });

        footer.getChildren().addAll(userRow, logoutBtn);
        return footer;
    }

    // ── Navigation ────────────────────────────────────────────────────────
    private void navigate(String section, Button activeBtn) {
        // Update button styles
        navButtons.forEach(b -> {
            b.getStyleClass().remove("nav-btn-active");
        });
        if (activeBtn != null) activeBtn.getStyleClass().add("nav-btn-active");

        // Swap content
        Node content = switch (section) {
            case "books"   -> getBooksView();
            case "members" -> getMembersView();
            case "issue"   -> getIssueView();
            case "return"  -> getReturnView();
            default        -> getDashboardView();
        };

        root.setCenter(content);

        // Refresh data on every navigation
        switch (section) {
            case "dashboard" -> dashboardView.refresh();
            case "books"     -> booksView.refresh();
            case "members"   -> membersView.refresh();
            case "issue"     -> issueView.refresh();
            case "return"    -> returnView.refresh();
        }
    }

    // ── Lazy view getters ─────────────────────────────────────────────────
    private Node getDashboardView() {
        if (dashboardView == null) dashboardView = new DashboardView();
        return dashboardView.getRoot();
    }

    private Node getBooksView() {
        if (booksView == null) booksView = new ManageBooksView(stage);
        return booksView.getRoot();
    }

    private Node getMembersView() {
        if (membersView == null) membersView = new ManageMembersView(stage);
        return membersView.getRoot();
    }

    private Node getIssueView() {
        if (issueView == null) issueView = new IssueBookView(currentUser.getLibrarianID());
        return issueView.getRoot();
    }

    private Node getReturnView() {
        if (returnView == null) returnView = new ReturnBookView();
        return returnView.getRoot();
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}