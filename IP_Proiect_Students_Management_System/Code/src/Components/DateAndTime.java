package Components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

public class DateAndTime {
	private Date date;
	private Timer timer;
	private SimpleDateFormat format;
	private JLabel dateLabel;
	private JLabel timeLabel;
	
	public DateAndTime() {
		createDate();
		createTimer();
		timer.start();
	}
	
	public void createDate() {
		dateLabel = new JLabel();
		date = new Date();
		format = new SimpleDateFormat("EEE, d MMM yyyy");
		dateLabel.setText(format.format(date));
		dateLabel.setFont(Design.tahomaBold14);
	}
	
	public void createTimer() {
		timeLabel = new JLabel();
		timeLabel.setFont(Design.tahomaBold14);
		timer = new Timer(0, new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				date = new Date();
				format = new SimpleDateFormat("hh:mm:ss a");
				timeLabel.setText(format.format(date));
			}
		});
	}
	
	public void stopTimer() {
		timer.stop();
	}

	public JRoundedPanel getDateAndTimePanel(Color background) {
		JRoundedPanel panel = new JRoundedPanel();
		panel.setLayout(new MigLayout("insets 10, align 50% 50%"));
		panel.setBackground(background);
		panel.add(dateLabel, "wrap");
		panel.add(Box.createVerticalStrut(5), "wrap");
		panel.add(timeLabel);
		
		return panel;
	}
	
	public int getMonthNumber(String monthName) {
		switch (monthName) {
			case "Jan" : return 1;
			case "Feb" : return 2;
			case "Mar" : return 3;
			case "Apr" : return 4;
			case "May" : return 5;
			case "Jun" : return 6;
			case "Jul" : return 7;
			case "Aug" : return 8;
			case "Sep" : return 9;
			case "Oct" : return 10;
			case "Nov" : return 11;
			case "Dec" : return 12;
			
			default : return -1;
		}
	}
}
