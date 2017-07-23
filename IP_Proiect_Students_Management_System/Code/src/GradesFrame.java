import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Components.Design;
import Components.ImageIconLoader;
import Components.JRoundedPanel;
import net.miginfocom.swing.MigLayout;

public class GradesFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private boolean isActive;
	private JPanel panelCont;
	private JScrollPane scrollPane;
	private String studID;
	private SQL dbConnection;
	private HashMap<String, String> initialGradesMap;
	private HashMap<String, JComboBox<String>> fieldsMap;
	private JButton save;

	public GradesFrame(String title, Vector<Vector<Vector<Object>>> allSemesters,
			String studID, SQL dbConnection) {
		try {
			setIconImage(ImageIO.read(ClassLoader.getSystemResource(Design.semesterPlans)));
		} catch (IOException e) {
			System.out.println(e);
		}

		this.studID = studID;
		this.dbConnection = dbConnection;

		setTitle(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		isActive = true;
		initialGradesMap = new HashMap<> ();
		fieldsMap = new HashMap<> ();

		save = new JButton("Save");
		save.setIcon(ImageIconLoader.getImageIcon(Design.save, 24, true));
		saveActionListener();

		panelCont = new JPanel();
		panelCont.setLayout(new MigLayout("insets 20, align 50% 50%"));
		panelCont.setBackground(Design.mountain);
		createPanelCont(allSemesters);
		
		scrollPane = new JScrollPane(null, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(panelCont);
		add(scrollPane);
		pack();
		setLocationRelativeTo(null);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
	    		isActive = false;
	    		dispose();
    		}
		});
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * Obtine panel-ul mare, cu toate coloanele, reprezentand semestre.
	 * @param allSemesters
	 */
	private void createPanelCont(Vector<Vector<Vector<Object>>> allSemesters) {
		Vector<Vector<Object>> sem;
		
		for (int i = 0; i < allSemesters.size(); i++) {
			sem = allSemesters.get(i);
			if (!sem.isEmpty()) {
				if (i < allSemesters.size() - 1) {
					panelCont.add(getSemesterPanel(i + 1, sem), "top");
					panelCont.add(Box.createHorizontalStrut(25));
				} else
					panelCont.add(getSemesterPanel(i + 1, sem), "top, wrap");
			}
		}
		
		panelCont.add(Box.createVerticalStrut(10), "wrap");
		panelCont.add(save, "center, wrap");
	}
	
	/**
	 * O coloana din Panel reprezentand un semestru
	 * @param semester
	 * @param semPlan
	 * @return
	 */
	private JRoundedPanel getSemesterPanel(int semester, Vector<Vector<Object>> sem) {
		Vector<Object> course;
		JLabel element;
		JComboBox<String> gradeSelector;
		String courseID, gradeValue, condition;
		Object gradeValueObj;
		
		JRoundedPanel semPanel = new JRoundedPanel();
		semPanel.setLayout(new MigLayout("insets 20, align 50% 50%"));
		semPanel.setBackground(Design.scrub);
		
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
			gradeSelector = new JComboBox<> ();
			populateGradeSelector(gradeSelector);
			fieldsMap.put(courseID, gradeSelector);

			/**
			 *  1. daca studentul are nota la disciplina curenta, setez valoarea in selector
			 *  2. salvez nota initiala, astfel incat daca se apasa "Save" sa updatez doar
			 *  notele a caror valoare s-a modificat
			 */
    		condition = "course_id=" + courseID + " AND stud_id=" + studID;
    		gradeValueObj = dbConnection.getOneValue(Main.TABLE_GRADES, "grade_value", condition);
			if (gradeValueObj != null) {
				gradeValue = gradeValueObj.toString();
				gradeSelector.setSelectedItem(gradeValue);
				initialGradesMap.put(courseID, gradeValue);
			} else
				initialGradesMap.put(courseID, null);

			semPanel.add(gradeSelector, "center, wrap");
		}

		return semPanel;
	}
	
	private void populateGradeSelector(JComboBox<String> gradeSelector) {
		gradeSelector.addItem("-");
		for (Integer i = 1; i <= 10; i++)
			gradeSelector.addItem(i.toString());
	}
	
	private void saveActionListener() {
		save.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent ev) {
		    	String courseID, grade, initialGrade, query, condition, gradeID;
		    	Object gradeIDObj;

		    	for (Map.Entry<String, JComboBox<String>> entry : fieldsMap.entrySet()) {
		    		courseID = entry.getKey();
		    		grade = entry.getValue().getSelectedItem().toString();
		    		initialGrade = initialGradesMap.get(courseID);

		    		// nota la disciplina X nu s-a schimbat => nu fac modificari pe server
		    		if (initialGrade != null)
		    			if (grade.equals(initialGrade))
		    				continue;

		    		// nota la disciplina X s-a schimbat => o updatez in map-ul cu note
		    		initialGradesMap.put(courseID, grade);

		    		// verific daca deja exista nota si o updatez
		    		condition = "course_id=" + courseID + " AND stud_id=" + studID;
		    		gradeIDObj = dbConnection.getOneValue(Main.TABLE_GRADES, "id", condition);
		    		
		    		/** 1. nu a fost gasita aceeasi intrare, fac inserare
		    		  * 2. am inregistrat deja o nota pt materia X a studentului Y, fac update
		    		  */
		    		if (gradeIDObj == null) {
		    			query = "INSERT INTO " + Main.TABLE_GRADES + " VALUES (null, ";
			    		if (grade.equals("-"))
			    			query += "DEFAULT, ";
			    		else
			    			query += grade + ", ";
			    		query += courseID + ", " + studID + ")";
		    		} else {
		    			gradeID = gradeIDObj.toString();
		    			query = " UPDATE " + Main.TABLE_GRADES + " SET grade_value=";
			    		if (grade.equals("-"))
			    			query += "DEFAULT";
			    		else
			    			query += grade;
			    		query += " WHERE id=" + gradeID;
		    		}
		    		
		    		dbConnection.executeQuery(query);
		    	}
		    }
		});
	}
}
