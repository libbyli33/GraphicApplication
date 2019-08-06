package a8;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.List;
import java.util.ArrayList;

public class ImageAdjusterWidget extends JPanel implements ChangeListener{
	private PictureView picture_view;
	private JPanel picture_panel;
	private JPanel image_adjuster;
	private JSlider blur_slider;
	private JSlider brightness_slider;
	private JSlider saturation_slider;
	private Picture picture;
	
	public ImageAdjusterWidget (Picture picture) {
		this.picture = picture;
		
		setLayout(new BorderLayout());
		picture_view = new PictureView(picture.createObservable());
		
		//create the panel to display picture
		picture_panel = new JPanel();
		picture_panel.add(picture_view);
		add(picture_panel, BorderLayout.CENTER);
		
		//create the panel for three sliders
		image_adjuster =  new JPanel();
		image_adjuster.setLayout(new GridLayout(3,1));
		
		//set up the first slider for blurring function
		JPanel blur_panel = new JPanel();
		blur_panel.setLayout(new BorderLayout());
		
		blur_panel.add(new JLabel("Blur: "), BorderLayout.WEST);
		blur_slider = new JSlider(0, 5, 0);
		blur_slider.setPaintTicks(true);
		blur_slider.setSnapToTicks(true);
		blur_slider.setPaintLabels(true);
		blur_slider.setMajorTickSpacing(1);
		blur_panel.add(blur_slider, BorderLayout.CENTER);
		
		image_adjuster.add(blur_panel);
		
		//set up the second slider for saturation adjustment function
		JPanel saturation_panel = new JPanel();
		saturation_panel.setLayout(new BorderLayout());
		
		saturation_panel.add(new JLabel("Saturation: "), BorderLayout.WEST);
		saturation_slider = new JSlider(-100, 100, 0);
		saturation_slider.setPaintTicks(true);
		saturation_slider.setPaintLabels(true);
		saturation_slider.setMajorTickSpacing(25);
		saturation_panel.add(saturation_slider, BorderLayout.CENTER);
		
		image_adjuster.add(saturation_panel);
		
		//set up the third slider for brightness adjustment function
		JPanel brightness_panel = new JPanel();
		brightness_panel.setLayout(new BorderLayout());
		
		brightness_panel.add(new JLabel("Brightness: "), BorderLayout.WEST);
		brightness_slider = new JSlider(-100, 100, 0);
		brightness_slider.setPaintTicks(true);
		brightness_slider.setPaintLabels(true);
		brightness_slider.setMajorTickSpacing(25);
		brightness_panel.add(brightness_slider, BorderLayout.CENTER);
		
		image_adjuster.add(brightness_panel);
		
		add(image_adjuster, BorderLayout.SOUTH);
		
		blur_slider.addChangeListener(this);
		brightness_slider.addChangeListener(this);
		saturation_slider.addChangeListener(this);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		Picture picture2 = blur(picture, blur_slider.getValue());
		Picture picture3 = brightness(picture2, brightness_slider.getValue());
		Picture picture4 = saturation(picture3, saturation_slider.getValue());
		
		picture_panel.removeAll();
		picture_view = new PictureView(picture4.createObservable());
		picture_panel.add(picture_view);
		add(picture_panel, BorderLayout.CENTER);
		
		this.revalidate();
	}
	
	private Picture blur (Picture picture, int scale) {
		Picture blurred_picture = new PictureImpl(picture.getWidth(), picture.getHeight());
		for (int i = 0; i < picture.getWidth(); i++) {
			for (int j = 0; j < picture.getHeight(); j++) {
				//calculate the average red index of the surrounding pixels
				double red_total = 0.0;
				int red_count = 0;
				for (int m = i-scale; m <= i+scale; m++) {
					for (int n = j-scale; n <= j+scale; n++) {
						try {
							red_total += picture.getPixel(m, n).getRed();
							red_count++;
						} catch (RuntimeException e) {}
					}
				}
				double red_average = red_total / red_count;
				
				//calculate the average green index of the surrounding pixels
				double green_total = 0.0;
				int green_count = 0;
				for (int m = i-scale; m <= i+scale; m++) {
					for (int n = j-scale; n <= j+scale; n++) {
						try {
							green_total += picture.getPixel(m, n).getGreen();
							green_count++;
						} catch (RuntimeException e) {}
					}
				}
				double green_average = green_total / green_count;
				
				//calculate the average blue index of the surrounding pixels
				double blue_total = 0.0;
				int blue_count = 0;
				for (int m = i-scale; m <= i+scale; m++) {
					for (int n = j-scale; n <= j+scale; n++) {
						try {
							blue_total += picture.getPixel(m, n).getBlue();
							blue_count++;
						} catch (RuntimeException e) {}
					}
				}
				double blue_average = blue_total / blue_count;
				
				//change the pixel into the blurred one
				Pixel blurred_pixel = new ColorPixel(red_average, green_average, blue_average);
				blurred_picture.setPixel(i, j, blurred_pixel);
			}
		}
		return blurred_picture;
	}
	
