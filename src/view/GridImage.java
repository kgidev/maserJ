/**
 * 
 */
package view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.commons.io.FilenameUtils;

import controller.MaserJ;

import tools.MyImage;

/**
 *
 */
public class GridImage extends BufferedImage {
	private static int imageType=MaserJ.BIMG_TYPE;	
	private Graphics2D g2d;

	protected int width=super.getWidth();
	protected int height=super.getHeight();
	protected BufferedImage gridImg;
//	protected BufferedImage yAxis;
	protected String caption = "Line-of-sight-velocity";
	protected String unitStr = "mas";
	protected int[] unitRange = { -width/2, width/2 }; //unit
	protected int scaleSize = 50; // scalelineDistance in px
	protected int scaleLine = 5; 	//length in px
	protected Color bgColor; 	//BackGroundColor
	protected Color fgColor=Color.BLACK; 	//ForeGroundColor
	protected int xUnit = 10; 	//units distanxe on X-axis in px
	protected int yUnit = 10; 	//units distanxe on X-axis in px
	
	/**
	 * @param width
	 * @param height
	 */
	public GridImage(int width, int height) {
		super(width, height, imageType);
		g2d=this.createGraphics();
		setGridImg(createGridImage(width, height, xUnit, yUnit, unitStr, fgColor));
		compose();
	}

	/**
	 * compose this Image from the parts
	 */
	public void compose() {
		
		g2d.drawImage(gridImg, 0, 0, null);
	}
	
	/**
	 * create a GridImage
	 * 
	 * @param ????
	 *            TODO -javadoc -other sizeValues asymetric
	 */
	public BufferedImage createGridImage(int width, int height, int xUnit,
			int yUnit, String unitStr, Color gridColor) {
		//gridColor = gridcolor;
		int largeScale = 10;
		int smallScale = largeScale / 2;
		BufferedImage gridImg = new BufferedImage(width, height, imageType);
		MyImage.setBackgroundColor(gridImg, bgColor);
		Graphics2D gridG2D = gridImg.createGraphics();
		FontMetrics fMetric = gridG2D.getFontMetrics();
	
		// Outline
		int outLineWidth = (width % 2 == 0) ? width - 99 : width - 100;
		int outLineHeight = (height % 2 == 0) ? height - 99 : height - 100;
		final int marginLeft = 49;
		final int marginTop = 49;
		gridG2D.setColor(fgColor);
		gridG2D.drawRect(marginLeft, marginTop, outLineWidth, outLineHeight);
		//gridG2D.drawRect(0, 0, gridWidth, gridHeight);
	
		// units & Grid
		int xCenter = outLineWidth / 2 + marginLeft;
		int yCenter = outLineHeight / 2 + marginTop;
		gridG2D.translate(xCenter, yCenter); // set point of origin of
		// X-Axis
		BufferedImage xAxis = MyImage.cartesianAxis(outLineWidth, xUnit, -outLineWidth / 2,
				outLineHeight / 2, largeScale, smallScale, "X-Offset ("
						+ unitStr + ")", "X", gridG2D.getFont(), gridColor,
				null, 1);
		gridG2D.drawImage(xAxis, -outLineWidth / 2 + 1, outLineHeight / 2 + 2,
				null);
		BufferedImage xAxisTop = new BufferedImage(xAxis.getWidth(),
				largeScale + 2, imageType);
		Graphics2D xAxisTopG2d = xAxisTop.createGraphics();
		xAxisTopG2d.drawImage(xAxis, 0, 0, null);
		AffineTransform affineT = new AffineTransform();
		affineT.rotate(Math.toRadians(180), 0, 0);
		affineT.translate(-outLineWidth / 2 - 2, outLineHeight / 2);
		gridG2D.drawImage(xAxisTop, affineT, null);
		// Y-Axis
		BufferedImage yAxis = MyImage.cartesianAxis(outLineWidth, yUnit, -outLineWidth / 2,
				outLineHeight / 2, largeScale, smallScale, "Y-Offset ("
						+ unitStr + ")", "Y", gridG2D.getFont(), gridColor,
				null, 1);
		
		affineT = new AffineTransform();
		affineT.rotate(Math.toRadians(90), 0, 0);
//		affineT.rotate(Math.toRadians(180), 0, 0);
//		affineT.translate(-outLineWidth / 2 + 1, outLineHeight / 2);

		affineT.translate(-outLineWidth / 2 + 1, outLineHeight / 2);
		gridG2D.drawImage(yAxis, affineT, null);
		//rotate caption
		BufferedImage yCaption = new BufferedImage(yAxis.getWidth(),
				20, imageType);
		Graphics2D yCaptionG2d = yCaption.createGraphics();
		yCaptionG2d.drawImage(yAxis, 0, -11, null);
		affineT = new AffineTransform();
		affineT.rotate(Math.toRadians(-90), 0, 0);
		affineT.translate(-outLineWidth / 2 , -outLineWidth / 2-30 );
		gridG2D.setBackground(Color.WHITE);
		gridG2D.clearRect(-outLineHeight/2-30, -outLineHeight/2,
				yCaption.getHeight(), yCaption.getWidth());
		gridG2D.drawImage(yCaption, affineT, null);
		
		//yAxis on right border
		BufferedImage yAxisRight = new BufferedImage(yAxis.getWidth(),
				largeScale + 2, imageType);
		Graphics2D yAxisRightG2d = yAxisRight.createGraphics();
		yAxisRightG2d.drawImage(yAxis, 0, 0, null);
		affineT = new AffineTransform();
		affineT.rotate(Math.toRadians(270), 0, 0);
		affineT.translate(-outLineWidth / 2 - 2, outLineWidth / 2 + 2);
		gridG2D.drawImage(yAxisRight, affineT, null);
		return gridImg;
	}
	
	//getter & setter
	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * @return the gridImg
	 */
	public BufferedImage getGridImg() {
		return gridImg;
	}

	/**
	 * @param gridImg the gridImg to set
	 */
	public void setGridImg(BufferedImage gridImg) {
		this.gridImg = gridImg;
	}

	/**
	 * @return the unitStr
	 */
	public String getUnitStr() {
		return unitStr;
	}

	/**
	 * @param unitStr the unitStr to set
	 */
	public void setUnitStr(String unitStr) {
		this.unitStr = unitStr;
	}

}
