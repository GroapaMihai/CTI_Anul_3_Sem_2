import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import Components.Design;
import Components.JRoundedPanel;
import net.miginfocom.swing.MigLayout;

public class SemesterPlansFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private boolean isActive;
	private JPanel panelCont;
	private JScrollPane scrollPane;

	public SemesterPlansFrame(String title, Vector<Vector<Vector<Object>>> allSemesters) {
		try {
			setIconImage(ImageIO.read(ClassLoader.getSystemResource(Design.semesterPlans)));
		} catch (IOException e) {
			System.out.println(e);
		}

		setTitle(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
		isActive = true;

		panelCont = new JPanel();
		panelCont.setLayout(new MigLayout("insets 20, align 50% 50%"));
		panelCont.setBackground(Design.mustard);
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
				panelCont.add(getSemesterPanel(i + 1, sem), "top");
				if (i < allSemesters.size() - 1)
					panelCont.add(Box.createHorizontalStrut(25));
			}
		}
	}
	
	/**
	 * O coloana din Panel reprezentand un semestru
	 * @param semester
	 * @param semPlan
	 * @return
	 */
	private JRoundedPanel getSemesterPanel(int semester, Vector<Vector<Object>> sem) {
		Vector<Object> discipline;
		JLabel element;
		
		JRoundedPanel semPanel = new JRoundedPanel();
		semPanel.setLayout(new MigLayout("insets 20, align 50% 50%"));
		semPanel.setBackground(Design.papaya);
		
		element = new JLabel("Semester " + semester);
		element.setFont(Design.serifBold26);
		semPanel.add(element, "spanx9, center, wrap");
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
		semPanel.add(element, "center, wrap");

		for (int i = 0; i < sem.size(); i++) {
			discipline = sem.get(i);
			for (int j = 0; j < discipline.size() - 1; j++) {
				element = new JLabel(discipline.get(j).toString());
				element.setFont(Design.tahomaPlain16);
				semPanel.add(element, "center");
				semPanel.add(Box.createHorizontalStrut(10));
			}
			
			element = new JLabel(discipline.get(discipline.size() - 1).toString());
			element.setFont(Design.tahomaPlain16);
			semPanel.add(element, "center, wrap");
		}

		return semPanel;
	}
}
