/**
 * Some useful additions to the built-in functions Math
 */
package tools;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.math.NumberRange;
import org.apache.commons.math.MathException;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.DefaultTransformer;

/**
 * 
 */
public class MyMath {

	static final Class[] numericTypes = { byte.class, short.class, int.class,
			long.class, float.class, double.class };

	// Byte, Double, Float, Integer, Long, Short
	static final Class[] numericClasses = { Byte.class, Short.class,
			Integer.class, Long.class, Float.class, Double.class };

	// java.math.BigInteger.class,java.math.BigDecimal.class

	static final Class[] integralClasses = { Byte.class, Short.class,
			Integer.class, Long.class };// , java.math.BigInteger.class

	static final Class[] floatingPointClasses = { Float.class, Double.class};//,java.math.BigDecimal.class 

	static final Number[] NumericMinValues = { Byte.MIN_VALUE, Short.MIN_VALUE,
			Integer.MIN_VALUE, Long.MIN_VALUE, Float.MIN_VALUE,
			Double.MIN_VALUE };

	static final Number[] NumericMaxValues = { Byte.MAX_VALUE, Short.MAX_VALUE,
			Integer.MAX_VALUE, Long.MAX_VALUE, Float.MAX_VALUE,
			Double.MAX_VALUE };

	static Random random = new Random();

	/**
	 * calculate the first quadrant (x:0 -5 ,y:0 - 5) of a 2dim Table from
	 * standart Normal Distrubution Values
	 * 
	 * @param (color,size)
	 *            TODO UnitTest, scaling by mean... Error :NullPointerExepction
	 */
	public static double[][] gauss2DTable(int size) {
		int xDim = size;
		int yDim = size;
		double meanX = 0;
		double meanY = 0;
		double sDeviationX = 1;
		double sDeviationY = 1;
		double cc = 0;
		Double x = 0.0;
		Double y = 0.0;
		// int precision = Math.round((float) Math.log10(size));
		double[][] gauss2DVals = new double[xDim][yDim];
		//
		for (int i = 0; i < gauss2DVals.length; i++) {
			x = i * (5.0 / xDim);
			for (int j = 0; j < gauss2DVals.length; j++) {
				y = j * (5.0 / yDim);
				gauss2DVals[i][j] = normal2DDist(x, y, meanX, meanY,
						sDeviationX, sDeviationY, cc);
			}
		}
		return gauss2DVals;
	}

	/**
	 * Returns the Z , to 2 Dim Gaussian ("normally") distributed double value
	 * with mean and standard deviation and Coefficient of correlation
	 */
	public static double normal2DDist(double x, double y, double meanX,
			double meanY, double sDeviationX, double sDeviationY, double cc) {

		double term1 = (0.5 / (Math.PI * sDeviationX * sDeviationY * Math
				.sqrt(1 - Math.pow(cc, 2))));

		double term2 = -1 / (2 * (1 - Math.pow(cc, 2)));
		double term3 = (x - meanX) / sDeviationX;
		double term4 = (y - meanY) / sDeviationY;

		return term1
				* Math.exp(term2
						* (Math.pow(term3, 2) - (2 * cc * term3 * term4) + Math
								.pow(term4, 2)));
	}

	/**
	 * Returns the Y , to X Gaussian ("normally") distributed double value with
	 * mean and standard deviation
	 */
	public static double normalDist(double x, double mean, double sDeviation) {
		double y = (1 / (Math.sqrt(2 * Math.PI) * sDeviation))
				* Math.exp(-0.5 * (Math.pow(((x - mean) / sDeviation), 2)));
		return y;
	}

