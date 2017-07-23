import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import Components.*;

public class DepartmentsPanel extends BasicPanel {
	private JRoundedPanel infoPanel;
	private JScaledTextPane title;
	private JButton back;
	private JLabel idLabel;
	private JRoundedTextField id;
	private JLabel nameLabel;
	private JRoundedTextField name;
	private JLabel addressLabel;
	private JRoundedTextField address;
	private JLabel phoneLabel;
	private JRoundedTextField phone;
	private JLabel yearsOfStudyLabel;
	private JComboBox<String> yearsOfStudy;
	private JLabel nrOfStudentsLabel;
	private JRoundedTextField nrOfStudents;
	private JLabel adminImage;
	private JLabel facultyLogo;
	private String facultyID;
	private String imageURL;
	private ImageIcon genericAdminImage;
	private ImageIcon genericFacultyIcon;

	public DepartmentsPanel(Window window, String facultyID, String retrieveQuery) {
		super(window);
		panel.setBackground(Design.mustard);
		panelWidth = 950;
		panelHeight = 720;
		this.facultyID = facultyID;

		title = new JScaledTextPane("Serif", 22, Color.BLACK);
		createTable(Main.TABLE_DEPARTMENTS, retrieveQuery);
		
		genericAdminImage = ImageIconLoader.getImageIcon(Design.genericAdmin, 64, true);
		genericFacultyIcon = ImageIconLoader.getImageIcon(Design.genericFaculty, 128, true);
		adminImage = new JLabel();
		facultyLogo = new JLabel();
		facultyLogo.setIcon(genericFacultyIcon);

		back = new JButton("Back");
		back.setIcon(ImageIconLoader.getImageIcon(Design.back, 24, true));
		backActionListener();

		createAccountInfoBox(Design.papaya);
		createInfoPanel();
		createAssoc();
		addElements();
		mouseListener();
		selectionListener();
		addActionListener();
		deleteActionListener();
		updateActionListener();
		clearActionListener();
		logoutActionListener();
	}
	
	public void addSorters() {
		table.setIntComparatorAt(0);
	}

	public void hideColumns() {
		table.hideColumn(5);
		table.hideColumn(6);
	}

