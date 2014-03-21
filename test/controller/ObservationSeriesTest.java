package controller;

import static common.TEST.DEFAULT_TIMEOUT;
import static common.TEST.random;

import static common.TEST.random;
import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import model.CelestialObject;
import model.CelestialObjectTest;
import model.DBTable;
import model.DataBase;
import model.Observation;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.DateCalculator;
import tools.ToolBox;


public class ObservationSeriesTest {
	static DataBase db;

	CelestialObject celestialObject;

//	CelestialObject[] celObjects;

	DBTable celestialObjectsTable;

	DBTable obsTable;

	ObservationSeries obsSeries;

	private static int numberOfObs;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// DB
		db = new DataBase(true, common.TEST.dbURL, common.TEST.dbFile);
		// CelestialObject
		// Observations
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// DB cleanup
		db.dropTables();
		db.shutdownDB();
		db = null;

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		celestialObjectsTable = new DBTable("CELESTIALOBJECTS");
		// random CelestialObject
		HashMap<String, Object> row = CelestialObjectTest.randomCelObjRow();
		celestialObjectsTable.insertMap2DBTable(row);
		celestialObject = new CelestialObject(row);
		// random Observations
		// TODO dates in range
		obsTable = new DBTable("OBSERVATIONS");
		numberOfObs = random.nextInt(100) + 1;
		HashMap<String, Object> link2TableRow = celestialObject.link2TableRow(null);
		celestialObject.setDataMap(link2TableRow);
		ToolBox.insertObservationRows(numberOfObs, obsTable, celestialObject
				.getID(), null);
		HashMap<String, Object>[] rows = obsTable.getRows();
		HashMap<String, String> ts = obsTable.getTableStruct();
		
