import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;
import ij.gui.Overlay;
import ij.gui.Line;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import java.awt.Color;
import java.awt.Point;
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
       // long start = System.currentTimeMillis(); // Start der Laufzeitmessung
       
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
        
        long[][] asb_blue = asb(ip_blue);
        long[][] asb_green = asb(ip_green);
        long[][] asb_red = asb(ip_red);
        
        long[][] asb_blue_squared = asb_squared(ip_blue);
        long[][] asb_green_squared = asb_squared(ip_green);
        long[][] asb_red_squared = asb_squared(ip_red);
        
        int[] point_x = new int[10];
        int[] point_y = new int[10];
        for(int i = 0; i < 10; i++) {
            point_x[i] = ThreadLocalRandom.current().nextInt(ip_blue.getWidth() / 4, 3 * (ip_blue.getWidth() / 4) + 1);
            point_y[i] = ThreadLocalRandom.current().nextInt(ip_blue.getHeight() / 4, 3 * (ip_blue.getHeight() / 4) + 1);
            addCrossToOverlay(point_x[i], point_y[i]);
        }
        
        long sum_pixels_blue = windowPixels(asb_blue, point_x[0], point_y[0]);
        double mittelwert_blue = sum_pixels_blue / 625.0;
        double standardAbweichung_blue = Math.sqrt((windowPixels(asb_blue_squared, point_x[0], point_y[0]) / 625.0) - 
        											(Math.pow(mittelwert_blue, 2.0)));
        
        long sum_pixels_green = windowPixels(asb_green, point_x[0] - 25, point_y[0] - 90);
        double mittelwert_green = sum_pixels_green / 625.0;
        double standardAbweichung_green = Math.sqrt((windowPixels(asb_green_squared, point_x[0] - 25, point_y[0] - 90) / 625.0) - 
													(Math.pow(mittelwert_green, 2.0)));
        
        Roi roi_blue = new Roi(point_x[0] - 12, point_y[0] - 12, 25, 25);
        Point[] points_blue = roi_blue.getContainedPoints();
        
        Roi roi_green = new Roi(point_x[0] - 25 - 12, point_y[0] - 90 - 12, 25, 25);
        Point[] points_green = roi_green.getContainedPoints();
        
        long sum = 0;
        for(int i = 0; i < points_blue.length; i++) {
        	sum = (long) (sum + (ip_blue.getPixel(points_blue[i].x, points_blue[i].y) * ip_green.getPixel(points_green[i].x, points_green[i].y)));
        }
        
        double c = ((sum / 625.0) - (mittelwert_blue * mittelwert_green)) / 
        			(standardAbweichung_blue * standardAbweichung_green);
        
        System.out.println(mittelwert_blue + "\n" + standardAbweichung_blue + "\n" + standardAbweichung_green + "\n" + c);
        
        
        
        /* Point[] windowPoints = windows[0].getContainedPoints();
        byte[] windowPixels = new byte[windowPoints.length];
        for(int i = 0; i < windowPoints.length; i++) {
        	windowPixels[i] = (byte) ip_blue.getPixel(windowPoints[i].x, windowPoints[i].y);
        }
        
        Roi searchArea = new Roi(point_x[0] - 25, point_y[0] - 90, 50, 180);
        Point[] searchPoints = searchArea.getContainedPoints();
        
        Roi pointWindow = new Roi(searchPoints[0].x - 12, searchPoints[0].y - 12, 25, 25);
        Point[] pointWindowPoints = pointWindow.getContainedPoints();
        byte[] pointWindowPixels = new byte[pointWindowPoints.length];
        for(int i = 0; i < pointWindowPoints.length; i++) {
        	pointWindowPixels[i] = (byte) ip_green.getPixel(pointWindowPoints[i].x, pointWindowPoints[i].y);
        }
        
        byte mittelwertBlue = 0;
        for(int i = 0; i < windowPixels.length; i++) {
        	mittelwertBlue = (byte) (mittelwertBlue + windowPixels[i]);
        }
        mittelwertBlue = (byte) (mittelwertBlue / windowPixels.length);
        
        byte mittelwertGreen = 0;
        for(int i = 0; i < windowPixels.length; i++) {
        	mittelwertGreen = (byte) (mittelwertGreen + windowPixels[i]);
        }
        mittelwertGreen = (byte) (mittelwertGreen / windowPixels.length);
        
        byte d = 0;
        for(int i = 0; i < windowPixels.length; i++) {
        	d = (byte) (d + (((windowPixels[i] - mittelwertBlue) - (pointWindowPixels[i] - mittelwertGreen))) ^ 2);
        }
        
        System.out.println(d); */
        
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
    
    public long[][] asb(ImageProcessor ip) {
    	long[][] asb = new long[ip.getHeight()][ip.getWidth()];
    	long sum_row = 0;
    	
    	for(int col = 0; col < ip.getWidth(); col++) {
    		sum_row = (long) (sum_row + ip.getPixel(0, col));
    		asb[0][col] = sum_row;
    	}
    	
    	for(int row = 1; row < ip.getHeight(); row++) {
    		sum_row = 0;
    		for(int col = 0; col < ip.getWidth(); col++) {
    			sum_row = (long) (sum_row + ip.getPixel(row, col));
    			asb[row][col] = sum_row + asb[row - 1][col];
    		}
    	}
    	
    	return asb;
    }
    
    public long[][] asb_squared(ImageProcessor ip) {
    	long[][] asb = new long[ip.getHeight()][ip.getWidth()];
    	long sum_row = 0;
    	
    	for(int col = 0; col < ip.getWidth(); col++) {
    		sum_row = (long) (sum_row + Math.pow(ip.getPixel(0, col), 2));
    		asb[0][col] = sum_row;
    	}
    	
    	for(int row = 1; row < ip.getHeight(); row++) {
    		sum_row = 0;
    		for(int col = 0; col < ip.getWidth(); col++) {
    			sum_row = (long) (sum_row + Math.pow(ip.getPixel(row, col), 2));
    			asb[row][col] = sum_row + asb[row - 1][col];
    		}
    	}
    	
    	return asb;
    }
    
    public long windowPixels(long[][] asb, int point_x, int point_y) {
    	return asb[point_x + 12][point_y + 12] - asb[point_x + 12][point_y - 12 - 1]
				- asb[point_x - 12 - 1][point_y + 12] + asb[point_x - 12 - 1][point_y - 12 - 1];
    }
     
	public void addCrossToOverlay(int x, int y) {
		Line line1 = new Line(x, y - 50, x, y + 50);
		Line line2 = new Line(x - 50, y, x + 50, y);
    	line1.setStrokeColor(Color.MAGENTA);
    	line1.setStrokeWidth(1);
    	line2.setStrokeColor(Color.MAGENTA);
    	line2.setStrokeWidth(1);
    	myOverlay.add(line1);
    	myOverlay.add(line2);
	}
     
	public void addWindowToOverlay(int x, int y) {
		Roi window = new Roi(x - 12, y - 12, 25, 25);
		window.setStrokeColor(Color.GREEN);
		window.setStrokeWidth(2);
		myOverlay.add(window);
	}
     
  	public static void showOverlay(ImageProcessor ip, Overlay myOverlay, String title) {
  		ImagePlus impOverlay = new ImagePlus(title, ip);
  		impOverlay.setOverlay(myOverlay);
  		impOverlay.show();
  	}
}