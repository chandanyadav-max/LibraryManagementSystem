package ui;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.geometry.*;

import model.Book;
import dao.BookDAO;

public class BookView {

    private VBox root = new VBox(10);
    private TableView<Book> table = new TableView<>();

    public BookView(){
        root.setPadding(new Insets(20));

        TextField isbn = new TextField(); isbn.setPromptText("ISBN");
        TextField title = new TextField(); title.setPromptText("Title");
        TextField author = new TextField(); author.setPromptText("Author");

        Button add = new Button("Add");

        add.setOnAction(e->{
            try{
                BookDAO.add(new Book(0,isbn.getText(),title.getText(),author.getText()));
                load();
            }catch(Exception ex){ex.printStackTrace();}
        });

        // TABLE COLUMNS
        TableColumn<Book,String> col1 = new TableColumn<>("ISBN");
        col1.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getIsbn()));

        TableColumn<Book,String> col2 = new TableColumn<>("Title");
        col2.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book,String> col3 = new TableColumn<>("Author");
        col3.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));

        table.getColumns().addAll(col1,col2,col3);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        root.getChildren().addAll(new HBox(10,isbn,title,author,add),table);
        load();
    }

    private void load(){
        try{
            ObservableList<Book> data = FXCollections.observableArrayList(BookDAO.getAll());
            table.setItems(data);
        }catch(Exception e){e.printStackTrace();}
    }

    public VBox getView(){ return root; }
}