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
    
    Overlay myOverlayBlue = new Overlay();
    Overlay myOverlayGreen = new Overlay();
    Overlay myOverlayRed = new Overlay();

    public int setup(String arg, ImagePlus imp) {
        return DOES_8G + NO_UNDO + NO_CHANGES;
    }
    
    public void run(ImageProcessor ip) {
    	long start = System.currentTimeMillis(); // Start der Laufzeitmessung
       
        // Image Prozessoren
        ImageProcessor ip_blue = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
        ImageProcessor ip_green = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
        ImageProcessor ip_red = new ByteProcessor((ip.getWidth()), (ip.getHeight() / 3));
       
        // Arrays für die Pixel
        byte[] pixels = (byte[]) ip.getPixels();
        byte[] blue_pixels = (byte[]) ip_blue.getPixels();
        byte[] green_pixels = (byte[]) ip_green.getPixels();
        byte[] red_pixels = (byte[]) ip_red.getPixels();
       
        // einzelne Bilder
        System.arraycopy(pixels, 0, blue_pixels, 0, (pixels.length / 3));
        System.arraycopy(pixels, (pixels.length / 3), green_pixels, 0, (pixels.length / 3));
        System.arraycopy(pixels, (2 * (pixels.length / 3)), red_pixels, 0, (pixels.length / 3));
        
        // ASBs
        long[][] asb_blue = asb(ip_blue);
        long[][] asb_green = asb(ip_green);
        long[][] asb_red = asb(ip_red);
        
        // quadrierte ASBs
        long[][] asb_blue_squared = asb_squared(ip_blue);
        long[][] asb_green_squared = asb_squared(ip_green);
        long[][] asb_red_squared = asb_squared(ip_red);
        
        Point[] point = new Point[10];
        Point[] correspondingPoint_green = new Point[point.length];
        Point[] correspondingPoint_red = new Point[point.length];
        
        int[] verschiebungsvektorBlueGreen_x = new int[point.length];
        int[] verschiebungsvektorBlueGreen_y = new int[point.length];

        int[] verschiebungsvektorBlueRed_x = new int[point.length];
        int[] verschiebungsvektorBlueRed_y = new int[point.length];
        
        for(int i = 0; i < point.length; i++) {
            // Zufallspunkte
        	point[i] = new Point(ThreadLocalRandom.current().nextInt(ip_blue.getWidth() / 4, 3 * (ip_blue.getWidth() / 4) + 1), 
            						ThreadLocalRandom.current().nextInt(ip_blue.getHeight() / 4, 3 * (ip_blue.getHeight() / 4) + 1));
            
        	// korrespondierende Punkte in den Grün und Rot Bildern
            correspondingPoint_green[i] = findCorrelation(point[i], ip_blue, ip_green, asb_blue, asb_blue_squared, asb_green, asb_green_squared);
            correspondingPoint_red[i] = findCorrelation(point[i], ip_blue, ip_red, asb_blue, asb_blue_squared, asb_red, asb_red_squared);
            
            // Verschiebungsvektoren Blau zu Grün
            verschiebungsvektorBlueGreen_x[i] = point[i].x - correspondingPoint_green[i].x;
            verschiebungsvektorBlueGreen_y[i] = point[i].y - correspondingPoint_green[i].y;
            
            // Verschiebungsvektoren Blau zu Rot
            verschiebungsvektorBlueRed_x[i] = point[i].x - correspondingPoint_red[i].x;
            verschiebungsvektorBlueRed_y[i] = point[i].y - correspondingPoint_red[i].y;
            
            // Darstellung der Zufallspunkte und Korrespondenzen
            addCrossToOverlay(point[i], myOverlayBlue);
            addWindowToOverlay(correspondingPoint_green[i], myOverlayGreen);
            addWindowToOverlay(correspondingPoint_red[i], myOverlayRed);
            
            System.out.println(i + "   Punkt(" + point[i].x + ", " + point[i].y + ")   " + verschiebungsvektorBlueGreen_x[i] + 
            					", " +  verschiebungsvektorBlueGreen_y[i] + "    " + verschiebungsvektorBlueRed_x[i] + ", " + 
            					verschiebungsvektorBlueRed_y[i]);
        }
        
        showOverlay(ip_blue, myOverlayBlue, "Overlay Blue");
        showOverlay(ip_green, myOverlayGreen, "Overlay Green");
        showOverlay(ip_red, myOverlayRed, "Overlay Red");
        
        // Verschieben der Grün- und Rotbilder
        ip_green.translate(verschiebungsvektorBlueGreen_x[5], verschiebungsvektorBlueGreen_y[5]);
        ip_red.translate(verschiebungsvektorBlueRed_x[5], verschiebungsvektorBlueRed_y[5]);
        
        // Bilder zu einem Farbbild zusammenfügen
        ImageProcessor ip_color = new ColorProcessor(ip.getWidth(), (ip.getHeight() / 3));
        int[] color_pixels = (int[]) ip_color.getPixels();
        for(int i = 0; i < blue_pixels.length; i++) {
            color_pixels[i] = ((red_pixels[i] & 0xff) << 16) | ((green_pixels[i] & 0xff) << 8) | (blue_pixels[i] & 0xff);
        }
        ImagePlus colorImage = new ImagePlus("Color Image", ip_color);
       
        long ende = System.currentTimeMillis(); // Ende der Laufzeitmessung
       
        System.out.println(ende - start); // Ausgeben der Laufzeit
       
        colorImage.show();
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
    
    public Point findCorrelation(Point point, ImageProcessor ip, ImageProcessor ip_corr, long[][] asb, long [][] asb_squared, 
    								long[][] asb_corr, long[][] asb_corr_squared) {
    	
    	// Benötigte Werte des Zufallspunkts für Korrelationsberechnung
    	long sum_pixels = windowPixels(asb, point.x, point.y);
        double mittelwert = sum_pixels / 625.0;
        double standardAbweichung = Math.sqrt((windowPixels(asb_squared, point.x, point.y) / 625.0) - 
        											(Math.pow(mittelwert, 2.0)));
        
        // Punkte im Fenster um den Zufallspunkt für spätere Berechnung
        Roi roi = new Roi(point.x - 12, point.y - 12, 25, 25);
        Point[] points = roi.getContainedPoints();
        
        // Suchbereich und die darin enthaltenen Punkte
        Roi searchArea = new Roi(point.x - 25, point.y - 90, 50, 180);
        Point[] searchPoints = searchArea.getContainedPoints();
        
        Point correlation = searchPoints[0];
        double check = -1.0;
        
        for(int i = 0; i < searchPoints.length; i++) {
        	// Benötigte Werte des Suchpunkts für Korrelationsberechnung
        	long sum_pixels_corr = windowPixels(asb_corr, searchPoints[i].x, searchPoints[i].y);
            double mittelwert_corr = sum_pixels_corr / 625.0;
            double standardAbweichung_corr = Math.sqrt((windowPixels(asb_corr_squared, searchPoints[i].x, searchPoints[i].y) / 625.0) - 
    													(Math.pow(mittelwert_corr, 2.0)));
            
            // Punkte im Fenster um den Suchpunkt für spätere Berechnung
            Roi roi_corr = new Roi(searchPoints[i].x - 12, searchPoints[i].y - 12, 25, 25);
            Point[] points_corr = roi_corr.getContainedPoints();
            
            // Summe der Multiplikationen der einzelnen Pixelwerte
            long sum = 0;
            for(int j = 0; j < points.length; j++) {
            	sum = (long) (sum + (ip.getPixel(points[j].x, points[j].y) * ip_corr.getPixel(points_corr[j].x, points_corr[j].y)));
            }
            
            // Korrelation c
            double c = ((sum / 625.0) - (mittelwert * mittelwert_corr)) / (standardAbweichung * standardAbweichung_corr);
            
            if(c > check) {
            	check = c;
            	correlation = searchPoints[i];
            }
        }
    	
    	return correlation;
    }
    
    // Erstellen eines Akkumulierten Summenbildes
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
    
    // Erstellen eines Akkumulierten Summenbildes aufgrund quadrierter Pixelwerte
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
    
    // Summe der Pixel in einem Fenster
    public long windowPixels(long[][] asb, int point_x, int point_y) {
    	return asb[point_x + 12][point_y + 12] - asb[point_x + 12][point_y - 12 - 1]
				- asb[point_x - 12 - 1][point_y + 12] + asb[point_x - 12 - 1][point_y - 12 - 1];
    }
     
	public void addCrossToOverlay(Point point, Overlay overlay) {
		Line line1 = new Line(point.x, point.y - 50, point.x, point.y + 50);
		Line line2 = new Line(point.x - 50, point.y, point.x + 50, point.y);
    	line1.setStrokeColor(Color.MAGENTA);
    	line1.setStrokeWidth(1);
    	line2.setStrokeColor(Color.MAGENTA);
    	line2.setStrokeWidth(1);
    	overlay.add(line1);
    	overlay.add(line2);
	}
     
	public void addWindowToOverlay(Point point, Overlay overlay) {
		Roi window = new Roi(point.x - 12, point.y - 12, 25, 25);
		window.setStrokeColor(Color.GREEN);
		window.setStrokeWidth(2);
		overlay.add(window);
	}
     
  	public static void showOverlay(ImageProcessor ip, Overlay myOverlay, String title) {
  		ImagePlus impOverlay = new ImagePlus(title, ip);
  		impOverlay.setOverlay(myOverlay);
  		impOverlay.show();
  	}
}