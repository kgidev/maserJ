/**
 * 
 */
package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import javax.swing.text.StyledEditorKit.BoldAction;



import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.lang.time.DateUtils;

import tools.DateCalculator;
import tools.ToolBox;
import java.util.Collection;

/**
 * Holds Data from one Observation
 */
public class Observation extends DBTable {
	final static String TABLE_NAME = "Observations";
	// Container for all attributes from Table-Row
	protected HashMap<String, Object> dataMap = new HashMap<String, Object>();
	protected boolean dbSynchron=true;  //Default hold DBTable Row & synchron
	protected String[] keyOrder={"instrument","date","noise","interpolated"};
	Date date;
//	int objectId;
	CelestialObject celObject; 	//parent Object
	MaserComponent[] maserComponents; 	//child Objects 

	
	public Observation(HashMap<String, Object> obsData, boolean dbSynchron) 
			throws Exception {
		super(TABLE_NAME);
		this.date = (Date) obsData.get("date");
		
		setDataMap(obsData);
		setObjectId((Integer) obsData.get("object_id"));
		this.dbSynchron=dbSynchron;
		if (dbSynchron) {
			int dbID=insertMap2DBTable(dataMap);
			setID(dbID);
		}	
	}
	
	public Observation(HashMap<String, Object> obsData) throws Exception {
		this( obsData, true);
	}
	
	/**
	 * Precondition: DataBase is running and Table Observations exists
	 * 
	 * @param obsDate
	 *            Date of the Observation
	 * @throws Exception
	 */
	public Observation(Date obsDate, int objectID) throws Exception {
		super(TABLE_NAME);
		this.date = obsDate;
		setObjectId(objectID);
		setDataMap(getDBTableRow(obsDate, objectID));
	}
	
	public Observation(HashMap<String, Object> obsData,CelestialObject celObj) throws Exception {
		this( obsData, celObj, true);
	}
	
	public Observation(HashMap<String, Object> obsData, CelestialObject celObj,
						boolean dbSynchron)
	throws Exception {
		super(TABLE_NAME);
		this.date = (Date) obsData.get("date");
		setDataMap(obsData);
		setObjectId(celObj.getID());
		this.dbSynchron=dbSynchron;
		if (dbSynchron) {
			int dbID=insertMap2DBTable(dataMap);
			setID(dbID);
		}	
		this.celObject=celObj;
	}
	
	public Observation(File fitsFile, CelestialObject celObj, boolean dbSynchron)
	throws Exception {
		this(Observation.dataFromFitsFile(fitsFile),celObj,dbSynchron);
		setObjectId(celObj.getID());
	}
	
	public Observation(File fitsFile, CelestialObject celObj) throws Exception {
		this( fitsFile, celObj,  true);
	}
	
	/**
	 * @param the DB-Row-ID to set
	 */
	public void setID(int id) {
		dataMap.put("id", id);
		
	}
	
	// GET-Methods for the known Attributes
	/**
	 * @return the noise
	 */
	public int getID() throws Exception {
		return Integer.parseInt(colValueFromRow("id", dataMap).toString());
	}
	
	
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the instrument String
	 * @throws Exception
	 */
	public Object getInstrument() throws Exception {
		return colValueFromRow("instrument", dataMap);
	}

	/**
	 * @return the interpolatedFlag
	 * @throws Exception
	 */
	public Object getInterpolated() throws Exception {
		return colValueFromRow("interpolated", dataMap);
	}

	/**
	 * @return the interpolatedFlag
	 * @throws Exception
	 */
	public boolean isInterpolated() throws Exception {
		return (Boolean)colValueFromRow("interpolated", dataMap);
	}
	
	/**
	 * @return the noise
	 */
	public Object getNoise() throws Exception {
		return colValueFromRow("noise", dataMap);
	}

	/**
	 * @return the dataMap
	 */
	public HashMap<String, Object> getDataMap() {
		return dataMap;
	}

	
	/**
	 * @param obsDate
	 * 			the ObsservationDate
	 * @param objectID
	 * 			the ID of the Parent-Object
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> getDBTableRow(Date obsDate, int objectID) throws Exception {
		java.sql.Date sqlDate=new java.sql.Date(obsDate.getTime());
		String whereCond="WHERE date ='"+ sqlDate.toString() +"'" + "AND OBJECT_ID=" + objectID + ";";
		return  getRowsByWhereCondition(whereCond)[0];
	}

	/**
	 * @param ID
	 *            the ID in DB-Table
	 * @throws Exception
	 * 
	 */
	public HashMap<String, Object> getDBTableRowById(int Id) throws Exception {
		return  getRowById(Id);
	}
	
	/**
	 * @param newParam TODO
	 * @param dataMap
	 *            the dataMap to set
	 * @throws Exception
	 * TODO better select, e.g by ID
	 */
	void setDataMap(HashMap<String, Object> map) throws Exception {
		this.dataMap=map;
	}

