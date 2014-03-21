/**
 * 
 */
package model;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static common.TEST.DEFAULT_TIMEOUT;
import static common.TEST.USERDIR;
import tools.CrossPlatform;
import tools.ToolBox;

public class DataBaseTest {
	DataBase dataBase;

	String[] tables = { "COMPONENTS", "OBSERVATIONS", "CELESTIALOBJECTS", "UNITS" };
	static String dbURL = common.TEST.dbURL;
	static String dbFile = common.TEST.dbFile;
	static String usr = "sa";
	static String pwd = "";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dataBase = new DataBase(false,dbURL,dbFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		try {
			ResultSet rsDrop = dataBase.dropTables();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link model.DataBase#DBConnect()}.
	 *FIXME  works only if DB is up!? java.sql.SQLException: socket creation error
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testStartDBServer() throws Exception {
		
		Class.forName("org.hsqldb.jdbcDriver").newInstance();

		// insertTestData();
		// MUT
		try {
			Connection conn2 = dataBase.DBConnect(dbURL, usr, pwd);

		} catch (Exception e1) {
			 System.out.println(e1);
			// dataBase.shutdownDB();
		}
		dataBase.startDBSever();
	
		// test Server alive
		Connection conn = null;
		try {
			conn = dataBase.DBConnect(dbURL, usr, pwd);
			// System.out.println("conn.getMetaData():
			// "+conn.getMetaData().getURL());
			// System.out.println("conn.getCatalog(): "+conn.getCatalog());
			// System.out.println("conn.toString(): "+conn.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
//		System.out.println("testStartDBServer() dataBase.server.getErrWriter() "+dataBase.server.getErrWriter());
//		System.out.println("testStartDBServer() dataBase.server.getLogWriter() "+dataBase.server.getLogWriter().toString());
		assertEquals("DB-URL: ", dbURL, conn.getMetaData().getURL());
		// cleanUp
		// dataBase.shutdownDB();
	}

	/**
	 * Test method for {@link model.DataBase#DBConnect()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// , expected = SQLException.class)
	public final void testDbConnect() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver").newInstance();
		// MUT
		//DataBase dataBase = new DataBase();
		// dataBase.closeDB();
		Connection conn = dataBase.DBConnect(dbURL, usr, pwd);

		// tests
		assertEquals("dbURL: ", dbURL, conn.getMetaData().getURL());
		dataBase.closeDB();
	}

	/**
	 * Test method for {@link model.DataBase#DBConnect()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testExecuteSQL() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver").newInstance();
		//TODO test SELECT,CREATE, DROP, INSERT and UPDATE
		ResultSet rs;
		//dataBase = new DataBase();
		String testQuery = " DROP TABLE Test IF EXISTS; "
				+ " CREATE TABLE Test(ID INTEGER PRIMARY KEY, FirstName VARCHAR(20), "
				+ "Name VARCHAR(50), ZIP INTEGER) ; "
				+ "INSERT INTO Test VALUES(10,'Julia','Peterson-ClancyD',10) ;"
				+ "UPDATE Test SET Name='Hans' WHERE ID=10 ;"
				+ "SELECT * FROM Test WHERE ID=10 ;";
		//				
		// MUT
		dataBase.DBConnect(dbURL, usr, pwd);
		rs = dataBase.executeSQL(testQuery);
		while (rs.next()) {
			// System.out.println(rs.getMetaData());
			// System.out.println("rs.getInt(1)"+rs.getInt(1));
			// System.out.println("rs.getString(2)"+rs.getString(2));
			// tests
			assertEquals("ID: ", 10, rs.getInt("ID"));
			assertEquals("FirstName: ", "Julia", rs.getString("FirstName"));
			assertEquals("Name: ", "Hans", rs.getString("Name"));
			assertEquals("ZIP: ", 10, rs.getInt("ZIP"));
		}
	}

	/**
	 * Test method for {@link model.DataBase#createTables()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// , expected = SQLException.class)
	public final void testCreateTables() throws Exception {
		dataBase.DBConnect(dbURL, usr, pwd);
		// MUT
		ResultSet rsCreate = dataBase.createTables(dataBase.createTablesSQL);
		// tests
		// CELESTIALOBJECTS-Table
		ResultSet rsInsert = dataBase
				.executeSQL("INSERT INTO CELESTIALOBJECTS (ID,name) VALUES (1,'Groeni')");
		ResultSet rsSelect = dataBase.executeSQL("select * from CELESTIALOBJECTS");
		while (rsSelect.next()) {
			// System.out.println(rsSelect.getMetaData());
			assertEquals("ID: ", 1, rsSelect.getInt("ID"));
			assertEquals("name: ", "Groeni", rsSelect.getString("name"));
		}
		// observation-Table
		rsInsert = dataBase.executeSQL("INSERT INTO OBSERVATIONS VALUES (1,1,'"
				+ "instrument','1970-01-01',null,null)");
		rsSelect = dataBase.executeSQL("select * from OBSERVATIONS");
		while (rsSelect.next()) {
			// System.out.println(rsSelect.getMetaData());
			assertEquals("ID: ", 1, rsSelect.getInt("id"));
			assertEquals("ID: ", 1, rsSelect.getInt("object_id"));
			assertEquals("Object: ", "instrument", rsSelect
					.getString("instrument"));
		}
		//provoke unique (object_id,date) violation
		try {
			rsInsert = dataBase.executeSQL("INSERT INTO OBSERVATIONS VALUES (2,1,"
					+ "'instrument','1970-01-01',null,null)");
		} catch (SQLException e) {
			if(!(e.getMessage().startsWith("Violation of unique constraint"))) {
			e.printStackTrace();
			throw e;
			}
		}
		
		// observation-Components
		rsInsert = dataBase.executeSQL("INSERT INTO COMPONENTS VALUES (1,1,"
				+ "'name',null,null,null,null,null)");
		rsSelect = dataBase.executeSQL("select * from COMPONENTS");
		// dataBase.dump(rsSelect);
		while (rsSelect.next()) {
			// System.out.println(rsSelect.getMetaData());
			assertEquals("ID: ", 1, rsSelect.getInt("ID"));
			assertEquals("ID: ", 1, rsSelect.getInt("observation_ID"));
			assertEquals("Object: ", "name", rsSelect.getString("name"));
		}
		// units-Table
		rsInsert = dataBase
				.executeSQL("INSERT INTO UNITS VALUES (1,'unitname',"
						+ "'m/s')");
		rsSelect = dataBase.executeSQL("select * from UNITS");
		// dataBase.dump(rsSelect);
		while (rsSelect.next()) {
			// System.out.println(rsSelect.getMetaData());
			assertEquals("ID: ", 1, rsSelect.getInt("ID"));
			assertEquals("Name: ", "unitname", rsSelect.getString("name"));
			assertEquals("Value: ", "m/s", rsSelect.getString("value"));
		}
	}

	/**
	 * Test method for {@link model.DataBase#DBConnect()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testClearTables() throws Exception {
		dataBase.DBConnect(dbURL, usr, pwd);
		dataBase.createTables(dataBase.createTablesSQL);
		insertTestData();
		// MUT
		dataBase.clearTables();
		for (int i = 0; i < tables.length; i++) {
			String sqlCmd = "select count(*) from " + tables[i] + ";";
			ResultSet rs = dataBase.executeSQL(sqlCmd);
			while (rs.next()) {
				// System.out.println(rs.getMetaData());
				// tests
				assertEquals("count: ", 0, rs.getInt(1));
			}
		}
		// dataBase.shutdownDB();
	}

	/**
	 * Test method for {@link model.DataBase#startDBintern()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testStartDBintern() throws Exception {
	
		// MUT
		try {
			dataBase.startDBintern();
		} catch (Exception e) {
			// TODO better catch
			System.out.println(e);
//			e.printStackTrace();
		}
		dataBase.dropTables();
		dataBase.createTables(dataBase.createTablesSQL);
		insertTestData();
		// tests
		ResultSet rsSelect = dataBase.executeSQL("select * from CELESTIALOBJECTS");
		while (rsSelect.next()) {
			// System.out.println(rsSelect.getMetaData());
			assertEquals("ID: ", 0, rsSelect.getInt("ID"));
		}
	}
	
	/**
	 * Test method for {@link model.DataBase#ddlScript()}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testDdlScript() throws Exception {
		String createTable="CREATE MEMORY TABLE TESTTABLE(ID INTEGER GENERATED BY " +
				"DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY," +
				"NAME VARCHAR(255),VALUE VARCHAR(255))";
		dataBase.DBConnect(dbURL, usr, pwd);
//		 MUT
		ResultSet rsCreate = dataBase.createTables(new String[] {createTable});
		String returnVal=dataBase.ddlScript();
//		System.out.println("DDL:"+returnVal);
		//the Test
		assertTrue("DDL contains create: ", StringUtils.contains(returnVal, createTable));
	}

	/**
	 * Test method for {@link model.DataBase#DBConnect()}.
	 * 
	 * @throws Exception
	 */
//	@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// , expected = SQLException.class)
	public final void testShutdownDB() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver").newInstance();
		// MUT
		Connection conn = dataBase.DBConnect(dbURL, usr, pwd);
		dataBase.shutdownDB();
		try {
			dataBase.DBConnect(dbURL, usr, pwd);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// tests
			assertEquals("dbURL no Connect: ", "socket creation error", e
					.getMessage());
			// e.printStackTrace();
		}

	}
	
	/**
	 * Test method for {@link model.DataBase#testAllSqlTypes()}.
	 * 
	 * @throws Exception
	 */
//	@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// , expected = SQLException.class)
	public final void testAllSqlTypes() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver").newInstance();
		// MUT
		dataBase.allSqlTypes(dataBase.dbAlias);
	}

	/**
	 * Test method for {@link model.DataBase#sqlTypes2JavaClasses()}.
	 * 
	 * @throws Exception
	 */
//	@Ignore
	@Test//(timeout = DEFAULT_TIMEOUT)
	// , expected = SQLException.class)
	public final void testSqlTypes2JavaClasses() throws Exception {
		Class.forName("org.hsqldb.jdbcDriver").newInstance();
		// MUT
		HashMap<String,Object> returnMap =dataBase.sqlTypes2JavaClasses();
		//the Test
		ToolBox.dumpHahsMap(returnMap);
	}
	
	
	/**
	 * convinience Method for Data-Insert
	 * 
	 * @throws SQLException
	 */
	void insertTestData() throws SQLException {
		for (int i = 0; i < tables.length; i++) {
			String sqlCmd = "INSERT INTO " + tables[i] + " (ID) VALUES(null);";
			dataBase.executeSQL(sqlCmd);
		}
	}

}
