package assign4;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import javax.swing.*;

@SuppressWarnings("serial")
public class WebFrame extends JFrame {
  private WebTableModel model;
	private JTable table;
	private final int rowCount;
	private JPanel panel, controlPanel;
	private JButton single, concurrent;
	private JTextField textField;
	private JLabel running, completed, elapsed;
	private JProgressBar bar;
	private JButton stop;
	private Semaphore webSemaphore;
	private int maxThreads;
	private Launcher launcher;
	public WebFrame(String urlFile) {
		super("Web Loader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String[] labels = {"URL","Status"};
		model = new WebTableModel(labels);
		model.loadUrls(urlFile);
		table = new JTable(model);
		rowCount = table.getRowCount();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollpane = new JScrollPane(table);
		scrollpane.setPreferredSize(new Dimension(600,300));
		
		panel = new JPanel();
		panel.add(scrollpane);
		add(panel, BorderLayout.NORTH);
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		
		single = new JButton("Single Thread Fetch");
		concurrent = new JButton("Concurrent Thread Fetch");
		textField = new JTextField();
		textField.setMaximumSize(new Dimension(50, 20));
		running = new JLabel("Running:");
		completed = new JLabel("Completed:");
		elapsed = new JLabel("Elapsed");
		bar = new JProgressBar();
		
		stop = new JButton("Stop");
		stop.setEnabled(false);
		single.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launcher = new Launcher(1);
				launcher.start();
			}
		});
		concurrent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textField.getText().length() > 0) {
					maxThreads = Integer.parseInt(textField.getText());
					launcher = new Launcher(maxThreads);
					launcher.start();
				}				
			}
		});
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				launcher.interrupt();
			}
		});
		controlPanel.add(single);
		controlPanel.add(concurrent);
		controlPanel.add(textField);
		controlPanel.add(running);
		controlPanel.add(completed);
		controlPanel.add(elapsed);
		controlPanel.add(bar);
		controlPanel.add(stop);
		
		add(controlPanel, BorderLayout.SOUTH);
		pack();
		setVisible(true);
	}
	// buttons in running state
	public void runningState() {
		stop.setEnabled(true);
		single.setEnabled(false);
		concurrent.setEnabled(false);
	}
	//buttons in not running state
	public void stoppedState() {
		stop.setEnabled(false);
		single.setEnabled(true);
		concurrent.setEnabled(true);
	}
	
	// launches all threads
	public class Launcher extends Thread {
		private int numThreads;
		private LinkedList<WebWorker> workers;
		private int[] values;
		private long startTime, endTime;
		
		public Launcher(int numThreads) {
			this.numThreads = numThreads;
			webSemaphore = new Semaphore(numThreads);
			workers = new LinkedList<WebWorker>();
			values = new int[2];
			
		}
		public void run() {
			runningState();
			try {
				for (int i = 0; i < rowCount; i++) {	
					model.setValueAt(null, i, 1);
				}
				startTime = System.currentTimeMillis();
				for (int i = 0; i < rowCount; i++) {	
					webSemaphore.acquire();
					WebWorker worker = new WebWorker(i, model, webSemaphore, running, completed, values, bar);
					workers.add(worker);
					worker.start();
				}
				webSemaphore.acquire(numThreads);
				webSemaphore.release(numThreads);
			} 
			catch (InterruptedException ignored) {
				// TODO Auto-generated catch block
				for (WebWorker w : workers) {
					w.interrupt();
				}
			}
			
			finally {
				endTime = System.currentTimeMillis();
				elapsed.setText("Elapsed: " + (endTime - startTime) + "ms");
				stoppedState();
			}
		}
	}
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	
			@SuppressWarnings("unused")
			WebFrame frame = new WebFrame(args[0]);
	      } 
		catch (Exception ignored) { }
		
	}
}
