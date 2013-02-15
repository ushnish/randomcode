package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;


 @SuppressWarnings("serial")
public class SudokuFrame extends JFrame {
  private int numSolutions;
	private Sudoku sudoku;
	private JTextArea textPuzzle, textSolution;
	private boolean always;
	private JButton check;
	private JCheckBox checkBox;
	private JPanel panel;
	private ActionListener checkListener;
	private DocumentListener alwaysListener;
	
	private void solveSudoku() {
		try {
			sudoku = new Sudoku(Sudoku.textToGrid(textPuzzle.getText()));
			numSolutions = sudoku.solve();
			String output = sudoku.getSolutionText() + "\n" + "Solutions: " + numSolutions + "\n" + "Elapsed Time: " + sudoku.getElapsed() +" ms";
			textSolution.setText(output);
		}
		catch(RuntimeException e) {
			textSolution.setText("Parsing Error!");
		}
	}
	
	public SudokuFrame() {
		super("Sudoku Solver");
		JComponent container = (JComponent)this.getContentPane();
		container.setLayout(new BorderLayout(4,4));
		always = true;
		
		textPuzzle = new JTextArea(15,20);
		container.add(textPuzzle, BorderLayout.WEST);
		textPuzzle.setBorder(new TitledBorder("Puzzle"));
		
		textSolution = new JTextArea(15,20);
		container.add(textSolution, BorderLayout.EAST);
		textSolution.setBorder(new TitledBorder("Solution"));
		
		check = new JButton("Check");
		checkListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
					solveSudoku();			
				}
		}; 
		
		check.addActionListener(checkListener);
		
		alwaysListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				if (always) solveSudoku();
			}

			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				if (always) solveSudoku();
			}

			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				if (always) solveSudoku();
			}
		};
		
		textPuzzle.getDocument().addDocumentListener(alwaysListener);
		
		checkBox = new JCheckBox("Auto", true);
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				always = !always;
			}
		});		
		
		panel = new JPanel();
		panel.add(check);
		panel.add(checkBox);
		container.add(panel, BorderLayout.SOUTH);	
		
		// YOUR CODE HERE
		
		// Could do this:
		// setLocationByPlatform(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}	
	
	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
		frame.setVisible(true);
	}
}
