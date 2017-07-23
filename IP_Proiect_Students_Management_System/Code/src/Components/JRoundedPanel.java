package Components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class JRoundedPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private int strokeSize = 1;
	private Color shadowColor = Color.BLACK;
	private boolean shadowed = true;
	private boolean highQuality = true;
	private Dimension arcs = new Dimension(30, 30);
	private int shadowGap = 5;
	private int shadowOffset = 4;
	private int shadowAlpha = 150;
	private Color backgroundColor = Color.LIGHT_GRAY;

    public JRoundedPanel() {
        super();
        setOpaque(false);
    }

    @Override
    public void setBackground(Color c) {
        backgroundColor = c;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int shadowGap = this.shadowGap;
        Color shadowColorA = new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), shadowAlpha);
        Graphics2D graphics = (Graphics2D) g;

        if(highQuality)
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(shadowed) {
            graphics.setColor(shadowColorA);
            graphics.fillRoundRect(shadowOffset, shadowOffset, width - strokeSize - shadowOffset,
                    height - strokeSize - shadowOffset, arcs.width, arcs.height);
        }
        else
            shadowGap = 1;

        graphics.setColor(backgroundColor);
        graphics.fillRoundRect(0,  0, width - shadowGap, height - shadowGap, arcs.width, arcs.height);
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.setColor(getForeground());
        graphics.drawRoundRect(0,  0, width - shadowGap, height - shadowGap, arcs.width, arcs.height);
        graphics.setStroke(new BasicStroke());
    }
}
