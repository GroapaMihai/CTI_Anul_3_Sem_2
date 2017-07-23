package Components;

import javax.swing.text.Style;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JScaledTextPane extends JTextPane {
	private static final long serialVersionUID = 1L;
	private StyledDocument doc;
	private Style style;

	public JScaledTextPane(String FontFamily, int fontSize, Color color) {
		super();

		SimpleAttributeSet attribs = new SimpleAttributeSet();  
		StyleConstants.setAlignment(attribs , StyleConstants.ALIGN_CENTER);  
		setParagraphAttributes(attribs, true);
		setFocusable(false);
		setEditable(false);
		setOpaque(false);
		setBackground(new Color(0, 0, 0, 0));

		doc = (StyledDocument) getDocument();
	    style = doc.addStyle("StyleName", null);
	    StyleConstants.setFontSize(style, fontSize);
	    StyleConstants.setFontFamily(style, FontFamily);
	    StyleConstants.setBold(style, true);
	    StyleConstants.setForeground(style, color);
	}
	
	@Override
	public void setText(String text) {
		 try {
			doc.remove(0, doc.getLength());
			doc.insertString(doc.getLength(), text, style);
		} catch (BadLocationException e) {
			System.out.println(e);
		}
	}
}
