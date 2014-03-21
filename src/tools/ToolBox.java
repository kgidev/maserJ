/**
 *
 */
package tools;

import static common.TEST.USERDIR;
import static common.TEST.random;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.DBTable;
import nom.tam.fits.BasicHDU;
import nom.tam.fits.Fits;
import nom.tam.fits.Header;
import nom.tam.util.BufferedFile;

import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * Collection of static convenience and helper methods
 */

public class ToolBox extends Thread {

	static public boolean debugFlag = false;

	static public final String LINEBREAK = System.getProperty("line.separator");

	static public final String SEPERATOR = System.getProperty("file.separator");

	static Thread thread = currentThread();

	static StackTraceElement[] threadStack;

	static public final String TMPDIR = System.getProperty("java.io.tmpdir");

	static public int verboseMode = 0;

	/**
	 * convenience method compare two HashMaps<String, Object> by Keys & Values
	 * (Objects must not be nested)
	 * 
	 * @param map1
	 * @param map2
	 * @param keysCaseSensitve
	 *            compare with casesensitive keys or not
	 * @return
	 */
	public static boolean compareHashMaps(HashMap<String, Object> map1,
			HashMap<String, Object> map2, boolean keysCaseSensitve) {
		boolean compareHashMaps = true;
		HashMap<String, Object> lokalMap1 = (HashMap<String, Object>) map1
				.clone();
		HashMap<String, Object> lokalMap2 = (HashMap<String, Object>) map2
				.clone();
		Set<String> keys1 = lokalMap1.keySet();
		Set<String> keys2 = lokalMap2.keySet();
		String[] keyArray1 = keys1.toArray(new String[0]);

		if (!keysCaseSensitve) {
			for (Iterator iter = keys2.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				key = key.toLowerCase();
			}
			for (Iterator iter = keys1.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				key = key.toLowerCase();
			}
		}
		// different size
		if (keys1.size() != keys2.size())
			return false;

		for (int i = 0; i < keyArray1.length; i++) {
			// keys
			// System.out.println("key:" + keyArray1[i]);
			compareHashMaps = lokalMap2.containsKey(keyArray1[i]);
			if (compareHashMaps == false)
				break;
			// values
			Class clazz = lokalMap1.get(keyArray1[i]).getClass();
			if (clazz == java.sql.Timestamp.class) {
				Timestamp ts1 = new Timestamp(((Timestamp) lokalMap1
						.get(keyArray1[i])).getTime());
				Timestamp ts2 = new Timestamp(((Timestamp) lokalMap2
						.get(keyArray1[i])).getTime());
				
				compareHashMaps = DateCalculator.compareTimestamps(ts1, ts2);
				if (compareHashMaps == false)
					break;
				else
					continue;
			}

			
			compareHashMaps = (lokalMap1.get(keyArray1[i]).equals(lokalMap2
					.get(keyArray1[i])));
			if (compareHashMaps == false)
				break;
		}

		return compareHashMaps;
	}

	/**
	 * convenience method Create DirStruct with mutiple exampleFiles in subdirs.
	 * 
	 * @return
	 * @throws IOException
	 */

	public static File[] createDirStruct(String startDirName, int depth, int width,
			String[] filePathes, boolean randomName) throws IOException {
		// TODO wich files, performance and size
		if (filePathes.length == 0) {
			throw new IllegalArgumentException("filePathes empty!");
		}
		// validate Input
		File dir = new File(startDirName);
		File firstPath = new File("");
		Collection<File> expectedFiles = new ArrayList<File>();
		for (int i = 0; i < depth; i++) {
			if (randomName)
				// generate radom dirStruct and copy inputfiles there
				startDirName += RandomStringUtils.randomAlphabetic(12);
			else
				startDirName += "ObjectName";
			if (i == 0)
				firstPath = new File(startDirName);
			startDirName += SEPERATOR;
			startDirName += randomDateString(); // radom Date
			// System.out.println(startDirName);
			startDirName += SEPERATOR;
			dir = new File(startDirName);
			dir.mkdirs();
			for (int j = 0; j < filePathes.length; j++) {
				FileUtils.copyFileToDirectory(new File(filePathes[j]), dir);
			}

			File file = new File(startDirName
					+ new File(filePathes[0]).getName());
			expectedFiles.add(file);
		}
		// System.out.println("startDirName: "+startDirName);
		dir = new File(startDirName);
		dir.mkdirs();
		File[] expectedVal = new File[expectedFiles.size()];
		expectedVal = expectedFiles.toArray(expectedVal);
		Arrays.sort(expectedVal);
		return expectedVal;
	}

