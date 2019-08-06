package a8;

import java.util.List;
import java.util.ArrayList;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class FramePuzzleWidget extends JPanel implements MouseListener, KeyListener{
	private Picture picture;
	private PictureView[][] puzzles;
	private Picture blank_pic;
	private PictureView blank_picture_view;
	private JPanel picture_panel;
	int blank_x_index = 4;
	int blank_y_index = 4;
	
	public FramePuzzleWidget (Picture p) {
		this.picture = p;
		puzzles = new PictureView[5][5];
		int x_index;
		int y_index;
		int width = picture.getWidth() / 5;
		int height = picture.getHeight() / 5;
		
		setLayout(new BorderLayout());
		
		//put 25 individual pictures into a list in row-major order
		List<Picture> pictures = new ArrayList<Picture>();
		for (y_index = 0; y_index < picture.getHeight(); y_index += height) {
			for (x_index = 0; x_index < picture.getWidth(); x_index += width) {
				int x_index_leftover = picture.getWidth() - x_index - width;
				int y_index_leftover = picture.getHeight() - y_index - height;
				
				Picture pic;
				if (x_index_leftover > 0 && x_index_leftover < width &&
					y_index_leftover > 0 && y_index_leftover < height) {
					pic = new SubPictureImpl
							(picture, x_index, y_index, width+x_index_leftover, height+y_index_leftover);
				}
				else {
					pic = new SubPictureImpl(picture, x_index, y_index, width, height);
				}
				pictures.add(pic);
			}
		}
		
		
		//put 25 individual PictureView object into a 2D array
		int idx = 0;
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++) {
				puzzles[i][j] = new PictureView(pictures.get(idx).createObservable());
				idx++;
			}
		}
		
		//set the last picture into the blank one
		blank_pic = new PictureImpl(width+picture.getWidth()%5, height+picture.getHeight()%5);
		for (int i = 0; i < width+picture.getWidth()%5; i++) {
			for(int j = 0; j < height+picture.getHeight()%5; j++) {
				blank_pic.setPixel(i,j,new GrayPixel(1.0));
			}
		}
		
		blank_picture_view = new PictureView(blank_pic.createObservable());
		puzzles[4][4] = blank_picture_view;
		
		//put 25 individual PictureView objects into a GridLayout
		picture_panel = new JPanel();
		picture_panel.setLayout(new GridLayout(5,5));
		
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++) {
				picture_panel.add(puzzles[i][j]);
				puzzles[i][j].addMouseListener(this);
				puzzles[i][j].setFocusable(false);
			}
		}
		
		add(picture_panel, BorderLayout.CENTER);
		
		addKeyListener(this);
		setFocusable(true);
		
		picture_panel.setFocusable(false);
		requestFocus();
	}
	
	@Override
	public void mouseClicked (MouseEvent e) {
		//find out where the clicked PictureView is
		PictureView picture_view_clicked = (PictureView) e.getSource();
		int clicked_x_index = 0;
		int clicked_y_index = 0;
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++) {
				if (puzzles[i][j] == picture_view_clicked) {
					clicked_x_index = i;
					clicked_y_index = j;
				}
			}
		}
		
		//first deal with the situation when click on the column with blank PictureView
		if (clicked_x_index == blank_x_index) {
			if(clicked_y_index < blank_y_index) {
				for (int y_index = blank_y_index; y_index > clicked_y_index; y_index--) {
					puzzles[clicked_x_index][y_index].setPicture(puzzles[clicked_x_index][y_index-1].getPicture());
				}
				puzzles[clicked_x_index][clicked_y_index].setPicture(blank_pic.createObservable());
				blank_y_index = clicked_y_index;
			}
			else if (clicked_y_index > blank_y_index) {
				for (int y_index = blank_y_index; y_index < clicked_y_index; y_index++) {
					puzzles[clicked_x_index][y_index].setPicture(puzzles[clicked_x_index][y_index+1].getPicture());
				}
				puzzles[clicked_x_index][clicked_y_index].setPicture(blank_pic.createObservable());
				blank_y_index = clicked_y_index;
			}
		}
		//then deal with the situation when click the row with blank PictureView
		else if (clicked_y_index == blank_y_index) {
			if (clicked_x_index < blank_x_index) {
				for (int x_index = blank_x_index; x_index > clicked_x_index; x_index--) {
					puzzles[x_index][clicked_y_index].setPicture(puzzles[x_index-1][clicked_y_index].getPicture());
				}
				puzzles[clicked_x_index][clicked_y_index].setPicture(blank_pic.createObservable());
				blank_x_index = clicked_x_index;
			}
			else if (clicked_x_index > blank_x_index) {
				for (int x_index = blank_x_index; x_index < clicked_x_index; x_index++) {
					puzzles[x_index][clicked_y_index].setPicture(puzzles[x_index+1][clicked_y_index].getPicture());
				}
				puzzles[clicked_x_index][clicked_y_index].setPicture(blank_pic.createObservable());
				blank_x_index = clicked_x_index;
			}
		}
	}
	
	@Override
	public void keyPressed (KeyEvent e) {
		//swap the blank PictureView with its adjacent one
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (blank_y_index != 0) {
				puzzles[blank_x_index][blank_y_index].setPicture(puzzles[blank_x_index][blank_y_index-1].getPicture());
				puzzles[blank_x_index][blank_y_index-1].setPicture(blank_pic.createObservable());
				blank_y_index--;
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (blank_y_index != 4) {
				puzzles[blank_x_index][blank_y_index].setPicture(puzzles[blank_x_index][blank_y_index+1].getPicture());
				puzzles[blank_x_index][blank_y_index+1].setPicture(blank_pic.createObservable());
				blank_y_index++;
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (blank_x_index != 0) {
				puzzles[blank_x_index][blank_y_index].setPicture(puzzles[blank_x_index-1][blank_y_index].getPicture());
				puzzles[blank_x_index-1][blank_y_index].setPicture(blank_pic.createObservable());
				blank_x_index--;
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (blank_x_index != 4) {
				puzzles[blank_x_index][blank_y_index].setPicture(puzzles[blank_x_index+1][blank_y_index].getPicture());
				puzzles[blank_x_index+1][blank_y_index].setPicture(blank_pic.createObservable());
				blank_x_index++;
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		//TODO Auto-generated method stub
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

}
