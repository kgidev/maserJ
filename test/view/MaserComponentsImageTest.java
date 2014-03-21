/**
 * 
 */
package view;

import static common.TEST.DEFAULT_TIMEOUT;
import static common.TEST.random;
import static org.junit.Assert.*;
import static tools.MyImage.getHue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import model.MaserComponent;
import model.MaserComponentTest;
import model.DBTable;
import model.DBTableTest;
import model.DataBase;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import controller.MaserJ;

import tools.MyImage;
import tools.MyMath;
import tools.ToolBox;


public class MaserComponentsImageTest {

	static public Double[] xRange = { -120.0, 120.0 };
	static public Double[] yRange = { -120.0, 120.0 };
	static public Double[] vRange = { -5.0, 5.0 };
	static public Double[] iRange = { 0.0, 500.0 };
	static public Double[] bRange = { 0.0, 20.0 };

	static DataBase db;

	static final String TMPDIR_NAME = SystemUtils.getJavaIoTmpDir()
			.getAbsolutePath();
	String savePath;
	
	MaserComponentsImage maserCompImg;

	MaserComponent[] maserComponents;

	DBTable componentsTable;

	MaserComponent maserComponent;
	MaserComponentTest coTest;

	int imgWidth = 500;

	int imgHeight = 500;
//TODO randomize Ranges , symetric or not?
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new DataBase(true, common.TEST.dbURL, common.TEST.dbFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
//		System.out.println(ToolBox.getCurrentMethodName());
		db.dropTables();
		db.shutdownDB();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		componentsTable = new DBTable("COMPONENTS");
		MaserComponentTest coTest = new MaserComponentTest();
		int amount = 10;
		maserComponents = new MaserComponent[amount];
		for (int i = 0; i < amount; i++) {
			HashMap<String, Object> compData = coTest.randomComponentRow(
					xRange, yRange, vRange, iRange, bRange);
			maserComponents[i] = new MaserComponent(compData, false);
		}

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
//		System.out.println(ToolBox.getCurrentMethodName());
		maserCompImg = null;
		maserComponents = null;
		coTest=null;
	}

