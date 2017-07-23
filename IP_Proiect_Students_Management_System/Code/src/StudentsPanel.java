import Components.DateLabelFormatter;
import Components.Design;
import Components.ImageIconLoader;
import Components.JNumberTextField;
import Components.JRoundedPanel;
import Components.JRoundedTextField;
import Components.JScaledTextPane;
import Components.Utils;
import Components.Validator;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import net.miginfocom.swing.MigLayout;

public class StudentsPanel extends BasicPanel {
	private static int hiddenColumnsIndexes[] = {
		 3, 7, 8, 9, 10, 11, 12,
		 13, 14, 16, 17, 18, 19,
		 20, 22, 25, 26, 27, 28
	};

	private JScrollPane scrollPane;
	private GroupsPanel groupsPanel;
	private AccountInfoPanel accInfoPanel;
	private IdentityPanel idPanel;
	private FamillyPanel famPanel;
	private AddressPanel addressPanel;
	private ScholarshipPanel schoolPanel;
    private JScaledTextPane title;

    private JLabel secretaryImage;
    private String deptID;
    private ImageIcon genericSecretary;
    private ImageIcon genericFaculty;
    private ImageIcon genericStudent;
    private JButton select;
    private String year;
    private String displayedGroupName;
    private JLabel deptLogo;
    private JComboBox<String> groupSelector;
    private JComboBox<String> currentStudentGroupSelector;
    private GradesFrame gradesFrame;

    public StudentsPanel(Window window, ArrayList<String> args, String retrieveQuery) {
        super(window);
        panel.setBackground(Design.mountain);
        panelWidth = 950;
        panelHeight = 720;

        deptID = args.get(0);
        year = args.get(2);
        displayedGroupName = "None";

        scrollPane = new JScrollPane(null, 20, 30);
        scrollPane.setViewportView(panel);

        title = new JScaledTextPane("Serif", 22, Color.BLACK);

        createTable(Main.TABLE_STUDENTS, retrieveQuery);

        genericSecretary = ImageIconLoader.getImageIcon(Design.genericSecretary, 64, true);
        genericFaculty = ImageIconLoader.getImageIcon(Design.genericFaculty, 128, true);
        genericStudent = ImageIconLoader.getImageIcon(Design.genericStudent, 128, true);
        secretaryImage = new JLabel();
        updateSecretaryImage();
        deptLogo = new JLabel();
        icon.setIcon(genericStudent);

        select = new JButton("Select");
        select.setIcon(ImageIconLoader.getImageIcon("Select.png", 24, true));

        groupSelector = new JComboBox<String> ();
    	currentStudentGroupSelector = new JComboBox<String> ();

        createAccountInfoBox(Design.scrub);
        groupsPanel = new GroupsPanel();
        accInfoPanel = new AccountInfoPanel();
        idPanel = new IdentityPanel();
        addressPanel = new AddressPanel();
        famPanel = new FamillyPanel();
        schoolPanel = new ScholarshipPanel();

		createAssoc();
        addElements();
        selectionListener();
        addActionListener();
        deleteActionListener();
        updateActionListener();
        clearActionListener();
        selectActionListener();
        logoutActionListener();
    }

    private class GroupsPanel {
        private JRoundedPanel panel;
        private JLabel groupNameLabel;
        private JRoundedTextField groupName;
        private JButton add;
        private JButton delete;
        private JButton rename;
        private JComboBox<String> groupsList;

        public GroupsPanel() {
            createPanel();
            selectionListener();
            addListener();
            deleteListener();
            renameListener();
        }

        public JRoundedPanel getPanel() {
            return panel;
        }

        private void createPanel() {
            panel = new JRoundedPanel();
            panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
            panel.setBackground(Design.scrub);

            groupNameLabel = Design.getCustomJLabel("Group name", Design.tahomaBold16);
            groupName = new JRoundedTextField(16);
            groupName.setFont(Design.tahomaPlain16);

            add = new JButton("Add");
            add.setIcon(ImageIconLoader.getImageIcon("Add.png", 24, true));

            delete = new JButton("Delete");
            delete.setIcon(ImageIconLoader.getImageIcon("Delete.png", 24, true));

            rename = new JButton("Rename");
            rename.setIcon(ImageIconLoader.getImageIcon("Update.png", 24, true));

            groupsList = new JComboBox<> ();
            populateWithGroups();

            panel.add(groupNameLabel);
            panel.add(groupName);
            panel.add(groupsList,"wrap");
            panel.add(add, "spanx3, split5");
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(delete);
            panel.add(Box.createHorizontalGlue(), "pushx, growx");
            panel.add(rename);
        }

