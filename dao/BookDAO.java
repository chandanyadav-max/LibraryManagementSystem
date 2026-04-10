package dao;

import model.Book;
import util.DBConnection;

import java.sql.*;
import java.util.*;

public class BookDAO {

    public static void add(Book b) throws Exception {
        String sql = "INSERT INTO books(isbn,title,author) VALUES(?,?,?)";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, b.getIsbn());
        ps.setString(2, b.getTitle());
        ps.setString(3, b.getAuthor());

        ps.executeUpdate();
    }

    public static List<Book> getAll() throws Exception {
        List<Book> list = new ArrayList<>();

        String sql = "SELECT * FROM books";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Book(
                    rs.getInt("book_id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author")
            ));
        }
        return list;
    }
}