/**
 * TestSuite 
 *  Regression-Test
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
	
	@RunWith(Suite.class)
	@Suite.SuiteClasses({
//passed 
		tools.MyMathTest.class,
	    tools.MyImageTest.class,
	    tools.DateCalculatorTest.class,
		tools.ToolBoxTest.class,	
		model.DataBaseTest.class,
		model.DBTableTest.class,
		model.CelestialObjectTest.class,
		model.ObservationTest.class,
		model.MaserComponentTest.class, 
		model.UnitTest.class,
		controller.ObservationSeriesTest.class,
		view.GridImageTest.class,
		view.ColorCodeImageTest.class,
		view.ObservationImageTest.class,
		view.TextDataImageTest.class,
		view.MaserComponentsImageTest.class,
		controller.ObservationImageBuilderTest.class,
//		Errors & Warnings 
		controller.MaserJTest.class
	        })
	public class TestSuite {
	}