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

@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String url  = "jdbc:mysql://localhost:3306/new_user";
        String user = "root";
        String pass = "Salvi360@";

        HttpSession session = req.getSession();

        String eventIdParam = req.getParameter("eventId");
        String userMessage = req.getParameter("messageInput");

        // ✅ Add validation
        if (eventIdParam == null || userMessage == null || userMessage.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Object nameObj = session.getAttribute("userFname");
        Object roleObj = session.getAttribute("userRole");
        String userName = (nameObj != null) ? nameObj.toString() : "Guest";

        String role = session.getAttribute("userRole").toString();

        // ✅ Use try-with-resources to auto-close connections
        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = connection.prepareStatement("INSERT INTO message(eventId, senderName, comment, role) VALUES (?, ?, ?, ?)")) {

            ps.setInt(1, Integer.parseInt(eventIdParam));
            ps.setString(2, userName);
            ps.setString(3, userMessage);
            ps.setString(4, role);

            ps.executeUpdate();
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}