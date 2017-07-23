import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import Components.*;

public class CoursesPanel extends BasicPanel {
	private JRoundedPanel infoPanel;
	private JScaledTextPane title;
	private JButton back;
	private JLabel idLabel;
	private JRoundedTextField id;
	private JLabel nameLabel;
	private JRoundedTextField name;
	private JLabel semesterLabel;
	private JNumberTextField semester;
	private JLabel teacherLabel;
	private JRoundedTextField teacher;
	private JLabel typeLabel;
	private JComboBox<String> type;
	private JLabel creditsLabel;
	private JNumberTextField credits;
	private JLabel adminImage;
	private String deptID;
	private ImageIcon genericAdminImage;
	private ImageIcon genericFacultyIcon;
	private JComboBox<String> yearSpinner;
	private JButton select;
	private JButton view;
	private String displayedYear;
	private SemesterPlansFrame semPlans;

	public CoursesPanel(Window window, String deptID, String retrieveQuery) {
		super(window);
		panel.setBackground(Design.mustard);
		panelWidth = 950;
		panelHeight = 720;
		this.deptID = deptID;
		displayedYear = "None";

		title = new JScaledTextPane("Serif", 22, Color.BLACK);
		createTable(Main.TABLE_COURSES, retrieveQuery);

		adminImage = new JLabel();
		genericAdminImage = ImageIconLoader.getImageIcon(Design.genericAdmin, 64, true);
		genericFacultyIcon = ImageIconLoader.getImageIcon(Design.genericFaculty, 128, true);
		icon.setIcon(genericFacultyIcon);

		back = new JButton("Back");
		back.setIcon(ImageIconLoader.getImageIcon(Design.back, 24, true));
		backActionListener();

		select = new JButton("Select");
		select.setIcon(ImageIconLoader.getImageIcon(Design.select, 24, true));

		view = new JButton("Annual plan");
		view.setIcon(ImageIconLoader.getImageIcon(Design.viewContract, 24, true));

		yearSpinner = new JComboBox<String>();
		populateYearSpinner();

		createAccountInfoBox(Design.papaya);
		createInfoPanel();
		createAssoc();
		addElements();
		selectionListener();
		addActionListener();
		deleteActionListener();
		updateActionListener();
		clearActionListener();
		selectActionListener();
		viewActionListener();
		logoutActionListener();
	}
	
	public void addSorters() {
		table.setIntComparatorAt(0);
		table.setIntComparatorAt(2);
		table.setIntComparatorAt(4);
	}

	public void populateYearSpinner() {
		String condition = "id= " + deptID;
		Object yearsObj = dbConnection.getOneValue(Main.TABLE_DEPARTMENTS, "years_of_study", condition);
		
		if (yearsObj == null)
			return;

		yearSpinner.removeAllItems();
		yearSpinner.addItem("None");

		for (int i = 1; i <= Integer.valueOf(yearsObj.toString()); i++)
			yearSpinner.addItem("Year " + i);
	}

	public void updateDeptID(String deptID) {
		this.deptID = deptID;
	}

	public void updateTitle(String name) {
		title.setText(name);
	}

	public void updateAdminImage() {
		String condition = "id = " + Main.userID;
		Object imagObj = dbConnection.getOneValue(Main.TABLE_ADMINS, "image", condition);
		
		if (imagObj == null) {
			adminImage.setIcon(genericAdminImage);
			return;
		}

		String imageName = imagObj.toString();

		if (imageName.equals("DEFAULT"))
			adminImage.setIcon(genericAdminImage);
		else {
			String imagePath = Utils.homePath + imageName;

        	// download image
			if (!new File(imagePath).exists()) {
				boolean downloadSuccess = Utils.downloadImage(imageName);
				if (!downloadSuccess) {
					adminImage.setIcon(genericAdminImage);
					return;
				}
			}

			adminImage.setIcon(ImageIconLoader.getImageIcon(imagePath, 64, false));
		}
	}

