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

@WebServlet("/addEvent")
public class addEvent extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        int userId = (Integer) session.getAttribute("userId");
        String eventName    = req.getParameter("eventName");
        String eventDate    = req.getParameter("eventDate");
        String eventTime    = req.getParameter("eventTime");
        String eventAddress = req.getParameter("eventAddress");
        String eventCity    = req.getParameter("eventCity");
        String eventPincode = req.getParameter("eventPincode");
        String eventSport   = req.getParameter("eventSport");
        String eventLevel   = req.getParameter("eventLevel");
        String eventDesc    = req.getParameter("eventDesc");

        String userName= (String) session.getAttribute("userFname");
        String userCity= (String) session.getAttribute("userCity");
        String userLevel= (String) session.getAttribute("userLevel");


        String url      = "jdbc:mysql://localhost:3306/new_user";
        String user     = "root";
        String password = "Salvi360@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);

            con.setAutoCommit(true);

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO eventslist(name, eventDate, eventTime, sports, level, description, address, city, pincode, user_name, user_city, user_level, userId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, eventName);
            ps.setString(2, eventDate);
            ps.setString(3, eventTime);
            ps.setString(4, eventSport);
            ps.setString(5, eventLevel);
            ps.setString(6, eventDesc);
            ps.setString(7, eventAddress);
            ps.setString(8, eventCity);
            ps.setString(9, eventPincode);
            ps.setString(10, userName);
            ps.setString(11, userCity);
            ps.setString(12, userLevel);
            ps.setInt(13, userId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println("ERROR: " + e.getMessage());
        }

        resp.sendRedirect("dashboard.jsp");
    }
}