	/**
	 * convenience method Create random DirStruct with mutiple exampleFiles in
	 * subdirs.
	 * 
	 * @return the createdFiles as Array
	 * @throws IOException
	 */
	
	public static File[] createObservationDirStruct(File obsDir) throws IOException {
		return createObservationDirStruct(obsDir, 10, CrossPlatform
				.path(USERDIR + "/test/examples/observation.fits"));
	}

	/**
	 * convenience method Create random DirStruct with mutiple exampleFiles in
	 * subdirs.
	 * @param numberOfFiles TODO
	 * @param FilePat TODO
	 * 
	 * @return the createdFiles as Array
	 * @throws IOException
	 */

	public static File[] createObservationDirStruct(File obsDir, 
			int numberOfFiles, String FilePath) throws IOException {
		File[] files = new File[numberOfFiles];
		
		for (int i = 0; i < files.length; i++) {
			files[i] = createDirStruct(obsDir.getAbsolutePath()
					+ File.separator, 1, 1, new String[] { FilePath }, false)[0];
		}
		return files;
	}

	public static void debugMsg(Object msgObj) {

		if (debugFlag) { // show or not

			String message = msgObj.toString();

			// thread.dumpStack();
			threadStack = thread.getStackTrace();
			// System.out.println("threadStack length: " + threadStack.length);
			String parentName = "";
			String initStr = "<init>";
			String msgCaller = "";
			for (StackTraceElement element : threadStack) {
				parentName = element.getMethodName();

				if (parentName.equals(initStr)) {
					// System.out.println(parentName);
					msgCaller = element.getFileName() + " Line: "
							+ element.getLineNumber();

					break;
				}
			}

			GregorianCalendar cal = new GregorianCalendar();
			String msgTime = cal.getTime().toString();

			String prefix = "DEBUG MESSAGE START from :'" + msgCaller
					+ "' at '" + msgTime + "' : ";
			String suffix = "DEBUG MESSAGE END from :'" + msgCaller + "'"
					+ LINEBREAK;
			System.out.println(prefix);
			System.out.println("\t'" + message + "'");
			System.out.println(suffix);
		}
	}

	/**
	 * convenience method dump a HashMap
	 */
	public static void dumpHahsMap(HashMap map) {
		System.out.println("HashMap keys: " + map.keySet().toString());
		System.out.println("HashMap values: " + map.values().toString());
	}

