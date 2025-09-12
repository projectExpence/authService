package org.example.auth;

import org.example.service.GmailOAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import java.util.Properties;

@Configuration
public class MailConfig {
    @Value("${gmail.sender}")
    private String email;
    private final GmailOAuthService gmailOAuth2Service;

    public MailConfig(GmailOAuthService gmailOAuthService){
        this.gmailOAuth2Service = gmailOAuthService;
    }
    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(email);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String accessToken = null; // refresh from Google
                try {
                    accessToken = gmailOAuth2Service.getAccessToken();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return new PasswordAuthentication(email, accessToken);
            }
        });
        mailSender.setSession(session);

        return mailSender;
    }
}