	/**
	 * insert in Observation-Table
	 */
	void insert2DBTable() throws Exception {
		HashMap<String,Object> rowMap=(HashMap<String,Object>) dataMap.clone();
//		??let the DB handle IDs 
		rowMap.remove("id");
		String[] columns=new String[rowMap.size()];
		columns=rowMap.keySet().toArray(columns);
		insertRow(columns, rowMap.values().toArray());
	}

	/**
	 * @return the objectId
	 */
	public int getObjectId() {
		return (Integer)dataMap.get("object_id");
	}

	/**
	 * @param objectId the objectId to set
	 */
	void setObjectId(int objectId) {
//		this.objectId = objectId;
		dataMap.put("object_id",objectId );
	}


	/**
	 * @return the observations
	 */
	public MaserComponent[] getComponents() {
		return maserComponents;
	}
	
	/**
	 * @return the Component with index
	 */
	public MaserComponent getComponent(int index) {
		return maserComponents[index];
	}
	
	
	/**
	 * @param objectId the objectId to set
	 */
	public void  setComponents(MaserComponent[] components) {
		this.maserComponents = components;
	}


	/**
	 * @param set component at index 
	 */
	public void  setComponent(MaserComponent maserComponent, int index) {
		this.maserComponents[index] = maserComponent;
	}
	

	/**
	 * the components to set
	 * @throws Exception 
	 */
	MaserComponent[] loadComponentsFromDB() throws Exception {
		String sqlCmd="SELECT * FROM components WHERE observation_id="+getID()+";";
		ResultSet rs=dataBase.executeSQL(sqlCmd);
		ArrayList<MaserComponent> componentList = new ArrayList<MaserComponent>();
		BasicRowProcessor rowProc=new BasicRowProcessor();
		while (rs.next()) {
			String name=rs.getString("name");
			HashMap<String,Object> rowMap=(HashMap<String, Object>) rowProc.toMap(rs);
			MaserComponent comp=new MaserComponent(rowMap,false);//rs.row
			componentList.add(comp);
		}
		maserComponents=new MaserComponent[componentList.size()];
		maserComponents=componentList.toArray(maserComponents);
		return componentList.toArray(maserComponents);
	}
	
	/**
	 * @return the Array of all Values to Key in components
	 */
	public Object[] componentsValuesByKey(String key) {
		Object[] obsValues=new Object[maserComponents.length];
		for (int i = 0; i < maserComponents.length; i++) {
			obsValues[i]=maserComponents[i].getDataMap().get(key);
		}
		return obsValues;
	}
	