	/**
	 * Returns the value of the Derivation of the function by aproximation
	 * through the difference quotient
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static double approxDerivation(double x, Object className,
			String functionName) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Class c = className.getClass();
		Method[] methods = c.getDeclaredMethods();
		Method function = null;
		for (Method method : methods) {
			if (method.getName().equals(functionName)
					&& method.getParameterTypes().length == 1)
				function = method;
		}

		double deltaX = 1E-5; // seems to be a good Value for double
		c = function.getReturnType();
		// double y1 = (Double) function.invoke(className, x);
		double y2 = (Double) function.invoke(className, x + deltaX);// right
		// difference
		double y3 = (Double) function.invoke(className, x - deltaX);// left
		// difference
		// System.out.println(" y1:"+y1+" y2:"+y2);
		double deltaY = y2 - y3;

		// invoke
		// System.out.println("deltay: " + deltaY + " /deltaX: " + deltaX);
		return deltaY / (2 * deltaX);
	}

	/**
	 * cast inputObject to ojectArray only 1Dim Array(for now)
	 * 
	 * @param Array
	 * @return Array of Objects
	 * @throws IllegalArgumentException
	 *             if input ist not an Array
	 */
	public static Object[] arrayCast2Objects(Object obj)
			throws IllegalArgumentException {
		// TODO isArray, getType,Dim-Test refactor to MyArray?
		Class objClass = obj.getClass();
		if (!objClass.isArray()) {
			String errorMsg = "Only Arrays allowed !";
			// System.out.println(ToolBox.getCallerMethod(obj)+": "+errorMsg);
			throw new IllegalArgumentException(errorMsg);
		}

		Object[] objArray = new Object[Array.getLength(obj)];

		for (int i = 0; i < objArray.length; i++) {
			objArray[i] = Array.get(obj, i);
			// System.out.println(objArray[i]);
		}
		return objArray;
	}

	/**
	 * cast inputObject to ojectArray only 2Dim Array
	 * 
	 * @param Object
	 *            (the 2DArray as Object )
	 * @return Array[][] of Objects
	 * @throws IllegalArgumentException
	 *             if input ist not an 2D Array
	 */
	public static Object[][] array2DCast2Objects(Object obj)
			throws IllegalArgumentException {
		// TODO isArray, getType,Dim-Test refactor to MyArray?
		Class objClass = obj.getClass();

		if (!objClass.isArray()) {
			String errorMsg = "Only Arrays allowed !";
			// System.out.println(ToolBox.getCallerMethod(obj)+": "+errorMsg);
			throw new IllegalArgumentException(errorMsg);
		} else {
			Class row1Class = Array.get(obj, 0).getClass();
			if (!row1Class.isArray()) {
				String errorMsg = "Only 2 dim Arrays allowed !";
				// System.out.println(ToolBox.getCallerMethod(obj)+":
				// "+errorMsg);
				throw new IllegalArgumentException(errorMsg);

			}
		}

		Object[] rowArray = new Object[Array.getLength(obj)];
		int maxRowlength = 0;
		for (int i = 0; i < rowArray.length; i++) {
			rowArray[i] = Array.get(obj, i);
			int rowLength = Array.getLength(rowArray[i]);
			maxRowlength = (rowLength > maxRowlength) ? rowLength
					: maxRowlength;
			// System.out.println(maxRowlength);
		}

		Object[][] twoDimArray = new Object[rowArray.length][maxRowlength];
		for (int j = 0; j < twoDimArray.length; j++) {
			twoDimArray[j] = arrayCast2Objects(rowArray[j]);
		}
		return twoDimArray;
	}

	/**
	 * @param objArray
	 * @return the maximum Element of the ArrayContent
	 */
	public static Object arrayMax(Object[] objArray) {
		Arrays.sort(objArray);
		return objArray[objArray.length - 1];
	}

	/**
	 * @param objArray
	 * @return the maximum Element of the ArrayContent
	 */
	public static Object array2DMax(Object[][] objArray) {
		ArrayList maxs = new ArrayList();
		for (int i = 0; i < objArray.length; i++) {
			maxs.add(arrayMax(objArray[i]));
		}
		Object max = arrayMax(maxs.toArray());
		return max;
	}

