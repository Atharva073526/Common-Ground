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

@WebServlet("/signup")
public class SignupPage extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String role     = req.getParameter("signup-role");
        String fname    = req.getParameter("signup-fullname");
        String email    = req.getParameter("signup-email");
        String password = req.getParameter("signup-pass");
        String address  = req.getParameter("signup-address");
        String city     = req.getParameter("signup-city");
        String pincode  = req.getParameter("signup-pincode");

        String url  = "jdbc:mysql://localhost:3306/new_user";
        String user = "root";
        String pass = "Salvi360@";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            // Check if user already exists
            PreparedStatement checkStmt = con.prepareStatement("SELECT * FROM userlist WHERE Email = ?");
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                resp.sendRedirect("common-ground-simple.html?error=exists");
            } else {
                // Insert new user
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO userlist (Fname, Email, Password, role, address, city, pincode, level) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                );
                ps.setString(1, fname);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.setString(4, role);
                ps.setString(5, address);
                ps.setString(6, city);
                ps.setString(7, pincode);
                ps.setString(8, "Beginner");

                int result = ps.executeUpdate();
                ps.close();

                if (result > 0) {
                    // ✅ FIXED: Get the actual user we just inserted
                    PreparedStatement getUserStmt = con.prepareStatement("SELECT * FROM userlist WHERE Email = ?");
                    getUserStmt.setString(1, email);
                    ResultSet userRs = getUserStmt.executeQuery();

                    if (userRs.next()) {
                        HttpSession session = req.getSession();
                        session.setAttribute("userId", userRs.getInt("id"));
                        session.setAttribute("userEmail", email);
                        session.setAttribute("userRole", role);
                        session.setAttribute("userFname", fname);
                        session.setAttribute("userCity", city);
                        session.setAttribute("userLevel", "Beginner");

                        getUserStmt.close();
                        userRs.close();
                        resp.sendRedirect("dashboard.jsp");
                    } else {
                        resp.sendRedirect("common-ground-simple.html?error=db");
                    }
                } else {
                    resp.sendRedirect("common-ground-simple.html?error=db");
                }
            }

            checkStmt.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("common-ground-simple.html?error=db");
        }
    }
}