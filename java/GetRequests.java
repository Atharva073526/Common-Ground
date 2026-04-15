import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/GetRequests")
public class GetRequests extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer adminId = (Integer) session.getAttribute("userId");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        StringBuilder json = new StringBuilder("[");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/new_user", "root", "Salvi360@")) {
            // Using JOIN to pull the sender's name directly from the userlist table
            String sql = "SELECT ec.connection_id, ec.event_name, u.Fname AS sender_name " +
                    "FROM event_connections ec " +
                    "JOIN userlist u ON ec.sender_id = u.id " +
                    "WHERE ec.admin_id = ? AND ec.status = 'pending'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                json.append(String.format(
                        "{\"id\":%d, \"sender\":\"%s\", \"event\":\"%s\"},",
                        rs.getInt("connection_id"),
                        rs.getString("sender_name"),
                        rs.getString("event_name")
                ));
            }
            if (json.length() > 1) json.setLength(json.length() - 1);
        } catch (Exception e) { e.printStackTrace(); }
        json.append("]");

        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }
}