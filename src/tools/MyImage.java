/**
 * Some useful Image functions
 */
package tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.MediaLocator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.util.MathUtils;

import view.JpegImagesToMovie;

import controller.MaserJ;

/**
 */
public class MyImage {

	/**
	 * @param width,height,startHue,
	 *            endHue
	 * @return BufferedImage with ColorCode
	 */
	public static BufferedImage colorCodeImg() {
		// TODO : unitTests,exception
		int width = 500;
		int height = 50;
		//Color color = Color.getHSBColor(0f, 1f, 1f);
		BufferedImage colorCodeImg = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = colorCodeImg.createGraphics();
		// startHue-endHue
		for (int i = 0; i <= width; i++) {
			Color colorHue = Color.getHSBColor(new Float(i / 720f), 1f, 1f);
			// System.out.println("Color: "+colorHue);
			g2d.setColor(colorHue);
			// g2d.drawLine(1, i, height, i);
			g2d.drawLine(width - i, 0, width - i, height);
		}
		return colorCodeImg;
	}

	/**
	 * @param img1,img2 two images to compare      
	 * @return true if images PixelRaster-Values are equal
	 */
	public static boolean compareImagesRawData(BufferedImage img1,
			BufferedImage img2) {
		boolean verbose= true;
		int samplesSize = img1.getWidth() * img1.getHeight()
				* img1.getRaster().getNumDataElements();
		int[] img1Samples = img1.getRaster().getSamples(0, 0, img1.getWidth(),
				img1.getHeight(), 0, new int[samplesSize]);

		samplesSize = img2.getWidth() * img2.getHeight()
				* img2.getRaster().getNumDataElements();
		int[] img2Samples = img2.getRaster().getSamples(0, 0, img2.getWidth(),
				img2.getHeight(), 0, new int[samplesSize]);
		if (img1Samples.length != img2Samples.length){
			if (verbose) {
				System.out.println("Diff at ImgSamplesSize size1: "
						+ img1Samples.length+" ,size2: "+img2Samples.length);
			}
			return false;
		}
		if (verbose){
			for (int i = 0; i < samplesSize; i++) {
				if (img1Samples[i]!=img2Samples[i]) {
					System.out.println("Diff at ImgSamplesIndex: " +i+"value1: "+img1Samples[i]+"value2:"+img2Samples[i]);
				}
			}
		}
		return (Arrays.equals(img1Samples,img2Samples));
	}

	/**
	 * 
	 * @param Image to copy
	 * @return copied Image
	 */
	static BufferedImage copyBufferedImage(BufferedImage bi) {

		BufferedImage returnBImg = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
		Graphics2D g2d =returnBImg.createGraphics();
		g2d.drawImage(bi, 0, 0, null);
		return returnBImg;
	}

	/**
	 * 
	 * HelperMethod countColorPixels in Image
	 */
	public static int countColorPixel(BufferedImage bImg, Color c) {
		bImg=translateBi2Type(bImg, 1);
		int w = bImg.getWidth(), h = bImg.getHeight();
		int[] argbArray = new int[w * h];
		bImg.getRGB(0, 0, w, h, argbArray, 0, w);
		int colorCounter = 0;
		for (int i : argbArray) {
			if (i == c.getRGB())
				colorCounter++;
		}
		return colorCounter;
	}

	/**
	 * 
	 * HelperMethod calculateMinPixelOnDrawChar in Image
	 */
	public static double calculateMinPixelOnDrawChar(Font font) {
		int size=50;
		BufferedImage bi=new BufferedImage(size,size,2);
		Graphics2D g2d=bi.createGraphics();
		FontMetrics fm=g2d.getFontMetrics(font);
		Color strColor=Color.BLACK;
		int center=size/2;
		String str="x";
		int amount=256*10; //10*ascii
		double[] pixelVals=new double[amount];
		String allChars=RandomStringUtils.randomAscii(amount);
		
		for (int i = 0; i < pixelVals.length; i++) {
			g2d.setColor(strColor);
			g2d.drawString(str, center, center);
			pixelVals[i]=countColorPixel(bi, strColor);
		}		
		return StatUtils.min(pixelVals);
	}
	
	
	/**
	 * Create an BufferedImage of the string rotated 90 degrees to the left.
	 * 
	 * @param s
	 *            String to use in image
	 * @param font
	 *            Font to print label in
	 * @param backgroundColor
	 *            The background color of the Image
	 * @param foregroundColor
	 *            The foreground color of the Image
	 * @return The rotated image.
	 */
	public static BufferedImage createRotatedStrImage(String s, Font font,
			Color backgroundColor, Color foregroundColor) {
		int imageType = MaserJ.BIMG_TYPE;
		int angle = 90;
		BufferedImage tmp = new BufferedImage(1, 1, imageType);// to
																// getFontMetrics
		Graphics2D tmpG2D = tmp.createGraphics();
		FontMetrics fm = tmpG2D.getFontMetrics(font);
		int w = fm.stringWidth(s);
		int h = fm.getMaxDescent() + fm.getMaxAscent();

		BufferedImage horizontal = new BufferedImage(w, h, imageType);
		MyImage.setBackgroundColor(horizontal, backgroundColor);
		Graphics2D g2Dhorizontal = horizontal.createGraphics();
		g2Dhorizontal.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2Dhorizontal.setColor(foregroundColor);
		g2Dhorizontal.drawString(s, 1, fm.getMaxAscent() + 2);

		AffineTransform affineT = new AffineTransform();
		affineT.rotate(Math.toRadians(angle), 0, 0);
		affineT.translate(0, -h);

		BufferedImage vertical = new BufferedImage(h, w, imageType);
		MyImage.setBackgroundColor(vertical, backgroundColor);
		Graphics2D g2Dvertical = vertical.createGraphics();
		g2Dvertical.drawImage(horizontal, affineT, null);
		return vertical;
	}

