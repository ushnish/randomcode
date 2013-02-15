package tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class JBrainTetris extends JTetris{
  
	private DefaultBrain myBrain;
	private Brain.Move myMove;
	private Brain.Move worstMove;
	protected Piece newPiece;
	protected JCheckBox brainMode;
	protected JLabel okLabel;
	protected JSlider adversary;
	
	JBrainTetris(int pixels) {
		super(pixels);
		myBrain = new DefaultBrain();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Piece pickNextPiece() {
		int pieceNum;
		double adversaryLevel = (double)adversary.getValue()/(double)100;
		double rand = random.nextDouble();
		
		pieceNum = (int) (pieces.length * rand);
		Piece worstPiece = pieces[pieceNum];
		
		okLabel.setText("OK");
		
		if (adversaryLevel > rand) {
			double worstScore = 0.0;
			for (Piece piece : pieces) {
				worstMove = myBrain.bestMove(board, piece, HEIGHT, worstMove);
				if (worstMove != null && worstScore < worstMove.score) {
					worstScore = worstMove.score;
					worstPiece = worstMove.piece;
				}
			}
			okLabel.setText("*OK*");
		}
		return worstPiece;
	}
	
	@Override
	public void tick(int verb) {
		if(brainMode.isSelected()) {
			board.undo();
			myMove = myBrain.bestMove(board, currentPiece, HEIGHT, myMove);
			if (myMove != null) {
				currentPiece = myMove.piece;
				if (myMove.x < currentX) {
					super.tick(LEFT);
				}
				else if (myMove.x > currentX) {
					super.tick(RIGHT);
				}				
			}
			super.tick(DOWN);
		}		
		else super.tick(verb);
	}
	
	


	/**
	 Sets the enabling of the start/stop buttons
	 based on the gameOn state.
	*/
	private void enableButtons() {
		startButton.setEnabled(!gameOn);
		stopButton.setEnabled(gameOn);
	}
	
	/**
	 Creates the panel of UI controls -- controls wired
	 up to call methods on the JTetris. This code is very repetitive.
	*/
	@Override
	public JComponent createControlPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// COUNT
		countLabel = new JLabel("0");
		panel.add(countLabel);
		
		// SCORE
		scoreLabel = new JLabel("0");
		panel.add(scoreLabel);
		
		// TIME 
		timeLabel = new JLabel(" ");
		panel.add(timeLabel);

		panel.add(Box.createVerticalStrut(12));
		
		// START button
		startButton = new JButton("Start");
		panel.add(startButton);
		startButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		
		// STOP button
		stopButton = new JButton("Stop");
		panel.add(stopButton);
		stopButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopGame();
			}
		});
		
		enableButtons();
		
		JPanel row = new JPanel();
		
		// SPEED slider
		panel.add(Box.createVerticalStrut(12));
		row.add(new JLabel("Speed:"));
		speed = new JSlider(0, 200, 75);	// min, max, current
		speed.setPreferredSize(new Dimension(100, 15));
		
		updateTimer();
		row.add(speed);
		
		panel.add(row);
		speed.addChangeListener( new ChangeListener() {
			// when the slider changes, sync the timer to its value
			public void stateChanged(ChangeEvent e) {
				updateTimer();
			}
		});
		
		testButton = new JCheckBox("Test sequence");
		panel.add(testButton);
		
		panel.add(new JLabel("Brain:"));
		brainMode = new JCheckBox("Brain active");
		panel.add(brainMode);
		
		panel.add(new JLabel("Adversary:"));
		adversary = new JSlider(0, 100, 0);	// min, max, current
		adversary.setPreferredSize(new Dimension(100,15));
		
		okLabel = new JLabel("OK");
		
		panel.add(adversary);
		panel.add(okLabel);				
		return panel;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		JBrainTetris tetris = new JBrainTetris(16);
		JFrame frame = JBrainTetris.createFrame(tetris);
		
		
		frame.setVisible(true);
	}

}
