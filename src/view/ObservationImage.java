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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;

import controller.MaserJ;

import tools.MyImage;

/**
 *
 */
public class ObservationImage extends BufferedImage {
	
	protected static DecimalFormat decimalFormat = new DecimalFormat( "###,##0.0000");
	protected static SimpleDateFormat celestialAngelFormat = 
		new SimpleDateFormat("HH:mm:ss:SSS");
	
	private static int imageType=MaserJ.BIMG_TYPE;	
	private Graphics2D g2d=this.createGraphics();
	
	protected int width=super.getWidth();
	protected int height=super.getHeight();
	protected Color bgColor=Color.WHITE; 	//BackGroundColor
	protected Color fgColor=Color.BLACK; 	//ForeGroundColor
	protected int margin=25;
	protected int padding=2*margin;
	
	
	//parts
	protected BufferedImage gridImg;
	protected int gridImgX=0;
	protected int gridImgY=0;
	protected BufferedImage colorCodeImg;
	protected int colorCodeImgX=575;
	protected int colorCodeImgY=padding;
	protected BufferedImage celestialObjectTextImg;
	protected int celestialObjectTextImgX=650;
	protected int celestialObjectTextImgY=padding;
	public BufferedImage observationTextImg;
	protected int observationTextImgX=650;
	protected int observationTextImgY=300;
	protected BufferedImage maserImg;
	protected int maserImgX=padding;
	protected int maserImgY=padding;
	

	
	/**
	 * @param width
	 * @param height
	 */
	public ObservationImage(int width, int height) {
		super(width, height, imageType);
		g2d=this.createGraphics();

	}
	
	/**
	 * @param width
	 * @param height
	 */
	public ObservationImage(int width, int height, BufferedImage gridImg,
			BufferedImage colorCodeImg, BufferedImage celestialObjectTextImg,
			BufferedImage observationTextImg, BufferedImage maserImg) {
		super(width, height, imageType);
		g2d=this.createGraphics();

		setGridImg(gridImg);
		setColorCodeImg(colorCodeImg);
		setCelestialObjectTextImg(celestialObjectTextImg);
		setObservationTextImg(observationTextImg);
		setMaserImg(maserImg);
		compose();
	}

	/**
	 * compose this Image from the parts
	 */
	public void compose() {
		g2d.setBackground(bgColor);
		g2d.clearRect(0, 0, width, height);
		boolean drawed=g2d.drawImage(gridImg, gridImgX, gridImgY, null);
		drawed=g2d.drawImage(colorCodeImg, colorCodeImgX, colorCodeImgY, null);
		drawed=g2d.drawImage(celestialObjectTextImg, celestialObjectTextImgX, celestialObjectTextImgY, null);
		drawed=g2d.drawImage(observationTextImg, observationTextImgX, observationTextImgY, null);
		drawed=g2d.drawImage(maserImg, maserImgX, maserImgY, null);
	}

	//getter & setter
	
	/**
	 * @return the celestialObjectTextImg
	 */
	public BufferedImage getCelestialObjectTextImg() {
		return celestialObjectTextImg;
	}

	/**
	 * @param celestialObjectTextImg the celestialObjectTextImg to set
	 */
	public void setCelestialObjectTextImg(BufferedImage celestialObjectTextImg) {
		this.celestialObjectTextImg = celestialObjectTextImg;
	}

	/**
	 * @return the celestialObjectTextImgX
	 */
	public int getCelestialObjectTextImgX() {
		return celestialObjectTextImgX;
	}

	/**
	 * @param celestialObjectTextImgX the celestialObjectTextImgX to set
	 */
	public void setCelestialObjectTextImgX(int celestialObjectTextImgX) {
		this.celestialObjectTextImgX = celestialObjectTextImgX;
	}

	/**
	 * @return the celestialObjectTextImgY
	 */
	public int getCelestialObjectTextImgY() {
		return celestialObjectTextImgY;
	}

