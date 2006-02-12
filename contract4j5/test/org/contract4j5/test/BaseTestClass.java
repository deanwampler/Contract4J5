/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.contract4j.org
 *
 * Licensed under the Eclipse Public License - v 1.0; you may not use this
 * software except in compliance with the License. You may obtain a copy of the 
 * License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * A copy is also included with this distribution. See the "LICENSE" file.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
package org.contract4j5.test;

import org.contract4j5.Contract;
import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

/**
 * A (contrived) example Java class that demonstrates how to define DbC tests.
 * The "@Contract" annotation is required. Then, we define a class-level
 * invariant, which happens to be for one of the fields. Note that we have 
 * to prefix the field name with "$this", one of several special keywords that
 * begin with "$" and are replaced with special values before passing the 
 * expression to Jexl. In this case, "$this" means "this object" (You can't just
 * use "this" without the "$" for backwards compatibility reasons - sorry). 
 * Prefixing field names with $this is necessary for Jexl to be able to
 * resolve the variable name. While not required in all cases, as a rule it is
 * best to always refer to fields this way for consistent. The one case where
 * you don't need the "$this." is when you define an invariant for a field 
 * itself (See the test for "name" below).
 */
@Contract
@Invar("$this.lazyPi==3.14159")	// see comments for "lazyPi" below.
public class BaseTestClass {
	/**
	 * A field that is initialized "lazily", but cannot change after that. This
	 * invariance is enforced by the @Invar annotation on the class. 
	 * The constructor must call {@link #getLazyPi()} BEFORE ANY OTHER PUBLIC
	 * FUNCTION, or the invariant test will fail!
	 * NOTE: the Jexl parser chokes if the invariant test appends "f" to the 
	 * constant!
	 */
	private float lazyPi = -1f;

	/**
	 * "getLazyPi()" always simply sets the value to 3.14159, so the
	 * class invariant "$this.lazyPi==3.14159" will always pass. However,
	 * see {@link #setLazyPi(float)}.
	 * @return pi
	 */
	public float getLazyPi() {
		if (lazyPi == -1f) {
			lazyPi = 3.14159f;
		}
		return lazyPi;
	}
	
	/**
	 * This function exists so the unit tests can force a failure!
	 */ 
	public void setLazyPi (float f) {
		lazyPi = f;
	}

    /**
     * A field that should never be null or "". See also comments in
     * {@link #setName(String)}. Note that you can safely use the "bare"
     * field name "name" here. You can also use "$this.name", which you have
     * to use in all other types of tests (i.e., tests other than the invariant
     * test on the field itself). You can also use the keyword "$target", which
     * currently is only used to refer to a corresponding field when used in a
     * test expression. (In the future, "$target" may have other uses in the 
     * more general AspectJ-sense of the poincut "target()" expression.)
     * NOTE: You can specify an optional error message that will be reported
     * with any failure message.
     */
    @Invar(value="name != null && name.length() > 0",
		   message="this.name must never be null!")
    private String name;

    /**
     * @return String name of the object
     */
    public String getName() { return this.name; }

    /**
     * Use a precondition to prevent setting name to null. Note this test is
     * less restrictive than the invariant test on the field itself, a poor
     * design. (Hopefully, the developer will realize the mistake when one test
     * fails while the other passes.) In this case, this "mistake" is useful
     * for the dbc4j unit tests.
     * @param name String naming the object
     */    
    @Pre("name != null")
    public void setName (String name) { this.name = name; }

    // A flag; used for other contract tests.
    private boolean flag;

    /**
     * Set the flag.
     */
    public void setFlag () { flag = true; }

    /**
     * Set the flag. This method is used in unit tests to force a contract 
     * assertion failures.
     */
    public void setFlag (boolean f) { flag = f; }

