
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import tools.ToolBox;
import tools.ToolBoxTest;

@RunWith(Parameterized.class)
public class ParameterizedTest {
	final static String FS = System.getProperty("file.separator");

	final static String PS = System.getProperty("path.separator");

	ToolBoxTest toolBoxTest;

	static int runCount;

	static int runs = 3;

	private static Class[] testClasses;

	private static ArrayList<Object> testObjects = new ArrayList<Object>();

	private int run;

	@Parameters
	public static Collection data() {
		Object[][] retList = new Object[runs][1];
		for (int i = 0; i < runs; i++) {
			retList[i][0] = i;
		}
		return Arrays.asList(retList);
	}

	/**
	 * @param input
	 * @param expected
	 * @throws Exception
	 */
	public ParameterizedTest(int run) throws Exception {
		ToolBox.debugMsg("i am method Constructor()");
		this.run = run;
		runCount++;
		System.out.println("runCount: " + runCount);
		// building Instances of classes to Test with Parameters(or without)
		if (runCount == 1)
			testClasses = findTestClasses(); // only once
		Constructor ctor;
		for (int i = 0; i < testClasses.length; i++) {
			try {
				ctor = testClasses[i].getDeclaredConstructor(int.class);
				testObjects.add(ctor.newInstance(runCount));
				//
			} catch (Exception e) {
				// TODO: handle exception
				//e.printStackTrace();
			}
		}
		// dCTest = new DateCalculatorTest(input);//call with
		// toolBoxTest = new ToolBoxTest();
		// runTestClasses();
	}

	@Test
	public void runTestClasses() throws Exception {
		ToolBox.debugMsg("i am mthod runTestClasses()");
		for (int i = 0; i < testObjects.size(); i++) {
			runMethodsFromClass(testObjects.get(i));
		}
	}

	/**
	 * search all TestClasses in Project
	 * 
	 * @throws Exception
	 * @throws Exception
	 */
	Class[] findTestClasses() throws Exception {
		// findPath
		// String
		String testClassPath = System.getProperty("java.class.path").split(":")[0];
		System.out.println(testClassPath);

		File dir = new File("");

		// findTestClasses
		String[] pathes = ToolBox.listFilesRecursiveByFilter(testClassPath,
				true, "*Test.class");
		String thisName = this.getClass().getName();
		Arrays.sort(pathes);
		int i = Arrays.binarySearch(pathes, testClassPath + FS + thisName
				+ ".class");
		Array.set(pathes, i, "");
		ArrayList<Class> classes = new ArrayList<Class>();
		System.out.println(this.getClass().getClassLoader());
		for (int j = 0; j < pathes.length; j++) {
			if (pathes[j].length() > 0) {
				String className = pathes[j].replaceAll(".class", "");
				className = className.replaceAll(testClassPath + FS, "");
				className = className.replaceAll(FS, ".");
				System.out.println(className);
				Class c = this.getClass();
				try {
					c = Class.forName(className, true, this.getClass()
							.getClassLoader());
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				classes.add(c);
			}
		}

		System.out.println(Arrays.deepToString(pathes));
		Class[] retClasses = new Class[classes.size()];
		return classes.toArray(retClasses);

	}

	/**
	 * @param obj
	 *            Class Instance under Test
	 * @throws Exception
	 */
	void runMethodsFromClass(Object obj) throws Exception {
		ToolBox.debugMsg("i am mthod runMethodsFromClass() runCount: "+runCount);
		Method[] methods = getMethods2run(obj.getClass());

		// number of runs by runs=value of runParameterized
		// ERROR starts everytime at runcount 1 ?!
		for (Method method : methods) {
			if (runCount <= getRuns(method.getAnnotations()[0])) {
				method.invoke(obj, null);
			}
		}
	}

	/**
	 * @param c
	 *            TestClass with methods
	 * @return all methods to runParameterized TODO - dont ignore
	 *         Annotation:@Ignore ?! - check runs
	 * @throws Exception
	 */
	Method[] getMethods2run(Class c) throws Exception {
		ToolBox.debugMsg("i am mthod getMethods2run()");
		ArrayList methods2run = new ArrayList();
		Method[] methods = c.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Annotation[] annArr = methods[i].getDeclaredAnnotations();
			for (Annotation annotation : annArr) {
				if (annotation.annotationType().toString().equals(
						"interface tools.runParameterized")) {
					methods2run.add(methods[i]);
				}
			}
		}
		methods2run.trimToSize();
		Method[] retMethods = new Method[methods2run.size()];
		methods2run.toArray(retMethods);
		return retMethods;
	}

	/**
	 * @param Annotation
	 *            from Method
	 * @return number of runs by value of runParameterized(runs=N)
	 * @throws Exception
	 */
	int getRuns(Annotation ann) throws Exception {
		Integer runs = 1; // default one run
		String runsStr = ann.toString();

		if (runsStr.indexOf("runs=") > -1) {
			runsStr = runsStr.split("runs=")[1];
			runsStr = runsStr.split("\\)")[0];
			runs = runs.parseInt(runsStr);
		}
		return runs;
	}

}