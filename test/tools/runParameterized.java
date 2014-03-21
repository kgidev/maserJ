package tools;



import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Sometimes you want to enable/dsiable a method to test with
 * 
 * @RunWith(Parameterized.class). Methods annotated with {@link org.junit.Test}
 *                                that are also annotated with
 *                                <code>&#064;runParameterized</code> will be
 *                                executed as by a Parameterized TestClass .
 *                                </p>
 * Parameter for number of runs: runs 
 * @since Junit 1.4
 * @since JDK 1.5
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.TYPE })
public @interface runParameterized {
	/** 
	 * Optionally specify <code>runs</code> a number of desired runs.*/
	int runs() default 1; 
}
