import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.activation.*;

import java.io.InputStream;
import java.util.Properties;

import java.io.IOException;

@WebServlet("/SendEmailServlet")
@MultipartConfig   // required for file upload
public class SendEmailServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userEmail = request.getParameter("email");
        String userMessage = request.getParameter("message");
        String userPassword = request.getParameter("password");

        jakarta.servlet.http.Part filePart = request.getPart("file");

        final String username = "siddhisalvi360@student.sfit.ac.in";
        final String password = "hsaf kkgb lwzk fygc"; // NOT your real password

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(username)
            );

            // Reply goes to user
            message.setReplyTo(new Address[]{
                    new InternetAddress(userEmail)
            });

            message.setSubject("New Form Submission");

            // Create multipart
            Multipart multipart = new MimeMultipart();

            // 📝 Text part
            MimeBodyPart textPart = new MimeBodyPart();

            if(userPassword != null && !userPassword.trim().isEmpty()) {
                textPart.setText("User Email: " + userEmail + "\nMessage: " + userMessage + "\nNew Password: "+userPassword);
            }

            else {
                textPart.setText("User Email: " + userEmail + "\nMessage: " + userMessage);
            }

            MimeBodyPart filePartMime = new MimeBodyPart();

            InputStream is = filePart.getInputStream();

            filePartMime.setDataHandler(
                    new DataHandler(new ByteArrayDataSource(is, filePart.getContentType()))
            );

            filePartMime.setFileName(filePart.getSubmittedFileName());

            multipart.addBodyPart(textPart);
            multipart.addBodyPart(filePartMime);

            message.setContent(multipart);


            // Send mail
            Transport.send(message);

            response.sendRedirect("common-ground-simple.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Failed to send email.");
        }

    }
}
