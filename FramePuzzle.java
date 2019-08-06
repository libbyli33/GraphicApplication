package a8;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FramePuzzle {
	public static void main (String args[]) throws IOException {
		Picture p = A8Helper.readFromURL("http://www.cs.unc.edu/~kmp/kmp-in-namibia.jpg");
		FramePuzzleWidget frame_puzzle_widget = new FramePuzzleWidget(p);
		
		JFrame main_frame = new JFrame();
		main_frame.setTitle("Assignment 8 Frame Puzzle");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		main_frame.setContentPane(frame_puzzle_widget);
		
		main_frame.pack();
		main_frame.setVisible(true);
	}
}
