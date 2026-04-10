package dao;

import model.Member;
import util.DBConnection;

import java.sql.*;
import java.util.*;

public class MemberDAO {

    public static void add(Member m) throws Exception {
        String sql = "INSERT INTO members(name,email,password,address) VALUES(?,?,?,?)";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, m.getName());
        ps.setString(2, m.getEmail());
        ps.setString(3, m.getPassword());
        ps.setString(4, m.getAddress());

        ps.executeUpdate();
    }

    public static List<Member> getAll() throws Exception {
        List<Member> list = new ArrayList<>();

        String sql = "SELECT * FROM members";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Member(
                    rs.getInt("member_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("address")
            ));
        }

        return list;
    }

    public static Member login(String email, String password) throws Exception {
        String sql = "SELECT * FROM members WHERE email=? AND password=?";

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, email);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Member(
                    rs.getInt("member_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("address")
            );
        }
        return null;
    }
}