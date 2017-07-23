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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import Components.*;

public class FacultiesPanel extends BasicPanel {
	private JRoundedPanel infoPanel;
	private JScaledTextPane title;
	private JLabel idLabel;
	private JRoundedTextField id;
	private JLabel nameLabel;
	private JRoundedTextField name;
	private JLabel addressLabel;
	private JRoundedTextField address;
	private JLabel phoneLabel;
	private JRoundedTextField phone;
	private JLabel profileLabel;
	private JRoundedTextField profile;
	private JLabel adminLabel;
	private JRoundedTextField adminName;
	private JLabel adminImage;
	private JLabel univLogo;
	private String univID;
	private String imageURL;
	private ImageIcon genericAdminImage;
	private ImageIcon genericFacultyIcon;
	private ImageIcon genericUnivIcon;

	public FacultiesPanel(Window window, String univID, String retrieveQuery) {
		super(window);
		panel.setBackground(Design.mustard);
		panelWidth = 950;
		panelHeight = 720;
		this.univID = univID;

		title = new JScaledTextPane("Serif", 22, Color.BLACK);
		createTable(Main.TABLE_FACULTIES, retrieveQuery);
		
		genericAdminImage = ImageIconLoader.getImageIcon(Design.genericAdmin, 64, true);
		genericFacultyIcon = ImageIconLoader.getImageIcon(Design.genericFaculty, 128, true);
		genericUnivIcon = ImageIconLoader.getImageIcon(Design.genericUniversity, 128, true);
		adminImage = new JLabel();
		univLogo = new JLabel();
		univLogo.setIcon(genericUnivIcon);

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
		logoutActionListener() ;
	}
	
	public void addSorters() {
		table.setIntComparatorAt(0);
	}

	public void hideColumns() {
		table.hideColumn(5);
		table.hideColumn(6);
	}

	public void updateUnivID(String univID) {
		this.univID = univID;
	}