	static public String getCurrentMethodName() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		(new Throwable()).printStackTrace(pw);
		pw.flush();
		String stackTrace = baos.toString();
		pw.close();
		StringTokenizer tok = new StringTokenizer(stackTrace, "\n");
		String l = tok.nextToken(); // 'java.lang.Throwable'
		l = tok.nextToken(); // 'at ...getCurrentMethodName'
		l = tok.nextToken(); // 'at ...<caller to getCurrentRoutine>'
		// Parse line 3
		tok = new StringTokenizer(l.trim(), " <(");
		String t = tok.nextToken(); // 'at'
		t = tok.nextToken(); // '...<caller to getCurrentRoutine>'
		return t;
	}

	/**
	 * 
	 * @param obj
	 * @return name of the calling method, from the input class object
	 */
	public static String getMethodName(Object obj) {
		String name = "";
		String elementName = "";
		String thisName = "getMethodName";
		// thread.dumpStack();

		// Caller is direct successor in Threadstack
		threadStack = thread.getStackTrace();
		for (int i = 0; i < threadStack.length; i++) {
			elementName = threadStack[i].getMethodName();

			if (elementName.equals(thisName)) {
				name = threadStack[i + 1].getMethodName();
				break;
			}

		}

		return name;
	}

	/**
	 * 
	 * @param context
	 *            to search in, tagname
	 * @return Text of (first occurence!) tag if in <tt>context</tt>, null
	 *         else
	 * @throws Exception
	 *             TODO occurence ?
	 */
	public static String getXMLTag(String context, String tagName)
			throws Exception {

		String regexpForFontAttrib = "(\\s*([a-z]+)\\s*=\\s*\"([^\"]+)\")*";// ???
		String openTag = "<\\s*" + tagName + "\\s*([^>]*)\\s*>";// ???
		String endTag = "</\\s*" + tagName + "\\s*>";
		Pattern p = Pattern.compile(openTag + "([^>]+)" + endTag);
		Matcher mat = p.matcher(context);
		mat.find();
		String tagStr = mat.group().toString();

		return tagStr;
	}

	/**
	 * 
	 * @param context
	 *            to search in, tagname, position of tag in context
	 * @return TextContent of XML-tag on position index, if in <tt>context</tt>,
	 *         null else
	 * @throws Exception
	 */
	public static String getXMLTagByPos(String context, String tagName,
			int index) throws Exception {
		ArrayList<String> retList = getXMLTagList(context, tagName, "");

		return retList.get(index);
	}

	/**
	 * 
	 * @param context
	 *            to search in, tagname
	 * @return TextContent of first occurences from XML-tag, if in
	 *         <tt>context</tt>, null else
	 * @throws Exception
	 */
	public static String getXMLTagContent(String context, String tagName)
			throws Exception {
		String value = null;
		value = getXMLTagContentByPos(context, tagName, 0);
		return value;
	}

	/**
	 * 
	 * @param context
	 *            to search in, tagname, position of tag in context
	 * @return TextContent of one occurences(by pos) from XML-tag, if in
	 *         <tt>context</tt>, null else
	 * @throws Exception
	 */
	public static String getXMLTagContentByPos(String context, String tagName,
			int pos) throws Exception {
		String[] valueArr = new String[0];
		valueArr = getXMLTagContentList(context, tagName).toArray(valueArr);
		// parameterValidation

		if (pos > valueArr.length - 1)
			throw new IllegalArgumentException("no such column-Position : "
					+ pos);

		String value = valueArr[pos];
		return value;
	}

	/**
	 * 
	 * @param context
	 *            to search in, tagname
	 * @return TextContent of XML-tag, if in <tt>context</tt>, null else
	 * @throws Exception
	 */
	public static ArrayList<String> getXMLTagContentList(String context,
			String tagName) throws Exception {
		return getXMLTagList(context, tagName, "onlyContent");
	}

	/**
	 * 
	 * @param context
	 *            to search in, tagname, position of tag in context
	 * @return TextContent of one or all occurences from XML-tag, if in
	 *         <tt>context</tt>, null else
	 * @throws Exception
	 */
	public static ArrayList<String> getXMLTagList(String context,
			String tagName, String type) throws Exception {

		String attribRegex = "(\\s*(\\w+)\\s*=\\s*\"([^\"]+)\")*";
		String openTag = "<\\s*" + tagName + "\\s*([^>]*)\\s*>";
		String endTag = "</\\s*" + tagName + "\\s*>";
		String pattern = openTag + "([^>]+)" + endTag;
		if (type == "onlyAttributes") {
			pattern = attribRegex;
		}
		Pattern p = Pattern.compile(pattern);
		Matcher mat = p.matcher(context);

		ArrayList<String> retList = new ArrayList<String>();
		while (mat.find()) {
			String tagStr = mat.group();
			if (type == "onlyContent") {
				tagStr = tagStr.replaceAll(openTag, "");
				tagStr = tagStr.replaceAll(endTag, "");
			}
			if (!tagStr.equals("")) {
				retList.add(tagStr);
			}

		}

		return retList;
	}

	/**
	 * insert mutiple Rows to Table Observations
	 * 
	 */
	public static void insertObservationRows(int numberOfObs, DBTable obsTable,
			int object_id) throws Exception {
		insertObservationRows(numberOfObs, obsTable, object_id, null, null);
	}

	/**
	 * insert mutiple Rows to Table Observations
	 * 
	 */
	public static void insertObservationRows(int numberOfObs, DBTable obsTable,
			int object_id, Date[] observationDateRange) throws Exception {
		insertObservationRows(numberOfObs, obsTable, object_id,
				observationDateRange, null);
	}

	/**
	 * onvenience method insert mutiple Test-Rows to Table Observations
	 * 
	 * @param numberOfObs
	 * @param obsTable
	 * @param object_id
	 * @param observationDateRange
	 * @throws Exception
	 */
	public static void insertObservationRows(int numberOfObs, DBTable obsTable,
			int object_id, Date[] observationDateRange, Double[] noiseRange)
			throws Exception {
		Date startDate = new Date(0);
		// int dayLimit = 10 * 365;
		// char sign = '+';
		Date[] obsDays;
		if (observationDateRange == null) {
			obsDays = DateCalculator.uniqueRadomDayDates(startDate,
					numberOfObs, false);
		} else {
			obsDays = DateCalculator.uniqueRadomDayDates(
					observationDateRange[0], observationDateRange[1], false);
		}

		for (int i = 0; i < numberOfObs; i++) {
			HashMap<String, Object> randRow = randomRow(obsTable
					.getTableStruct());
			randRow.remove("ID");
			String[] columns = new String[randRow.size()];
			columns = randRow.keySet().toArray(columns);
			Object[] values = new Object[randRow.size()];
			values = randRow.values().toArray(values);
			int keyPos = ArrayUtils.indexOf(columns, "object_id");
			values[keyPos] = object_id; // object_id
			// Date
			keyPos = ArrayUtils.indexOf(columns, "date");
			java.sql.Date obsDate = new java.sql.Date(obsDays[i].getTime());
			values[keyPos] = obsDate.toString();
			if (noiseRange != null) {
				// noise
				keyPos = ArrayUtils.indexOf(columns, "noise");
				Double noise = (Double) randomValueByClassInRange(Double.class,
						noiseRange[0], noiseRange[1]);
				values[keyPos] = noise;
			}
			obsTable.insertRow(columns, values);
		}
	}

	/**
	 * @param c
	 *            the class to test
	 * @return true if c has compareTo Method
	 */
	public static boolean isComparable(Class c) {

		Method compareMethod = null;
		try {
			compareMethod = c.getMethod("compareTo", new Class[] { c });
			// System.out.println("compareTo" + compareMethod);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return (compareMethod != null);
	}

	/**
	 * @return the debugFlag
	 */
	public static boolean isDebugFlag() {
		return debugFlag;
	}

	/**
	 * return (filtered) List of pathnames in dirPath
	 */
	public static String[] listDir(String dirPath, FilenameFilter filter) {
		File file = new File(dirPath);
		// parameterValidation
		if (!file.exists())
			throw new IllegalArgumentException("no such path: " + dirPath);
		String[] dirList = new File(dirPath).list();

		if (filter.equals("none")) {
			dirList = new File(dirPath).list();
		} else {
			dirList = new File(dirPath).list(filter);
		}
		return dirList;
	}

	/**
	 * ListDirectoryEntrys in dir
	 * 
	 */

	public static String[] listDirectorys(String dir) {
		// parameterValidation
		File f = new File(dir);
		if (!f.exists())
			throw new IllegalArgumentException("no such path: " + dir);

		String[] localList = listDir(dir, new FileFilter("directoryFilter"));
		// System.out.println("localDir " + path + ": "
		// + Arrays.deepToString(localList));// show current dir-list
		return localList;
	}

	/**
	 * List Files by Filter in dir
	 * 
	 */

	public static String[] listFilesByFilter(String dir, String filterName) {
		// parameterValidation
		File f = new File(dir);
		if (!f.exists())
			throw new IllegalArgumentException("no such path: " + dir);

		String[] localList = listDir(dir, new FileFilter(filterName));
		// System.out.println("localDir " + path + ": "
		// + Arrays.deepToString(localList));// show current dir-list
		return localList;
	}

	/**
	 * List recursive Files from start dir filtered by filterName
	 * 
	 * @return Array of filenames
	 * @throws Exception
	 */

	public static String[] listFilesRecursiveByFilter(String dir,
			boolean pathNameFlag, String filterName) throws Exception {

		String[] pathes = traverseDirTree(dir, true, filterName,
				new ArrayList<String>());
		ArrayList<String> files = new ArrayList<String>();
		for (String path : pathes) {
			for (String file : ToolBox.listFilesByFilter(path, filterName)) {

				if (pathNameFlag) {
					File f = new File(path + SEPERATOR + file);
					files.add(f.getAbsolutePath());
				} else {
					files.add(file);
				}
				// System.out.println("xml-Pathes: "+path+SEPERATOR+file);
			}
		}
		String[] retArr = new String[1];
		retArr = files.toArray(retArr);
		return retArr;
	}

	/**
	 * List XMLFiles in dir
	 * 
	 */

	public static String[] listXMLFiles(String dir) {
		// parameterValidation
		File f = new File(dir);
		if (!f.exists())
			throw new IllegalArgumentException("no such path: " + dir);

		String[] localList = listDir(dir, new FileFilter(".xml"));
		// System.out.println("localDir " + path + ": "
		// + Arrays.deepToString(localList));// show current dir-list
		return localList;
	}

	/**
	 * List recursive XMLFiles from start dir
	 * 
	 * @return Array of filenames
	 * @throws Exception
	 */

	public static String[] listXMLFilesRecursive(String dir,
			boolean pathNameFlag) throws Exception {
		return listFilesRecursiveByFilter(dir, pathNameFlag, ".xml");
	}

	/**
	 * create Object from Class
	 * 
	 * @param class
	 * @return Object of this class or null if not assignable
	 */
	public static Object objectFromClass(Class c) {
		Object obj = null;
		c = ClassUtils.primitiveToWrapper(c);
		if (obj == null)
			try {
				obj = randomValueByClassName(c.getName());
			} catch (Exception e) {
			}

		if (c == char.class || c == Character.class)
			obj = '0';
		if (c == Date.class) {
			obj = new Date(0);
			return obj;
		}
		if (c == byte.class || c == Byte.class)
			obj = new Byte("0");

		if (MyMath.isNumericClass(c)) {
			if (MyMath.isIntegralClass(c)) {
				Constructor intConst;
				try {
					intConst = c.getConstructor(int.class);
					obj = intConst.newInstance(0);

				} catch (Exception e) {
				}
			}
			if (MyMath.isFloatingPointClass(c)) {
				Constructor strConst;
				try {
					strConst = c.getConstructor(String.class);
					obj = strConst.newInstance("0.0");

				} catch (Exception e) {
				}
			}
		}
		try {
			Constructor strConst = c.getConstructor(String.class);
			obj = strConst.newInstance("0");
			return obj;
		} catch (Exception e) {
		}

		return obj;
	}

	/**
	 * @param
	 */
	public static void printgetProperties() {
		Properties sysProperties = System.getProperties();
		System.out.println(sysProperties.toString());
	}

	/**
	 * @param imagePath
	 * @return image
	 */
	public static String printMethodName(Object obj) {
		String methodName = "";
		thread.dumpStack();
		return methodName;
	}

	/**
	 * convenience method Create random Date String JJJJMMDD
	 * 
	 * @return
	 * @throws IOException
	 */

	static String randomDateString() throws IOException {
		Double randDays = (random.nextGaussian() - 4) * 2 * 365;
		Date date = new Date();
		date = DateUtils.addDays(date, randDays.intValue());
		String dateStr = DateFormatUtils.format(date, "yyyyMMdd");
		while (dateStr.length() < 8) {
			dateStr = "0" + dateStr;
		}
		return dateStr;
	}

	/**
	 * convenience method create Random Row to Table-Struct
	 */
	public static HashMap<String, Object> randomRow(HashMap tableStruct) {
		HashMap<String, Object> randomRow = new HashMap<String, Object>();
		// dumpHahsMap(tableStruct);
		Set columns = tableStruct.keySet();
		for (Iterator iter = columns.iterator(); iter.hasNext();) {
			String columnName = (String) iter.next();
			Object randomOb = randomValueByClassName((String) tableStruct
					.get(columnName), "*");
			randomRow.put(columnName, randomOb);
		}
		// dumpHahsMap(randomRow);
		return randomRow;
	}

	/**
	 * convenience method: create Random-Object-value
	 * 
	 * @param min
	 *            Value
	 * @param max
	 *            Value
	 * 
	 */
	public static Object randomValueByClassInRange(Class clazz, Object min,
			Object max) {
		Object randomValue = null;
		// parameter Validation
		if (min != null
				&& max != null
				&& (min.getClass() != max.getClass() || (min.getClass() == max
						.getClass() && min.getClass() != clazz)))
			throw new IllegalArgumentException(
					"min,max and class don't match! " + clazz + " min: "
							+ min.getClass() + " max: " + max.getClass());
		Object[] order = { min, max };
		Arrays.sort(order);
		if (!order[0].equals(min) && !min.equals(max))
			throw new IllegalArgumentException("min > max not allowed! min: "
					+ min.toString() + " max: " + max.toString());

		// calculations
		if (MyMath.isNumericClass(clazz))
			randomValue = MyMath.randomValueByRange(clazz, (Number) min,
					(Number) max, '+');
		else { // nonNumeric classes
			// TODO char,string,time,timestamp
			if (clazz == java.util.Date.class) {
				// min, max as DayDates, hour,min,sec doesnt matter
				Date[] randomDates = DateCalculator.dayDatesByRange((Date) min,
						(Date) max);
				randomValue = randomDates[random
						.nextInt(randomDates.length - 1)];
			}
			if (clazz == java.sql.Timestamp.class) {
				// min, max as Timestamp only with millisecond precision
				Long minL = ((Timestamp) min).getTime();
				Long maxL = ((Timestamp) max).getTime();
				Number randomL = MyMath.randomValueByRange(Long.class,
						(Number) minL, (Number) maxL, '+');
				Timestamp randomTs = new Timestamp((Long) randomL);
				randomValue = randomTs;
			}
		}

		// TODO date(day),char,string
		return randomValue;
	}

	/**
	 * convenience method: create Random-Object-value
	 * 
	 * @param className
	 *            name of class to generate random Value for
	 * 
	 */
	public static Object randomValueByClassName(String className) {
		return randomValueByClassName(className, "*");
	}

	/**
	 * convenience method: create Random-Object-value from SQL-ClassName
	 * 
	 * @param sign
	 *            +,- or *(whatever)
	 * @param min
	 *            TODO
	 * @param max
	 *            TODO
	 */
	static Object randomValueByClassName(String className, String sign) {
//		 System.out.println("randomValueByClassName(className): "+className);
		Object ranObjVaL = null;
		// java.sql.Types to java.lang mapping
//		NULL
		if (className==null)
			return  "";
		// Boolean
		if (className.equals("java.lang.Object"))
			ranObjVaL = new String("");
		// Boolean
		if (className.equals("java.lang.Boolean")
				|| className.equals("boolean"))
			ranObjVaL = new Boolean(random.nextBoolean());
		// INTEGER
		if (className.equals("java.lang.Integer") || className.equals("int"))
			ranObjVaL = new Integer(random.nextInt());

		// REAL,DOUBLE, FLOAT
		if (className.equals("java.lang.Double") || className.equals("double"))
			ranObjVaL = new Double(random.nextDouble());

		// DECIMAL,NUMERIC
		if (className.equals("java.math.BigDecimal"))
			ranObjVaL = new BigDecimal(random.nextInt());
		// "BIT"
		if (className.equals("java.lang.Boolean")
				|| className.equals("boolean"))
			ranObjVaL = new Boolean(random.nextBoolean());
		// TINYINT
		if (className.equals("java.lang.Byte") || className.equals("byte"))
			ranObjVaL = new Byte((byte) random.nextInt(Byte.MAX_VALUE));
		// SMALLINT
		if (className.equals("java.lang.Short") || className.equals("short"))
			ranObjVaL = new Short((short) random.nextInt(Short.MAX_VALUE));

		// BIGINT
		if (className.equals("java.lang.Long") || className.equals("long"))
			ranObjVaL = new Long(random.nextLong());
		// FLOAT
		if (className.equals("java.lang.Float") || className.equals("float"))
			ranObjVaL = new Float(random.nextFloat());
		// "BINARY", "VARBINARY", "LONGVARBINARY", "OTHER" Byte[]
		if (className.equals("java.lang.Byte[]") || className.equals("[B")) {
			// System.out.println("randomValueByClassName(ByteArray)");
			byte[] bytes = new byte[(random.nextInt(1)+1)*2];
			random.nextBytes(bytes);
			ranObjVaL = bytes;
		}

		// java.math.BigInteger
		if (className.equals("java.math.BigInteger"))
			ranObjVaL = BigInteger.valueOf(random.nextLong() * Long.MAX_VALUE);

		// java.util.Date
		if (className.equals("java.util.Date")) { // Dates only in Days
			// long randLong
			// =random.nextLong()%280000000*365*DateUtils.MILLIS_PER_DAY;
			// ranObjVaL = new java.util.Date(randLong);
			Date dayOneCE = new Date(0);
			int limit = 365 * 50;
			int amount = random.nextInt(limit);
			if (sign.equals("+"))
				amount = Math.abs(amount);
			if (sign.equals("-"))
				amount = Math.abs(amount) * -1;

			ranObjVaL = DateUtils.addDays(dayOneCE, random.nextInt(limit));
		}
		// VARCHAR
		if (className.equals("java.lang.String")) // NOTMUMERIC
			ranObjVaL = RandomStringUtils.randomAlphanumeric(20);
		// CHAR
		if (className.equals("java.lang.Character") || className.equals("char")) // NOTMUMERIC
			ranObjVaL = CharUtils.toCharacterObject(RandomStringUtils
					.randomAscii(1));
		// SQL DATE
		if (className.equals("java.sql.Date")) // NOTMUMERIC
			ranObjVaL = new java.sql.Date(random.nextInt(31536000 * 10));
		// TIME
		if (className.equals("java.sql.Time"))
			ranObjVaL = new Time(random.nextInt(24 * 3600));
		// TIMESTAMP
		if (className.equals("java.sql.Timestamp"))
			ranObjVaL = new Timestamp(random.nextLong());
		// sign if numeric
		if (MyMath.isNumeric(ranObjVaL))
			try {
				ranObjVaL = MyMath.translate2singed(ranObjVaL, sign
						.toCharArray()[0]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return ranObjVaL;
	}

	/**
	 * overloads readFile2String
	 * 
	 * @param path
	 * 
	 * @return content of File (without lineBreaks with UTF-8 encoding)
	 * @throws IOException
	 */
	public static String readFile2String(String filePath) throws IOException {
		return readFile2String(filePath, false, "UTF-8");
	}

	/**
	 * overloads readFile2String
	 * 
	 * @param path,
	 *            linebrakFlag
	 * 
	 * @return content of File (without lineBreaks with UTF-8 encoding)
	 * @throws IOException
	 */
	public static String readFile2String(String filePath, boolean linebreak)
			throws IOException {
		return readFile2String(filePath, linebreak, "UTF-8");
	}

	/**
	 * @param path
	 *            to file, lineBreak Flag characterEncoding (CodePage)
	 * @return content of File (with or without lineBreaks)
	 * @throws IOException
	 */
	public static String readFile2String(String filePath, boolean lineBreak,
			String encoding) throws IOException {
		String name = "";
		// boolean lineBreak = false;
		File file = new File(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath), encoding));
		StringBuffer contentOfFile = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			contentOfFile.append(line);
			if (lineBreak) {
				contentOfFile.append(LINEBREAK);
			}
		}
		String content = contentOfFile.toString();
		// System.out.println(file.length());

		// encoding

		return content;
	}

	/**
	 * @param filePath
	 *            to cleanup
	 */
	public static void removeTmpfile(String path) {
		File tmpImg = new File(path);
		tmpImg.deleteOnExit();
	}

	/**
	 * overloading
	 * 
	 * @param cmd
	 *            to call in OS-Shell
	 * @throws Exception
	 */
	public static String runOScommand(String cmd) throws Exception {
		return runOScommand(cmd, ".");
	}

	/**
	 * @param cmd
	 *            to call in OS-Shell
	 * @throws IOException
	 */
	public static String runOScommand(String cmd, String path)
			throws IOException {
		// TODO exception handling, cross platform(unixTools in lib(win) or find
		// bins in unix)
		// return string with results, param workdir

		String s = null;
		String returnStr = "";
		// OS command to run
		String[] cmdArr = { "/bin/sh", "-c", cmd };
		// set the working directory for the OS command processor from param
		// path
		File workDir = new File(path);
		BufferedReader stdInput = null;
		// Process p = Runtime.getRuntime().exec(cmdArr, null, workDir);
		try {
			Process p = Runtime.getRuntime().exec(cmdArr, null, workDir);
			int i = p.waitFor();
			if (i == 0) {
				stdInput = new BufferedReader(new InputStreamReader(p
						.getInputStream()));
				// read the output from the command
				while ((s = stdInput.readLine()) != null) {
					// System.out.println(s);
					returnStr += s + LINEBREAK;
				}
			} else {
				BufferedReader stdErr = new BufferedReader(
						new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				while ((s = stdErr.readLine()) != null) {
					// System.out.println(s);
					returnStr += s + LINEBREAK;
				}
				throw new IllegalArgumentException(s);

			}
		} catch (IOException e) {
			System.out.println(e);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		return returnStr;
	}

	/**
	 * show free VM-Memory
	 */

	public static void runtimeMemInfo() {
		Long mem = Runtime.getRuntime().freeMemory();
		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		System.out.print(df.format(mem));
	}

	/**
	 * @param debug
	 *            the debug to set
	 */
	public static void setDebugFlag(boolean debug) {
		debugFlag = debug;
	}

	/**
	 * @param strings to translate
	 * @return strings in lowerCase
	 */
	public static String[] toLowerCase(String[] strings)  {
		for (int i = 0; i < strings.length; i++) {
			strings[i]=strings[i].toLowerCase();
		}
		return strings;
	}

	/**
	 * @param strings to translate
	 * @return strings in UpperCase
	 */
	public static String[] toUpperCase(String[] strings)  {
		for (int i = 0; i < strings.length; i++) {
			strings[i]=strings[i].toUpperCase();
		}
		return strings;
	}

	/**
	 * overloads traverseDirTree to no FileFiltering
	 * 
	 * @throws Exception
	 * 
	 * @throws IOException
	 */

	public static String[] traverseDirTree(String path, boolean verbose)
			throws Exception {
		String fileFilterName = "";
		return traverseDirTree(path, verbose, fileFilterName,
				new ArrayList<String>());
	}

	/**
	 * traverse Directory-Tree depth-first TODO: cross-plattform
	 * 
	 * @throws IOException
	 */

	public static String[] traverseDirTree(String path, boolean verbose,
			String FileFilterName, ArrayList<String> pathNames)
			throws Exception {
		pathNames.add(path);
		FileFilter fileFilter = new FileFilter(FileFilterName);
		String retVal = "";
		File f = new File(path);
		// parameterValidation
		if (!f.exists())
			throw new IllegalArgumentException("no such path: " + path);

		String[] retList = listDir(path, new FileFilter("directoryFilter"));
		ArrayList<String> localList = (retList != null) ? new ArrayList<String>(
				Arrays.asList(retList))
				: new ArrayList<String>();

		if (verbose) {

			String currentDir = path + LINEBREAK;

			// File outFile = new File();
			// FileWriter out = new FileWriter(outFile, true);
			// out.write(currentDir);
			// out.close();

			writeString2File(currentDir, TMPDIR + SEPERATOR
					+ "traverseDirTreeOut.txt", "UTF-8");
			String outStr = currentDir
					+ Arrays.deepToString(listDir(path, fileFilter));
			// System.out.println(outStr);
			retVal += currentDir;
		}

		for (Iterator iter = localList.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			File file = new File(path + "/" + element);
			if (file.isDirectory()) {
				traverseDirTree((path + "/" + element).replaceAll("//", "/"),
						verbose, FileFilterName, pathNames);
			}
		}
		String[] pathes = new String[1];
		pathes = pathNames.toArray(pathes);

		removeTmpfile(TMPDIR + SEPERATOR + "traverseDirTreeOut.txt");

		return pathes;
	}

	/**
	 * write File with content to path
	 * 
	 * @param path,
	 *            outString
	 * @throws IOException
	 */
	public static void writeString2File(String content, String filePath,
			String charEncoding) throws IOException {
		boolean append = true;
		try {

			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filePath, append), charEncoding));

			out.write(content);
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/**
	 * convenience method Create random DirStruct with mutiple exampleFiles in
	 * subdirs.
	 * 
	 * @return
	 * @throws IOException
	 */

	File[] createRandomDirStruct(String startDirName, int depth,
			String[] filePathes) throws IOException {
		return createDirStruct(startDirName, depth, 1, filePathes, true);
	}
	
	/**
	 * @param
	 * @throws Exception
	 */
	public void waitTool(int millis) throws Exception {
		this.wait(millis);
	}

	
	/**
	 * change Obs-Date in FitsFile 
	 * @param fitsFilePathName
	 * @param obsDate to set
	 * @throws Exception
	 */
	public static void changeObsDateInFitsFile(String fitsFilePathName, Date obsDate) throws Exception {
		obsDate=DateUtils.truncate(obsDate, Calendar.DATE);
		//load File
		File fitsFile = new File(fitsFilePathName);
		Fits fits = new Fits(fitsFile);
		BasicHDU hdu = fits.readHDU();
		Header header = hdu.getHeader();
		//change OBS_DATE
//		FIXME 2k
		String dateStr=DateFormatUtils.format(obsDate, "dd/MM/yy");
		header.addValue("DATE-OBS", dateStr,dateStr);
		//write File
		BufferedFile dos = new BufferedFile(fitsFilePathName, "rw");
		fits.write(dos);
		fits.setChecksum();
		fitsFile.setLastModified(new Date().getTime());
	}
	
	/**
	 * translates HashMap<String,Object> to HashMap<String,String> with classNames as Values
	 * 
	 * @param objectMap
	 *            the tableName to set
	 * @throws SQLException
	 */
	public static HashMap<String,String> objectMap2ClassNameMap(HashMap<String,Object> objectMap)
			throws Exception {
		HashMap<String,String> classNameMap =new HashMap<String,String>();
		Set<String> keys = objectMap.keySet();
		for (String key : keys) {
			classNameMap.put(key, objectMap.get(key).getClass().getName());
		}
		return classNameMap;
	}
	
	/**
	 * @param keyArray
	 *            to sort
	 * @param sortOrder
	 * @return sorted keyArray
	 */
	public static String[] sortKeys(String[] keys, String[] keyOrder) {
		String[] sortKeys = keys.clone();
		sortKeys = ToolBox.toLowerCase(sortKeys);
		keyOrder = ToolBox.toLowerCase(keyOrder);

		for (int i = 0; i < keys.length; i++) {
			try { // only if keyOrder has keyOrder[i] element
				if (ArrayUtils.contains(sortKeys, keyOrder[i])) {
					sortKeys = (String[]) ArrayUtils.removeElement(sortKeys,
							keyOrder[i]);
					sortKeys = (String[]) ArrayUtils.add(sortKeys, i,
							keyOrder[i]);
				}
			} catch (RuntimeException e) {
				// e.printStackTrace();
			}
		}
		return sortKeys;
	}
	
	public static Object[] sortHashMapValuesByKeyOrder(HashMap<String,Object> map, String[] keyOrder) {

		Object[] localValues= new Object[map.size()];
		for (int i = 0; i < keyOrder.length; i++) {
			localValues[i]=map.get(keyOrder[i]);
		}
		return localValues;
	}
	
}
