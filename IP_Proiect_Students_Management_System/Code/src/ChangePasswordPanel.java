import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Components.Design;
import Components.ImageIconLoader;
import Components.JRoundedPasswordField;
import Components.JRoundedTextField;
import Components.JScaledTextArea;
import Components.Validator;
import net.miginfocom.swing.MigLayout;

public class ChangePasswordPanel implements Subject {
	private ArrayList<Observer> observers;
	private JPanel panel;
	private JLabel titleLabel;
	private JLabel emailLabel;
	private JLabel codeLabel;
	private JLabel newPasswordLabel;
	private JLabel retypePasswordLabel;
	private JLabel lockLabel; 
	private JScaledTextArea info;
	private JRoundedTextField email;
	private JRoundedTextField code;
	private JRoundedPasswordField newPassword;
	private JRoundedPasswordField retypePassword;
	private JButton confirm;
	private JButton cancel;
	private JButton resetButton;
	private SQL dbConnection;
	private int panelWidth = 800;
	private int panelHeight = 480;
	
	public ChangePasswordPanel(Window window) {
		panel = new JPanel();
		panel.setLayout(new MigLayout("insets 20, align 50% 50%"));
		panel.setBackground(Design.customGray);
		observers = new ArrayList<> ();
		addObserver(window);

		initElements();
		addElements();
		cancelListener();
		resetButtonListener();
		confirmListener();
		
		try {
			dbConnection = new SQL(window);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		dbConnection.createConnection();
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public JButton getCancelButton() {
		return cancel;
	}
	
	public Dimension getDimension() {
		return new Dimension(panelWidth, panelHeight);
	}
	
	private void initElements() {
		titleLabel = new JLabel("Change Password ", SwingConstants.CENTER);
		titleLabel.setFont(Design.serifBold26);

		lockLabel = new JLabel("", SwingConstants.CENTER);
		lockLabel.setIcon(ImageIconLoader.getImageIcon(Design.keys, 175, true));

		emailLabel = new JLabel("Email *");
		emailLabel.setFont(Design.tahomaBold16);

		email = new JRoundedTextField(18);
		email.setFont(Design.tahomaPlain16);

		codeLabel = new JLabel("Reset Code *");
		codeLabel.setFont(Design.tahomaBold16);

		code = new JRoundedTextField(18);
		code.setFont(Design.tahomaPlain16);

		newPasswordLabel = new JLabel("New Password *");
		newPasswordLabel.setFont(Design.tahomaBold16);

		newPassword = new JRoundedPasswordField(18);
		newPassword.setFont(Design.tahomaBold16);

		retypePasswordLabel = new JLabel("Retype Password *");
		retypePasswordLabel.setFont(Design.tahomaBold16);

		retypePassword = new JRoundedPasswordField(18);
		retypePassword.setFont(Design.tahomaBold16);

		StringBuilder sb = new StringBuilder();
        sb.append("Enter the code provided by our server via email, ").
           append("then type your new password (minimum 6 characters). ").
           append("We highly recommend you to choose a strong one.");
		info = new JScaledTextArea(sb.toString(), 3, 30);

		confirm = new JButton("Confirm");
		confirm.setIcon(ImageIconLoader.getImageIcon(Design.confirm, 24, true));
		confirm.setHorizontalAlignment(SwingConstants.LEFT);

		cancel = new JButton("Cancel");
		cancel.setIcon(ImageIconLoader.getImageIcon(Design.cancel, 24, true));
		cancel.setHorizontalAlignment(SwingConstants.LEFT);
		
		resetButton = Design.getFbStyleButton("Email reset code to me");
	}
	
	private void addElements() {
		panel.add(Box.createVerticalStrut(15), "wrap");
		panel.add(titleLabel, "spanx2, center, wrap");
		panel.add(Box.createVerticalStrut(15), "wrap");
		panel.add(Box.createVerticalGlue(), "pushy, growy, wrap");
		panel.add(lockLabel, "spany11, center");
		panel.add(emailLabel, "wrap");
		panel.add(email, "split2, pushx, growx");
		panel.add(resetButton, "wrap");
		panel.add(Box.createVerticalStrut(10), "wrap");
		panel.add(codeLabel, "wrap");
		panel.add(code, "wrap, pushx, growx");
		panel.add(Box.createVerticalStrut(10), "wrap");
		panel.add(newPasswordLabel, "wrap");
		panel.add(newPassword, "wrap, pushx, growx");
		panel.add(Box.createVerticalStrut(10), "wrap");
		panel.add(retypePasswordLabel, "wrap");
		panel.add(retypePassword, "wrap, pushx, growx");
		panel.add(Box.createVerticalStrut(10), "wrap");
		panel.add(info, "center");
		
		Box buttonsBox = Box.createHorizontalBox();
		buttonsBox.add(confirm);
		buttonsBox.add(Box.createHorizontalStrut(30));
		buttonsBox.add(cancel);
		panel.add(buttonsBox, "center, wrap");
		panel.add(Box.createVerticalGlue(), "pushy, growy, wrap");
	}

	private void cancelListener() {
		cancel.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {		    	
		    	notifyAllObservers(Main.TABLE_LOGIN);
		    }
		});
	}
	