	public void updateTitle(String name) {
		title.setText(name);
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

	public void updateUnivLogo() {
		if (univID == null) {
			univLogo.setIcon(genericUnivIcon);
			return;
		}

		String condition = "id = " + univID;
		Object logoNameObj = dbConnection.getOneValue(Main.TABLE_UNIVERSITIES, "logo", condition);
		
		if (logoNameObj == null) {
			univLogo.setIcon(genericUnivIcon);
			return;
		}

		String logoName = logoNameObj.toString();

		if (logoName.equals("DEFAULT"))
			univLogo.setIcon(genericUnivIcon);
		else {
			String imagePath = Utils.homePath + logoName;

        	// download image
			if (!new File(imagePath).exists()) {
				boolean downloadSuccess = Utils.downloadImage(logoName);
				if (!downloadSuccess) {
					univLogo.setIcon(genericUnivIcon);
					return;
				}
			}

			univLogo.setIcon(ImageIconLoader.getImageIcon(imagePath, 128, false));	
		}
	}

	private String getAdminName(String adminID) {
		String adminName;
		String condition = "id = " + adminID;
		Vector<Vector<Object>> res;
		Vector<String> cols = new Vector<> ();
		cols.add("first_name");
		cols.add("last_name");
		res = dbConnection.selectEntries(cols, Main.TABLE_ADMINS, condition);
		
		if (res.isEmpty())
			return null;

		adminName = res.get(0).get(0) + " " + res.get(0).get(1);
		
		return adminName;
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
		infoPanel.add(profileLabel);
		infoPanel.add(profile, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(5), "wrap");
		infoPanel.add(adminLabel);
		infoPanel.add(adminName, "w 200, wrap");
	}
	
	private void createLabels() {
		idLabel = Design.getCustomJLabel("ID", Design.tahomaBold16);
		nameLabel = Design.getCustomJLabel("Name", Design.tahomaBold16);
		addressLabel = Design.getCustomJLabel("Address", Design.tahomaBold16);
		phoneLabel = Design.getCustomJLabel("Phone number", Design.tahomaBold16);
		profileLabel = Design.getCustomJLabel("Profile", Design.tahomaBold16);
		adminLabel = Design.getCustomJLabel("Admin name", Design.tahomaBold16);
	}
	
	private void createFields() {
		id = new JRoundedTextField(16);
		id.setEditable(false);
		id.setFocusable(false);
		id.setFont(Design.tahomaPlain16);

		name = new JRoundedTextField(16);
		address = new JRoundedTextField(16);
		phone = new JRoundedTextField(16);
		profile = new JRoundedTextField(16);
		name.setFont(Design.tahomaPlain16);
		address.setFont(Design.tahomaPlain16);
		phone.setFont(Design.tahomaPlain16);
		profile.setFont(Design.tahomaPlain16);
		
		adminName = new JRoundedTextField(16);
		adminName.setEditable(false);
		adminName.setFocusable(false);
		adminName.setFont(Design.tahomaPlain16);
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
		panel.add(univLogo, "spanx3, right, wrap");
		panel.add(tableScrollPane, "spanx3, push, grow, wrap");
		panel.add(add, "split4, center");
		panel.add(delete);
		panel.add(update);
		panel.add(clear);
		panel.add(searchField, "split2, pushx, growx");
		panel.add(search, "center");
	}
	
	private void createAssoc() {
		fieldsMap.put(1, name);
		fieldsMap.put(2, address);
		fieldsMap.put(3, phone);
		fieldsMap.put(4, profile);
	}
	
	// dublu click -> trec la Departments Panel
	public void mouseListener() {
		table.addMouseListener(new MouseAdapter() {			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int selectedRow = table.getSelectedRow();
					
					if (selectedRow < 0)
						return;

					selectedRow = table.convertRowIndexToModel(selectedRow);
					String selectedFacultyAdminID;
					selectedFacultyAdminID = tableModel.getValueAt(selectedRow, 5).toString();

					if (!selectedFacultyAdminID.equals(Main.userID))
			    		JOptionPane.showMessageDialog(null, "You are not the admin of "
			    				+ tableModel.getValueAt(selectedRow, 1).toString() + "!",
		    					"Error", JOptionPane.ERROR_MESSAGE);
					else {
						String facultyID = tableModel.getValueAt(selectedRow, 0).toString();
						String facultyName = tableModel.getValueAt(selectedRow, 1).toString();
						clearAction();
						ArrayList<String> args = new ArrayList<> ();
						args.add(facultyID);
						args.add(facultyName);
						notifyAllObservers(Main.TABLE_DEPARTMENTS, args);
					}
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
    			id.setText(tableModel.getValueAt(selectedRow, 0).toString());

    			String selectedFacultyAdminID;
    			selectedFacultyAdminID = tableModel.getValueAt(selectedRow, 5).toString();
    			adminName.setText(getAdminName(selectedFacultyAdminID));

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
		    	row.add(5, univID);
		    	row.add(6, Main.userID);
		    	int newID = dbConnection.insertEntry(Main.TABLE_FACULTIES, row);
		    	
		    	// seteaza in tabela din GUI id-ul intrarii adaugate
		    	row.set(0, newID);
		    	row.remove(5);
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
		    	String selectedFacultyAdminID;
		    	int nrOfDeletedEntries = 0;

	    		if (reallyDelete) {
		    		for (int i = selectedRows.length - 1; i >= 0; i--) {
		    			selectedRow = selectedRows[i];
		    			selectedRow = table.convertRowIndexToModel(selectedRow);
		    			selectedFacultyAdminID = tableModel.getValueAt(selectedRow, 5).toString();

		    			if (!selectedFacultyAdminID.equals(Main.userID))
		    				continue;

		    			ID = tableModel.getValueAt(selectedRow, 0).toString();
		    			tableModel.removeRow(selectedRow);
		    			dbConnection.deleteEntry(Main.TABLE_FACULTIES, "id=" + ID);
		    			nrOfDeletedEntries++;
		    		}
		    		
		    		if (nrOfDeletedEntries == 0)
			    		JOptionPane.showMessageDialog(null, "No entries deleted!",
		    					"Info", JOptionPane.INFORMATION_MESSAGE);
		    		else {
			    		icon.setIcon(genericFacultyIcon);
			    		clearAction();
			    		JOptionPane.showMessageDialog(null, "You have deleted " +
			    				nrOfDeletedEntries + " entries!",
		    					"Success", JOptionPane.INFORMATION_MESSAGE);
		    		}
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
				String selectedFacultyAdminID;
				selectedFacultyAdminID = tableModel.getValueAt(selectedRow, 5).toString();
				
				if (!selectedFacultyAdminID.equals(Main.userID)) {
		    		JOptionPane.showMessageDialog(null, "You are not the admin of "
		    				+ tableModel.getValueAt(selectedRow, 1).toString() + "!",
	    					"Error", JOptionPane.ERROR_MESSAGE);
		    		return;
				}

		    	Vector<Object> row = new Vector<> ();
		    	boolean updateSuccess = basicUpdateListener(selectedRow, row);
		    	String updateQuery;
		    	boolean updateImage = false;

		    	// exista field necompletat
				if(!updateSuccess)
		    		return;
				
		    	// verific ca numarul de telefon este valid
		    	String phoneNo = row.get(3).toString();
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

		    	updateQuery = "UPDATE " + Main.TABLE_FACULTIES + " SET " +
		    			"name='" + row.get(1) + "', address='" + row.get(2) +
		    			"', phone='" + row.get(3) + "', profile='" + row.get(4) + "'";
		    	if (updateImage)
		    		updateQuery += ", logo='" + imageURL + "'";
		    	updateQuery += " WHERE id=" + id.getText();
		    	
		    	// updatez in tabelul server-ului informatia
		    	dbConnection.executeQuery(updateQuery);
		    	
		    	browseFrame.clearFilePath();
		    }
		});
	}

	private void clearAction() {
    	clearFields();
    	id.setText(null);
    	adminName.setText(null);
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
