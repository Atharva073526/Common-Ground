import com.mysql.cj.jdbc.Driver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/getAllEvents")
public class EventList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String user = "root";
        String password = "Salvi360@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/new_user", user, password
            );

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name, level, city, sports, description, userId, eventDate, eventTime, address FROM eventslist");

            JSONArray users = new JSONArray();
            while (rs.next()) {
                JSONObject userx = new JSONObject();
                userx.put("eventId", rs.getInt("id"));
                userx.put("name", rs.getString("name") != null ? rs.getString("name") : "");
                userx.put("level", rs.getString("level") != null ? rs.getString("level") : "");
                userx.put("city", rs.getString("city") != null ? rs.getString("city") : "");
                userx.put("sports", rs.getString("sports") != null ? rs.getString("sports") : "");
                userx.put("description", rs.getString("description") != null ? rs.getString("description") : "");

                // ✅ FIX: Ensure adminId is a valid number, default to -1 if NULL
                int adminId = rs.getInt("userId");
                if (rs.wasNull()) {
                    adminId = -1;  // Invalid adminId - will show error or disable connect button
                }
                userx.put("adminId", adminId);

                userx.put("distance_km", 0);
                userx.put("eventDate", rs.getString("eventDate") != null ? rs.getString("eventDate") : "");
                userx.put("eventTime", rs.getString("eventTime") != null ? rs.getString("eventTime") : "");
                userx.put("address", rs.getString("address") != null ? rs.getString("address") : "");

                users.put(userx);
            }
            out.print(users);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");
        }
        out.flush();
    }
}