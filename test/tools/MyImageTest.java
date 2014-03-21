/**
 * 
 */
package tools;

import static common.TEST.DEFAULT_TIMEOUT;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.math.util.MathUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import view.MaserComponentsImage;

import java.util.Random;

public class MyImageTest {
	final static String LBR = System.getProperty("line.separator");

	final static String FS = System.getProperty("file.separator");

	final static String TMPDIR = System.getProperty("java.io.tmpdir");
	static File tmpDir = SystemUtils.getJavaIoTmpDir();

	Random random;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		random = new Random();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link tools.MyImage#readImage(java.lang.String)}.
	 * 
	 * @throws Exception
	 * @throws IOException
	 * TODO pick Image at random
	 */
	// @Ignore
	@Test(expected = IOException.class)
	// TODO java.lang.AssertionError: Expected exception: java.io.IOException
	public final void testReadImage() throws Exception {
		String imagePath = "test" + FS + "data" + FS + "images" + FS
				+ "testimage.png";

		// MUT
		BufferedImage testImg = null;
		testImg = MyImage.readImage(imagePath);

		// the Test
		assertTrue("Image-size >0 ?!", testImg.getHeight() > 0);

		// provoke IOException
		imagePath = "ReadNonsense";
		testImg = MyImage.readImage(imagePath);
	}

	/**
	 * Test method for
	 * {@link tools.MyImage#saveImage(java.awt.image.BufferedImage, java.lang.String)}.
	 * TODO pick Image at random
	 */
	// @Ignore
	@Test
	// (expected = IOException.class)
	public final void testSaveImage() throws Exception {
		String imagePath = "test" + FS + "data" + FS + "images" + FS
				+ "testimage.png";
		// imagePath = FS+"Users" + FS + "test" + FS + "Pictures" + FS+
		// "sesamstrasse.jpg";
		String savePath = TMPDIR + FS + "testimage.png";
		// MUT
		BufferedImage testImg = MyImage.readImage(imagePath);
		MyImage.saveImage(testImg, savePath);

		// the Test
		BufferedImage saveImg = MyImage.readImage(savePath);
		assertTrue("ImagesData equal?: ", MyImage.compareImagesRawData(saveImg, testImg));

		// cleanup
		ToolBox.removeTmpfile(savePath);

		// // provoke IOException TODO HOW ???
		imagePath = "nonsense";
		MyImage.saveImage(testImg, imagePath);

	}

	/**
	 * Test method for {@link tools.MyImage#getHue(java.awt.Color)}.
	 */
	// @Ignore
	@Test
	public final void testGetHue() throws Exception {

		int testRuns = 10000;
		// PointsOfIntrest
		int[][] poi = { { 250, 149, 130, 255 }, { 233, 244, 255, 255 },
				{ 255, 255, 255, 255 }, { 233, 244, 2, 2 } };// 1, 0, 359,
																// 360, 180
		for (int i = 0; i < poi.length + testRuns; i++) {
			int r = (i < poi.length) ? poi[i][0] : random.nextInt(255);
			int g = (i < poi.length) ? poi[i][1] : random.nextInt(255);
			int b = (i < poi.length) ? poi[i][2] : random.nextInt(255);
			int a = (i < poi.length) ? poi[i][3] : random.nextInt(255);
			Color color = new Color(r, g, b);
			// color = new Color(-152320318);
			// r=color.getRed();
			// g=color.getGreen();
			// b=color.getBlue();

			// expected
			float[] hsb = new float[3];
			hsb = color.RGBtoHSB(r, g, b, null);
			// System.out.println("Inputcolor"+color);
			// System.out.println("hsb"+Arrays.toString(hsb));
			double[] hsbD = new double[3];
			float[] rgbF = new float[4];
			rgbF = color.getRGBComponents(rgbF);
			hsbD[0] = (double) rgbF[0];
			hsbD[0] = (double) rgbF[1];
			hsbD[0] = (double) rgbF[2];
			double expectedHueD = MyImage.RGBToHSB(rgbF[0], rgbF[1], rgbF[2])[0];
			float expectedHueFloat = hsb[0];
			Long expectedHueL = Math.round(360 * expectedHueD) % 360;
			int expectedHue = expectedHueL.intValue();

			// MUT
			int returnHue = MyImage.getHue(color);

			// the Test
			assertEquals("Hue:", expectedHue, returnHue);
		}
	}

