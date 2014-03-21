/**
 
 */
package tools;

import static common.TEST.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DateCalculatorTest extends Thread {

	private Random randGen = new Random(); // To generate Radom Numbers

	private DateCalculator dC = new DateCalculator(); // instance of CUT

	private Class CUT = dC.getClass();// Purpose??

	private GregorianCalendar testGcal = new GregorianCalendar(); // testCalendar

	final private long MILLIS; // base for testvalues

	final int JDEPOCH0 = 2440588; // JulinaDay of 1.1.1970 12:00:00:000 UTC CE

	// private Object[] point = new Object[2];//PoinOfInterest Pir of
	// (intVal,expectedVal)

	Hashtable<String, Comparable> point = new Hashtable<String, Comparable>(2);

	private ArrayList<Hashtable<String, Comparable>> points = new ArrayList();

	/**
	 * @param randGen
	 * @param dc
	 * @param testGcal
	 */
	public DateCalculatorTest() {
		long millis = randGen.nextLong();// tmpValue
		this.MILLIS = millis;
	}

	public DateCalculatorTest(long param) {
		long millis = param;// tmpValue
		this.MILLIS = millis;
	}

	/**
	 * Sets up the test fixture. (Called before every test case method.)
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randGen = new Random();
		dC = new DateCalculator();
		testGcal = new GregorianCalendar();
	}

	/**
	 * Tears down the test fixture. (Called after every test case method.)
	 */

	@After
	public void tearDown() {
		randGen = null;
		dC = null;
		testGcal = null;
	}

	/**
	 * Test method for
	 * {@link tools.DateCalculator#calendar2julianDay(java.util.GregorianCalendar)}.
	 * TODO Parameters Yes, type GregorianCalendar, special values:jd0, today,
	 * min,max, 0,1,-1 julian cutover, exceptions, failures, outoffrange
	 * ?expected valus extern
	 */
	// @Ignore("BUGGI")
	@runParameterized
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testCalendar2julianDay() {

		GregorianCalendar testInput = new GregorianCalendar(dC.TIMEZONEID,
				dC.LOCALE);
		testInput.setTimeInMillis(MILLIS);
		// System.out.println(MILLIS);
		// testInput.setTimeInMillis(9223372036854775807L);
		testInput.set(Calendar.HOUR_OF_DAY, 12);
		testInput.set(Calendar.MINUTE, 0);
		testInput.set(Calendar.SECOND, 0);
		testInput.set(Calendar.MILLISECOND, 0);
		testInput.getTimeInMillis();

		// MethodUnderTest
		int retVal = 0;
		try {
			retVal = dC.calendar2julianDay(testInput);
		} catch (IllegalArgumentException e) {
			System.out.println("catched : " + e.toString());
			return;
		}

		// expectedVal
		GregorianCalendar expectedVal = (GregorianCalendar) dC.jdStartDate
				.clone();
		expectedVal.setTimeInMillis(dC.JDZEROMILLIS);
		expectedVal.add(Calendar.DATE, retVal);

		// Check
		assertEquals("JulianDay: MILLIS: " + MILLIS + "\n", expectedVal,
				testInput);

		// Inverse-Function-Test
		GregorianCalendar expectedInverse = dC.julianDay2Calendar(retVal);
		// without Day Fractions!
		testInput.set(Calendar.HOUR_OF_DAY, 12);
		testInput.set(Calendar.MINUTE, 0);
		testInput.set(Calendar.SECOND, 0);
		testInput.set(Calendar.MILLISECOND, 0);
		testInput.getTimeInMillis();
		assertEquals(
				"JulianDay2Calendar() should inverse calendar2julianDay() {without Day Fractions!}  Input : "
						+ expectedVal + " \n", expectedInverse, testInput);

		// fail(" Under Construction !");
	}

	/**
	 * Test method for
	 * {@link tools.DateCalculator#calendar2string(java.util.GregorianCalendar)}.
	 * :TRICKY: ERA differs from Java Standart
	 */
	@runParameterized
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testCalendar2string() throws ParseException {

		testGcal = new GregorianCalendar(TimeZone.getTimeZone("UTC"),
				Locale.ENGLISH);
		testGcal.setTimeInMillis(MILLIS);// CalanderValue from ClassMember
		// MILLIS
		// the Method Under Test
		String cal2str = dC.calendar2string(testGcal);// MUT

		SimpleDateFormat formater = new SimpleDateFormat(dC.formatPattern,
				Locale.ENGLISH);

		int eraLength = dC.getErafromDateStr(cal2str).length();// BCE or CE
		int yearLength = ((Integer) testGcal.get(Calendar.YEAR)).toString()
				.length();

		// test string length against Format
		assertEquals("YEAR.MM.dd HH:mm:ss:SSS TimeZone ERA stringLength : ",
				yearLength + ".MM.DD HH:mm:ss:SSS UTC ".length() + eraLength,
				cal2str.length());

		// Validate Date via SimpleDateFormat.parse
		// special ERA treatment
		DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);

		String cal2strERA = cal2str.endsWith(dC.BCE) ? cal2str.replace(dC.BCE,
				dfs.getEras()[0]) : cal2str.replace(dC.CE, dfs.getEras()[1]);

		formater.applyPattern(dC.formatPattern);

		Date parsedDate = formater.parse(cal2str, new ParsePosition(0));

		assertNotNull("Parse Error INVALID Date : "
				+ formater.parse(cal2strERA, new ParsePosition(0)), formater
				.parse(cal2strERA, new ParsePosition(0)));

		// Date from ParsedString has to be the same as input to MethodUT
		GregorianCalendar expectedCal = new GregorianCalendar(TimeZone
				.getTimeZone("UTC"), Locale.ENGLISH);
		expectedCal.setTime(parsedDate);

		assertEquals(
				"Date from ParsedString has to be the same as input to MethodUT :",
				expectedCal, testGcal);

		// Inverse-Function-Test
		GregorianCalendar expectedInverse = dC.string2Calendar(cal2str);

		assertEquals(
				"calendar2string() should inverse string2Calendar() Input : ",
				expectedInverse, testGcal);

	}

	/**
	 * Test method for {@link tools.DateCalculator#dayDatesByRange(Date, Date)}
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testDayDatesByRange() {
		//TODO negativ amount, limits of Date Class
		int testruns = 2;
		for (int i = 0; i < testruns; i++) {
			int amount = random.nextInt(100000);
//			if (random.nextBoolean()) amount=amount*-1;
			long l=0;//random.nextLong()%(30*365*DateUtils.MILLIS_PER_DAY);
			Date startDate = DateUtils.truncate(new Date(l), Calendar.DATE);
			Date endDate = DateUtils.addDays(startDate, amount);
			// MUT
			Date[] returnDates = DateCalculator.dayDatesByRange(startDate,
					endDate);
			int delta = returnDates.length - 1;
			// the tests
			assertEquals("DayDelta :" + delta, Math.abs(amount), delta);
			assertFalse("null not allowed :",ArrayUtils.contains(returnDates, null));
			for (int j = 0; j < returnDates.length; j++) {
//				System.out.println("returnDates[j] :" +j+" "+returnDates[j]);
//				System.out.println("DateUtils.addDays(startDate, j) :" +DateUtils.addDays(startDate, j));
				assertTrue("DayDate must have 00h00m00s000millis:"+returnDates[j],returnDates[j].equals(DateUtils.truncate(returnDates[j], Calendar.DATE)));
				assertTrue("DayDate :"+returnDates[j],returnDates[j].equals(DateUtils.addDays(startDate, j)));
				assertEquals("DayDate :",DateUtils.truncate(new Date(startDate.getTime()+i*DateUtils.MILLIS_PER_DAY),Calendar.DATE), returnDates[i]);
			}
		}
	}

	/**
	 * Test method for
	 * {@link tools.DateCalculator#dayDelta(java.util.GregorianCalendar, java.util.GregorianCalendar)}.
	 */
	@runParameterized
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testDayDelta() {

		int expectedVal = randGen.nextInt();
		// int expectedVal = -1;

		GregorianCalendar input1 = new GregorianCalendar(dC.TIMEZONEID,
				dC.LOCALE);
		input1.setTimeInMillis(MILLIS);
		// input1.setTimeInMillis(input1.getGregorianChange().getTime());

		GregorianCalendar input2 = (GregorianCalendar) input1.clone();
		input2.add(Calendar.DATE, expectedVal);

		// MethodUnderTest
		int retVal = dC.dayDelta(input1, input2);

		assertEquals("returnValue == expectedValue ?! : ", Math
				.abs(expectedVal), retVal);

		// provoke Exception, Overflow if DayDelta > Integer.MAX_VALUE
		try {
			input1.setTimeInMillis(0);
			long intMax = (long) Integer.MAX_VALUE;
			long errorVal = ((intMax + 1) * dC.DAYMILLIS);
			input2.setTimeInMillis(errorVal);

			// MethodUnderTest
			retVal = dC.dayDelta(input1, input2);

		} catch (IllegalArgumentException e) {// only IllegalArgumentException
												// else ALARM ?!
			System.out.println("provoked IllegalArgumentException: "
					+ e.toString());
			throw e;

		}
	}

	/**
	 * Test method for {@link tools.DateCalculator#dayDatesByRange(Date, Date)}
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testDayDeltaDate() {
		//TODO negativ amount, limits of Date Class
		int testruns = 20000;
		for (int i = 0; i < testruns; i++) {
			int amount = random.nextInt(100000000);
		if (random.nextBoolean()) amount=amount*-1;
			long l=random.nextLong()%(280000000*365*DateUtils.MILLIS_PER_DAY);
			Date startDate = DateUtils.truncate(new Date(l), Calendar.DATE);
			Date endDate = DateUtils.addDays(startDate, amount);
			// MUT
			int delta = DateCalculator.dayDelta(startDate,endDate);
			// the test
			assertEquals("DayDelta :" + delta, Math.abs(amount), delta);
		}
	}

	/**
	 * Test method for
	 * {@link tools.DateCalculator#getErafromDateStr(java.lang.String)}.
	 */

	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetErafromDateStr() {
		String expectedVal = "BC";
		String testInput = "2000.11.11 23:59:59:998 UTC " + expectedVal;
		Hashtable wrkVal = dC.splitDateStr(testInput); // MUT
		Object retVal = wrkVal.get("G");

		assertEquals("Era-String:", expectedVal, retVal);
		// fail("! Under Construction !");
	}

	/**
	 * Test method for
	 * {@link tools.DateCalculator#getTZfromDateStr(java.lang.String)}. return
	 * a valid TimeZone or null
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGetTZfromDateStr() {
		String expectedVal = "UTC";
		String testInput = "2000.11.11 23:59:59:998 " + expectedVal + " BC";
		Hashtable wrkVal = dC.splitDateStr(testInput); // MUT
		Object retVal = wrkVal.get("z");

		assertEquals("Era-String:", expectedVal, retVal);
		// fail("! Under Construction !");
	}

	/**
	 * Test method for
	 * {@link tools.DateCalculator#getTZfromDateStr(java.lang.String)}. return
	 * a valid TimeZone or null
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testIsTimeZone() {
		String testInput = "totalerMia";

		boolean retVal = dC.isTimeZone(testInput);
		boolean expectedVal = TimeZone.getTimeZone(testInput).getID().equals(
				testInput);
		// GMT id DefaultError Value?!
		if (TimeZone.getTimeZone(testInput).getID().equals(testInput)) {
			expectedVal = true;
		} else {
			expectedVal = false;
		}
		// assertTrue("correct TimzoneID input :"+testInput,expectedValue);
		assertEquals("TimzoneID input :'" + testInput + "'", expectedVal,
				retVal);

		// fail("! Under Construction !");
	}

	@runParameterized
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testJulianDay2Calendar() {
		GregorianCalendar expectedVal = new GregorianCalendar(TimeZone
				.getTimeZone("UTC"), Locale.ENGLISH);

		// PointsOfInterest -> ArayList

		// point 0 element from classmeber MILLIS
		Integer testInput = new Long(MILLIS
				% (Integer.MAX_VALUE / dC.DAYMILLIS)).intValue();// JulianDays
		expectedVal.setTimeInMillis(0);
		expectedVal.set(Calendar.HOUR_OF_DAY, 12);
		expectedVal.getTimeInMillis();
		expectedVal.add(Calendar.DATE, (-JDEPOCH0 + testInput));
		expectedVal.getTimeInMillis();

		point.put("expectedVal", (GregorianCalendar) expectedVal.clone()); // ERROR
																			// ref
																			// not
																			// Value
		point.put("inputVal", testInput);
		points.add((Hashtable) point.clone());

		// point 1
		expectedVal = new GregorianCalendar(TimeZone.getTimeZone("UTC"),
				Locale.ENGLISH);
		expectedVal.set(1990, 01, 06, 12, 0, 0);
		expectedVal.set(Calendar.MILLISECOND, 0);
		expectedVal.add(Calendar.DATE, 0); // force recomputation of
											// Calendarfiels

		point.put("expectedVal", (GregorianCalendar) expectedVal.clone());
		point.put("inputVal", new Integer(2447929)); // by Hand
		points.add((Hashtable) point.clone());
		System.out.println(points.toString());

		// //point 2
		expectedVal = new GregorianCalendar(TimeZone.getTimeZone("UTC"),
				Locale.ENGLISH);
		expectedVal.set(1993, 04, 13, 12, 0, 0);
		expectedVal.set(Calendar.MILLISECOND, 0);
		expectedVal.getTimeInMillis();

		point.put("expectedVal", (GregorianCalendar) expectedVal);
		point.put("inputVal", new Integer(2449121)); // by Hand
		points.add((Hashtable) point.clone());

		for (Object element : points) { // iterate over Points of Interest

			point = (Hashtable) element;

			// MethodUnerTest
			testInput = (Integer) point.get("inputVal");
			GregorianCalendar retVal = dC.julianDay2Calendar(testInput);

			// expectedVal
			expectedVal = (GregorianCalendar) point.get("expectedVal");
			expectedVal.add(Calendar.DATE, 0); // force recomputation

			// check
			assertEquals("JulianDay2Calendar:", expectedVal, retVal);

			// check by Inverse-Function
			int expectedInverse = dC.calendar2julianDay(retVal);
			assertEquals("JulianDay2Calendar.calendar2julianDay:",
					expectedInverse, testInput);
		}
		// System.out.println("testInput: "+testInput+" retVal:
		// "+dC.calendar2string(retVal));
		// fail(" Under Construction: @runParameterized !");
	}

	/**
	 * JD startDate has to be "4713-01-01 12:00:00:000 UTC BC" !
	 */

	@Test(timeout = DEFAULT_TIMEOUT)
	public void testJulianStartdate() {

		testGcal = dC.getJdStartDate();
		final int expectedERA = 0; // B.C.E
		final int expectedYEAR = 4713;
		final int expectedMONTH = 0;
		final int expectedDAY_OF_MONTH = 1;
		final int expectedHOUR_OF_DAY = 12;
		final int expectedMINUTE = 0;
		final int expectedSECOND = 0;
		final int expectedMILLISECOND = 0;
		final String expectedTIMEZONE_ID = "UTC";
		final long expectedGetTimeinMillis = -210866760000000L;

		assertNotNull("NOT NULL: ", testGcal);
		assertEquals("ERA: ", expectedERA, testGcal.get(Calendar.ERA));
		assertEquals("YEAR: ", expectedYEAR, testGcal.get(Calendar.YEAR));
		assertEquals("MONTH: ", expectedMONTH, testGcal.get(Calendar.MONTH));
		assertEquals("DAY_OF_MONTH: ", expectedDAY_OF_MONTH, testGcal
				.get(Calendar.DAY_OF_MONTH));
		assertEquals("HOUR_OF_DAY: ", expectedHOUR_OF_DAY, testGcal
				.get(Calendar.HOUR_OF_DAY));
		assertEquals("MINUTE: ", expectedMINUTE, testGcal.get(Calendar.MINUTE));
		assertEquals("SECOND: ", expectedSECOND, testGcal.get(Calendar.SECOND));
		assertEquals("MILLISECOND: ", expectedMILLISECOND, testGcal
				.get(Calendar.MILLISECOND));
		assertEquals("TIMEZONE: ", expectedTIMEZONE_ID, testGcal.getTimeZone()
				.getID());
		assertEquals("getTimeInMillis: ", expectedGetTimeinMillis, testGcal
				.getTimeInMillis());
		// fail("Default Fail");
	}

	/**
	 * Test method for {@link tools.DateCalculator#dayDatesByRange(Date, Date)}
	 */
	@Test//(timeout = DEFAULT_TIMEOUT)
	public final void testParseFitsDate() {
		Date testDate = DateUtils.addMonths(new Date(0), 11);
		String[] testDateStrings={"01/12/70","01-12-70","01/12/1970"};
		int testruns = testDateStrings.length;
		for (int i = 0; i < testruns; i++) {
			Date expectedDate = DateUtils.truncate(testDate, Calendar.DATE);
			String dateStr=testDateStrings[i];
			// MUT
			Date returnDate = DateCalculator.parseFitsDate(dateStr);
			// the test
			assertEquals("testParseFitsDate :" + returnDate, expectedDate, returnDate);
		}
	}
	
	/**
	 * Test method for
	 * {@link tools.DateCalculator#splitDateStr(java.lang.String)}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testSplitDateStr() {
		String testInput = "2000.11.11 23:59:59:998 UTC BCE";
		// assozitiv array from formatPattern?? HashMap !
		// YEAR,MONTH,DAY_OF_MONTH,HOUR_OF_DAY,MINUTE,SECOND,MILLISECOND,TIMEZONE_ID
		String[] partNamesArr;
		partNamesArr = (dC.getFormatPattern()).split("[\\.:\\ ]");
		String[] partValuesArr;
		partValuesArr = testInput.split("[\\.:\\ ]");
		HashMap dateParts = new HashMap(partNamesArr.length);
		for (int i = 0; i < partNamesArr.length; i++) {
			dateParts.put(partNamesArr[i], partValuesArr[i]);
		}
		HashMap retVal = new HashMap(dC.splitDateStr(testInput));
		HashMap expectedVal = dateParts;

		assertEquals("DateParts:", expectedVal, retVal);
		// fail("Not yet implemented");
	}
	
	/**
	 * Test method for
	 * {@link tools.DateCalculator#string2Calendar(java.lang.String)}.
	 * 
	 */

	@runParameterized
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public void testString2Calendar() {

		// String dateStr = "2000.11.11 23:59:59:998 UTC CE";
		GregorianCalendar wrkCal = new GregorianCalendar(TimeZone
				.getTimeZone("UTC"), Locale.ENGLISH);
		wrkCal.setTimeInMillis(MILLIS);
		String dateStr = dC.calendar2string(wrkCal);
		// System.out.println("dC.string2Calendar(dateStr) Input: "+dateStr);

		// split the DateString in the DateParts and construct a
		// GegorianCalendar
		String wrkStr = dateStr.replace(" ", ".");
		wrkStr = wrkStr.replace(":", ".");
		String[] wrkArr = wrkStr.split("\\.");

		GregorianCalendar expectedGcal = new GregorianCalendar();
		expectedGcal.set(Calendar.YEAR, Integer.parseInt(wrkArr[0]));
		expectedGcal.set(Calendar.MONTH, Integer.parseInt(wrkArr[1]) - 1);
		expectedGcal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(wrkArr[2]));
		expectedGcal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(wrkArr[3]));
		expectedGcal.set(Calendar.MINUTE, Integer.parseInt(wrkArr[4]));
		expectedGcal.set(Calendar.SECOND, Integer.parseInt(wrkArr[5]));
		expectedGcal.set(Calendar.MILLISECOND, Integer.parseInt(wrkArr[6]));
		expectedGcal.setTimeZone(TimeZone.getTimeZone(wrkArr[7]));

		if (wrkArr[8].equals("AD")) {
			expectedGcal.set(Calendar.ERA, 1);
		} else {
			expectedGcal.set(Calendar.ERA, 0);
		}

		DateCalculator dC = new DateCalculator(); // CUT
		testGcal = dC.string2Calendar(dateStr); // MUT

		// System.out.println("testGcal :" + dC.string2Calendar(dateStr));

		// NEXT STEP:returned Calendar hast to be equal to here calculated
		// expectedGcal
		assertEquals("wrong calculation  dC.string2Calendar('dateStr')! \n",
				expectedGcal.getTimeInMillis(), testGcal.getTimeInMillis());

		// invalid Datestring has to throw IllegalArgumentException
		dateStr = "invalid";
		try {
			testGcal = dC.string2Calendar(dateStr);
		} catch (IllegalArgumentException e) {
			System.out.println("***IllegalArgumentException: " + e.toString());
			// e.printStackTrace();
			throw e;
		}
		//		
		// // Inverse-Function-Test
		// String expectedInverse = dC.calendar2string(testGcal);
		//
		// assertEquals(
		// "calendar2string() should inverse string2Calendar() Input : "
		// + dateStr + " \n", expectedInverse, dateStr);

		// fail("Under Construction");
	}
	

	/**
	 * Test method for {@link tools.DateCalculator#uniqueRadomDayDates(Date startDate, int amount,
			boolean order)}
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testUniqueRadomDayDates() {
		//TODO negativ amount, limits of Date Class
		int testruns = 10;
		int amount = random.nextInt(10*365);
		for (int i = 0; i < testruns; i++) {
			Date startDate=DateUtils.addDays(new Date(0), random.nextInt(amount));
			startDate = DateUtils.truncate(startDate, Calendar.DATE);
//			MUT
			Date[] returnedDates=DateCalculator.uniqueRadomDayDates(startDate,  amount, true);
//			test in range
			Date endDate = DateUtils.addDays(startDate, amount);			
			for (int j = 0; j < returnedDates.length; j++) {
				int delta = DateCalculator.dayDelta(startDate,returnedDates[i]);
				assertTrue("DayDelta :" + delta+"returnedDates[i]): "+returnedDates[i]
				, Math.abs(amount)>=delta);
			}	
			
//			test uniqeness			
			Date[] cloneDates=returnedDates.clone();
//			returnedDates[returnedDates.length/2]=returnedDates[1]; //provoke error
			for (int j = 0; j < cloneDates.length; j++) {
				assertEquals("date only once: "+cloneDates[j],
						ArrayUtils.indexOf(returnedDates, cloneDates[j]),
						ArrayUtils.lastIndexOf(returnedDates, cloneDates[j]));
			}
		}
	}
}
