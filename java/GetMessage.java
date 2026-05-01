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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@WebServlet("/GetMessages")
public class GetMessage extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // ✅ FIX 1: Add validation for eventId parameter
        String eventId = req.getParameter("eventId");

        if (eventId == null || eventId.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            resp.getWriter().write("[]");
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8"); // ✅ FIX 2: Add character encoding

        String url = "jdbc:mysql://localhost:3306/new_user";
        String user = "root";
        String pass = "Salvi360@";

        // ✅ FIX 3: Move Driver loading inside try-catch
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Database driver not found\"}");
            return;
        }

        // ✅ FIX 4: Parse eventId with try-catch
        int eventIdInt;
        try {
            eventIdInt = Integer.parseInt(eventId);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("[]");
            return;
        }

        // ✅ FIX 5: Use try-with-resources (already good)
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT senderName, comment, time, role FROM message WHERE eventId = ? ORDER BY messageId ASC"
             )) {

            ps.setInt(1, eventIdInt);
            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("time");
                String formattedTime = (ts != null) ? timeFormat.format(ts) : "";

                String role = rs.getString("role");
                role = escapeJson(role);

                // ✅ FIX 6: Escape special characters in JSON to prevent injection
                String senderName = rs.getString("senderName");
                String comment = rs.getString("comment");

                // Escape JSON strings
                senderName = escapeJson(senderName);
                comment = escapeJson(comment);

                json.append(String.format(
                        "{\"sender\":\"%s\", \"text\":\"%s\", \"time\":\"%s\", \"role\":\"%s\"},",
                        senderName,
                        comment,
                        formattedTime,
                        role
                ));

            }

            if (json.length() > 1) {
                json.setLength(json.length() - 1); // Remove trailing comma
            }
            json.append("]");

            resp.getWriter().write(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Failed to load messages\"}");
        }
    }

    // ✅ FIX 7: Helper method to escape JSON strings
    private String escapeJson(String input) {
        if (input == null) return "";

        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}