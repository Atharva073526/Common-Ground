import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;

@WebServlet("/GetMessages")
public class GetMessage extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String eventId = req.getParameter("eventId");
        resp.setContentType("application/json");

        String url = "jdbc:mysql://localhost:3306/new_user";
        String user = "root";
        String pass = "Salvi360@";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            // Fetch messages for this specific event
            String sql = "SELECT senderName, comment, time FROM message WHERE eventId = ? ORDER BY messageId ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(eventId));
            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {

                Timestamp ts = rs.getTimestamp("time");
                String formattedTime = (ts != null) ? new java.text.SimpleDateFormat("HH:mm").format(ts) : "";

                json.append(String.format(
                        "{\"sender\":\"%s\", \"text\":\"%s\", \"time\":\"%s\"},",
                        rs.getString("senderName"),
                        rs.getString("comment"),
                        formattedTime
                ));
            }
            if (json.length() > 1) json.setLength(json.length() - 1); // Remove trailing comma
            json.append("]");

            resp.getWriter().write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(500);
        }
    }
}