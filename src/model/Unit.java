/**
 * 
 */
package model;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

/**
 */
public class Unit extends DBTable {
	final static String TABLE_NAME = "UNITS";

	// Container for all attributes from one Table-Row
	protected HashMap<String, Object> dataMap = new HashMap<String, Object>();

	
	/**
	 * @param dataMap
	 * @throws Exception
	 */
	Unit(HashMap<String, Object> unitData)
			throws Exception {
		super(TABLE_NAME);
		setDataMap(unitData);
	}

	/**
	 * @return the dataMap
	 */
	public HashMap<String, Object> getDataMap() {
		return dataMap;
	}

	/**
	 * @param dataMap
	 *            the dataMap to set
	 */
	void setDataMap(HashMap<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public HashMap<String, Object> getComponentRowByName(String name) 
			throws Exception {
		return getRowsByWhereCondition("WHERE NAME=" + name + "';")[0];
	}

	// GET-Methods for the known Attributes
	/**
	 * @return the DB-Row-ID
	 */
	public int getID() throws Exception {
		return Integer.parseInt(colValueFromRow("ID", dataMap).toString());
	}

	/**
	 * @return the unit-name
	 */
	public String getName() throws Exception {
		return colValueFromRow("name", dataMap).toString();
	}

	/**
	 * @return the unit-VALUE 
	 */
	public String getValue() throws Exception {
		return colValueFromRow("value", dataMap).toString();
	}
}
