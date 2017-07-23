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

import net.miginfocom.swing.MigLayout;

public class LoginPanel implements Subject {
	private ArrayList<Observer> observers;
	private JPanel panel;
	private JPanel infoPanel;
	private JLabel titleLabel;
	private JLabel lockLabel;
	private JLabel messageLabel;
	private JLabel emailLabel;
	private JLabel passwordLabel;
	private JRoundedTextField email;
	private JRoundedPasswordField password;
	private JButton changePassword;
	private JButton login;
	private JButton reset;
	private SQL dbConnection;
	private int panelWidth = 600;
	private int panelHeight = 400;
	
	public LoginPanel(Window window) {
		panel = new JPanel();
		panel.setLayout(new MigLayout("insets 20, align 50% 50%"));
		panel.setBackground(Design.customGray);
		observers = new ArrayList<> ();
		addObserver(window);
		
		initElements();
		addElements();
		
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
	
	public Dimension getDimension() {
		return new Dimension(panelWidth, panelHeight);
	}
	
	private void initElements() {
		titleLabel = new JLabel("Students Management System", SwingConstants.CENTER);
		titleLabel.setFont(Design.serifBold26);

		lockLabel = new JLabel();
		lockLabel.setIcon(ImageIconLoader.getImageIcon(Design.lockedAccount, 175, true));

		messageLabel = new JLabel("Enter your email and password:");
		messageLabel.setFont(Design.tahomaBold18);

		emailLabel = new JLabel("Email       ");
		emailLabel.setFont(Design.tahomaBold16);

		email = new JRoundedTextField(18);
		email.setFont(Design.tahomaPlain16);

		passwordLabel = new JLabel("Password");
		passwordLabel.setFont(Design.tahomaBold16);

		password = new JRoundedPasswordField(18);
		password.setFont(Design.tahomaBold16);
		
		changePassword = Design.getFbStyleButton("Change Password");
		changePassword.setFont(Design.tahomaBold16);
		changePasswordActionListener();
		
		login = new JButton("Log In");
		login.setIcon(ImageIconLoader.getImageIcon(Design.confirm, 24, true));
		login.setHorizontalAlignment(SwingConstants.LEFT);
		loginActionListener();
		
		reset = new JButton("Reset");
		reset.setIcon(ImageIconLoader.getImageIcon(Design.reset, 24, true));
		reset.setHorizontalAlignment(SwingConstants.LEFT);
		resetActionListener();
		
		infoPanel = new JPanel();
		infoPanel.setLayout(new MigLayout("insets 20, align 50% 50%"));
		infoPanel.setBackground(Design.customGray);
	}

	private void addElements() {
		panel.add(titleLabel, "spanx3, center, wrap");
		panel.add(Box.createVerticalStrut(15), "wrap");
		panel.add(Box.createVerticalGlue(), "pushy, growy, wrap");
		
		infoPanel.add(messageLabel, "spanx2, center, wrap");
		infoPanel.add(Box.createVerticalStrut(20), "wrap");
		infoPanel.add(emailLabel, "split2");
		infoPanel.add(email, "pushx, growx, wrap");
		infoPanel.add(Box.createVerticalStrut(15), "wrap");
		infoPanel.add(passwordLabel, "split2");
		infoPanel.add(password, "pushx, growx, wrap");

		panel.add(lockLabel, "center");
		panel.add(Box.createHorizontalStrut(10));
		panel.add(infoPanel, "pushx, growx, wrap");
		
		panel.add(Box.createVerticalStrut(5), "wrap");
		panel.add(changePassword, "center");

		panel.add(login, "skip, center, split3");
		panel.add(Box.createHorizontalStrut(20));
		panel.add(reset, "wrap");
		panel.add(Box.createVerticalGlue(), "pushy, growy, wrap");
	}

	@SuppressWarnings("deprecation")
	public boolean loginSuccess() {
		Vector<Vector<Object>> res = null;
		Vector<String> cols = new Vector<> ();
		String condition = "email = '" + email.getText() + "'";
		boolean emailFound = false;

		cols.add("id");
		cols.add("first_name");
		cols.add("last_name");
		cols.add("pwd");
		
		for (int i = 0; i < Main.accountTypes.size(); i++) {
			Main.accountType = Main.accountTypes.get(i);
			res = dbConnection.selectEntries(cols, Main.accountType, condition);
			
			if (!res.isEmpty()) {
				emailFound = true;
				break;
			}
		}

		if (!emailFound) 
			return false;
		
		Main.userID = res.get(0).get(0).toString();
		Main.userName = res.get(0).get(1).toString() +
			" " + res.get(0).get(2).toString();
		String real_password = res.get(0).get(3).toString();
		String typed_password;
		
		try {
			typed_password = Main.encode(password.getText());
		} catch (Exception e) {
			return false;
		}
		
		if (real_password.equals(typed_password))
			return true;
		
		return false;
	}
	
	public void clearFields() {
		email.setText("");
		password.setText("");
	}

	private void changePasswordActionListener() {
		changePassword.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
    			notifyAllObservers(Main.TABLE_CHANGE_PWD);
			}
		});
	}

	private void loginActionListener() {
		login.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	if(loginSuccess()) {
		    		JOptionPane.showMessageDialog(null, "You have been successfully logged in!",
	    					"Success", JOptionPane.INFORMATION_MESSAGE);

		    		if (Main.accountType.equals(Main.TABLE_SERVER_ADMINS)) {
		    			ArrayList<String> args = new ArrayList<> ();
		    			args.add("craaap");
		    			args.add("craaap");
		    			notifyAllObservers(Main.TABLE_UNIVERSITIES, args);
		    		} else if (Main.accountType.equals(Main.TABLE_ADMINS)) {
		    			String condition = "id = " + Main.userID;
		    			String univID = dbConnection.getOneValue(Main.TABLE_ADMINS, "univ_id", condition).toString();
		    			condition = "id = " + univID;
		    			String univName = dbConnection.getOneValue(Main.TABLE_UNIVERSITIES, "name", condition).toString();

		    			ArrayList<String> args = new ArrayList<> ();
		    			args.add(univID);
		    			args.add(univName);
		    			notifyAllObservers(Main.TABLE_FACULTIES, args);
		    		} else if (Main.accountType.equals(Main.TABLE_SECRETARIES)) {
		    			String condition;

		    			condition = "id = " + Main.userID;
		    			String deptID = dbConnection.getOneValue(Main.TABLE_SECRETARIES, "dept_id", condition).toString();
		    			condition = "id=" + deptID;
		    			String deptName = dbConnection.getOneValue(Main.TABLE_DEPARTMENTS, "name", condition).toString();
		    			condition = "id = " + Main.userID;
		    			String year = dbConnection.getOneValue(Main.TABLE_SECRETARIES, "year", condition).toString();

		    			ArrayList<String> args = new ArrayList<> ();
		    			args.add(deptID);
		    			args.add(deptName);
		    			args.add(year);
		    			notifyAllObservers(Main.TABLE_STUDENTS, args);
		    		} else if (Main.accountType.equals(Main.TABLE_STUDENTS)) {
		    			Vector<Vector<Object>> res = new Vector<> ();
		    			Vector<String> columns = new Vector<> ();
		    			columns.add("dept_id");
		    			columns.addElement("year");
		    			
		    			String condition = "id=" + Main.userID;
		    			res = dbConnection.selectEntries(columns, Main.TABLE_STUDENTS, condition);
		    			
		    			if (res.isEmpty())
		    				return;

		    			ArrayList<String> args = new ArrayList<> ();
		    			args.add(res.get(0).get(0).toString());
		    			args.add(res.get(0).get(1).toString());

		    			notifyAllObservers(Main.STUDENT_PROFILE, args);
		    		}
		    	} else
		    		JOptionPane.showMessageDialog(null, "Wrong username or password!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
			    
		    	clearFields();
		    }
		});
	}
	
	private void resetActionListener() {
		reset.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	clearFields();
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
		for(Observer o : observers)
			o.update(nextPanelName, args);
	}
}