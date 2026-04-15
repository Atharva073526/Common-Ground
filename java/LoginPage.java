
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
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginPage extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String role     = req.getParameter("role");
        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        String url  = "jdbc:mysql://localhost:3306/new_user";
        String user = "root";
        String pass = "Salvi360@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            PreparedStatement ps = con.prepareStatement("SELECT * FROM userlist WHERE role=? AND Email=? AND Password=?");
            ps.setString(1, role);
            ps.setString(2, email);
            ps.setString(3, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                HttpSession session = req.getSession();
                int id = rs.getInt("id");
                session.setAttribute("userId", id);

                session.setAttribute("userEmail", email);
                session.setAttribute("userRole", role);
                session.setAttribute("userFname", rs.getString("Fname"));
                session.setAttribute("userCity", rs.getString("city"));
                session.setAttribute("userLevel", rs.getString("level") != null ? rs.getString("level") : "Beginner");

                resp.sendRedirect("dashboard.jsp");
            } else {
                resp.sendRedirect("common-ground-simple.html?error=1");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
