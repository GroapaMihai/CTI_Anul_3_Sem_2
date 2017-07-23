package Components;

import javax.swing.JTextArea;

public class JScaledTextArea extends JTextArea {
	private static final long serialVersionUID = 1L;

	public JScaledTextArea(String text, int rows, int columns) {
        super(rows, columns);
        setText(text);
        setBackground(null);
        setEditable(false);
        setBorder(null);
        setLineWrap(true);
        setWrapStyleWord(true);
        setFocusable(false);
    }
}