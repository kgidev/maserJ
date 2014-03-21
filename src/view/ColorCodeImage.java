/**
 * 
 */
package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

import controller.MaserJ;

import tools.MyImage;

/**
 *
 */
public class ColorCodeImage extends BufferedImage {
	
	private Graphics2D g2d;
	private static int imageType=MaserJ.BIMG_TYPE;
	protected int width=super.getWidth();
	protected int height=super.getHeight();
	protected BufferedImage colorImg;
	protected BufferedImage scaleImg;
	protected String caption = "Line-of-sight-velocity";
	protected String unitStr = "km/s";
	protected int[] unitRange = { -height/2, height/2 }; //unit
	protected int scaleSize = 50; // scalelineDistance in px
	protected int scaleLine = 5; 	//length in px
	protected boolean portrait= true; 	//true=portrait,false=landscape
	
	
	/**
	 * @param width
	 * @param height
	 */
	public ColorCodeImage(int width, int height) {
		super(width, height, imageType);
		g2d=this.createGraphics();
		setColorImg(MyImage.colorCodeImg());
		setScaleImg(createScale());
		compose();
	}

	/**
	 * create a scale-image for the ColorCode
	 */
	public BufferedImage createScale() {
		
		BufferedImage axis = MyImage.cartesianAxis(height, scaleSize, unitRange[0], unitRange[1],
				2 * scaleLine, scaleLine, caption + " (" + unitStr + ")", "X",
				g2d.getFont(), Color.BLACK, null, 0.02f);
		BufferedImage scaleImg = new BufferedImage(height,axis.getWidth(), imageType);
		Graphics2D g2DcolorCode = scaleImg.createGraphics();
		g2DcolorCode.drawImage(axis, 0,0, null);
		return scaleImg;
	}
	
	/**
	 * compose this Image from the parts
	 */
	public void compose() {
		BufferedImage tmpImg =new BufferedImage(height,width,imageType);
		Graphics2D tmpG2d = tmpImg.createGraphics();
		tmpG2d.drawImage(colorImg, 0, 0, null);
		tmpG2d.drawImage(scaleImg, 0, 0, null);
		
		g2d.drawImage(MyImage.rotateImg(tmpImg, 90), 0, 0, null);
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
	 * @return the colorImg
	 */
	public BufferedImage getColorImg() {
		return colorImg;
	}

	/**
	 * @param colorImg the colorImg to set
	 */
	public void setColorImg(BufferedImage colorImg) {
		this.colorImg = colorImg;
	}

	/**
	 * @return the scaleImg
	 */
	public BufferedImage getScaleImg() {
		return scaleImg;
	}

	/**
	 * @param scaleImg the scaleImg to set
	 */
	public void setScaleImg(BufferedImage scaleImg) {
		this.scaleImg = scaleImg;
	}

	/**
	 * @return the unitRange
	 */
	public int[] getUnitRange() {
		return unitRange;
	}

	/**
	 * @param unitRange the unitRange to set
	 */
	public void setUnitRange(int[] unitRange) {
		this.unitRange = unitRange;
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
