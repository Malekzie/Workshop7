package com.sait.peelin.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sait.peelin.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WelcomeEmailService {

    @Value("${app.sendgrid.api-key:}")
    private String sendGridApiKey;

    @Value("${app.sendgrid.from-email:}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public void sendWelcomeEmail(User user) {
        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            System.out.println("SendGrid not configured, skipping welcome email for: " + user.getUserEmail());
            return;
        }

        String htmlBody = """
                <div style="font-family: sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #703210;">Welcome to Peelin' Good!</h2>
                    <p>Hi %s,</p>
                    <p>Your account has been created successfully. We're thrilled to have you!</p>
                    <a href="%s/menu" style="display: inline-block; background-color: #703210; color: white; padding: 12px 24px; border-radius: 999px; text-decoration: none; font-weight: bold; margin: 16px 0;">
                        Browse Our Menu
                    </a>
                    <p>If you have any questions, feel free to reach out to us.</p>
                    <p style="color: #999; font-size: 12px;">You're receiving this because you created an account at Peelin' Good Bakery.</p>
                </div>
                """.formatted(user.getUsername(), frontendUrl);

        Email from = new Email(fromEmail, "Peelin' Good Bakery");
        Email to = new Email(user.getUserEmail());
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, "Welcome to Peelin' Good!", to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Welcome email sent, status: " + response.getStatusCode());
        } catch (IOException e) {
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
}