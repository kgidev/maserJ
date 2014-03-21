/**
 * 
 */
package model;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.lang.StringUtils;

import tools.ToolBox;

/*
 */
public class DBTable {
	String createStatement;

	DataBase dataBase;

	String tableName;

	HashMap<String, String> tableStruct; // Column-Names,Column-Java.ClassNames

	/**
	 * @param dataBase
	 * @param createStatement
	 * @throws Exception
	 */
	DBTable(DataBase dataBase, String createStatement) throws Exception {
		super();
		// if not exists, else connect to
		this.dataBase = dataBase;
		setTableName(tableNameFromCreateStmt(createStatement));
		createTable(createStatement);
		setCreateStatement(createStatement);
		setTableStruct(ToolBox.objectMap2ClassNameMap(getTableStructAsContainerMap()));

	}

	/**
	 * @param dataBase
	 * @param tableName
	 * @throws Exception
	 */
	public DBTable(String tableName) throws Exception {
		super();
		this.dataBase = new DataBase();// ?? test or not test in DB?!
		// dataBase has to run !
		// if exists only connect common.TEST.dbURL
		dataBase.DBConnect(dataBase.dataBaseURL, dataBase.user,
				dataBase.password);
		setTableName(tableName);
		setCreateStatement("");
		setTableStruct(ToolBox.objectMap2ClassNameMap(getTableStructAsContainerMap()));
	}

	/**
	 * @param colName
	 *            the column-Name
	 * @param row
	 *            the Table-Row with the columns
	 */
	public Object colValueFromRow(String colName, HashMap<String, Object> row) {
		return row.get(colName);
	}

