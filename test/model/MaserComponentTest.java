/**
 * 
 */
package model;

import static common.TEST.DEFAULT_TIMEOUT;
import static common.TEST.random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import tools.MyMath;
import tools.ToolBox;

public class MaserComponentTest {
	static DataBase db;

	static Integer observationId;

	static MaserComponent maserComponent;

	static DBTable compTable;

	private Observation expectedObservation;

	private int expectedObservationId;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new DataBase(true, common.TEST.dbURL, common.TEST.dbFile);
		// write TestData to DB: 1 object,1 Observation
		Date obsDate = new Date(0);
		int objectId = 1;
		observationId = Math.abs(random.nextInt());
		;
		String sqlCmd = " DELETE FROM COMPONENTS;";
		sqlCmd += " DELETE FROM OBSERVATIONS;";
		sqlCmd += " DELETE FROM COMPONENTS;";
		sqlCmd += " INSERT INTO CELESTIALOBJECTS (id,name) VALUES (1,'ObjectName');";
		sqlCmd += " INSERT INTO OBSERVATIONS (id,date,object_id) VALUES ('"
				+ observationId + "','" + obsDate + "'," + objectId + ");";
		// ??components
		db.executeSQL(sqlCmd);
	}

	@Before
	public void setUp() throws Exception {
		compTable = new DBTable("Components");
		// insert TestData rows into Components
		int numberOfComponents = random.nextInt(100) + 1;
		//System.out.println("typeNumber: "+MyMath.isNumeric(((Object)number)));
		HashMap<String, Object> obsData = ObservationTest.randomObservationData();
		obsData.put("id", observationId);
		expectedObservation = new Observation(obsData,false);
		expectedObservationId = expectedObservation.getID();
		observationId=expectedObservationId;
		for (int i = 0; i < numberOfComponents; i++) {
			HashMap<String, Object> row = randomComponentRow(null, null, null, null,null);
			row.put("observation_id", observationId);
			compTable.insertMap2DBTable(row);
		}
//		 fetch Radom TableRow
		maserComponent = new MaserComponent(compTable.getRowById(random
				.nextInt(numberOfComponents) + 1),expectedObservation,false);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.dropTables();
		db.closeDB();
		//db.shutdownDB();
		db = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		compTable = null;
		maserComponent = null;
	}

	/**
	 * Test method for {@link model.MaserComponent#Component(int, HashMap)}.
	 */
//	 @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testMasetComponent() {
		// MUT
		HashMap<String, Object> dataMap = maserComponent.getDataMap();
		// the Test
		Set<String> keySet = dataMap.keySet();
		for (String key : keySet) {
			// value ist not empty
//		System.out.println("dataMap.get(key)" + dataMap.get(key));
			assertTrue("value is not Null: ", !dataMap.get(key).equals(null));
		}

	}

	/**
	 * Test method for {@link model.MaserComponent#setDataMap(java.util.HashMap)}.
	 * 
	 * @throws Exception
	 */
//	 @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetSetDataMap() throws Exception {
		HashMap<String, Object>[] rows = compTable.getRows();
		HashMap<String, Object> expectedData = compTable.getRowById(random
				.nextInt(rows.length) + 1);
		// MUT
		maserComponent.setDataMap(expectedData);
		HashMap<String, Object> returnData = maserComponent.getDataMap();
		// the Test
		Set<String> keySet = returnData.keySet();
		for (String key : keySet) {
//			System.out.println("returnData.get(key)" + key + " "
//					+ returnData.get(key));
			assertEquals("value: ", expectedData.get(key), returnData.get(key));
		}
		// Tests get-Methods for the known Attributes
		assertEquals("getID: ", expectedData.get("ID"), maserComponent.getID());
		assertEquals("getObservationId: ", expectedData.get("observation_id"),
				maserComponent.getObservation().getID());
		assertEquals("getName: ", expectedData.get("NAME"), maserComponent.getName());
		assertEquals("getXOffset: ", expectedData.get("XOFFSET"), maserComponent
				.getXOffset());
		assertEquals("getYOffset: ", expectedData.get("YOFFSET"), maserComponent
				.getYOffset());
		assertEquals("getVelocity: ", expectedData.get("VELOCITY"), maserComponent
				.getVelocity());
		assertEquals("getBrightness: ", expectedData.get("BRIGHTNESS"),
				maserComponent.getBrightness());
	}

	/**
	 * Test method for {@link model.MaserComponent#getObservation()}.
	 * @throws Exception 
	 */
//	 @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetSetObservatio() throws Exception {
		
	
		
		
		// MUT
		maserComponent.setObservation(expectedObservation);
		int returnObservationId = maserComponent.getObservation().getID();
		// the Test
		assertEquals("setObservationId: ", expectedObservationId,
				returnObservationId);
	}

	/**
	 * Test method for {@link model.MaserComponent#getDBTableRow(int)}.
	 * 
	 * @throws Exception
	 */
//	 @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetMaserComponentRowByUniqueKey() throws Exception {
		HashMap<String, Object>[] rows = compTable.getRows();

		for (int i = 0; i < rows.length; i++) {

			int expectedObservationID = (Integer) rows[i].get("observation_id");
			String expectedName = (String) rows[i].get("NAME");
			// MUT
			HashMap<String, Object> compRow = maserComponent
					.getComponentRowByUniqueKey(expectedObservationID,
							expectedName);
			// the Test
			assertEquals("compRow observation_id: ", expectedObservationID,
					(Integer) compRow.get("observation_id"));
			assertEquals("compRow Name: ", expectedName, (String) compRow
					.get("NAME"));
		}
	}

	/**
	 * convenience method: create Random-Component Row
	 * 
	 * @throws Exception
	 * 
	 */
	public static HashMap<String, Object> randomComponentRow() throws Exception {
		return randomComponentRow(null, null, null, null,null);
	}

	/**
	 * convenience method: create Random-Component Row
	 * @param xRange TODO
	 * @param yRange TODO
	 * @param vRange TODO
	 * @param iRange TODO
	 * 
	 * @throws Exception
	 * 
	 */
	static public HashMap<String, Object> randomComponentRow(Double[] xRange, Double[] yRange,
			Double[] vRange, Double[] iRange,Double[]  bRange) throws Exception {
		DBTable compTable=new DBTable("Components");
		HashMap<String, Object> compData = ToolBox.randomRow(compTable.getTableStruct());
		if (xRange!=null) {
			Double xOffset=(Double)MyMath.randomValueByRange(
									Double.class, xRange[0], xRange[1], '*');
			compData.put("XOFFSET", xOffset);
		}
		if (yRange!=null) {
			Double yOffset=(Double)MyMath.randomValueByRange(
									Double.class, yRange[0], yRange[1], '*');
			compData.put("YOFFSET", yOffset);
		}
		if (vRange!=null) {
			Double velocity=(Double)MyMath.randomValueByRange(
									Double.class, vRange[0], vRange[1], '*');
			compData.put("VELOCITY", velocity);
		}
		if (iRange!=null) {
			Double intensity=(Double)MyMath.randomValueByRange(
									Double.class, iRange[0], iRange[1], '*');
			compData.put("INTENSITY", intensity);
		}
		if (bRange!=null) {
			Double brightness=(Double)MyMath.randomValueByRange(
									Double.class, bRange[0], bRange[1], '*');
			compData.put("BRIGHTNESS", brightness);
		}
		
		return new HashMap<String, Object> (new CaseInsensitiveMap(compData));
	}

}