	/**
	 * Test method for {@link tools.MyImage#getHSB(java.awt.Color)}.
	 */
	// @Ignore
	@Test
	public final void testGetHSB() throws Exception {

		int testRuns = 10000;
		// PointsOfIntrest
		int[][] poi = { { 233, 244, 255, 255 }, { 255, 255, 255, 255 } };//
		for (int i = 0; i < poi.length + testRuns; i++) {
			int r = (i < poi.length) ? poi[i][0] : random.nextInt(255);
			int g = (i < poi.length) ? poi[i][1] : random.nextInt(255);
			int b = (i < poi.length) ? poi[i][2] : random.nextInt(255);
			int a = (i < poi.length) ? poi[i][3] : random.nextInt(255);
			Color color = new Color(r, g, b, a);
			// color = new Color(-152320318);
			// r=color.getRed();
			// g=color.getGreen();
			// b=color.getBlue();

			// expected
			float[] hsb = new float[3];
			hsb = color.RGBtoHSB(r, g, b, null);
			// System.out.println("color"+color);
			// System.out.println("hsb"+Arrays.toString(hsb));
			double[] hsbD = new double[3];
			float[] rgbF = new float[4];
			rgbF = color.getRGBComponents(rgbF);
			hsbD[0] = (double) rgbF[0];
			hsbD[0] = (double) rgbF[1];
			hsbD[0] = (double) rgbF[2];
			double expectedHueD = MyImage.RGBToHSB(rgbF[0], rgbF[1], rgbF[2])[0];
			float expectedHueFloat = hsb[0];
			Long expectedHueL = Math.round(360 * expectedHueD) % 360;
			int expectedHue = expectedHueL.intValue();

			double expectedSaturationD = MyImage.RGBToHSB(rgbF[0], rgbF[1],
					rgbF[2])[1];
			float expectedSaturationFloat = hsb[1];
			Long expectedSaturationL = Math.round(100 * expectedSaturationD) % 101;
			int expectedSaturation = expectedSaturationL.intValue();

			double expectedBrightnessD = MyImage.RGBToHSB(rgbF[0], rgbF[1],
					rgbF[2])[2];
			float expectedBrightnessFloat = hsb[2];
			Long expectedBrightnessL = Math.round(100 * expectedBrightnessD) % 101;
			int expectedBrightness = expectedBrightnessL.intValue();

			// MUT
			int returnHue = MyImage.getHue(color);
			int returnSaturation = MyImage.getSaturation(color);
			int returnBrightness = MyImage.getBrightness(color);

			// the Test
			assertEquals("Hue:", expectedHue, returnHue);
			assertEquals("Saturation:", expectedSaturation, returnSaturation);
			assertEquals("Brightness:", expectedBrightness, returnBrightness);
		}
	}

	/**
	 * Test method for {@link tools.MyImage#getHue(java.awt.Color)}.
	 */
	// @Ignore
	@Test
	public final void testSetSaturation() throws Exception {
		// TODO : :KLUDGE: Refactor for other Colors then GREEN
		int testRuns = 10000;
		// PointsOfIntrest
		int[][] poi = { { 14, 17, 39, 255 }, { 182, 20, 87, 255 },
				{ 255, 255, 255, 255 }, { 0, 0, 0, 0 }, { 1, 1, 1, 1 },
				{ 233, 244, 255, 255 }, { 0, 0, 255, 255 } };// 1, 0, 359,
																// 360, 180
		for (int i = 0; i < poi.length + testRuns; i++) {
			int r = (i < poi.length) ? poi[i][0] : random.nextInt(255);
			int g = (i < poi.length) ? poi[i][1] : random.nextInt(255);
			int b = (i < poi.length) ? poi[i][2] : random.nextInt(255);
			int a = (i < poi.length) ? poi[i][3] : random.nextInt(255);
			Color color = new Color(r, g, b, a);
			color = Color.GREEN;
			int testSaturation = random.nextInt(101);
			// System.out.println("Index: "+i);

			// expected
			float[] hsb = new float[3];
			hsb = color.RGBtoHSB(r, g, b, null);
			// System.out.println("Inputcolor"+color);
			// System.out.println("hsb"+Arrays.toString(hsb));

			int expectedHue = MyImage.getHue(color);
			int expectedBrightness = MyImage.getBrightness(color);
			int expectedSaturation = testSaturation;

			// MUT
			Color returnColor = MyImage.setSaturation(color, testSaturation);
			// System.out.println(returnColor+" :returnColor");
			int returnHue = MyImage.getHue(returnColor);
			int returnSaturation = MyImage.getSaturation(returnColor);
			int returnBrightness = MyImage.getBrightness(returnColor);
			if (returnSaturation == 0)
				expectedSaturation = 0;
			if (returnHue == 0)
				expectedHue = 0;
			if (returnBrightness == 0)
				expectedBrightness = 0;

			// the Test

			assertTrue("testSaturation " + testSaturation + " Hue :"
					+ " inputColor: " + color + " returnColor: " + returnColor
					+ " Delta: " + (expectedHue - returnHue), Math
					.abs(expectedHue - returnHue) < 1);
			assertTrue("testSaturation " + testSaturation + " Brightness: "
					+ " inputColor: " + color + " returnColor: " + returnColor
					+ " Delta: " + (expectedBrightness - returnBrightness),
					Math.abs(expectedBrightness - returnBrightness) < 1);
			assertTrue("testSaturation " + testSaturation + " Saturation: "
					+ " inputColor: " + color + " returnColor: " + returnColor
					+ " Delta: " + (expectedSaturation - returnSaturation),
					Math.abs(expectedSaturation - returnSaturation) < 1);
		}
	}

