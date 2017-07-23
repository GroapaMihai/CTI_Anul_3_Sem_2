import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.JOptionPane;
 
public class MailSender {
    private static String username = "gerogesorosheadquarters@gmail.com";
    private static String password = "AaBbCc123";
    private static String host = "smtp.gmail.com";
    private static String port = "465";
 
    public static void send(String dest, String subject, String text) {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.socketFactory.port", port);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", port);

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dest));
			message.setSubject(subject);
			message.setText(text);
			Transport.send(message);

		} catch (MessagingException e) {
			JOptionPane.showMessageDialog(
					null, "We couldn't provide you a password reset code. Try again later!",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}