	public void updateDeptLogo() {
		if (deptID == null) {
			icon.setIcon(genericFacultyIcon);
			return;
		}

		String condition = "id = " + deptID;
		Object logoObj = dbConnection.getOneValue(Main.TABLE_DEPARTMENTS, "logo", condition);
		
		if (logoObj == null) {
			icon.setIcon(genericFacultyIcon);
			return;
		}
		
		String logoName = logoObj.toString();

		if (logoName == null)
			icon.setIcon(genericFacultyIcon);
		else {
			String imagePath = Utils.homePath + logoName;

        	// download image
			if (!new File(imagePath).exists()) {
				boolean downloadSuccess = Utils.downloadImage(logoName);
				if (!downloadSuccess) {
					icon.setIcon(genericFacultyIcon);
					return;
				}
			}

			icon.setIcon(ImageIconLoader.getImageIcon(imagePath, 128, false));
		}
	}

	private void createInfoPanel() {
		infoPanel = new JRoundedPanel();
		infoPanel.setLayout(new MigLayout("insets 10, align 50% 50%"));
		infoPanel.setBackground(Design.papaya);

		createLabels();
		createFields();

		infoPanel.add(idLabel);
		infoPanel.add(id, "pushx, growx");
		infoPanel.add(Box.createHorizontalStrut(15));
		infoPanel.add(nameLabel);
		infoPanel.add(name, "pushx, growx, wrap");
		infoPanel.add(semesterLabel);
		infoPanel.add(semester, "pushx, growx");
		infoPanel.add(Box.createHorizontalStrut(15));
		infoPanel.add(teacherLabel);
		infoPanel.add(teacher, "pushx, growx, wrap");
		infoPanel.add(creditsLabel);
		infoPanel.add(credits, "pushx, growx");
		infoPanel.add(Box.createHorizontalStrut(15));
		infoPanel.add(typeLabel);
		infoPanel.add(type, "pushx, growx, wrap");
	}

	private void createLabels() {
		idLabel = Design.getCustomJLabel("ID", Design.tahomaBold16);
		nameLabel = Design.getCustomJLabel("Name", Design.tahomaBold16);
		semesterLabel = Design.getCustomJLabel("Semester", Design.tahomaBold16);
		teacherLabel = Design.getCustomJLabel("Teacher's name", Design.tahomaBold16);
		typeLabel = Design.getCustomJLabel("Type", Design.tahomaBold16);
		creditsLabel = Design.getCustomJLabel("Nr. of credits", Design.tahomaBold16);
	}
	
	private void createFields() {
		id = new JRoundedTextField(16);
		id.setEditable(false);
		id.setFocusable(false);
		id.setFont(Design.tahomaPlain16);

		name = new JRoundedTextField(16);
		semester = new JNumberTextField(16);
		teacher = new JRoundedTextField(16);

		type = new JComboBox<> ();
		type.addItem("None");
		type.addItem("Mandatory");
		type.addItem("Optional");
		type.addItem("Facultative");

		credits = new JNumberTextField(16);
		name.setFont(Design.tahomaPlain16);
		semester.setFont(Design.tahomaPlain16);
		teacher.setFont(Design.tahomaPlain16);
		type.setFont(Design.tahomaPlain16);
		credits.setFont(Design.tahomaPlain16);
		
		semester.setMaxLength(1);
		semester.setPrecision(0);
		semester.setAllowNegative(false);
		credits.setMaxLength(1);
		credits.setPrecision(0);
		credits.setAllowNegative(false);
	}
	
	public void addElements() {
		panel.add(dateAndTime.getDateAndTimePanel(Design.papaya), "split3");
		panel.add(Box.createHorizontalGlue(), "pushx, growx");
		panel.add(accountInfo);
		panel.add(adminImage);
		panel.add(logout, "right, wrap");
		panel.add(Box.createVerticalStrut(10), "wrap");
		panel.add(title, "spanx3, center, wrap");
		panel.add(Box.createVerticalStrut(25), "wrap");
		panel.add(infoPanel, "spanx2, pushx, growx");
		panel.add(icon, "wrap");
		panel.add(add, "split9, spanx3");
		panel.add(delete);
		panel.add(Box.createHorizontalGlue(), "pushx, growx");
		panel.add(select);
		panel.add(yearSpinner);
		panel.add(view);
		panel.add(Box.createHorizontalGlue(), "pushx, growx");
		panel.add(update);
		panel.add(clear, "wrap");
		panel.add(Box.createVerticalStrut(5), "wrap");		
		panel.add(tableScrollPane, "spanx3, push, grow, wrap");		
		panel.add(back, "split4, spanx3");
		panel.add(Box.createHorizontalGlue(), "pushx, growx");
		panel.add(search);
		panel.add(searchField);
	}

