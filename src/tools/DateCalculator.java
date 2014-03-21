package tools;

/**
 * Class contains some usefull DateCalculation Methods
 */
import static common.TEST.random;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

public class DateCalculator extends DateUtils {

	final static int DAYMILLIS = 24 * 3600 * 1000; // Day in Milliseconds

	// Common Era Start: 0001.01.01 as Milliseconds
	static final long CE_START_MILLIS = -(DateUtils
			.addYears(new Date(0), -1970).getTime());

	final GregorianCalendar jdStartDate;

	String formatPattern = "yyyy.MM.dd HH:mm:ss:SSS z G";

	int jp = 1; // Julian period 1 = [-4712-01-01 , 4713-01-01 ]

	final String BCE = "BCE"; // Before Common Era

	final String CE = "CE"; // Common Era

	final Locale LOCALE = Locale.ENGLISH; // 

	final TimeZone TIMEZONEID = TimeZone.getTimeZone("UTC");

	final DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);

	SimpleDateFormat formater = new SimpleDateFormat(formatPattern, dfs);

	
	static private Random randGen = new Random();
	

	final long JDZEROMILLIS = -210866760000000L; // JulianDayZero in

	int size = 10;

	ArrayList inputArray = new ArrayList(10);

	/**
	 * @param
	 * @return true if Timestamps are equal
	 */
	public static boolean compareTimestamps(Timestamp ts1, Timestamp ts2) {
		boolean compareTimestamps = false;
		String ts1Str = ts1.toString();
		String ts2Str = ts2.toString();
		// System.out.println("ts1Str"+ts1Str);
		// System.out.println("ts2Str"+ts2Str);
		return ts1Str.equals(ts2Str);
	}

	/**
	 * @param startDate,
	 *            endDate
	 * @return all days between start- & endDate
	 */
	public static Date[] dayDatesByRange(Date startDate, Date endDate) {
		ArrayList<Date> dateList = new ArrayList<Date>();
		Date[] dates = new Date[2];
		dates[0] = DateUtils.truncate(startDate, Calendar.DATE);
		dates[dates.length - 1] = DateUtils.truncate(endDate, Calendar.DATE);
		Date day = dates[0];
		// dateList.add(dates[0]);
		do {
			dateList.add(day);
			day = DateUtils.addDays(day, 1);
			// System.out.println( "currentDay"+day );
		} while (day.getTime() <= dates[dates.length - 1].getTime());
		return dateList.toArray(dates);
	}

	/**
	 * dayDelta Dates
	 * 
	 * @param date1
	 * @param date2
	 * @return delta in Days as int
	 */
	static public int dayDelta(Date date1, Date date2) {
		DateUtils.truncate(date1, Calendar.DATE);
		GregorianCalendar gCal1 = new GregorianCalendar();
		gCal1.setTimeInMillis(date1.getTime());
		gCal1.add(Calendar.DATE, 0);
		GregorianCalendar gCal2 = new GregorianCalendar();
		gCal2.setTimeInMillis(date2.getTime());
		gCal2.add(Calendar.DATE, 0);

		return dayDelta(gCal1, gCal2);
	}

	/**
	 * @param two
	 *            GregorianCalendar
	 * 
	 */
	/**
	 * @param cal1
	 * @param cal2
	 * @return delta in days
	 */
	public static int dayDelta(GregorianCalendar cal1, GregorianCalendar cal2)
			throws IllegalArgumentException {
		int delta = 0;
		Long longDelta = cal1.getTimeInMillis() - cal2.getTimeInMillis();
		Double wrk = longDelta.doubleValue() / (double) DAYMILLIS;
		wrk = Math.abs(wrk);
		longDelta = Math.round(wrk);

		// report Overflow Error
		// TODO refine ERROR-Condition
		if (longDelta > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Day-Delta bigger then Integer.MAX_VALUE overflow ERROR:"
							+ "\n\t input1: " + cal1 + "\n\t input2: " + cal2
							+ ")");
		}
		delta = longDelta.intValue();// wrong if longDelta > MAX_INT?
		// int delta2 = (int) longDelta;
		return delta;
	}

	/**
	 * main method for test and standalone purposes
	 * 
	 */
	public static void main(String[] args) {
		DateCalculator dc = new DateCalculator();
		// System.out.println("JulianDays: "+dc.julianDay2Date(20));
	}

	/**
	 * @param str
	 *            datestring in formatPattern "dd/MM/yy"
	 * @return Date Object from input
	 */
	public static Date parseFitsDate(String dateStr) throws IllegalArgumentException {
		Date parseDate = null;
		dateStr=dateStr.trim();
		String pattern = "dd/MM/yy";
		String seperator="/";
		if (StringUtils.countMatches(dateStr, "-")==2) {
			seperator="-";
			pattern=pattern.replaceAll("/", seperator);
		}
		
		String[] dateParts=dateStr.split(seperator);
		if (dateParts[2].length()==4) {
			dateStr=dateStr+"yy";
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			DateFormat formatter = new SimpleDateFormat(pattern);
			parseDate = (Date) format.parse(dateStr);
			if (parseDate == null) {
				throw new IllegalArgumentException(
						"Invalid DateString :"
								+ dateStr
								+ "\n\t not valid against SimpleDateFormat.applyPattern("
								+ pattern + ")");
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//UTC 
		GregorianCalendar cal=new GregorianCalendar(TimeZone.getTimeZone("UTC"),
                Locale.ENGLISH);
		cal.setTimeInMillis(parseDate.getTime());
		cal.add(Calendar.DATE, 0);
		parseDate = DateUtils.truncate(cal.getTime(), Calendar.DATE);
		return parseDate;
	}

	/**
	 * convenience method: create Random-DayDate
	 * 
	 * @param startDate
	 * @param dayLimit
	 *            max-amount of Day-Difference
	 * @param sign
	 *            +,-,* :positive, negative or random sign Difference
	 * @return
	 */
	static Date randomDayDate(Date startDate, int dayLimit, char sign) {
		int amount = random.nextInt(dayLimit);
		if (sign == '-')
			amount = amount * -1;
		if (sign == '*' && !random.nextBoolean())
			amount = amount * -1;
		return DateUtils.addDays(startDate, amount);
	}

	/**
	 * @param Date
	 *            to truncate millis from
	 * @return truncated Date
	 */
	public static Date truncateMillis(Date date) {
		return DateUtils.truncate(date, Calendar.SECOND);
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @param order
	 * @return
	 */
	public static Date[] uniqueRadomDayDates(Date startDate, Date endDate,
			boolean order) {
		int amount = dayDelta(startDate, endDate);
		return uniqueRadomDayDates(startDate, amount, order);
	}

	/**
	 * TODO finish & Test
	 * 
	 * @param c
	 * @param amount
	 * @param order
	 *            ordered or undordered return Array
	 * @return unique Array of ObjectValues
	 */
	public static Date[] uniqueRadomDayDates(Date startDate, int amount,
			boolean order) {
		if (startDate == null)
			throw new IllegalArgumentException("parameters must not be null");
		Date[] days = new Date[amount];
		ArrayList<Date> valueList = new ArrayList<Date>(amount);
		while (valueList.size() < amount) {
			Date ceStartDate = new Date(CE_START_MILLIS);
			int dayLimit = amount;// 365*8029; //Year 9999
			Date value = randomDayDate(startDate, dayLimit, '+');
			// Date value=(Date)
			// ToolBox.randomValueByClassName(Date.class.getName());
			// System.out.println("DateValue: "+value);
			Date day = DateUtils.truncate(value, Calendar.DATE);
			if (!valueList.contains(value))
				valueList.add(value);
		}
		return valueList.toArray(days);
	}

	/*
	 */

	//
	// helper-methods
	//


	/**
	 * @param ??
	 */
	public DateCalculator() {
		// -4712-01-01 J as day 0 or as day 1 in the first Julian period
		// wich is also written as 4713-01-01 B.C. UTC
		jdStartDate = new GregorianCalendar(TimeZone.getTimeZone("UTC"),
				Locale.ENGLISH);
		jdStartDate.set(Calendar.MILLISECOND, 0);
		jdStartDate.set(4713, 00, 01, 12, 00, 0);
		jdStartDate.set(Calendar.ERA, 0);
		jdStartDate.getTimeInMillis();
		jdStartDate.setTimeZone(TimeZone.getTimeZone("UTC"));
		formater.setTimeZone(TimeZone.getTimeZone("UTC"));
		formater.applyPattern(formatPattern);

		// PointsOfInterest
		GregorianCalendar cal = new GregorianCalendar(TimeZone
				.getTimeZone("UTC"), Locale.ENGLISH);// Today UTC
		inputArray.add(cal.getTimeInMillis());
		cal.setTimeInMillis(0); // UINXTIME Epoch=0
		inputArray.add(cal.getTimeInMillis());
		cal.setTimeInMillis(cal.getGregorianChange().getTime());// GregorinaChangeDate
		inputArray.add(cal.getTimeInMillis());
		inputArray.add(cal.getTimeInMillis() - (24 * 3600000));// GregorinaChangeDate-1Day
		inputArray.add(cal.getTimeInMillis() + (24 * 3600000));// GregorinaChangeDate+1Day
		inputArray.add(cal.getTimeInMillis() - (10 * 24 * 3600000));// GregorinaChangeDate-10Day
		inputArray.add(cal.getTimeInMillis() - (11 * 24 * 3600000));// GregorinaChangeDate-11Day
		inputArray.add(cal.getTimeInMillis() - (12 * 24 * 3600000));// GregorinaChangeDate-12Day
		inputArray.add(cal.getTimeInMillis() + (11 * 24 * 3600000));// GregorinaChangeDate+11Day
		inputArray.add(cal.getTimeInMillis() + (10 * 24 * 3600000));// GregorinaChangeDate+10Day
		inputArray.add(cal.getTimeInMillis() + (12 * 24 * 3600000));// GregorinaChangeDate+12Day
		inputArray.add(0);
		inputArray.add(1);
		inputArray.add(-1);
		inputArray.add(Long.MAX_VALUE);
		inputArray.add(Long.MIN_VALUE);
		inputArray.add(Integer.MAX_VALUE);
		inputArray.add(Integer.MIN_VALUE);
		inputArray.add(Long.MAX_VALUE - 1);
		inputArray.add(Long.MIN_VALUE - 1);
		inputArray.add(Integer.MAX_VALUE - 1);
		inputArray.add(Integer.MIN_VALUE - 1);
		inputArray.add(Long.MAX_VALUE + 1);
		inputArray.add(Long.MIN_VALUE + 1);
		inputArray.add(Integer.MAX_VALUE + 1);
		inputArray.add(Integer.MIN_VALUE + 1);
		inputArray.add(1);
		inputArray.add(0);
		inputArray.add(-1);
		// inputArray.add("teststring");

		// set Radom Values
		for (int i = 0; i < size; i++) {
			inputArray.add(randGen.nextLong());
		}

	}

	/**
	 * calculates JulianDay Number from Input
	 * 
	 * @param date
	 * @return int julianDays as Offset in days from -4712-01-01
	 */
	public int calendar2julianDay(GregorianCalendar date) {

		date.setTimeZone(TimeZone.getTimeZone("UTC")); // Universal Time Code
		jdStartDate.setTimeInMillis(JDZEROMILLIS);

		int retVal = 0;
		try {
			retVal = dayDelta(date, jdStartDate);
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
			throw e;
		}

		retVal = retVal * date.compareTo(jdStartDate);
		return retVal;
	}

	/**
	 * @param gC
	 * @return dateString in
	 */

	public String calendar2string(GregorianCalendar gC) {

		String str, era;
		str = formater.format(gC.getTime()); // translate

		if (gC.get(Calendar.ERA) == 0) { // SUFFIX :Before or After Common
			// Era
			era = BCE;
		} else {
			era = CE;
		}

		return str;
	}

	/**
	 * @param dateString
	 *            wich contains ERA
	 * @return ERA-Part from param by formatPattern from
	 *         java.text.SimpleDateFormat
	 */
	String getErafromDateStr(String dateStr) {

		// pos ?, length? language? TestMethod :BC,BCE,AD,
		String eraStr = splitDateStr(dateStr).get("G").toString();
		return eraStr;
	}

	/**
	 * @return the formatPatter
	 */
	public String getFormatPattern() {
		return formatPattern;
	}

	/**
	 * @return the jdStartDate
	 */
	public GregorianCalendar getJdStartDate() {
		return jdStartDate;
	}

	/**
	 * @param String
	 *            wich contains TimeZone
	 * @return TimeZone
	 */
	String getTZfromDateStr(String dateStr) throws IllegalArgumentException {
		// pos ?, length? language? TestMethod
		String tZ = "UTC";
		return tZ;
	}

	/**
	 * @param str
	 * @return Tell whether str parsing gives a valid TimeZoneID
	 */
	public boolean isTimeZone(String str) {
		String arr[] = TimeZone.getAvailableIDs();
		int i = Arrays.binarySearch(arr, str);
		boolean ret = (i >= 0) ? true : false;
		return ret;
	}

	/**
	 * @param Input:
	 *            jd Julian Day Number
	 * @return a <code>Date</code> <b><code>Date</code></b> as day
	 *         differenz from -4712-01-01
	 */
	public GregorianCalendar julianDay2Calendar(int jd) {
		GregorianCalendar wrkVal = (GregorianCalendar) jdStartDate.clone();
		wrkVal.add(Calendar.DATE, jd);
		return wrkVal;
	}

	public void printAllTimezones() {
		String arr[] = TimeZone.getAvailableIDs();
		Arrays.sort(arr = TimeZone.getAvailableIDs()); // Kleiner Hack

		for (int i = 0; i < arr.length; ++i) {
			System.out.println("id: " + i + " value: " + arr[i]);
		}
	}

	/**
	 * @param formatPattern
	 *            the formatPattern to set
	 */
	public void setFormatPattern(String formatPattern) {
		this.formatPattern = formatPattern;
	}

	/**
	 * @param dateStr
	 * @return Hashtable with Key=names of Dateprts from formatPattern, Values
	 *         from param split with formatPattern
	 */
	public Hashtable splitDateStr(String dateStr) {
		String[] partNames = formatPattern.replaceAll("[\\p{Punct}\\s]", "_")
				.split("_");
		String[] partValues = dateStr.replaceAll("[\\p{Punct}\\s]", "_").split(
				"_");
		Hashtable dateParts = new Hashtable();
		for (int i = 0; i < partNames.length; i++) {
			dateParts.put(partNames[i], partValues[i]);
		}

		HashMap retVal = new HashMap(dateParts);

		return dateParts;
	}

	/**
	 * @param str
	 *            datestring in formatPattern "yyyy.MM.dd HH:mm:ss:SSS z G"
	 *            example: 2000.11.11 23.59.59.9999 UTC BCE
	 * @return Calender Object from input TODO: parse with simpledateformat &
	 *         parsePatter valid Timezone & ERA
	 */
	public GregorianCalendar string2Calendar(String str)
			throws IllegalArgumentException {

		int pos = 0;

		ParsePosition parsePos = new ParsePosition(pos);

		formater = new SimpleDateFormat(formatPattern, dfs);

		Date parseDate = new Date();

		// valid dateStirng ?
		// ERA string where

		// TIMEZone: string or offset where in string ?
		parseDate = formater.parse(str, parsePos);
		// System.out.println(formater.toLocalizedPattern());

		if (parseDate == null) {
			throw new IllegalArgumentException("Invalid DateString :" + str
					+ "\n\t not valid against SimpleDateFormat.applyPattern("
					+ formatPattern + ")");
		}

		GregorianCalendar output = new GregorianCalendar(TIMEZONEID, LOCALE);
		output.setTime(parseDate);
		output.setTimeZone(TIMEZONEID);
		output.setTimeInMillis(output.getTimeInMillis());
		return output;
	}

}
