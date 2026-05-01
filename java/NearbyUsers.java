import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/getData")
public class NearbyUsers extends HttpServlet {

    private static final double EARTH_RADIUS_KM = 6371;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/new_user";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Salvi360@";

    // ===== GET: fetch nearby events =====
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
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

            // Get current user's lat/lng and city
            PreparedStatement userStmt = con.prepareStatement(
                    "SELECT latitude, longitude, city FROM userlist WHERE Email = ?"
            );
            userStmt.setString(1, userEmail);
            ResultSet userRs = userStmt.executeQuery();

            JSONArray events = new JSONArray();

            if (userRs.next()) {
                double userLat = userRs.getDouble("latitude");
                double userLng = userRs.getDouble("longitude");
                String userCity = userRs.getString("city");

                boolean hasLocation = (userLat != 0 && userLng != 0);

                ResultSet eventsRs;

                if(hasLocation) {
                    // Use Haversine formula to find events within 20 km
                    PreparedStatement eventsStmt = con.prepareStatement(
                            "SELECT *, (" + EARTH_RADIUS_KM + " * acos(" +
                                    "cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) " +
                                    "+ sin(radians(?)) * sin(radians(latitude))" +
                                    ")) AS distance_km FROM eventslist " +
                                    "WHERE eventDate >= CURDATE() " +
                                    "AND latitude IS NOT NULL AND longitude IS NOT NULL " +
                                    "HAVING distance_km <= 20 " +
                                    "ORDER BY distance_km ASC"
                    );
                    eventsStmt.setDouble(1, userLat);
                    eventsStmt.setDouble(2, userLng);
                    eventsStmt.setDouble(3, userLat);
                    eventsRs = eventsStmt.executeQuery();
                } else {
                    // Fallback: match by city name
                    PreparedStatement eventsStmt = con.prepareStatement(
                            "SELECT *, 0 AS distance_km FROM eventslist " +
                                    "WHERE eventDate >= CURDATE() AND LOWER(city) = LOWER(?) " +
                                    "ORDER BY eventDate ASC"
                    );
                    eventsStmt.setString(1, userCity != null ? userCity : "");
                    eventsRs = eventsStmt.executeQuery();
                }

                while (eventsRs.next()) {
                    JSONObject event = new JSONObject();
                    event.put("eventId", eventsRs.getInt("id"));
                    event.put("name", eventsRs.getString("name") != null ? eventsRs.getString("name") : "");
                    event.put("sports", eventsRs.getString("sports") != null ? eventsRs.getString("sports") : "");
                    event.put("level", eventsRs.getString("level") != null ? eventsRs.getString("level") : "");
                    event.put("description", eventsRs.getString("description") != null ? eventsRs.getString("description") : "");
                    event.put("city", eventsRs.getString("city") != null ? eventsRs.getString("city") : "");
                    event.put("address", eventsRs.getString("address") != null ? eventsRs.getString("address") : "");
                    event.put("eventDate", eventsRs.getString("eventDate") != null ? eventsRs.getString("eventDate") : "");
                    event.put("eventTime", eventsRs.getString("eventTime") != null ? eventsRs.getString("eventTime") : "");
                    event.put("distance_km", Math.round(eventsRs.getDouble("distance_km") * 10.0) / 10.0);

                    // ✅ FIX: Add adminId to the response
                    int adminId = eventsRs.getInt("userId");
                    if (eventsRs.wasNull()) {
                        adminId = -1;
                    }
                    event.put("adminId", adminId);

                    events.put(event);
                }
                eventsRs.close();
            }

            out.print(events.toString());
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ===== POST: save user's lat/lng to DB =====
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession();
        String userEmail = (String) session.getAttribute("userEmail");

        if (userEmail == null) {
            out.print("{\"success\": false, \"message\": \"Not logged in\"}");
            return;
        }

        String latitude = req.getParameter("latitude");
        String longitude = req.getParameter("longitude");

        if (latitude == null || longitude == null) {
            out.print("{\"success\": false, \"message\": \"Missing location data\"}");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE userlist SET latitude = ?, longitude = ? WHERE Email = ?"
            );
            ps.setDouble(1, Double.parseDouble(latitude));
            ps.setDouble(2, Double.parseDouble(longitude));
            ps.setString(3, userEmail);

            int updated = ps.executeUpdate();
            con.close();

            if (updated > 0) {
                out.print("{\"success\": true}");
            } else {
                out.print("{\"success\": false, \"message\": \"Update failed\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}