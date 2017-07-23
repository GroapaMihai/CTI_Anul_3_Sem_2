import Components.DateLabelFormatter;
import Components.Design;
import Components.ImageIconLoader;
import Components.JNumberTextField;
import Components.JRoundedPanel;
import Components.JRoundedTextField;
import Components.JScaledTextPane;
import Components.Utils;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import net.miginfocom.swing.MigLayout;

public class StudentProfilePanel extends BasicPanel {
	private JScrollPane scrollPane;
	private AccountInfoPanel accInfoPanel;
	private IdentityPanel idPanel;
	private FamillyPanel famPanel;
	private AddressPanel addressPanel;
	private ScholarshipPanel schoolPanel;
    private JScaledTextPane title;
    private JScaledTextPane subTitle;
    private ArrayList<JRoundedPanel> allSemestersPanels;
    private String deptID;
    private String year;
    private int toRemoveElements;

    private JLabel studentImage;
    private ImageIcon genericStudent;

    public StudentProfilePanel(Window window, ArrayList<String> args) {
        super(window);
        panel.setBackground(Design.rust);
        panelWidth = 950;
        panelHeight = 720;
        this.deptID = args.get(0);
        this.year = args.get(1);
        toRemoveElements = 0;

        scrollPane = new JScrollPane(null, 20, 30);
        scrollPane.setViewportView(panel);

        title = new JScaledTextPane("Serif", 22, Color.BLACK);
        subTitle = new JScaledTextPane("Serif", 22, Color.BLACK);

        genericStudent = ImageIconLoader.getImageIcon(Design.genericStudent, 128, true);
        studentImage = new JLabel();

        allSemestersPanels = new ArrayList<> ();
        updateAllSemestersPanels();

        createAccountInfoBox(Design.mint);
        accInfoPanel = new AccountInfoPanel();
        idPanel = new IdentityPanel();
        addressPanel = new AddressPanel();
        famPanel = new FamillyPanel();
        schoolPanel = new ScholarshipPanel();

        addElements();
        logoutActionListener();
    }

    private class AccountInfoPanel {
    	private JRoundedPanel panel;
    	private JPanel infoPanel;
    	private JLabel idLabel;
    	private JRoundedTextField id;
    	private JLabel emailLabel;
    	private JRoundedTextField email;
    	private JLabel phoneLabel;
    	private JRoundedTextField phone;
    	private JLabel univLabel;
    	private JRoundedTextField univ;
    	private JLabel facultyLabel;
    	private JRoundedTextField faculty;
    	private JLabel deptLabel;
    	private JRoundedTextField dept;
    	private JLabel yearLabel;
    	private JRoundedTextField year;
    	private JLabel groupLabel;
    	private JRoundedTextField group;

