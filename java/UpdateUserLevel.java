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

@WebServlet("/updateUserLevel")
public class UpdateUserLevel extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/new_user";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Salvi360@";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession();
        String userEmail = (String) session.getAttribute("userEmail");
        String newLevel = req.getParameter("level");

        JSONObject response = new JSONObject();

        if (userEmail == null) {
            response.put("success", false);
            response.put("message", "Not logged in");
            out.print(response.toString());
            return;
        }

        if (newLevel == null || newLevel.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid level");
            out.print(response.toString());
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE userlist SET level = ? WHERE Email = ?"
            );
            ps.setString(1, newLevel);
            ps.setString(2, userEmail);

            int updated = ps.executeUpdate();
            con.close();

            if (updated > 0) {
                // Update session attribute
                session.setAttribute("userLevel", newLevel);

                response.put("success", true);
                response.put("message", "Level updated successfully");
            } else {
                response.put("success", false);
                response.put("message", "Update failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        out.print(response.toString());
    }
}