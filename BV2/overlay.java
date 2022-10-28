import ij.gui.Overlay;
import ij.gui.Line;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import java.awt.Color;
import java.awt.Polygon;

//**********************************************************************
Overlay makeLineOverlay() {
	Overlay myOverlay = new Overlay();
 		
	Line myLine = new Line(x_left, y_left, x_right, y_right);
	myLine.setStrokeColor(Color.MAGENTA);
	myLine.setStrokeWidth(1);	
	myOverlay.add(myLine);
	// more Lines / Circles / Rectangles etc. could be added here

  	return myOverlay;
}

//**********************************************************************
Overlay makeRoiOverlay() {
	Overlay myOverlay = new Overlay();
 		
	Polygon myPoly = new Polygon();
	myPoly.addPoint(col1, row1);
	myPoly.addPoint(col2, row1);
	myPoly.addPoint(col2, row2);
			
	Roi myRoi = new PolygonRoi(myPoly, Roi.POLYLINE);
	myRoi.setStrokeColor(Color.GREEN);
	myRoi.setStrokeWidth(2);	
						
	myOverlay.add(myRoi);
	// more Rois could be added to the same Overlay here

  	return myOverlay;
}
	
//**********************************************************************
// show new image with overlay
static void showOverlay(ImageProcessor ip, Overlay myOverlay, String title) {
	ImagePlus impOverlay = new ImagePlus(title, ip);
	impOverlay.setOverlay(myOverlay);
	impOverlay.show();
}