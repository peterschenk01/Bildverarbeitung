import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class BV_1 implements PlugIn {

    public void run(String arg) {
		ImagePlus image = new ImagePlus("C:/Users/pschenk/Downloads/Emir.jpg");
		image.show();
		ImageProcessor ip = image.getProcessor();
		ImageProcessor ip_blue = new ByteProcessor((ip.getWidth() / 3), (ip.getHeight() / 3));
		
		
		
		byte[] pixels = (byte[]) ip.getPixels();
		byte[] blue_pixels = (byte[]) ip_blue.getPixels();
		byte[] green;
		byte[] red;
		
		System.arraycopy(pixels, 0, blue_pixels, 0, (pixels.length / 3));
		ImagePlus blue_image = new ImagePlus("Blue Image", ip_blue);
		blue_image.show();
		
		
    }

}