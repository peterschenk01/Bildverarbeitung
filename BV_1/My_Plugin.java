import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.io.*;

public class My_Plugin implements PlugIn {

    public void run(String arg) {
        
		//Load Image
		ImagePlus image = new ImagePlus("C:/Users/pschenk/Downloads/test.jpg");
		image.show();
		//Duplicate Image
		ImagePlus image2 = image.duplicate();
		image2.show();
		//Get ImageProcessors
		ImageProcessor ip = image.getProcessor();
		ImageProcessor ip2 = image2.getProcessor();
		
		//Grayscale Image
		ImageProcessor ip_gray = new ByteProcessor(ip.getWidth(), ip.getHeight());
		int[] rgb_pixels = (int[]) ip.getPixels();
		byte[] gray_pixels = (byte[]) ip_gray.getPixels();
		
		for (int i=0; i<rgb_pixels.length; i++) {
			int r = (rgb_pixels[i] & 0xff0000) >> 16;
			int g = (rgb_pixels[i] & 0x00ff00) >> 8;
			int b = (rgb_pixels[i] & 0x0000ff);
			gray_pixels[i] = (byte) (0.299*r + 0.587*g + 0.114*b + 0.5);
		}
		
		ImagePlus grayImage = new ImagePlus("Gray Image", ip_gray);
		grayImage.show();
		
		//Grayscale Image2
		ImageProcessor ip_gray2 = new ByteProcessor(ip2.getWidth(), ip2.getHeight());
		int[] rgb_pixels2 = (int[]) ip2.getPixels();
		byte[] gray_pixels2 = (byte[]) ip_gray2.getPixels();
		
		for (int i=0; i<rgb_pixels2.length; i++) {
			int r2 = (rgb_pixels2[i] & 0xff0000) >> 16;
			int g2 = (rgb_pixels2[i] & 0x00ff00) >> 8;
			int b2 = (rgb_pixels2[i] & 0x0000ff);
			gray_pixels2[i] = (byte) (0.299*r2 + 0.587*g2 + 0.114*b2 + 0.5);
		}
		
		ImagePlus grayImage2 = new ImagePlus("Gray Image2", ip_gray2);
		grayImage2.show();
	}
}
