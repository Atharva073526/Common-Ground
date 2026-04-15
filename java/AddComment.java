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

        Object nameObj = session.getAttribute("userFname");
        String userName = (nameObj != null) ? nameObj.toString() : "Guest";

        try {

            Connection connection = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = connection.prepareStatement("INSERT INTO message(eventId, senderName, comment) VALUES (?, ?, ?)");

            ps.setInt(1, Integer.parseInt(eventIdParam));
            ps.setString(2, userName);
            ps.setString(3, userMessage);

            ps.executeUpdate();

            resp.setStatus(HttpServletResponse.SC_OK);        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
