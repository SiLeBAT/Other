package de.bund.bfr.busstopp.util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import de.bund.bfr.busstopp.Constants;

public class SendEmail {

   public void doSend(String messageText, String filePath) {    
      // Recipient's email ID needs to be mentioned.
      String to = "armin.weiser@bfr.bund.de";

      // Sender's email ID needs to be mentioned
      String from = "admin@busstop.foodrisklabs.bfr.bund.de";

      // Assuming you are sending email from localhost
      String host = "localhost";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try {
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         // Set Subject: header field
         message.setSubject("Busstop validation of new xml upload - " + getFQDN());//(Constants.IS_TEST ? "Testsystem" : "Produktivsystem"));

         // Create the message part 
         BodyPart messageBodyPart = new MimeBodyPart();

         // Fill the message
         messageBodyPart.setText(messageText);
         
         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         // Part two is attachment
         File f = new File(filePath);
         messageBodyPart = new MimeBodyPart();
         DataSource source = new FileDataSource(f);
         messageBodyPart.setDataHandler(new DataHandler(source));
         messageBodyPart.setFileName(f.getName());
         multipart.addBodyPart(messageBodyPart);

         // Send the complete message parts
         message.setContent(multipart );
         // Now set the actual message
         //message.setText(messageText);

         // Send message
         Transport.send(message);
         System.out.println("Sent message successfully....");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
   private String getFQDN() {
	   String result = null;
	   try {
		result = InetAddress.getLocalHost().toString();
	} catch (UnknownHostException e) {
	}
	   return result;
   }
}