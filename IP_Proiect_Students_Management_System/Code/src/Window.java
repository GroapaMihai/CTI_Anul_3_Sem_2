import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Components.DateAndTime;
import Components.Design;

public class Window extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private JPanel panelCont;
	private CardLayout cl;
	private LoginPanel loginPanel;
	private ChangePasswordPanel cpPanel;
	private UnivPanel univPanel;
	private AdminsPanel adminsPanel;
	private FacultiesPanel facPanel;
	private DepartmentsPanel depPanel;
	private SecretariesPanel secPanel;
	private CoursesPanel coursesPanel;
	private StudentsPanel studPanel;
	private StudentProfilePanel studProfilePanel;
	private ArrayList<DateAndTime> timers;
	
	public Window() {
		try {
			setIconImage(ImageIO.read(ClassLoader.getSystemResource(Design.genericUniversity)));
		} catch (IOException e) {
			System.out.println(e);
		}

		timers = new ArrayList<> ();
		panelCont = new JPanel();
		loginPanel = new LoginPanel(this);
		cl = new CardLayout();
		panelCont.setLayout(cl);
		panelCont.add(loginPanel.getPanel(), Main.TABLE_LOGIN);
		
		updateLoginPanel();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		add(panelCont);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		    	int dialogButton = JOptionPane.YES_NO_OPTION;
	    		int dialogResult = JOptionPane.showConfirmDialog
	    				(null, "Are you sure that you want to exit?\n" +
	    					"Any unsaved changes will be lost.", "Warning", dialogButton);
	    		if(dialogResult == JOptionPane.YES_OPTION) {
	    			stopTimers();
	    			dispose();
	    		}
		    }
		});
	}

	private void stopTimers() {
		for(DateAndTime timer : timers)
			timer.stopTimer();
	}

	@Override
	public void update(String nextPanelName) {
		if(nextPanelName.equals(Main.NO_INTERNET)) {
		   JOptionPane.showMessageDialog(null, "No Internet Access!",
					"Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} else if(nextPanelName.equals(Main.TABLE_LOGIN))
			updateLoginPanel();
		else if(nextPanelName.equals(Main.TABLE_CHANGE_PWD))
			updateChangePasswordPanel();
		else if(nextPanelName.equals(Main.TABLE_UNIVERSITIES)) {
			Dimension dim = univPanel.getDimension();
			displayPanel(dim, Main.TABLE_UNIVERSITIES);
		} else if (nextPanelName.equals(Main.TABLE_FACULTIES)) {
			Dimension dim = facPanel.getDimension();
			displayPanel(dim, Main.TABLE_FACULTIES);
		} else if (nextPanelName.equals(Main.TABLE_DEPARTMENTS)) {
			Dimension dim = depPanel.getDimension();
			displayPanel(dim, Main.TABLE_DEPARTMENTS);
		} else if (nextPanelName.equals(Main.TABLE_SECRETARIES)) {
			Dimension dim = secPanel.getDimension();
			displayPanel(dim, Main.TABLE_SECRETARIES);
		}
	}

	@Override
	public void update(String nextPanelName, ArrayList<String> args) {
		if (nextPanelName.equals(Main.TABLE_UNIVERSITIES))
			updateUnivPanel();
		if (nextPanelName.equals(Main.TABLE_ADMINS))
			updateAdminsPanel(args.get(0), args.get(1));
		else if (nextPanelName.equals(Main.TABLE_FACULTIES))
			updateFacultiesPanel(args.get(0), args.get(1));
		else if (nextPanelName.equals(Main.TABLE_DEPARTMENTS))
			updateDepartmentsPanel(args.get(0), args.get(1));
		else if (nextPanelName.equals(Main.TABLE_SECRETARIES))
			updateSecretariesPanel(args.get(0), args.get(1));
		else if (nextPanelName.equals(Main.TABLE_COURSES))
			updateAnnualPlansPanel(args.get(0), args.get(1));
		else if (nextPanelName.equals(Main.TABLE_STUDENTS)) {
			updateStudentsPanel(args);
		} else if (nextPanelName.equals(Main.STUDENT_PROFILE))
			updateStudentProfilePanel(args);
	}

	private void displayPanel(Dimension dim, String tableName) {
		setTitle(tableName);
    	cl.show(panelCont, tableName);
    	setMinimumSize(dim);
    	setSize(dim);
    	setLocationRelativeTo(null);
	}

	private void updateLoginPanel() {
		Dimension dim = loginPanel.getDimension();
		displayPanel(dim, Main.TABLE_LOGIN);
	}
	
	private void updateChangePasswordPanel() {
    	if(cpPanel == null) {
    		cpPanel = new ChangePasswordPanel(this);
    		panelCont.add(cpPanel.getPanel(), Main.TABLE_CHANGE_PWD);
    	}

    	Dimension dim = cpPanel.getDimension();
    	displayPanel(dim, Main.TABLE_CHANGE_PWD);
	}

	private void updateUnivPanel() {
		if(univPanel == null) {
			univPanel = new UnivPanel(this);
			timers.add(univPanel.dateAndTime);
			panelCont.add(univPanel.getPanel(), Main.TABLE_UNIVERSITIES);
		}

		univPanel.updateUserName();
		univPanel.updateAccountType();

		Dimension dim = univPanel.getDimension();
		displayPanel(dim, Main.TABLE_UNIVERSITIES);
	}
	
	private void updateAdminsPanel(String ID, String name) {
		if(adminsPanel == null) {
			adminsPanel = new AdminsPanel(this, ID);
			adminsPanel.addElements();
			adminsPanel.createAssoc();
			timers.add(adminsPanel.dateAndTime);
			panelCont.add(adminsPanel.getPanel(), Main.TABLE_ADMINS);
		} else {
			adminsPanel.createTable(Main.TABLE_ADMINS, Main.TABLE_ADMINS_QUERY + " WHERE univ_id=" + ID);
			adminsPanel.selectionListener();
		}

		adminsPanel.addSorters();
		adminsPanel.hideColumns();
		adminsPanel.updateTitle(name);
		adminsPanel.setUnivID(ID);
		adminsPanel.updateUnivLogo();
		adminsPanel.updateUserName();
		adminsPanel.updateAccountType();
		
		Dimension dim = univPanel.getDimension();
		displayPanel(dim, Main.TABLE_ADMINS);
	}
	
	private void updateFacultiesPanel(String ID, String name) {
		String retrieveQuery = Main.TABLE_FACULTIES_QUERY + 
				" WHERE univ_id=" + ID;

		if (facPanel == null) {
			facPanel = new FacultiesPanel(this, ID, retrieveQuery);
			timers.add(facPanel.dateAndTime);
			panelCont.add(facPanel.getPanel(), Main.TABLE_FACULTIES);
		} else {
			facPanel.createTable(Main.TABLE_FACULTIES, retrieveQuery);
			facPanel.mouseListener();
			facPanel.selectionListener();
		}

		facPanel.addSorters();
		facPanel.hideColumns();
		facPanel.updateTitle(name);
		facPanel.updateUnivID(ID);
		facPanel.updateUnivLogo();
		facPanel.updateUserName();
		facPanel.updateAccountType();
		facPanel.updateAdminImage();

		Dimension dim = facPanel.getDimension();
		displayPanel(dim, Main.TABLE_FACULTIES);
	}
	
	private void updateDepartmentsPanel(String ID, String name) {
		String retrieveQuery = Main.TABLE_DEPARTMENTS_QUERY + 
				" WHERE faculty_id=" + ID;

		if (depPanel == null) {
			depPanel = new DepartmentsPanel(this, ID, retrieveQuery);
			timers.add(depPanel.dateAndTime);
			panelCont.add(depPanel.getPanel(), Main.TABLE_DEPARTMENTS);
		} else {
			depPanel.createTable(Main.TABLE_DEPARTMENTS, retrieveQuery);
			depPanel.mouseListener();
			depPanel.selectionListener();
		}

		depPanel.addSorters();
		depPanel.hideColumns();
		depPanel.updateTitle(name);
		depPanel.updateFacultyID(ID);
		depPanel.updateFacultyLogo();
		depPanel.updateUserName();
		depPanel.updateAccountType();
		depPanel.updateAdminImage();

		Dimension dim = depPanel.getDimension();
		displayPanel(dim, Main.TABLE_DEPARTMENTS);
	}
	
	private void updateSecretariesPanel(String ID, String name) {
		String retrieveQuery = Main.TABLE_SECRETARIES_QUERY + 
				" WHERE dept_id=" + ID;

		if (secPanel == null) {
			secPanel = new SecretariesPanel(this, ID, retrieveQuery);
			timers.add(secPanel.dateAndTime);
			panelCont.add(secPanel.getPanel(), Main.TABLE_SECRETARIES);
		} else {
			secPanel.createTable(Main.TABLE_SECRETARIES, retrieveQuery);
			secPanel.selectionListener();
		}

		secPanel.addSorters();
		secPanel.hideColumns();
		secPanel.updateTitle(name);
		secPanel.updateYearsOfStudy();
		secPanel.updateDeptID(ID);
		secPanel.updateDeptLogo();
		secPanel.updateUserName();
		secPanel.updateAccountType();
		secPanel.updateAdminImage();

		Dimension dim = secPanel.getDimension();
		displayPanel(dim, Main.TABLE_SECRETARIES);
	}
	
	private void updateAnnualPlansPanel(String ID, String name) {
		String retrieveQuery = Main.TABLE_COURSES_QUERY + 
				" WHERE 1=2";

		if (coursesPanel == null) {
			coursesPanel = new CoursesPanel(this, ID, retrieveQuery);
			timers.add(coursesPanel.dateAndTime);
			panelCont.add(coursesPanel.getPanel(), Main.TABLE_COURSES);
		} else {
			coursesPanel.createTable(Main.TABLE_COURSES, retrieveQuery);
			coursesPanel.selectionListener();
			coursesPanel.populateYearSpinner();
		}

		coursesPanel.addSorters();
		coursesPanel.updateTitle(name);
		coursesPanel.updateDeptID(ID);
		coursesPanel.updateDeptLogo();
		coursesPanel.updateUserName();
		coursesPanel.updateAccountType();
		coursesPanel.updateAdminImage();

		Dimension dim = coursesPanel.getDimension();
		displayPanel(dim, Main.TABLE_COURSES);
	}
	
    private void updateStudentsPanel(ArrayList<String> args) {
        String retrieveQuery = String.valueOf(Main.TABLE_STUDENTS_QUERY) +
        		" WHERE 1 = 2";

        if (studPanel == null) {
            studPanel = new StudentsPanel(this, args, retrieveQuery);
            timers.add(studPanel.dateAndTime);
            panelCont.add(studPanel.getScrollPane(), Main.TABLE_STUDENTS);
        } else {
            studPanel.createTable(Main.TABLE_STUDENTS, retrieveQuery);
            studPanel.selectionListener();
            studPanel.updateDeptID(args.get(0));
            studPanel.updateYear(args.get(2));
            studPanel.populateWithGroups();
        }

        studPanel.addSorters();
        studPanel.updateTitle(args.get(1) + " - " + args.get(2));
        studPanel.updateUserName();
        studPanel.updateAccountType();
        studPanel.updateSecretaryImage();
        studPanel.updateDeptLogo();
        studPanel.hideColumns();

        Dimension dim = studPanel.getDimension();
        displayPanel(dim, Main.TABLE_STUDENTS);
    }
    
    private void updateStudentProfilePanel(ArrayList<String> args) {
    	if (studProfilePanel == null) {
    		studProfilePanel = new StudentProfilePanel(this, args);
            timers.add(studProfilePanel.dateAndTime);
            panelCont.add(studProfilePanel.getScrollPane(), Main.STUDENT_PROFILE);
    	} else {
    		studProfilePanel.removeSemestersPanels();
        	studProfilePanel.updateDeptID(args.get(0));
        	studProfilePanel.updateYear(args.get(1));
    		studProfilePanel.updateAllSemestersPanels();
    	}

    	studProfilePanel.addSemestersPanels();
    	studProfilePanel.updateUserName();
    	studProfilePanel.updateAccountType();
    	studProfilePanel.updateTitle();
    	studProfilePanel.updateStudentImage();
    	studProfilePanel.populateFields();

        Dimension dim = studProfilePanel.getDimension();
        displayPanel(dim, Main.STUDENT_PROFILE);
    }
}