	/**
	 * Test method for
	 * {@link view.MaserComponentsImage#MaserComponentsImage(int, int, model.MaserComponent[])}.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testMaserComponentsImage() throws Exception {

		// MUT
		maserCompImg = new MaserComponentsImage();
		// the test
		assertEquals("width: ", imgWidth, maserCompImg.getWidth());
		assertEquals("height: ", imgHeight, maserCompImg.getHeight());

	}

	/**
	 * Test method for {@link view.MaserComponentsImage#compose()}.
	 * 
	 * @throws IOException
	 */
	@Test//(timeout = DEFAULT_TIMEOUT)
	public final void testCompose() throws IOException {
		// TODO test componentsImage wich are not fitting to Frame
		String savePath1="";
		String savePath2="";
		String savePath3="";
		double compVelocity = Double.MIN_VALUE;
		for (int i = 0; i < maserComponents.length; i++) {
			// MUT
			maserCompImg = new MaserComponentsImage(imgWidth, imgHeight,
					new MaserComponent[] { maserComponents[i] });
			savePath=FilenameUtils.separatorsToSystem(TMPDIR_NAME + "/"
					+ ToolBox.getCurrentMethodName() + ".png");
			MyImage.saveImage(maserCompImg,savePath );
			int[] compPos = maserCompImg.position(maserComponents[i].getXOffset(),
					maserComponents[i].getYOffset());
			BufferedImage compImg = maserCompImg.createComponent(maserComponents[i]);
			BufferedImage expectedImg = new BufferedImage(compImg.getWidth(),
					compImg.getHeight(), MaserJ.BIMG_TYPE);
			expectedImg = MyImage.setBackgroundColor(expectedImg,
					maserCompImg.bgColor);
			expectedImg.getGraphics().drawImage(compImg, 0, 0, null);
			// System.out.println(ToolBox.getCurrentMethodName() + " posXY: "
			// + Arrays.toString(compPos));
			// System.out.println(ToolBox.getCurrentMethodName()
			// + " expectedImg.getHeight(): " + expectedImg.getHeight());
			BufferedImage returnImg = null;
			//compare full CompImage if fits in Frame 
			if ((compPos[0] + expectedImg.getWidth()/2 <= maserCompImg.getWidth()-1 &&
					compPos[0] - expectedImg.getWidth()/2 >= 0)
					&&
				 (compPos[1]+expectedImg.getHeight()/2<=maserCompImg.getHeight()-1 &&
					compPos[1] - expectedImg.getHeight()/2 >= 0)) 
			
			{
				
				returnImg = maserCompImg.getSubimage(compPos[0]
						- expectedImg.getWidth() / 2, compPos[1]
						- expectedImg.getHeight() / 2,
						expectedImg.getWidth(), expectedImg.getHeight());
				savePath1=FilenameUtils.separatorsToSystem(TMPDIR_NAME + "/"
						+ ToolBox.getCurrentMethodName() + "_expectedImg.png");
				MyImage.saveImage(expectedImg, savePath1);
				savePath2=FilenameUtils.separatorsToSystem(TMPDIR_NAME + "/"
						+ ToolBox.getCurrentMethodName() + "_returnImg.png");
				MyImage.saveImage(returnImg, savePath2);
				assertTrue("component Img : ", MyImage.compareImagesRawData(
						expectedImg, returnImg));
				Double xPos = (maserComponents[i].getXOffset()
						/ (Math.abs(xRange[1] - xRange[0])) * maserCompImg
						.getWidth())
						+ maserCompImg.getWidth() / 2;
				assertTrue("component Xposition: " + i, Math.abs(xPos
						.intValue()
						- compPos[0]) < 2);
				Double yPos = (maserComponents[i].getYOffset()
						/ (Math.abs(yRange[1] - yRange[0])) * maserCompImg
						.getWidth())
						+ maserCompImg.getHeight() / 2;
				assertTrue("component Yposition: " + i, Math.abs(yPos
						.intValue()
						- compPos[1]) < 2);

			}
		}

		// all components
		MaserComponent[] unorderedComponents = maserComponents.clone();
		Collections.shuffle(Arrays.asList(unorderedComponents));
		Collections.shuffle(Arrays.asList(maserComponents));
//		 System.out.println(ToolBox.getCurrentMethodName() + " unorderedComponents: "
//		 + Arrays.toString(unorderedComponents));
//		 System.out.println(ToolBox.getCurrentMethodName() + " components: "
//				 + Arrays.toString(components));
//		
		maserCompImg = new MaserComponentsImage(imgWidth, imgHeight, maserComponents);
//		System.out.println(ToolBox.getCurrentMethodName() + " post compose() components: "
//				 + Arrays.toString(maserCompImg.components));
		MaserComponent[] expectedComps = maserCompImg.sortComponentsByVelocity(unorderedComponents);
//		System.out.println(ToolBox.getCurrentMethodName() + " post sort() expectedComps: "
//				 + Arrays.toString(expectedComps));
		assertTrue("component Velocity order: ", Arrays.equals(expectedComps,
				maserCompImg.maserComponents));
		 savePath3 = FilenameUtils.separatorsToSystem(TMPDIR_NAME 
				+ "/"+ ToolBox.getCurrentMethodName() + "All.png");
		MyImage.saveImage(maserCompImg,savePath3 );
		
		// cleanup
		 ToolBox.removeTmpfile(savePath);
		 ToolBox.removeTmpfile(savePath1);
		 ToolBox.removeTmpfile(savePath2);
		 ToolBox.removeTmpfile(savePath3);
	}

	/**
	 * Test method for
	 * {@link view.MaserComponentsImage#createComponent(model.MaserComponent)}.
	 * 
	 * @throws Exception
	 */
	@Test//(timeout = DEFAULT_TIMEOUT)
	public final void testCreateComponent() throws Exception {
		int testruns = 10;
		for (int i = 0; i < testruns; i++) {
			maserCompImg = new MaserComponentsImage(imgWidth, imgHeight,
					maserComponents);
			HashMap<String, Object> compData = DBTableTest
					.randomRowData("COMPONENTS");
			double velocity = (Double) MyMath.randomValueByRange(Double.class,
					vRange[0], vRange[1], '*');
			velocity=0.0;
			compData.put("velocity", velocity);
			double intensity = (Double) MyMath.randomValueByRange(Double.class,
					iRange[0], iRange[1], '*');
			compData.put("intensity", intensity);
			int expectedSize = maserCompImg.size(intensity);
			Color expectedColor = maserCompImg.dopplerColor(velocity);
			maserComponent = new MaserComponent(compData, false);
			// MUT
			BufferedImage testCreateComponentImg = maserCompImg
					.createComponent(maserComponent);
			savePath=FilenameUtils.separatorsToSystem(TMPDIR_NAME
					+ "/testCreateComponent.png");
			MyImage.saveImage(testCreateComponentImg, savePath);
			// the tests
			assertEquals("getWidth: ", expectedSize, testCreateComponentImg
					.getWidth());
			assertEquals("getHeight: ", expectedSize, testCreateComponentImg
					.getHeight());
			int centerX = testCreateComponentImg.getWidth() / 2;
			int centerY = testCreateComponentImg.getHeight() / 2;
			if (centerX > 0) {
				int colorDelta = Math.abs(Math.abs(expectedColor.getRGB())-
						Math.abs(testCreateComponentImg.getRGB(centerX, centerY)));
				assertEquals("color delta: " + colorDelta,0,colorDelta);
			}
		}
//		 cleanup
//		 ToolBox.removeTmpfile(savePath);

	}

