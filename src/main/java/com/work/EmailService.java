package com.work;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	 @Autowired
	    private JavaMailSender mailSender;
	 

	    public void sendRegistrationEmail(String toEmail, String name, String designation) {
	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);
	            helper.setFrom("TaskMatrix Team <sathishn1503@gmail.com>");
	            helper.setTo(toEmail);
	            helper.setSubject("🎉 Registration Successful!");

	            String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
	                    "<h2 style='color: #2E86C1;'>Welcome, " + name + "!</h2>" +
	                    "<p>You have <strong>successfully registered</strong> as a <strong style='color: #28a745;'>" + designation + "</strong>.</p>" +
	                    "<p>Thank you for joining us.</p>" +
	                    "<hr>" +
	                    "<p style='font-size: 12px; color: gray;'>This is an automated email. Please do not reply.</p>" +
	                    "</body></html>";

	            helper.setText(htmlContent, true); // true enables HTML

	            mailSender.send(message);
	        } catch (MessagingException e) {
	            e.printStackTrace();
	        }
	    }

}
