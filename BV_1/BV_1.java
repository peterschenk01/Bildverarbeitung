import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class BV_1 implements PlugIn {

    public void run(String arg) {
		ImagePlus image = new ImagePlus("C:/Users/peter/Downloads/Emir.jpg");
		image.show();
		ImageProcessor ip = image.getProcessor();
		ImageProcessor ip_blue = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
		ImageProcessor ip_green = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
		ImageProcessor ip_red = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
		
		byte[] pixels = (byte[]) ip.getPixels();
		byte[] blue_pixels = (byte[]) ip_blue.getPixels();
		byte[] green_pixels = (byte[]) ip_green.getPixels();
		byte[] red_pixels = (byte[]) ip_red.getPixels();
		
		System.arraycopy(pixels, 0, blue_pixels, 0, (pixels.length / 3));
		System.arraycopy(pixels, (pixels.length / 3), green_pixels, 0, (pixels.length / 3));
		System.arraycopy(pixels, (2 * (pixels.length / 3)), red_pixels, 0, (pixels.length / 3));
		ImagePlus blue_image = new ImagePlus("Blue Image", ip_blue);
		ImagePlus green_image = new ImagePlus("Green Image", ip_green);
		ImagePlus red_image = new ImagePlus("Red Image", ip_red);
		blue_image.show();
		green_image.show();
		red_image.show();
		
		byte[] color_pixels[];
		//for(int i = 0; i 
    }

}