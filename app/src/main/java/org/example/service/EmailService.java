package org.example.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${gmail.sender}")
    private String email;


    public void sendForgotPasswordLink(String toEmail, String resetLink,String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(email, "Moneyfy Support");
            helper.setTo(toEmail);
            helper.setSubject("Reset your Password - Moneyfy");

            String content = "<html>" +
                    "  <body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">" +
                    "    <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" " +
                    "           style=\"max-width: 600px; margin: auto; background: #ffffff; border-radius: 10px; " +
                    "                  box-shadow: 0 4px 8px rgba(0,0,0,0.1);\">" +
                    "      <tr>" +
                    "        <td style=\"padding: 30px; text-align: center; background-color: #169905; " +
                    "                   border-radius: 10px 10px 0 0; color: #ffffff;\">" +
                    "          <h1 style=\"margin: 0; font-size: 24px;\">Moneyfy</h1>" +
                    "        </td>" +
                    "      </tr>" +
                    "      <tr>" +
                    "        <td style=\"padding: 30px; color: #333333;\">" +
                    "          <h2 style=\"margin-top: 0;\">Hello " + firstName + ",</h2>" +
                    "          <p style=\"font-size: 16px; line-height: 1.6;\">" +
                    "            We received a request to reset your password for your <b>Moneyfy</b> account." +
                    "          </p>" +
                    "          <p style=\"font-size: 16px; line-height: 1.6;\">" +
                    "            Click the button below to set a new password:" +
                    "          </p>" +
                    "          <p style=\"text-align: center; margin: 30px 0;\">" +
                    "            <a href=\"" + resetLink + "\" " +
                    "               style=\"background-color: #169905; color: #ffffff; text-decoration: none; " +
                    "                      padding: 12px 25px; font-size: 16px; border-radius: 6px; display: inline-block;\">" +
                    "              Reset Password" +
                    "            </a>" +
                    "          </p>" +
                    "          <p style=\"font-size: 14px; color: #666666;\">" +
                    "            If you didn’t request this, you can safely ignore this email." +
                    "          </p>" +
                    "          <p style=\"font-size: 14px; color: #666666; margin-top: 40px;\">" +
                    "            Thanks,<br>The Moneyfy Team" +
                    "          </p>" +
                    "        </td>" +
                    "      </tr>" +
                    "      <tr>" +
                    "        <td style=\"padding: 20px; text-align: center; font-size: 12px; color: #999999; " +
                    "                   background-color: #f4f4f4; border-radius: 0 0 10px 10px;\">" +
                    "          &copy; 2025 Moneyfy. All rights reserved." +
                    "        </td>" +
                    "      </tr>" +
                    "    </table>" +
                    "  </body>" +
                    "</html>";

            helper.setText(content, true);

            mailSender.send(message);

            System.out.println("✅ Reset password email sent to: " + toEmail);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