		Observation[] observations = new Observation[numberOfObs];
		for (int i = 0; i < observations.length; i++) {
			observations[i] = new Observation(rows[i],false);
		}
		celestialObject.setObseravtions(observations);
		// celestialObject.getObseravtions();
		// System.out.println(celestialObject.getObseravtions());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		celestialObjectsTable.deleteRows();
		obsTable.deleteRows();
		celestialObject=null;
		obsSeries =null;
	}

	/**
	 * Test method for
	 * {@link controller.ObservationSeries#ObservationSeries(model.CelestialObject, java.util.Date[])}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testObservationSeriesCelestialObjectDateArray()
			throws Exception {

		// all Dates from celestialObjects
		Observation[] expectedObs = celestialObject.getObseravtions();
		Date[] obsDates = new Date[expectedObs.length];
		for (int i = 0; i < obsDates.length; i++) {
			obsDates[i] = expectedObs[i].getDate();
		}
		// MUT
		obsSeries = new ObservationSeries(celestialObject, obsDates);
		Observation[] retrunObs = obsSeries.getObservations();
		assertEquals("same Observations: ", expectedObs, retrunObs);

		// subset of Observations in celestialObjects
		// System.out.println("all obsDates: "+Arrays.toString(obsDates));
		int subsetLength = random.nextInt(expectedObs.length);
		Observation[] subsetOfObs = new Observation[subsetLength];
		System.arraycopy(expectedObs, 0, subsetOfObs, 0, subsetLength);
		obsDates = new Date[subsetOfObs.length];
		for (int i = 0; i < obsDates.length; i++) {
			obsDates[i] = subsetOfObs[i].getDate();
		}
		// System.out.println("subset of obsDates: "+Arrays.toString(obsDates));
		// MUT
		obsSeries = new ObservationSeries(celestialObject, obsDates);
		retrunObs = obsSeries.getObservations();
		assertEquals("subset of Dates Observations: ", subsetOfObs, retrunObs);

		// more Dates of Observations as in celestialObjects
		System.out.println("all obsDates: " + Arrays.toString(obsDates));
		int supersetLength = expectedObs.length
				+ random.nextInt(expectedObs.length) + 1;
		Observation[] supersetObs = new Observation[supersetLength];
		System.arraycopy(expectedObs, 0, supersetObs, 0, expectedObs.length);
		Arrays.sort(obsDates);
		Date maxDate = (obsDates.length>0)?  
								obsDates[obsDates.length-1]:new Date(0);
		obsDates = new Date[supersetLength];
		// all ObsDates from CelestialObject
		for (int i = 0; i < expectedObs.length; i++) {
			obsDates[i] = supersetObs[i].getDate();
		}
//		System.out.println(ToolBox.getCurrentMethodName()+" maxDate: "+maxDate);
		// more random ObsDates bigger then the available dates
		
		Date[] radomDates = DateCalculator.uniqueRadomDayDates(
				DateUtils.addDays(maxDate, 100),
				supersetLength - expectedObs.length, false);
		
		System.arraycopy(radomDates, 0, obsDates, expectedObs.length,
				radomDates.length);

		 System.out.println("SuperSet of obsDates:"
				 +Arrays.toString(obsDates));
		Date[] expectedDates = obsDates.clone();
		// MUT
		obsSeries = new ObservationSeries(celestialObject, obsDates);
		retrunObs = obsSeries.getObservations();
		if (ArrayUtils.contains(supersetObs, null))
			System.out.println("testObservationSeriesCelestialObjectDateArray()" +
					" supersetObs: "+ Arrays.toString(supersetObs));
		for (int i = 0; i < supersetLength; i++) {
			assertEquals("SuperSet of Dates for Observations: ",
					expectedDates[i], retrunObs[i].getDate());
			assertEquals("ObjectID: ", celestialObject.getID(), retrunObs[i]
					.getObjectId());
			if (i > expectedObs.length) { // the Observations to be
											// interpolated
				assertEquals("the Observations to be interpolated: ", true,
						retrunObs[i].getInterpolated());
			}
		}
	}

	/**
	 * Test method for {@link controller.ObservationSeries#
	 * ObservationSeries(model.CelestialObject, java.util.Date,
	 * java.util.Date)}.
	 * 
	 * @throws Exception
	 */
	@Test
	// (timeout = DEFAULT_TIMEOUT)
	public final void testObservationSeriesCelestialObjectDateDate()
			throws Exception {
		int testRuns=1;
		for (int j = 0; j < testRuns; j++) {
			celestialObjectsTable.deleteRows();
			obsTable.deleteRows();
			Date startDate = new Date(0);
			Date endDate = DateUtils.addDays(startDate, 20);
			Date[] obsDates = DateCalculator
					.dayDatesByRange(startDate, endDate);
			ObservationSeries obsSeriesByDates = new ObservationSeries(
					celestialObject, obsDates);
			Observation[] expectedObs = obsSeriesByDates.getObservations();
			System.out.println(ToolBox.getCurrentMethodName()
					+ " expectedObs: " + expectedObs);
			//		 MUT
			ObservationSeries obsSeriesByDateRange = new ObservationSeries(
					celestialObject, startDate, endDate);
			Observation[] returnedObs = obsSeriesByDateRange.getObservations();
			System.out.println(ToolBox.getCurrentMethodName()
					+ " returnedObs: " + returnedObs);
			// TODO same, interpolated
			for (int i = 0; i < returnedObs.length; i++) {
				if (!(Boolean) returnedObs[i].getInterpolated()) {
					assertTrue(
							"obsSeriesByDates =? obsSeriesByDateRange, index: "
									+ i, ArrayUtils.contains(expectedObs,
									returnedObs[i]));
				} else {
					assertEquals("this objectID: , index: "+i, obsSeriesByDates
							.getCelestialObject().getID(), returnedObs[i]
							.getObjectId());
				}
			}
		}		
	}

	/**
	 * Test method for {@link controller.ObservationSeries#getCelestialObject()}.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetSetCelestialObject() throws Exception {
		HashMap<String, Object> testData = celestialObjectsTable
				.getTableStructAsContainerMap();
		String name = (String) ToolBox
				.randomValueByClassName("java.lang.String");
		testData.put("name", name);
		CelestialObject celestialObject = new CelestialObject(testData,false);//update 
		// MUT
		obsSeries = new ObservationSeries();
		obsSeries.setCelestialObject(celestialObject);
		CelestialObject returnCelestialObject = obsSeries.getCelestialObject();
		// the Tests
		assertEquals("ObjectHash: ", celestialObject.hashCode(),
				returnCelestialObject.hashCode());
		assertEquals("NAME String: ", name, returnCelestialObject.getName());
	}

	/**
	 * Test method for
	 * {@link controller.ObservationSeries#setObservationsDates(java.util.Date[])}.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testSetObservationsDates() throws Exception {
		obsSeries = new ObservationSeries();
		Date[] observationsDates = DateCalculator.uniqueRadomDayDates(new Date(
				0), random.nextInt(100), false);
		// MUT
		obsSeries.setObservationsDates(observationsDates);
		Date[] returnDates = obsSeries.getObservationsDates();
		// the Tests
		assertEquals("observationsDates: ", observationsDates, returnDates);
	}

	/**
	 * Test method for
	 * {@link controller.ObservationSeries#getObservationValuesByKey(java.util.Date[])}.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetObservationValuesByKey() throws Exception {
		// TODO full dataMap from Observation, fill with RandomValues from Range
		// ID? OBJECT_ID? instrument? date ! noise! interpolated ?"

		Date[] observationsDates = DateCalculator.uniqueRadomDayDates(new Date(
				0), random.nextInt(100), false);

		// all Dates from celestialObjects
		Observation[] expectedObs = celestialObject.getObseravtions();
		Date[] obsDates = new Date[expectedObs.length];
		for (int i = 0; i < obsDates.length; i++) {
			obsDates[i] = expectedObs[i].getDate();
		}

		obsSeries = new ObservationSeries(celestialObject, obsDates);
		// DB or CelestObj, more Obs then in DB?
		Set<String> fieldNames = obsSeries.getObservations()[0].getDataMap()
				.keySet();
		for (String key : fieldNames) {
			Object[] expectedValues = new Object[numberOfObs];
			for (int i = 0; i < expectedValues.length; i++) {
				expectedValues[i] = obsSeries.getObservations()[i].getDataMap()
						.get(key);
			}
			// MUT
			Object[] returnValues = obsSeries.getObservationValuesByKey(key);
			// the Tests
			assertEquals("amount of Values: ", numberOfObs, returnValues.length);
//			System.out
//					.println("testGetObservationValuesByKey().expectedValues "
//							+ key + ": " + Arrays.toString(expectedValues));
//			System.out.println("testGetObservationValuesByKey().returnValues "
//					+ key + ": " + Arrays.toString(returnValues));
			assertEquals("Value to Key: " + key, expectedValues, returnValues);
		}

	}

	/**
	 * Test method for
	 * {@link controller.ObservationSeries#getObservationValuesRange(java.lang.String)}.
	 * 
	 * @throws Exception
	 */
	@Test//(timeout = DEFAULT_TIMEOUT)
	public final void testGetObservationValuesRange() throws Exception {
		// generate random Hashmap for obsTable
		DBTable obsTable = new DBTable("OBSERVATIONS");
		HashMap<String, Object> obsMap = obsTable
				.getTableStructAsContainerMap();
		Set<String> keys = obsMap.keySet();
		// Random objectValues

		// TODO set Ranges for date, noise
		// DateRange
		Date startDate = DateUtils.truncate(new Date(0), Calendar.DATE);
		Date endDate = new Date(DateUtils.MILLIS_PER_DAY * 365 * 10);
		Date[] dateRange = { startDate, endDate };
		Double[] noiseRange={ 0.0, 0.5 };
//		System.out.println("testGetObservationValuesRange() dateRange: "
//				+ Arrays.toString(dateRange));
		obsTable.deleteRows();
		ToolBox.insertObservationRows(numberOfObs, obsTable, celestialObject
				.getID(), dateRange, noiseRange);
		
		Observation[] observations = new Observation[numberOfObs];
		HashMap<String, Object>[] rows = obsTable.getRows();
		for (int i = 0; i < observations.length; i++) {
			observations[i] = new Observation(rows[i],false);
		}
		
		celestialObject.setObseravtions(observations);
		Date testDate = (Date) ToolBox.randomValueByClassInRange(Date.class,
				dateRange[0], dateRange[1]);
		Object value;
		for (String key : keys) {
			value = ToolBox.randomValueByClassName(obsMap.get(key).getClass()
					.getName());
			if (key.equals("date"))
				value = testDate;
//			System.out.println("testGetObservationValuesRange value: " + value);
			obsMap.put(key, value);
		}

		// all Dates from celestialObjects
		Observation[] expectedObs = celestialObject.getObseravtions();
		Date[] obsDates = new Date[expectedObs.length];
		for (int i = 0; i < obsDates.length; i++) {
			obsDates[i] = expectedObs[i].getDate();
		}
		obsSeries = new ObservationSeries(celestialObject, obsDates);
		// set randomValues in Range,for all keys in Obs do getRange
		keys = expectedObs[0].getDataMap().keySet();
		for (String key : keys) {
			Object[] returnValues = obsSeries.getObservationValuesRange(key);
//			System.out.println("returnValues_" + key + ": "
//					+ Arrays.toString(returnValues)
//					+ returnValues[0].getClass());
			// dateRange
			if (key.equalsIgnoreCase("date")) {
				int compare = ((Date) returnValues[0])
						.compareTo(dateRange[0]);
				
				assertEquals("dateRange min: " + dateRange[0]
						+ " returnVlaue min: " + returnValues[0], true,
						(compare == 0 || compare == 1));
				compare = ((Date) returnValues[1]).compareTo(dateRange[1]);
				assertEquals("dateRange max: " + dateRange[1]
				       	+ " returnVlaue max: " + returnValues[1], true,
				       		(compare == 0 || compare == -1));
			}
			
//			 noiseRange
			if (key.equalsIgnoreCase("noise")) {
				int compare = ((Double) returnValues[0])
						.compareTo(noiseRange[0]);
				assertEquals("noiseRange min: " + noiseRange[0]
						+ " returnVlaue min: " + returnValues[0], true,
						(compare == 0 || compare == 1));
				compare = ((Double) returnValues[1]).compareTo(noiseRange[1]);
				assertEquals("noiseRange max: " + dateRange[1]
				       	+ " returnVlaue max: " + returnValues[1], true,
				       		(compare == 0 || compare == -1));
			}
		}
	}

}
