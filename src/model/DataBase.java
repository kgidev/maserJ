/**
 * 
 */
package model;

import static common.TEST.USERDIR;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hsqldb.Server;

import common.TEST;

import controller.MaserJ;

import tools.CrossPlatform;
import tools.ToolBox;

/**
 */
public class DataBase {
	static Map sqlTypeNamesMap;

	static int[] notSupportedSQLTypesInHSQLDB = { 0, 70, 2000, 2001, 2002,
			2003, 2004, 2005, 2006 };

	static final Map JdbcTypes2javaClass = mapSqlTypes2JavaClass();

	public static void dump(ResultSet rs) throws SQLException {
		ResultSet localRs = rs;
		// the order of the rows in a cursor
		// are implementation dependent unless you use the SQL ORDER statement
		ResultSetMetaData meta = localRs.getMetaData();
		int colmax = meta.getColumnCount();
		int i;
		Object o = null;

		// the result set is a cursor into the data. You can only
		// point to one row at a time
		// assume we are pointing to BEFORE the first row
		// rs.next() points to next row and returns true
		// or false if there is no next row, which breaks the loop
		for (; rs.next();) {
			for (i = 0; i < colmax; ++i) {
				o = (Object) localRs.getObject(i + 1); // Is SQL the first
				// column is
				// indexed
				// with 1 not 0
				System.out.print(meta.getColumnName(i + 1) + ": ");
				System.out.print(o.toString() + " ");
			}

			System.out.println(" ");

		}
		rs.beforeFirst();
	}

	// This method returns the name of a JDBC type.
	// Returns null if jdbcType is not recognized.
	static String getJdbcTypeName(int jdbcType) {
		// Use reflection to populate a map of int values to names
		if (sqlTypeNamesMap == null) {
			sqlTypeNamesMap = new HashMap();

			// Get all field in java.sql.Types
			Field[] fields = java.sql.Types.class.getFields();
			for (int i = 0; i < fields.length; i++) {
				try {
					// Get field name
					String name = fields[i].getName();

					// Get field value
					Integer value = (Integer) fields[i].get(null);

					// Add to map
					sqlTypeNamesMap.put(value, name);
				} catch (IllegalAccessException e) {
				}
			}
		}

		// Return the JDBC type name
		return (String) sqlTypeNamesMap.get(new Integer(jdbcType));
	}

	/**
	 * Listing Available SQL Types Used by Database
	 * 
	 */
	public static HashMap<String, Class> mapSqlTypes2JavaClass() {
		HashMap<String, Class> mapSqlTypes2JavaClass = new HashMap<String, Class>();

		mapSqlTypes2JavaClass.put("BINARY", Byte[].class);
		mapSqlTypes2JavaClass.put("TINYINT", Byte.class);
		mapSqlTypes2JavaClass.put("SMALLINT", Short.class);
		mapSqlTypes2JavaClass.put("DOUBLE", Double.class);
		mapSqlTypes2JavaClass.put("TIMESTAMP", java.sql.Timestamp.class);
		mapSqlTypes2JavaClass.put("VARBINARY", byte[].class);
		mapSqlTypes2JavaClass.put("OTHER", Object.class);
		mapSqlTypes2JavaClass.put("LONGVARBINARY", byte[].class);
		mapSqlTypes2JavaClass.put("DECIMAL", java.math.BigDecimal.class);
		mapSqlTypes2JavaClass.put("TIME", java.sql.Time.class);
		mapSqlTypes2JavaClass.put("INTEGER", Integer.class);
		mapSqlTypes2JavaClass.put("LONGVARCHAR", String.class);
		mapSqlTypes2JavaClass.put("DATE", java.sql.Date.class);
		mapSqlTypes2JavaClass.put("FLOAT", Double.class);
		mapSqlTypes2JavaClass.put("NUMERIC", java.math.BigDecimal.class);
		mapSqlTypes2JavaClass.put("CHAR", String.class);
		mapSqlTypes2JavaClass.put("VARCHAR", String.class);
		mapSqlTypes2JavaClass.put("VARCHAR_IGNORECASE", String.class);
		mapSqlTypes2JavaClass.put("BOOLEAN", Boolean.class);
		mapSqlTypes2JavaClass.put("BIT", Boolean.class);
		mapSqlTypes2JavaClass.put("REAL", Double.class);
		mapSqlTypes2JavaClass.put("BIGINT", Long.class);

		return mapSqlTypes2JavaClass;
	}

	String[] dbModes = { "Server", "Standalone" };

	String dbMode = "Server";

	Connection conn;

	String dataBaseURL = "jdbc:hsqldb:hsql://localhost/observations";

	String dataBaseFile = "jdbc:hsqldb:file:lib/hsqldb/DB/observations";