        /**
         * Obtine numele tuturor grupelor de la departamentul X, anul Y
         * si "populeaza" ComboBox-urile cu acestea
         */
        private void populateWithGroups() {
            Vector<String> columns = new Vector<> ();
            columns.add("name");
            String condition = "dept_id=" + deptID + " AND year='" + year + "'";
            Vector<Vector<Object>> groupsSet = dbConnection.selectEntries(columns, Main.TABLE_GROUPS, condition);
            String item;

            groupsList.addItem("None");
            groupSelector.addItem("None");
            currentStudentGroupSelector.addItem("None");

            if (groupsSet.isEmpty())
        		return;

            for (int i = 0; i < groupsSet.size(); i++) {
            	item = groupsSet.get(i).get(0).toString();
            	groupsList.addItem(item);
            	groupSelector.addItem(item);
            	currentStudentGroupSelector.addItem(item);
            }
        }

        private void selectionListener() {
            groupsList.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
	                    String selectedItem = groupsList.getSelectedItem().toString();

	                    if (!selectedItem.equals("None"))
	                        groupName.setText(selectedItem);
	                    else
	                        groupName.setText(null);
					}
				}
            });
        }

        private void addListener() {
            add.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newGroupName = groupName.getText();

                    for (int i = 0; i < groupsList.getItemCount(); i++)
                        if (groupsList.getItemAt(i).equalsIgnoreCase(newGroupName)) {
                            JOptionPane.showMessageDialog(null, "Group " + newGroupName +
                            		" already exists!", "Error", 0);
                            return;
                        }

                    groupName.setText(null);
                    groupsList.addItem(newGroupName);
                    groupSelector.addItem(newGroupName);
                    currentStudentGroupSelector.addItem(newGroupName);

                    Vector<Object> row = new Vector<> ();
                    row.add(null);
                    row.add(newGroupName);
                    row.addElement(deptID);
                    row.add(year);

                    dbConnection.insertEntry(Main.TABLE_GROUPS, row);
                }
            });
        }

        private void deleteListener() {
        	delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = groupsList.getSelectedIndex();

                    if (selectedIndex > 0) {
                    	String item = groupsList.getSelectedItem().toString();
                        groupsList.removeItemAt(selectedIndex);
                        groupsList.setSelectedIndex(0);

                        if (groupSelector.getSelectedIndex() == selectedIndex)
                        	groupSelector.setSelectedIndex(0);
                        groupSelector.removeItemAt(selectedIndex);

                        if (currentStudentGroupSelector.getSelectedIndex() == selectedIndex)
                        	currentStudentGroupSelector.setSelectedIndex(0);
                        currentStudentGroupSelector.removeItemAt(selectedIndex);

                        groupName.setText(null);

                        /**
                         * Sterg grupa cunoscandu-i ID-ul si toti
                         * studentii inscrisi in aceasta grupa
                         */
                        String groupID = getGroupIDByName(item);
                        if (groupID == null)
                        	return;

                        // stergere grupa
                        String condition = "id=" + groupID;
                        dbConnection.deleteEntry(Main.TABLE_GROUPS, condition);

                        // stergere studenti
                        condition = "group_id=" + groupID;
                        dbConnection.deleteEntry(Main.TABLE_STUDENTS, condition);

                        // nu e nicio grupa afisata in GUI
                        if (displayedGroupName.equals("None"))
                        	return;

                        /** 
                         * daca se sterge grupa afisata pe ecran,
                         * golesc tabela si field-urile
                         */
                        if (item.equals(displayedGroupName)) {
                        	clearAction();
                        	groupSelector.setSelectedIndex(0);
                        	table.removeAllRows();
                        	displayedGroupName = "None";
                        }
                    }
                }
            });
        }

        private void renameListener() {
            rename.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = groupsList.getSelectedIndex();
                    String newGroupName = groupName.getText();

                    if (selectedIndex <= 0)
                        return;

                    for (int i = 0; i < groupsList.getItemCount(); i++)
                        if (groupsList.getItemAt(i).equals(newGroupName)) {
                            JOptionPane.showMessageDialog(null, "Group " +
                            		newGroupName +" already exists!",
                            		"Error", 0);
                            return;
                        }

                    String renameQuery = "UPDATE " + Main.TABLE_GROUPS +
                    		" SET " + "name='" + newGroupName + "' WHERE" + " dept_id=" +
                    		deptID + " AND name='" + groupsList.getSelectedItem().toString() + 
                    		"' AND year='" + year + "'";

                    dbConnection.executeQuery(renameQuery);
                    groupName.setText(null);

                    int nrOfGroups = groupsList.getItemCount();
                    groupsList.removeItemAt(selectedIndex);
                    groupsList.addItem(newGroupName);
                    groupsList.setSelectedIndex(nrOfGroups - 1);

                    groupSelector.removeItemAt(selectedIndex);
                    groupSelector.addItem(newGroupName);
                    if (groupSelector.getSelectedIndex() == selectedIndex)
                    	groupSelector.setSelectedIndex(nrOfGroups - 1);

                    currentStudentGroupSelector.removeItemAt(selectedIndex);
                    currentStudentGroupSelector.addItem(newGroupName);
                    if (currentStudentGroupSelector.getSelectedIndex() == selectedIndex)
                    	currentStudentGroupSelector.setSelectedIndex(nrOfGroups - 1);
                }
            });
        }
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
    	private JLabel groupLabel;

        public AccountInfoPanel() {
        	panel = new JRoundedPanel();
        	panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
        	panel.setBackground(Design.scrub);
        	
        	infoPanel = new JPanel();
        	infoPanel.setLayout(new MigLayout("insets 10, align 50% 50%"));
        	infoPanel.setBackground(Design.scrub);

        	idLabel = Design.getCustomJLabel("ID", Design.tahomaBold16);
        	emailLabel = Design.getCustomJLabel("Email", Design.tahomaBold16);
        	phoneLabel = Design.getCustomJLabel("Phone Number", Design.tahomaBold16);
        	groupLabel = Design.getCustomJLabel("Group Name", Design.tahomaBold16); 
        	
        	id = new JRoundedTextField(16);
    		id.setEditable(false);
    		id.setFocusable(false);
        	email = new JRoundedTextField(16);
        	phone = new JRoundedTextField(16);

    		infoPanel.add(idLabel);
    		infoPanel.add(id, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(emailLabel);
    		infoPanel.add(email, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(phoneLabel);
    		infoPanel.add(phone, "pushx, growx, wrap");
    		infoPanel.add(Box.createVerticalStrut(10), "wrap");
    		infoPanel.add(groupLabel);
    		infoPanel.add(currentStudentGroupSelector);

    		panel.add(Box.createHorizontalGlue(), "pushx, growx");
    		panel.add(icon);
    		panel.add(browse, "bottom");
    		panel.add(Box.createHorizontalStrut(25));
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
    	private JComboBox<String> gender;
    	private JLabel nationalityLabel;
    	private JRoundedTextField nationality;
    	private JLabel birthCountryLabel;
    	private JRoundedTextField birthCountry;
    	private JLabel birthCityLabel;
    	private JRoundedTextField birthCity;

        public IdentityPanel() {
        	panel = new JRoundedPanel();
        	panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
        	panel.setBackground(Design.scrub);
        	
        	firstNameLabel = Design.getCustomJLabel("First Name", Design.tahomaBold16);
        	fatherInitialsLabel = Design.getCustomJLabel("Father's Initials", Design.tahomaBold16);
        	lastNameLabel = Design.getCustomJLabel("Last Name", Design.tahomaBold16);
        	birthDateLabel = Design.getCustomJLabel("Birth Date", Design.tahomaBold16);
        	genderLabel = Design.getCustomJLabel("Gender", Design.tahomaBold16);
        	nationalityLabel = Design.getCustomJLabel("Nationality", Design.tahomaBold16);
        	birthCountryLabel = Design.getCustomJLabel("Birth Country", Design.tahomaBold16);
        	birthCityLabel = Design.getCustomJLabel("Birth City", Design.tahomaBold16);
        	
        	firstName = new JRoundedTextField(8);
        	fatherInitials = new JRoundedTextField(10);
            lastName = new JRoundedTextField(10);

    		UtilDateModel model = new UtilDateModel();
    		model.setSelected(true);
            Properties p = new Properties();
            p.put("text.today", "Today");
            p.put("text.month", "Month");
            p.put("text.year", "Year");
            JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
            birthDate = new JDatePickerImpl(datePanel, new DateLabelFormatter());
            birthDate.getModel().setDate(1980, 0, 1);

        	gender = new JComboBox<> ();
        	gender.addItem("Male");
        	gender.addItem("Female");
            gender.setFont(Design.tahomaPlain16);

            nationality = new JRoundedTextField(10);
            birthCountry = new JRoundedTextField(10);
            birthCity = new JRoundedTextField(10);
            
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
        	panel.setBackground(Design.scrub);
        	
        	countryLabel = Design.getCustomJLabel("Living Country", Design.tahomaBold16);
        	cityLabel = Design.getCustomJLabel("Living City", Design.tahomaBold16);
        	streetLabel = Design.getCustomJLabel("Street", Design.tahomaBold16);
        	aptLabel = Design.getCustomJLabel("Apt nr.", Design.tahomaBold16);
        	postalCodeLabel = Design.getCustomJLabel("Postal Code", Design.tahomaBold16);
        	ibanLabel = Design.getCustomJLabel("IBAN", Design.tahomaBold16);
        	bankNameLabel = Design.getCustomJLabel("Bank Name", Design.tahomaBold16);
        	
        	country = new JRoundedTextField(8);
        	city = new JRoundedTextField(10);
        	street = new JRoundedTextField(10);

        	apt = new JNumberTextField();
        	apt.setFont(Design.tahomaPlain16);
        	apt.setMaxLength(8);
        	apt.setAllowNegative(false);

        	postalCode = new JRoundedTextField(10);
        	iban = new JRoundedTextField(10);
        	bankName = new JRoundedTextField(10);
            
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
        private JComboBox<String> maritalStatus;
        
        public FamillyPanel() {
            panel = new JRoundedPanel();
            panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
            panel.setBackground(Design.scrub);

            fatherFNLabel = Design.getCustomJLabel("Father's first name", Design.tahomaBold16);
            fatherLNLabel = Design.getCustomJLabel("Father's last name", Design.tahomaBold16);
            motherFNLabel = Design.getCustomJLabel("Mother's first name", Design.tahomaBold16);
            motherLNLabel = Design.getCustomJLabel("Mother's last name", Design.tahomaBold16);
            maritalStatusLabel = Design.getCustomJLabel("Marital status", Design.tahomaBold16);

            fatherFN = new JRoundedTextField(16);
            fatherLN = new JRoundedTextField(16);
            motherFN = new JRoundedTextField(16);
            motherLN = new JRoundedTextField(16);

            maritalStatus = new JComboBox<> ();
            maritalStatus.addItem("Single");
            maritalStatus.addItem("Married");
            maritalStatus.addItem("Divorced");
            maritalStatus.addItem("Widowed");
            maritalStatus.setFont(Design.tahomaPlain16);

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
        private JComboBox<String> admissionType;
        private JLabel admissionGradeLabel;
        private JNumberTextField admissionGrade;
        private JLabel taxisLabel;
        private JComboBox<String> taxis;

        public ScholarshipPanel() {
            panel = new JRoundedPanel();
            panel.setLayout((LayoutManager)new MigLayout("insets 10, align 50% 50%"));
            panel.setBackground(Design.scrub);

            admissionYearLabel = Design.getCustomJLabel("Admission year", Design.tahomaBold16);
            admissionTypeLabel = Design.getCustomJLabel("Admission type", Design.tahomaBold16);
            admissionGradeLabel = Design.getCustomJLabel("Admission grade", Design.tahomaBold16);
            taxisLabel = Design.getCustomJLabel("Taxis", Design.tahomaBold16);

            admissionYear = new JNumberTextField();
            admissionYear.setFont(Design.tahomaPlain16);
            admissionYear.setMaxLength(4);
            admissionYear.setAllowNegative(false);

            admissionType = new JComboBox<> ();
            admissionType.addItem("Admission Exam");
            admissionType.addItem("Baccalaureate Diploma");
            admissionType.addItem("International Applicant");
            admissionType.addItem("Transfer");
            admissionType.addItem("Other");
            admissionType.setFont(Design.tahomaPlain16);

            admissionGrade = new JNumberTextField();
            admissionGrade.setFormat(3);
            admissionGrade.setFont(Design.tahomaPlain16);
            admissionGrade.setMaxLength(2);
            admissionGrade.setPrecision(2);
            admissionGrade.setAllowNegative(false);

            taxis = new JComboBox<> ();
            taxis.addItem("No");
            taxis.addItem("Yes");
            taxis.setFont(Design.tahomaPlain16);

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

    public void addSorters() {
    	table.setIntComparatorAt(0);
    	table.setDateComparatorAt(4);
    	table.setIntComparatorAt(21);
    	table.setFloatComparatorAt(23);
    }

    public void hideColumns() {
    	for (int i = 0; i < hiddenColumnsIndexes.length; i++)
    		table.hideColumn(hiddenColumnsIndexes[i]);
    }

    public void updateTitle(String name) {
        title.setText(name);
    }

    public void updateSecretaryImage() {
        String condition = "id = " + Main.userID;
        Object imageObj = dbConnection.getOneValue(Main.TABLE_SECRETARIES, "image", condition);

        if (imageObj == null) {
        	secretaryImage.setIcon(genericSecretary);
        	return;
        }

        String imageName = imageObj.toString();

        if (imageName.equals("DEFAULT"))
            secretaryImage.setIcon(genericSecretary);
        else {
        	String imagePath = Utils.homePath + imageName;
        	
        	// download image
			if (!new File(imagePath).exists()) {
				boolean downloadSuccess = Utils.downloadImage(imageName);
				if (!downloadSuccess) {
					secretaryImage.setIcon(genericSecretary);
					return;
				}
			}

            secretaryImage.setIcon(ImageIconLoader.getImageIcon(imagePath, 64, false));
        }
    }

    public void updateDeptLogo() {
        if (deptID == null) {
            deptLogo.setIcon(genericFaculty);
            return;
        }

        String condition = "id = " + deptID;
        Object logoNameObj = dbConnection.getOneValue(Main.TABLE_DEPARTMENTS, "logo", condition);
        
        if (logoNameObj == null) {
        	deptLogo.setIcon(genericFaculty);
        	return;
        }

        String logoName = logoNameObj.toString();

        if (logoName.equals("DEFAULT"))
            deptLogo.setIcon(genericFaculty);
        else {
        	String imagePath = Utils.homePath + logoName;
        	
        	// download image
			if (!new File(imagePath).exists()) {
				boolean downloadSuccess = Utils.downloadImage(logoName);
				if (!downloadSuccess) {
					deptLogo.setIcon(genericFaculty);
					return;
				}
			}

            deptLogo.setIcon(ImageIconLoader.getImageIcon(imagePath, 128, false));
        }
    }

    public void addElements() {
        panel.add(dateAndTime.getDateAndTimePanel(Design.scrub), "split3");
        panel.add(Box.createHorizontalGlue(), "pushx, growx");
        panel.add(accountInfo);
        panel.add(secretaryImage);
        panel.add(logout, "right, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(title, "spanx3, center, wrap");
        panel.add(Box.createVerticalStrut(20), "wrap");
        panel.add(groupsPanel.getPanel());
        panel.add(deptLogo, "skip, wrap");
        panel.add(Box.createVerticalStrut(15), "wrap");
        panel.add(tableScrollPane, "spanx3, pushx, growx, height 300, wrap");
        panel.add(select, "split5, spanx3");
        panel.add(groupSelector);
        panel.add(Box.createHorizontalGlue(), "pushx, growx");
        panel.add(searchField, "pushx, growx");
        panel.add(search, "wrap");
        panel.add(Box.createVerticalStrut(20), "wrap");
        panel.add(accInfoPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(idPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(addressPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(famPanel.getPanel(), "spanx3, pushx, growx, wrap");
        panel.add(Box.createVerticalStrut(10), "wrap");
        panel.add(schoolPanel.getPanel(), "spanx3, pushx, growx, wrap");
		panel.add(Box.createVerticalStrut(10), "wrap");
		panel.add(add, "center, split7, spanx5");
		panel.add(Box.createHorizontalStrut(25));
		panel.add(delete);
		panel.add(Box.createHorizontalStrut(25));
		panel.add(update);
		panel.add(Box.createHorizontalStrut(25));
		panel.add(clear);
    }

    private void createAssoc() {
		fieldsMap.put(1, idPanel.firstName);
		fieldsMap.put(2, idPanel.lastName);
		fieldsMap.put(3, idPanel.fatherInitials);
		fieldsMap.put(4, idPanel.birthDate);
		fieldsMap.put(5, idPanel.gender);
		fieldsMap.put(6, idPanel.nationality);
		fieldsMap.put(7, idPanel.birthCountry);
		fieldsMap.put(8, idPanel.birthCity);
		fieldsMap.put(9, famPanel.fatherFN);
		fieldsMap.put(10, famPanel.fatherLN);
		fieldsMap.put(11, famPanel.motherFN);
		fieldsMap.put(12, famPanel.motherLN);
		fieldsMap.put(13, famPanel.maritalStatus);
		fieldsMap.put(14, addressPanel.country);
		fieldsMap.put(15, addressPanel.city);
		fieldsMap.put(16, addressPanel.street);
		fieldsMap.put(17, addressPanel.apt);
		fieldsMap.put(18, addressPanel.postalCode);
		fieldsMap.put(19, addressPanel.iban);
		fieldsMap.put(20, addressPanel.bankName);
		fieldsMap.put(21, schoolPanel.admissionYear);
		fieldsMap.put(22, schoolPanel.admissionType);
		fieldsMap.put(23, schoolPanel.admissionGrade);
		fieldsMap.put(24, schoolPanel.taxis);
		fieldsMap.put(25, accInfoPanel.email);
		fieldsMap.put(26, accInfoPanel.phone);
    }

    public void populateWithGroups() {
    	groupsPanel.groupsList.removeAllItems();
    	groupSelector.removeAllItems();
    	currentStudentGroupSelector.removeAllItems();
    	groupsPanel.populateWithGroups();
    }

    /**
     * Intoarce ID-ul unei grupe (ca String) stiind numele,
     * id-ul departamentului si anul asociat grupei sau null
     * @param groupName
     * @return
     */
    private String getGroupIDByName(String groupName) {
    	String condition = "name='" + groupName + "' AND dept_id=" +
    			deptID + " AND year='" + year + "'";
    	Object groupIDObj = dbConnection.getOneValue(Main.TABLE_GROUPS, "id", condition);
    	
    	if (groupIDObj == null)
    		return null;
    	
    	return groupIDObj.toString();
    }

	public void selectionListener() {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    @Override
		    public void valueChanged(ListSelectionEvent event) {
	    		basicSelectionListener();
		        
	    		int selectedRow = table.getSelectedRow();
	    		
	    		if(selectedRow > -1) {
	    			selectedRow = table.convertRowIndexToModel(selectedRow);
	    			accInfoPanel.id.setText(tableModel.getValueAt(selectedRow, 0).toString());

	    			// setare denumire grupa pornind de la ID grupa
	    			Object groupIDObj = tableModel.getValueAt(selectedRow, 27);
	    			if (groupIDObj == null)
	    				currentStudentGroupSelector.setSelectedIndex(0);
	    			else {
	    		    	String condition = "id=" + Integer.valueOf(groupIDObj.toString());
	    		    	Object groupNameObj = dbConnection.getOneValue(Main.TABLE_GROUPS, "name", condition);
	    		    	if (groupNameObj != null)
	    		    		currentStudentGroupSelector.setSelectedItem(groupNameObj.toString());
	    			}

	    			Object imgObj = tableModel.getValueAt(selectedRow, 28);
	    			if (imgObj == null || imgObj.toString().equals("DEFAULT")) {
	    				icon.setIcon(genericStudent);
	    				return;
	    			}

	    			String imageURL = imgObj.toString();
    				String localPath = Utils.homePath + imageURL;

    				// download image
    				if (!new File(localPath).exists()) {
    					boolean downloadSuccess = Utils.downloadImage(imageURL);
    					if(!downloadSuccess) {
    						icon.setIcon(genericStudent);
    						return;
    					}
    				}

    				icon.setIcon(ImageIconLoader.getImageIcon(localPath, 128, false));
	    		}
	    	}
		});	
	}

    private void addActionListener() {
		add.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	Vector<Object> row = new Vector<> ();

		    	// verific ca toate field-urile sunt completate
		    	boolean addSuccess = basicAddListener(row);
		    	if (!addSuccess)
		    		return;

		    	// verific ca adresa de email este valida
		    	String emailAddr = row.get(24).toString();
		    	if (!Validator.isValidEmailAddress(emailAddr)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid email address!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// asigur unicitatea email-urilor
		    	if (!uniqueEmail(emailAddr, null)) {
		    		JOptionPane.showMessageDialog(null, "Typed email is already associated " +
		    				"with an account. Please try another!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;		    		
		    	}

		    	// verific ca numarul de telefon este valid
		    	String phoneNo = row.get(25).toString();
		    	if (!Validator.isValidPhoneNumber(phoneNo)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid phone number or wrong format!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// verific ca anul de admitere este valid
		    	String amdYear = row.get(20).toString();
		    	if (!Validator.isValidYear(amdYear)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid admission year!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// verific ca media de admitere este valida
		    	String grade = row.get(22).toString();
		    	if (!Validator.isValidGrade(grade)) {
		    		JOptionPane.showMessageDialog(null, "Not a admission grade!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// setez manual campurile invizibile
		    	row.add(0, null);
		    	row.add(26, "DEFAULT");
		    	row.add(28, deptID);

		    	// adaugare ID grupa student
		    	String groupName = currentStudentGroupSelector.getSelectedItem().toString();
		    	String groupID = getGroupIDByName(groupName);
		    	if (groupID == null)
		    		row.add(29, -1);
		    	else
		    		row.add(29, groupID);

		    	row.add(30, year);

		    	// adauga path-ul catre imagine (din computer)
		    	if(browseFrame.getFilePath() == null)
		    		row.add("DEFAULT");
		    	else {
		    		try {
		    			String imageURL = Utils.uploadImage(browseFrame.getFilePath());
						row.add(imageURL);
					} catch (Exception e1) {
						System.out.println(e1);
					}
		    	}

		    	// adaug intrarea in tabela de pe server
		    	int newID = dbConnection.insertEntry(Main.TABLE_STUDENTS, row);

				if (!groupName.equals(displayedGroupName)) {
					browseFrame.clearFilePath();
					return;
				}

		    	// seteaza in tabela din GUI id-ul intrarii adaugate
		    	row.set(0, newID);

		    	// sterge campurile invizibile
		    	row.remove(30);
		    	row.remove(28);
		    	row.remove(26);

		    	// adauga in tabela intrarea
		    	tableModel.addRow(row);

		    	clearAction();
		    }
		});
	}

    private void deleteActionListener() {
    	delete.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	int[] selectedRows = table.getSelectedRows();
		    	int selectedRow;
		    	String ID;
		    	boolean reallyDelete = basicDeleteListener(selectedRows);
		    	int nrOfDeletedEntries = 0;

		    	if (reallyDelete) {
		    		for (int i = selectedRows.length - 1; i >= 0; i--) {
		    			selectedRow = selectedRows[i];
		    			selectedRow = table.convertRowIndexToModel(selectedRow);
		    			ID = tableModel.getValueAt(selectedRow, 0).toString();
		    			tableModel.removeRow(selectedRow);
		    			dbConnection.deleteEntry(Main.TABLE_STUDENTS, "id=" + ID);
		    			nrOfDeletedEntries++;
		    		}

		    		clearAction();
		    		JOptionPane.showMessageDialog(null, "You have deleted " +
		    				nrOfDeletedEntries + " entries!",
	    					"Success", JOptionPane.INFORMATION_MESSAGE);
	    		}
		    }
		});
    }

	private void updateActionListener() {
		update.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	int selectedRow = table.getSelectedRow();
		    	boolean updateImage = false;
		    	String imageURL = null;

		    	// apasa update fara a selecta o intrare
		    	if (selectedRow < 0)
		    		return;

		    	selectedRow = table.convertRowIndexToModel(selectedRow);

		    	Vector<Object> row = new Vector<> ();
		    	boolean updateSuccess = basicUpdateListener(selectedRow, row);

		    	// exista field necompletat
				if (!updateSuccess)
		    		return;

				// verific ca adresa de email este valida
		    	String emailAddr = row.get(25).toString();
		    	if (!Validator.isValidEmailAddress(emailAddr)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid email address!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// asigur unicitatea email-urilor
		    	if (!uniqueEmail(emailAddr, accInfoPanel.id.getText())) {
		    		JOptionPane.showMessageDialog(null, "Typed email is already associated " +
		    				"with an account. Please try another!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;		    		
		    	}

		    	// verific ca numarul de telefon este valid
		    	String phoneNo = row.get(26).toString();
		    	if (!Validator.isValidPhoneNumber(phoneNo)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid phone number or wrong format!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// verific ca anul de admitere este valid
		    	String amdYear = row.get(21).toString();
		    	if (!Validator.isValidYear(amdYear)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid admission year!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// verific ca media de admitere este valida
		    	String grade = row.get(23).toString();
		    	if (!Validator.isValidGrade(grade)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid admission grade!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// updatez in tabelul din gui informatia
				tableModel.removeRow(selectedRow);
				String groupName = currentStudentGroupSelector.getSelectedItem().toString();
		    	String groupID = getGroupIDByName(groupName);
		    	if (groupID == null)
		    		row.add(29, -1);
		    	else
		    		row.add(29, groupID);

		    	// adauga path-ul catre imagine (din computer)
		    	if(browseFrame.getFilePath() != null)
		    		try {
		    			imageURL = Utils.uploadImage(browseFrame.getFilePath());
						row.add(imageURL);
						updateImage = true;
					} catch (Exception e1) {
						System.out.println(e1);
					}

				// inlocuiesc in GUI linia doar daca grupa nu s-a schimbat
				if (groupName.equals(displayedGroupName))
			    	tableModel.insertRow(selectedRow, row);

		    	// updatez in tabelul server-ului informatia
		    	String updateQuery = "UPDATE " + Main.TABLE_STUDENTS + " SET " +
		    			"first_name='" + row.get(1) + "', last_name='" + row.get(2) +
		    			"', father_initials='" + row.get(3) + "', birth_date='" + row.get(4) +
		    			"', gender='" + row.get(5) + "', nationality='" + row.get(6) +
		    			"', birth_country='" + row.get(7) + "', birth_city='" +
		    			row.get(8) + "', father_first_name='" + row.get(9) +
		    			"', father_last_name='" + row.get(10) + "', mother_first_name='" +
		    			row.get(11) + "', mother_last_name='" + row.get(12) + "', marital_status='" +
		    			row.get(13) + "', current_country='" + row.get(14) + "', current_city='" +
		    			row.get(15) + "', street='" + row.get(16) +"', apt_number=" + row.get(17) +
		    			", postal_code='" + row.get(18) + "', iban='" + row.get(19) + "', bank_name='" +
		    			row.get(20) + "', admission_year=" + row.get(21) + ", admission_type='" +
		    			row.get(22) + "', admission_grade=" + row.get(23) + ", taxis='" +
		    			row.get(24) + "', email='" + row.get(25) + "', phone='" + row.get(26) +
		    			"', group_id=" + groupID;
		    	if (updateImage)
		    		updateQuery += ", image='" + imageURL + "'";
		    	updateQuery += " WHERE id=" + accInfoPanel.id.getText();

		    	dbConnection.executeQuery(updateQuery);

		    	browseFrame.clearFilePath();
		    }
		});
	}

    private void clearAction() {
        clearFields();
        groupsPanel.groupsList.setSelectedIndex(0);
        groupsPanel.groupName.setText(null);
        icon.setIcon(genericStudent);
        accInfoPanel.id.setText(null);
        currentStudentGroupSelector.setSelectedIndex(0);
        browseFrame.clearFilePath();
    }

	private void clearActionListener() {
		clear.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	clearAction();
		    }
		});
	}
	
	private void selectActionListener() {
		select.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	String selectedGroupName = groupSelector.getSelectedItem().toString();

		    	// se solicita tabelul deja afisat
		    	if (selectedGroupName.equals("None") || selectedGroupName.equals(displayedGroupName))
		    		return;

		    	String groupID = getGroupIDByName(selectedGroupName);
		    	
		    	if (groupID == null)
		    		return;

				String retrieveQuery = Main.TABLE_STUDENTS_QUERY + 
						" WHERE dept_id=" + deptID + " AND year='" +
						year + "' AND group_id=" + groupID;
		    	createTable(Main.TABLE_STUDENTS, retrieveQuery);
		    	hideColumns();
		    	mouseListener();
				selectionListener();
				displayedGroupName = selectedGroupName;
				clearAction();
		    }
		});
	}
	
	private void logoutActionListener() {
		logout.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent ev) {		    	
		    	int dialogButton = JOptionPane.YES_NO_OPTION;
	    		int dialogResult = JOptionPane.showConfirmDialog
	    				(null, "Are you sure that you want to logout?\n" +
	    					   "Any unsaved changes will be lost.", "Warning", dialogButton);
	    		if(dialogResult == JOptionPane.YES_OPTION) {
	    			clearAction();
					notifyAllObservers(Main.TABLE_LOGIN);
	    		}
		    }
		});
	}

	public void mouseListener() {
		table.addMouseListener(new MouseAdapter() {			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
			    	if (gradesFrame != null && gradesFrame.isActive())
			    		return;

					if (table.getSelectedRow() < 0)
						return;

					final int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							Vector<Vector<Vector<Object>>> allSemesters = new Vector<> ();
							Vector<Vector<Object>> sem;
							Vector<String> columns;
							String cond = "dept_id=" + deptID + " AND year='" + year + "'";
							Object nrOfSemestersObj = dbConnection.getOneValue(Main.TABLE_COURSES, "MAX(semester)", cond);

							if (nrOfSemestersObj == null) {
					    		JOptionPane.showMessageDialog(null, "No entries for " + year + "!",
				    					"Information", JOptionPane.INFORMATION_MESSAGE);
								return;
							}

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
							
							String studID = table.getValueAt(selectedRow, 0).toString();
							gradesFrame = new GradesFrame(year, allSemesters, studID, dbConnection);
						}
					});
				}
			}
		});
	}
}