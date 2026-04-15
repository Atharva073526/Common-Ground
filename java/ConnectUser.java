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

@WebServlet("/ConnectServlet")
public class ConnectUser extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String url  = "jdbc:mysql://localhost:3306/new_user";
        String user = "root";
        String pass = "Salvi360@";

        HttpSession session = request.getSession();

        // Get the person who IS logged in (the sender)
        Integer senderId = (Integer) session.getAttribute("userId");

        // Get the info sent from JavaScript
        String adminId = request.getParameter("targetAdminId");
        String eventName = request.getParameter("eventName");

        // ... existing session and parameter code ...

        if (senderId != null && adminId != null && !adminId.isEmpty()) {
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                String sql = "INSERT INTO event_connections (sender_id, admin_id, event_name, status) VALUES (?, ?, ?, 'pending')";
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setInt(1, senderId);

                // FIX: Convert the String adminId from the request into an Integer
                ps.setInt(2, Integer.parseInt(adminId));

                ps.setString(3, eventName);

                ps.executeUpdate();
                response.getWriter().write("success");
            } catch (NumberFormatException e) {
                System.out.println("Error: adminId was not a valid number");
                response.getWriter().write("error");
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("error");
            }
        }
    }

}
