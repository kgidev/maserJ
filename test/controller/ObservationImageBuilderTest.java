/**
 * 
 */
package controller;

import static common.TEST.random;
import static common.TEST.DEFAULT_TIMEOUT;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import model.CelestialObject;
import model.CelestialObjectTest;
import model.MaserComponent;
import model.MaserComponentTest;
import model.DBTable;
import model.DBTableTest;
import model.DataBase;
import model.Observation;
import view.ColorCodeImage;
import view.GridImage;
import view.MaserComponentsImage;
import view.MaserComponentsImageTest;
import view.ObservationImage;
import view.TextDataImage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.MyImage;
import tools.ToolBox;


public class ObservationImageBuilderTest {

	static DataBase dataBase;
	static ObservationImageBuilder obsImgBuilder;
	static File tmpDir = SystemUtils.getJavaIoTmpDir();
	
	Observation observation;
	HashMap<String, Object> obsData;
	MaserComponent[] maserComponents;
	CelestialObject celObj;

	/**
	 * @throws java.lang.Exception
	 */ 
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dataBase = new DataBase(true, common.TEST.dbURL, common.TEST.dbFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dataBase=null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

//		create CelestialObject 
		celObj = new CelestialObject(CelestialObjectTest.randomCelObjRow());
		DBTable celObjTable=new DBTable("CELESTIALOBJECTS");
		celObj.insertMap2DBTable(celObj.getDataMap());
		int celObjTableID=(Integer)celObjTable.getRowsOrderByID()[0].get("id");
		celObj.setID(celObjTableID);
//		create Observation 
	    obsData =DBTableTest.randomRowData("OBSERVATIONS");
	    obsData.put("object_id", celObjTableID);
		observation = new Observation(obsData);
//		create components 
		int numberOfComponents = random.nextInt(100) + 1;
		maserComponents=new MaserComponent[numberOfComponents];
		for (int i = 0; i < numberOfComponents; i++) {
			HashMap<String, Object> row = MaserComponentTest.randomComponentRow(MaserComponentsImageTest.xRange, 
					MaserComponentsImageTest.yRange, MaserComponentsImageTest.vRange,
					MaserComponentsImageTest.iRange, MaserComponentsImageTest.bRange);
			row.put("id", 0);
			row.put("object_id", 666);
			maserComponents[i]=new MaserComponent(row, false);
		}	
		observation.setComponents(maserComponents);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		dataBase.clearTables();
		obsImgBuilder=null;
		observation=null;
		obsData=null;
		maserComponents=null;
		celObj=null;
	}

