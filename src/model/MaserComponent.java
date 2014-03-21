/**
 * 
 */
package model;

import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

/**
 */
public class MaserComponent extends DBTable {
	final static String TABLE_NAME = "COMPONENTS";
	protected boolean dbSynchron=true;  //Default hold DBTable Row & synchron

	// Container for all attributes from one Table-Row
	protected HashMap<String, Object> dataMap = new HashMap<String, Object>();

	Observation observation; // ref to parent-observation-object
	protected String[] keyOrder={"name","xoffset","yoffset","velocity",
			"brightness", "intensity", "observation_id", "id"};
	
	/**
	 * @param observationId 
	 * @param dataMap
	 * @throws Exception
	 */
	public MaserComponent(HashMap<String, Object> compData, boolean dbSynchron,
			Integer observation_id) throws Exception {
		super(TABLE_NAME);
		if (observation_id!= null) {
			compData.put("observation_id", observation_id);
		}
		setDataMap(compData);
		this.dbSynchron=dbSynchron;
		if (dbSynchron) {
			int dbID=insertMap2DBTable(dataMap);
			setID(dbID);
		}	
	}
	
	public MaserComponent(HashMap<String, Object> compData) throws Exception{
		this(compData,true);
	}
	
	public MaserComponent(HashMap<String, Object> compData,boolean dbSynchron) 
			throws Exception{
		this(compData,dbSynchron,null);
	}
	/**
	 * @param observationId 
	 * @param dataMap
	 * @throws Exception
	 */
	public MaserComponent(HashMap<String, Object> compData, Observation observation,
			boolean dbSynchron)	throws Exception {
		this(compData, dbSynchron,observation.getID());
		setObservation(observation);
	}
	public MaserComponent(HashMap<String, Object> compData, Observation observation)
		throws Exception{
		this(compData,observation,true);
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
	public void setDataMap(HashMap<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	/**
	 * @return the parent Observation 
	 */
	public Observation getObservation() {
		return this.observation;
	}

	/**
	 * @param observationId
	 *            the observation to set
	 */
	void setObservation(Observation observation) {
		this.observation = observation;
	}

	public HashMap<String, Object> getComponentRowByUniqueKey(int observationId,
			String name) throws Exception {
		return getRowsByWhereCondition("WHERE OBSERVATION_ID=" + observationId
				+ " AND name='" + name + "';")[0];
	}

	// GET-Methods for the known Attributes
	/**
	 * @return the ID
	 */
	public int getID() {
		return Integer.parseInt(colValueFromRow("id", dataMap).toString());
	}

	/**
	 * @param the DB-Row-ID to set
	 */
	public void setID(int id) {
		dataMap.put("id", id);
		
	}
	
	
	/**
	 * @return the name
	 */
	public String getName(){
		return colValueFromRow("name", dataMap).toString();
	}

	/**
	 * @return the intensity
	 */
	public double getIntensity(){
		return Double.parseDouble(colValueFromRow("intensity", dataMap)
				.toString());
	}
	/**
	 * @return the xOffset
	 */
	public double getXOffset()  {
		return Double.parseDouble(colValueFromRow("xoffset", dataMap)
				.toString());
	}

	/**
	 * @return the yOffset
	 */
	public double getYOffset(){
		return Double.parseDouble(colValueFromRow("yoffset", dataMap)
				.toString());
	}
	
	/**
	 * @return the velocity
	 */
	public double getVelocity(){
		return Double.parseDouble(colValueFromRow("velocity", dataMap)
				.toString());
	}

	
	
	/**
	 * @return the brightness
	 */
	public double getBrightness(){
		return Double.parseDouble(colValueFromRow("brightness", dataMap)
				.toString());
	}
	
	/**
	 * update DBRow
	 */
	public int updateDBRow() {
		int rowCount=0;
		try {
			rowCount= updateRowById(this.dataBase.getConn(), this.getID(), this.getDataMap());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rowCount;
	}

	/**
	 * @return the keyOrder
	 */
	public String[] getKeyOrder() {
		return keyOrder;
	}

	/**
	 * @param keyOrder the keyOrder to set
	 */
	public void setKeyOrder(String[] keyOrder) {
		this.keyOrder = keyOrder;
	}
}
