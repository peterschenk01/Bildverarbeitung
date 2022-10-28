import ij.*;
import ij.process.*;
import ij.plugin.filter.PlugInFilter;

public class BV1 implements PlugInFilter {
    public static String INPUT_DIR = "C:\\Users\\peter\\Downloads\\";
    public static String INPUT_IMA = "Emir.jpg";

    public int setup(String arg, ImagePlus imp)
    {
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
        ip_blue.translate(-18, -55);
        ip_red.translate(18, 55);
       
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
    
     public static void main(String[] args)
     {       
         ImageJ myImageJ = new ImageJ();
         myImageJ.exitWhenQuitting(true);
        
         ImagePlus image = IJ.openImage(INPUT_DIR + INPUT_IMA);
         if (image == null)
         {
             IJ.error("Couldn't open image " + INPUT_IMA);
             System.exit(-1);
         }
            
         image.show();
         IJ.runPlugIn(image, "BV1", "");
     }
}