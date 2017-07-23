package Components;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;

public class Design {
	public static final String lockedAccount = "LockedAccount.png";
	public static final String confirm = "Confirm.png";
	public static final String reset = "Reset.png";
	public static final String keys = "Keys.png";
	public static final String cancel = "Cancel.png";
	public static final String genericUniversity = "GenericUniversity.png";
	public static final String genericAdmin = "GenericAdmin.png";
	public static final String genericFaculty= "GenericFaculty.png";
	public static final String genericSecretary= "GenericSecretary.png";
	public static final String genericStudent= "GenericStudent.png";
	public static final String annualPlans= "AnnualPlans.png";
	public static final String semesterPlans= "SemesterPlans.png";
	public static final String logout = "Logout.png";
	public static final String browse = "Browse.png";
	public static final String add = "Add.png";
	public static final String delete = "Delete.png";
	public static final String update = "Update.png";
	public static final String search = "Search.png";
	public static final String back = "Back.png";
	public static final String select = "Select.png";
	public static final String viewContract = "ViewContract.png";
	public static final String save = "Save.png";

	public static final Color customGray = new Color(214, 217, 223);
	public static final Color tangerine = new Color(237, 137, 52);
	public static final Color mustard = new Color(239, 190, 34);
	public static final Color papaya = new Color(239, 102, 83);
	public static final Color teal = new Color(1, 153, 177);
	public static final Color mountain = new Color(81, 66, 95);
	public static final Color scrub = new Color(186, 164, 106);
	public static final Color rust = new Color(198, 121, 105);
	public static final Color mint = new Color(192, 224, 203);
	public static final Color fbBlue = new Color(59, 89, 182);

	public static final Font tahomaBold12 = new Font("Tahoma", Font.BOLD, 12);
	public static final Font tahomaBold14 = new Font("Tahoma", Font.BOLD, 14);
	public static final Font tahomaPlain14 = new Font("Tahoma", Font.PLAIN, 14);
	public static final Font tahomaBold16 = new Font("Tahoma", Font.BOLD, 16);
	public static final Font tahomaPlain16 = new Font("Tahoma", Font.PLAIN, 16);
	public static final Font tahomaBold18 = new Font("Tahoma", Font.BOLD, 18);
	public static final Font serifBold26 = new Font("Serif", Font.BOLD, 26);

	public static JButton getFbStyleButton(String text) {
		JButton button = new JButton(text);

		button.setBackground(fbBlue);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setFont(tahomaBold12);

		return button;
	}

	public static JLabel getCustomJLabel(String text, Font font) {
		return getCustomJLabel(text, font, Color.BLACK);
	}

	public static JLabel getCustomJLabel(String text, Font font, Color color) {
		JLabel label = new JLabel(text);

		label.setFont(font);
		label.setForeground(color);

		return label;
	}

	public static JRoundedTextField getImmutableTextField(int size, Font font) {
		JRoundedTextField field = new JRoundedTextField(size);

		field.setEditable(false);
		field.setFocusable(false);
		field.setFont(font);

		return field;
	}
}
