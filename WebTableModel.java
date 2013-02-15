package assign4;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class WebTableModel extends AbstractTableModel {
  private String[] colNames;	// defines the number of cols
	private ArrayList<Object[]> data;	// one array of size for each row, first element is url string 2nd element is status of fetching
	
	
	public WebTableModel(String[] cols) {
		colNames = cols;
		data = new ArrayList<Object[]>();
	}

	/*
	 Basic getXXX methods required by an class implementing TableModel
	*/
	
	// Returns the name of each col, numbered 0..columns-1
	public String getColumnName(int col) {
		return colNames[col];
	}
	
	// Returns the number of columns
	public int getColumnCount() {
		return(colNames.length);
	}
	
	// Returns the number of rows
	public int getRowCount() {
		return(data.size());
	}
	
	// Returns the data for each cell, identified by its
	// row, col index.
	public Object getValueAt(int row, int col) {
		Object[] rowList = data.get(row);
		Object result = null;
		if (col<rowList.length) {
			result = rowList[col];
		}
		
		return(result);
	}
	
	
	// Changes the value of a cell
	public void setValueAt(Object value, int row, int col) {
		Object[] rowList = data.get(row);
		
		// install the data
		rowList[col] = value;
		
		// notify model listeners of cell change
		fireTableDataChanged();
	}
	
	public void loadUrls(String urlFile) {
		try {
			FileInputStream fstream = new FileInputStream(urlFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				Object[] objArray = new Object[2];
				objArray[0] = strLine;
				data.add(objArray);
			}
			br.close();
			
			// Send notifications that the whole table is now different
			fireTableStructureChanged();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
