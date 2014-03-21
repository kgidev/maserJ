/**
 * 
 */
package model;

import static common.TEST.DEFAULT_TIMEOUT;
import static common.TEST.random;
import static org.junit.Assert.*;

import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Set;



import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.util.BufferedFile;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.ToolBox;

public class CelestialObjectTest {
	static DataBase db;
	
	static File tmpDir = SystemUtils.getJavaIoTmpDir();

	CelestialObject celestialObject;

	CelestialObject[] celObjects;

	static DBTable celestialObjectsTable;

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
		db.dropTables();
		db.shutdownDB();
		db = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		celestialObjectsTable = new DBTable("CELESTIALOBJECTS");
		// insert TestData rows into UNITS
		int numberOfCelObjs = 1;// random.nextInt(100) + 1;
		celObjects = new CelestialObject[numberOfCelObjs];
		for (int i = 0; i < numberOfCelObjs; i++) {
			HashMap<String, Object> row = randomCelObjRow();
			// celestialObjectsTable.insertMap2DBTable(row);
			celObjects[i] = new CelestialObject(row);
		}
		// fetch celestialObject by random
		celestialObject = celObjects[random.nextInt(numberOfCelObjs)];
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		celestialObjectsTable = null;
		celestialObject = null;
		celObjects = null;
	}

	/**
	 * Test method for
	 * {@link model.CelestialObject#CelestialObject(java.util.HashMap)}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testCelestialObject() {
		// MUT
		HashMap<String, Object> dataMap = celestialObject.getDataMap();
		// the Test
		Set<String> keySet = dataMap.keySet();
		for (String key : keySet) {
			// value ist not empty
			// System.out.println("dataMap.get(key)" + dataMap.get(key));
			assertTrue("value is not Null: ", !dataMap.get(key).equals(null));
		}
	}

	/**
	 * Test method for
	 * {@link model.CelestialObject#CelestialObject(HashMap<String, Object> objectData, boolean dBSynchron)}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testCelestialObjectDbSynchron() throws Exception {
		celestialObjectsTable.deleteRows();
		// MUT
		HashMap<String, Object> dataMap = randomCelObjRow();

		boolean dBSynchron = true;
		celestialObject = new CelestialObject(dataMap, dBSynchron);
		// the tests
		// in DBTable after new Object if(dBSynchron == true)
		Set<String> keySet = dataMap.keySet();
		for (String key : keySet) {
			// value ist not empty
			// System.out.println("dataMap.get(key)" + dataMap.get(key));
			assertTrue("value is not Null: ", !dataMap.get(key).equals(null));
		}
		// in DBTable after new Object if(dBSynchron == true)
		if (dBSynchron == true) {
			HashMap<String, Object> returnMapDB = celestialObjectsTable
					.getRowById(celestialObject.getID());
			assertTrue("dataMap compare to DBTableRow: ", ToolBox
					.compareHashMaps(dataMap, returnMapDB, false));
		}
	}

	// /**
	// * Test method for
	// * {@link model.CelestialObject#CelestialObject(int)}.
	// */
	// @Test//(timeout = DEFAULT_TIMEOUT)
	// public final void testCelestialObjectInt() {
	// // MUT
	// // wrongID
	// int id=Integer.MAX_VALUE;
	// try {
	// celestialObject = new CelestialObject(id);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// // rightID
	// id=celestialObject.getIdentity();
	// try {
	// celestialObject = new CelestialObject(id);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// HashMap<String, Object> dataMap = celestialObject.getDataMap();
	//		
	// // the Test
	// Set<String> keySet = dataMap.keySet();
	// for (String key : keySet) {
	// // value ist not empty
	// // System.out.println("dataMap.get(key)" + dataMap.get(key));
	// assertTrue("value is not Null: ", !dataMap.get(key).equals(null));
	// }
	// }

	/**
	 * Test method for {@link model.CelestialObject#getDataMap()}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testGetSetDataMap() throws Exception {

		HashMap<String, Object>[] rows = celestialObjectsTable.getRows();
		HashMap<String, Object> expectedData = celestialObjectsTable
				.getRowById((Integer) rows[random.nextInt(rows.length)]
						.get("id"));
		// MUT
		celestialObject.setDataMap(expectedData);
		HashMap<String, Object> returnData = celestialObject.getDataMap();
		// the Test
		Set<String> keySet = returnData.keySet();
		for (String key : keySet) {
			// System.out.println("returnData.get(key)" + key + " "
			// + returnData.get(key));
			assertEquals("value: ", expectedData.get(key), returnData.get(key));
		}
		// Tests get-Methods for the known Attributes
		assertEquals("getID: ", expectedData.get("id"), celestialObject.getID());
		assertEquals("getName: ", expectedData.get("name"), celestialObject
				.getName());
		assertEquals("getRightAscension: ", expectedData.get("ra"),
				celestialObject.getRightAscension());
		assertEquals("getValue: ", expectedData.get("dec"), celestialObject
				.getDeclination());

	}

	/**
	 * Test method for {@link model.CelestialObject#link2TableRow(int)}.
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testLink2TableRow() {
		HashMap<String, Object> expectedData = celestialObject.getDataMap();
		// MUT
		HashMap<String, Object> returnData = celestialObject
				.link2TableRow(null);
		expectedData.put("id", returnData.get("id"));
		// the tests
		// ToolBox.dumpHahsMap(expectedData);
		// ToolBox.dumpHahsMap(returnData);
		assertFalse("data is not Null: ", returnData == null);
		assertTrue("compare ObjectData & DBTable.row: ", ToolBox
				.compareHashMaps(expectedData, returnData, false));
	}

	/**
	 * Test method for
	 * {@link model.CelestialObject#getComponentRowByName(java.lang.String)}.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetComponentRowByName() throws Exception {
		HashMap<String, Object> expectedData = celestialObject.getDataMap();
		// MUT
		HashMap<String, Object> dbRow = celestialObject
				.getRowByName((String) expectedData.get("name"));
		// ID is maintained by Database
		expectedData.put("id", dbRow.get("id"));
		ToolBox.dumpHahsMap(expectedData);
		ToolBox.dumpHahsMap(dbRow);
		// the Test
		assertTrue("map: ", ToolBox.compareHashMaps(expectedData, dbRow, false));

	}

	/**
	 * Test method for {@link model.CelestialObject#setObservations()}.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetSetObservations() throws Exception {
		DBTable obsTable = new DBTable("OBSERVATIONS");
		int numberOfObs = random.nextInt(100) + 1;
		HashMap<String, Object> link2TableRow = celestialObject
				.link2TableRow(null);
		celestialObject.setDataMap(link2TableRow);
		ToolBox.insertObservationRows(numberOfObs, obsTable, celestialObject
				.getID(), null);
		HashMap<String, Object>[] rows = obsTable.getRowsOrderByID();
		HashMap<String, String> ts = obsTable.getTableStruct();
		Observation[] observations = new Observation[numberOfObs];
		for (int i = 0; i < observations.length; i++) {
			observations[i] = new Observation(rows[i],false);
		}
		// MUT
		celestialObject.setObseravtions(observations);
		Observation[] returnedObs = celestialObject.getObseravtions();
		// the Test
		for (int i = 0; i < returnedObs.length; i++) {
			// ToolBox.dumpHahsMap(rows[i]);
			// ToolBox.dumpHahsMap(returnedObs[i].getDataMap());
			assertTrue("map: ", ToolBox.compareHashMaps(rows[i], returnedObs[i]
					.getDataMap(), false));
		}
	}

	/**
	 * Test method for
	 * {@link model.CelestialObject#dataFromFitsFile(File fitsFile)}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testdataFromFitsFile() throws Exception {
		int testRuns=100;
		for (int i = 0; i < testRuns; i++) {
			HashMap<String, Object> dataFromFitsFile = new HashMap<String, Object>();
			String name = "ObjectName"+i;
			Double ra = random.nextDouble() * 360;
			Double dec = random.nextDouble() * 90;
			String epoch = "1950.0";
			dataFromFitsFile.put("name", name);
			dataFromFitsFile.put("ra", ra);
			dataFromFitsFile.put("dec", dec);
			dataFromFitsFile.put("epoch", epoch);
			HashMap<String, Object> expectedData = (HashMap<String, Object>) dataFromFitsFile
					.clone();
			// change Fits-Content
			String fitsPath = FilenameUtils
					.separatorsToSystem("test/examples/effelsbergSample.fits");
			File fitsFile = new File(fitsPath);
			Fits fits = new Fits(fitsFile);
			BasicHDU hdu = fits.readHDU();
			Header header = hdu.getHeader();
			header.addValue("CRVAL2", ra, "RA-Value");
			header.addValue("CRVAL3", dec, "DEC-Value");
			header.addValue("OBJECT", name, "OBJECT-Value");
			header.addValue("EQUINOX", epoch, "EQUINOX-Value");
			String imagePath = FilenameUtils.separatorsToSystem(tmpDir
					.getCanonicalPath()
					+ "/" + ToolBox.getCurrentMethodName() + ".fits");
			File testFitsFile= new File(imagePath);
			BufferedFile dos = new BufferedFile(imagePath, "rw");
			fits.write(dos);
			// MUT
			dataFromFitsFile = CelestialObject.dataFromFitsFile(testFitsFile);
			// the test
			assertTrue("compare maps: ", ToolBox.compareHashMaps(expectedData,
					dataFromFitsFile, false));
			//cleanup
			FileUtils.forceDeleteOnExit(testFitsFile);
		}		
	}

	/**
	 * convenience method: create Random-Row
	 * 
	 * @throws Exception
	 * 
	 */
	public static HashMap<String, Object> randomCelObjRow() throws Exception {
		celestialObjectsTable = new DBTable("CELESTIALOBJECTS");
		CaseInsensitiveMap noCaseMap = new CaseInsensitiveMap(ToolBox
				.randomRow(celestialObjectsTable.getTableStruct()));
		noCaseMap.remove("id");
		HashMap<String, Object> randomCelObjRow = new HashMap<String, Object>(
				noCaseMap);
		return randomCelObjRow;// ToolBox.randomRow(celestialObjectsTable.getTableStruct());
	}

	//	
	// public static HashMap<String, Object> randomCelObjRow() throws Exception
	// {
	// celestialObjectsTable = new DBTable("CELESTIALOBJECTS");
	// HashMap<String, Object> randomCelObjRow =
	// new HashMap<String, Object>(new CaseInsensitiveMap(
	// ToolBox.randomRow(celestialObjectsTable.getTableStruct())));
	// return randomCelObjRow;
	// }

}
