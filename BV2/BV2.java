import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import ij.gui.Overlay;
import ij.gui.Line;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import java.awt.Color;
import java.awt.Polygon;
import java.util.concurrent.ThreadLocalRandom;

public class BV2 implements PlugInFilter {
    public static String INPUT_DIR = "C:\\Users\\peter\\Downloads\\";
    public static String INPUT_IMA = "Emir.jpg";
    
    Overlay myOverlay = new Overlay();

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G + NO_UNDO + NO_CHANGES;
    }
    
    public void run(ImageProcessor ip) {
        long start = System.currentTimeMillis(); // Start der Laufzeitmessung
       
        // Erstellen der Prozessoren
        ImageProcessor ip_blue = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
        ImageProcessor ip_green = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
        ImageProcessor ip_red = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
       
        // Erstellen der Arrays für die Pixel
        byte[] pixels = (byte[]) ip.getPixels();
        byte[] blue_pixels = (byte[]) ip_blue.getPixels();
        byte[] green_pixels = (byte[]) ip_green.getPixels();
        byte[] red_pixels = (byte[]) ip_red.getPixels();
       
        // Erstellen der einzelnen Bilder
        System.arraycopy(pixels, 0, blue_pixels, 0, (pixels.length / 3));
        System.arraycopy(pixels, (pixels.length / 3), green_pixels, 0, (pixels.length / 3));
        System.arraycopy(pixels, (2 * (pixels.length / 3)), red_pixels, 0, (pixels.length / 3));
       
        // Bilder ausrichten (verschieben)
        
        //ip_blue.translate(-18, -55);
        //ip_red.translate(18, 55);
        
        for(int i = 0; i < 10; i++) {
            addCrossOverlay(ThreadLocalRandom.current().nextInt(ip_blue.getWidth() / 4, 3 * (ip_blue.getWidth() / 4) + 1), ThreadLocalRandom.current().nextInt(ip_blue.getHeight() / 4, 3 * (ip_blue.getHeight() / 4) + 1));
        }
        showOverlay(ip_blue, myOverlay, "Overlay");
        
        // Bilder zu einem Farbbild zusammenfügen
        /* ImageProcessor ip_color = new ColorProcessor(ip.getWidth(), (ip.getHeight() / 3));
        int[] color_pixels = (int[]) ip_color.getPixels();
        for(int i = 0; i < blue_pixels.length; i++) {
            color_pixels[i] = ((red_pixels[i] & 0xff) << 16) | ((green_pixels[i] & 0xff) << 8) | (blue_pixels[i] & 0xff);
        }
        ImagePlus colorImage = new ImagePlus("Color Image", ip_color);
       
        long ende = System.currentTimeMillis(); // Ende der Laufzeitmessung
       
        System.out.println(ende - start); // Ausgeben der Laufzeit
       
        colorImage.show(); */
    }
    
    public static void main(String[] args) {
    	ImageJ myImageJ = new ImageJ();
    	myImageJ.exitWhenQuitting(true);
    	
    	ImagePlus image = IJ.openImage(INPUT_DIR + INPUT_IMA);
    	if (image == null)
    	{
    		IJ.error("Couldn't open image " + INPUT_IMA);
    		System.exit(-1);
        }
            
    	//image.show();
    	IJ.runPlugIn(image, "BV2", "");
     }
     
     void addCrossOverlay(int x, int y) {
    	 Line line1 = new Line(x, y - 50, x, y + 50);
    	 Line line2 = new Line(x - 50, y, x + 50, y);
    	 line1.setStrokeColor(Color.MAGENTA);
    	 line1.setStrokeWidth(1);
    	 line2.setStrokeColor(Color.MAGENTA);
    	 line2.setStrokeWidth(1);
    	 myOverlay.add(line1);
    	 myOverlay.add(line2);
     }
     
     static void showOverlay(ImageProcessor ip, Overlay myOverlay, String title) {
    	 ImagePlus impOverlay = new ImagePlus(title, ip);
    	 impOverlay.setOverlay(myOverlay);
    	 impOverlay.show();
     }
}