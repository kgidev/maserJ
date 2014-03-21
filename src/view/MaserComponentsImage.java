/**
 * 
 */
package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;

import tools.MyMath;
import tools.ToolBox;

import model.MaserComponent;
import model.DBTable;

import controller.MaserJ;

/**
 */
public class MaserComponentsImage extends BufferedImage {
	static int width=500;
	static int height=500;
	private Graphics2D g2d;
	private static int imageType=MaserJ.BIMG_TYPE;
	Color bgColor=Color.BLACK;
	protected Color textColor=Color.WHITE;//new Color(183,215,250);	//light-blue
	protected FontMetrics fontMetrics;
	double[] xRange = {-250,250}; //range of X-Offset from Model
	double[] yRange = {-250,250}; //range of Y-Offset from Model
	double[] intensityRange = {0.0,110.0}; 	//range of Maser-Intensitys 
	double[] velocityRange = {-5.0,5.0}; 		//range of Maser-velocitys
	int[] dopplerColorRange = {0,240};
	MaserComponent[] maserComponents;
	protected boolean interpolated=false;
	
	/**
	 * empty Constuctor 
	 */
	public MaserComponentsImage() {
		super(width, height, imageType);
	}
	
	/**
	 * @param width
	 * @param height
	 */
	public MaserComponentsImage(int width, int height,MaserComponent[] components) {
		super(width, height, imageType);
		g2d=this.createGraphics();
		this.maserComponents=components;
		compose();
		// TODO Auto-generated constructor stub
	}

	/**
	 *  Constuctor 
	 */
	public MaserComponentsImage(int width, int height,MaserComponent[] components,
			boolean interpolated) {
		super(width, height, imageType);
		g2d=this.createGraphics();
		this.maserComponents=components;
		this.interpolated=interpolated;
		compose();
		
	}

	/**
	 * compose this Image from the parts
	 */
	public void compose() {
		g2d.setBackground(bgColor);
		g2d.clearRect(0, 0, this.getWidth(), getHeight());
		//sort by velocity bzw. dopplerColor descending
		maserComponents =sortComponentsByVelocity(maserComponents);
		//loop over all components 
		for (int i = 0; i < maserComponents.length; i++) {
			int[] xy= position(maserComponents[i].getXOffset(),maserComponents[i].getYOffset());
			//TODO draw with center of CompImage as Pos
			BufferedImage compImage = createComponent(maserComponents[i]);
			g2d.drawImage(compImage, xy[0]-compImage.getWidth()/2,
					xy[1]-compImage.getHeight()/2, null);
		}
		if (interpolated) {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Font font=g2d.getFont();
			Font fontBold=font.deriveFont(Font.BOLD,14);
			g2d.setFont(fontBold);
			g2d.setColor(textColor);
			fontMetrics=g2d.getFontMetrics();
			String str="INTERPOLATED DATA";
			g2d.drawString(str, width-fontMetrics.stringWidth(str+"  "),fontMetrics.getMaxAscent()+6);
		}	
	}
	
	/**
	 * 
	 */
	public BufferedImage createComponent(MaserComponent maserComponent) {
		int compSize=size(maserComponent.getIntensity());
		int sizeX=compSize;
		int sizeY=compSize;
//		System.out.println(ToolBox.getCurrentMethodName()+" compSize: "+compSize);
		BufferedImage createComponent=new BufferedImage(1,1,imageType);
		Color compColor=dopplerColor(maserComponent.getVelocity());
		if (compSize>0) {
			createComponent=new BufferedImage(sizeX,sizeY,imageType);
			Graphics2D compG2d = createComponent.createGraphics();
			compG2d.drawImage(drawMaserComponent(compSize, compColor), 0, 0,
					null);
		}		
		return createComponent;
	}

	/**
	 * size calculated from logarithm of component-intensity
	 * ?? max & min 
	 * @param intensity
	 * @return size in pixel with in the Image dimensions
	 */
	public int size(double intensity) {
		//min =1px , max=ca. 1/10 imgSize
		// d = 20 log S + 26 
		int pixelSize=0;
		double scaleFactor=20;//this.getWidth()*(0.05);
		Double size= (Math.log10(intensity)*scaleFactor)+26;
		
		pixelSize= Math.round(size.longValue());
		return pixelSize;
	}
	
	/**
	 * pos calculated relative to imageSize and center coordinates
	 * 
	 * @param xOffset, yOffset
	 * @return xy position in this Image
	 * 
	 */
	public int[] position(double x,double y) {
		int[] position=new int[2];
		int centerX=this.getWidth()/2;
		int centerY=this.getHeight()/2;
		double scaleX =2*centerX/Math.abs(xRange[1]-xRange[0]);
		position[0]=(int) (x*scaleX)+centerX-1;
		double scaleY =2*centerY/Math.abs(yRange[1]-yRange[0]);
//		device-coordinates are upsidedown
		position[1]=(int) (centerY-(y*scaleY));
		return position;
	}
	
	
	
	/**
	 * @param velocity
	 * @return the dopplerColor  relativ to velocity-Range
	 */
	public Color dopplerColor(double  velocity) {
//		deltaVelocity from Veloctiy-Range
		double dV = Math.abs(getVelocityRange()[1]-getVelocityRange()[0]);
//		deltaHue from Hue-Range
		double dHue = Math.abs(getDopplerColorRange()[1]-getDopplerColorRange()[0]);
		double scale =dHue/dV;
		//Hue offset from zero Velocity
		double hueOffset = dHue*0.5f;
		//linear transform 
		double hue = (scale*velocity+hueOffset)/dHue;
		return Color.getHSBColor((float)hue, 1f, 1f);
	}
	

	
	/**
	 * draw a MaserImg by calculating the 1. quadrant and mirror it on x&y-Axis
	 * 
	 * @param color
	 * @param size
	 * @return image
	 */

