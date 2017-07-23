import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import Components.DateLabelFormatter;
import Components.Design;
import Components.ImageIconLoader;
import Components.JRoundedPanel;
import Components.JRoundedTextField;
import Components.JScaledTextPane;
import Components.Utils;
import Components.Validator;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class SecretariesPanel extends BasicPanel {
	private JRoundedPanel infoPanel;
	private JScaledTextPane title;
	private JLabel idLabel;
	private JRoundedTextField id;
	private JLabel firstNameLabel;
	private JRoundedTextField firstName;
	private JLabel lastNameLabel;
	private JRoundedTextField lastName;
	private JLabel birthDateLabel;
	private JDatePickerImpl birthDate;
	private JLabel nationalityLabel;
	private JRoundedTextField nationality;
	private JLabel emailLabel;
	private JRoundedTextField email;
	private JLabel yearLabel;
	private JComboBox<String> year;
	private JLabel phoneLabel;
	private JRoundedTextField phone;
	private JLabel addressLabel;
	private JRoundedTextField address;
	private JButton annualPlans;
	private JButton back;
	private String deptID;
	private String imageURL;
	private JLabel deptLogo;
	private int yearsOfStudy;
	private JLabel adminImage;
	private ImageIcon genericAdminImage;
	private ImageIcon genericDeptIcon;
	private ImageIcon genericSecretaryIcon;
	
	public SecretariesPanel(Window window, String deptID, String retrieveQuery) {
		super(window);
		panel.setBackground(Design.mustard);
		panelWidth = 950;
		panelHeight = 720;
		this.deptID = deptID;

		title = new JScaledTextPane("Serif", 22, Color.BLACK);
		createTable(Main.TABLE_SECRETARIES, retrieveQuery);
		createAccountInfoBox(Design.papaya);
		createInfoPanel();

		adminImage = new JLabel();
		genericAdminImage = ImageIconLoader.getImageIcon(Design.genericAdmin, 64, true);
		genericDeptIcon = ImageIconLoader.getImageIcon(Design.genericFaculty, 128, true);
		genericSecretaryIcon = ImageIconLoader.getImageIcon(Design.genericSecretary, 128, true);

		icon.setIcon(genericSecretaryIcon);
		deptLogo = new JLabel();

		annualPlans = new JButton("Annual Plans");
		annualPlans.setIcon(ImageIconLoader.getImageIcon(Design.annualPlans, 24, true));

		back = new JButton("Back");
		back.setIcon(ImageIconLoader.getImageIcon(Design.back, 24, true));
		backActionListener();

		createAssoc();
		addElements();
		selectionListener();
		addActionListener();
		deleteActionListener();
		updateActionListener();
		clearActionListener();
		annualPlansActionListener();
		logoutActionListener();
	}

	public void addSorters() {
		table.setIntComparatorAt(0);
		table.setDateComparatorAt(3);
	}

	public void hideColumns() {
		table.hideColumn(9);
	}

	public void updateTitle(String deptName) {
		title.setText(deptName);
	}

	public void updateDeptID(String deptID) {
		this.deptID = deptID;
	}
	
	public void updateAdminImage() {
		String condition = "id = " + Main.userID;
		Object imageObj = dbConnection.getOneValue(Main.TABLE_ADMINS, "image", condition);
		
		if (imageObj == null) {
			adminImage.setIcon(genericAdminImage);
			return;
		}

		String imageName = imageObj.toString();
		
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
		String condition = "id = " + deptID;
		Object logoObj = dbConnection.getOneValue(Main.TABLE_DEPARTMENTS, "logo", condition);
		
		if (logoObj == null)
			return;

		String logoName = logoObj.toString();
		
		if (logoName.equals("DEFAULT"))
			deptLogo.setIcon(genericDeptIcon);
		else {
			String imagePath = Utils.homePath + logoName;

        	// download image
			if (!new File(imagePath).exists()) {
				boolean downloadSuccess = Utils.downloadImage(logoName);
				if (!downloadSuccess) {
					deptLogo.setIcon(genericDeptIcon);
					return;
				}
			}

			deptLogo.setIcon(ImageIconLoader.getImageIcon(imagePath, 128, false));	
		}
	}

	public void updateYearsOfStudy() {
		String condition = "id = " + deptID;
		Object yearsOfStudyObj = dbConnection.getOneValue(Main.TABLE_DEPARTMENTS, "years_of_study", condition);
	
		if (yearsOfStudyObj == null)
			return;
		
		int yearsOfStudy = Integer.valueOf(yearsOfStudyObj.toString());
		
		// updatez ComboBox-ul
		if (this.yearsOfStudy != yearsOfStudy) {
			this.yearsOfStudy = yearsOfStudy;
			populateComboBox();
		}
	}

	private void populateComboBox() {
		year.removeAllItems();
		year.addItem("None");

		for (int i = 1; i <= yearsOfStudy; i++)
			year.addItem("Year " + i);
	}
	
	private void createInfoPanel() {
		infoPanel = new JRoundedPanel();
		infoPanel.setLayout(new MigLayout("insets 10, align 50% 50%"));
		infoPanel.setBackground(Design.papaya);

		createLabels();
		createFields();

		infoPanel.add(idLabel, "split2");
		infoPanel.add(id, "w 100");
		infoPanel.add(firstNameLabel, "split2");
		infoPanel.add(firstName);
		infoPanel.add(Box.createHorizontalStrut(15));
		infoPanel.add(lastNameLabel);
		infoPanel.add(lastName, "wrap, pushx, growx");
		infoPanel.add(Box.createVerticalGlue(), "wrap");
		infoPanel.add(birthDateLabel);
		infoPanel.add(birthDate);
		infoPanel.add(Box.createHorizontalStrut(15));
		infoPanel.add(nationalityLabel);
		infoPanel.add(nationality, "wrap, pushx, growx");
		infoPanel.add(Box.createVerticalGlue(), "wrap");
		infoPanel.add(phoneLabel);
		infoPanel.add(phone);
		infoPanel.add(Box.createHorizontalStrut(15));
		infoPanel.add(emailLabel);
		infoPanel.add(email, "wrap, pushx, growx");
		infoPanel.add(Box.createVerticalGlue(), "wrap");
		infoPanel.add(yearLabel);
		infoPanel.add(year);
		infoPanel.add(Box.createHorizontalStrut(15));
		infoPanel.add(addressLabel);
		infoPanel.add(address, "pushx, growx, wrap");
	}
	
	private void createLabels() {
		idLabel = Design.getCustomJLabel("ID", Design.tahomaBold16);
		firstNameLabel = Design.getCustomJLabel("First Name", Design.tahomaBold16);
		lastNameLabel = Design.getCustomJLabel("Last Name", Design.tahomaBold16);
		birthDateLabel = Design.getCustomJLabel("Birth Date", Design.tahomaBold16);
		nationalityLabel = Design.getCustomJLabel("Nationality", Design.tahomaBold16);
		emailLabel = Design.getCustomJLabel("Email", Design.tahomaBold16);
		phoneLabel = Design.getCustomJLabel("Phone", Design.tahomaBold16);
		yearLabel = Design.getCustomJLabel("Secretary for", Design.tahomaBold16);
		addressLabel = Design.getCustomJLabel("Address", Design.tahomaBold16);
	}
	
	private void createFields() {
		id = new JRoundedTextField(16);
		id.setEditable(false);
		id.setFocusable(false);
		
		firstName = new JRoundedTextField(16);
		lastName = new JRoundedTextField(16);
		nationality = new JRoundedTextField(16);

		email = new JRoundedTextField(16);
		phone = new JRoundedTextField(16);
		address = new JRoundedTextField(16);

		year = new JComboBox<> ();
		populateComboBox();
		
		UtilDateModel model = new UtilDateModel();
		model.setSelected(true);
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		birthDate = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		birthDate.getModel().setDate(1980, 0, 1);
	}
	
	public void addElements() {
		panel.add(dateAndTime.getDateAndTimePanel(Design.papaya), "split3");
		panel.add(Box.createHorizontalGlue(), "pushx, growx");
		panel.add(adminImage);
		panel.add(accountInfo, "center, wrap");
		panel.add(annualPlans);
		panel.add(logout, "right, wrap");
		panel.add(Box.createHorizontalGlue(), "spanx2, split3, pushx, growx");
		panel.add(title, "center");
		panel.add(Box.createHorizontalGlue(), "pushx, growx");
		panel.add(Box.createVerticalStrut(30), "wrap");
		panel.add(infoPanel, "center, pushx, growx");
		panel.add(icon, "split2, right");
		panel.add(browse, "bottom, wrap");
		panel.add(tableScrollPane, "push, grow");
		panel.add(deptLogo, "center, wrap");
		panel.add(Box.createVerticalStrut(5), "wrap");
		
		panel.add(back, "split8, center");
		panel.add(Box.createHorizontalGlue(), "pushx, growx");
		panel.add(add);
		panel.add(delete);
		panel.add(update);
		panel.add(clear);
		panel.add(Box.createHorizontalGlue(), "pushx, growx");
		panel.add(search);
		
		panel.add(searchField);
	}
	
	public void createAssoc() {
		fieldsMap.put(1, firstName);
		fieldsMap.put(2, lastName);
		fieldsMap.put(3, birthDate);
		fieldsMap.put(4, nationality);
		fieldsMap.put(5, email);
		fieldsMap.put(6, year);
		fieldsMap.put(7, phone);
		fieldsMap.put(8, address);
	}
	
	private void backActionListener() {
		back.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	clearAction();
		    	notifyAllObservers(Main.TABLE_DEPARTMENTS);
		    }
		});
	}
	
	public void selectionListener() {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    @Override
		    public void valueChanged(ListSelectionEvent event) {
	    		basicSelectionListener();
		        
	    		int selectedRow = table.getSelectedRow();
	    		
	    		if(selectedRow > -1) {
	    			selectedRow = table.convertRowIndexToModel(selectedRow);
	    			id.setText(tableModel.getValueAt(selectedRow, 0).toString());

	    			Object imgObj = tableModel.getValueAt(selectedRow, 9);
	    			if(imgObj == null || imgObj.toString().equals("DEFAULT")) {
	    				icon.setIcon(genericSecretaryIcon);
	    				return;
	    			}

	    			imageURL = imgObj.toString();
    				String localPath = Utils.homePath + imageURL;

    				// download image
    				if (!new File(localPath).exists()) {
    					boolean downloadSuccess = Utils.downloadImage(imageURL);
    					if (!downloadSuccess) {
    						icon.setIcon(genericSecretaryIcon);
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
		    	String emailAddr = row.get(4).toString();
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
		    	String phoneNo = row.get(6).toString();
		    	if (!Validator.isValidPhoneNumber(phoneNo)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid phone number or wrong format!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// setez manual campurile invizibile
		    	row.add(0, null);
		    	row.add(5, deptID);
		    	row.add(6, "DEFAULT");

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
		    	int newID = dbConnection.insertEntry(Main.TABLE_SECRETARIES, row);

		    	// seteaza in tabela din GUI id-ul intrarii adaugate
		    	row.set(0, newID);

		    	// sterge campurile invizibile
		    	row.remove(6);
		    	row.remove(5);

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
		    			dbConnection.deleteEntry(Main.TABLE_SECRETARIES, "id=" + ID);
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
		    	boolean updateImage = false;
		    	String updateQuery;
		    	boolean updateSuccess = basicUpdateListener(selectedRow, row);

		    	// exista field necompletat
		    	if (!updateSuccess)
		    		return;

		    	// verific ca adresa de email este valida
		    	String emailAddr = row.get(5).toString();
		    	if (!Validator.isValidEmailAddress(emailAddr)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid email address!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// asigur unicitatea email-urilor
		    	if (!uniqueEmail(emailAddr, id.getText())) {
		    		JOptionPane.showMessageDialog(null, "Typed email is already associated " +
		    				"with an account. Please try another!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;		    		
		    	}

		    	// verific ca numarul de telefon este valid
		    	String phoneNo = row.get(7).toString();
		    	if (!Validator.isValidPhoneNumber(phoneNo)) {
		    		JOptionPane.showMessageDialog(null, "Not a valid phone number or wrong format!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
		    	}

		    	// adauga path-ul catre imagine (din computer)
		    	if(browseFrame.getFilePath() != null) {
		    		try {
		    			imageURL = Utils.uploadImage(browseFrame.getFilePath());
						row.set(row.size() - 1, imageURL);
						updateImage = true;
					} catch (Exception e1) {
						System.out.println(e1);
					}
		    	}

		    	// updatez in tabelul din gui informatia
		    	tableModel.removeRow(selectedRow);
		    	tableModel.insertRow(selectedRow, row);

		    	// updatez in tabelul server-ului informatia
		    	updateQuery = "UPDATE " + Main.TABLE_SECRETARIES + " SET " +
		    			"first_name='" + row.get(1) + "', last_name='" + row.get(2) +
		    			"', birth_date='" + row.get(3) +"', nationality='" +
		    			row.get(4) + "', email='" + row.get(5) + "', " +
		    			"year='" + row.get(6) + "', phone='" +
		    			row.get(7) + "', address='" + row.get(8) + "'";
		    	if(updateImage)
		    		updateQuery += ", image='" + imageURL + "'";
		    	updateQuery += " WHERE id=" + id.getText();

		    	dbConnection.executeQuery(updateQuery);
		    	
		    	browseFrame.clearFilePath();
		    }
		});
	}
	
	private void annualPlansActionListener() {
		annualPlans.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	clearAction();
		    	ArrayList<String> args = new ArrayList<> ();
		    	args.add(deptID);
		    	args.add(title.getText());
				notifyAllObservers(Main.TABLE_COURSES, args);
		    }
		});
	}

	private void clearAction() {
		clearFields();
    	id.setText(null);
    	icon.setIcon(genericSecretaryIcon);
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