	/**
	 * convenience method translate Numeric Value to signed Value TODO big ones
	 * 
	 * @throws Exception
	 */
	public static Object translate2singed(Object number, char sign)
			throws Exception {
		if (number.toString().equals("0"))
			return number;
		if (!isNumeric(number))
			throw new IllegalArgumentException("NaN: " + number);

		Class numClass = number.getClass();
		Number minval = limit(numClass, true);

		if (sign == '+' && minval.toString().equals(number.toString()))
			throw new IllegalArgumentException(
					"out of Class limits number*(-1) bigger then MAX_VALUE : "
							+ number);

		if (isIntegral(number)) {
			Long numberL = Long.parseLong(number.toString());
			Long minL = Long.parseLong(limit(numClass, true).toString());
			Long maxL = Long.parseLong(limit(numClass, false).toString());
			if (numberL > maxL || numberL < minL)
				throw new IllegalArgumentException("out of Class limits : "
						+ minL + ", " + maxL + ", " + numClass + ", " + numberL);
		}
		if (isFloatingPoint(number)) {
			Double numberD = Double.parseDouble(number.toString());
			Double minD = Double.parseDouble(limit(numClass, true).toString());
			Double maxD = Double.parseDouble(limit(numClass, false).toString());
			if (numberD > maxD || numberD < minD)
				throw new IllegalArgumentException("out of Class limits : "
						+ minD + ", " + maxD + ", " + numClass + ", " + numberD);
		}

		// integral-Numbers
		if (isIntegral(number)) {
			Long integralNumber = Long.valueOf(number.toString());
			switch (sign) {
			case '+':
				number = Math.abs(integralNumber);
				break;
			case '-':
				number = -1 * Math.abs(integralNumber);
				break;
			case '*':
				if (new Random().nextBoolean())
					number = -1 * Math.abs(integralNumber);
				break;
			default:
				throw new IllegalArgumentException("unknown sign char: " + sign);
			}
		}
		// floating point
		if (isFloatingPoint(number)) {
			Double floatingNumber = Double.valueOf(number.toString());
			switch (sign) {
			case '+':
				number = Math.abs(floatingNumber);
				break;
			case '-':
				number = -1 * Math.abs(floatingNumber);
				break;
			case '*':
				if (new Random().nextBoolean())
					number = -1 * Math.abs(floatingNumber);
				break;
			default:
				throw new IllegalArgumentException("unknown sign char: " + sign);
			}
		}
		Constructor construct = numClass.getConstructor(String.class);
		// System.out.println("numClass: "+numClass);
		// System.out.println("sign: "+sign);
		// System.out.println("number: "+number.toString());
		number = construct.newInstance(number.toString());

		return number;
	}

	/**
	 * convenience method has Object numeric Type
	 */
	public static boolean isNumeric(Object number) {
		return isNumericClass(number.getClass());
	}

	/**
	 * convenience method ic class numeric Type
	 */
	public static boolean isNumericClass(Class numberClass) {
		boolean isNumericClass = false;
		if (ArrayUtils.contains(numericTypes, numberClass)
				|| ArrayUtils.contains(numericClasses, numberClass)) {
			isNumericClass = true;
		}
		return isNumericClass;
	}

	/**
	 * convenience method is Object from integral-Class
	 */
	public static boolean isIntegral(Object number) {
		return isIntegralClass(number.getClass());
	}

	/**
	 * convenience method is Class from integral-Classes
	 */
	public static boolean isIntegralClass(Class nClazz) {
		if (!isNumericClass(nClazz))
			throw new IllegalArgumentException("NaN Class: " + nClazz);
		if (nClazz.isPrimitive())
			nClazz = ClassUtils.primitiveToWrapper(nClazz);
		return ArrayUtils.contains(integralClasses, nClazz);
	}

	/**
	 * convenience method is Object from FloatingPoint-Class
	 */
	public static boolean isFloatingPoint(Object number) {
		return isFloatingPointClass(number.getClass());
	}

	/**
	 * convenience method is Object from FloatingPoint-Class
	 */
	public static boolean isFloatingPointClass(Class nClazz) {
		if (!isNumericClass(nClazz))
			throw new IllegalArgumentException("NaN Class: " + nClazz);
		if (nClazz.isPrimitive())
			nClazz = ClassUtils.primitiveToWrapper(nClazz);
		return ArrayUtils.contains(floatingPointClasses, nClazz);
	}

	/**
	 * @param numberClass
	 * @return randomValue from numberClass
	 * @throws Exception
	 */
	public static Number randomValueByClass(Class numberClass) throws Exception {
		return randomValueByClass(numberClass, null);
	}

	/**
	 * @param numberClass
	 * @param sign
	 *            TODO
	 * @return randomValue from numberClass
	 * @throws Exception
	 */
	public static Number randomValueByClass(Class numberClass, Character sign)
			throws Exception {
		Number randomValue = 0;
		// validations
		if (!isNumericClass(numberClass))
			throw new IllegalArgumentException("Class is not numeric!: "
					+ numberClass);
		if (numberClass.isPrimitive())
			numberClass = ClassUtils.primitiveToWrapper(numberClass);

		// generating Values for all numeric classes
		if (numberClass == Byte.class) {
			byte[] bytes = new byte[1];
			random.nextBytes(bytes);
			Byte b = bytes[0];
			randomValue = b;
		}
		if (numberClass == Short.class) {
			Integer i = random.nextInt(Short.MAX_VALUE);
			Short s = i.shortValue();
			randomValue = s;
		}
		if (numberClass == Integer.class) {
			Integer i = random.nextInt();
			randomValue = i;
		}
		if (numberClass == Long.class) {
			randomValue = random.nextLong();
		}
		if (numberClass == Float.class) {
			randomValue = random.nextFloat();
		}
		if (numberClass == Double.class) {
			randomValue = random.nextDouble();
		}

		if (sign != null) {
			try {
				randomValue = (Number) translate2singed(randomValue, sign);
			} catch (IllegalArgumentException e) {
				System.out.println(e);
				if (e
						.getMessage()
						.startsWith(
								"out of Class limits number*(-1) bigger then MAX_VALUE"))
					if (isIntegral(randomValue)) {
						randomValue = Long.parseLong(randomValue.toString()) + 1;
					}

				if (isFloatingPoint(randomValue)) {
					randomValue = Double.parseDouble(randomValue.toString()) + 1;
				}
				randomValue = (Number) translate2singed(randomValue, sign);
			}
		}

		return randomValue;
	}

