package Components;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageIconLoader {

	public static ImageIcon getImageIcon(String imagePath, int width,
			int height, boolean fromClassPath) {
		Image image = null;

		try {
			if (fromClassPath)
				image = ImageIO.read(ClassLoader.getSystemResource(imagePath));
			else
				image = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
		    System.out.println(e);
		}

		return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	public static ImageIcon getImageIcon(String imagePath, int size, boolean fromClassPath) {		
		return getImageIcon(imagePath, size, size, fromClassPath);
	}
}
