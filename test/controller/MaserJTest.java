/**
 * 
 */
package controller;

import static common.TEST.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import model.CelestialObject;
import model.CelestialObjectTest;
import model.DBTable;
import model.MaserComponent;
import model.MaserComponentTest;
import model.DataBase;
import model.Observation;
import nom.tam.fits.FitsException;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import tools.DateCalculator;
import tools.ToolBox;
import model.ObservationTest;


public class MaserJTest {

	static DataBase db;

	static File tmpDir = SystemUtils.getJavaIoTmpDir();

	String inputPath;

	File startDir;

	MaserJ maserJ;

	private Date[] obsDates;

	private File[] inputFitsFiles;

	private Date startDate;

	private int obsDateAmount;

	private File fitsFileSrc;

	private String fitsSrcPath;

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
//		db.dropTables();
//		db.shutdownDB();
		db = null;
		// TODO CleanUp in TMP
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// application = new MaserJ(SystemUtils.getUserDir().getAbsolutePath());
		// create inputFiles
		// ?? create consistent TestData fits
		db.clearTables();
		inputPath = tmpDir + "/testMaserJ/InputFiles";
		inputPath = FilenameUtils.separatorsToSystem(inputPath);
		startDir = new File(inputPath);
		FileUtils.deleteQuietly(startDir);
		FileUtils.forceMkdir(startDir);
		FileUtils.cleanDirectory(startDir);