    /**
     * Constructor. Note that the precondition on the "name" parameter
     * is redundant, since {@link #setName(String)} is called, but it is still 
     * useful for documenting the interface. Note that the @Pre test does not
     * define a test expression. In this case, C4J5 uses a 
     * {@link org.contract4j5.testexpression.DefaultTestExpressionMaker} to
     * generate a default test expression. There are separate "makers" for 
     * different types of tests and contexts and they are user configurable.
     * For preconditions, the default is to require that all arguments are 
     * non-null.
     * Note that tests can call methods, too, but watch for side effects,
     * especially since tests will normally be disabled in production builds.
     * Therefore, never call a method with side effects!
     * @param name a non-null String
     */
    @Pre
    @Post ("$this.isValid() == true")
    public BaseTestClass (String name) {
    	/* float ignore = */ getLazyPi();
		setName (name);
		setFlag ();
    }
    
    /**
     * Constructor. As discussed in {@link #BaseTestClass(String)}, the default
     * test expression for the precondition test will be that all parameters,
     * in this case "name" and "flag", must be non-null. What does that mean for
     * "flag", which is boolean. Not much; this argument will be converted to
     * {@link java.lang.Boolean} internally and it will never be null!
     * Also, in this example, the precondition test is actually redundant, 
     * since {@link #setName(String)} is called. However, the test is still 
     * useful for documenting the interface.
     * @param name a non-null String
     * @param flag a boolean flag; if false, causes the postcondition to fail.
     */
    @Pre
    @Post ("$this.isValid() == true")		// watch out for side effects!
    public BaseTestClass (String name, boolean flag) {
    	/* float ignore = */ getLazyPi();
		setName (name);
		setFlag (flag);
    }
    
    /**
     * Is the object valid?
     */
    public boolean isValid () {
    	System.out.println ("ExampleClass.isValid(): flag = " + flag);
     	return flag;		// reusing our flag...
	}

    /** 
     * Method that requires flag to have been previously set. E.g.,
     * {@link #setFlag(boolean)}, {@link #doIt()}, etc. Note the postcondition 
     * to confirm that the method succeeded, where "$return" is the keyword 
     * that matches the value returned by the method (an int in this case).
     */
    @Pre(value="$this.flag == true", 
    	 message="this.flag must be true before calling 'doIt()'.")
    @Post("$return == 0")
    public int doIt () {
		if (name != null && name.equals("bad name")) {
			return 1;
		}
		return 0;
    }

    /** 
     * Overloaded method. Useful to confirm that the generated tests correctly
     * discriminate between the methods (note the conflicting @Post annotations
     * on the two versions.)
     */
    @Post("$return != 0")
    public int doIt (int toss) {
		if (name.equals("good name")) {
			return 1;
		}
		return 0;
    }
    
    /** 
     * Method with tests on more than one parameter. The keywords "$args[n]"
     * refer to the parameter arguments, counting from 0.
     */
    @Pre ("$args[0]> 0 && $args[1].equals(\"foo\")") 
    public int doThat (int toss, String fooStr) {
		return toss;
    }
    
    /** 
     * Method with tests on more than one parameter. Tests whether we correctly
     * generate matching aspects on the second and last parameter.
     * Note that a nested string in a test must be escaped.
     */
    @Pre ("toss2 > 0 && toss4.equals(\"foo\")") 
    public int doTheOther (int toss1, int toss2, String toss3, String toss4) {
		return toss1;
    }
    
    /**
     * Test contract4j5 with a nested class
     */
    @Contract()
    public static class NestedBaseTestClass {
     	private String name;
     	@Post
     	public String getName() {
     		return name;
     	}
     	
     	@Pre
     	public void setName(String name) {
     		this.name = name;
     	}
     	
     	@Invar ("$target > 0")
     	private int positive;
 
 		/**
 		 * Method to force the invariant test to fail, if a negative argument
 		 * is used.
 		 */
 		public void setPositive (int p) { this.positive = p; }
 		
 		public int getPositive () { return this.positive; }
 		 
     	// The @Post on "name" should really be a @Pre on "nm", as it is more
     	// restrictive, but it is useful for example purposes.
     	@Post ("$this.name != null && $this.name.length() > 0 && nm != null")  
     	NestedBaseTestClass (String nm) {
     		this.name = nm;
     		this.positive  = nm != null ? nm.length() : -1;
		}
    }
}
