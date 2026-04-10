package dao;

import model.Issue;
import util.DBConnection;

import java.sql.*;
import java.util.*;

public class IssueDAO {

    public static void issue(int bookId, int memberId) throws Exception {
        String sql = "INSERT INTO issue(book_id,member_id) VALUES(?,?)";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, bookId);
        ps.setInt(2, memberId);

        ps.executeUpdate();
    }

    public static void returnBook(int bookId, int memberId) throws Exception {
        String sql = "UPDATE issue SET return_date=NOW() " +
                "WHERE book_id=? AND member_id=? AND return_date IS NULL";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, bookId);
        ps.setInt(2, memberId);

        ps.executeUpdate();
    }

    public static List<Issue> getAll() throws Exception {
        List<Issue> list = new ArrayList<>();

        String sql =
                "SELECT i.issue_id, b.title, m.name, i.issue_date, i.return_date " +
                "FROM issue i " +
                "JOIN books b ON i.book_id=b.book_id " +
                "JOIN members m ON i.member_id=m.member_id";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Issue(
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getTimestamp("issue_date"),
                    rs.getTimestamp("return_date")
            ));
        }
        return list;
    }

    public static List<Issue> getByMember(int memberId) throws Exception {
        List<Issue> list = new ArrayList<>();

        String sql =
                "SELECT i.issue_id, b.title, m.name, i.issue_date, i.return_date " +
                "FROM issue i " +
                "JOIN books b ON i.book_id=b.book_id " +
                "JOIN members m ON i.member_id=m.member_id " +
                "WHERE m.member_id=?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, memberId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Issue(
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getTimestamp("issue_date"),
                    rs.getTimestamp("return_date")
            ));
        }
        return list;
    }
}