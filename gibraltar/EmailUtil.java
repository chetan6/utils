package gibraltar;

import java.io.File;
import java.util.Properties;

import javax.mail.Message; 
import javax.mail.Multipart;
import javax.mail.Session; 
import javax.mail.Transport; 
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart;

public class EmailUtil {

	public static void main(String [] a) {
		EmailUtil.sendEmail("chetan.shah@protiviti.com", "C:\\code\\gibraltar\\deliverables\\tm\\thresholdValues\\exceptions\\", "Wires In.xlsx", "Test Message");
		
	}
	
	public static void sendEmail(String to, String folder, String fileToAttach, String briefMessage){
        Properties props = new Properties(); 
        props.put("mail.smtp.user", "chetan.shah@protiviti.com");
        props.put("mail.smtp.host", "casarray.corp.rhalf.com");
        
        Session session = Session.getDefaultInstance(props, null); 
              
        try { 
            Message msg = new MimeMessage(session); 
            msg.setFrom(new InternetAddress("chetan.shah@protiviti.com", "Chetan Shah"));
            InternetAddress [] addresses = new InternetAddress[1];
            addresses[0] = new InternetAddress(to);
            //msg.setReplyTo(addresses);
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, ""));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("thomas.dessalet@protiviti.com", "Tom"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress("nicole.pfeil@protiviti.com", "Nicole"));
            msg.addRecipient(Message.RecipientType.CC, new InternetAddress("chetan.shah@protiviti.com", "Chetan"));
            msg.setSubject("Your Kraken Request: " + fileToAttach + " has been completed"); 

            
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            Multipart multipart = new MimeMultipart();

            messageBodyPart = new MimeBodyPart();
            File att = new File(new File(folder), fileToAttach);
            messageBodyPart.attachFile(att);
            
            MimeBodyPart text = new MimeBodyPart();
            text.setText(briefMessage);
            
            
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(text);
            
            msg.setContent(multipart);            
            
            
            Transport.send(msg); 
     
        } catch (Exception e) { 
          e.printStackTrace();       	
        } 
	}
	
}