        public AccountInfoPanel() {
        	panel = new JRoundedPanel();
        	panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
        	panel.setBackground(Design.mint);
        	
        	infoPanel = new JPanel();
        	infoPanel.setLayout(new MigLayout("insets 10, align 50% 50%"));
        	infoPanel.setBackground(Design.mint);

        	idLabel = Design.getCustomJLabel("ID", Design.tahomaBold16);
        	emailLabel = Design.getCustomJLabel("Email", Design.tahomaBold16);
        	phoneLabel = Design.getCustomJLabel("Phone Number", Design.tahomaBold16);
        	univLabel = Design.getCustomJLabel("University Name", Design.tahomaBold16);
        	facultyLabel = Design.getCustomJLabel("Faculty Name", Design.tahomaBold16);
        	deptLabel = Design.getCustomJLabel("Department Name", Design.tahomaBold16); 
        	yearLabel = Design.getCustomJLabel("Year", Design.tahomaBold16); 
        	groupLabel = Design.getCustomJLabel("Group Name", Design.tahomaBold16); 
        	
        	id = Design.getImmutableTextField(16, Design.tahomaPlain14);
        	email = Design.getImmutableTextField(16, Design.tahomaPlain14);
        	phone = Design.getImmutableTextField(16, Design.tahomaPlain14);
        	univ = Design.getImmutableTextField(16, Design.tahomaPlain14);
        	faculty = Design.getImmutableTextField(16, Design.tahomaPlain14);
        	dept = Design.getImmutableTextField(16, Design.tahomaPlain14);
        	year = Design.getImmutableTextField(16, Design.tahomaPlain14);
        	group = Design.getImmutableTextField(16, Design.tahomaPlain14);

    		infoPanel.add(idLabel);
    		infoPanel.add(id, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(emailLabel);
    		infoPanel.add(email, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(phoneLabel);
    		infoPanel.add(phone, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(univLabel);
    		infoPanel.add(univ, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(facultyLabel);
    		infoPanel.add(faculty, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(deptLabel);
    		infoPanel.add(dept, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(yearLabel);
    		infoPanel.add(year, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(groupLabel);
    		infoPanel.add(group, "pushx, growx, wrap");

    		panel.add(Box.createHorizontalGlue(), "pushx, growx");
    		panel.add(icon);
    		panel.add(Box.createHorizontalStrut(40));
    		panel.add(infoPanel, "pushx, growx");
    		panel.add(Box.createHorizontalGlue(), "pushx, growx");
        }
        
        public JRoundedPanel getPanel() {
        	return panel;
        }
    }

    private class IdentityPanel {
    	private JRoundedPanel panel;
    	private JLabel firstNameLabel;
    	private JRoundedTextField firstName;
    	private JLabel fatherInitialsLabel;
    	private JRoundedTextField fatherInitials;
    	private JLabel lastNameLabel;
    	private JRoundedTextField lastName;
    	private JLabel birthDateLabel;
    	private JDatePickerImpl birthDate;
    	private JLabel genderLabel;
    	private JRoundedTextField gender;
    	private JLabel nationalityLabel;
    	private JRoundedTextField nationality;
    	private JLabel birthCountryLabel;
    	private JRoundedTextField birthCountry;
    	private JLabel birthCityLabel;
    	private JRoundedTextField birthCity;

        public IdentityPanel() {
        	panel = new JRoundedPanel();
        	panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
        	panel.setBackground(Design.mint);
        	
        	firstNameLabel = Design.getCustomJLabel("First Name", Design.tahomaBold16);
        	fatherInitialsLabel = Design.getCustomJLabel("Father's Initials", Design.tahomaBold16);
        	lastNameLabel = Design.getCustomJLabel("Last Name", Design.tahomaBold16);
        	birthDateLabel = Design.getCustomJLabel("Birth Date", Design.tahomaBold16);
        	genderLabel = Design.getCustomJLabel("Gender", Design.tahomaBold16);
        	nationalityLabel = Design.getCustomJLabel("Nationality", Design.tahomaBold16);
        	birthCountryLabel = Design.getCustomJLabel("Birth Country", Design.tahomaBold16);
        	birthCityLabel = Design.getCustomJLabel("Birth City", Design.tahomaBold16);
        	
        	firstName = Design.getImmutableTextField(8, Design.tahomaPlain14);
        	fatherInitials = Design.getImmutableTextField(10, Design.tahomaPlain14);
            lastName = Design.getImmutableTextField(10, Design.tahomaPlain14);

    		UtilDateModel model = new UtilDateModel();
    		model.setSelected(true);
            Properties p = new Properties();
            p.put("text.today", "Today");
            p.put("text.month", "Month");
            p.put("text.year", "Year");
            JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
            birthDate = new JDatePickerImpl(datePanel, new DateLabelFormatter());
            birthDate.getModel().setDate(1980, 0, 1);
            birthDate.getComponent(0).setFocusable(false);
            birthDate.getComponent(1).setEnabled(false);

        	gender = Design.getImmutableTextField(8, Design.tahomaPlain14);

            nationality = Design.getImmutableTextField(10, Design.tahomaPlain14);
            birthCountry = Design.getImmutableTextField(10, Design.tahomaPlain14);
            birthCity = Design.getImmutableTextField(10, Design.tahomaPlain14);
            
            panel.add(firstNameLabel);
            panel.add(firstName, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(fatherInitialsLabel);
            panel.add(fatherInitials, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(lastNameLabel);
            panel.add(lastName, "pushx, growx, wrap");
            panel.add(Box.createVerticalStrut(5), "wrap");
            panel.add(birthDateLabel);
            panel.add(birthDate);
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(genderLabel);
            panel.add(gender);
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(nationalityLabel);
            panel.add(nationality, "pushx, growx, wrap");
            panel.add(Box.createVerticalStrut(5), "wrap");
            panel.add(birthCountryLabel);
            panel.add(birthCountry, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(birthCityLabel);
            panel.add(birthCity, "pushx, growx, wrap");
        }
        
        public JRoundedPanel getPanel() {
        	return panel;
        }
    }

    private class AddressPanel {
    	private JRoundedPanel panel;
    	private JLabel countryLabel;
    	private JRoundedTextField country;
    	private JLabel cityLabel;
    	private JRoundedTextField city;
    	private JLabel streetLabel;
    	private JRoundedTextField street;
    	private JLabel aptLabel;
    	private JNumberTextField apt;
    	private JLabel postalCodeLabel;
    	private JRoundedTextField postalCode;
    	private JLabel ibanLabel;
    	private JRoundedTextField iban;
    	private JLabel bankNameLabel;
    	private JRoundedTextField bankName;

        public AddressPanel() {
        	panel = new JRoundedPanel();
        	panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
        	panel.setBackground(Design.mint);
        	
        	countryLabel = Design.getCustomJLabel("Living Country", Design.tahomaBold16);
        	cityLabel = Design.getCustomJLabel("Living City", Design.tahomaBold16);
        	streetLabel = Design.getCustomJLabel("Street", Design.tahomaBold16);
        	aptLabel = Design.getCustomJLabel("Apt nr.", Design.tahomaBold16);
        	postalCodeLabel = Design.getCustomJLabel("Postal Code", Design.tahomaBold16);
        	ibanLabel = Design.getCustomJLabel("IBAN", Design.tahomaBold16);
        	bankNameLabel = Design.getCustomJLabel("Bank Name", Design.tahomaBold16);
        	
        	country = Design.getImmutableTextField(8, Design.tahomaPlain14);
        	city = Design.getImmutableTextField(10, Design.tahomaPlain14);
        	street = Design.getImmutableTextField(10, Design.tahomaPlain14);

        	apt = new JNumberTextField();
        	apt.setFont(Design.tahomaPlain16);
        	apt.setMaxLength(8);
        	apt.setAllowNegative(false);
        	apt.setEditable(false);
        	apt.setFocusable(false);

        	postalCode = Design.getImmutableTextField(10, Design.tahomaPlain14);
        	iban = Design.getImmutableTextField(10, Design.tahomaPlain14);
        	bankName = Design.getImmutableTextField(10, Design.tahomaPlain14);
            
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(countryLabel);
            panel.add(country, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx, spanx4");
            panel.add(cityLabel);
            panel.add(city, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx, wrap");
            panel.add(Box.createVerticalStrut(5), "wrap");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(streetLabel);
            panel.add(street, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(aptLabel);
            panel.add(apt, "width 75");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(postalCodeLabel);
            panel.add(postalCode, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx, wrap");
            panel.add(Box.createVerticalStrut(5), "wrap");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(ibanLabel);
            panel.add(iban, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx, spanx4");
            panel.add(bankNameLabel);
            panel.add(bankName, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx, wrap");
            panel.add(Box.createVerticalStrut(5), "wrap");
        }
        
        public JRoundedPanel getPanel() {
        	return panel;
        }
    }

    private class FamillyPanel {
        private JRoundedPanel panel;
        private JLabel fatherFNLabel;
        private JRoundedTextField fatherFN;
        private JLabel fatherLNLabel;
        private JRoundedTextField fatherLN;
        private JLabel motherFNLabel;
        private JRoundedTextField motherFN;
        private JLabel motherLNLabel;
        private JRoundedTextField motherLN;
        private JLabel maritalStatusLabel;
        private JRoundedTextField maritalStatus;
        
        public FamillyPanel() {
            panel = new JRoundedPanel();
            panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
            panel.setBackground(Design.mint);

            fatherFNLabel = Design.getCustomJLabel("Father's first name", Design.tahomaBold16);
            fatherLNLabel = Design.getCustomJLabel("Father's last name", Design.tahomaBold16);
            motherFNLabel = Design.getCustomJLabel("Mother's first name", Design.tahomaBold16);
            motherLNLabel = Design.getCustomJLabel("Mother's last name", Design.tahomaBold16);
            maritalStatusLabel = Design.getCustomJLabel("Marital status", Design.tahomaBold16);

            fatherFN = Design.getImmutableTextField(16, Design.tahomaPlain14);
            fatherLN = Design.getImmutableTextField(16, Design.tahomaPlain14);
            motherFN = Design.getImmutableTextField(16, Design.tahomaPlain14);
            motherLN = Design.getImmutableTextField(16, Design.tahomaPlain14);
            maritalStatus = Design.getImmutableTextField(16, Design.tahomaPlain14);

            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(fatherFNLabel);
            panel.add(fatherFN, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(fatherLNLabel);
            panel.add(fatherLN, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx, wrap");
            panel.add(Box.createVerticalStrut(5), "wrap");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(motherFNLabel);
            panel.add(motherFN, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(motherLNLabel);
            panel.add(motherLN, "pushx, growx");
            panel.add(Box.createHorizontalGlue(), "pushx, growx, wrap");
            panel.add(Box.createVerticalStrut(5), "wrap");
            panel.add(maritalStatusLabel, "spanx7, split2, center");
            panel.add(maritalStatus);
        }
        
        public JRoundedPanel getPanel() {
        	return panel;
        }
    }

    private class ScholarshipPanel {
        private JRoundedPanel panel;
        private JLabel admissionYearLabel;
        private JNumberTextField admissionYear;
        private JLabel admissionTypeLabel;
        private JRoundedTextField admissionType;
        private JLabel admissionGradeLabel;
        private JNumberTextField admissionGrade;
        private JLabel taxisLabel;
        private JRoundedTextField taxis;

        public ScholarshipPanel() {
            panel = new JRoundedPanel();
            panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
            panel.setBackground(Design.mint);

            admissionYearLabel = Design.getCustomJLabel("Admission year", Design.tahomaBold16);
            admissionTypeLabel = Design.getCustomJLabel("Admission type", Design.tahomaBold16);
            admissionGradeLabel = Design.getCustomJLabel("Admission grade", Design.tahomaBold16);
            taxisLabel = Design.getCustomJLabel("Taxis", Design.tahomaBold16);

            admissionYear = new JNumberTextField();
            admissionYear.setFont(Design.tahomaPlain16);
            admissionYear.setMaxLength(4);
            admissionYear.setAllowNegative(false);
            admissionYear.setEditable(false);
            admissionYear.setFocusable(false);

            admissionType = Design.getImmutableTextField(16, Design.tahomaPlain14);

            admissionGrade = new JNumberTextField();
            admissionGrade.setFormat(3);
            admissionGrade.setFont(Design.tahomaPlain16);
            admissionGrade.setMaxLength(4);
            admissionGrade.setPrecision(2);
            admissionGrade.setAllowNegative(false);
            admissionGrade.setEditable(false);
            admissionGrade.setFocusable(false);

            taxis = Design.getImmutableTextField(16, Design.tahomaPlain14);

            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(admissionYearLabel);
            panel.add(admissionYear, "width 125");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(admissionTypeLabel);
            panel.add(admissionType);
            panel.add(Box.createHorizontalGlue(), "pushx, growx, wrap");
            panel.add(Box.createVerticalStrut(5), "wrap");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(admissionGradeLabel);
            panel.add(admissionGrade, "width 125");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(taxisLabel);
            panel.add(taxis);
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
        }
        
        public JRoundedPanel getPanel() {
        	return panel;
        }
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void updateDeptID(String deptID) {
    	this.deptID = deptID;
    }

    public void updateYear(String year) {
    	this.year = year;
    }

    public void updateTitle() {
        title.setText(Main.userName + "'s Personal Info");
        subTitle.setText(Main.userName + "'s Grades");
    }

    /**
     * Obtin sub forma unui tabel cu o singura linie informatiile
     * despre studentul autentificat, dupa care, extrag valorile si
     * completez field-urile.
     */
    public void populateFields() {
    	String retrieveQuery = Main.TABLE_STUDENTS_QUERY + " WHERE id=" + Main.userID;
    	String condition, ID;
    	Object valueObj;
    	Vector<Vector<Object>> res = new Vector<> ();
    	Vector<String> columns = new Vector<> ();

    	createTable(Main.TABLE_STUDENTS, retrieveQuery);
    	
    	setComponentText(accInfoPanel.id, table.getValueAt(0, 0).toString());
    	setComponentText(accInfoPanel.email, table.getValueAt(0, 25).toString());
    	setComponentText(accInfoPanel.phone, table.getValueAt(0, 26).toString());

    	setComponentText(idPanel.firstName, table.getValueAt(0, 1).toString());
    	setComponentText(idPanel.lastName, table.getValueAt(0, 2).toString());
    	setComponentText(idPanel.fatherInitials, table.getValueAt(0, 3).toString());
       	setComponentText(idPanel.birthDate, table.getValueAt(0, 4).toString());
    	setComponentText(idPanel.gender, table.getValueAt(0, 5).toString());
    	setComponentText(idPanel.nationality, table.getValueAt(0, 6).toString());
       	setComponentText(idPanel.birthCountry, table.getValueAt(0, 7).toString());
       	setComponentText(idPanel.birthCity, table.getValueAt(0, 8).toString());

       	setComponentText(famPanel.fatherFN, table.getValueAt(0, 9).toString());
       	setComponentText(famPanel.fatherLN, table.getValueAt(0, 10).toString());
       	setComponentText(famPanel.motherFN, table.getValueAt(0, 11).toString());
       	setComponentText(famPanel.motherLN, table.getValueAt(0, 12).toString());
       	setComponentText(famPanel.maritalStatus, table.getValueAt(0, 13).toString());

       	setComponentText(addressPanel.country, table.getValueAt(0, 14).toString());
       	setComponentText(addressPanel.city, table.getValueAt(0, 15).toString());
       	setComponentText(addressPanel.street, table.getValueAt(0, 16).toString());
       	setComponentText(addressPanel.apt, table.getValueAt(0, 17).toString());
       	setComponentText(addressPanel.postalCode, table.getValueAt(0, 18).toString());
       	setComponentText(addressPanel.iban, table.getValueAt(0, 19).toString());
       	setComponentText(addressPanel.bankName, table.getValueAt(0, 20).toString());

       	setComponentText(schoolPanel.admissionYear, table.getValueAt(0, 21).toString());
       	setComponentText(schoolPanel.admissionType, table.getValueAt(0, 22).toString());
       	setComponentText(schoolPanel.admissionGrade, table.getValueAt(0, 23).toString());
       	setComponentText(schoolPanel.taxis, table.getValueAt(0, 24).toString());
       	setComponentText(schoolPanel.admissionType, table.getValueAt(0, 22).toString());
       	setComponentText(schoolPanel.admissionGrade, table.getValueAt(0, 23).toString());
       	setComponentText(schoolPanel.taxis, table.getValueAt(0, 24).toString());
       	
       	// obtinere denumire grupa
       	condition = "id=" + table.getValueAt(0, 27).toString();
       	valueObj = dbConnection.getOneValue(Main.TABLE_GROUPS, "name", condition);
       	if (valueObj == null)
       		return;
       	
       	// setare denumire grupa + an de studiu si salvare ID departament
    	setComponentText(accInfoPanel.group, valueObj.toString());
    	ID = deptID;
    	setComponentText(accInfoPanel.year, year);
    	
    	// obtinere denumire departament + ID facultate
    	condition = "id=" + ID;
    	columns.clear();
    	columns.add("name");
    	columns.add("faculty_id");
       	res = dbConnection.selectEntries(columns, Main.TABLE_DEPARTMENTS, condition);
       	if (res.isEmpty())
       		return;
       	
       	// setare denumire departament si salvare ID facultate
    	setComponentText(accInfoPanel.dept, res.get(0).get(0).toString());
    	ID = res.get(0).get(1).toString();
    	
    	// obtinere denumire facultate + ID universitate
    	condition = "id=" + ID;
    	columns.clear();
    	columns.add("name");
    	columns.add("univ_id");
       	res = dbConnection.selectEntries(columns, Main.TABLE_FACULTIES, condition);
       	if (res.isEmpty())
       		return;
       	
       	// setare denumire facultate si salvare ID universitate
    	setComponentText(accInfoPanel.faculty, res.get(0).get(0).toString());
    	ID = res.get(0).get(1).toString();
    	
    	// obtinere si setare denumire universitate
    	condition = "id=" + ID;
    	valueObj = dbConnection.getOneValue(Main.TABLE_UNIVERSITIES, "name", condition);
    	if (valueObj != null)
        	setComponentText(accInfoPanel.univ, valueObj.toString());
    }

    public void updateAllSemestersPanels() {
    	allSemestersPanels.clear();
		Vector<Vector<Vector<Object>>> allSemesters = new Vector<> ();
		Vector<Vector<Object>> sem;
		Vector<String> columns;
		String cond = "dept_id=" + deptID + " AND year='" + year + "'";
		Object nrOfSemestersObj = dbConnection.getOneValue(Main.TABLE_COURSES, "MAX(semester)", cond);

		if (nrOfSemestersObj == null)
			return;

		String nrOfSemesters = nrOfSemestersObj.toString();

		columns = new Vector<> ();
		columns.add("id");
		columns.add("name");
		columns.add("teacher");
		columns.add("credits");
		columns.add("type");

		for (int i = 1; i <= Integer.valueOf(nrOfSemesters); i++) {
			cond = "dept_id=" + deptID + " AND semester=" + i +
					" AND year='" + year + "'";
			sem = dbConnection.selectEntries(columns, Main.TABLE_COURSES, cond);
			allSemesters.add(sem);
		}

    	allSemestersPanels = getAllSemestersPanels(allSemesters);
    }

    public void updateStudentImage() {
        String condition = "id = " + Main.userID;
        Object imageObj = dbConnection.getOneValue(Main.TABLE_STUDENTS, "image", condition);

        if (imageObj == null) {
            studentImage.setIcon(genericStudent);
            icon.setIcon(genericStudent);
            return;
        }

        String imageName = imageObj.toString();

        if (imageName.equals("DEFAULT")) {
            studentImage.setIcon(genericStudent);
            icon.setIcon(genericStudent);
        } else {
        	String imagePath = Utils.homePath + imageName;

        	// download image
			if (!new File(imagePath).exists()) {
				boolean downloadSuccess = Utils.downloadImage(imageName);
				if (!downloadSuccess) {
					studentImage.setIcon(genericStudent);
		            icon.setIcon(genericStudent);
					return;
				}
			}
			
            studentImage.setIcon(ImageIconLoader.getImageIcon(imagePath, 64, false));
            icon.setIcon(ImageIconLoader.getImageIcon(imagePath, 128, false));
        }
    }

    public void addElements() {
        panel.add(dateAndTime.getDateAndTimePanel(Design.mint), "split3");
        panel.add(Box.createHorizontalGlue(), "pushx, growx");
        panel.add(accountInfo);
        panel.add(studentImage);
        panel.add(logout, "right, wrap");
        panel.add(Box.createVerticalStrut(20), "wrap");
        panel.add(title, "spanx3, center, wrap");
        panel.add(Box.createVerticalStrut(25), "wrap");
        panel.add(accInfoPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(idPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(addressPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(famPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(schoolPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(20), "spanx3, wrap");
        panel.add(subTitle, "spanx3, center, wrap");
        panel.add(Box.createVerticalStrut(25), "wrap");
    }

    // adaugare panel pentru fiecare semestru
    public void addSemestersPanels() {
        for (JRoundedPanel sem : allSemestersPanels) {
        	panel.add(sem, "spanx3, pushx, growx, wrap");
        	panel.add(Box.createVerticalStrut(15), "spanx3, wrap");
        	toRemoveElements += 2;
        }
    }

    public void removeSemestersPanels() {
    	int totalPanelComponents = panel.getComponentCount();

    	if (totalPanelComponents < toRemoveElements)
    		return;

    	for (int i = totalPanelComponents - 1; i >= totalPanelComponents - toRemoveElements; i--)
    		panel.remove(i);

    	toRemoveElements = 0;
    }

    private void clearAction() {
        clearFields();
        icon.setIcon(genericStudent);
        accInfoPanel.id.setText(null);
        browseFrame.clearFilePath();
    }

	private void logoutActionListener() {
		logout.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent ev) {		    	
		    	int dialogButton = JOptionPane.YES_NO_OPTION;
	    		int dialogResult = JOptionPane.showConfirmDialog
	    				(null, "Are you sure that you want to logout?", "Warning", dialogButton);
	    		if(dialogResult == JOptionPane.YES_OPTION) {
	    			clearAction();
					notifyAllObservers(Main.TABLE_LOGIN);
	    		}
		    }
		});
	}
	
	/**
	 * Obtine o Lista cu panel-urile corespunzatoare fiecarui semestru. 
	 * @param allSemesters
	 * @return
	 */
	private ArrayList<JRoundedPanel> getAllSemestersPanels(Vector<Vector<Vector<Object>>> allSemesters) {
		ArrayList<JRoundedPanel> allSemestersPanelsList = new ArrayList<> ();
		Vector<Vector<Object>> sem;
		
		for (int i = 0; i < allSemesters.size(); i++) {
			sem = allSemesters.get(i);
			if (!sem.isEmpty())
				allSemestersPanelsList.add(getSemesterPanel(i + 1, sem));
		}
		
		return allSemestersPanelsList;
	}
	
	/**
	 * Obtine un panel reprezentand un semestru.
	 * @param semester
	 * @param semPlan
	 * @return
	 */
	private JRoundedPanel getSemesterPanel(int semester, Vector<Vector<Object>> sem) {
		Vector<Object> course;
		JLabel element;
		String courseID, gradeValue, condition;
		Object gradeValueObj;
		
		JRoundedPanel semPanel = new JRoundedPanel();
		semPanel.setLayout(new MigLayout("insets 20, align 50% 50%"));
		semPanel.setBackground(Design.mint);
		
		element = new JLabel("Semester " + semester);
		element.setFont(Design.serifBold26);
		semPanel.add(element, "spanx11, center, wrap");
		semPanel.add(Box.createVerticalStrut(10), "wrap");
		
		element = new JLabel("ID");
		element.setFont(Design.tahomaBold18);
		semPanel.add(element, "center");
		semPanel.add(Box.createHorizontalStrut(10));
		element = new JLabel("Course name");
		element.setFont(Design.tahomaBold18);
		semPanel.add(element, "center");
		semPanel.add(Box.createHorizontalStrut(10));
		element = new JLabel("Teacher's name");
		element.setFont(Design.tahomaBold18);
		semPanel.add(element, "center");
		semPanel.add(Box.createHorizontalStrut(10));
		element = new JLabel("Number of credits");
		element.setFont(Design.tahomaBold18);
		semPanel.add(element, "center");
		semPanel.add(Box.createHorizontalStrut(10));
		element = new JLabel("Type");
		element.setFont(Design.tahomaBold18);
		semPanel.add(element, "center");
		semPanel.add(Box.createHorizontalStrut(10));
		element = new JLabel("Grade");
		element.setFont(Design.tahomaBold18);
		semPanel.add(element, "center, wrap");

		for (int i = 0; i < sem.size(); i++) {
			course = sem.get(i);
			for (int j = 0; j < course.size(); j++) {
				element = new JLabel(course.get(j).toString());
				element.setFont(Design.tahomaPlain16);
				semPanel.add(element, "center");
				semPanel.add(Box.createHorizontalStrut(10));
			}

			courseID = course.get(0).toString();

    		condition = "course_id=" + courseID + " AND stud_id=" + Main.userID;
    		gradeValueObj = dbConnection.getOneValue(Main.TABLE_GRADES, "grade_value", condition);
			if (gradeValueObj != null) {
				gradeValue = gradeValueObj.toString();
				element = new JLabel(gradeValue);
			} else
				element = new JLabel("-");
			element.setFont(Design.tahomaPlain16);

			semPanel.add(element, "center, wrap");
		}

		return semPanel;
	}
}
