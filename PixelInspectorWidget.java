package a8;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PixelInspectorWidget extends JPanel implements MouseListener{
	private PictureView picture_view;
	private JLabel x;
	private JLabel y;
	private JLabel red;
	private JLabel green;
	private JLabel blue;
	private JLabel brightness;
	
	public PixelInspectorWidget (Picture picture) {
		setLayout(new BorderLayout());
		
		picture_view = new PictureView(picture.createObservable());
		picture_view.addMouseListener(this);
		add(picture_view, BorderLayout.CENTER);
		
		//create the panel to put pixel information
		JPanel pixel_inspector = new JPanel();
		pixel_inspector.setLayout(new GridLayout(0,1));
		
		//create five labels to contain the five pieces of information
		x = new JLabel();
		y = new JLabel();
		red = new JLabel();
		green = new JLabel();
		blue = new JLabel();
		brightness = new JLabel();
		
		pixel_inspector.add(x);
		pixel_inspector.add(y);
		pixel_inspector.add(red);
		pixel_inspector.add(green);
		pixel_inspector.add(blue);
		pixel_inspector.add(brightness);
		
		add(pixel_inspector, BorderLayout.WEST);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("You clicked on the frame at: " + e.getX() + "," + e.getY());
		
		//put the five pieces of information into those five labels
		x.setText("X: " + e.getX());		
		y.setText("Y: " + e.getY());
		red.setText("Red: " + String.format("%.2f",
					picture_view.getPicture().getPixel(e.getX(), e.getY()).getRed()));
		green.setText("Green: " + String.format("%.2f", 
					picture_view.getPicture().getPixel(e.getX(), e.getY()).getGreen()));
		blue.setText("Blue: " + String.format("%.2f", 
					picture_view.getPicture().getPixel(e.getX(), e.getY()).getBlue()));
		brightness.setText("Brightness: " + String.format("%.2f", 
					picture_view.getPicture().getPixel(e.getX(), e.getY()).getIntensity()));
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

}

