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

public class UnivPanel extends BasicPanel {
	private JRoundedPanel infoPanel;
	private JLabel idLabel;
	private JRoundedTextField id;
	private JLabel nameLabel;
	private JRoundedTextField name;
	private JLabel cityLabel;
	private JRoundedTextField city;
	private JLabel countryLabel;
	private JRoundedTextField country;
	private JLabel addressLabel;
	private JRoundedTextField address;
	private JLabel phoneLabel;
	private JRoundedTextField phone;
	private String imageURL;
	private ImageIcon genericUnivIcon;
	
	public UnivPanel(Window window) {
		super(window);
		panel.setBackground(Design.tangerine);
		panelWidth = 950;
		panelHeight = 685;

		createTable(Main.TABLE_UNIVERSITIES, Main.TABLE_UNIVERSITIES_QUERY);
		table.setIntComparatorAt(0);
		table.hideColumn(6);

		genericUnivIcon = ImageIconLoader.getImageIcon(Design.genericUniversity, 128, true);

		createAccountInfoBox(Design.teal);
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

	private void createInfoPanel() {
		infoPanel = new JRoundedPanel();
		infoPanel.setLayout(new MigLayout("insets 10, align 50% 50%"));
		infoPanel.setBackground(Design.teal);
		createLabels();
		createFields();
		
		icon.setIcon(genericUnivIcon);
		
		infoPanel.add(icon);
		infoPanel.add(browse, "bottom, wrap");
		infoPanel.add(Box.createVerticalStrut(20), "wrap");
		infoPanel.add(idLabel);
		infoPanel.add(id, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(10), "wrap");
		infoPanel.add(nameLabel);
		infoPanel.add(name, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(10), "wrap");
		infoPanel.add(cityLabel);
		infoPanel.add(city, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(10), "wrap");
		infoPanel.add(countryLabel);
		infoPanel.add(country, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(10), "wrap");
		infoPanel.add(addressLabel);
		infoPanel.add(address, "w 200, wrap");
		infoPanel.add(Box.createVerticalStrut(10), "wrap");
		infoPanel.add(phoneLabel);
		infoPanel.add(phone, "w 200, wrap");
	}
	
	private void createLabels() {
		idLabel = Design.getCustomJLabel("ID", Design.tahomaBold16);
		nameLabel = Design.getCustomJLabel("Name", Design.tahomaBold16);
		cityLabel = Design.getCustomJLabel("City", Design.tahomaBold16);
		countryLabel = Design.getCustomJLabel("Country", Design.tahomaBold16);
		addressLabel = Design.getCustomJLabel("Address", Design.tahomaBold16);
		phoneLabel = Design.getCustomJLabel("Phone number", Design.tahomaBold16);
	}
	
	private void createFields() {
		id = new JRoundedTextField(16);
		id.setEditable(false);
		id.setFocusable(false);
		
		name = new JRoundedTextField(16);
		city = new JRoundedTextField(16);
		country = new JRoundedTextField(16);
		address = new JRoundedTextField(16);
		phone = new JRoundedTextField(16);
		id.setFont(Design.tahomaPlain16);
		name.setFont(Design.tahomaPlain16);
		city.setFont(Design.tahomaPlain16);
		country.setFont(Design.tahomaPlain16);
		address.setFont(Design.tahomaPlain16);
		phone.setFont(Design.tahomaPlain16);
	}
	
	public void addElements() {
		panel.add(dateAndTime.getDateAndTimePanel(Design.teal));
		panel.add(Box.createHorizontalStrut(25));
		panel.add(accountInfo, "right");
		panel.add(Box.createHorizontalStrut(25));
		panel.add(logout, "wrap");
		panel.add(Box.createVerticalGlue(), "wrap");
		panel.add(infoPanel);
		panel.add(tableScrollPane, "skip, span3, push, grow, wrap");
		panel.add(add, "split4, center");
		panel.add(delete);
		panel.add(update);
		panel.add(clear);
		panel.add(searchField, "skip, split2, pushx, growx");
		panel.add(search, "center");
	}
	
	private void createAssoc() {
		fieldsMap.put(1, name);
		fieldsMap.put(2, city);
		fieldsMap.put(3, country);
		fieldsMap.put(4, address);
		fieldsMap.put(5, phone);
	}
	
	// dublu click -> trec la Admins Panel
	private void mouseListener() {
		table.addMouseListener(new MouseAdapter() {			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int selectedRow = table.getSelectedRow();

					if (selectedRow < 0)
						return;

					selectedRow = table.convertRowIndexToModel(selectedRow);
					String ID = tableModel.getValueAt(selectedRow, 0).toString();
					String name = tableModel.getValueAt(selectedRow, 1).toString();
					clearAction();
					ArrayList<String> args = new ArrayList<> ();
					args.add(ID);
					args.add(name);
					notifyAllObservers(Main.TABLE_ADMINS, args);
				}
			}
		});
	}
	
	private void selectionListener() {
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    @Override
		    public void valueChanged(ListSelectionEvent event) {
	    		basicSelectionListener();
	    		int selectedRow = table.getSelectedRow();
	    		
	    		if(selectedRow > -1) {
	    			selectedRow = table.convertRowIndexToModel(selectedRow);
	    			id.setText(tableModel.getValueAt(selectedRow, 0).toString());

	    			Object imgObj = tableModel.getValueAt(selectedRow, 6);
	    			if(imgObj == null || imgObj.toString().equals("DEFAULT")) {
	    				icon.setIcon(genericUnivIcon);
	    				return;
	    			}

	    			imageURL = imgObj.toString();
    				String localPath = Utils.homePath + imageURL;

    				// download image
    				if (!new File(localPath).exists()) {
    					boolean downloadSuccess = Utils.downloadImage(imageURL);
    					if(!downloadSuccess) {
    						icon.setIcon(genericUnivIcon);
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
		    	
		    	// verific ca numarul de telefon este valid
		    	String phoneNo = row.get(4).toString();
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
		    	int newID = dbConnection.insertEntry(Main.TABLE_UNIVERSITIES, row);
		    	
		    	// seteaza in tabela din GUI id-ul intrarii adaugate
		    	row.set(0, newID);
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
		    			dbConnection.deleteEntry(Main.TABLE_UNIVERSITIES, "id=" + ID);
		    			nrOfDeletedEntries++;
		    		}
		    		
		    		icon.setIcon(genericUnivIcon);
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
				if(!updateSuccess)
		    		return;

		    	// verific ca numarul de telefon este valid
		    	String phoneNo = row.get(5).toString();
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
					} catch (Exception e1) {
						System.out.println(e1);
					}
		    	}

		    	// updatez in tabelul din gui informatia
		    	tableModel.removeRow(selectedRow);
		    	tableModel.insertRow(selectedRow, row);

		    	// updatez in tabelul server-ului informatia
		    	dbConnection.updateEntry(Main.TABLE_UNIVERSITIES, row, "id=" + id.getText());
		    	
		    	browseFrame.clearFilePath();
		    }
		});
	}

	private void clearAction() {
    	clearFields();
    	id.setText(null);
    	icon.setIcon(genericUnivIcon);
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