	/**
	 * Test method for
	 * {@link controller.ObservationImageBuilder#createRotatedStrImage(String, Font,Color,Color)}.
	 * 
	 * @throws IOException
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testCreateRotatedStrImage() throws IOException {

		String str = RandomStringUtils.randomAscii(random.nextInt(20) + 1);
		BufferedImage bi = new BufferedImage(1, 1,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = bi.createGraphics();
		Font font = g2d.getFont();

		FontMetrics fm = g2d.getFontMetrics();
		int strWidth = Double.valueOf(
				Math.ceil(fm.getStringBounds(str, g2d.create()).getWidth()))
				.intValue();
		int strHeight = Double.valueOf(
				Math.ceil(fm.getStringBounds(str, g2d.create()).getHeight()))
				.intValue();
		Color backgroundColor = Color.YELLOW;
		Color foregroundColor = Color.BLACK;
		// expected
		BufferedImage horizontal = new BufferedImage(strWidth, strHeight,
				BufferedImage.TYPE_4BYTE_ABGR);
		MyImage.setBackgroundColor(horizontal, backgroundColor);
		Graphics2D g2Dhorizontal = horizontal.createGraphics();
		g2Dhorizontal.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2Dhorizontal.setColor(foregroundColor);
		g2Dhorizontal.drawString(str, 1, fm.getMaxAscent() + 2);
		// MUT
		BufferedImage retImg = MyImage.createRotatedStrImage(str, font,
				backgroundColor, foregroundColor);
		String imgPath = TMPDIR + FS + "rotateString.png";
		MyImage.saveImage(retImg, imgPath);

		// the Test
		// rotatet 90¡? check by exchanged Width & Height
		assertEquals("ImgSize : ", strWidth, retImg.getHeight());
		assertEquals("ImgSize : ", strHeight, retImg.getWidth());
		// cleanup
		ToolBox.removeTmpfile(imgPath);
	}

	/**
	 * Test method for {@link tools.MyImage#colorCodeImg(int,int,int,int)}.
	 * 
	 * @throws Exception
	 * @throws IOException
	 *             TODO pick Image at random
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testColorCodeImg() throws Exception {
		String imagePath = TMPDIR + FS + "colorcode.png";

		int testRuns = 100;
		// PointsOfIntrest
		int[][] poi = {};
		for (int i = 0; i < poi.length + testRuns; i++) {
			// MUT
			BufferedImage retImg = null;
			retImg = MyImage.colorCodeImg();
			MyImage.saveImage(retImg, imagePath);

			// the Test
			int x = random.nextInt(retImg.getWidth());
			float hue = (500f - x) / 720;
			Color expectedColor = Color.getHSBColor(hue, 1f, 1f);
			Color retColor = new Color(retImg.getRGB(x, 0));
			// System.out.println("Colors: x expectedColor,retColor "
			// +x+expectedColor+" , "+
			// retColor.RGBtoHSB(retColor.getRed(), retColor.getGreen(),
			// retColor.getBlue(), new float[3])[0]*360);
			assertEquals("Image-Color ?!", expectedColor.getRGB(), retColor
					.getRGB());
		}
		// cleanup
		ToolBox.removeTmpfile(imagePath);
	}

	/**
	 * Test method for {@link controller.ObservationImageBuilder#rotateImg(BufferedImage, int)}.
	 * 
	 * @throws IOException
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT ,expected = IllegalArgumentException.class)
	public final void testRotateImg() throws IOException {
		int testRuns = 0;
		int angle;
		String imgPath = TMPDIR + FS + "rotatedImage.png";
		String imgPath1 = TMPDIR + FS + "rotateImage.png";
		// PointsOfIntrest
		int[] poi = { 90, 180, 270 };// 360};
		for (int i = 0; i < poi.length + testRuns; i++) {
			angle = poi[i];

			String str = RandomStringUtils.randomAscii(random.nextInt(20) + 10);
			BufferedImage bi = new BufferedImage(1, 1,
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2d = bi.createGraphics();
			Font font = g2d.getFont();

			FontMetrics fm = g2d.getFontMetrics();
			int strWidth = Double
					.valueOf(
							Math.ceil(fm.getStringBounds(str, g2d.create())
									.getWidth())).intValue();
			int strHeight = Double.valueOf(
					Math
							.ceil(fm.getStringBounds(str, g2d.create())
									.getHeight())).intValue();
			Color backgroundColor = Color.YELLOW;
			Color foregroundColor = Color.BLACK;
			// expected
			BufferedImage horizontal = new BufferedImage(strWidth, strHeight,
					BufferedImage.TYPE_4BYTE_ABGR);
			MyImage.setBackgroundColor(horizontal, backgroundColor);
			Graphics2D g2Dhorizontal = horizontal.createGraphics();
			g2Dhorizontal.setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2Dhorizontal.setColor(foregroundColor);
			g2Dhorizontal.drawString(str, 1, fm.getMaxAscent() + 2);

			MyImage.saveImage(horizontal, imgPath1);
			// MUT
			BufferedImage retImg = MyImage.rotateImg(horizontal, angle);

			MyImage.saveImage(retImg, imgPath);

			// the Tests
			// rotatet 90¡? check by exchanged Width & Height
			switch (angle) {
			case 90:
				// Width & Height exchanged
				assertEquals("Size pre & post: ", strWidth, retImg.getHeight());
				assertEquals("ImgSize : ", strHeight, retImg.getWidth());
				break;
			case 180:
				// Width & Height same
				assertEquals("Size pre & post: ", strWidth, retImg.getWidth());
				assertEquals("Size pre & post: : ", strHeight, retImg.getHeight());
				break;
			case 270:
//				 Width & Height exchanged
				assertEquals("Size pre & post: ", strWidth, retImg.getHeight());
				assertEquals("Size pre & post: : ", strHeight, retImg.getWidth());
				break;
			}
			//amount of colredPixel pre&post the same ?
			assertEquals("Size pre & post: ", MyImage.countColorPixel(horizontal, backgroundColor), MyImage.countColorPixel(retImg, backgroundColor));
			assertEquals("Size pre & post: ", MyImage.countColorPixel(horizontal, foregroundColor), MyImage.countColorPixel(retImg, foregroundColor));
			
			
		}
		// cleanup
		ToolBox.removeTmpfile(imgPath1);
		ToolBox.removeTmpfile(imgPath);
		// //provoke Exception
		angle = -7;
		MyImage.rotateImg(null, angle);
		

	}
	
	/**
	 * Test method for {@link tools.MyImage#compareImagesRawData(BufferedImage,BufferedImage)}.
	 * @throws Exception 
	 * 
	 *            
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testCompareImagesRawData() throws Exception  {
		String imagePath = CrossPlatform.path(TMPDIR +"/compare.png");

		int testRuns = 10;
		// PointsOfIntrest
		int[][] poi = {};
		for (int i = 0; i < poi.length + testRuns; i++) {
			// MUT
			BufferedImage expectedImg = MyImage.generatNoiseImage(random.nextInt(1000)+1, random.nextInt(1000)+1);
			BufferedImage retImg = MyImage.copyBufferedImage(expectedImg);
			MyImage.saveImage(retImg, imagePath);

			// the Test
			assertTrue("ImageData Compare",MyImage.compareImagesRawData(expectedImg, retImg));
		}
		// cleanup
		ToolBox.removeTmpfile(imagePath);
	}

	/**
	 * Test method for {@link controller.ObservationImageBuilder#generatNoiseImage(int, int)}.
	 * TODO : size vs. Memory vs. time, values per Pixel as Gauss from Parameter
	 * Execptions
	 * 
	 * @throws Exception
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// (expected = IllegalArgumentException.class)
	public final void testGenerateNoiseImage() throws Exception {
		// testParams
		int imgHeight = 600; // maxSize ca. 2750 at VM-Memory=??
		int imgWidth = 400;
		//MUT
		BufferedImage retImg = MyImage.generatNoiseImage(imgWidth,imgHeight);
		String imagePath=CrossPlatform.path(TMPDIR + "/noise.png");
		MyImage.saveImage(retImg,imagePath );
		//the Tests
		assertEquals("imgWidth:", imgWidth,retImg.getWidth());
		assertEquals("imgHeight:", imgHeight,retImg.getHeight());
		//Cleanup 
		ToolBox.removeTmpfile(imagePath);
	}
	
	/**
	 * Test method for {@link tools.MyImage#translateBi2Type(BufferedImage,int)}.
	 * 
	 * @throws Exception
	 * TODO pick Image at random
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	// TODO java.lang.AssertionError: Expected exception: java.io.IOException
	public final void testTranslateBi2Type() throws Exception {
		String imagePath = "test" + FS + "data" + FS + "images" + FS
				+ "testimage.png";

		// MUT
		int imgType;
		BufferedImage testImg= MyImage.readImage(imagePath);
		BufferedImage transImg;

		for (int i = BufferedImage.TYPE_INT_RGB; i <= BufferedImage.TYPE_BYTE_INDEXED; i++) {
			imgType = i;
			transImg = MyImage.translateBi2Type(testImg, imgType);
			// the Test
			assertEquals("Image-Type: ", imgType, transImg.getType());
			assertEquals("reverse Type-translation: ",testImg.getType(),
				MyImage.translateBi2Type(transImg, testImg.getType()).getType());
			assertTrue("reverse Type-translation: RawData-compare",
					MyImage.compareImagesRawData(testImg, 
						MyImage.translateBi2Type(transImg, testImg.getType())));
		}		
		// provoke IOException
		imgType = 0;
		transImg=MyImage.translateBi2Type(testImg,imgType);
	}
	
	/**
	 * Test method for
	 * {@link tools.MyImage#saveImage(java.awt.image.BufferedImage, java.lang.String)}.
	 * TODO pick Image at random
	 */
	// @Ignore
	@Test
	// (expected = IOException.class)
	public final void testPng2jpg() throws Exception {
		
		String savePath = FilenameUtils.separatorsToSystem(tmpDir
				.getCanonicalPath()
				+ "/"+ToolBox.getCurrentMethodName()+".png");
		String imagePath = "test" + FS + "data" + FS + "images" + FS
				+ "testimage.png";
		// MUT
		BufferedImage testImg = MyImage.readImage(imagePath);
//		MyImage.saveImage(testImg, savePath ,"jpg");
		MyImage.png2jpg(imagePath);
		// the Test
		BufferedImage saveImg = MyImage.readImage(imagePath);
		assertTrue("ImagesData equal?: ", MyImage.compareImagesRawData(saveImg, testImg));

		// cleanup
//		ToolBox.removeTmpfile(savePath);

//		// // provoke IOException TODO HOW ???
//		imagePath = "nonsense";
//		MyImage.saveImage(testImg, imagePath);

	}
	
