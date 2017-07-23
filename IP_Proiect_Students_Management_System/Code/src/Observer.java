import java.util.ArrayList;

public interface Observer {
	public void update(String nextPanelName);
	public void update(String nextPanelName, ArrayList<String> args);
}
