package assign4;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;

import javax.swing.*;

public class WebWorker extends Thread {
  private String urlString;
	private InputStream input;
	private StringBuilder contents;
	private WebTableModel model;
	private int index;
	private Semaphore webSemaphore;
	private long startTime, endTime;
	private int[] values;
	private JLabel running, completed;
	private JProgressBar bar;
/*
  This is the core web/download i/o code...*/
	// given the row index of a model, it fetches that url string and updates the status of that row in the model
	public WebWorker(int index, WebTableModel mod, Semaphore semaphore, JLabel running, JLabel completed, int[] values, JProgressBar bar) {
		this.index = index;
		model = mod;
		urlString = (String) model.getValueAt(index, 0);
		input = null;
		contents = null;		
		webSemaphore = semaphore;
		this.running = running;
		this.completed = completed;
		this.values = values;
		this.bar = bar;
	}
	public void run() {
		download();
	}
	public synchronized void setLabels() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				running.setText("Running: " + values[0]);
				completed.setText("Completed: " + values[1]);
				bar.setValue(values[1]*100/(model.getRowCount()));
			}
		});
	}
	public void download() {
		try {
			startTime = System.currentTimeMillis();
			synchronized(values) {
				values[0]++;
			}
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			Thread.sleep(100);
			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);
			
			connection.connect();
			input = connection.getInputStream();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				contents.append(array, 0, len);
				Thread.sleep(100);
			}
			endTime = System.currentTimeMillis();

			int remdr = (int) ( endTime % ( 24L * 60 * 60 * 1000 ) );

			int hours = remdr / ( 60 * 60 * 1000 );

			remdr %= 60 * 60 * 1000;

			int minutes = remdr / ( 60 * 1000 );
			DecimalFormat df = new DecimalFormat("00");
			String min = df.format(minutes);

			remdr %= 60 * 1000;

			int seconds = remdr / 1000;
			String sec = df.format(seconds);
			String out = ((hours + 16) % 24) + ":" + min + ":" + sec + "   " + (endTime - startTime) + "ms   " + contents.toString().length() + "bytes ";
			model.setValueAt(out, index, 1);

			// Successful download if we get here
			
		}
		// Otherwise control jumps to a catch...
		catch(MalformedURLException e) {
			model.setValueAt("URL Error!", index, 1);
		}
		catch(InterruptedException exception) {
			// YOUR CODE HERE
			model.setValueAt("Interrupted!", index, 1);
		}
		catch(IOException e) {
			model.setValueAt("I/O Error!", index, 1);
		}
		// "finally" clause, to close the input stream
		// in any case
		finally {
			webSemaphore.release();
			synchronized(values) {
				values[0]--;
				values[1]++;
			}
			setLabels();
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {}
			}
		}
}