	/**
	 * @param name of Value 
	 * @return  Array of 2 Objects [minimum,maximum]
	 */
	public Object[] componentsValuesRange(String key) {
		Object[] values=componentsValuesByKey(key);
		Arrays.sort(values);
		Object[] range={null,null};
		if (values.length>0) {
			range= new Object[] { values[0], values[values.length - 1] };
		} 
		return range;
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
	 * @return the celObject
	 */
	public CelestialObject getCelObject() {
		return celObject;
	}

	/**
	 * @param celObject the celObject to set
	 */
	public void setCelObject(CelestialObject celObject) {
		this.celObject = celObject;
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

		
//		Date observed DATE-OBS
		String dateStr = header.getStringValue("DATE-OBS");
		Date d=(new DateCalculator()).parseFitsDate(dateStr);
		
//		instrument 
		String instrument=hdu.getTelescope();//??telescope or instrument ??
		//noise
		Double noise =0.0;
		
		dataFromFitsFile.put("date", d);
		dataFromFitsFile.put("instrument", instrument);
		dataFromFitsFile.put("interpolated", false);
		dataFromFitsFile.put("noise", noise);
		return dataFromFitsFile;
	}

	/**
	 * @param fitsFile
	 * @return Array of Components in Fits-File
	 * @throws Exception 
	 */
	public static MaserComponent[] componentsFromFitsFile(File fitsFile, 
			Observation compParentObs) throws Exception {
		int numberOfComponents=4;
		MaserComponent[] componentsFromFitsFile = new MaserComponent[numberOfComponents];
		HashMap<String, Object> compDataMap 
					= (new DBTable("COMPONENTS").getTableStructAsContainerMap());
		HashMap<String, Object> compData=(HashMap<String, Object>) compDataMap.clone();
		compData.put("observation_id", compParentObs.getID());
		//laod & validate Fits-Format
		Fits fits =new Fits(fitsFile);
		BasicHDU hdu = fits.readHDU();
		Header header=hdu.getHeader();
		Date obsdate=compParentObs.getDate();
		int dateFactor = DateCalculator.dayDelta(new Date(0), obsdate);
		//TODO change to real code, these are fix values for demonstration
//		Values to Obsdate 17.02.1990
		GregorianCalendar cal=new GregorianCalendar(1990,01,17);
		if (DateUtils.isSameDay(obsdate, cal.getTime() )) {
			//Values to Obsdate 17.02.1990
			//			comp0
			compData = (HashMap<String, Object>) compDataMap.clone();
			compData.put("name", "A");
			compData.put("xoffset", -42.0);
			compData.put("yoffset", 74.0);
			compData.put("intensity", 2.6);
			compData.put("velocity", -4.6);
			componentsFromFitsFile[0] = new MaserComponent(compData,
					compParentObs);
			//		comp1
			compData = (HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "C");
			compData.put("xoffset", 18.0);
			compData.put("yoffset", 15.0);
			compData.put("intensity", 6.2);
			compData.put("velocity", -2.9);
			componentsFromFitsFile[1] = new MaserComponent(compData,
					compParentObs);
			//			comp2
			compData = (HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "J");
			compData.put("xoffset", 90.0);
			compData.put("yoffset", -32.0);
			compData.put("intensity", 64.7);
			compData.put("velocity", 2.9);
			componentsFromFitsFile[2] = new MaserComponent(compData,
					compParentObs);
			//			comp3
			compData = (HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "K");
			compData.put("xoffset", 49.0);
			compData.put("yoffset", 33.0);
			compData.put("intensity", 0.0);
			compData.put("velocity", 4.0);
			componentsFromFitsFile[3] = new MaserComponent(compData,
					compParentObs);
		}
//		Values to Obsdate 5.07.1992
		cal=new GregorianCalendar(1992,06,05);
		if (DateUtils.isSameDay(obsdate, cal.getTime())) {	
//			comp0
			compData=(HashMap<String, Object>) compDataMap.clone();
			compData.put("name", "A");
			compData.put("xoffset", -42.0);
			compData.put("yoffset", 74.0);	
			compData.put("intensity", 2.9);
			compData.put("velocity", -4.5); 
			componentsFromFitsFile[0]=new MaserComponent(compData,compParentObs);
//		comp1
			compData=(HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "C");
			compData.put("xoffset", 18.0);
			compData.put("yoffset", 15.0);	
			compData.put("intensity", 5.6);
			compData.put("velocity", -2.5); 
			componentsFromFitsFile[1]=new MaserComponent(compData,compParentObs);
//			comp2
			compData=(HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "J");
			compData.put("xoffset", 90.0);
			compData.put("yoffset", -32.0);	
			compData.put("intensity", 5.7);
			compData.put("velocity", 3.2); 
			componentsFromFitsFile[2]=new MaserComponent(compData,compParentObs);
//			comp3
			compData=(HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "K");
			compData.put("xoffset", 49.0);
			compData.put("yoffset", 33.0);	
			compData.put("intensity", 6.1);
			compData.put("velocity", 4.0); 
			componentsFromFitsFile[3]=new MaserComponent(compData,compParentObs);	
		}	
			
//		Values to Obsdate 13.05.1993
		cal=new GregorianCalendar(1993,04,13);
		if (DateUtils.isSameDay(obsdate, cal.getTime())) {	
//			comp0
			compData=(HashMap<String, Object>) compDataMap.clone();
			compData.put("name", "A");
			compData.put("xoffset", -42.0);
			compData.put("yoffset", 74.0);	
			compData.put("intensity", 5.5);
			compData.put("velocity", -4.5); 
			componentsFromFitsFile[0]=new MaserComponent(compData,compParentObs);
//		comp1
			compData=(HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "C");
			compData.put("xoffset", 18.0);
			compData.put("yoffset", 15.0);	
			compData.put("intensity", 8.0);
			compData.put("velocity", -2.9); 
			componentsFromFitsFile[1]=new MaserComponent(compData,compParentObs);
//			comp2
			compData=(HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "J");
			compData.put("xoffset", 90.0);
			compData.put("yoffset", -32.0);	
			compData.put("intensity", 0);
			compData.put("velocity", 3.2); 
			componentsFromFitsFile[2]=new MaserComponent(compData,compParentObs);
//			comp3
			compData=(HashMap<String, Object>) compDataMap.clone();
			compData.clear();
			compData.put("name", "K");
			compData.put("xoffset", 49.0);
			compData.put("yoffset", 33.0);	
			compData.put("intensity", 8.5);
			compData.put("velocity", 3.9); 
			componentsFromFitsFile[3]=new MaserComponent(compData,compParentObs);	
		}	
		return componentsFromFitsFile;
	}

	/**
	 * @uml.property  name="celestialObject"
	 * @uml.associationEnd  multiplicity="(0 -1)" inverse="observation:model.CelestialObject"
	 */
	private Collection<CelestialObject> celestialObject;


	/**
	 * Getter of the property <tt>celestialObject</tt>
	 * @return  Returns the celestialObject.
	 * @uml.property  name="celestialObject"
	 */
	public Collection<CelestialObject> getCelestialObject() {
		return celestialObject;
	}

	/**
	 * Setter of the property <tt>celestialObject</tt>
	 * @param celestialObject  The celestialObject to set.
	 * @uml.property  name="celestialObject"
	 */
	public void setCelestialObject(Collection<CelestialObject> celestialObject) {
		this.celestialObject = celestialObject;
	}
}
