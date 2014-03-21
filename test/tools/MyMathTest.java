/**
 * 
 */
package tools;

import static common.TEST.DEFAULT_TIMEOUT;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberRange;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.math.distribution.DistributionFactory;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.util.MathUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MyMathTest {
	Class[] classes = { boolean.class, char.class, byte.class, short.class,
			int.class, long.class, float.class, double.class, String.class };

	Object[] minValues = { false, Character.MIN_VALUE, Byte.MIN_VALUE,
			Short.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE,
			Float.MIN_VALUE, Double.MIN_VALUE, "" };

	Random random;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		random = new Random((long) (Math.random() * Long.MAX_VALUE));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link tools.MyMath#normal2DDist(double, double, double)}.
	 * TODO : inverse
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// (expected = IllegalArgumentException.class)
	public final void testNormal2DDist() throws Exception {
		// System.out.println(ToolBox.getMethodName(this));
		int testRuns = 100; // number of test to run
		// PointsOfIntrest
		double[][] poi = { { 0, 0 }, { -6, 6 } };
		// testParams

		double cc = 0;
		double meanX = 0;
		double sDeviationX = 1;
		double meanY = 0;
		double sDeviationY = 1;
		double expectedVal = 0;
		double retVal = 0;

		for (int i = 0; i < poi.length + testRuns; i++) { // POIs first
			double x = (i < poi.length) ? poi[i][0] : Math.random() * 5;
			double y = (i < poi.length) ? poi[i][1] : Math.random() * 5;
			// MUT
			retVal = MyMath.normal2DDist(x, y, meanX, meanY, sDeviationX,
					sDeviationY, cc);
			// expectedValue standartNormalDistribution 2D
			expectedVal = 1 / (2 * Math.PI)
					* Math.exp((-0.5 * ((x * x) + (y * y))));
			// the Test
			assertTrue("Delta(expectedVal -retVal): ",
					(expectedVal - retVal) < 1E-10);
		}

	}

	/**
	 * Test method for {@link tools.MyMath#gauss2DTable(int)}.
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testGauss2DTable() {
		// System.out.println("Method:" + ToolBox.getMethodName(this));
		int testRuns = 100; // number of test to run

		// PointsOfIntrest
		int[] poi = { 0, 5 };

		for (int k = 0; k < poi.length + testRuns; k++) {
			// System.out.println("processing Testrun: " + k);
			int size = Math.max(poi.length, random.nextInt(250));

			// POIs
			int j = (k < poi.length) ? poi[k] : random.nextInt(size);

			double y = (5.0 / size) * j;
			double[] expectedVals = new double[size];
			for (int i = 0; i < expectedVals.length; i++) {
				// System.out.print(".");
				double x = (5.0 / size) * i;

				// expected Vals
				expectedVals[i] = 1 / (2 * Math.PI)
						* Math.exp((-0.5 * ((x * x) + (y * y))));
			}

			// MUT
			double[][] retVals = MyMath.gauss2DTable(size);
			// System.out.println("\n" + "testing row: " + j);

			// the Test
			assertTrue("Gauss z-Values to fix y :" + y, Arrays.equals(
					expectedVals, retVals[j]));
		}
	}

	/**
	 * Test method for {@link tools.MyMath#normalDist(double, double, double)}.
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// (expected = IllegalArgumentException.class) ??
	public final void testNormalDist() throws Exception {
		int testRuns = 1000; // number of test to run
		// PointsOfIntrest
		double[] poi = { 0.0, 6.0, -6 };
		// testParams
		double mean = 0;
		double sDeviation = 1;
		DistributionFactory factory = DistributionFactory.newInstance();

		for (int i = 0; i < poi.length + testRuns; i++) {

			double x = (i < poi.length) ? poi[i] : Math.random() * 5; // POIs
			// first

			// MUT
			double retVal = MyMath.normalDist(x, mean, sDeviation);

			// expectedVal by
			// org.apache.commons.math.distribution.NormalDistribution
			NormalDistribution nDist = factory.createNormalDistribution(mean,
					sDeviation);
			double expectedVal = MyMath.approxDerivation(x, nDist,
					"cumulativeProbability");

			// the Test
			assertTrue("Delta less then 1E-10: ", (Math.abs(expectedVal
					- retVal) < 1E-10));
		}
	}

	/**
	 * Test method for {@link tools.MyMath#arrayCast2Objects(java.lang.Object)}.
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testArrayCast2Objects() {
		int testRuns = 100; // number of test to run
		double[] poi = {}; // PointsOfIntrest

		int length = random.nextInt(100); // random ArrayLength

		for (int i = 0; i < poi.length + testRuns; i++) {
			int rand = random.nextInt(classes.length);// pick class at random
			Object expectedArr = genArray(classes[rand], length);

			// MUT
			Object[] returnArray = MyMath.arrayCast2Objects(expectedArr);

			// the Tests
			for (int j = 0; j < returnArray.length; j++) {
				assertEquals("ArrayElement :" + j, Array.get(expectedArr, j),
						returnArray[j]);
			}
		}
		// provoke IllegalArgumentException
		String errorObj = "Error";
		MyMath.arrayCast2Objects((Object) errorObj);

	}

	/**
	 * Test method for {@link tools.MyMath#arrayMax(java.lang.Object)}.
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// , expected = IllegalArgumentException.class)
	public final void testArrayMax() {
		int testRuns = 100; // number of test to run
		double[] poi = {}; // PointsOfIntrest

		int length = random.nextInt(100) + 2; // random ArrayLength min=2

		for (int i = 0; i < poi.length + testRuns; i++) {
			int rand = random.nextInt(classes.length);// pick class at random
			Object expectedArr = genArray(classes[rand], length);
			Object val = 0;
			Object expectedVal = 0;
			expectedVal = minValues[rand];

			for (int k = 0; k < Array.getLength(expectedArr); k++) {

				switch (rand) {
				case 0:
					boolean boolVal = (Boolean) Array.get(expectedArr, k);
					if (boolVal)
						expectedVal = boolVal; // max
					break;
				case 1:
					char c = (Character) Array.get(expectedArr, k);
					if (c > (Character) expectedVal)
						expectedVal = c; // max
					break;
				case 2:
					byte bytes = (Byte) Array.get(expectedArr, k);
					if (bytes > (Byte) expectedVal)
						expectedVal = bytes; // max
					break;
				case 3:
					short s = (Short) Array.get(expectedArr, k);
					if (s > (Short) expectedVal)
						expectedVal = s; // max
					break;
				case 4:
					int j = (Integer) Array.get(expectedArr, k);
					if (j > (Integer) expectedVal)
						expectedVal = j; // max
					break;
				case 5:
					long l = (Long) Array.get(expectedArr, k);
					if (l > (Long) expectedVal)
						expectedVal = l; // max
					break;
				case 6:
					float f = (Float) Array.get(expectedArr, k);
					if (f > (Float) expectedVal)
						expectedVal = f; // max
					break;
				case 7:
					double d = (Double) Array.get(expectedArr, k);
					if (d > (Double) expectedVal)
						expectedVal = d; // max
					break;
				case 8:
					String str = (String) Array.get(expectedArr, k);
					if (str.compareTo((String) expectedVal) > 0)
						expectedVal = str; // max
					break;
				}
			}
			// MUT
			Object[] objArray = MyMath.arrayCast2Objects(expectedArr);
			Object returnVal = MyMath.arrayMax(objArray);

			// the Tests

			assertEquals("MaxElement :", expectedVal, returnVal);

		}
		// provoke IllegalArgumentException
		// String errorObj = "Error";
		// MyMath.arrayCast2Objects((Object) errorObj);

	}

	/**
	 * Test method for
	 * {@link tools.MyMath#array2DCast2Objects(java.lang.Object)}.
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// , expected = IllegalArgumentException.class)
	public final void testArray2DMax() {
		int testRuns = 1000; // number of test to run
		double[] poi = {}; // PointsOfIntrest

		int width = random.nextInt(100); // random ArrayLength
		int height = random.nextInt(100); // random ArrayLength
		for (int i = 0; i < poi.length + testRuns; i++) {
			int rand = random.nextInt(classes.length);// pick class at random
			// generate 2Dim Array
			Object[][] expectedArr = new Object[height][width];
			for (int j = 0; j < expectedArr.length; j++) {
				expectedArr[j] = MyMath.arrayCast2Objects(genArray(
						classes[rand], width));
			}
			// expectedValue
			Object expectedValue = minValues[rand];
			// max
			for (int j = 0; j < expectedArr.length; j++) {
				Arrays.sort(expectedArr[j]);
				Object rowMax = expectedArr[j][expectedArr[j].length - 1];
				Object[] order = { rowMax, expectedValue };
				Arrays.sort(order);
				expectedValue = order[1];
			}

			// MUT
			Object returnValue = MyMath.array2DMax(expectedArr);

			// the Tests
			assertEquals("2Dim ArrayMax: ", expectedValue, returnValue);
		}
		// fail("TODO IllegalArgumentException");

		// // provoke IllegalArgumentException
		// String errorObj = "Error";
		// MyMath.arrayCast2Objects((Object) errorObj);
	}

	/**
	 * Test method for
	 * {@link tools.MyMath#array2DCast2Objects(java.lang.Object)}.
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testArray2DCast2Objects() {
		int testRuns = 100; // number of test to run
		double[] poi = {}; // PointsOfIntrest

		int width = random.nextInt(100); // random ArrayLength
		int height = random.nextInt(100); // random ArrayLength
		for (int i = 0; i < poi.length + testRuns; i++) {
			int rand = random.nextInt(classes.length);// pick class at random
			// generate 2Dim Array
			Object[][] expectedArr = new Object[height][width];
			for (int j = 0; j < expectedArr.length; j++) {
				expectedArr[j] = MyMath.arrayCast2Objects(genArray(
						classes[rand], width));
			}
			// MUT
			Object[][] returnArray = MyMath
					.array2DCast2Objects((Object) expectedArr);

			// the Tests
			assertEquals("2Dim Array: ", expectedArr, returnArray);
		}
		// fail("TODO IllegalArgumentException");

		// provoke IllegalArgumentException
		String errorObj = "Error";
		MyMath.arrayCast2Objects((Object) errorObj);
	}

	/**
	 * helper Method to generate Array of Class with random Content
	 * 
	 * @param c
	 *            element Class of Array
	 * @param length
	 *            of Array
	 * @return Object with Array of Elements from Class c
	 */
	Object genArray(Class c, int length) {
		Object expectedArr = Array.newInstance(c, length);
		int cNumber = ArrayUtils.indexOf(classes, c);
		for (int k = 0; k < Array.getLength(expectedArr); k++) {

			switch (cNumber) {
			case 0:
				boolean boolVal = random.nextBoolean();
				Array.set(expectedArr, k, boolVal);
				break;
			case 1:
				String str = RandomStringUtils.randomAscii(1);
				char chr = str.charAt(0); // charAt(index)Character.;
				// System.out.println("random char: " + chr);
				Array.set(expectedArr, k, chr);

				break;
			case 2:
				byte[] bytes = new byte[1];
				random.nextBytes(bytes);
				// System.out.println("random bytes: " + bytes);
				Array.set(expectedArr, k, bytes[0]);
				break;
			case 3:
				short s = (short) random.nextInt(Short.MAX_VALUE);
				// System.out.println("random short: " + s);
				Array.set(expectedArr, k, s);
				break;
			case 4:
				int j = random.nextInt();
				// System.out.println("random int: " + j);
				Array.set(expectedArr, k, j);
				break;
			case 5:
				long l = random.nextLong();
				// System.out.println("random long: " + l);
				Array.set(expectedArr, k, l);
				break;
			case 6:
				float f = random.nextFloat();
				// System.out.println("random float: " + f);
				Array.set(expectedArr, k, f);
				break;
			case 7:
				double d = random.nextDouble();
				// System.out.println("random double: " + d);
				Array.set(expectedArr, k, d);
				break;
			case 8:
				String string = RandomStringUtils.random(100);
				// System.out.println("random String: " + str);
				Array.set(expectedArr, k, string);
				break;
			}
		}
		return expectedArr;
	}

	/**
	 * Test method for {@link tools.MyMath#translate2singed(Object,char)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT, expected = IllegalArgumentException.class)
	public final void testTranslate2singed() throws Exception {
		Object obj = null;
		Class[] numClasses = (Class[]) ArrayUtils.addAll(MyMath.numericClasses,
				MyMath.numericTypes);
		int testRuns = 100;
		// PointsOfIntrest
		Number[] poi = (Number[]) ArrayUtils.addAll(MyMath.NumericMaxValues,
				MyMath.NumericMinValues);

		for (int i = 0; i < poi.length + testRuns; i++) {
			for (Class c : numClasses) {
				// System.out.println("class: " + c);
				// obj = ToolBox.randomValueByClassName(c.getName());
				obj = (i < poi.length) ? poi[i] : ToolBox
						.randomValueByClassName(c.getName()); // POIs
				// translate to positive
				String negativeSign = "-";
				String expectedValue = obj.toString();
				if (obj.toString().startsWith("-")) {
					expectedValue = expectedValue
							.replaceFirst(negativeSign, "");
				}
				String returnValue = MyMath.translate2singed(obj, '+')
						.toString();
				assertEquals("signed Number: " + obj.getClass().getName(),
						expectedValue, returnValue);
				// translate to negative
				String positveSign = "+";
				expectedValue = obj.toString();
				// System.out.println("expectedValue: "+expectedValue);
				if (!obj.toString().startsWith("-")) {
					expectedValue = "-" + expectedValue;
				}
				if (obj.toString().equals("0"))
					expectedValue = "0";
				returnValue = MyMath.translate2singed(obj, '-').toString();
				assertEquals("signed Number: " + obj.getClass().getName(),
						expectedValue, returnValue);
			}
		}

	}

	/**
	 * Test method for {@link tools.MyMath#isNumeric(Object)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// , expected = IllegalArgumentException.class)
	public final void testIsNumeric() throws Exception {
		Object obj = null;
		Class[] numClasses = (Class[]) ArrayUtils.addAll(MyMath.numericClasses,
				MyMath.numericTypes);
		for (Class c : numClasses) {
			// System.out.println("class: " + c);
			obj = ToolBox.randomValueByClassName(c.getName());
			// System.out.println("obj Value: " + obj);
		}
		assertTrue("isNumeric: " + obj.getClass().getName(), MyMath
				.isNumeric(obj));
	}

	/**
	 * Test method for {@link tools.MyMath#isNumeric(Object)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)//, expected = IllegalArgumentException.class)
	public final void TestRandomValueByClass() throws Exception {
		Object returnVal = null;
		Class[] numClasses = MyMath.numericClasses;
		int testRuns = 1000;
		for (int i = 0; i < testRuns; i++) {
			for (Class numberClass : numClasses) {
				// System.out.println("class: " + numberClass);
				returnVal = MyMath.randomValueByClass(numberClass, '*');
				// System.out.println("obj Value: " + returnVal);

				// the Tests
				assertTrue("isNumeric: " + returnVal.getClass().getName(),
						MyMath.isNumeric(returnVal));
				assertEquals("class: ", numberClass, returnVal.getClass());
				assertTrue("Value not null: ", returnVal != null);
				if (numberClass == Byte.class) {
					// System.out.println("ByteValue"+returnVal);
					assertTrue("Value in ByteRange: ", ((Integer
							.valueOf(returnVal.toString()) >= -128) && (Integer
							.valueOf(returnVal.toString()) < 128)));
				}
				// signs
				Number positivN = MyMath.randomValueByClass(numberClass, '+');
				positivN = 128;
				assertFalse("Value is positiv: " + positivN, positivN
						.toString().startsWith("-"));
				Number negativN = MyMath.randomValueByClass(numberClass, '-');
				if (!negativN.toString().equals("0")) {
					assertTrue("Value is negativ: " + negativN, negativN
							.toString().startsWith("-"));
				}

			}
		}
	}

	/**
	 * Test method for {@link tools.ToolBox#randomValueByClassName (String
	 * className, String sign,Object min, Object max)
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testRandomValueByRange() throws Exception {
//		{ Byte.class, Short.class,Integer.class, Long.class, Float.class, Double.class };
		Class expectedClass=Float.class;
		Class[] classes=MyMath.numericClasses;
		int testruns=1000;
		for (int i = 0; i < testruns; i++) {
			for (Class clazz : classes) {
				expectedClass = clazz;
				//MUT
				Number returnValue = MyMath.randomValueByRange(expectedClass,
						0, 0, '*');
				//		test value
				Number num1 = random.nextLong();
				num1=(Double)num1.doubleValue();
				Number num2 = random.nextLong();
				num2=(Double)num2.doubleValue();
				if (expectedClass == Byte.class) {
					num1 = num1.byteValue();
					num2 = num2.byteValue();
				}

				if (expectedClass == Short.class) {
					num1 = num1.shortValue();
					num2 = num2.shortValue();
				}

				if (expectedClass == Integer.class) {
					num1 = num1.intValue();
					num2 = num2.intValue();
				}
				if (expectedClass == Long.class) {
					num1 = num1.longValue();
					num2 = num2.longValue();
				}
				if (expectedClass == Float.class) {
					num1 = num1.floatValue();
					num2 = num2.floatValue();
				}
				NumberRange range = new NumberRange(num1, num2);
				returnValue = MyMath.randomValueByRange(expectedClass, range
						.getMinimumNumber(), range.getMaximumNumber(), '*');
//				System.out.println("testRandomValueByRange() returnValueClass: "+returnValue
//						.getClass());
//				System.out.println("testRandomValueByRange() returnValue: "+returnValue);
				assertTrue("returnValue value in range:", range
						.containsNumber(returnValue));
				//test Class of Object
				assertEquals("returnValue Class:", expectedClass, returnValue
						.getClass());
			}
		}		
	}

	/**
	 * Test method for {@link tools.MyMath#isNumericlimit(Class, boolean)}.
	 * 
	 * @throws Exception
	 */
	// @Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	// , expected = IllegalArgumentException.class)
	public final void testLimit() throws Exception {
		Object obj = null;
		Class[] numClasses = (Class[]) ArrayUtils.addAll(MyMath.numericClasses,
				MyMath.numericTypes);
		for (Class c : numClasses) {
			// System.out.println("class: " + c);
			// Byte
			obj = MyMath.limit(c, true);
			if (c == Byte.class || c == byte.class) {
				assertEquals("MIN_VALUE: ", Byte.parseByte(obj.toString()),
						Byte.MIN_VALUE);
			}
			obj = MyMath.limit(c, false);
			if (c == Byte.class || c == byte.class) {
				assertEquals("MAX_VALUE: ", Byte.parseByte(obj.toString()),
						Byte.MAX_VALUE);
			}
			// Double
			obj = MyMath.limit(c, true);
			if (c == Double.class || c == double.class) {
				assertEquals("MIN_VALUE: ", Double.parseDouble(obj.toString()),
						Double.MIN_VALUE);
			}
			obj = MyMath.limit(c, false);
			if (c == Double.class || c == double.class) {
				assertEquals("MAX_VALUE: ", Double.parseDouble(obj.toString()),
						Double.MAX_VALUE);
			}

			// Float
			obj = MyMath.limit(c, true);
			if (c == Float.class || c == float.class) {
				assertEquals("MIN_VALUE: ", Float.parseFloat(obj.toString()),
						Float.MIN_VALUE);
			}
			obj = MyMath.limit(c, false);
			if (c == Float.class || c == float.class) {
				assertEquals("MAX_VALUE: ", Float.parseFloat(obj.toString()),
						Float.MAX_VALUE);
			}

			// Integer
			obj = MyMath.limit(c, true);
			if (c == Integer.class || c == int.class) {
				assertEquals("MIN_VALUE: ", Integer.parseInt(obj.toString()),
						Integer.MIN_VALUE);
			}
			obj = MyMath.limit(c, false);
			if (c == Integer.class || c == int.class) {
				assertEquals("MAX_VALUE: ", Integer.parseInt(obj.toString()),
						Integer.MAX_VALUE);
			}

			// Long
			obj = MyMath.limit(c, true);
			if (c == Long.class || c == long.class) {
				assertEquals("MIN_VALUE: ", Long.parseLong(obj.toString()),
						Long.MIN_VALUE);
			}
			obj = MyMath.limit(c, false);
			if (c == Long.class || c == long.class) {
				assertEquals("MAX_VALUE: ", Long.parseLong(obj.toString()),
						Long.MAX_VALUE);
			}
			// Short
			obj = MyMath.limit(c, true);
			if (c == Short.class || c == short.class) {
				assertEquals("MIN_VALUE: ", Short.parseShort(obj.toString()),
						Short.MIN_VALUE);
			}
			obj = MyMath.limit(c, false);
			if (c == Short.class || c == short.class) {
				assertEquals("MAX_VALUE: ", Short.parseShort(obj.toString()),
						Short.MAX_VALUE);
			}
			// System.out.println("obj Value: " + obj);
		}
	}
}