	/**
	 * generate a NoiseImage(width,height) with radom RGB-Values
	 * 
	 * @param strColor
	 *            the strColor to set
	 */
	static BufferedImage generatNoiseImage(int width, int height) {
		Random random = new Random();
		random.setSeed(random.nextLong());
		BufferedImage noiseImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//int[] argbArray = new int[width * height];
		Color c = new Color(255, 255, 153, 235);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				c = new Color(random.nextInt(255), random.nextInt(255), random
						.nextInt(255), 255);
				noiseImg.setRGB(i, j, c.getRGB());
			}
		}
		return noiseImg;
	}

	/**
	 * @param color
	 * @return Brightness from HSB-Color-Modell as %
	 */
	public static int getBrightness(Color color) {
		return getHSBbyKey(color, 'b');
	}

	/**
	 * @param color
	 * @return Hue,Saturation,Brightness from HSB-Color-Modell as int[¡,%,%]
	 */
	public static int[] getHSB(Color color) {
		int[] hsb = new int[3];
		hsb[0] = getHue(color);
		hsb[1] = getSaturation(color);
		hsb[2] = getBrightness(color);
		return hsb;
	}

	/**
	 * @param color
	 * @return
	 * @return Hue,Saturation,Brightness bey key from HSB-Color-Modell as
	 *         int[¡,%,%]
	 */
	public static int getHSBbyKey(Color color, char key) {
		double[] hsb = new double[3];
		float[] rgbF = new float[4];
		rgbF = color.getRGBComponents(rgbF);
		for (int i = 0; i < hsb.length; i++) {
			hsb[i] = (double) rgbF[i];
		}
		hsb = RGBToHSB(hsb[0], hsb[1], hsb[2]);
		Double retValueD = 0.0;
		int scale = 100;
		int i = 0;
		int modulo = 101;
		switch (key) {
		case 'h': // Hue
			scale = 360;
			modulo = 360;
			break;
		case 's':
			i = 1;
			break;
		case 'b':
			i = 2;
			break;
		}

		retValueD = MathUtils.round((scale * hsb[i]), 0);
		int retValue = retValueD.intValue() % modulo;
		return retValue;
	}

	/**
	 * @param color
	 * @return Hue from HSB-Color-Modell as Degrees
	 */
	public static int getHue(Color color) {
		return getHSBbyKey(color, 'h');
	}

	/**
	 * @param color
	 * @return Saturation from HSB-Color-Modell as %
	 */
	public static int getSaturation(Color color) {
		return getHSBbyKey(color, 's');
	}

	/**
	 * @param imagePath
	 * @return image
	 * @throws IOException if <code>imagePath</code> not accessible

	 */
	public static BufferedImage readImage(String imagePath) throws IOException {
		return ImageIO.read(new File(imagePath));
	}

	// Based on C Code in "Computer Graphics -- Principles and Practice,"
	// Foley et al, 1996, p. 594.
	public static double[] RGBToHSB(double r, double g, double b) {
		double[] result = new double[3];
		double delta, min;

		min = Math.min(r, Math.min(g, b));
		result[2] = Math.max(r, Math.max(g, b));
		delta = result[2] - min;

		// Calculate saturation: saturation is 0 if r, g and b are all 0
		if (result[2] == 0.0)
			result[1] = 0;
		else
			result[1] = delta / result[2];

		if (result[1] == 0)
			result[0] = 0; // Achromatic
		else {
			if (r == result[2]) { // winkel zw. gelb/magenta
				result[0] = (g - b) / delta * (2 * Math.PI / 6.0);
			} else if (g == result[2]) { // zwischen cyan und gelb
				result[0] = (2 + (b - r) / delta) * (2 * Math.PI / 6.0);
			} else if (b == result[2]) {// ...
				result[0] = (4 + (r - g) / delta) * (2 * Math.PI / 6.0);
			}
			if (result[0] < 0)
				result[0] = result[0] + 2 * Math.PI;
		}
		result[0] = result[0] / (2 * Math.PI);
		return result;
	}

	/**
	 * rotate Image orthogonal
	 * 
	 * @param BufferedImage
	 *            to rotate
	 * @param angle
	 *            to rotate clockwise
	 * @return via agnle clockwise orthogonal rotated BufferedImage
	 */
	public static BufferedImage rotateImg(BufferedImage Img, int angle) {
		// TODO : all & Test
		if (angle % 90 != 0 || (angle < 0 && angle > 360)) {
			throw new IllegalArgumentException(
					"Only multiple to 90¡ between 0 - 360 allowed !");
		}

		int rotWidth = Img.getWidth();
		int rotHeight = Img.getHeight();
		if (angle == 90 || angle == 270) { // exchange Dimensions
			rotWidth = rotHeight;
			rotHeight = Img.getWidth();
		}

		BufferedImage rotatedImg = new BufferedImage(rotWidth, rotHeight,
				Img.getType());
		// MyImage.setBackgroundColor(rotatedImg, Color.BLUE);
		Graphics2D g2d = rotatedImg.createGraphics();
		AffineTransform affineT = new AffineTransform();
		affineT.rotate(Math.toRadians(angle));
		// translate to correct position
		switch (angle) {
		case 90:
			affineT.translate(0, -rotWidth);
			break;
		case 180:
			affineT.translate(-Img.getWidth(), -Img.getHeight());
			break;
		case 270:
			affineT.translate(-Img.getWidth(), 0);
			break;
		}
		g2d.drawImage(Img, affineT, null);
		return rotatedImg;
	}

	/**
	 * @param image
	 *            to save, Path to save to
	 * @return image
	 * @throws IOException
	 */
	public static boolean saveImage(BufferedImage bImg, String imagePath)
			throws IOException {
		File f = new File(imagePath);
		return ImageIO.write(bImg, "png", f);
	}

	/**
	 * @param image to save, 
	 * @param imagePath to save to
	 * @param formatName
	 * @return image
	 * @throws IOException
	 */
	public static boolean saveImage(BufferedImage bImg, String imagePath, String formatName)
			throws IOException {
		File f = new File(imagePath);
		return ImageIO.write(bImg, formatName, f);
	}
	
	/**
	 * 
	 * @param bi
	 * @param g2d
	 * @return BufferedImage with set completely to backgroundColor
	 */
	public static BufferedImage setBackgroundColor(BufferedImage bi,
			Color backgroundColor) {
		Graphics2D g2D = bi.createGraphics();
		g2D.setBackground(backgroundColor);
		g2D.clearRect(0, 0, bi.getWidth(), bi.getHeight());
		return bi;
	}

	/**
	 * @param color,hue
	 * @return Hue from HSB-Color-Modell as Degrees
	 */
	public static Color setSaturation(Color color, int saturation) {
		// TODO : :KLUDGE: Refactor for other Colors then GREEN
		int[] hsb = new int[3];
		float[] rgb = color.getRGBColorComponents(null);
		hsb = getHSB(color);
		float saturationF = saturation / 100f;
		// System.out.println("saturationF: " + saturationF + " 100*float: " +
		// 100
		// * saturationF);
		Float h = new Float((getHue(color) / 360f) % 360f);
		Float b = new Float((getBrightness(color) / 100f) % 101f);

		Color  retColor = color.getHSBColor(h, saturationF, b);

		return retColor;
	}
	
	/**
	 * 
	 * @param bi the BufferedImage to translate
	 * @param imgType the Destination-Type
	 * @return BufferedImage with imgType
	 */
	public static BufferedImage translateBi2Type(BufferedImage bi,
			int imgType) throws IllegalArgumentException {
		if (imgType<BufferedImage.TYPE_INT_RGB || 
				imgType >BufferedImage.TYPE_BYTE_INDEXED)
			throw new IllegalArgumentException("no such BufferedImage ImageType: "+imgType);
		int w = bi.getWidth(null);
        int h = bi.getHeight(null);
        BufferedImage bi2 = new BufferedImage(w, h, imgType);
        if (bi.getType() != imgType) {
            Graphics big = bi2.getGraphics();
            big.drawImage(bi, 0, 0, null);
        }
		return bi2;
	}
	
	/**
	 * generate an image of an axis of cartesian coordinates
	 * 
	 * @param rulerLength
	 * @param unit
	 *            distance of Ruler lines
	 * @param start
	 *            scale-lower Bound
	 * @param end
	 *            scale-upper Bound
	 * @param largeScale
	 *            size of long Ruler lines
	 * @param smallScale
	 *            size of short Ruler lines
	 * @param caption
	 *            axis Text
	 * @param orientation
	 *            axis as X=Landscape or Y=Portrait
	 * @param font
	 *            of Caption
	 * @return BufferedImage of  axis
	 */
	public static BufferedImage cartesianAxis(int rulerLength, int unit, int start, int end,
			int largeScale, int smallScale, String caption, String orientation,
			Font font, Color foreGroundColor, Color backGroundColor,
			float scaleUnitFactor) {
		// TODO caption YES/NO, pos, top,bottom,left,right,cleanup, TESTS, Y
		// range & unitStr to diffrent scaleCaption not Pixels!
		int imageType=BufferedImage.TYPE_4BYTE_ABGR;
		BufferedImage tmp = new BufferedImage(1, 1, imageType);// to
		// getFontMetrics
		Graphics2D tmpG2D = tmp.createGraphics();
		FontMetrics fm = tmpG2D.getFontMetrics(font);
		int width = rulerLength;
		int height = fm.getHeight() * 2 + largeScale + 4;
		BufferedImage rulerImg = new BufferedImage(width, height, imageType);
		if (backGroundColor != null)
			MyImage.setBackgroundColor(rulerImg, backGroundColor);
		Graphics2D rulerG2D = rulerImg.createGraphics();
		rulerG2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	
		rulerG2D.setColor(foreGroundColor);
		int line = smallScale;
		int longIndicator = 50;
		String scaleStr = "";
	
		for (int i = start + unit; i < end; i += unit) {
			int x = i + rulerLength / 2;
			// otherscale ?i*
			if ((i % longIndicator) == 0) {
				line = largeScale;
	
				Integer pos = new Float(i * scaleUnitFactor).intValue();
				scaleStr = pos.toString();
				rulerG2D.drawString(scaleStr, x - fm.stringWidth(scaleStr) / 2,
						fm.getMaxAscent() + line + 2);
			} else
				line = smallScale;
			rulerG2D.drawLine(x, 0, x, line);
		}
		rulerG2D.drawString(caption,
				(rulerLength - fm.stringWidth(caption)) / 2, height - 2);
	
		return rulerImg;
	}
	
	/**
	 * convert an png Image from to jpg, replaces transperency with white
	 * @param imagePath
	 * @throws IOException 
	 */
	public static void png2jpg(String imagePath) throws IOException {
		if (!FilenameUtils.isExtension(imagePath, "png")) 
			throw new IllegalArgumentException("input has to be png! "+imagePath);
		BufferedImage bi1=readImage(imagePath);
		
		//BufferedImage bi1 = ImageIO.read(url);
        int w = bi1.getWidth();
        int h = bi1.getHeight();
        BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi2.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,w,h);
        g.drawRenderedImage(bi1, null);
      //  g.dispose();
		
		
		String formatName="jpg";
		imagePath=FilenameUtils.removeExtension(imagePath)+".jpg";
		saveImage( bi2,imagePath,  formatName);
	}
	
	/**
	 * convert an png Images to jpgs, and create QuicktimeMovie from jpgs
	 * @param imagePath to pngs
	 * @throws IOException 
	 */
	public static void pngs2mov(String imagePath) throws IOException {
	
//		TODO size,from images, framerate as param
		//list all pngs in movDir 
		File directory =new File(imagePath);
		Vector<String> inputFiles=new Vector();
		Collection<File> pngFiles = FileUtils.listFiles(directory,new String[] {"png"},false);
		for (Iterator iter = pngFiles.iterator(); iter.hasNext();) {
			File pngPath = (File) iter.next();
			png2jpg(pngPath.getAbsolutePath());
			inputFiles.add(FilenameUtils.removeExtension(pngPath.getAbsolutePath())+".jpg");
		}
		
		int width=800;
		int height=600;
		int frameRate=10;
		String outPutPath="file:/"+imagePath+"/pngs2mov.mov";
//		
		String omlURL =outPutPath;//output url
		MediaLocator oml;
		JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
		if ((oml = imageToMovie.createMediaLocator(omlURL)) == null) {
			System.err.println("Cannot build media locator from: " + omlURL);
			System.exit(0);
		}
		imageToMovie.doIt(width, height, frameRate, inputFiles, oml);
	}
}