		fitsSrcPath = FilenameUtils
				.separatorsToSystem("test/examples/effelsbergSample.fits");
		fitsFileSrc = new File(fitsSrcPath);
		obsDateAmount = 100;
		startDate = DateUtils.addDays(new Date(0), random.nextInt(365 * 20));
		obsDates = DateCalculator.uniqueRadomDayDates(startDate, obsDateAmount,
				false);// /really unit
		inputFitsFiles = ObservationTest.createFitsFiles(startDir, fitsFileSrc,
				obsDates);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		FileUtils.cleanDirectory(startDir);
		FileUtils.deleteQuietly(startDir);
		//db.clearTables();
		Thread.sleep(1000);
		maserJ = null;
		inputPath = null;
		startDir = null;
		obsDates = null;
		inputFitsFiles = null;
		startDate = null;
		obsDateAmount = 0;
		fitsFileSrc = null;
		fitsSrcPath = null;
	}

	/**
	 * Test method for {@link controller.MaserJ#main(java.lang.String[])}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testMain() throws Exception {

		// System.out.println(ToolBox.getCurrentMethodName()
		// + "PRE new MaserJ(startDir.getAbsolutePath()");
		// MUT
		maserJ = new MaserJ(startDir.getAbsolutePath());
		// the Test
		assertEquals("inputDir: ", startDir, maserJ.getInputDir());
		// cleanUp
		FileUtils.forceDeleteOnExit(startDir);
	}

	/**
	 * Test method for {@link controller.MaserJ#inputFiles(String startPath)}.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testInputFiles() throws Exception {
		// System.out.println(ToolBox.getCurrentMethodName()
		// + "PRE new MaserJ(startDir.getAbsolutePath()");
		File[] expectedFiles = inputFitsFiles;
		Arrays.sort(expectedFiles);
		// MUT
		maserJ = new MaserJ(startDir.getAbsolutePath());
		File[] returnedFiles = maserJ.inputFiles(startDir);
		Arrays.sort(returnedFiles);
		for (int i = 0; i < returnedFiles.length; i++) {
			// the test
			assertEquals("inputFilesPathes: " + i, expectedFiles[i]
					.getAbsolutePath(), returnedFiles[i].getAbsolutePath());
		}
		// cleanUp
		FileUtils.forceDeleteOnExit(startDir);
	}

	/**
	 * Test method for {@link controller.MaserJ#main(java.lang.String[])}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testMaserJ() throws Exception {
		String expectedVal = startDir.getAbsolutePath();
		String examplePath = FilenameUtils
				.separatorsToSystem("test/examples/observation.fits");
		File inputFile = new File(examplePath);
		// File[] expectedFiles = ToolBox.createObservationDirStruct(startDir,
		// 1,
		// examplePath);
		File[] expectedFiles = inputFitsFiles;
		Arrays.sort(expectedFiles);
		CelestialObject expectedCelObj = new CelestialObject(CelestialObject
				.dataFromFitsFile(inputFile));

		// System.out.println(ToolBox.getCurrentMethodName()
		// + "PRE new MaserJ(startDir.getAbsolutePath()");
		// MUT
		maserJ = new MaserJ(expectedVal);
		// the tests
		assertEquals("inputPath: ", new File(expectedVal), maserJ.getInputDir());
		assertEquals("celObj: ", expectedCelObj.getName(), maserJ.celObj
				.getName());

		// test all created observations
		Observation[] expectedObservations = new Observation[expectedFiles.length];
		for (int i = 0; i < expectedFiles.length; i++) {
			expectedObservations[i] = new Observation(expectedFiles[i],
					maserJ.celObj, false);
			expectedObservations[i].getDataMap().remove("id");
		}

		Observation[] returnededObservations = maserJ.celObj.getObseravtions();
		for (int i = 0; i < returnededObservations.length; i++) {
			// expectedObservations has no id
			expectedObservations[i].getDataMap().put("id",
					returnededObservations[i].getDataMap().get("id"));
			assertTrue("allObservations compare Maps, expected: "
					+ expectedObservations[i].getDataMap() + ", returned: "
					+ returnededObservations[i].getDataMap(), ToolBox
					.compareHashMaps(expectedObservations[i].getDataMap(),
							returnededObservations[i].getDataMap(), false));
		}
		// test all created Components
		int amount = 4;
		assertEquals("amount of created Components:", amount,
				returnededObservations[0].getComponents().length);

		// provoke IllegalArgumentException
		try {
			maserJ = new MaserJ("nonsense");
		} catch (IllegalArgumentException e) {
			System.out.println("expected IllegalArgumentException: " + e);
		}
	}

	/**
	 * Test method for
	 * {@link controller.MaserJ#editComponentPositions(Observation[] obs)}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testEditComponentPositions() throws Exception {
		//TODO generate TestData, 1 celObj ->n Obeservations -> m Components
		ObservationSeries testObsSeries= appTestData(4);
		maserJ = new MaserJ();
		// MUT
		maserJ.editComponentPositions(testObsSeries.getObservations());
		
		ToolBox.sleep(10000*60); //show GUI longer
		
		// the Test TODO
		// assertEquals("inputDir: ", startDir, maserJ.getInputDir());
		// cleanUp
		// FileUtils.forceDeleteOnExit(startDir);
	}
	
	/**
	 * convenience method: create consistent TestData:
	 * 						 1 CelestialObject with n Observations with m MaserComponents

	 * 
	 */
	public static ObservationSeries appTestData(int numberOfObs) throws Exception {
		Double[] xRange = {-120.0,120.0};
		Double[] yRange = {-120.0,120.0};
		Double[] vRange = {-12.0,12.0}; 
		Double[] iRange = { 0.0,100.0}; 
		Double[] bRange = {0.0,120.0};
		Date startDate=DateUtils.addYears(new Date(0), 25);
		Date[] obsDates=DateCalculator.uniqueRadomDayDates(startDate, 2*numberOfObs
				,false);
		HashMap<String, Object> celObjData = CelestialObjectTest.randomCelObjRow();
		celObjData.put("name", "Object XY");
		CelestialObject celObj= new CelestialObject(celObjData);
		
		Observation[] observations = new Observation[numberOfObs];
		MaserComponent[] mComps= new MaserComponent[random.nextInt(numberOfObs)+1];
		for (int i = 0; i < observations.length; i++) {
			HashMap<String, Object> obsData = ObservationTest.randomObservationData();
			obsData.put("object_id", celObj.getID());
			obsData.put("date", obsDates[i]);
			observations[i]=new Observation(obsData, celObj);
			for (int j = 0; j < mComps.length; j++) {
				HashMap<String, Object> compData = MaserComponentTest.
					randomComponentRow(xRange, yRange, vRange, iRange, bRange);
				if (random.nextBoolean()) {
					compData.put("xoffset", Double.MIN_VALUE);
					compData.put("yoffset", Double.MIN_VALUE);
				}
				compData.put("name", "Comp "+j);
				mComps[j]=new MaserComponent(compData, observations[i]);
			}
			observations[i].setComponents(mComps);
			mComps= new MaserComponent[random.nextInt(numberOfObs)+1];
		}
		celObj.setObseravtions(observations);
		ObservationSeries obsSeries = new ObservationSeries(celObj, obsDates);
		return obsSeries;
	}
	
}