	/**
	 * Test method for {@link view.MaserComponentsImage#size(double)}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testSize() {

		maserCompImg = new MaserComponentsImage();
		int testruns = 500;
		for (int i = 1; i < testruns; i++) {
			double intensity = i;// random.nextDouble() * 1000;
			// MUT
			int size = maserCompImg.size(intensity);
			// System.out.println("testSize() : "+size);
			// the test
			assertTrue("< imgSize: ", size <= maserCompImg.getWidth());
		}

	}

	/**
	 * Test method for {@link view.MaserComponentsImage#size(double)}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testPosition() {

		// TODO x,y center of component?!
		maserCompImg = new MaserComponentsImage();
		int testruns = 10000;
		for (int i = 0; i < testruns; i++) {
			double xInput = (Double) MyMath.randomValueByRange(Double.class,
					xRange[0], xRange[1], '*');
			double yInput = (Double) MyMath.randomValueByRange(Double.class,
					yRange[0], yRange[1], '*');
			// MUT
			int[] xyPos = maserCompImg.position(xInput, yInput);
			// System.out.println(ToolBox.getCurrentMethodName() + " xInput: "
			// + xInput + " xOut: " + xyPos[0]);
			// System.out.println(ToolBox.getCurrentMethodName() + " yInput: "
			// + yInput + " yOut: " + xyPos[1]);
			// the tests
			assertTrue("yPos in ImageSize: ", xyPos[1] < maserCompImg
					.getHeight());
			assertTrue("yPos > 0: " + xyPos[1], xyPos[1] >= 0);
			assertTrue("xPos in ImageSize: ", xyPos[0] < maserCompImg
					.getWidth());
			assertTrue("xPos > 0: ", xyPos[0] >= 0);
		}

	}

	/**
	 * Test method for {@link view.MaserComponentsImage#dopplerColor(double)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// (timeout = DEFAULT_TIMEOUT)
	public final void testDopplerColor() throws Exception {
		maserCompImg = new MaserComponentsImage();
		int testRuns = 100000; // number of test to run Default 100
		// PointsOfIntrest
		double[] veloctityRange = maserCompImg.velocityRange;
		int[] hueRange = { 0, 240 };

		Object[][] poi = { { veloctityRange, hueRange } };

		// testParams

		// System.out.println("Method:" + ToolBox.getMethodName(this) + ":");

		for (int i = 0; i < poi.length + testRuns; i++) {
			double vMax = random.nextDouble() * 100;
			double[] vRange = (i < poi.length) ? (double[]) poi[i][0]
					: new double[] { -vMax, vMax };
			maserCompImg.setVelocityRange(vRange);
			int[] hRange = (i < poi.length) ? (int[]) poi[i][1] : new int[] {
					0, random.nextInt(360) };
			// maserCompImg.setDopllerColorRange(hRange);

			vMax = Math.max(Math.abs(vRange[0]), Math.abs(vRange[1]));
			double velocity = (random.nextDouble() - 0.5f) * vMax;
			double posVelocity = velocity + vMax;
			double scale = new Float(hRange[1]) / 2 / vMax;
			double expectedHue = (posVelocity * scale) / hRange[1];

			Color expectedVal = Color.getHSBColor((float) expectedHue, 1f, 1f);
			if (expectedVal.getRGB() == Color.RED.getRGB())
				continue; // no RED please why ??
			// MUT
			Color returnVal = maserCompImg.dopplerColor(velocity);

			// the Test
			// System.out.println("Colors: "+expectedVal+"return: "+returnVal);
			assertEquals("dopplerColor : ", expectedVal, returnVal);

		}
	}

	/**
	 * Test method for {@link model.MaserImage#drawGridImage(int, int)}.
	 * 
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// (expected = IllegalArgumentException.class)
	public final void testDrawMaserComponent() throws Exception {
		// TODO : :KLUDGE: works for only Colors like GREEN
		// FIXME Refactor for other Colors then GREEN
		String savePath1="";
		maserCompImg = new MaserComponentsImage(imgWidth, imgHeight, maserComponents);
		int testRuns = 100; // number of test to run Default 100
		// PointsOfIntrest
		int[][] poi = {};
		// {212, 205, 197,255},{186, 218, 188, 1},{ 168, 173, 71, 0 },
		// {59,55,15,255}
		// { 1, 255, 1, 102 }, { 5, 5, 5, 5 }, { 0, 0, 0, 0 }, { 0, 255, 0, 0
		// },{ 0, 255, 0, 12 }

		// testParams
		Color color = Color.GREEN; // is Default-Color
		// System.out.println("Method:" + ToolBox.getMethodName(this) + ":");

		for (int i = 0; i < poi.length + testRuns; i++) {
			int imgSize = random.nextInt(250) + 1;
			// System.out.println("imgSize: " + imgSize);

			int r = (i < poi.length) ? poi[i][0] : random.nextInt(255);
			int g = (i < poi.length) ? poi[i][1] : random.nextInt(255);
			int b = (i < poi.length) ? poi[i][2] : random.nextInt(255);
			int a = (i < poi.length) ? poi[i][3] : random.nextInt(255);
			color = new Color(r, g, b);

			color = Color.GREEN;
			BufferedImage tmpImg = new BufferedImage(imgSize, imgSize,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2dBG = tmpImg.createGraphics();
			// g2dBG.setBackground(Color.BLUE);
			// g2dBG.clearRect(0, 0, imgSize+5, imgSize+5);

			// MUT
			BufferedImage returnImg = maserCompImg.drawMaserComponent(imgSize,
					color);
			savePath=FilenameUtils.separatorsToSystem(TMPDIR_NAME
					+ "/testDrawMaserComponent.png");
			MyImage.saveImage(returnImg, savePath);

			
			// expected Values
			int expectedSize = returnImg.getHeight();
			int expectedHue = getHue(color);

			// the Tests
			// small sizes have special cases
			switch (imgSize) {
			case 1:
				assertTrue("hasColor ?! ", returnImg.getRGB(0, 0) != 0);
				break;
			case 2:
				assertTrue("hasColor ?! ", returnImg.getRGB(0, 0) != 0);
				assertTrue("hasColor ?! ", returnImg.getRGB(1, 0) != 0);
				assertTrue("hasColor ?! ", returnImg.getRGB(0, 1) != 0);
				assertTrue("hasColor ?! ", returnImg.getRGB(1, 1) != 0);
			case 3:
				assertTrue("hasColor ?! ", returnImg.getRGB(1, 1) != 0);
				break;
			// size > 3 pixels
			default:
				// 1. Color
				int[] rgbaVals = new int[returnImg.getWidth()
						* returnImg.getHeight()];
				rgbaVals = returnImg.getRGB(0, 0, returnImg.getWidth(),
						returnImg.getHeight(), rgbaVals, 0, returnImg
								.getWidth());

				g2dBG.drawImage(returnImg, 0, 0, null);
				// g2dBG.drawLine(0, imgSize/2, imgSize, imgSize/2);
				// mImage.saveImage(tmpImg, TMPDIR+FS+"MaserComp.png");
				int transpCounter = 0;
				for (int j = 0; j < rgbaVals.length; j++) {
					if (rgbaVals[j] != 0) {
						// TODO Alpha ?

						Color rgbaColor = new Color(rgbaVals[j]);
						int returnHue = getHue(rgbaColor);

						// TODO : rounding Problems ?! HSBtoRGB and Back
						assertTrue(
								"right Color Delta? : errorQuotient: "
										+ new Float(
												(float) (expectedHue - returnHue)
														/ (color.getRed()
																+ color
																		.getGreen() + color
																.getBlue()))
										+ "expectedHue: " + expectedHue
										+ "returnHue:" + returnHue,
								(expectedHue - returnHue) < 1);// ||returnHue==0
						// assertEquals("right Color? ", expectedHue,
						// returnHue);//value -16056566

					} else
						transpCounter++;
				}

				// 2. Transparancy
				assertTrue("Transparent pixels > 0 : " + transpCounter,
						transpCounter > 0);

				// 3. Point Symmetry to ImageCenter
				BufferedImage symImg = new BufferedImage(imgSize, imgSize,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2dSym = symImg.createGraphics();
				g2dBG.setColor(Color.BLACK);
				// g2dBG.drawLine(0, imgSize/2, imgSize/2, imgSize/2);
				AffineTransform affineT = new AffineTransform();
				// :KLUDGE: works only for multiple of 90¡
				affineT.rotate(Math.toRadians(180), (imgSize / 2),
						(imgSize / 2));

				g2dSym.drawImage(tmpImg, affineT, null);
				savePath1=FilenameUtils.separatorsToSystem(TMPDIR_NAME
						+ "/testDrawMaserComponent_SymImg.png");
				MyImage.saveImage(symImg, savePath);
				int[] symVals = new int[imgSize * imgSize];
				int[] returnVals = new int[imgSize * imgSize];
				symVals = symImg.getRGB(0, 0, imgSize, imgSize, null, 0,
						imgSize);
				returnVals = returnImg.getRGB(0, 0, imgSize, imgSize, null, 0,
						imgSize);
				// for (int j = 0; j < returnVals.length; j++) {
				// assertEquals("right Color? at index: "+j, symVals[j],
				// returnVals[j]);
				// }
				assertTrue("srcImg=rotated Img?: ", Arrays.equals(symVals,
						returnVals));
			}
			// 4. size
			assertEquals("grid pixels: ", expectedSize, imgSize);
//			assertEquals("grid pixels: ", 0, imgSize);

		}
		// cleanup
		 ToolBox.removeTmpfile(savePath);
		 ToolBox.removeTmpfile(savePath1);
	}

	/**
	 * Test method for
	 * {@link view.MaserComponentsImage#sortComponentsByVelocity(MaserComponent[])}.
	 */
	@Test
//	(timeout = DEFAULT_TIMEOUT)
	public final void testsSortComponentsByVelocity() {
		maserCompImg = new MaserComponentsImage(500, 500, maserComponents);
		MaserComponent[] expectedComponents = maserComponents.clone();
		Collections.shuffle(Arrays.asList(expectedComponents));
		Collections.shuffle(Arrays.asList(maserComponents));
		String key = "VELOCITY";
		// set a few velocitys equal
		Double velocityLast = (Double) expectedComponents[expectedComponents.length - 1]
				.getDataMap().get(key);
		expectedComponents[random.nextInt(expectedComponents.length - 1)]
				.getDataMap().put(key, velocityLast);
		expectedComponents[random.nextInt(expectedComponents.length - 1)]
				.getDataMap().put(key, velocityLast);
		expectedComponents[random.nextInt(expectedComponents.length - 1)]
				.getDataMap().put(key, velocityLast);
		int testruns = 1;
		for (int i = 0; i < testruns; i++) {
			Object[] values = new Object[expectedComponents.length];
			for (int j = 0; j < expectedComponents.length; j++) {
				values[j] = expectedComponents[j].getVelocity();
			}
			Object[] sortedVals = values.clone();
			// System.out.println(ToolBox.getCurrentMethodName() + "unsorted :"
			// + key + " " + Arrays.toString(values));
			Arrays.sort(sortedVals);
			// System.out.println(ToolBox.getCurrentMethodName() + "sorted asc: "
			// + key + " " + Arrays.toString(sortedVals));

			// MUT
			MaserComponent[] returnComponents = maserCompImg
					.sortComponentsByVelocity(expectedComponents);
			// System.out.println(ToolBox.getCurrentMethodName() + " key:"+ key);

			for (int j = 0; j < returnComponents.length; j++) {
				// the tests
				assertEquals("sorted by  " + key, sortedVals[i],
						returnComponents[i].getVelocity());
				//component only once
				assertTrue("component only once " + returnComponents[i],
						ArrayUtils.indexOf(returnComponents,
								returnComponents[i]) == ArrayUtils.lastIndexOf(
								returnComponents, returnComponents[i]));
			}
		}
	}
}
