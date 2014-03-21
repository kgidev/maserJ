/**
 * 
 */
package model;

import static common.TEST.DEFAULT_TIMEOUT;
import static common.TEST.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.HashMap;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.util.BufferedFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.ToolBox;


public class ObservationTest {

	Observation obsvervation;

	static File tmpDir = SystemUtils.getJavaIoTmpDir();

	static DataBase db;

	DBTable celObjectsTable;

	static DBTable obsTable;

	int objectId;

	Date obsDate;

	HashMap<String, Object>[] obsRows;

	HashMap<String, Object> obsData;

	HashMap<String, Object> celObjData;

	CelestialObject celObj;

	int numberOfObs;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.dropTables();
		// db.shutdownDB();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// insert Test-Data in table Objects
		db = new DataBase(true, common.TEST.dbURL, common.TEST.dbFile);
		celObjectsTable = new DBTable("CELESTIALOBJECTS");
		celObjData = CelestialObjectTest.randomCelObjRow();

		objectId = 666;
		String objectName = "ObservationTest";
		//		
		// String sqlCmd = "INSERT INTO CELESTIALOBJECTS (id,name) VALUES ("
		// + objectId + ",'" + objectName + "')";
		// db.executeSQL(sqlCmd);

		celObjData.put("name", objectName);
		celObjData.put("id", objectId);
		// objectId = (Integer)celObjData.get("id");