	public void updateFacultyID(String facultyID) {
		this.facultyID = facultyID;
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

	public void updateFacultyLogo() {
		if (facultyID == null) {
			facultyLogo.setIcon(genericFacultyIcon);
			return;
		}

		String condition = "id = " + facultyID;
		Object logoObj = dbConnection.getOneValue(Main.TABLE_FACULTIES, "logo", condition);
		
		if (logoObj == null) {
			facultyLogo.setIcon(genericFacultyIcon);
			return;
		}

		String logoName = logoObj.toString();

		if (logoName.equals("DEFAULT"))
			facultyLogo.setIcon(genericFacultyIcon);
		else {
			String imagePath = Utils.homePath + logoName;

        	// download image
			if (!new File(imagePath).exists()) {
				boolean downloadSuccess = Utils.downloadImage(logoName);
				if (!downloadSuccess) {
					facultyLogo.setIcon(genericFacultyIcon);
					return;
				}
			}

			facultyLogo.setIcon(ImageIconLoader.getImageIcon(imagePath, 128, false));
		}
	}

	private void createInfoPanel() {
		infoPanel = new JRoundedPanel();
		infoPanel.setLayout(new MigLayout("insets 10, align 50% 50%"));
		infoPanel.setBackground(Design.papaya);
		
		createLabels();
		createFields();
		
		icon.setIcon(genericFacultyIcon);
		
		infoPanel.add(icon);
		infoPanel.add(browse, "bottom, wrap");
		infoPanel.add(Box.createVerticalStrut(15), "wrap");
		infoPanel.add(idLabel);
		infoPanel.add(id, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(5), "wrap");
		infoPanel.add(nameLabel);
		infoPanel.add(name, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(5), "wrap");
		infoPanel.add(addressLabel);
		infoPanel.add(address, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(5), "wrap");
		infoPanel.add(phoneLabel);
		infoPanel.add(phone, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(5), "wrap");
		infoPanel.add(yearsOfStudyLabel);
		infoPanel.add(yearsOfStudy, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(5), "wrap");
		infoPanel.add(nrOfStudentsLabel);
		infoPanel.add(nrOfStudents, "w 200, wrap");
	}
	
	private void createLabels() {
		idLabel = Design.getCustomJLabel("ID", Design.tahomaBold16);
		nameLabel = Design.getCustomJLabel("Name", Design.tahomaBold16);
		addressLabel = Design.getCustomJLabel("Address", Design.tahomaBold16);
		phoneLabel = Design.getCustomJLabel("Phone number", Design.tahomaBold16);
		yearsOfStudyLabel = Design.getCustomJLabel("Years of study", Design.tahomaBold16);
		nrOfStudentsLabel = Design.getCustomJLabel("Nr. of students", Design.tahomaBold16);
	}
	
	private void createFields() {
		id = new JRoundedTextField(16);
		id.setEditable(false);
		id.setFocusable(false);
		id.setFont(Design.tahomaPlain16);

		name = new JRoundedTextField(16);
		address = new JRoundedTextField(16);
		phone = new JRoundedTextField(16);
		name.setFont(Design.tahomaPlain16);
		address.setFont(Design.tahomaPlain16);
		phone.setFont(Design.tahomaPlain16);
		
		yearsOfStudy = new JComboBox<> ();
		yearsOfStudy.addItem("None");
		yearsOfStudy.setSelectedItem("None");
		for (int i = 1; i <= 8; i++)
			yearsOfStudy.addItem(String.valueOf(i));
			
		nrOfStudents = Design.getImmutableTextField(16, Design.tahomaPlain14);
	}
	
	public void addElements() {
		panel.add(dateAndTime.getDateAndTimePanel(Design.papaya));
		panel.add(accountInfo, "split2, right");
		panel.add(adminImage);
		panel.add(Box.createHorizontalStrut(25));
		panel.add(logout, "wrap");
		panel.add(title, "spanx4, center, wrap");
		panel.add(Box.createVerticalStrut(5), "wrap");
		panel.add(infoPanel, "spany2");
		panel.add(facultyLogo, "spanx3, right, wrap");
		panel.add(tableScrollPane, "spanx3, push, grow, wrap");
		panel.add(back, "split4, center");
		panel.add(add);
		panel.add(delete);
		panel.add(update);
		panel.add(clear, "split4");
		panel.add(Box.createHorizontalStrut(25));
		panel.add(searchField, "right, pushx, growx");
		panel.add(search);
	}
	
	private void createAssoc() {
		fieldsMap.put(1, name);
		fieldsMap.put(2, address);
		fieldsMap.put(3, phone);
		fieldsMap.put(4, yearsOfStudy);
	}
	
	private void backActionListener() {
		back.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	clearAction();
		    	notifyAllObservers(Main.TABLE_FACULTIES);
		    }
		});
	}

	// dublu click -> trec la SecretariesPanel
	public void mouseListener() {
		table.addMouseListener(new MouseAdapter() {			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int selectedRow = table.getSelectedRow();
					
					if (selectedRow < 0)
						return;

					String deptID = tableModel.getValueAt(selectedRow, 0).toString();
					String deptName = tableModel.getValueAt(selectedRow, 1).toString();
					clearAction();
					ArrayList<String> args = new ArrayList<> ();
					args.add(deptID);
					args.add(deptName);
					notifyAllObservers(Main.TABLE_SECRETARIES, args);
				}
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
    			String deptID = tableModel.getValueAt(selectedRow, 0).toString();
    			id.setText(deptID);

    			// numar cati studenti sunt la departamentul selectat
    			String condition = "dept_id=" + deptID;
    			Integer countResult = dbConnection.executeCountQuery(Main.TABLE_STUDENTS, condition);
    			nrOfStudents.setText(String.valueOf(countResult));
    			
    			Object imgObj = tableModel.getValueAt(selectedRow, 6);
    			if(imgObj == null || imgObj.toString().equals("DEFAULT")) {
    				icon.setIcon(genericFacultyIcon);
    				return;
    			}

    			imageURL = imgObj.toString();
				String localPath = Utils.homePath + imageURL;

				// download image
				if (!new File(localPath).exists()) {
					boolean downloadSuccess = Utils.downloadImage(imageURL);
					if(!downloadSuccess) {
						icon.setIcon(genericFacultyIcon);
						return;
					}
				}

				icon.setIcon(ImageIconLoader.getImageIcon(localPath, 128, false));
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
		    	
		    	// verific ca numarul de telefon este valid
		    	String phoneNo = row.get(2).toString();
		    	if (!Validator.isValidPhoneNumber(phoneNo)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid phone number or wrong format!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// adauga path-ul catre imagine (din computer)
		    	if(browseFrame.getFilePath() == null)
		    		row.add("DEFAULT");
		    	else {
		    		try {
		    			imageURL = Utils.uploadImage(browseFrame.getFilePath());
						row.add(imageURL);
					} catch (Exception e1) {
						System.out.println(e1);
					}
		    	}

		    	// adaug intrarea in tabela de pe server
		    	row.add(0, null);
		    	row.add(5, facultyID);
		    	int newID = dbConnection.insertEntry(Main.TABLE_DEPARTMENTS, row);
		    	
		    	// seteaza in tabela din GUI id-ul intrarii adaugate
		    	row.set(0, newID);
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
		    			dbConnection.deleteEntry(Main.TABLE_DEPARTMENTS, "id=" + ID);
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

		    	String phoneNo = row.get(3).toString();
		    	if (!Validator.isValidPhoneNumber(phoneNo)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid phone number or wrong format!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// adauga path-ul catre imagine (din computer)
		    	if (browseFrame.getFilePath() != null) {
		    		try {
		    			imageURL = Utils.uploadImage(browseFrame.getFilePath());
		    			row.set(row.size() - 1, imageURL);
					} catch (Exception e1) {
						System.out.println(e1);
					}
		    	}

		    	// updatez in tabelul din gui informatia
		    	tableModel.removeRow(selectedRow);
		    	tableModel.insertRow(selectedRow, row);

		    	// updatez in tabelul server-ului informatia
		    	dbConnection.updateEntry(Main.TABLE_DEPARTMENTS, row, "id=" + id.getText());
		    	
		    	browseFrame.clearFilePath();
		    }
		});
	}

	private void clearAction() {
		clearFields();
    	id.setText(null);
    	nrOfStudents.setText(null);
    	icon.setIcon(genericFacultyIcon);
    	browseFrame.clearFilePath();
	}

	private void clearActionListener() {
		clear.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
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
}