	String dbAlias = "observations";

	String user = "sa";

	String password = "";
	
//	 ?? DEC as Double ?
	String createCelestialObjects = "CREATE TABLE CELESTIALOBJECTS" + "("
			+ "ID INTEGER IDENTITY PRIMARY KEY, " + "NAME VARCHAR(255), "
			+ "RA DOUBLE, " + "DEC DOUBLE, " + "EPOCH VARCHAR(255)"
			+ ");";

	// ?? date unique?
	String createObservations = "CREATE TABLE OBSERVATIONS " + "("
			+ "ID INTEGER IDENTITY PRIMARY KEY , " + "OBJECT_ID INTEGER,"
			+ "instrument VARCHAR(255)," + "date DATE," + "noise DOUBLE,"
			+ "interpolated BOOLEAN,"
			+ "FOREIGN KEY (OBJECT_ID) REFERENCES CELESTIALOBJECTS(ID),"
			+ "UNIQUE (OBJECT_ID,date) " + ");";

	String createComponents = "CREATE TABLE COMPONENTS " + "("
			+ "ID INTEGER IDENTITY PRIMARY KEY, " + "OBSERVATION_ID INTEGER,"
			+ "name VARCHAR(255)," + "Xoffset DOUBLE," + "Yoffset DOUBLE,"
			+ "intensity DOUBLE, " + "velocity DOUBLE," + "brightness DOUBLE,"
			+ "FOREIGN KEY (OBSERVATION_ID) REFERENCES OBSERVATIONS(ID)"+ ");";
			//+ "UNIQUE (OBSERVATION_ID,name) " 

	String createUnits = "CREATE TABLE UNITS " + "("
			+ "ID INTEGER IDENTITY PRIMARY KEY, " + "name VARCHAR(255),"
			+ "value VARCHAR(255)" + ");";

	String[] createTablesSQL = { createCelestialObjects, createObservations,
			createComponents, createUnits };

	/**
	 * standart Constuctor
	 * 
	 */
	public DataBase() {
		super();
		if (MaserJ.APP_MODE != "PROD") { // set Values for TestDB
			setDataBaseFile(getDataBaseFile() + TEST.testDBsuffix);
			setDataBaseURL(getDataBaseURL() + TEST.testDBsuffix);
			setDbAlias(getDbAlias() + TEST.testDBsuffix);
			getJdbcTypeName(1);
		}
	}

	/**
	 * @throws Exception
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws Exception
	 * @param createDB
	 *            to build or not to build Tables
	 * 
	 */
	public DataBase(boolean createDB) throws IllegalAccessException,
			ClassNotFoundException, Exception {
		this(createDB, null, null);
	}

	/**
	 * @throws Exception
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws Exception
	 * @param createDB
	 *            to build or not to build Tables
	 * @param dbURL
	 *            TODO
	 * 
	 */
	public DataBase(boolean createDB, String dbURL, String dbFile)
			throws IllegalAccessException, ClassNotFoundException, Exception {
		this();
		setDataBaseURL(dbURL);
		setDataBaseFile(dbFile);
		startDB(dbMode);// ?? Condition?
		Connection conn = DBConnect(dataBaseURL, user, password);
		if (createDB) {
			dropTables();
			createTables(createTablesSQL);
		}
	}

	/**
	 * Listing Available SQL Types Used by a Database
	 * 
	 * @param dbAlias
	 *            the dbAlias to set
	 */
	public void allSqlTypes(String dbAlias) {
		try {
			// Get database meta data
			DatabaseMetaData dbmd = conn.getMetaData();

			// Get type info
			ResultSet resultSet = dbmd.getTypeInfo();

			// Retrieve type info from the result set
			while (resultSet.next()) {
				// Get the database-specific type name
				String typeName = resultSet.getString("TYPE_NAME");
				System.out.println("typeName: " + typeName);

				// Get the java.sql.Types type to which this database-specific
				// type is mapped
				short dataType = resultSet.getShort("DATA_TYPE");
				// System.out.println("dataType: "+dataType);

				// Get the name of the java.sql.Types value.
				String jdbcTypeName = getJdbcTypeName(dataType);
				System.out.println("jdbcTypeName: " + dataType);
			}

		} catch (SQLException e) {
		}
	}

	/**
	 * deletes the MaserJ DB-Tables-Content
	 * 
	 * @throws SQLException
	 */
	public ResultSet clearTables() throws SQLException {
		String sqlCMD = "DELETE FROM COMPONENTS; "
				+ "DELETE FROM OBSERVATIONS; "
				+ "DELETE FROM CELESTIALOBJECTS; " + "DELETE FROM UNITS ;";
		ResultSet rsDelete = executeSQL(sqlCMD);
		return rsDelete;
	}

