/**
 * 
 */
package model;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

//
//
//
//import nom.tam.fits.BasicHDU;
//import nom.tam.fits.Fits;
//import nom.tam.fits.FitsException;
//import nom.tam.fits.Header;

import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;

import org.apache.commons.lang.StringUtils;

/**
 * Proxy-Class for one Row from DataBase Table CELESTIALOBJECTS
 * 
 */
public class CelestialObject extends DBTable {
	final static String TABLE_NAME = "CELESTIALOBJECTS";

	// Container for all attributes from one Table-Row
	protected HashMap<String, Object> dataMap = new HashMap<String, Object>();
	protected boolean dbSynchron=true;  //Default hold DBTable Row & synchron
//	protected int ID; 
	protected Observation[] obseravtions; //childs
	protected String[] keyOrder={"name","ra","dec","epoch"};
	
	/**
	 * standart Constructor
	 */
	public CelestialObject() throws Exception {
		super(TABLE_NAME);
	}
	
	/**
	 * @param dataMap
	 * @throws Exception
	 */
	public CelestialObject(HashMap<String, Object> objectData, boolean dBSynchron)
			throws Exception {
		super(TABLE_NAME);
		this.dbSynchron=dBSynchron;
		setDataMap(objectData);
		if (dBSynchron) {
			int dbID=insertMap2DBTable(dataMap);
			setID(dbID);
		}		
	}

//	/**
//	 * @param id the DataBase ID from Table CELESTIALOBJECTS
//	 * CELESTIALOBJECTS-Row with this ID must exist
//	 * @throws Exception 
//
//	 */
//	public CelestialObject(int id) throws Exception {
//		super(TABLE_NAME);
//		setID(id);
//		setDataMap(link2TableRow(id));
//	}

	/**
	 * @param dataMap
	 * @throws Exception
	 */
	public CelestialObject(HashMap<String, Object> objectData)
			throws Exception {
		this(objectData, true);

	}
	