	private void resetButtonListener() {
		resetButton.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	Vector<String> cols = new Vector<> ();
				Vector<Vector<Object>> res = null;
				boolean emailFound = false;

				cols.add("id");
				cols.add("first_name");
				cols.add("last_name");
				cols.add("email");

				for (int i = 0; i < Main.accountTypes.size(); i++) {
					Main.accountType = Main.accountTypes.get(i);
					res = dbConnection.selectEntries(cols, Main.accountType, "email = '" + email.getText() +"'");
					
					if (!res.isEmpty()) {
						emailFound = true;
						break;
					}
				}

				if (!emailFound) {
					JOptionPane.showMessageDialog(
							null, "Typed email is not associated with any account!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String uid = res.get(0).get(0).toString();
				String first_name = res.get(0).get(1).toString();
				String last_name = res.get(0).get(2).toString();
				String email = res.get(0).get(3).toString();
				String hashed = null;
				String insertTicketQuery = null;

				try {
					hashed = Main.encode(email + "_" + first_name + "_" + last_name + "_" + System.currentTimeMillis());
				} catch (Exception e1) {
					System.out.println(e1);
				}				
				
				insertTicketQuery = "INSERT INTO " + Main.TABLE_TICKETS;
				insertTicketQuery += "(uid, ticket) VALUES('" + uid + "', '" + hashed + "')";

				if (dbConnection.executeQuery(insertTicketQuery) == -1) {
					JOptionPane.showMessageDialog(null, 
							"The server is busy at the moment! Please try later!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String mailSubject = "[Students Management System] Password change request";
				String mailContent = "Hello " + first_name + " " + last_name + ",\n\n" +
									 "You recently requested to reset your password for your " +
									 "Students Management System account. " +
									 "The reset code can be found below.\n\t"+
									 hashed + "\r\n";

				MailSender.send(email, mailSubject, mailContent);
		    }
		});
	}
	
	@SuppressWarnings("deprecation")
	private void confirmListener() {
		confirm.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
		    	if (!retypePassword.getText().equals(newPassword.getText())) {
					JOptionPane.showMessageDialog(null, 
							"Passwords do not match! Please retype!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
					return;
		    	}

		    	if (Validator.shortPassword(newPassword.getText())) {
					JOptionPane.showMessageDialog(null, 
							"Passwords should have at least 6 characters!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
					return;
		    	}

		    	if (Validator.wrongPasswordCharacters(newPassword.getText())) {
					JOptionPane.showMessageDialog(null, 
							"Password contains invalid symbols!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
					return;
		    	}

		    	Vector<Vector<Object>> res;
				Vector<String> cols = new Vector<String>();

				cols.add("a.id");
				cols.add("t.uid");
				cols.add("t.ticket");
				cols.add("t.tid");

				String tableName = Main.accountType + " a, " + Main.TABLE_TICKETS + " t ";
				String condition = "email = '" + email.getText() + 
									"' AND a.id = t.uid AND t.ticket = '" + 
									code.getText() +"' ORDER by t.tid DESC";

				res = dbConnection.selectEntries(cols, tableName, condition);

				if (res.size() == 0) {
					JOptionPane.showMessageDialog(
							null, "Typed email or reset code is invalid!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String deleteTicketQuery = "DELETE FROM " + Main.TABLE_TICKETS +
										   " WHERE tid = '" + res.get(0).get(3) + "'";
				
				String updatePasswordQuery = null;
				try {
					updatePasswordQuery = "UPDATE " + Main.accountType + " SET pwd = '" +
										  Main.encode(newPassword.getText()) +
										  "' WHERE id = '" + res.get(0).get(0) + "'";
				} catch (Exception e1) {
					System.out.println(e1);
				}
				
				dbConnection.executeQuery(deleteTicketQuery);
				dbConnection.executeQuery(updatePasswordQuery);
		    	
				JOptionPane.showMessageDialog(null, 
						"Password changed successfully!",
    					"Success", JOptionPane.INFORMATION_MESSAGE);

		    	notifyAllObservers(Main.TABLE_LOGIN);
		    }
		});
	}

	public void addObserver(Observer o) {
		observers.add(o);
	}

	public void notifyAllObservers(String nextPanelName) {
		for(Observer o : observers)
			o.update(nextPanelName);
	}

	public void notifyAllObservers(String nextPanelName, ArrayList<String> args) {		
	}
}