	/**
	 * @param min
	 *            Value
	 * @param max
	 *            Value
	 * @param sign +, -, *
	 * @return random Number-Object from numberClass between ]min,max[
	 * 
	 */
	public static Number randomValueByRange(Class numberClass, Number min,
			Number max, char sign) {
		NumberRange range=new NumberRange(min,max);
		Number randVal=null;
		Double delta = Math.abs(range.getMaximumDouble()-range.getMinimumDouble());
		randVal=random.nextDouble()*delta+range.getMinimumDouble();
		if (numberClass==Byte.class) randVal=randVal.byteValue();
		if (numberClass==Short.class) randVal=randVal.shortValue();
		if (numberClass==Integer.class) randVal=randVal.intValue();
		if (numberClass==Long.class) randVal=randVal.longValue();
		if (numberClass==Float.class) randVal=randVal.floatValue();
		if (numberClass==Double.class) randVal=randVal.doubleValue();
		return randVal;	
	}

	/**
	 * @param min
	 *            Value
	 * @param max
	 *            Value
	 * @param sign +, -, *
	 * @return random Number-Object from numberClass between ]min,max[ *
	 * @deprecated Use {@link #randomValueByRange(Class,Number,Number,char)}
	 *             instead
	 */
	public static Number randomValueByRangeOLD(Class numberClass, Number min,
			Number max, char sign) {
		// validations
		if (!isNumericClass(numberClass))
			throw new IllegalArgumentException("Class is not numeric!: "
					+ numberClass);
		if ((min.getClass() != max.getClass()))
			throw new IllegalArgumentException(
					"min- & max-Classes are different!");
		if ((min.getClass() != numberClass))
			throw new IllegalArgumentException(
					"min- & max-Classes don't match numberClass! : "
							+ numberClass);

		if (numberClass.isPrimitive())
			numberClass = ClassUtils.primitiveToWrapper(numberClass);
		Object numObject = ConvertUtils.convert("0", numberClass);
		RandomDataImpl randData = new RandomDataImpl();
		// Long isIntegral

		Number retNumber = 0;
		if (isIntegralClass(numberClass)) {

			Long randNumber;

			if (min.longValue() <= max.longValue()) {
				randNumber = randData.nextLong((Long) min.longValue(),
						(Long) max.longValue());
			} else {
				randNumber = randData.nextLong((Long) max.longValue(),
						(Long) min.longValue());
			}

			if (numberClass == Short.class) {
				return randNumber.shortValue();
			}
			if (numberClass == Byte.class) {
				return randNumber.byteValue();
			}
			if (numberClass == Integer.class) {
				return randNumber.intValue();
			}
		}
		return retNumber;
	}

	/**
	 * @param numberClass
	 * @param min
	 *            true: MIN_VALUE, false: MAX_VALUE
	 * @return MIN_ or MAX_VALUE
	 * @throws Exception
	 */
	public static Number limit(Class numberClass, boolean min) throws Exception {
		Number num = 0;
		if (!isNumericClass(numberClass))
			throw new IllegalArgumentException("Class is not numeric!: "
					+ numberClass);
		if (numberClass.isPrimitive())
			numberClass = ClassUtils.primitiveToWrapper(numberClass);
		Field field = null;
		if (min)
			field = numberClass.getField("MIN_VALUE");
		else
			field = numberClass.getField("MAX_VALUE");

		num = (Number) field.get(num);
		// System.out.println("minval: "+minval.toString());
		// System.out.println("field.getGenericType();"+field.getGenericType());
		//		
		return num;
	}

}
