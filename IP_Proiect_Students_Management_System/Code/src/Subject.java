import java.util.ArrayList;

public interface Subject {
	public void addObserver(Observer o);
	public void notifyAllObservers(String nextPanelName);
	public void notifyAllObservers(String nextPanelName, ArrayList<String> args);
}
