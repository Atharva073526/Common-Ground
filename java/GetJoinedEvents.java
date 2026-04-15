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

@WebServlet("/GetJoinedEvents")
public class GetJoinedEvents extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer myId = (Integer) session.getAttribute("userId");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        StringBuilder json = new StringBuilder("[");
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/new_user", "root", "Salvi360@")) {
            // Join to see the name of the Admin who accepted you
            String sql = "SELECT ec.event_name, u.Fname AS admin_name " +
                    "FROM event_connections ec " +
                    "JOIN userlist u ON ec.admin_id = u.id " +
                    "WHERE ec.sender_id = ? AND ec.status = 'accepted'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, myId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                json.append(String.format(
                        "{\"event\":\"%s\", \"admin\":\"%s\"},",
                        rs.getString("event_name"),
                        rs.getString("admin_name")
                ));
            }
            if (json.length() > 1) json.setLength(json.length() - 1);
        } catch (Exception e) { e.printStackTrace(); }
        json.append("]");

        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }
}