	private void createAssoc() {
		fieldsMap.put(1, name);
		fieldsMap.put(2, semester);
		fieldsMap.put(3, teacher);
		fieldsMap.put(4, credits);
		fieldsMap.put(5, type);
	}
	
	private void backActionListener() {
		back.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	clearAction();
		    	notifyAllObservers(Main.TABLE_SECRETARIES);
		    }
		});
	}
	
	public void selectionListener() {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    @Override
		    public void valueChanged(ListSelectionEvent event) {
	    		basicSelectionListener();
	    		int selectedRow = table.getSelectedRow();
	    		
	    		if (selectedRow < 0)
	    			return;

    			selectedRow = table.convertRowIndexToModel(selectedRow);
    			id.setText(tableModel.getValueAt(selectedRow, 0).toString());
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

		    	// adaug intrarea in tabela de pe server
		    	String year = yearSpinner.getSelectedItem().toString();
		    	row.add(0, null);
		    	row.add(6, deptID);
		    	row.add(7, year);
		    	int newID = dbConnection.insertEntry(Main.TABLE_COURSES, row);

		    	// seteaza in tabela din GUI id-ul intrarii adaugate
		    	row.set(0, newID);
		    	if (displayedYear.equals(year))
		    		tableModel.addRow(row);
		    	
		    	browseFrame.clearFilePath();
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
		    			dbConnection.deleteEntry(Main.TABLE_COURSES, "id=" + ID);
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
		    	
		    	// apasa update fara a selecta o intrare
		    	if (selectedRow < 0)
		    		return;

		    	selectedRow = table.convertRowIndexToModel(selectedRow);

		    	Vector<Object> row = new Vector<> ();
		    	boolean updateSuccess = basicUpdateListener(selectedRow, row);

		    	// exista field necompletat
				if (!updateSuccess)
		    		return;

		    	// updatez in tabelul din gui informatia
				String year = yearSpinner.getSelectedItem().toString();
				tableModel.removeRow(selectedRow);

				// inlocuiesc in GUI linia doar daca year nu s-a schimbat
				if (year.equals(displayedYear))
			    	tableModel.insertRow(selectedRow, row);

		    	// updatez in tabelul server-ului informatia
		    	String updateQuery = "UPDATE " + Main.TABLE_COURSES + " SET " +
		    			"name='" + row.get(1) + "', semester=" + row.get(2) +
		    			", teacher='" + row.get(3) + "', credits=" + row.get(4) +
		    			", type='" + row.get(5) + "', year='" +
		    			year + "' WHERE id=" + id.getText();
		    	dbConnection.executeQuery(updateQuery);
		    }
		});
	}

	private void clearAction() {
		clearFields();
    	id.setText(null);
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
		    	String year = yearSpinner.getSelectedItem().toString();

		    	// se solicita tabelul deja afisat
		    	if (year.equals("None") || year.equals(displayedYear))
		    		return;

				String retrieveQuery = Main.TABLE_COURSES_QUERY + 
						" WHERE dept_id=" + deptID + " AND year='" + year + "'";
		    	createTable(Main.TABLE_COURSES, retrieveQuery);
				selectionListener();
				displayedYear = year;
				clearAction();
		    }
		});
	}

	private void viewActionListener() {
		view.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	if (semPlans != null && semPlans.isActive())
		    		return;

		    	String year = yearSpinner.getSelectedItem().toString();
		    	if (year.equals("None"))
		    		return;

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
						
						semPlans = new SemesterPlansFrame(year, allSemesters);
					}
				});
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
}
