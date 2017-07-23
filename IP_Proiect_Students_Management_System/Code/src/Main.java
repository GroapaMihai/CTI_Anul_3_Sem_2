import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import Components.Utils;

public class Main {
	public static String userID;
	public static String userName;
	public static String accountType;

	public static String NO_INTERNET = "NO_INTERNET";
	public static String TABLE_SERVER_ADMINS = "Server_Admins";
	public static String TABLE_LOGIN = "Login";
	public static String TABLE_CHANGE_PWD = "Change Password";
	public static String TABLE_UNIVERSITIES = "Universities";
	public static String TABLE_ADMINS = "Admins";
	public static String TABLE_TICKETS = "Tickets";
	public static String TABLE_FACULTIES = "Faculties";
	public static String TABLE_DEPARTMENTS = "Departments";
	public static String TABLE_SECRETARIES = "Secretaries";
	public static String TABLE_COURSES = "Courses";
	public static String TABLE_GROUPS = "Groups";
	public static String TABLE_STUDENTS = "Students";
	public static String TABLE_GRADES = "Grades";
	public static String STUDENT_PROFILE = "Student Profile";

	public static String TABLE_DEPARTMENTS_QUERY = "SELECT " +
			"* FROM " + TABLE_DEPARTMENTS;

	public static String TABLE_FACULTIES_QUERY = "SELECT " +
			"id, name, address, phone, profile, admin_id, logo" + 
			" FROM " + TABLE_FACULTIES;

	public static String TABLE_UNIVERSITIES_QUERY = "SELECT " +
			"* FROM " + TABLE_UNIVERSITIES;

	public static String TABLE_ADMINS_QUERY = "SELECT " +
			"id, first_name, last_name, birth_date, nationality, " +
			"email, academic_rank, phone, address, image "+
			"FROM " + TABLE_ADMINS;

	public static String TABLE_SECRETARIES_QUERY = "SELECT " +
			"id, first_name, last_name, birth_date, nationality, " +
			"email, year, phone, address, image "+
			"FROM " + TABLE_SECRETARIES;
	
	public static String TABLE_COURSES_QUERY = "SELECT " +
			"id, name, semester, teacher, credits, type " +
			"FROM " + TABLE_COURSES;
	
	public static String TABLE_STUDENTS_QUERY = "SELECT id, first_name, last_name, " +
			"father_initials, birth_date, gender, nationality, birth_country, birth_city, " +
			"father_first_name, father_last_name, mother_first_name, mother_last_name, marital_status, " +
			"current_country, current_city, street, apt_number, postal_code, iban, bank_name, " +
			"admission_year, admission_type, admission_grade, taxis, email, phone, group_id, " +
			"image FROM " + TABLE_STUDENTS;

	public static Vector<String> accountTypes;

	public static String encode(String data) throws Exception {
	    if (data == null || data.isEmpty())
	        throw new IllegalArgumentException("Null value provided for " + "MD5 Encoding");

	    MessageDigest m = MessageDigest.getInstance("MD5");
	    m.update(data.getBytes(), 0, data.length());
	    String digest = String.format("%032x", new BigInteger(1, m.digest()));

	    return digest;
	}

	public static String getAccountTypeName() {
		switch (accountType) {
			case "Server_Admins" : return "Server Admin";
			case "Admins" : return "Administrator";
			case "Secretaries" : return "Secretary";
			case "Students" : return "Student";
		}

		return null;
	}

	private static void createHomeDirectory() {
		File home = new File(Utils.homePath);

		if (!home.exists()) {
		    try {
		    	home.mkdir();
		    } catch(SecurityException se) {
		    	System.out.println(se);
		    }        
		}
	}

	public static void main(String[] args) {
		accountTypes = new Vector<> ();
		accountTypes.add(Main.TABLE_SERVER_ADMINS);
		accountTypes.add(Main.TABLE_ADMINS);
		accountTypes.addElement(Main.TABLE_SECRETARIES);
		accountTypes.addElement(Main.TABLE_STUDENTS);

		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
			System.out.println(e);
		}

		createHomeDirectory();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Window();
			}
		});
	}
}