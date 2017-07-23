import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class ImageBrowseFrame {
	private JFileChooser fc;
	private boolean isImage;
	private String filePath;
	private JFrame window;
	private static String imageExtensions[] = {
		".ai", ".bmp", ".gif", ".ico", ".jpg", ".jpeg",
		".png", ".ps", ".psd", ".svg", ".tif", ".tiff"
	};
	
	public ImageBrowseFrame(JFrame window) {
		this.window = window;
		String userDir = System.getProperty("user.home");
		fc = new JFileChooser(userDir + "/Desktop");
	}

	public void browse() {
		if (fc.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fc.getSelectedFile();
			filePath = selectedFile.toPath().toString();
			isImage = fileIsImage(filePath);
		}
	}

	public boolean fileIsImage(String fileName) {   
		int pointPosition = fileName.lastIndexOf('.');
		String extension = fileName.substring(pointPosition);

		for (int i = 0; i < imageExtensions.length; i++)
			if (extension.equals(imageExtensions[i]))
				return true;

		return false;
	}

	public boolean selectedFileIsImage() {
		return isImage;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void clearFilePath() {
		if (filePath != null)
			filePath = null;
	}
}