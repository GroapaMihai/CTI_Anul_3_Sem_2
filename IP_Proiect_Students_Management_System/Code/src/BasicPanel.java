import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePickerImpl;

import Components.*;

import net.miginfocom.swing.MigLayout;

public class BasicPanel implements Subject {
	private ArrayList<Observer> observers;
	protected JPanel panel;
	protected DefaultTableModel tableModel;
	protected CustomJTable table;
	protected JScrollPane tableScrollPane;
	protected ImageBrowseFrame browseFrame;
	protected DateAndTime dateAndTime;
	protected JButton logout;
	protected JLabel icon;
	protected JButton browse;
	protected JButton add;
	protected JButton delete;
	protected JButton update;
	protected JButton clear;
	protected JButton search;
	protected JRoundedTextField searchField;
	protected HashMap<Integer, Component> fieldsMap;
	protected int panelWidth;
	protected int panelHeight;
	protected SQL dbConnection;
	protected JLabel userNameLabel;
	protected JLabel accountTypeLabel;
	protected JRoundedPanel accountInfo;

	public BasicPanel(Window window) {
		panel = new JPanel();
		panel.setLayout(new MigLayout("insets 20, align 50% 50%"));
		observers = new ArrayList<> ();
		addObserver(window);
		fieldsMap = new HashMap<> ();
		tableScrollPane = new JScrollPane(null, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		browseFrame = new ImageBrowseFrame(window);

		try {
			dbConnection = new SQL(window);
			dbConnection.createConnection();
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (SQLException e) {
			System.out.println(e);
		}

		createButtons();
		dateAndTime = new DateAndTime();
		icon = new JLabel();
	}

	public JPanel getPanel() {
		return panel;
	}

	public Dimension getDimension() {
		return new Dimension(panelWidth, panelHeight);
	}
	
	public void updateUserName() {
		userNameLabel.setText("Logged in as : " + Main.userName);
	}

	public void updateAccountType() {
		accountTypeLabel.setText("Account type : " + Main.getAccountTypeName());
	}

	public void createTable(String tableName, String querry) {
		table = dbConnection.retrieveTable(tableName, querry);
		tableModel = (DefaultTableModel) table.getModel();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setDefaultEditor(Object.class, null);
		table.getTableHeader().setReorderingAllowed(false);
		table.setFillsViewportHeight(true);
		tableScrollPane.setViewportView(table);
	}

	protected void createButtons() {
		logout = new JButton("Log Out");
		logout.setIcon(ImageIconLoader.getImageIcon(Design.logout, 24, true));

		browse = new JButton("Browse");
		browse.setIcon(ImageIconLoader.getImageIcon(Design.browse, 24, true));
		browseActionListener();

		add = new JButton("Add");
		add.setIcon(ImageIconLoader.getImageIcon(Design.add, 24, true));

		delete = new JButton("Delete");
		delete.setIcon(ImageIconLoader.getImageIcon(Design.delete, 24, true));

		update = new JButton("Update");
		update.setIcon(ImageIconLoader.getImageIcon(Design.update, 24, true));

		clear = new JButton("Clear");
		clear.setIcon(ImageIconLoader.getImageIcon(Design.reset, 24, true));

		searchField = new JRoundedTextField(16);
		searchField.setFont(Design.tahomaPlain16);
		searchDocumentListener();

		search = new JButton("Search");
		search.setIcon(ImageIconLoader.getImageIcon(Design.search, 24, true));
		searchActionListener();
	}

	protected void createAccountInfoBox(Color background) {
		userNameLabel = Design.getCustomJLabel("", Design.tahomaBold14);
		accountTypeLabel = Design.getCustomJLabel("", Design.tahomaBold14);
		accountInfo = new JRoundedPanel();
		accountInfo.setLayout(new MigLayout("insets 10, align 50% 50%"));
		accountInfo.setBackground(background);
		accountInfo.add(userNameLabel, "wrap");
		accountInfo.add(Box.createVerticalStrut(5), "wrap");
		accountInfo.add(accountTypeLabel);
	}

	// ia text din tabel si il adauga in campurile albe
	protected void setComponentText(Component field, String text) {
		if (field instanceof JRoundedTextField) {
			((JRoundedTextField) field).setText(text);
			((JRoundedTextField) field).setCaretPosition(0);
		} else if (field instanceof JNumberTextField) {
			((JNumberTextField) field).setText(text);
			((JNumberTextField) field).setCaretPosition(0);
		} else if (field instanceof JDatePickerImpl) {
			String[] tokens = text.split("-");
			int year = Integer.parseInt(tokens[0]);
			int month = Integer.parseInt(tokens[1]) - 1;
			int day = Integer.parseInt(tokens[2]);
			((JDatePickerImpl) field).getModel().setDate(year, month, day);
		} else if (field instanceof JComboBox)
			((JComboBox<?>) field).setSelectedItem(text);
	}

	// extrage text dintr-o componenta de completat
	protected String getComponentText(Component field) {
		if (field instanceof JRoundedTextField)
			return ((JRoundedTextField) field).getText();
		else if (field instanceof JNumberTextField)
			return ((JNumberTextField) field).getText();
		else if (field instanceof JDatePickerImpl) {
			String date = (((JDatePickerImpl) field).getJFormattedTextField().getText());
		    String[] tokens = date.split("-");
		    int month = dateAndTime.getMonthNumber(tokens[1]);

		    date = tokens[2] + "-";
		    if(month < 10)
		    	date += "0";
		    date += dateAndTime.getMonthNumber(tokens[1]);
		    date += "-" + tokens[0];

		    return date;
		} else if (field instanceof JComboBox) {
			String option = ((JComboBox<?>) field).getSelectedItem().toString();
			if (option.equals("None"))
				return "";
			return option;
		} else
			System.out.println(field.getClass());

		return "";
	}

	// goleste campurile de completat
	protected void clearFields() {
		Component field;
		searchField.setText(null);

    	for(int i = 0; i < table.getColumnCount(); i++) {
    		field = fieldsMap.get(i);

    		if (field instanceof JRoundedTextField)
    			setComponentText(field, null);
    		else if (field instanceof JNumberTextField)
    			setComponentText(field, null);
    		else if (field instanceof JComboBox<?>) {
    			if (((JComboBox<?>) field).getItemCount() > 0)
    				((JComboBox<?>) field).setSelectedIndex(0);
    		} else if (field instanceof JDatePickerImpl)
    			((JDatePickerImpl) field).getModel().setDate(1980, 0, 1);
    	}
	}

	public boolean uniqueEmail(String email, String currentUserID) {
		String condition = "email='" + email + "'";
		Object idObj;

		idObj = dbConnection.getOneValue(Main.TABLE_SERVER_ADMINS, "id", condition);
		if (idObj != null) {
			if (currentUserID != null)
				return idObj.toString().equals(currentUserID);
			else
				return false;
		}

		idObj = dbConnection.getOneValue(Main.TABLE_ADMINS, "id", condition);
		if (idObj != null) {
			if (currentUserID != null)
				return idObj.toString().equals(currentUserID);
			else
				return false;
		}

		idObj = dbConnection.getOneValue(Main.TABLE_SECRETARIES, "id", condition);
		if (idObj != null) {
			if (currentUserID != null)
				return idObj.toString().equals(currentUserID);
			else
				return false;
		}

		idObj = dbConnection.getOneValue(Main.TABLE_STUDENTS, "id", condition);
		if (idObj != null) {
			if (currentUserID != null)
				return idObj.toString().equals(currentUserID);
			else
				return false;
		}

		return true;
	}

	protected void basicSelectionListener() {
		Component field;
    	int selectedRow = table.getSelectedRow();

    	if(selectedRow < 0)
    		return;

    	selectedRow = table.convertRowIndexToModel(selectedRow);
    	for (int i = 0; i < table.getColumnCount(); i++) {
    		field = fieldsMap.get(i);

    		if (field == null)
    			continue;

    		setComponentText(field, tableModel.getValueAt(selectedRow, i).toString());
    		browseFrame.clearFilePath();
    	}
	}

	/* 
	 * Extrage text din field-uri si obtine un rand de tabela
	 * pe care il stocheaza in parametrul "row". Intoarce false
	 * in cazul in care unul sau mai multe field-uri sunt goale.
	 */
	protected boolean basicAddListener(Vector<Object> row) {
    	Component field;
    	String text;

    	for (int i = 0; i < table.getColumnCount(); i++) {
    		field = fieldsMap.get(i);

    		// exista coloane care nu au field asociat
    		if(field == null)
    			continue;

    		text = getComponentText(field);

    		// field necompletat
    		if (Validator.isEmptyString(text)) {
    			JOptionPane.showMessageDialog(null, "One or more fields are empty!",
    					"Error", JOptionPane.ERROR_MESSAGE);
    			return false;
    		}

    		row.add(text);
    	}

    	return true;
	}

	/*
	 * Metoda primeste indecsii randurilor ce ar trebui sterse.
	 * Daca nu sunt selectate randuri sau utilizatorul se
	 * razgandeste, metoda intoarce valoarea False.
	 * Daca se confirma optiunea de stergere, intoarce True.
	 */
	protected boolean basicDeleteListener(int[] selectedRows) {
		if(selectedRows.length == 0) {
    		JOptionPane.showMessageDialog(null, "No rows selected!",
					"Error", JOptionPane.ERROR_MESSAGE);
    		return false;
		}

		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog
				(null, "Are you sure you want to delete selected entries?", "Warning", dialogButton);

		if (dialogResult == JOptionPane.YES_OPTION) {
			browseFrame.clearFilePath();
			return true;
		}

		return false;
	}

	/*
	 * Extrage din campuri textul si il adauga in row.
	 * Row va fi folosit ulterior pentru actualizare rand
	 * in tabela locala si pe server. Daca vreun field este
	 * necompletat introarce False, altfel True.
	 */
	protected boolean basicUpdateListener(int selectedRow, Vector<Object> row) {
    	Component field;
    	String text;

    	for(int i = 0; i < table.getColumnCount(); i++) {
    		field = fieldsMap.get(i);

    		// exista coloane care nu au field asociat
    		if(field == null) {
    			row.add(tableModel.getValueAt(selectedRow, i));
    			continue;
    		}

    		text = getComponentText(field);

    		// field necompletat
    		if(Validator.isEmptyString(text)) {
    			JOptionPane.showMessageDialog(null, "One or more fields are empty!",
    					"Error", JOptionPane.ERROR_MESSAGE);
    			return false;
    		}

    		row.add(text);
    	}	

    	return true;
	}

	private void browseActionListener() {		
		browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						browseFrame.browse();
						if(browseFrame.selectedFileIsImage())
							icon.setIcon(ImageIconLoader.getImageIcon(browseFrame.getFilePath(), 128, false));
						else
							browseFrame.clearFilePath();
					}
				});
			}
		});
	}

	private void searchDocumentListener() {
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				browseFrame.clearFilePath();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				browseFrame.clearFilePath();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if (searchField.getText().isEmpty())
					table.filter("");
				browseFrame.clearFilePath();
			}
		});
	}

	private void searchActionListener() {
		search.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	table.filter(searchField.getText());
		    	browseFrame.clearFilePath();
		    }
		});
	}

	public Vector<Object> getRowAt(int rowIndex, int nrOfColumns) {
		Vector<Object> row = new Vector<> ();

		for(int i = 0; i < nrOfColumns; i++)
			row.add(tableModel.getValueAt(rowIndex, i));

		return row;
	}

	public void addObserver(Observer o) {
		observers.add(o);	
	}

	public void notifyAllObservers(String nextPanelName) {
		for(Observer o : observers)
			o.update(nextPanelName);
	}

	public void notifyAllObservers(String nextPanelName, ArrayList<String> args) {
		for(Observer o : observers)
			o.update(nextPanelName, args);
	}
}