	private Picture brightness (Picture picture, double factor) {
		Picture brightness_changed_picture = new PictureImpl(picture.getWidth(), picture.getHeight());
		for (int i = 0; i < picture.getWidth(); i++) {
			for (int j = 0; j < picture.getHeight(); j++) {
				double red;
				double green;
				double blue;
				Pixel unchanged_pixel = picture.getPixel(i, j);
				
				//calculate the adjusted red green and blue value to darken the pixel
				if (factor < 0) {
					red = ((100+factor) * unchanged_pixel.getRed()) / 100;
					green = ((100+factor) * unchanged_pixel.getGreen()) / 100;
					blue = ((100+factor) * unchanged_pixel.getBlue()) / 100;
				}
				//calculate the adjusted red green and blue value to whiten the pixel
				else {
					red = (factor * 1 + (100-factor) * unchanged_pixel.getRed()) / 100;
					green = (factor * 1 + (100-factor) * unchanged_pixel.getGreen()) / 100;
					blue = (factor * 1 + (100-factor) * unchanged_pixel.getBlue()) / 100;
				}
				
				//change the pixel into the brightness changed one
				Pixel brightness_changed_pixel = new ColorPixel(red, green, blue);
				brightness_changed_picture.setPixel(i, j, brightness_changed_pixel);
			}
		}
		return brightness_changed_picture;
	}
	
	private Picture saturation (Picture picture, double factor) {
		Picture saturation_changed_picture = new PictureImpl(picture.getWidth(), picture.getHeight());
		for (int i = 0; i < picture.getWidth(); i++) {
			for (int j = 0; j < picture.getHeight(); j++) {
				double red;
				double green;
				double blue;
				Pixel unchanged_pixel = picture.getPixel(i, j);
				
				//calculate the adjusted red green and blue value to decrease saturation
				if (factor < 0) {
					red = unchanged_pixel.getRed() * 
						(1.0 + (factor / 100.0) ) - (unchanged_pixel.getIntensity() * factor / 100.0);
					green = unchanged_pixel.getGreen() * 
						(1.0 + (factor / 100.0) ) - (unchanged_pixel.getIntensity() * factor / 100.0);
					blue = unchanged_pixel.getBlue() * 
						(1.0 + (factor / 100.0) ) - (unchanged_pixel.getIntensity() * factor / 100.0);
				}
				
				//calculate the adjusted red green and blue value to increase saturation
				else {
					//calculate the largest value for a
					double largest;
					if (unchanged_pixel.getRed() >= unchanged_pixel.getGreen() &&
							unchanged_pixel.getRed() >= unchanged_pixel.getBlue()) {
						largest = unchanged_pixel.getRed();
					}
					else if (unchanged_pixel.getGreen() >= unchanged_pixel.getBlue()) {
						largest = unchanged_pixel.getGreen();
					}
					else {
						largest = unchanged_pixel.getBlue();
					}
					
					if (largest == 0) {
						red = 0;
						green = 0;
						blue = 0;
					}
					else {
						red = unchanged_pixel.getRed() * 
							((largest + ((1.0 - largest) * (factor / 100.0))) / largest);
						green = unchanged_pixel.getGreen() * 
							((largest + ((1.0 - largest) * (factor / 100.0))) / largest);
						blue = unchanged_pixel.getBlue() * 
							((largest + ((1.0 - largest) * (factor / 100.0))) / largest);
					}
				}
				
				//change the pixel into the saturation changed one
				Pixel saturation_changed_pixel = new ColorPixel(red, green, blue);
				saturation_changed_picture.setPixel(i, j, saturation_changed_pixel);
			}
		}
		return saturation_changed_picture;
	}
}