	/**
	 * @param dataMap
	 * @throws Exception
	 */
	public CelestialObject(File fitsFile)
			throws Exception {
		this(dataFromFitsFile(fitsFile) , true);
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

	HashMap<String, Object> getRowByName(String name) throws Exception {
		return getRowsByWhereCondition("WHERE NAME='" + name + "';")[0];
	}
	
	/**
	 * @return the DB-Row-ID
	 */
	int setID() throws Exception {
		return Integer.parseInt(colValueFromRow("id", dataMap).toString());
	}
	
	/**
	 * @param the DB-Row-ID to set
	 */
	public void setID(int id) {
//		this.ID=id;
		dataMap.put("id", id);
		
	}

	// GET-Methods for the known Attributes
	/**
	 * @return the DB-Row-ID
	 */
	public int getID() throws Exception {
		return Integer.parseInt(colValueFromRow("id", dataMap).toString());
	}

	/**
	 * @return the Name-Value
	 */
	public String getName()  {
		return colValueFromRow("name", dataMap).toString();
	}

	/**
	 * @return the RightAscensionn-Value
	 */
	public Double getRightAscension() throws Exception {
		return (Double) colValueFromRow("ra", dataMap);
	}
	
	/**
	 * @return the DEC-Value
	 */
	public Double getDeclination() throws Exception {
		return (Double) colValueFromRow("dec", dataMap);
	}

	/**
	 * @return the obseravtions
	 */
	public Observation[] getObseravtions() {
		return obseravtions;
	}
	
	/**
	 * @return the obseravtion at index
	 */
	public Observation getObseravtion(int index) {
		return obseravtions[index];
	}

	/**
	 * @throws Exception  
	 */
	public void setObseravtions(Observation[] observations) throws Exception {
		this.obseravtions=observations;
	}
	
	/**
	 * @throws Exception  
	 */
	public void setObseravtion(Observation observation, int index) throws Exception {
		this.obseravtions[index]=observation;
	}
	

	/**
	 * fetch all rows from DBTable"OBSERVATIONS" with this Object_id and
	 * construct Observation-Objects foreach, wich are the observations to set
	 * @param observations TODO
	 * 
	 * @throws Exception 
	 * * @deprecated Use {@link #setObseravtions(Observation[])} instead
	 */
	public void setObseravtions() throws Exception {
		
		ArrayList<Observation> obs= new ArrayList<Observation>();
		DBTable obsTable = new DBTable("OBSERVATIONS");
		HashMap<String, Object>[] obsRows=obsTable.getRowsByWhereCondition(
				"where object_id = '"+getID()+"'" + "ORDER BY ID");
		
		for (int i = 0; i < obsRows.length; i++) {
			obs.add(new Observation(obsRows[i]));
		}
		this.obseravtions=new Observation[obs.size()];
		this.obseravtions = obs.toArray(obseravtions);
	}
	
	/**
	 * link this CelestialObject to the Table-Row
	 * @return the Table-Row as Map
	 * @throws Exception 
	 */
	public HashMap<String, Object> link2TableRow()  {
		return link2TableRow(null);
	}

	/**
	 * link this CelestialObject to the Table-Row
	 * @param tableID 
	 * 				the known DBTableRow-ID
	 * @return the Table-Row as Map
	 * @throws Exception if DB-access fails
	 */
	public HashMap<String, Object> link2TableRow(Integer tableID)  {
		HashMap<String, Object> row=null;
		String whereCond;
		if (tableID==null) {
			whereCond = "WHERE ";
			Set<String> keys = dataMap.keySet();
			for (Iterator iter = keys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				if (!key.equals("id")) {
					whereCond += " " + key + " = '"
							+ dataMap.get(key).toString() + "' AND";
				}
			}
			whereCond=StringUtils.removeEnd(whereCond, " AND");
		} else 	whereCond = "WHERE ID="+tableID+";";
		
		try {
			row=this.getRowsByWhereCondition(whereCond)[0];
		} catch (Exception e) {
			System.err.println("no such Data in Table:"+whereCond);
			e.printStackTrace();
		}
		return row;
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
	
	/**
	 * @param fitsFile
	 * @return Map of the relevant Data from Input-fitsFile
	 * @throws FitsException
	 * @throws IOException
	 */
	public static HashMap<String, Object> dataFromFitsFile(File fitsFile) throws FitsException, IOException {
		HashMap<String, Object> dataFromFitsFile = 
			new HashMap<String, Object>();
		
		//laod & validate Fits-Format
		Fits fits =new Fits(fitsFile);
		BasicHDU hdu = fits.readHDU();
		Header header=hdu.getHeader();
		hdu.isHeader(header);
		
//		OBJECT name 
		String name=hdu.getObject();		
//		RA 
		Double ra=header.getDoubleValue("CRVAL2");
//		DEC
		Double dec=header.getDoubleValue("CRVAL3");
//		EPOCH
		String epoch=((Double)hdu.getEquinox()).toString();	//EQUINOX =  0.1950000000000E+04

		dataFromFitsFile.put("name", name);
		dataFromFitsFile.put("ra", ra);
		dataFromFitsFile.put("dec", dec);
		dataFromFitsFile.put("epoch", epoch);
		return dataFromFitsFile;
	}
	
	/**
	 * fetch all rows from DBTable"OBSERVATIONS" with this Object_id and
	 *  
	 * @throws Exception 

	 */
	public HashMap<String, Object>[] getObseravtionsFromDB() throws Exception {
		ArrayList<Observation> obs= new ArrayList<Observation>();
		DBTable obsTable = new DBTable("OBSERVATIONS");
		HashMap<String, Object>[] obsRows=obsTable.getRowsByWhereCondition(
				"where object_id = '"+getID()+"'" + "ORDER BY ID");
		
		for (int i = 0; i < obsRows.length; i++) {
			obs.add(new Observation(obsRows[i]));
		}
		return obs.toArray(obsRows);
	}

	/** 
	 * @uml.property name="observation"
	 * @uml.associationEnd multiplicity="(0 -1)" dimension="1" ordering="true" inverse="celestialObject:model.Observation"
	 */
	private Observation[] observation;

	/** 
	 * Getter of the property <tt>observation</tt>
	 * @return  Returns the observation.
	 * @uml.property  name="observation"
	 */
	public Observation[] getObservation() {
		return observation;
	}

	/** 
	 * Setter of the property <tt>observation</tt>
	 * @param observation  The observation to set.
	 * @uml.property  name="observation"
	 */
	public void setObservation(Observation[] observation) {
		this.observation = observation;
	}
}
