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

@WebServlet("/DiscussionServlet")
public class DiscussionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url  = "jdbc:mysql://localhost:3306/new_user";
        String user = "root";
        String pass = "Salvi360@";

        String eventId = request.getParameter("event_id");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            String query = "SELECT * FROM eventslist WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(eventId));

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                request.setAttribute("eventId", rs.getInt("id"));
                request.setAttribute("eventName", rs.getString("name"));
                request.setAttribute("eventCity", rs.getString("city"));
                request.setAttribute("eventDate", rs.getString("eventDate"));
            }

            request.getRequestDispatcher("ChatRoom.jsp").forward(request, response);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