	/**
	 * Test method for
	 * {@link tools.MyImage#saveImage(java.awt.image.BufferedImage, java.lang.String)}.
	 * TODO pick Image at random
	 */
	// @Ignore
	@Test
	// (expected = IOException.class)
	public final void testPngs2mov() throws Exception {
		//TODO generate good png testImages  drawMaserComponent(int size,Color color) {
		String savePath = FilenameUtils.separatorsToSystem(tmpDir
				.getCanonicalPath()+"/pngmovie/");
		File movieDir = new File(savePath);
		FileUtils.forceMkdir(movieDir);
		FileUtils.cleanDirectory(movieDir);
		DecimalFormat df = new DecimalFormat( "00" ); 
		
		for (int i = 0; i < 10; i++) {
			BufferedImage testImg = new BufferedImage(500,500,2);
			Graphics2D g2d = testImg.createGraphics();
			g2d.drawImage(MaserComponentsImage.drawMaserComponent((i+1)*5, Color.GREEN), 0, 0, null);
			MyImage.saveImage(testImg, movieDir.getAbsolutePath()+"/testPng"+df.format(i)+".png");
		}		
	
		String imagePath = "test" + FS + "data" + FS + "images" + FS
				+ "testimage.png";
		// MUT
		MyImage.pngs2mov(savePath);
//		MyImage.saveImage(testImg, savePath ,"jpg");
		MyImage.png2jpg(imagePath);
		// the Test
		File movFile = new File(savePath);
		assertTrue("mov exists ?: ", movFile.exists());

		// cleanup
		FileUtils.cleanDirectory(movieDir);


	}

}