	/**
	 * Close DB-Connection
	 * 
	 * @throws SQLException
	 */
	public void closeDB() throws SQLException {
		Statement stmt = conn.createStatement();
		// stmt.execute("SHUTDOWN");
		conn.close();
	}

	/**
	 * create the MaserJ DB-Tables
	 * 
	 * @throws SQLException
	 */
	protected ResultSet createTables(String[] creates) throws SQLException {
		String createCMD = "";
		// TODO loop over DB-Tables ?? Config-propertys?
		for (int i = 0; i < creates.length; i++) {
			createCMD += creates[i];
		}
		return executeSQL(createCMD);
	}

	/**
	 * @return the Connection to DB
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	protected Connection DBConnect(String dbUrl, String usr, String pwd)
			throws Exception, IllegalAccessException, ClassNotFoundException {
		Class.forName("org.hsqldb.jdbcDriver").newInstance();
		conn = DriverManager.getConnection(dbUrl, usr, pwd);
		return conn;
	}

	/**
	 * get the hole DataBase as SQL-Script
	 * 
	 * @throws SQLException
	 */
	protected String ddlScript() throws SQLException {
		String resultScript = "";
		ResultSet rsDDL = this.executeSQL("script;");
		// System.out.println("rsDDL"+rsDDL.getMetaData());
		while (rsDDL.next())
			resultScript += rsDDL.getString("COMMAND") + "\n";
		return resultScript;
	}

	/**
	 * drops the MaserJ DB-Tables
	 * 
	 * @throws SQLException
	 *             TODO getAllTables and drop them !
	 */
	public ResultSet dropTables() throws SQLException {
		String dropTables = "DROP TABLE COMPONENTS IF EXISTS; "
				+ "DROP TABLE OBSERVATIONS IF EXISTS; "
				+ "DROP TABLE CELESTIALOBJECTS IF EXISTS; "
				+ "DROP TABLE UNITS IF EXISTS;";
		dropTables = "DROP SCHEMA PUBLIC CASCADE;"; // harder
		ResultSet rsDrop = executeSQL(dropTables);
		return rsDrop;
	}

	/**
	 * 
	 * @param sqlCmd
	 *            sql-coomand to execute
	 * @return the resultSet of the sqlCMD
	 * @throws SQLException
	 */
	public ResultSet executeSQL(String sqlCmd) throws SQLException {
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		// ResultSet rsStmt = stmt.executeQuery(sqlCmd);
		ResultSet rsStmt = query(sqlCmd);
		// stmt.close();
		return rsStmt;
	}

	protected void finalize() throws SQLException {
		conn.close();
	}

	/**
	 * @return the dataBaseFile
	 */
	String getDataBaseFile() {
		return dataBaseFile;
	}

	/**
	 * @return the dataBaseURL
	 */
	String getDataBaseURL() {
		return dataBaseURL;
	}

	/**
	 * @return the dbAlias
	 */
	public String getDbAlias() {
		return dbAlias;
	}

	/**
	 * @return the dbMode
	 */
	protected String getDbMode() {
		return dbMode;
	}

	/**
	 * @return the dbMode
	 */
	public synchronized ResultSet query(String expression) throws SQLException {
		return  query(expression,true);
	}
	
	public synchronized ResultSet query(String expression,boolean closeSt) throws SQLException {

		Statement st = null;
		ResultSet rs = null;

		st = conn.createStatement(); // statement objects can be reused with

		// repeated calls to execute but we
		// choose to make a new one each time
		rs = st.executeQuery(expression); // run the query

		if (closeSt) {
			st.close(); // NOTE!! if you close a statement the associated ResultSet
			// is
		}		
		// closed too
		// so you should copy the contents to some other object.
		// the result set is invalidated also if you recycle an Statement
		// and try to execute some other query before the result set has been
		// completely examined.
		return rs;
	}

	/**
	 * @param dataBaseFile
	 *            the dataBaseFile to set
	 */
	void setDataBaseFile(String dataBaseFile) {
		this.dataBaseFile = dataBaseFile;
	}

	/**
	 * @param dataBaseURL
	 *            the dataBaseURL to set
	 */
	void setDataBaseURL(String dataBaseURL) {
		this.dataBaseURL = dataBaseURL;
	}

	/**
	 * @param dbAlias
	 *            the dbAlias to set
	 */
	public void setDbAlias(String dbAlias) {
		this.dbAlias = dbAlias;
	}