		celObjectsTable.insertMap2DBTable(celObjData, false);
		celObj = new CelestialObject(celObjectsTable.getRows()[0]);
		obsTable = new DBTable("OBSERVATIONS");
		// insert TestData in Observations-Table
		// obsvervation=new Observation(obsDate,objectId);
		numberOfObs = 10;
		ToolBox.insertObservationRows(numberOfObs, new DBTable("Observations"),
				objectId, null);
		obsRows = obsTable.getRows();
		int randIndex = random.nextInt(obsRows.length);
		obsData = obsRows[randIndex];
		obsDate = (Date) obsData.get("date");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		db.dropTables();
		celObjectsTable = null;
		celObj = null;
		obsData = null;
		obsvervation = null;

	}

	/**
	 * Test method for {@link model.Observation#Observation(java.sql.Date)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testObservation() throws Exception {

		// MUT
		try {
			obsvervation = new Observation(obsDate, objectId);
		} catch (Exception e) {
			// TODO better catch
			e.printStackTrace();
			throw e;
		}
		// the Tests: date,objectId,obsData,
		assertEquals("Obs-Date: ", obsDate, obsvervation.getDate());
		assertEquals("Obs-objectId: ", objectId, obsvervation.getObjectId());
		String whereCond = "WHERE object_id=" + objectId + " AND date='"
				+ obsDate.toString() + "'";
		HashMap<String, Object> expectedData = obsvervation
				.getRowsByWhereCondition(whereCond)[0];
		assertTrue("obsData: ", expectedData.equals(obsvervation.getDataMap()));
		// 2.Constructor
		obsRows[0].put("date", DateUtils.addDays(new Date(0), -1000));// nut
																		// used
																		// Date
		obsvervation = new Observation(obsRows[0]);
		assertEquals("obsData: ", obsRows[0], obsvervation.getDataMap());
	}

	/**
	 * Test method for
	 * {@link model.Observation#Observation(HashMap<String, Object> obsData, CelestialObject celObj)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testObservationMapCelObj() throws Exception {

		// MUT
		try {
			obsData.put("date", DateUtils.addDays(new Date(0), -666));// nut
																		// used
																		// Date
			obsvervation = new Observation(obsData, celObj, true);
		} catch (Exception e) {
			// TODO better catch
			e.printStackTrace();
			throw e;
		}
		// the Tests: date,objectId,obsData,
		assertEquals("Obs-Data: ", obsData, obsvervation.getDataMap());
		assertEquals("Obs-celObj: ", celObj, obsvervation.getCelObject());
		assertEquals("celObjID == object_id: ", celObj.getID(), obsvervation
				.getObjectId());
	}

	/**
	 * Test method for
	 * {@link model.Observation#Observation(HashMap<String, Object> obsData, CelestialObject celObj)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testObservationFitsFileCelObj() throws Exception {
		HashMap<String, Object> dataFromFitsFile = new HashMap<String, Object>();
		String instrument = "instrumentName";
		Date date = DateUtils.addMonths(new Date(0), 5);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		SimpleDateFormat dfmt = new SimpleDateFormat("dd/MM/yy");
		String dateStr = dfmt.format(sqlDate);
		dataFromFitsFile.put("instrument", instrument);
		dataFromFitsFile.put("date", dateStr);
		dataFromFitsFile.put("noise", 0.0);
		dataFromFitsFile.put("interpolated", false);
		HashMap<String, Object> expectedData = (HashMap<String, Object>) dataFromFitsFile
				.clone();
		// create& change FitsFile
		String fitsPath = FilenameUtils
				.separatorsToSystem("test/examples/effelsbergSample.fits");
		File fitsFile = new File(fitsPath);
		Fits fits = new Fits(fitsFile);
		BasicHDU hdu = fits.readHDU();
		Header header = hdu.getHeader();
		header.addValue("TELESCOP", instrument, "instrument");
		header
				.addValue("DATE-OBS", dataFromFitsFile.get("date").toString(),
						"");
		String imagePath = FilenameUtils.separatorsToSystem(tmpDir
				.getCanonicalPath()
				+ "/" + ToolBox.getCurrentMethodName() + ".fits");
		File testFitsFile = new File(imagePath);
		BufferedFile dos = new BufferedFile(imagePath, "rw");
		fits.write(dos);
		expectedData.put("date", sqlDate);
		// MUT
		obsvervation = new Observation(testFitsFile, celObj);
		HashMap<String, Object> returnedData = (HashMap<String, Object>) obsvervation
				.getDataMap().clone();
		returnedData.remove("id");
		returnedData.remove("object_id");
		// the tests: date,objectId,obsData,
		Date expectedDate = (Date) expectedData.get("date");
		Date returnedDate = (Date) returnedData.get("date");
		returnedData.remove("date");
		expectedData.remove("date");
		assertEquals("dates: ",
				DateUtils.truncate(expectedDate, Calendar.DATE), DateUtils
						.truncate(returnedDate, Calendar.DATE));
		assertTrue("Obs-Data without Dates: ", ToolBox.compareHashMaps(
				expectedData, returnedData, true));

		assertEquals("celObjID == object_id: ", celObj.getID(), obsvervation
				.getObjectId());
	}

	/**
	 * Test method for {@link model.Observation#getDate()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetDate() throws Exception {
		obsvervation = new Observation(obsDate, objectId);
		assertEquals("obsDate: ", obsDate, obsvervation.getDate());
	}

	/**
	 * Test method for {@link model.Observation#getInstrument()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testGetInstrument() throws Exception {
		obsvervation = new Observation(obsDate, objectId);
		ResultSet expectedResult = db
				.executeSQL("SELECT instrument from Observations WHERE object_id="
						+ objectId + " AND date='" + obsDate.toString() + "'");
		expectedResult.next();
		assertEquals("getInstrument: ", expectedResult.getString(1),
				obsvervation.getInstrument());
	}

	/**
	 * Test method for {@link model.Observation#getInterpolated()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetInterpolated() throws Exception {
		obsvervation = new Observation(obsDate, objectId);
		ResultSet expectedResult = db
				.executeSQL("SELECT interpolated from Observations WHERE object_id="
						+ objectId + " AND date='" + obsDate.toString() + "'");
		expectedResult.next();
		assertEquals("getInterpolated: ", expectedResult.getBoolean(1),
				obsvervation.getInterpolated());
	}

	/**
	 * Test method for {@link model.Observation#getNoise()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetNoise() throws Exception {
		obsvervation = new Observation(obsDate, objectId);
		ResultSet expectedResult = db
				.executeSQL("SELECT noise from Observations WHERE object_id="
						+ objectId + " AND date='" + obsDate.toString() + "'");
		expectedResult.next();
		assertEquals("getNoise: ", expectedResult.getDouble(1), obsvervation
				.getNoise());
	}

	/**
	 * Test method for {@link model.Observation#insert2DBTable()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testInsert2DBTable() throws Exception {
		obsvervation = new Observation(obsDate, objectId);
		// TODO change values & insert in DB
		HashMap<String, Object> randRow = ToolBox.randomRow(obsvervation
				.getTableStruct());
		obsvervation.dataMap = randRow;
		obsvervation.dataMap.put("object_id", (Integer) objectId);

		Date sqldate = new Date(DateUtils.addMonths(new Date(0),
				random.nextInt(100) + 1).getTime());
		obsvervation.dataMap.put("date", sqldate);
		// MUT
		obsvervation.insert2DBTable();
		ResultSet expectedResult = db
				.executeSQL("SELECT * from Observations ORDER BY id DESC");// get
		// last
		// inserted
		db.executeSQL("commit");
		// db.dump(expectedResult);
		// ResultSet expectedResult=db.executeSQL("call identity()");//??funzt
		// nicht?!
		expectedResult.next();

		// the Test
		// ToolBox.dumpHahsMap(obsvervation.dataMap);
		obsvervation.setDataMap(obsvervation.getDBTableRowById(expectedResult
				.getInt("ID")));
		for (String key : obsvervation.dataMap.keySet()) {
			assertEquals("value: ", obsvervation.dataMap.get(key).toString(),
					expectedResult.getObject(key).toString());
		}
	}

	/**
	 * Test method for {@link model.Observation#loadComponentsFromDB()}. &
	 * {@link model.Observation#getComponents()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetSetComponents() throws Exception {
		obsvervation = new Observation(obsDate, objectId);
		// write TestData to DB: 1 object,1Observation, n components
		obsDate = new Date(0);
		objectId = 1;
		int observationId = 1;
		java.sql.Date sqlDate = new java.sql.Date(obsDate.getTime());
		String sqlCmd = " DELETE FROM COMPONENTS;";
		sqlCmd += " DELETE FROM OBSERVATIONS;";
		sqlCmd += " DELETE FROM COMPONENTS;";
		sqlCmd += " INSERT INTO CELESTIALOBJECTS (id,name) VALUES (1,'ObjectName');";
		sqlCmd += " INSERT INTO OBSERVATIONS (id,date,object_id) VALUES (1,'"
				+ sqlDate + "'," + objectId + ");";
		int numberOfComps = 10;
		for (int i = 0; i < numberOfComps; i++) {
			sqlCmd += " INSERT INTO Components (id,observation_id) VALUES (null,"
					+ observationId + ");";
		}
		db.executeSQL(sqlCmd);
		// // MUT
		obsvervation.setDataMap(obsvervation.getDBTableRow(obsDate, objectId));
		obsvervation.setComponents(obsvervation.loadComponentsFromDB());
		MaserComponent[] comps = obsvervation.getComponents();
		// the Test
		assertEquals("number of Components: ", numberOfComps, comps.length);
	}

	/**
	 * Test method for {@link model.componentsValuesByKey(String)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testComponentsValuesByKey() throws Exception {
		obsvervation = new Observation(obsDate, objectId);

		// components
		HashMap<String, Object> compData;
		HashMap<String, Object[]> expectedValues = new HashMap<String, Object[]>();
		MaserComponent[] components = new MaserComponent[random.nextInt(10)];
		for (int i = 0; i < components.length; i++) {
			compData = randomComponentData();
			components[i] = new MaserComponent(compData,obsvervation,false);
			Set<String> keys = compData.keySet();
			for (String key : keys) {
				expectedValues.put(key, ArrayUtils.add(expectedValues.get(key),
						compData.get(key)));
			}
		}
		obsvervation.setComponents(components);
		// the test
		Set<String> keys = expectedValues.keySet();
		for (String key : keys) {
			assertEquals("key Values: " + key, expectedValues.get(key),
					obsvervation.componentsValuesByKey(key));
		}
	}

	/**
	 * Test method for {@link model.componentsValuesRangeString)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testComponentsValuesRange() throws Exception {
		obsvervation = new Observation(obsDate, objectId);

		// components
		HashMap<String, Object> compData;
		HashMap<String, Object[]> expectedValues = new HashMap<String, Object[]>();
		int compAmount = random.nextInt(10);
		MaserComponent[] components = new MaserComponent[compAmount];
		for (int i = 0; i < components.length; i++) {
			compData = randomComponentData();
			components[i] = new MaserComponent(compData,false);
			Set<String> keys = compData.keySet();
			for (String key : keys) {
				expectedValues.put(key, ArrayUtils.add(expectedValues.get(key),
						compData.get(key)));
			}
		}
		obsvervation.setComponents(components);
		// the test
		Set<String> keys = expectedValues.keySet();
		for (String key : keys) {
			Arrays.sort(expectedValues.get(key));
			// System.out.println("testComponentsValuesRange() " + key +
			// " obsvervation.componentsValuesRange(key) :"
			// + Arrays.toString(obsvervation.componentsValuesRange(key)));
			assertEquals("key Values: " + key, expectedValues.get(key)[0],
					obsvervation.componentsValuesRange(key)[0]);
			assertEquals("key Values: " + key,
					expectedValues.get(key)[compAmount - 1], obsvervation
							.componentsValuesRange(key)[1]);
		}
	}

	/**
	 * Test method for {@link model.Observation#dataFromFitsFile(File fitsFile)}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testdataFromFitsFile() throws Exception {
		int testRuns = 100;
		for (int i = 0; i < testRuns; i++) {
			HashMap<String, Object> dataFromFitsFile = new HashMap<String, Object>();
			String instrument = "instrumentName" + i;
			Date date = DateUtils.addMonths(new Date(0), i + 1);
			dataFromFitsFile.put("instrument", instrument);
			// date
			String dateStr = DateFormatUtils.formatUTC(date, "dd/MM/yy");
			dataFromFitsFile.put("date", dateStr);
			dataFromFitsFile.put("noise", 0.0);
			dataFromFitsFile.put("interpolated", false);
			HashMap<String, Object> expectedData = (HashMap<String, Object>) dataFromFitsFile
					.clone();
			// change Fits-Content
			String fitsPath = FilenameUtils
					.separatorsToSystem("test/examples/effelsbergSample.fits");
			File fitsFile = new File(fitsPath);
			Fits fits = new Fits(fitsFile);
			BasicHDU hdu = fits.readHDU();
			Header header = hdu.getHeader();
			header.addValue("TELESCOP", instrument, "instrument");
			header.addValue("DATE-OBS",
					dataFromFitsFile.get("date").toString(), "");
			String imagePath = FilenameUtils.separatorsToSystem(tmpDir
					.getCanonicalPath()
					+ "/" + ToolBox.getCurrentMethodName() + ".fits");
			File testFitsFile = new File(imagePath);
			BufferedFile dos = new BufferedFile(imagePath, "rw");
			fits.write(dos);
			// MUT
			// dataFromFitsFile = Observation.dataFromFitsFile(testFitsFile);
			HashMap<String, Object> returnedData = Observation
					.dataFromFitsFile(testFitsFile);
			// fucking date special-Treatment
			returnedData.put("date", DateFormatUtils.format((Date) returnedData
					.get("date"), "dd/MM/yy"));
			// the test
			assertTrue("compare maps, expectedData: " + expectedData
					+ " returnedData: " + returnedData, ToolBox
					.compareHashMaps(expectedData, returnedData, false));
			// cleanup
			FileUtils.forceDeleteOnExit(testFitsFile);
		}
	}

	/**
	 * convenience method: create Random-Component-RowData
	 * 
	 * @throws Exception
	 * 
	 */
	public static HashMap<String, Object> randomComponentData()
			throws Exception {
		return DBTableTest.randomRowData("COMPONENTS");
	}
	
	/**
	 * convenience method: create Random-Observation-RowData
	 * 
	 * @throws Exception
	 * 
	 */
	public static HashMap<String, Object> randomObservationData()
			throws Exception {
		return DBTableTest.randomRowData("OBSERVATIONS");
	}

	/**
	 * convenience method: create a bunch of FitsFiles as Input for Observation 
	 * 						with this obsDates
	 * 
	 * @param destDir
	 * @param srcFile
	 * @param obsDates the obsDates to set in fitsFiles
	 * @return Array of FitsFiles
	 * @throws Exception
	 */
	public static File[] createFitsFiles(File destDir, File srcFile,
			Date[] obsDates) throws Exception {
		File[] createFitsFiles = new File[obsDates.length] ;
		//create destDir
		FileUtils.forceMkdir(destDir);
		//load srcFile
		Fits fits = new Fits(srcFile);
		//loop over dates & write outFiles to destDir
		for (int i = 0; i < obsDates.length; i++) {
			obsDates[i]=DateUtils.truncate(obsDates[i], Calendar.DATE);
			String destPath = destDir.getAbsolutePath()+IOUtils.DIR_SEPARATOR;
			File destFile=new File(destPath
								+ DateFormatUtils.format(obsDates[i], "yyyyMMdd")
								+"_"+srcFile.getName());
			FileUtils.copyFile(srcFile, destFile);
			ToolBox.changeObsDateInFitsFile(destFile.getAbsolutePath(), obsDates[i]);
			createFitsFiles[i]=destFile;
//			test success
			Fits fitsF=new Fits(destFile);
			Date fitsObsDate=fitsF.getHDU(0).getObservationDate();
			fitsObsDate=DateUtils.truncate(fitsObsDate, Calendar.DATE);
			//milleniumbug + oneDay Diff
			if (!fitsObsDate.equals(obsDates[i]) ) {
				
				throw new IOException("fitsObsDate: "+fitsObsDate
						+ "not equal with input: "+obsDates[i]);
			}
		}
		
		return createFitsFiles;
	}

}
