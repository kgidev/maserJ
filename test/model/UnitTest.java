/**
 * 
 */
package model;

import static common.TEST.DEFAULT_TIMEOUT;
import static common.TEST.random;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.ToolBox;

public class UnitTest {
	static DataBase db;
	Unit unit;
	DBTable unitsTable;
	
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
	//	db.shutdownDB();
		db = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		unitsTable = new DBTable("UNITS");
		// insert TestData rows into UNITS
		int numberOfUnits = random.nextInt(100) + 1;
		
		// System.out.println("typeNumber: "+MyMath.isNumeric(((Object)number)));
		for (int i = 0; i < numberOfUnits; i++) {
			HashMap<String, Object> row = randomUnitRow();
			unitsTable.insertMap2DBTable(row);
		}
		int index=random.nextInt(numberOfUnits) + 1;
		HashMap<String, Object>[] rows = unitsTable.getRows();
		unit = new Unit(rows[index]);// fetch Radom TableRow
	}


	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		unitsTable = null;
		unit = null;
	}

	/**
	 * Test method for {@link model.Unit#Unit(java.util.HashMap)}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testUnit() {
		// MUT
		HashMap<String, Object> dataMap = unit.getDataMap();
		// the Test
		Set<String> keySet = dataMap.keySet();
		for (String key : keySet) {
			// value ist not empty
//			System.out.println("dataMap.get(key)" + dataMap.get(key));
			assertTrue("value is not Null: ", !dataMap.get(key).equals(null));
		}
	}

	/**
	 * Test method for {@link model.Unit#getDataMap()}.
	 * @throws Exception 
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetSetDataMap() throws Exception {
		HashMap<String, Object>[] rows = unitsTable.getRows();
		HashMap<String, Object> expectedData = rows[random
				.nextInt(rows.length) + 1];
		// MUT
		unit.setDataMap(expectedData);
		HashMap<String, Object> returnData = unit.getDataMap();
		// the Test
		Set<String> keySet = returnData.keySet();
		for (String key : keySet) {
//			System.out.println("returnData.get(key)" + key + " "
//					+ returnData.get(key));
			assertEquals("value: ", expectedData.get(key), returnData.get(key));
		}
		// Tests get-Methods for the known Attributes
		assertEquals("getID: ", expectedData.get("id"), unit.getID());
		assertEquals("getName: ", expectedData.get("name"), unit.getName());
		assertEquals("getValue: ", expectedData.get("value"), unit
				.getValue());
	}

	/**
	 * convenience method: create Random-Component Row
	 * 
	 * @throws Exception
	 * 
	 */
	HashMap<String, Object> randomUnitRow() throws Exception {
		return ToolBox.randomRow(unitsTable.getTableStruct());
	}
	
}