	/**
	 * SHUTDOWN DB
	 * 
	 * @throws SQLException
	 *             TODO wait max 25sec for graceFull Shutdown,then kill
	 *             process(ID)/thread ?
	 */
	public void shutdownDB() throws Exception {
//		System.out.println(ToolBox.getCurrentMethodName());
		Statement stmt = conn.createStatement();
		stmt.execute("SHUTDOWN;");
		int timeOut = 1000;
		System.out.println("ToolBox.getCurrentMethodName() wait " + timeOut
				+ " milliSec" + " for DB-Shutdown.");
		Thread.sleep(timeOut);
		conn.close();
	}

	/**
	 * create table with all supported sqlTypes
	 * 
	 * @return map of all corresponding Key:SqlTypeNames and Values:Java-Objects
	 * @throws Exception
	 */
	protected HashMap<String, Object> sqlTypes2JavaClasses() throws Exception {
		HashMap<String, Object> sqlTypes2JavaClasses = null;

		Set<Integer> sqlTypeNumbers = sqlTypeNamesMap.keySet();
		String dropTable = "DROP TABLE ALLSQLTYPES IF EXISTS;";
		String createAllsqlTypes = "CREATE TABLE ALLSQLTYPES " + "(";
		for (Integer typeNumber : sqlTypeNumbers) {
			// ommit not supported types
			if (ArrayUtils.contains(notSupportedSQLTypesInHSQLDB, typeNumber))
				continue; // not supported
			createAllsqlTypes += sqlTypeNamesMap.get(typeNumber) + "_Column "
					+ sqlTypeNamesMap.get(typeNumber) + ", ";
		}
		createAllsqlTypes = StringUtils.removeEnd(createAllsqlTypes, ", ");
		createAllsqlTypes += ");";
		executeSQL(dropTable);
		System.out.println("createAllsqlTypes: " + createAllsqlTypes);
		executeSQL(createAllsqlTypes);
		DBTable allTypesTable = new DBTable("ALLSQLTYPES");
		sqlTypes2JavaClasses = allTypesTable.getTableStructAsContainerMap();// in
		// arbeit
		// Return the the map
		return sqlTypes2JavaClasses;
	}

	/**
	 * Strat DB in desired dbMode
	 * 
	 * @throws Exception
	 */
	protected void startDB(String dbMode) throws Exception {
		if (dbMode.equals(dbModes[0]))
			startDBSever(); // Server DEVELOPMENT&TEST
		if (dbMode.equals(dbModes[1]))
			startDBintern();// intern PRODUKTION
	}

	/**
	 * Start DB in MaserJ This mode runs the database engine as part of your
	 * application program in the same Java Virtual Machine.
	 * 
	 * @throws Exception
	 */
	protected void startDBintern() throws Exception {
		// only if DB is not Running
		Connection myConn = null;
		try {
			conn = DriverManager.getConnection(dataBaseURL, user, password);
			myConn = DBConnect(dataBaseFile, user, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (myConn != null)
			throw new IllegalArgumentException("DB: " + dataBaseFile
					+ " already running!");
	}

	/**
	 * Strat DB if no Server is up
	 * 
	 * @throws Exception
	 */
	protected void startDBSever() throws Exception {
		// only if Server is not Running
		Connection myConn = null;

		Server server = new Server();

		try {
			myConn = DBConnect(dataBaseURL, user, password);
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("DB's not running yet");
			
		}
		if (myConn != null)
			System.out.println("DataBase.startDBSever(): " + dataBaseURL
					+ " already running! Nothing to start.");
		else {	//Start DB-Server
			// if (dataBaseFile.endsWith(TEST.testDBsuffix))
			// dbAlias += common.TEST.testDBsuffix;
			server.setDatabaseName(0, dbAlias);
			server.setDatabasePath(0, dataBaseFile);
			server.setSilent(true);
			server.setTrace(false);
			server.start();
		}
	}

	// use for SQL commands CREATE, DROP, INSERT and UPDATE
	public synchronized int update(String expression) throws SQLException {

		Statement st = null;

		st = conn.createStatement(); // statements

		int i = st.executeUpdate(expression); // run the query

		if (i == -1) {
			System.out.println("db error : " + expression);
		}

		st.close();
		return i;
	}

	/**
	 * @return the conn
	 */
	public Connection getConn() {
		return conn;
	}

	/**
	 * @uml.property  name="dBTable"
	 * @uml.associationEnd  inverse="dataBase1:model.DBTable"
	 * @uml.association  name="uses"
	 */
	private DBTable table;

	/**
	 * Getter of the property <tt>dBTable</tt>
	 * @return  Returns the table.
	 * @uml.property  name="dBTable"
	 */
	public DBTable getDBTable() {
		return table;
	}

	/**
	 * Setter of the property <tt>dBTable</tt>
	 * @param dBTable  The table to set.
	 * @uml.property  name="dBTable"
	 */
	public void setDBTable(DBTable table) {
		this.table = table;
	} 

}