	/**
	 * @param celestialObjectTextImgY the celestialObjectTextImgY to set
	 */
	public void setCelestialObjectTextImgY(int celestialObjectTextImgY) {
		this.celestialObjectTextImgY = celestialObjectTextImgY;
	}

	/**
	 * @return the colorCodeImg
	 */
	public BufferedImage getColorCodeImg() {
		return colorCodeImg;
	}

	/**
	 * @param colorCodeImg the colorCodeImg to set
	 */
	public void setColorCodeImg(BufferedImage colorCodeImg) {
		this.colorCodeImg = colorCodeImg;
	}

	/**
	 * @return the colorCodeImgX
	 */
	public int getColorCodeImgX() {
		return colorCodeImgX;
	}

	/**
	 * @param colorCodeImgX the colorCodeImgX to set
	 */
	public void setColorCodeImgX(int colorCodeImgX) {
		this.colorCodeImgX = colorCodeImgX;
	}

	/**
	 * @return the colorCodeImgY
	 */
	public int getColorCodeImgY() {
		return colorCodeImgY;
	}

	/**
	 * @param colorCodeImgY the colorCodeImgY to set
	 */
	public void setColorCodeImgY(int colorCodeImgY) {
		this.colorCodeImgY = colorCodeImgY;
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
	 * @return the gridImgX
	 */
	public int getGridImgX() {
		return gridImgX;
	}

	/**
	 * @param gridImgX the gridImgX to set
	 */
	public void setGridImgX(int gridImgX) {
		this.gridImgX = gridImgX;
	}

	/**
	 * @return the gridImgY
	 */
	public int getGridImgY() {
		return gridImgY;
	}

	/**
	 * @param gridImgY the gridImgY to set
	 */
	public void setGridImgY(int gridImgY) {
		this.gridImgY = gridImgY;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the maserImg
	 */
	public BufferedImage getMaserImg() {
		return maserImg;
	}

	/**
	 * @param maserImg the maserImg to set
	 */
	public void setMaserImg(BufferedImage maserImg) {
		this.maserImg = maserImg;
	}

	/**
	 * @return the maserImgX
	 */
	public int getMaserImgX() {
		return maserImgX;
	}

	/**
	 * @param maserImgX the maserImgX to set
	 */
	public void setMaserImgX(int maserImgX) {
		this.maserImgX = maserImgX;
	}

	/**
	 * @return the maserImgY
	 */
	public int getMaserImgY() {
		return maserImgY;
	}

	/**
	 * @param maserImgY the maserImgY to set
	 */
	public void setMaserImgY(int maserImgY) {
		this.maserImgY = maserImgY;
	}

	/**
	 * @return the observationTextImg
	 */
	public BufferedImage getObservationTextImg() {
		return observationTextImg;
	}

	/**
	 * @param observationTextImg the observationTextImg to set
	 */
	public void setObservationTextImg(BufferedImage observationTextImg) {
		this.observationTextImg = observationTextImg;
	}

	/**
	 * @return the observationTextImgX
	 */
	public int getObservationTextImgX() {
		return observationTextImgX;
	}

	/**
	 * @param observationTextImgX the observationTextImgX to set
	 */
	public void setObservationTextImgX(int observationTextImgX) {
		this.observationTextImgX = observationTextImgX;
	}

	/**
	 * @return the observationTextImgY
	 */
	public int getObservationTextImgY() {
		return observationTextImgY;
	}

	/**
	 * @param observationTextImgY the observationTextImgY to set
	 */
	public void setObservationTextImgY(int observationTextImgY) {
		this.observationTextImgY = observationTextImgY;
	}

	/**
	 * @return the bgColor
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * @param bgColor the bgColor to set
	 */
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * @return the fgColor
	 */
	public Color getFgColor() {
		return fgColor;
	}

	/**
	 * @param fgColor the fgColor to set
	 */
	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
	}
	

}