	/**
	 * Test method for
	 * {@link controller.ObservationImageBuilder#ObservationImageBuilder()}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testObservationImageBuilder() {
		//MUT
		obsImgBuilder = new ObservationImageBuilder();
		//the tests
		assertTrue("obsImgBuilder exists: ", obsImgBuilder!=null);
	}

	/**
	 * Test method for
	 * {@link controller.ObservationImageBuilder#ObservationImageBuilder(model.Observation)}.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testObservationImageBuilderObservation() throws Exception {
//		MUT
		//observation = new Observation(obsData);
		obsImgBuilder = new ObservationImageBuilder(observation);
		String imagePath = FilenameUtils.separatorsToSystem(tmpDir
				.getCanonicalPath()
				+ "/"+ToolBox.getCurrentMethodName()+".png");
		obsImgBuilder.save(obsImgBuilder.obsImg, imagePath);
		//the test
		assertEquals("observation: ", obsData, obsImgBuilder.getObservation()
				.getDataMap());

		// cleanup
//		File imageFile = new File(imagePath);
		// FileUtils.forceDeleteOnExit(imageFile);
	}

	/**
	 * Test method for {@link controller.ObservationImageBuilder#build()}.
	 *
	 * @throws IOException
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testBuild() throws Exception {
//		obsImgBuilder = new ObservationImageBuilder();
		// TODO grid,colorCode,ComponentImg,TextImg
		String placeHolderImgPath=FilenameUtils.separatorsToSystem("lib/image/nopicture.png");
		BufferedImage placeHolderImg=MyImage.readImage(placeHolderImgPath);
		String imagePath = FilenameUtils.separatorsToSystem(tmpDir
				.getCanonicalPath()
				+ "/"+ToolBox.getCurrentMethodName()+".png");
		placeHolderImg =new BufferedImage(10,10,2);
		Color color=Color.WHITE;
		placeHolderImg=MyImage.setBackgroundColor(placeHolderImg, color);
		

//		 MUT	
		obsImgBuilder = new ObservationImageBuilder();
		obsImgBuilder.setObservation(observation);
		obsImgBuilder.gridImg=new GridImage(600,600);
		obsImgBuilder.celestialObjectTextImg=new TextDataImage(celObj.getDataMap(),"CELESTIALOBJECT", celObj.getKeyOrder());
		obsImgBuilder.observationTextImg=new TextDataImage(observation.getDataMap(),"OBSERVATION", observation.getKeyOrder());
		obsImgBuilder.colorCodeImg=new ColorCodeImage(50,500);
		//create components 
		int numberOfComponents = random.nextInt(100) + 1;
		MaserComponent[] components=new MaserComponent[numberOfComponents];
		for (int i = 0; i < numberOfComponents; i++) {
			HashMap<String, Object> row = MaserComponentTest.randomComponentRow(MaserComponentsImageTest.xRange, 
					MaserComponentsImageTest.yRange, MaserComponentsImageTest.vRange,
					MaserComponentsImageTest.iRange, MaserComponentsImageTest.bRange);
			row.put("observation_id".toUpperCase(), 0);
			components[i]=new MaserComponent(row, false);
		}	
		obsImgBuilder.maserCompsImg=new MaserComponentsImage(500, 500,components,
				observation.isInterpolated());
		obsImgBuilder.save(obsImgBuilder.maserCompsImg, imagePath.replace(".png", "_maserCompsImg.png"));
		obsImgBuilder.setObsImg(obsImgBuilder.build());
		ObservationImage obsImg = obsImgBuilder.getObsImg();//build();
		obsImgBuilder.save(obsImgBuilder.obsImg, imagePath.replace(".png", "_with_maserCompsImg.png"));
//		MyImage.png2jpg(imagePath.replace(".png", "_with_maserCompsImg.png"));
		// the tests
		assertEquals("width: ", obsImgBuilder.width, obsImgBuilder.obsImg.getWidth());
		assertEquals("height: ", obsImgBuilder.height, obsImgBuilder.obsImg.getHeight());	
		assertEquals("bgColor GridImg: ", color.getRGB(),
				obsImg.getRGB(obsImg.getGridImgX(), obsImg.getGridImgY()));
		assertEquals("bgColor CelestialObjectTextImg: ", obsImgBuilder.celestialObjectTextImg.getRGB(0, 0),
				obsImg.getRGB(obsImg.getCelestialObjectTextImgX(), obsImg.getCelestialObjectTextImgY()));
		assertEquals("bgColor observationTextImg: ", obsImgBuilder.observationTextImg.getRGB(0,0),
				obsImg.getRGB(obsImg.getObservationTextImgX(), obsImg.getObservationTextImgY()));
		assertEquals("bgColor colorCodeImg: ", obsImgBuilder.colorCodeImg.getRGB(0,0),
				obsImg.getRGB(obsImg.getColorCodeImgX(), obsImg.getColorCodeImgY()));
		assertEquals("bgColor maserImg: ", obsImgBuilder.maserCompsImg.getRGB(0,0),
				obsImg.getRGB(obsImg.getMaserImgX(), obsImg.getMaserImgY()));
		assertEquals("Interpolated: ", obsImgBuilder.maserCompsImg.getRGB(0,0),
				obsImg.getRGB(obsImg.getMaserImgX(), obsImg.getMaserImgY()));
//		

		//set every part-Image alone and test content
//		obsImgBuilder = new ObservationImageBuilder(observation);
		
//		BufferedImage returnImg = obsImgBuilder.getSubimage(
//				obsImgBuilder.backgroundImgX, obsImgBuilder.backgroundImgY,
//				obsImgBuilder.backgroundImg.getWidth(),
//				obsImgBuilder.backgroundImg.getHeight());
//
//		BufferedImage expectedImg = MyImage.translateBi2Type(
//				obsImgBuilder.backgroundImg, returnImg.getType());
//		obsImgBuilder.save(returnImg, imagePath + "1");
//
//		assertTrue("backgroundImg: ", MyImage.compareImagesRawData(returnImg,
//				expectedImg));


		// cleanup
//		File imageFile = new File(imagePath);
		// FileUtils.forceDeleteOnExit(imageFile);
	}

	/**
	 * Test method for
	 * {@link controller.ObservationImageBuilder#save(java.io.File)}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT, expected = IOException.class)
	public final void testSave() throws Exception {

		obsImgBuilder = new ObservationImageBuilder();
		String imagePath = FilenameUtils.separatorsToSystem(tmpDir
				.getCanonicalPath()
				+ "/"+ToolBox.getCurrentMethodName()+".png");
		File imageFile = new File(imagePath);
		BufferedImage testImg=new BufferedImage(50,40,2);
		testImg=MyImage.setBackgroundColor(testImg, Color.BLACK);
		// MUT
		obsImgBuilder.save(testImg, imagePath);
		// the test
		BufferedImage savedImg = MyImage.readImage(imagePath);
		savedImg = MyImage.translateBi2Type(savedImg, 2);
		assertTrue("Image-compare", MyImage.compareImagesRawData(testImg, savedImg));
		// cleanup
		FileUtils.forceDeleteOnExit(imageFile);
	}

}