	/**
	 * @return succsess or not
	 */
	boolean createTable(String createStmt) {
		boolean succsess = false;
		try {
			ResultSet rs = dataBase.executeSQL(createStmt);
			succsess = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return succsess;

	}

	/**
	 * delete all Table-Rows from DB-Table
	 * 
	 */
	public boolean deleteRows() {
		boolean succsess = false;
		try {
			dataBase.executeSQL("DELETE FROM " + tableName + ";");
			succsess = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return succsess;
	}

	/**
	 * drop Table from DB
	 */
	boolean dropTable() {
		boolean succsess = false;
		try {
			dataBase.executeSQL("DROP TABLE " + tableName + ";");
			succsess = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return succsess;
	}

	/**
	 * @return the createStatement
	 */
	public String getCreateStatement() {
		return this.createStatement;	
	}

	/**
	 * @return returns the last identity values that was inserted by this
	 *         connection
	 */
	public Integer getIdentity() {
		Integer getIdentity = null;
		String sqlStmt = "CALL IDENTITY()";
		Statement st = null;
		ResultSet rs = null;
		try {
			rs = dataBase.query(sqlStmt);
			rs.next();
			getIdentity = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getIdentity;
	}

	/**
	 * get one Row or All from Table
	 * 
	 * @throws Exception
	 */
	HashMap<String, Object> getRowById(int id) throws Exception {
		HashMap<String, Object> rowMap = null;
		BasicRowProcessor bp = new BasicRowProcessor();
		String selectStmt = "SELECT * FROM " + tableName + " WHERE ID=" + id
				+ ";";
		ResultSet rs = dataBase.executeSQL(selectStmt);
		rs.next();
		rowMap = (HashMap<String, Object>) bp.toMap(rs);
		return rowMap;
	}

	/**
	 * get all Rows from Table
	 * 
	 * @throws Exception
	 */
	public HashMap<String, Object>[] getRows() throws Exception {
		return getRowsByWhereCondition("");
	}

	/**
	 * get Rows from Table with whereCondition
	 * 
	 * @throws Exception
	 */
	public HashMap<String, Object>[] getRowsByWhereCondition(String whereCond)
			throws Exception {
		List<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
		BasicRowProcessor bp = new BasicRowProcessor();
		String selectStmt = "SELECT * FROM " + tableName + " " + whereCond;
		ResultSet rs = dataBase.executeSQL(selectStmt + ";");
		while (rs.next())
			maps.add((HashMap<String, Object>) bp.toMap(rs));
		HashMap<String, Object>[] returnMap = new HashMap[maps.size()];
		return maps.toArray(returnMap);
	}

	/**
	 * get all Rows from Table
	 * 
	 * @throws Exception
	 */
	public HashMap<String, Object>[] getRowsOrderByID() throws Exception {
		return getRowsByWhereCondition("ORDER BY ID");
	}

	/**
	 * @return the tableName
	 */
	protected String getTableName() {
		return tableName;
	}

	/**
	 * 
	 * @throws Exception
	 * @return Table-Struct as Description Map with
	 *         <ColumnName,Cloumn-ClassName>
	 */
	public HashMap<String, String> getTableStruct() throws Exception {
		return tableStruct;
	}

	/**
	 * translate tableStruct to HashMap<String, Object>
	 * 
	 * @throws Exception
	 * @return empty tableStruct as HashMap<String, Object>
	 */
	public HashMap<String, Object> getTableStructAsContainerMap()
			throws Exception {
		String selectSql = "SELECT * FROM " + tableName + ";";
		ResultSet rs = dataBase.executeSQL(selectSql);
		ResultSetMetaData metaData = rs.getMetaData();

		HashMap<String, String> localTableStruct = new HashMap<String, String>();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String columnClassName=metaData.getColumnClassName(i);
//			special treatment for different integerTypes
			if (metaData.getColumnTypeName(i)=="TINYINT") {
				columnClassName=Byte.class.getName();
			}
			if (metaData.getColumnTypeName(i)=="SMALLINT") {
				columnClassName=Short.class.getName();
			}
			if (metaData.getColumnTypeName(i)=="BIGINT") {
				columnClassName=Long.class.getName();
			}
			localTableStruct.put(metaData.getColumnName(i),columnClassName );
		}
		// System.out.print(ToolBox.getCurrentMethodName() + ":\n");
		// ToolBox.dumpHahsMap(localTableStruct);
		// translate tableStruct to HashMap<String, Object>
		HashMap<String, Object> containerMap = new HashMap<String, Object>();
		Set<String> keySet = localTableStruct.keySet();
		for (String key : keySet) {
			if (!key.equals("NULL_COLUMN")) {
				// System.out.println("key :" + key);
				String clazz = localTableStruct.get(key);
				// System.out.println("localTableStruct.get(key):" + clazz);
				//special treatment for different integerTypes
//				if (=="SMALLINT") {
//					clazz=Short.class.getSimpleName();
//				}
				
				Object o = ToolBox.randomValueByClassName(clazz);
				// put zero Value
				o = ToolBox.objectFromClass(o.getClass());
				containerMap.put(key, o);
			}
		}
		// CaseInsensitiveMap
		CaseInsensitiveMap noCaseMap = new CaseInsensitiveMap(containerMap);
		containerMap = new HashMap<String, Object>(noCaseMap);
		return containerMap;
	}

	public int insertMap2DBTable(HashMap<String, Object> dataMap)
			throws Exception {
		return insertMap2DBTable(dataMap, true);
	}

	/**
	 * convinience Method for Data-Insert with HashMap insert in
	 * Observation-Table
	 */
	/**
	 * @param dataMap
	 *            the row-data to write to DBTable
	 * @param dbHhandlesId
	 *            let the DB handle IDs or not
	 * @throws Exception
	 */
	public Integer insertMap2DBTable(HashMap<String, Object> dataMap,
			boolean dbHhandlesId) throws Exception {

		HashMap<String, Object> rowMap = (HashMap<String, Object>) dataMap
				.clone();
		Integer rowID = (Integer) dataMap.get("id");
		if (dbHhandlesId) {
			// 
			rowMap.remove("id");
			rowID = null;
		}
		String[] columns = new String[rowMap.size()];
		columns = rowMap.keySet().toArray(columns);
		insertRow(columns, rowMap.values().toArray());
		if (dbHhandlesId)
			rowID = getIdentity();
		return rowID;
	}

	/**
	 * delete all Table-Rows from DB-Table columns Order muts match values Order
	 * TODO better Type-Casting sql2Java
	 * 
	 * @throws SQLException
	 * 
	 */
	public boolean insertRow(String[] columns, Object[] values)
			throws SQLException {
		boolean succsess = false;
		String columsSql = Arrays.toString(columns).replace("[", "(").replace(
				"]", ")");
//		String valuesSql = "(";
//		for (int i = 0; i < values.length; i++) {
//			Object obj=values[i];
//			String value;
////			 TODO null values
////			if (values[i] == null) {
////				values[i] = "NULL";
////			}
//			 
//			if (obj.getClass().isArray()) {
//				if ((Array.get(obj, 0)).getClass()==Byte.class) {
//					System.out.println("ByteArr"+(Array.get(obj, 0)).getClass());
////					TODO ByteArrColumns to sql by prepared Stmt
////					PreparedStatement pstmt=
//				}
//				
//			}
//			value = values[i].toString();
//			if (values[i].getClass() != java.lang.Integer.class)
//				value = "'" + value + "'";
//			if (values[i].getClass() == java.util.Date.class) {
//
//				// TODO format to sqlDate
//				java.sql.Date sqlDate = new java.sql.Date(
//						((java.util.Date) values[i]).getTime());
//				value = "'" + sqlDate.toString() + "'"; // yyyy-mm-dd
//				// System.out.println("Date valuesSql: "+value);
//			}
//			valuesSql += value + ",";
//		}
//		valuesSql = StringUtils.chomp(valuesSql, ",");
//		valuesSql += ")";
//		dataBase.executeSQL("INSERT INTO  " + tableName + " " + columsSql
//		+ " VALUES " + valuesSql + " ;");
		
		//NEW
		String valuesPlaceHolder = "("+StringUtils.repeat("?,",values.length);
		valuesPlaceHolder=StringUtils.chomp(valuesPlaceHolder, ",")+")";
		String insertSql = "INSERT INTO  " + tableName + " " + columsSql
		+ " VALUES " + valuesPlaceHolder + " ;";
		Connection con =dataBase.getConn();
		PreparedStatement pstmt = con.prepareStatement(insertSql);
		
		//setValues
		for (int i = 1; i <= values.length; i++) {
			pstmt.setObject(i, values[i-1]);
		}
		
//		System.out.println("insertSql: "+insertSql);
		int rowCount =pstmt.executeUpdate();
		if (rowCount>0) succsess = true;
		return succsess;
	}

	/**
	 * @param createStatement
	 *            the createStatement to set
	 * @throws SQLException
	 */
	public void setCreateStatement(String createStmt) throws SQLException {
		String createStr = createStmt;
		String[] DDLScript = StringUtils.split(dataBase.ddlScript(), "\n");
		for (int i = 0; i < DDLScript.length; i++) {
			if (DDLScript[i].startsWith("CREATE")
					&& StringUtils.contains(DDLScript[i], "TABLE " + tableName
							+ "(")) {
				createStr = DDLScript[i];
				break;
			}
		}
		this.createStatement = createStr.toUpperCase();
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 * @throws SQLException
	 */
	public void setTableName(String tableName) throws SQLException {
		this.tableName = tableName;
	}

	/**
	 * get Table-Struct from DB
	 * 
	 * @throws Exception
	 */
	protected void setTableStruct(HashMap<String, String> tableStruct)  {
		this.tableStruct = tableStruct;
//		String selectSql = "SELECT * FROM " + tableName + ";";
//		ResultSet rs = dataBase.executeSQL(selectSql);
//		ResultSetMetaData metaData = rs.getMetaData();
//		// System.out.println("metaData: "+metaData);
//		tableStruct = new HashMap<String, String>();
//		
//		for (int i = 1; i <= metaData.getColumnCount(); i++) {
//			tableStruct.put(metaData.getColumnName(i), metaData
//					.getColumnClassName(i));
//		}
	}

	/**
	 * parse name from CreateStmt
	 * 
	 * @param tableName
	 *            the tableName to set
	 * @throws SQLException
	 */
	protected String tableNameFromCreateStmt(String createStmt)
			throws Exception {

		return createStmt.toUpperCase().split(" TABLE ")[1].split("\\(")[0]
				.trim();

	}

	/**
	 * @param dataMap
	 *            rowID the unique ID of the row from DBTable
	 * @param dataMap
	 *            the row-data to update in DBTable a id column will be ignored
	 * @return the number of updated rows (should be 1)
	 * @throws SQLException
	 */

	/**
	 * @param rowID
	 * @param dataMap
	 * 
	 * @throws SQLException
	 */
	public Integer updateRow(Integer rowID, HashMap<String, Object> dataMap)
			throws SQLException {
		HashMap<String, Object> rowMap = (HashMap<String, Object>) dataMap
				.clone();
		String[] columns = new String[rowMap.size()];
		columns = rowMap.keySet().toArray(columns);
		insertRow(columns, rowMap.values().toArray());
		String updateStmt = "UPDATE " + tableName
				+ " SET column = Expression [, ...] WHERE id =" + rowID;
		int rowCount = dataBase.update(updateStmt);
		return rowCount;
	}
	
	/**
	 * @param dataMap
	 *            rowID the unique ID of the row from DBTable
	 * @param dataMap
	 *            the row-data to update in DBTable a id column will be ignored
	 * @return the number of updated rows (should be 1)
	 * @throws SQLException
	 */

	/**
	 * @param rowID
	 * @param dataMap
	 * 
	 * @throws SQLException
	 */
	public Integer updateRowById(Connection con, Integer rowID,
			HashMap<String, Object> dataMap)
			throws SQLException {
		HashMap<String, Object> rowMap = (HashMap<String, Object>) dataMap
				.clone();
		String[] columnNames = dataMap.keySet().toArray(new String[1]);
		Object[] values=dataMap.values().toArray();
		String columns = Arrays.toString(columnNames).replace("[", "(").replace(
				"]", ")");
		
		//UPDATE allsqltypes SET id =0, double_column= 0.9
		
		String updateNames="";
		for (int i = 0; i < columnNames.length; i++) {
			updateNames = updateNames + (columnNames[i]+" = ?,");
		}
		updateNames = StringUtils.chomp(updateNames, ",");
		PreparedStatement pstmt = con.prepareStatement(
		"UPDATE "+this.tableName+" SET "+updateNames +" WHERE id = "+rowID);
		for (int i = 1; i <= columnNames.length; i++) {
			pstmt.setObject(i, values[i-1]);
		}
		int rowCount = pstmt.executeUpdate();
		return rowCount;
	}

	/**
	 * @uml.property  name="dataBase1"
	 * @uml.associationEnd  inverse="dBTable:model.DataBase"
	 * @uml.association  name="uses"
	 */
	private DataBase dataBase1;

	/**
	 * Getter of the property <tt>dataBase1</tt>
	 * @return  Returns the dataBase1.
	 * @uml.property  name="dataBase1"
	 */
	public DataBase getDataBase1() {
		return dataBase1;
	}

	/**
	 * Setter of the property <tt>dataBase1</tt>
	 * @param dataBase1  The dataBase1 to set.
	 * @uml.property  name="dataBase1"
	 */
	public void setDataBase1(DataBase dataBase1) {
		this.dataBase1 = dataBase1;
	}
	
	
}
