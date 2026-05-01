import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/getStats")
public class GetStats extends HttpServlet {

    private static final double EARTH_RADIUS_KM = 6371;
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/new_user";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Salvi360@";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail == null) {
            out.print("{\"error\": \"Not logged in\"}");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // ===== 1. Get user's lat/lng and city =====
            PreparedStatement userStmt = con.prepareStatement(
                    "SELECT latitude, longitude, city FROM userlist WHERE Email = ?"
            );
            userStmt.setString(1, userEmail);
            ResultSet userRs = userStmt.executeQuery();

            int nearbyMatches = 0;

            if (userRs.next()) {
                double lat = userRs.getDouble("latitude");
                double lng = userRs.getDouble("longitude");
                String city = userRs.getString("city");

                boolean hasLocation = (lat != 0 && lng != 0);

                if (hasLocation) {
                    // Count nearby events within 20 km using Haversine
                    PreparedStatement nearbyStmt = con.prepareStatement(
                            "SELECT COUNT(*) AS count FROM eventslist " +
                                    "WHERE eventDate >= CURDATE() " +
                                    "AND latitude IS NOT NULL AND longitude IS NOT NULL " +
                                    "AND (" + EARTH_RADIUS_KM + " * acos(" +
                                    "cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) " +
                                    "+ sin(radians(?)) * sin(radians(latitude))" +
                                    ")) <= 20"
                    );
                    nearbyStmt.setDouble(1, lat);
                    nearbyStmt.setDouble(2, lng);
                    nearbyStmt.setDouble(3, lat);
                    ResultSet nearbyRs = nearbyStmt.executeQuery();
                    if (nearbyRs.next()) nearbyMatches = nearbyRs.getInt("count");
                    nearbyRs.close();
                    nearbyStmt.close();

                } else if (city != null) {
                    // Fallback: count by city
                    PreparedStatement cityStmt = con.prepareStatement(
                            "SELECT COUNT(*) AS count FROM eventslist " +
                                    "WHERE eventDate >= CURDATE() AND LOWER(city) = LOWER(?)"
                    );
                    cityStmt.setString(1, city);
                    ResultSet cityRs = cityStmt.executeQuery();
                    if (cityRs.next()) nearbyMatches = cityRs.getInt("count");
                    cityRs.close();
                    cityStmt.close();
                }
            }
            userRs.close();
            userStmt.close();

            // ===== 2. Upcoming games count =====
            PreparedStatement upcomingStmt = con.prepareStatement(
                    "SELECT COUNT(*) AS count FROM eventslist WHERE eventDate >= CURDATE()"
            );
            ResultSet upcomingRs = upcomingStmt.executeQuery();
            int upcomingGames = 0;
            if (upcomingRs.next()) upcomingGames = upcomingRs.getInt("count");
            upcomingRs.close();
            upcomingStmt.close();

            con.close();

            JSONObject response = new JSONObject();
            response.put("nearbyMatches", nearbyMatches);
            response.put("upcomingGames", upcomingGames);
            out.print(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
