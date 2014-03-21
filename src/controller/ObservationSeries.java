/**
 * 
 */
package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;

import tools.DateCalculator;

import model.CelestialObject;
import model.DBTable;
import model.MaserComponent;
import model.Observation;

/**
 * Holds  Observations from one observed Object
 */
public class ObservationSeries {

	protected CelestialObject celestialObject;

	protected Date[] observationsDates;

	protected Observation[] observations;


	/**
	 * empty Constructor for Test
	 */
	protected ObservationSeries()  {
	}
	
	/**
	 * @param celestialObject
	 *            the refered celestialObject
	 * @param dates
	 *            the observationsDates
	 * @throws Exception 
	 * 
	 */
	public ObservationSeries(CelestialObject celestialObject,
			Date[] observationsDates) throws Exception {
		setCelestialObject(celestialObject);
		setObservationsDates(observationsDates);
		setObservations(obseravtionsByDates(observationsDates));

	}

	/**
	 * @param celestialObject
	 *            the refered celestialObject
	 * @param strartDate
	 *            the observationsDates Startday
	 * @param endDate
	 *            the observationsDates endday
	 * @throws Exception 
	 * 
	 */
	public ObservationSeries(CelestialObject celestialObject, Date strartDate,
			Date endDate) throws Exception {
		this(celestialObject,DateCalculator.dayDatesByRange(strartDate, endDate));
	}

	/**
	 * @return the celestialObject
	 */
	public CelestialObject getCelestialObject() {
		return celestialObject;
	}

	/**
	 * @param celestialObject
	 *            the celestialObject to set
	 */
	public void setCelestialObject(CelestialObject celestialObject) {
		this.celestialObject = celestialObject;
	}

	/**
	 * @return the observations
	 */
	public Observation[] getObservations() {
		return observations;
	}

	/**
	 * @param observations
	 *            the observations to set from
	 *            celestialObject with this dates
	 * @throws Exception 
	 */
	public Observation[] obseravtionsByDates(Date[] obsDates) throws Exception {
		Date[] observationsDates=obsDates.clone();
		Observation[] allObs=celestialObject.getObseravtions();
		ArrayList<Observation> obs=new ArrayList<Observation>();
//		observations from celestialObject
		for (int i = 0; i < allObs.length; i++) {
			if (ArrayUtils.contains(observationsDates,	allObs[i].getDate())) {
				obs.add(allObs[i]);
//				remove processed Date from InputArray
				observationsDates =(Date[])ArrayUtils.removeElement(observationsDates, allObs[i].getDate()); 
			}		
		}
//		new Observations not yet in celestialObject
		for (int i = allObs.length; i <obsDates.length ; i++) {
			System.out.println("Warning: no Observation on Date:"
					+obsDates[i]+" of Celestial Object: '"
					+celestialObject.getName()+"'");
			HashMap<String,Object> obsData=
				(new DBTable("Observations").getTableStructAsContainerMap());
				obsData.put("date", new java.sql.Date(obsDates[i].getTime()));
				obsData.put("object_id", celestialObject.getID());
				obsData.put("interpolated", true);
			obs.add(new Observation(obsData));
		}
		return obs.toArray(new Observation[obs.size()]);
	}

	/**
	 * @return the observationsDates
	 */
	public Date[] getObservationsDates() {
		return observationsDates;
	}

	/**
	 * @return the Array of all Values to Key in Observations
	 */
	public Object[] getObservationValuesByKey(String key) {
		Object[] obsValues=new Object[observations.length];
		for (int i = 0; i < observations.length; i++) {
			obsValues[i]=observations[i].getDataMap().get(key);
		}
		return obsValues;
	}
	
	/**
	 * @param name of Value 
	 * @return  Array of 2 Objects [minimum,maximum]
	 */
	public Object[] getObservationValuesRange(String key) {
		Object[] values=getObservationValuesByKey(key);
		Arrays.sort(values);
		Object[] range={null,null};
		if (values.length>0) {
			range= new Object[] { values[0], values[values.length - 1] };
		} 
		return range;
	}
	
	/**
	 * @param observationsDates
	 *            the observationsDates to set
	 */
	public void setObservationsDates(Date[] observationsDates) {
		this.observationsDates = observationsDates;
	}

	/**
	 * @param observations the observations to set
	 */
	public void setObservations(Observation[] observations) {
		this.observations = observations;
	}
	
	/**
	 * @param allObs the observations to get the Components from this ObsSeries
	 */
	public MaserComponent[] getAllCompsFromObs() {
		Observation[] allobs = getObservations();
		ArrayList<MaserComponent> allCompsFromObs=new ArrayList<MaserComponent>();
		for (int i = 0; i < allobs.length; i++) {
			MaserComponent[] compsFromObs = allobs[i].getComponents();
			if (compsFromObs!=null) {
				for (int j = 0; j < compsFromObs.length; j++) {
					allCompsFromObs.add(compsFromObs[j]);
				}
			}			
		}
		return  allCompsFromObs.toArray(new MaserComponent[allCompsFromObs.size()]);
	}

}
