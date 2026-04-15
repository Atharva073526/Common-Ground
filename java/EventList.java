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

@WebServlet("/getData")
public class EventList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String user= "root";
        String password= "Salvi360@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/new_user",user,password
            );

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, name, level, city, sports, description, userId FROM eventslist");

            JSONArray users = new JSONArray();
            while(rs.next()) {
                JSONObject userx = new JSONObject();
                userx.put("eventId", rs.getInt("id"));
                userx.put("name", rs.getString("name"));
                userx.put("level", rs.getString("level"));
                userx.put("city", rs.getString("city"));
                userx.put("sports", rs.getString("sports"));
                userx.put("description", rs.getString("description"));
                userx.put("adminId", rs.getInt("userId"));
                users.put(userx);
            }
            out.print(users);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.flush();


    }
}
