/**
 * 
 */
package tools;


import static common.TEST.TMPDIR;
import static org.junit.Assert.*;
import static common.TEST.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nom.tam.fits.Fits;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.NumberRange;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import controller.MaserJ;
import tools.CrossPlatform;
import  model.*;


public class ToolBoxTest {

	String FileFilterPattern = "";

	String globalPath = "";

	int runCount;

	boolean verbose = false;

	/**
	 * Sets up the test fixture. (Called before every test case method.)
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		FileFilterPattern = "";
		globalPath = "";
	}

	/**
	 * Tears down the test fixture. (Called after every test case method.)
	 */

	@After
	public void tearDown() {
		FileFilterPattern = "";
		globalPath = "";

	}

	/**
	 * Test method for {@link tools.ToolBox#getMethodName(java.lang.Object)}.
	 */
	//@Ignore
	@Test//(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testGetMethodName() {
		//FIXME "Caused by: org.junit.ComparisonFailure: Method Name expected:<[testGetMethodName]> but was:<[invoke0]>"
		//from inside Junit thread Problem, 
		//Junit seems to start new Thread for each Test-Method

		Object inputVal = this;
		String expectedVal = this.getClass().getName()+".testGetMethodName";

		// MethodUnderTest
		String retVal = ToolBox.getCurrentMethodName();
		assertEquals("Method Name", expectedVal, retVal);

//		// check against declaredMethods
//		Method[] methods = this.getClass().getDeclaredMethods();
//		String[] names = new String[methods.length];
//		for (int i = 0; i < names.length; i++) {
//			names[i] = methods[i].getName();
//		}
//		Arrays.sort(names);
//		int declared = Arrays.binarySearch(names, retVal);
////		 System.out.println("methods: " + methods[declared].getName());
//		assertTrue("Name has to be in Declared Methods retval :" + retVal.,
//				(declared >= 0));
	}

	/**
	 * Test method for {@link tools.ToolBox#listDir(java.lang.String)}.
	 * 
	 * @throws Exception
	 *             TODO cross-Platform(linux/win, not Bash),variable Filter,root &
	 *             /... wrongPath,wrongPattern &&,||, Exceptions
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testListDir() throws Exception {

		String wrongPath = "wrongpath";
		String wrongPattern = "wrongPattern";
		String rootPath = "/";
		String testPattern = ".xml";
		String myPath = "/tmp/test/";

		String path = myPath;
		if (!globalPath.equals("")) { // quasi-Parameter
			path = globalPath;
		}

		File lsfile = new File("ls.txt");
		String filterPattern = FileFilterPattern;

		String grep = (testPattern.length() > 0) ? " |grep " + filterPattern
				+ "$" : "";

		String lspath = path.replaceAll("\\s", "\\\\ "); // quoting
		// whitespace for
		// bash
		// expected
		String OsCmd = "ls -A1 " + lspath + grep + "  >" + lsfile.getName();
		// System.out.println("testListDir() OsCmd: " + OsCmd);
		ToolBox.runOScommand(OsCmd);

		String[] expectedVal = {};

		if (lsfile.length() > 0) {
			String expectedValStr = ToolBox.readFile2String("ls.txt", true);
			expectedVal = expectedValStr.split(ToolBox.LINEBREAK);
		}
		lsfile.delete(); // cleanup
		Arrays.sort(expectedVal);

		for (int i = 0; i < expectedVal.length; i++) {
			// remove Path prefix
			expectedVal[i] = expectedVal[i].replaceFirst(path + SEPERATOR, "");
			// ignore special-Chars like GER Umlauts
			// expectedVal[i] = expectedVal[i].replaceAll("((\\\\)(\\d{3})){2}",
			// "?");
		}
		// System.out.println("expectedVal" + Arrays.deepToString(expectedVal));

		// MUT
		String[] retVal = ToolBox.listDir(path, new FileFilter(filterPattern));
		Arrays.sort(retVal);
		// System.out.println("retVal" + Arrays.deepToString(retVal));

		// the Test
		assertEquals("dirList: ", expectedVal, retVal);
		
		//cleanUp
//		 cleanUp
		lsfile.deleteOnExit();
		// provoke Exception
		retVal = ToolBox.listDir(wrongPath, new FileFilter(filterPattern));

		// fail("Under Construction ! :" + Arrays.deepToString(retVal));
	}

	/**
	 * Test method for {@link tools.ToolBox#traverseDirTree(java.lang.String)}.
	 * 
	 * @throws Exception
	 *             TODO cross-Platform(linux/win, not Bash)
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testListDirectorys() throws Exception {
		String path = TMPDIR;

		File file = new File(CrossPlatform.path(TMPDIR+"/ls.txt"));
		file.createNewFile();
		file.deleteOnExit();
		// expected

		ToolBox.runOScommand("ls -AF " + path + " |grep /$ >" + file.getName());

		List<String> expectedValList = Arrays.asList(ToolBox.readFile2String(
				CrossPlatform.path(TMPDIR+"/ls.txt"), true).split(LINEBREAK));
		// cleanup
		file.delete();
		// remove non-dirs
		ArrayList<String> tmpList = new ArrayList<String>(expectedValList);

		String[] expectedVal = new String[tmpList.size()];
		expectedVal = tmpList.toArray(expectedVal);

		Arrays.sort(expectedVal);
		// System.out.println(("ls :" + Arrays.deepToString(expectedVal)));

		// MUT
		String[] retVal = ToolBox.listDirectorys(path);
		Arrays.sort(retVal);
		// System.out.println(("retVal :" + Arrays.deepToString(retVal)));

		// the Tests
		for (int j = 0; j < retVal.length; j++) {
			assertEquals("directory: ", expectedVal[j], retVal[j] + SEPERATOR);
		}
		
//		 cleanUp
		file.deleteOnExit();
		// provoke Exception
		ToolBox.listDirectorys("wrongPath");


	}

	/**
	 * Test method for {@link tools.ToolBox#listFiles(java.lang.String)}.
	 * 
	 * @throws Exception
	 *             TODO path prefix from ls or find
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// (expected = IllegalArgumentException.class)
	public final void testListFilesRecursiveByFilter() throws Exception {
		String filterName = ".xml";
		String path = "test/";

		File lsfile = File.createTempFile("lscmd", ".txt");
		// expectedValue
		String egrep = "|egrep " + filterName + "$";
		// String OsCmd = "ls -FRA1 \"" + path + "\" "+egrep+" >" +
		// lsfile.getAbsolutePath();
		String OsCmd = "find " + path + " -name *" + filterName + " >"
				+ lsfile.getAbsolutePath();
		// System.out.println("OsCmd: " + OsCmd);
		ToolBox.runOScommand(OsCmd);
		lsfile.deleteOnExit();
		String expectedValStr = "";
		if (lsfile.length() > 0) {
			expectedValStr += ToolBox.readFile2String(TMPDIR + SEPERATOR
					+ lsfile.getName(), true);
			expectedValStr = expectedValStr.replaceAll("//", "/");
			expectedValStr = expectedValStr.replaceAll("\\*" + LINEBREAK, ""
					+ LINEBREAK);
			expectedValStr = expectedValStr.replaceAll(":" + LINEBREAK, ""
					+ LINEBREAK);

		}
		String[] expectedArr = expectedValStr.split(LINEBREAK);
		for (String string : expectedArr) {

		}
		for (int i = 0; i < expectedArr.length; i++) {
			File f = new File(expectedArr[i]);
			expectedArr[i] = f.getAbsolutePath();
		}

		Arrays.sort(expectedArr);

		// MUT
		String[] retVal = ToolBox.listFilesRecursiveByFilter(path, true,
				filterName);
		Arrays.sort(retVal);

		// the Test
		assertEquals(filterName + "Files: ", expectedArr, retVal);

		// fail("Under Construction ! :" + Arrays.deepToString(retVal));
	}

	/**
	 * Test method for {@link tools.ToolBox#listXMLFiles(java.lang.String)}.
	 * 
	 * @throws Exception
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testListXMLFiles() throws Exception {
		FileFilterPattern = ".xml";
		globalPath = "/tmp/";
		testListDir();
	}

	/**
	 * Test method for {@link tools.ToolBox#listXMLFiles(java.lang.String)}.
	 * 
	 * @throws Exception
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// (expected = IllegalArgumentException.class)
	public final void testListXMLFilesRecursive() throws Exception {
		// System.out.println("Start "+ToolBox.getMethodName(this) +": "+new
		// Date());

		String path = "test/data";
		File lsfile = File.createTempFile("lscmd", ".txt");
		// expectedValue
		String egrep = "|egrep \"xml\\*$|XML\\*$|xml$|XML$\"";
		String OsCmd = "ls  -FRA1 \"" + path + "\" " + egrep + " >"
				+ lsfile.getAbsolutePath();
		// System.out.println("OsCmd: " + OsCmd);
		ToolBox.runOScommand(OsCmd);
		lsfile.deleteOnExit();
		String expectedValStr = "";
		if (lsfile.length() > 0) {
			expectedValStr += ToolBox.readFile2String(TMPDIR + SEPERATOR
					+ lsfile.getName(), true);
			expectedValStr = expectedValStr.replaceAll("//", "/");
			expectedValStr = expectedValStr.replaceAll("\\*" + LINEBREAK, ""
					+ LINEBREAK);
			expectedValStr = expectedValStr.replaceAll(":" + LINEBREAK, ""
					+ LINEBREAK);

		}
		String[] expectedArr = expectedValStr.split(LINEBREAK);
		Arrays.sort(expectedArr);

		// MUT
		String[] retVal = ToolBox.listXMLFilesRecursive(path, false);
		Arrays.sort(retVal);

		// the Test
		assertEquals("XML-Files: ", expectedArr, retVal);
		// System.out.println("End "+ToolBox.getMethodName(this) +": "+new
		// Date());
		// fail("Under Construction ! :" + Arrays.deepToString(retVal));

	}

	/**
	 * Test method for {@link tools.ToolBox#(java.lang.String)}.
	 * 
	 * @throws Exception
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testRunOScommand() throws Exception {
		String cmd = "ls -A";
		String path = TMPDIR;
		File dir = new File(path);
		File[] files;
		String[] expectedArr;

		if (dir.exists()) {
			// expectedValue
			files = dir.listFiles();
			expectedArr = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				expectedArr[i] = files[i].getName();
			}
			Arrays.sort(expectedArr);
		} else
			expectedArr = null;

		// MUT
		String retVal = ToolBox.runOScommand(cmd, path);
		String[] retArr = retVal.split(LINEBREAK);
		Arrays.sort(retArr);

		// the Test
		assertEquals("Cmd-Result: ", expectedArr, retArr);

		// provoke Exception
		cmd = "nonsense";
		retVal = ToolBox.runOScommand(cmd, path);
	}

	/**
	 * Test method for {@link tools.ToolBox#traverseDirTree(java.lang.String)}.
	 * 
	 * @throws Exception
	 *             TODO expected,cross-Platform(linux/win, not Bash),
	 *             Zeichensatz
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testTraverseDirTree() throws Exception {
		// System.out.println(" testTraverseDirTree Start: "+new Date());
		String path = "/Beach_House_Compilation/";

		File lsfile = new File(TMPDIR + SEPERATOR + "ls.txt");

		// expectedValue
		String OsCmd = "ls  -FRA1P \"" + path + "\"|grep :$  >" + TMPDIR
				+ SEPERATOR + lsfile.getName();
		// System.out.println("OsCmd: " + OsCmd);
		ToolBox.runOScommand(OsCmd);

		ArrayList<String> expectedVal = new ArrayList<String>();
		String expectedValStr = path + LINEBREAK;
		if (lsfile.length() > 0) {
			expectedValStr += ToolBox.readFile2String(TMPDIR + SEPERATOR
					+ lsfile.getName(), true);
			expectedValStr = expectedValStr.replaceAll("//", "/");
			expectedValStr = expectedValStr.replaceAll(":" + LINEBREAK, ""
					+ LINEBREAK);
		}
		expectedVal = new ArrayList<String>(Arrays.asList(expectedValStr
				.split(LINEBREAK)));
		String[] expectedArr = expectedValStr.split(LINEBREAK);
		Arrays.sort(expectedArr);
		expectedVal = new ArrayList<String>(Arrays.asList(expectedArr));

		// MUT
		String[] retArr = ToolBox.traverseDirTree(path, true);

		// cleanup
		lsfile.delete();

		// the Test
		assertEquals("ls output:", expectedArr, retArr);

		// provoke Exception
		retArr = ToolBox.listDir("wrongPath", new FileFilter(""));
		// System.out.println("testTraverseDirTree Stopp: "+new Date());
		lsfile.deleteOnExit();
	}

	/**
	 * Test method for
	 * {@link tools.ToolBox#writeString2File(String, String, String)}.
	 * 
	 * @throws Exception
	 *             BUG : works only up to char-number 55296 ?!
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// (expected = IllegalArgumentException.class)
	public final void testWriteString2File() throws Exception {
		String path = ToolBox.TMPDIR + ToolBox.SEPERATOR + "my_testfile";
		File f = new File(path);
		String expectedVal = "";
		// System.out.println("Start: "+new Date());
		// System.out.println((int)Character.MAX_VALUE);
		for (char c = 0; c <4000; c++) { // 55296Character.MAX_VALUE
			Character cha = new Character(c);
			expectedVal += cha.toString();
		}
		// System.out.println("End: "+new Date());
		// MUT
		ToolBox.writeString2File(expectedVal, path, "UTF-8");
		String retVal = ToolBox.readFile2String(path, false, "UTF-8");

		// the Test
		assertEquals("FileContent: ", expectedVal.trim(), retVal.trim());

		// cleanUp
		f.deleteOnExit();

	}
	
	/**
	 * Test method for {@link tools.ToolBox#randomValueByClassName(String,String,Object,Object)}.
	 * 
	 * @throws Exception
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)//, expected = IllegalArgumentException.class)
	public final void testRandomValueByClassNameInRange() throws Exception {
		Object returnVal;
		Object min,max;
		Object val1,val2;
		//provoke IllegalArgumentException
		try {
			returnVal =ToolBox.randomValueByClassInRange(String.class, 3, "4");
		} catch (Exception e) {
			if (!(e.getLocalizedMessage().startsWith("min,max and class don't match! "))) 
				throw new Exception(e);
		}
		
		try {
			returnVal =ToolBox.randomValueByClassInRange(Integer.class, 4, 3);
		} catch (Exception e) {
			if (!(e.getLocalizedMessage().startsWith("min > max not allowed!"))) 
				throw new Exception(e);
		}
		//all number Classes
		Class[] numClasses=MyMath.numericClasses;
		for (Class clazz : numClasses) {
//			System.out.println("class: "+clazz);
			val1=ToolBox.randomValueByClassName(clazz.getName());
			val2=ToolBox.randomValueByClassName(clazz.getName());
			NumberRange numRange=new NumberRange((Number)val1,(Number)val2);
			min=numRange.getMinimumNumber();
			max=numRange.getMaximumNumber();
			returnVal =ToolBox.randomValueByClassInRange(clazz,min ,max);
			
//			System.out.println("returnVal: "+returnVal);
			assertTrue("Value in Range: "+returnVal+" min:" +min+" max: "+max
					,numRange.containsNumber((Number)returnVal));
		}
//		not numeric Classes
		Class[] nonNumClasses=MaserJ.timeClasses;
		for (Class clazz : nonNumClasses) {
//			System.out.println("class: "+clazz);
			val1=ToolBox.randomValueByClassName(clazz.getName());
			val2=ToolBox.randomValueByClassName(clazz.getName());
			//DATE
			if (clazz==java.util.Date.class) {
				Date date1 = (Date)val1;
				Date date2 = (Date)val2;
				NumberRange numRange = new NumberRange((Number) date1.getTime(),
												(Number) date2.getTime());
				min = new Date(numRange.getMinimumLong());
				max = new Date(numRange.getMaximumLong());
//				System.out.println("min: " + min);
//				System.out.println("max: " + max);
				returnVal = ToolBox.randomValueByClassInRange(clazz, min, max);
					Date dateVal = (Date) returnVal;
//					System.out.println("returnVal: " + dateVal);
					assertTrue("Value in Range: " + returnVal + " min:" + min
							+ " max: " + max, numRange
							.containsNumber((Number) dateVal.getTime()));				
				}
//			TIMESTAMP
			if (clazz==java.sql.Timestamp.class) {
				
				Timestamp ts1 = (Timestamp)val1;
				Timestamp ts2 = (Timestamp)val2;
				
				NumberRange numRange = new NumberRange((Number) ts1.getTime(),
												(Number) ts2.getTime());
				min = new Timestamp(numRange.getMinimumLong());
				max = new Timestamp(numRange.getMaximumLong());
//				System.out.println("minTimeStamp: " + min);
//				System.out.println("maxTimeStamp: " + max);
				returnVal = ToolBox.randomValueByClassInRange(clazz, min, max);
				Timestamp ts = (Timestamp) returnVal;
//					System.out.println("returnValTimeStamp: " + ts);
					assertTrue("Value in Range: " + returnVal + " min:" + min
							+ " max: " + max, numRange
							.containsNumber((Number) ts.getTime()));			
			}
			
		}
	}
	
	/**
	 * Test method for {@link tools.ToolBox#isComparable(class)}.
	 * 
	 * @throws Exception
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testIsComparable() throws Exception {
		//MUT
		assertFalse("testIsComparable",ToolBox.isComparable(Observation.class));
		assertTrue("testIsComparable",ToolBox.isComparable(Date.class));
		assertTrue("testIsComparable",ToolBox.isComparable(Timestamp.class));
		assertFalse("testIsComparable",ToolBox.isComparable(Time.class));
	}
	
	/**
	 * Test method for {@link tools.ToolBox#compareHashMaps(HashMap<String, Object>,HashMap<String, Object>)}.
	 * 
	 * @throws Exception
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testCompareHashMaps() throws Exception {
		//generate Random Hashmap, size
		Object[] MaserJGroundClasses=	
			ArrayUtils.addAll(MaserJ.timeClasses, MaserJ.groundClasses);
		//+all ground classes in MaserJ, sql
		Class[] classes = (Class[]) MaserJGroundClasses;
		int	sizelimit =classes.length;
		HashMap<String, Object> map1=new HashMap<String, Object>(sizelimit);
		HashMap<String, Object> map2=new HashMap<String, Object>(sizelimit);
		//random keyStrings
		String[] keys=new String[sizelimit];
		for (int i = 0; i < keys.length; i++) {
			keys[i]=RandomStringUtils.randomAlphabetic(sizelimit);
			map1.put(keys[i], null);
//			System.out.println("key: "+keys[i]);
		}
		//Random objectValues
		
		for (int i = 0; i < keys.length; i++) {
			int classIndex=i;
			Object value = ToolBox.randomValueByClassName(classes[classIndex].getName());
//			System.out.println("classes[classIndex].getName(): "+classes[classIndex].getName());
			map1.put(keys[i], value);
		}
		map2=(HashMap<String, Object>) map1.clone();
		
		//MUT & tests
		assertEquals("compare",true,ToolBox.compareHashMaps(map1, map2, true));
		//test difference
		map2.put(keys[0], "");
		assertEquals("compare",false,ToolBox.compareHashMaps(map1, map2, true));
		map2.put(keys[1], ToolBox.randomValueByClassName(map1.get(keys[1]).getClass().getName()));
		assertEquals("compare",false,ToolBox.compareHashMaps(map1, map2, true));
	}
	
	/**
	 * Test method for {@link tools.ToolBox#objectFromClass(java.lang.Object)}.
	 * 
	 * 
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)//, expected = IllegalArgumentException.class)
	public final void testObjectFromClass() throws Exception {
		
		//not assignable Class
		Class c=Observation.class;
		//MUT
		Object obj=ToolBox.objectFromClass(c);
		assertEquals("Objects class: ",null,obj);
		
//		TODO all MaserJ(sql?)classes
		Class[] testClasses=(Class[])ArrayUtils.addAll(MaserJ.groundClasses, MaserJ.timeClasses);
		for (int i = 0; i < testClasses.length; i++) {
			c = testClasses[i];
//			System.out.println("testObjectFromClass(class)" + c);
			//MUT
			obj = ToolBox.objectFromClass(c);
//			System.out.println("testObjectFromClass() return: " + obj.getClass());
			assertEquals("Objects class: ", ClassUtils.primitiveToWrapper(c), obj.getClass());
			if (obj.getClass().equals(Boolean.class)) {
				assertEquals("Objects value: "+c, obj, false);
			}
			if (obj.getClass().equals(Date.class)) {
				assertEquals("Objects value: "+c, obj, new Date(0));
			}
			if (MyMath.isNumeric(obj)) {
				if (MyMath.isIntegral(obj))
					assertEquals("Objects value: "+c, ((Number)obj).toString(), "0");
				if (MyMath.isFloatingPoint(obj))
					assertEquals("Objects value: "+c, ((Number)obj).toString().substring(0, 1), "0");
			}
		}		
	}
	
	/**
	 * Test method for {@link tools.ToolBox#changeObsDateInFitsFile(String fitsFilePathName, Date obsDate)}.
	 * 
	 * @throws Exception
	 * 
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testChangeObsDateInFitsFile() throws Exception {
		Date expectedDate = DateUtils.addDays(new Date(0), random.nextInt(3650));
		System.out.println(ToolBox.getCurrentMethodName()+"expectedDate: "+expectedDate);
		String fitsFilePathName="";
		//copy fitsFile to workFile
		String fitsPathSrc = FilenameUtils
		.separatorsToSystem("test/examples/effelsbergSample.fits");
		String fitsPathDest = FilenameUtils
		.separatorsToSystem("/tmp/testChangeObsDateInFitsFile/effelsbergSample.fits");
		File srcFile = new File(fitsPathSrc);
		File destFile = new File(fitsPathDest);
		FileUtils.copyFile(srcFile, destFile, false);
		//MUT
		ToolBox.changeObsDateInFitsFile(fitsPathDest, expectedDate);
		//readDate from File
		File fitsFile = new File(fitsPathDest);
		Fits fits = new Fits(fitsFile);
		Date retrurnDate= fits.getHDU(0).getObservationDate();
		System.out.println(ToolBox.getCurrentMethodName()+"retrurnDate: "+retrurnDate);
		assertEquals("obsDate: ",expectedDate,retrurnDate);
		
	}
	
	
}