	static public BufferedImage drawMaserComponent(int size,Color color) {
		int xDim = size / 2;
		int yDim = size / 2;
		Color transpColor = new Color(color.getRGB());
		int saturation = 0;
		BufferedImage maserImg = new BufferedImage(size, size, imageType);

		// small sizes have special cases
		switch (size) {
		case 1:
			saturation = new Float(0.8f * 256f).intValue();
			transpColor = new Color(color.getRed(), color.getGreen(), color
					.getBlue(), saturation);
			maserImg.setRGB(0, 0, transpColor.getRGB());

			break;
		case 2:
			saturation = new Float(0.8f * 256f).intValue();
			transpColor = new Color(color.getRed(), color.getGreen(), color
					.getBlue(), saturation);
			maserImg.setRGB(0, 0, transpColor.getRGB());
			maserImg.setRGB(1, 0, transpColor.getRGB());
			maserImg.setRGB(0, 1, transpColor.getRGB());
			maserImg.setRGB(1, 1, transpColor.getRGB());
			break;
		case 3:
			transpColor = color;
			maserImg.setRGB(1, 1, transpColor.getRGB());

			break;
		// size > 3 pixels
		default:
			double[][] gauss2DVals = MyMath.gauss2DTable(size / 2);
			// max Value from gauss2DVals * scale <=1,
			// ergo median has always saturation close to 1
			Double max2DGauss = (Double) MyMath.array2DMax(MyMath
					.array2DCast2Objects(gauss2DVals));
			float zScale = 1 / max2DGauss.floatValue();
			// 1.Quadrant of 2D-Gauss
			// TODO Transparancy
			BufferedImage img1quad = new BufferedImage(xDim, yDim, imageType);
			for (int i = 0; i < xDim; i++) {
				for (int j = 0; j < xDim; j++) {
					saturation = new Float(gauss2DVals[i][j] * 255f * zScale)
							.intValue();
					// Color transpColor= MyImage.setSaturation(color,
					// saturation);
					transpColor = new Color(color.getRed(), color.getGreen(),
							color.getBlue(), saturation);
					img1quad.setRGB(i, j, transpColor.getRGB());
				}
			}
			AffineTransform affineT = new AffineTransform();
			Graphics2D g2d = maserImg.createGraphics();
			// g2d.setBackground(Color.RED);
			// g2d.clearRect(0, 0, size, size);
			// 1. Quadrant
			affineT.rotate(Math.toRadians(180), xDim, yDim);
			affineT.translate(xDim, yDim);
			g2d.drawImage(img1quad, affineT, null);
			// 2. Quadrant
			affineT = new AffineTransform();
			affineT.rotate(Math.toRadians(270), xDim, 0);
			affineT.translate(0, 0);
			g2d.drawImage(img1quad, affineT, null);
			// 3. Quadrant
			affineT = new AffineTransform();
			affineT.rotate(Math.toRadians(0), xDim, yDim);
			affineT.translate(xDim, yDim);
			g2d.drawImage(img1quad, affineT, null);
			// 4. Quadrant
			affineT = new AffineTransform();
			affineT.rotate(Math.toRadians(90), xDim, yDim);
			affineT.translate(xDim, yDim);
			g2d.drawImage(img1quad, affineT, null);
			break;
		}
		return maserImg;
	}

	/**
	 * @return the intensityRange
	 */
	public double[] getIntensityRange() {
		return intensityRange;
	}

	/**
	 * @param intensityRange the intensityRange to set
	 */
	public void setIntensityRange(double[] intensityRange) {
		this.intensityRange = intensityRange;
	}

	/**
	 * @return the velocityRange
	 */
	public double[] getVelocityRange() {
		return velocityRange;
	}

	/**
	 * @param velocityRange the velocityRange to set
	 */
	public void setVelocityRange(double[] velocityRange) {
		this.velocityRange = velocityRange;
	}

	/**
	 * @return the dopplerColorRange
	 */
	public int[] getDopplerColorRange() {
		return dopplerColorRange;
	}

	/**
	 * @param dopplerColorRange the dopplerColorRange to set
	 */
	public void setDopplerColorRange(int[] dopplerColorRange) {
		this.dopplerColorRange = dopplerColorRange;
	}
	/**
	 * sort ComponentsArray by Velocity
	 * @param comps
	 * @return sortedComps, sorted ComponentsArray by Velocity
	 */
	public static MaserComponent[] sortComponentsByVelocity(MaserComponent[] comps) {
		
		ArrayList<MaserComponent> compList = new ArrayList<MaserComponent>(Arrays.asList(comps));
		ArrayList<MaserComponent> sortedList =  new ArrayList<MaserComponent>(comps.length);
		compList.trimToSize();

//		try {
//			DBTable compTable=new DBTable("COMPONENTS");
//			HashMap<String, Object> componentData = 
//				new Component(compTable.getTableStructAsContainerMap()).getDataMap();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Double[] values = new Double[comps.length];
		for (int i = 0; i < comps.length; i++) {
			values[i]=comps[i].getVelocity();
		}
		
		Double[] preSortvalues = values.clone();
		Arrays.sort(values);	// and objects?!
		
		for (int i = 0; i < values.length; i++) {
			int sortIndex= ArrayUtils.indexOf(preSortvalues,values[i]);
			preSortvalues = (Double[]) ArrayUtils.remove(preSortvalues, sortIndex);
			sortedList.add(compList.get(sortIndex));
			compList.remove(sortIndex);
		//		ArrayUtils.indexOf(preSortvalues, );
			
		}			
		return sortedList.toArray(comps);
	}

}
