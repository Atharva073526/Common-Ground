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

        // Retrieve user details from session
        Integer userId = (Integer) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userFname");
        String userCity = (String) session.getAttribute("userCity");
        String userLevel = (String) session.getAttribute("userLevel");

        // Event details from form
        String eventName = req.getParameter("eventName");
        String eventDate = req.getParameter("eventDate");
        String eventTime = req.getParameter("eventTime");
        String eventAddress = req.getParameter("eventAddress");
        String eventCity = req.getParameter("eventCity");
        String eventPincode = req.getParameter("eventPincode");
        String eventSport = req.getParameter("eventSport");
        String eventLevel = req.getParameter("eventLevel");
        String eventDesc = req.getParameter("eventDesc");

        // Geolocation (optional) from hidden inputs
        String latStr = req.getParameter("eventLat");
        String lngStr = req.getParameter("eventLng");

        // Database connection details (adjust if needed)
        String url = "jdbc:mysql://localhost:3306/new_user";
        String dbUser = "root";
        String dbPassword = "Salvi360@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, dbUser, dbPassword);
            con.setAutoCommit(true);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO eventslist(" +
                            "name, eventDate, eventTime, sports, level, description, address, city, pincode, " +
                            "user_name, user_city, user_level, userId, latitude, longitude) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
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

            // Handle optional latitude/longitude
            if (latStr != null && lngStr != null && !latStr.isEmpty() && !lngStr.isEmpty()) {
                ps.setDouble(14, Double.parseDouble(latStr));
                ps.setDouble(15, Double.parseDouble(lngStr));
            } else {
                ps.setNull(14, java.sql.Types.DOUBLE);
                ps.setNull(15, java.sql.Types.DOUBLE);
            }

            ps.executeUpdate();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println("ERROR: " + e.getMessage());
            return;
        }

        resp.sendRedirect("dashboard.jsp");
    }
}