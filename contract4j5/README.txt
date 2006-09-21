Contract4J5 0.5.0 README  

Contract4J5 (Annotation form)
v0.6.0.0   October 1, 2006
v0.5.0.0   February 20, 2006
v0.1.1.0   October 4, 2005
v0.1.0.2   April 24, 2005
v0.1.0.1   February 6, 2005
v0.1.0     January 31, 2005

Contract4JBeans (Experimental "JavaBeans"-like form)
v0.3.0.0   February 20, 2006
v0.2.0.0   October 5, 2005
v0.2.0M1   August 15, 2005

Dean Wampler 
http://www.contract4j.org
http://www.aspectprogramming.com/contract4j

Contents:

** Copyright
** A Word About Naming and Versioning
** What Is "Contract4J"?
  --- What is Design by Contract?
  --- Design by Contract and Aspect-Oriented Programming
  --- How Does Contract4J Support Design by Contract?
  --- How Do I Use Contract4J?
  --- Show Me the Code!
  --- Invocation and Configuration of Contract4J
  --- Debugging Tips
** TODO Items
** Notes for Each Release
** General Notes
** For Further Information...

Impatient? If you want to see what Java code looks like with Contract4J in 
action, go to the section "Show Me the Code!"


** Copyright

This is open source software covered by the Eclipse Public License - v 1.0. A 
complete copy of the license can be found in the LICENSE file in the top-level
directory of the distribution.

===============================================================================
Copyright 2005,2006 Dean Wampler

   Licensed under the Eclipse Public License - v 1.0; you may not use this
   software except in compliance with the License. You may obtain a copy of the 
   License at

       http://www.eclipse.org/legal/epl-v10.html

   A copy is also included with this distribution. See the "LICENSE" file.
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
===============================================================================

** A Word About Naming and Versioning

I made an unfortunately strategic choice when I released the previous 
annotation-based version as "v1.0", then released the JavaBeans-inspired
("ContractBeans") version as "v2.0". Neither was really "1.0" caliber, 
especially since some subsequent unavoidable changes broke backwards 
compatibility. Even worse, the annotation form is really the one I intend to 
maintain going forward; the ContractBeans form was an interesting but not 
completely successful experiment and it will not be developed any further. 
Therefore, labeling it v2.0 was completely misleading.

To correct these problems, I have renamed and renumbered them as follows:

Annotation Form:
	Contract4J v1.X.Y  --> Contract4J5 v.0.1.X.Y  
The "5" emphasizes that it requires Java 5 annotations

"ContractBeans" Form:
	Contract4J v2.X.Y --> Contract4JBeans v.0.2.X.Y

The newest release described in these notes is "Contract4J5 v.0.6.0". 
As discussed below, the last several "0.X" releases have introduced some 
non-backwards-compatible API changes, but they offer significant advantages
over the previous releases. Once the API is stable and some other "TODO" items 
are finished, I will release Contract4J5 as the "real" v1.0. Note that 
development has stoped on the "ContractBeans" implementation.


** What Is "Contract4J"?

Contract4J supports "Design by Contract"(TM) programming in Java. a programming
practice introduced by Bertrand Meyer and incorporated in the Eiffel 
programming language in the 1980's. 

The Contract4J project is sponsored by Aspect Research Associates 
(http://www.aspectresearchassociates.com), a consulting firm specializing in
Aspect-Oriented Programming, Enterprise Java, and Ruby on Rails.


--- What is Design by Contract?

Design by Contract (DbC) starts with the observation that, implicitly or 
explicitly, a component defines a "contract" with its clients. When a client
invokes an operation on the component, it must agree to provide the component 
with appropriate inputs and context. Otherwise, the component can't perform its 
services. In return, if the input constraints are satisfied the component
guarantees delivery of prescribed results. 

DbC encourates the component developer to state the contract explicitly, by 
specifying the input constraints, known as "preconditions", and guaranteed
results, known as "postconditions", in a programmatic form that can be tested
at runtime. In addition, state "invariants" may be defined. 

DbC is a powerful and underused tool for detecting bugs during development and
testing. A key principle is that if a test fails during execution, the program
terminates abruptly. While this may seem draconian, it forces the developer to
solve the problem immediately, rather than allow problems to "slide", multiple,
and thereby undermine the quality of the software. 

Hence, during development, all tests are enabled and the code is thoroughly 
tested. During deployment, the tests are often disabled, both to prevent sudden
shutdown and and to allow possible recovery should a contract-violating
condition arise that was never detected during development. Turning off the 
tests also removes their overhead.

As such, DbC is a wonderful complement to Test-Driven Development 
(http://www.junit.org/news/article/test_first/index.htm), which exercises the  
code and hence the contract tests, thereby increasing the probability they will
detect bugs. DbC tends to emphasize the fine-grained design a little more than
test-driven development by itself. Designing the unit tests and specifying the
contracts also force the developer to think through the details of the design 
before writing the code. A third technique that supports thinking through the
design is to write the comment blocks for classes and methods before 
implementing them.

For more on Design by Contract, see "Building bug-free O-O software: An 
introduction to Design by Contract(TM)"
(http://archive.eiffel.com/doc/manuals/technology/contract/page.html) and the
discussion of DbC in the larger context of Agile Methods in Martin, et al., 
"Agile Software Development: Principles, Patterns, and Practices", Prentice 
Hall, 2003 (ISBN 0-13-597444-5).


--- Design by Contract and Aspect-Oriented Programming

So what does DbC have to do with Aspect-Oriented Programming (AOP)? On the 
one hand, the component's contract is an essential part of the complete, logical
component specification that clients must support. For example, an interface for
a bank account may have a contract requirement that all methods that return a 
balance must always return a non-negative number (ignoring overdraft features).
However, in practical terms, contracts often include implementation concerns
that may have little relationship to the domain logic of the application. 
For example, the code implementing the bank account may prohibit passing null 
values as method parameters. 

For both types of contract details, AOP allows us to specify the details with
sufficient "proximity" to the interface so that clients can see the constraints 
and AOP gives us an elegant way of testing the constraints at runtime without
cluttering the code with logic to run the tests and handle failures. 

More generally, AOP is a new approach to modularizing "concerns" that need to be
handled by a component, but which tend to obscure the main logic of the 
component, often compromising clarity, maintainability, reusability, etc. For 
example, modern web and enterprise applications typically must support secure 
access, transactional behavior, persistence of data, and mundane support issues
like logging. Without AOP, the code for these "concerns" gets mixed in with the
domain logic, thereby cluttering the code and diminishing the "ilities" we all 
strive for. AOP keeps these concerns in separate modules and provides powerful
facilities for "injecting" the concern behavior in the specific execution
points where needed. Contract4J uses AOP techniques to find the contract
specifications and test them at runtime at the appropriate execution points.

AOP is a good approach to supporting DbC because it permits DbC concerns to
be managed in a modular and minimally-intrusive way, without cluttering  
application logic, while still allowing the contracts to be integrated into the
runtime environment for development and testing. Contract4J uses the best-known
AOP language, AspectJ, to support DbC for Java.

For more information on AOP, see the references below.


--- How Does Contract4J Support Design by Contract?

These notes will describe how the annotation-based Contract4J5 v0.5.0 works. For
information on the experimental "ContractBeans" form, see the README that comes
with its distribution.

I'm a long-time believer in DbC and wanted to use it in Java. A few years
ago, I discovered the clever "Barter" project, which supports DbC in Java
using XDoclet tags and AspectJ code generation to perform the tests as "advice"
(http://barter.sourceforge.net/). 

A problem with doclet-based approaches is that they are buried in the comments
and hence decoupled from the runtime environment. Java 5 introduced annotations, 
which are like "javadoc tags for code". In particular, annotations are used to
ascribe meta-information to the code and to make that information available to
the runtime environment, when desired. Annotations are a logical tool for 
associating contract tests with code and Contract4J5 uses them for this purpose.
The specifications are written as executable Java expressions, enclosed in the
String "value()" attribute of the annotation. The details are discussed below.
A contrived example suffices for now:

@Contract
public class MyClass {
  @Invar ("name != null")
  private String name;

  @Pre ("n != null")
  public void setName (String n) { name = n; }
  @Post ("$return != null")
  public String getName () { return name; }

  @Pre ("n > 10 && s != null")
  @Post ("$return != null")
  public String  doIt (int n, String s) {...}
  ...
}

The "@Contract" annotation tells Contract4J that "MyClass" defines tests. The
tests are defined using "@Pre", "@Post", and "@Invar" annotations, for 
precondition, postcondition, and invariant tests respectively. In this example,
the tests are:
1) The field "name" has an invariant test that it can never be null (after
the object has been constructed...).
2) The "setName" method has a precondition that the value of the parameter
cannot be null.
3) The "getName" method has a postcondition that it can never return null.
4) The "doIt" method has both a pre- and a postcondition test.

If a test fails, Contract4J throws an unchecked exception, ContractError, after
printing some diagnostic information (see the unit tests for examples). A 
special subclass of ContractError is thrown if the test itself can't be 
evaluated for some reason (discussed in more detail below). That error is 
TestSpecificationError. So, clients who want to catch contract errors and also
distinguish between these two types should follow this idiom:
  try {
    ...
  } catch (TestSpecificationError tse) {
    ...
  } catch (ContractError ce) {
    ...
  }

Contract4J5 embeds the Jakarta "Jexl" interpreter, an expression evaluator,
to evaluate the test expressions in the annotations. (See  
http://jakarta.apache.org/commons/Jexl/ for more information on Jexl. Note,
that there are quirks and limitations of Jexl that are discussed throughout this
README. It might be worthing searching for "Jexl" to find and read those
details.)

The v0.1 version of Contract4J5 used a preprocessing approach. Using a plugin
for Sun's Annotation Processor Tool (APT), it generated AspectJ code that
hard-coded the test expressions. The generated code and the original code were
then compiled. 

V0.5 eliminated this preprocessor step, thereby simplifying adoption. All you
have to do is add a final AspectJ weaving step to your build, write your tests
in familiar annotations, and you are done. No custom AspectJ code is required.

V0.6 refined the internal structure of Contract4J to provide better 
configuration options.

---- Inheritance Behavior of Contracts

In DbC, there are rules for proper behavior of inherited contracts, based on the
Lyskov Substitution principle (LSP), which is a minimal definition of 
inheritance. Class B is considered a child class of class A, if objects of type
B can be substituted for objects of type A without breaking the program. In DbC
terms, this means that class B must obey A's contract, including all the class, 
method, and field tests.

However, there is one nuance affecting derived preconditions and postconditions.
A derived (overriding) precondition test can actually satisfy a "looser"
restriction than the overridden test. Put another way, the set of valid inputs
can be larger than the set for the parent (overridden) test. This is because 
precondition tests are tests the client must meet, so if a client already meets
a strict test defined by the overridden test, then it will also satisfy a 
looser derived test transparently. This is "contravariant" behavior because
even though subclassing is a form of increasing specialization and restriction,
the restrictions imposed by the precondition test can actually grow looser.

In contrast, postcondition tests are "covariant", meaning they must be as
narrow or narrower than the tests they override. This is true because
postcondition tests are tests on the results the component promises to deliver,
as opposed to tests on clients. So, if the client is expecting a result in a set
of possible results and a derived test narrows the set further, then the result
will still satisfy the client's expectations.

Contract4J provides only minimal support for contravariant precondition tests 
and covariant postcondition tests. First, because Java 5 annotations on methods 
are NOT inherited, it is a requirement for writers of subclass method overrides 
to also include the annotations on the parent method. However, the annotations 
do not have to reproduce the test expressions. C4J5 will locate the 
corresponding parent class test expressions automatically. In contrast, 
class-level invariants are inherited, since class annotations can be inherited.
(However, it is harmless to repeat those in subclasses, too.)

C4J5 attempts to enforce the rule that invariant tests can't change. However,
it uses a simple string comparison, ignoring whitespace, so some logically
equivalent expressions may get flagged incorrectly as different. For example:

	a == b   vs.   b == a

will appear to be different, when they are logically equivalent.

To properly write contravariant precondition tests and covariant postcondition
tests, you will have to repeat the "inherited" test expression and "append"
the appropriate refinements, e.g.,

	@Pre("new_test || parent_test")
	@Post("new_test && parent_test")
 
However, since the parent test is always valid for the derived method override,
if you don't need to modify the test, then you can simply use the @Pre or @Post
annotation without a test expression and C4J5 will find the parent expression.

--- How Do I Use Contract4J?

The distribution contains ant files and examples of how to use Contract4J. 
The examples are actually part of the unit/acceptance test suite. The tests are
the best way to see how to write test expressions correctly.

---- Installation:

For Linux/Unix systems, use these commands: 

	1) cd ~/work		# or wherever...
	3) cp .../contract4j5_060.tar.gz .
	4) tar xvzf contract4j5_060.tar.gz 

On Windows systems, Unzip the zip file to an appropriate location.

You will need Java 5, AspectJ 5, Jakarta Commons Jexl 1.0, and Commons Logging
(required by Jexl) to use Contract4J5. If you build Contract4J yourself, you 
will also need JUnit. If you build the Spring Framework example, which shows
how to configure Contract4J using Spring, you will need v1.2.5 or later of 
Spring.

You can use the installed "contract4j5.jar" file as is. If you want to rebuild
Contract4J, use the ant driver script "build.sh" or "build.bat", edit the file 
and change the environment variable definitions to point to appropriate 
locations for your environment. Or, you can define the appropriate environment
variables in your environment and use the build.xml ant script directly.

To use Contract4J in your own projects, add the jar to your class path and add
a binary weaving step to your build, if you are not already using the AspectJ
"ajc" compiler to compile your Java and AspectJ sources. To see how to do a
binary weaving step, consult the Ant build files in the distribution, in 
particular, the "test" target and dependencies. The "compile.test" target uses
javac to compile the JUnit test code. The "project-test.jar" target uses ajc to
do binary weaving, where the compiled class files are read by ajc, aspects are
woven into them (the "aspectpath" option to ajc) and the contract4j5-test.jar
file is output. The ant/targets.xml defines the "binaryWeaveTemplate" used for
this process.

To see the ajc command in action, invoke ant with the option 
  -Dbuild.compiler.verbose=true
which will cause ajc to print the command-line options used. Look for the output
that is part of the compile.test target. This output may be easier to understand
than trying to understand the ant files!

The distribution has the following structure. In the "contract4j5" directory,
you will find the following:

README.txt - This file
LICENSE.txt - The Apache 2 license file for Contract4J
build.sh - Unix/Linux build driver script. Sets "home" variables where the tools 
are found. Edit to taste.
build.bat - Windows build driver script. Also sets "home" variables...
build.xml - Ant build script

src - The source code tree.
test - The JUnit unit/acceptance tests, which also function as usage examples.
The files ending with "*Test.java" are JUnit tests. The other classes under 
"test" are example classes used by the tests, which also provide C4J5 usage 
examples. The JUnit test files often contain additional example classes that
demonstrate usage and they contain comments about tests the demonstrate known 
idiosyncrasies or limitations of C4J5 and Jexl evaluation.
classes - where build artifacts (except the jars) are stored
doc - Where Javadocs are written
contract4j5.jar - The runtime deployment jar. It contains the build products 
from "src".
contract4j5-test.jar - The jar containing the build products from "test", which
is not part of the normal runtime deployment.

In the separate "Contract4JWithSpring" directory, you will find the following:

README.txt - Description of the Spring example and build instructions
test - The only "source" directory with a single JUnit test class that runs the
example.


If you want to build Contract4J:

	1a) ./build.sh all    (*nix)
	1b) build.bat all     (windows)
or
	1c) ant all

Building "all" creates the jar files "contract4j5.jar" and "contract4j-test.jar"
in the current directory and runs the JUnit-based unit and acceptance tests in 
the "test" directory tree. 

To build the corresponding Spring example:

	2a) ./build.sh all.spring (*nix)
	2b) build.bat all.spring  (windows)
or
	2c) ant all.spring
	
(See ../Contract4J5WithSpring/README.txt for more information on this example.)

A preliminary example of using Contract4J with Load-Time Weaving (LTW) is 
included. Unfortunately, it is not complete because of an AspectJ bug, 
#158179, that prevents Contract4J from being used with load-time weaving.
(The bug involves weaving of annotations that are themselves annotated with
"@Inherited".) Once a patched version of AspectJ is available that fixes this
bug, I will do a "0.6.1" release with the completed LTW support example.

To run the tests using load-time weaving (discussed below), first build all,
then build the following arget:

	2a) ./build.sh test.loadtime.weaving (*nix)
	2b) build.bat test.loadtime.weaving  (windows)
or
	2c) ant test.loadtime.weaving

This runs the tests and does load-time weaving of the aspects on the fly.


Three different build approaches are demonstrated:

1) Weaving During Compilation:
The "src" directory is built with ajc, so weaving happens as part of 
compilation. In the build.xml and ant/targets.xml, see the "project.jar" and
"compile" targets for details.

2) Binary Weaving After Compilation
The "test" directory is built with javac (it contains only Java files), then 
binary weaving is used to weave in the aspects from the "contract4j5.jar" built
from "src". See the ant targets "compile-test" and "project-test.jar".

3) Load-Time Weaving
This is the least disruptive approach for using Contract4J in a pure Java 
environment, as it requires no ajc build step. Instead, you use the special 
"-javaagent:..." option for the JVM when starting your test runs. See the
target "_junitTemplate.loadtime.weaving" in ant/targets.xml, which demonstrates
invoking junit tests and doing load-time weaving. (Not yet working; see note
above.)


The tests generate a LOT of output, but they should all pass. You'll see words
like "Error" and "Fatal" go buy. That's okay. There will be some expected 
warnings that fall into two categories:
1) Warnings in some unit tests when test annotations are used without the
required @Contract annotation or when tests are defined for static methods, 
which isn't supported. These are deliberate for those tests.
2) Some unchecked casts involving generics.
3) The javadocs target also generates warnings for references to aspects
from Java files, which javadoc doesn't know how to resolve. The following 
missing "classes" are actually aspects:
  AbstractConditions
  ConstructorBoundaryConditions
  Invariant*Conditions (several)
  MethodBoundaryConditions
  UsageEnforcement
  Contract4JConfigurationEnsurer
You'll see lots of warnings about not being able to find members of these 
aspects.

If the unit tests fail, look for output in "contract4j5/TEST-*.txt" files.
Usually, the problem will be a classpath issue.

Next, we'll look at code examples, then return to a discussion of invoking and
configuring Contract4J5.

---- Show Me the Code!

Here is a large example showing how to define Contract4J5 tests in your code 
so that Contract4J5 can discover and execute them at runtime. It is the file
"test/org/contract4j5/test/BaseTestClass.java" (with some superfluous details
omitted). See additional examples in the unit/acceptance suite under the "test"
directory.

The comments in this class should be self explanatory. More specific details on
writing contracts are provided below.

----- BaseTestClass.java:

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
 * itself (See the test for "name" below). Note also that in order for Jexl to 
 * resolve the field reference, a JavaBeans "getter" method must exist for the 
 * field, even if the field is public!
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
   * NOTE: Jexl can't resolve "lazyPi" unless "getLazyPi()" exists!
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
     * with any failure message. Also, as stated before, "name" must have a 
     * "getName()" accessor or Jexl can't resolve it!
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


---- Details of Contract Specifications

Here are the rules for using Contract4J, which clarify the examples just
discussed.

1) Annotate Interfaces and Classes with "@Contract" 

Examples:
@Contract
public interface Foo { ... } 

@Contract
public class Bar { ... } 

Any interface or class that declares a contract must be annotated with 
"@Contract". Otherwise, the tests will be ignored. C4J5 will issue a warning 
during compilation, but if you use javac to compile and then weave later with
ajc, you may not get any warnings and the tests will be silently ignored. 

The annotation must also appear on any derived interfaces or classes if they
define new tests.

2) Define Class Invariants

Examples:
@Contract
@Invar("boolean_test_expression") 
public interface Foo { ... }

@Contract
@Invar(value="boolean_test_expression", message="The test failed") 
public class Bar { ... }

The second example shows the optional "message" that will be reported on error,
in addition to standard messages. The test expression must return boolean, or
it will be treated as a contract failure!

For subclasses, it isn't necessary to annotate them, too, because class-level
annotations are inherited, but you can do so for consistency with method 
annotations, which aren't inherited and must be added to subclass overrides.

3) Define Field Invariants

Examples:
@Contract
public class Bar {
  @Invar("name != null && name.length() > 0")
  private String name;
  public  String getName() { return name; }
  ...
}

Note that field invariants can't be defined on interfaces, since they don't have
mutable fields, but you can simulate the same thing by annotating corresponding
accessor methods in the interfaces (see below).

Note that for the field "name", we are able to use the "bare" field name when
defining an invariant test for it. You can also use "$this.name" or the
"$target" keyword. *However*, as discussed previously, the Jexl interpreter can 
only resolve the field if a JavaBeans "getter" method is defined for it, as 
shown in the example!

In the future, "$target" may be used more generally for objects that correspond
to AspectJ's "target()" pointcut expression, but currently "$target" is only 
used in field invariant tests to refer to the field.)

4) Define Method and Constructor Preconditions, Postconditions, and Invariants.
NOTE: we have to use "$this.name" in the following interface example, not just
"name" by itself, because we are no longer defining a field invariant test! 
Repeating for emphasis, don't forget that a field getter method is required for 
Jexl to resolve the field reference.

Examples:
@Contract
public interface Foo {
  @Invar("$this.name != null && $this.name.length() > 0")
  String getName();

  @Invar("$this.name != null && $this.name.length() > 0")
  void setName (String s);

  @Pre("$args[0] > 0")
  @Post("$this.i = $old($this.i) + $args[0]")
  void incrementI (int amount);
  ...
}

@Contract
public class FooImpl implements Foo {
  @Invar
  public String getName() {...}
  
  @Invar
  public void setName (String s) {...}

  @Pre @Post
  void incrementI (int amount) {...}

  int getI() { ... }   // getter method required by Jexl!
  ...
}
  
@Contract
public class Bar {
  private float factor;
  
  @Pre("fudge > 1.0")
  @Post("$this.factor > 1.0")
  void addFudgeFactor (float fudge) {...}
  
  @Pre("factor > 0.0")
  @Post("$this.factor > 0.0")
  public Bar (float factor) {
    this.factor = factor;
  }

	float getFactor() { ... }   // getter method required by Jexl!
}

The "Foo" interface simulates a field invariant test on an implied name field by
defining invariant tests on name's accessor methods. The precondition test for
"incrementI" requires the input amount to be positive. (You could also write
this test "amount > 0", but this is actually less robust; it is more likely to 
trip over parsing idiosyncrasies in Contract4J5 and Jexl.) This test implies an 
"int i" field, as does the post condition test which requires that the new
value of "i" be equal to the old value (grabbed by the "$old($this.i)" 
expression) plus the amount.

The "FooImpl" implementing class MUST repeat the annotations, but it can omit
the test expressions. Contract4J5 will determine the expressions from the parent
class or interface.

What if you omit the expressions? Currently, there is no compile-time checking
possible the tests will not be evaluated on these method implementations. For
subclass overrides of parent class methods, the tests will not be evaluated on
the overrides, but they will be evaluated on the parent methods if "super" is
called.

NOTE: A desired future enhancement is to either enforce proper usage of the 
annotations on implementing classes or subclasses or else evaluate the tests 
even when they are absent!

The "Bar" class shows that tests can also be defined for constructors. 
(Invariant tests are also allowed, but seldom useful, since contract tests
mostly focus on object state, which won't exist before construction!)

NOTE: Jexl appears to choke if floats have a trailing "f". As shown in the 
example, they should be omitted.

5) Special Keywords

The previous examples use the special keywords. Contract4J5 substitutes the
correct values before invoking Jexl to evaluate the expressions. Here is a
description of the keywords and their proper use.

$this    The "this" object under test
$target  A field in an invariant test. There *must* be a corresponding JavaBeans
         "getter" method or Jexl won't be able to resolve the field.
$result  The return result of a method; only valid in postconditions
$args[n] The "nth" argument in a parameter list
$old(..) The "old" value (before a method is actually executed) of the 
         contents of the expression, which can be one of the following:
         $old($this)   Not recommended, because only the reference is saved 
                       and the object pointed to by "this" may change! Use 
                       fields or method calls instead.
         $old($target) equal to $old($this.field). Be careful if "field" is
                       mutable; the value is not saved, just the reference to
                       the object!
         $old($this.field) Recommended usage, if "field" is primitive, in 
                       which case the value is captured, or it refers to an 
                       immutable object. Same for $old($target.otherField) 
         $old($this.method(x,y))  The returned value is saved. Due to parser
                       limitations, method calls may not contain nested method
                       calls.
        
The most important thing to remember about "$old(..)" is that Contract4J5 only
remembers the value, which may be a reference to a mutable object. (We can't 
rely on "clone()" working.) Try to use it only with primitives or immutable
objects like strings.
                       
6) Test Expression Best Practices

This section outlines the right way to write test expressions, reflecting the
limitations of Contract4J5 and Jexl. For detailed examples, see the code in 
the test suite.

** Default Test Expressions

If a contract annotation is used with no test expression, a default expression
will be inferred with possible. For cases where no test can be inferred, it
is considered an error, but this can be overridden with an API call:
  ExpressionInterpreter.setTreatEmptyTestExpressionAsValidTest(boolean);

If the test is on an element with no superclass equivalent, the following
default rules apply:

@Pre   All arguments are expected to be non-null, which means there is no
       meaningful default test for primitive arguments
@Post  The return value is expected to be non-null, unless the method
       returns void.
@Invar There is no default expression except for field invariants, where the
       field is expected to be non-null.

For elements with superclass equivalents, the following rules apply.
First, recall that preconditions and postconditions can only be used on
methods and constructors. Also, because Java5 method annotations are never
inherited, you MUST annotate any method with the same annotations found on
its parent. However, the test expressions can be empty and if so, the 
corresponding test defined in the parent element will be used. Note that if
you don't put the annotations on derived class overrides, if they call the
parent methods, the parent methods will be tested, but not anything the
override does (including constructors).

API calls exist in the org.contract4j5.aspects.*.aj aspects to specify
customized objects for calculating a default expression at runtime. These 
objects must implement
  org.contract4j5.testexpression.DefaultTestExpressionMaker.

** Inheritance Rules for Annotation Test Expressions.

This was discussed in depth above, in the section titled "How Does Contract4J 
Support Design by Contract?".

** $this refers to the object being tested. You can call any public method on 
the object in the test expression; Jexl will resolve the type. Additionally,
if you refer to a bare field that is not public, Jexl will convert the 
expression to the corresponding "getter" call. In fact, the getter method is
*required* in order for Jexl to resolve the field!

** $target currently is used only to refer to the field in a field invariant
test. Future use may include any context associated with the "target()" 
pointcut expression. Just as for $this, you can reference any method or field 
defined for the object.

** $return is the value returned by a method. It is only valid in 
postcondition tests. As for $this, you can reference any method or field 
defined for the object.

** $args[] are the arguments passed to a method, indexed starting at zero. 
You can also use the declared argument name. However, if the name shadows an
instance field, the parser may confuse the two; use the appropriate $arg[n] in
this case. As for $this, you can reference any method or field defined for the
objects in the array.

** Use of the "$old(..)" Keyword

The "old" $old(..) keyword tells Contract4J5 to remember the value of the
contained expression before evaluating evaluating the join point, so that 
value can be compared to the "new" value after evaluating the join point.
It can only be used in @Invar and @Post condition tests and the saved value
is forgotten once the test completes.

The most important thing to remember about "$old(..)" is that Contract4J5 only
remembers the value, which may be a reference to a mutable object. Since
"clone()" is not guaranteed by Java to be publicly available on an object, we
can't clone it and it was deemed too "obscure" to only permit, for example
"$old($this)" on objects where clone is publicly available. Hence, you should
try to use the $old keyword only with primitives or references to immutable
like strings.

Here are the allowed expressions.
  $old($this)   
    Not recommended, since only the reference is saved. 
  $old($target) 
    Equal to $old($this.field). Be careful if "field" is a reference to a
    mutable object!
  $old($this.field) 
    Recommended usage, if "field" is primitive. A synonym for
    $old($target) when used in a field invariant test. 
  $old($this.method(x,y))  
    Method call where the returned value is saved. Due to current parser
    limitations, nested method calls are not supported. So, the following
    is okay:
      $old($this.getFoo().doIt(1))
    but this is not
      $old($this.getFoo().doIt(getIntI()))

** References to Instance Fields

For fields, Jexl will automatically convert a "bare" field reference to its
accessor, even if the field is private. Hence, an expression like
  $this.foo.bar.baz.doIt(1)
is allowed and will be translated to
  $this.getFoo().getBar().getBaz().doIt(1)
In fact, the getter method is *required* in order for Jexl to resolve the field!

Normally, you should prepend "$this." before a "bare" field reference as the
parser does not always correctly resolve the reference to an instance field.
The one case where "$this" is unnecessary is inside a field invariant test.
Using the field's "bare" name will work. As an alternative in field invariant 
tests, "$target" can be used to refer to the field.

Note the example previously where a field invariant test was written with the 
bare field called "name", but when a set of "conceptually similar" @Pre and 
@Post tests were written in an interface on "setName()" and "getName()" methods,
it was necessary to use "$this.name". Contract4J may not resolve the field
correctly in those cases.  

** Tests Defined on Interfaces

You can define tests on interfaces and their methods. In fact, you are urged
to do so. Unfortunately, for reasons discussed previously, you must include
the same annotations, although not their test expressions, on the declarations
of the method implementations in implementing classes. Contract4J5 will find
the test expressions in the interfaces.

Unfortunately, you can't define contract tests for constructors, since they
don't exist in an interface. You may be able to work around this using class
invariants and tests on instance methods. Note that you can also implicitly 
define field invariant tests, either on declared accessor methods or as 
invariants on the class itself. You can refer to the (implied) bare field in
the test, as long as you declare an appropriate accessor method for it.

** Miscellaneous Notes and Debugging Tips

*** All test expressions must evaluate to a boolean value. 

*** Test expressions that fail to be evaluated by Jexl will be treated as test
failures, on the grounds that they expression is buggy in this case! Note that
if an annotation is empty, that is it doesn't define a test expression, then it
it considered an error if a default expression can't be inferred and no 
corresponding test exists on a parent class. However, there is an API call to
allow empty tests (see 
ExpressionInterpreter.setTreatEmptyTestExpressionAsValidTest(boolean) in the
org.contract4j5.interpreter package).

When a test fails due to a test error, a subclass of ContractErrror is thrown,
TestSpecificationError (new in v0.6.0) Nonetheless, it can be confusing when a
test that looks valid fails for this reason; in particular, see the next bullet
item.

*** Remember that if any test accesses an instance field, the field 
*must* have a corresponding JavaBeans getter method. Otherwise, Jexl will
fail to resolve the field and the test will fail. This can be confusing since
currently Contract4J does not distinguish between test "bugs" and actual
test failures.

*** Avoid expressions with side effects. Since tests will usually be turned 
off in production, test expressions with side effects, e.g., assignments, will
not be evaluated, thereby changing the logical behavior of the application.

*** Because runtime expression evaluation is very slow compared to compiled
code, consider embedding non-trivial tests in "validation" methods and calling
them from the test expression. (Prepend instance tests with "$this.")

*** White space follows the same rules for Java. For the "$" keywords, white
space is not allowed between the '$' and the word.

*** Jexl can't parse literal floats and doubles with the 'f' and 'd' appended,
respectively. Leave them off in both cases.

*** Most other Java expressions, like comparisons and arithmetic expressions can
be used. See the Jexl website for more information on allowed expressions,
  http://jakarta.apache.org/commons/Jexl/.

*** Before passing the expressions to Jexl, substitutions are made. Normally,
you shouldn't case, but when debugging, you may see strings with these 
substitutions. All the '$' keywords are changed. For example, 
	$this         -> c4jThis
	$target       -> c4jTarget
	$old($this)   -> c4jOldThis
	$old($target) -> c4jOldTarget
etc.

*** Turn on DEBUG logging to see what expressions are being evaluated and
some of the substitutions that are made.

*** Some common expression errors have "canned" strings defined for them in
  org.contract4j5.interpreter.ExpressionInterpreter
Most of the heavy lifting of expression evaluation BEFORE sending it to Jexl 
is done in
  org.contract4j5.interpreter.ExpressionInterpreterHelper
The wrapper for Jexl itself is 
  org.contract4j5.interpreter.Jexl.JexlExpressionInterpreter

----- Configuring the Behavior

The v0.6.0 release greatly expanded the options for configuring Contract4J5.
In addition to the previous support for configuration through API calls, you
can now customize most behavior using property files and also using Spring 
dependency injection. Besides setting properties such as which types of tests
to enable, whether or not to print the stack trace when contracts fail, etc.,
you can also specify your own classes to satisfy particular dependencies (e.g.,
the expression interpreter). 

We recommend using a standard mechanism like Spring's Dependency 
Injection (DI) for nontrivial configuration. (See the Spring example that is
part of this release.) However, for simple property configuration, property 
files are fine; see the unit test PropertiesConfiguratorTest.java in the
org.contract4j5.configurator.test package for examples.

Note that Contract4J5 will use reasonable defaults if you don't do any 
configuration.

Here are some API examples. Note that the wiring API changed significantly for
the v0.6.0 release. 

  import org.contract4j5.aspects.Contract4J;
  ...
  Contract4J.getInstance().setEnabled(Contract4J.TestType.Pre,   true);
  Contract4J.getInstance().setEnabled(Contract4J.TestType.Post,  true);
  Contract4J.getInstance().setEnabled(Contract4J.TestType.Invar, true);

This example turns on all three test types. 

NOTE: To completely disable contract checking, build the application without
"contract4j5.jar" in your path. This is recommended for "production" builds 
when you don't want any test overhead.

There are several components in Contract4J5. The main ones are:

  ExpressionInterpreter	- wraps Jexl; could wrap alternatives!
  ContractEnforcer     	- handles test invocation and failure handling
  Reporter              - simple output/logging wrapper.

Note that a runtime warning are issued if the ExpressionInterpreter or 
ContractEnforcer are not defined, as tests can't be run otherwise. The Reporter
objects will default to stdout/stderr if undefined.

Here are more details about these component interfaces and implementing
classes:

  org.contract4j5.enforcer.ContractEnforcer	-
  	The enforcer interface
  org.contract4j5.enforcer.ContractEnforcerImpl	-
  	The one implementation used here. It runs the tests and on failure, logs
  	a detailed error message and terminates program execution.
  org.contract4j5.interpreter.ExpressionInterpreter	-
    The expression interpreter interface
  org.contract4j5.interpreter.ExpressionInterpreterHelper	-
    An abstract helper class that provides a partial implementation.
  org.contract4j5.interpreter.Jexl.JexlExpressionInterpreter	-
    The Jexl implementation of the interpreter. A different interpreter,
    e.g., Groovy or Jython could be supported by subclassing the helper class.
  org.contract4j5.util.reporter.Reporter;
  	The "reporter" interface that is a thin veneer for a logging abstraction.
  org.contract4j5.util.reporter.Severity;
    Defines logging levels of severity, like INFO, WARN, ERROR, etc.
  org.contract4j5.util.reporter.WriterReporter
    "Logs" to stdout and stderr by default, but also supports file output. 
    It would be very easy to implement a "log4j reporter", for example.


Notes: 

1) For all properties currently defined, if a value is empty, it is ignored!

2) In pre-v0.6.0 releases, the fields in the aspects were static, e.g.,
  ConstructorBoundaryConditions.getDefaultPreTestExpressionMaker()
Now, you get the "aspectOf()" instance first:
  ConstructorBoundaryConditions.aspectOf().getDefaultPreTestExpressionMaker()

3) The type "Contract4J" was an aspect in pre-v0.6.0 releases. It is now a class
and a singleton instance is used. Hence,
   Contract4J.setEnabled(Contract4J.TestType.Pre, true);
is now
   Contract4J.getInstance().setEnabled(Contract4J.TestType.Pre, true);


--- Invocation and Configuration of Contract4J5

To build Contract4J5 the following third-party tools are required, along with 
corresponding "HOME" environment variable definitions needed by the ant build 
scripts:

1) JUnit 3.8.1 (JUNIT_HOME)
2) Java 5 (JAVA_HOME)
3) AspectJ 1.5 (ASPECTJ_HOME) Make sure you have the "final" 1.5 release.
3) Ant 1.6.X (ANT_HOME)
4) Jexl 1.0 (Jexl_HOME)
5) Commons Logging (COMMONS_LOGGING_HOME)
6) Spring (SPRING_HOME) - optional

Also define "CONTRACT4J5_HOME" to be the ".../contract4j5_050/contract4j5"
directory where you installed it.

For your convenience, you can use the build driver script "build.sh" or
"build.bat". Edit the values of the environment variables in the corresponding
scripts "env.sh" or "env.bat" for your environment.

Only Java, AspectJ, and Jexl are required if you simply use the binary 
"contract4j5.jar" in the distribution. Make sure your build process either 
compiles with AspectJ or weaves your precompiled jars or class files with 
"contract4j5.jar". Follow the instructions provided with AspectJ (or AJDT if 
you use eclipse) for doing this. 

Follow the example of the build.xml file (and the ant/*.xml files it includes) 
to incorporate the Contract4J5 jars into your build.

** TODO Items:

Here is a brief list of some of the more important "todo" items, roughly in 
order of importance. 

1) Automatically enforce proper usage of method/constructor annotations in
subclasses when the corresponding methods are annotated in the parent classes
or interfaces. Or, automatically evaluate the tests for method overrides even
they aren't annotated.
2) Implement more exhaustive tests. While Contract4J comes with a significant
JUnit-based test suite, it requires more test cases to ensure expected behavior
in a wide variety of possible usage scenarios.
3) Find a more elegant, aspectj-specific way to handle logging of information
without embedding the ad hoc Reporter objects.
4) Refine test handling. For example, field invariants are evaluated for reads,
but this probably only makes sense for the very first read, in case it is 
uninitialized. The rest of the time, evaluating just field writes makes sense.
5) Eliminate the requirement to annotate the class with @Contract.
6) For the keywords:
a) Allow "this" without the '$'? 
b) Remove "$target" if it won't be used for anything other than fields, for 
which it is redundant.
7) Allow contract tests on static methods.


** Notes for Each Release

*** v0.6.0 October 1, 2006

- Major refactoring to improve the internal structure, e.g., to reduce the 
over-reliance on singletons and to improve the "wiring" options. Defining 
contracts is unchanged (except for some bug fixes), but the configuration API 
has changed (this is still pre-1.0 software).

- Added an example using the Spring Framework v1.2.5 to configure Contract4J
(see http://www.springframework.org). See the separate "src" folder called
"Contract4J5WithSpring". Spring v2.0 configuration should be backwards 
compatable, if not easier with new 2.0 features. However, using Contract4J 
with Spring v2.0 was not tested.

- Greatly improved the options for using properties files to "wire" Contract4J, 
as an alternative to using Spring. See the tests in 
"org.contract4j5.configurator.test" for examples.
 
- If a test fails because the expression is empty or can't be evaluated by Jexl,
a subclass of ContractError, TestSpecificationError, is now thrown. This makes 
it easier to determine when a test failed because the test itself was bad, as
opposed to the class under test failing to meet the contract. Using a subclass
of ContractError means that any existing "catch (ContractError ce)" code will
continue to catch both kinds of failures. However, users who want to distinguish
between the two types of errors should use this idiom:
  try {
    ...
  } catch (TestSpecificationError tse) {
    ...
  } catch (ContractError ce) {
    ...
  }
  
- The build process now uses  binary (jar) weaving when the "tests" are built,
providing an example of using Contract4J with this approach. Preliminary 
support for a load-time weaving (LTW) example is also provided. However, it
is not yet complete, pending an AspectJ bug fix (see above).

"Binary" weaving is weaving done after compiling all code, using a 
post-compilation weaving step. It is useful for organizations that prefer to use
javac for all java files. The unit tests are built using binary weaving, as 
discussed earlier in this README.

Load-time weaving is done as the application loads class files, using a special
"java agent" for this purpose. See the section "Invocation and Configuration of 
Contract4J5" for more details.

Previous releases just used compile-time weaving, where ajc was used to compile
all sources and weave the class files. This approach is still used to build
Contract4J's non-test code and to create the contract4j5.jar file.

- Replaced the entity definitions in the build-related XML files with the ant
<import> task. Apparently, NetBeans doesn't like the entity definitions. (Thanks
to Matthew Harrison for bringing this to my attention and for providing 
refactored build files.)

- Added more tests that explicitly demonstrate that contract expressions that
access instance properties only work if the properties have JavaBeans getter
methods. This is an unfortunate Jexl limitation.

- Fixed some bugs, including a few warnings related to generics. (Thanks to 
Falk Bruegmann for a generics warning fix.)

Some of the API Changes:

- Removed the API calls to set separate "Reporter" objects (for logging) in
each major component. This greatly reduced some boilerplate code with a small 
reduction in flexibility which had dubious value. Now, a global Reporter is
used, the one set in the "Contract4J" class.

- All properties on aspects are no longer static methods. Use the "aspectOf()"
method to get the instance. So for example, 
  ConstructorBoundaryConditions.getDefaultPreTestExpressionMaker();
is now
  ConstructorBoundaryConditions.aspectOf().getDefaultPreTestExpressionMaker();

- "Contract4J" is no longer an aspect, but a class. The aspect code was moved
to a separate aspect called "AbstractConditions", leaving only pure-Java code.
The conversion to Java made it easier to instant these objects as needed and
also to exploit the more complete support for Java in Eclipse (e.g., for 
refactorings). Note that this change means that any properties that were
previously set on "Contract4J" are not changed to use "aspectOf", as just 
described for the aspects. Instead, use for example,
  Contract4J.getInstance().setEnabled(Contract4J.TestType.Pre, true);

*** v0.5.0 February 20, 2006

Eliminated the precompilation step, replacing it with runtime teste expression
evaluation using the Jakarta Commons Jexl expression parser.

The package structure has been changed from com.aspectprogramming.* to
	org.contract4j5.*.
	
Deprecated several features; they aren't supported in this milestone release.

1) The ad hoc configuration API and full support for configuration through 
property files. Subsequent releases will "re-add" limited support for property 
file configuration, for convenience, but the preferred way to configure
Contract4J5 is through a standard dependency injection (DI) solution like 
the Spring Framework. This release does all you to globally enable or disable 
all tests or just all precondition, postcondition, or invariant tests. To use,
define one or more of the following System properties (true or false):

	"org.contract4j5.Contract"	Enable/disable all tests
	"org.contract4j5.Pre"		Enable/disable all precondition tests
	"org.contract4j5.Post"		Enable/disable all postcondition tests
	"org.contract4j5.Invar"		Enable/disable all invariant tests

2) The "alwaysActive" property of the V0.1 annotations, which kept a test 
"active" even if all other annotations of the same kind were disabled globally.
The complexity of implementing this feature in the new architecture outweighed 
the benefits. Use an alternative implementation like embedded assert statements.

3) Similarly, the "messagePrefix" and "messageSuffix" attributes of the 
@Contract annotation are no longer supported. However, you can still define
individual messages in the test annotations.

3) Annotations on method parameters. This is a current AspectJ5 limitation; 
it doesn't support annotations on method parameters, which were supported in
Contract4J v0.1. The workaround is to put all parameter tests in a method 
precondition test.

*** v0.1.1.0 October 4, 2005

Support for Ant builds and numerous small bug fixes in the V1 branch.

*** v0.1.0.2 April 24, 2005

Fixed bug that prevented use of precondition annotations on individual method 
parameters if the method contains more than one parameter. (The generated 
aspectj code pointcut uses the "args()" specifier. For it to match correctly, 
args() must contain the correct parameter list for the method, with the 
parameter name used for the parameter of interest and the parameter types used 
for the other parameters.)

Numerous minor enhancements.

*** v1.0.1 February 6, 2005

Minor bug fixes.


** For Further Information...

http://www.contract4j.org/ is the home page for Contract4J5 and Contract4JBeans.
It is hosted by Aspect Research Associates (ARA)
(http://www.aspectresearchassociates.com/), a consulting company specializing
in Aspect-Oriented Programming, enterprise Java, and Ruby on Rails. ARA also
manages the Aspect Programming web site, http://www.aspectprogramming.com/. 
There you will find more information and whitepapers on Contract4J and 
Aspect-Oriented Software Development (AOSD), in general. 

The AOP@Work series
(http://www.ibm.com/developerworks/views/java/libraryview.jsp?search_by=aop@work)
developerWorks.com (http://www.developerWorks.com) contains an article about 
Contract4J, http://www-128.ibm.com/developerworks/java/library/j-aopwork17.html.
It introduces "Design by Contract" and how Contract4J supports it in Java. The 
article concludes with a discussion of emerging trends in Aspect-Oriented 
Design.

The recent AOSD.06 Conference in Bonn, Germany (March 19-24, 2006) featured
a talk in the Industry Track on Contract4J, specifically on the lessoned about
writing generic, reusable aspects in AspectJ while implementing Contract4J. 
There was also a brief paper on aspect-oriented design patterns in
Contract4J in the ACP4IS workshop. See http://www.aosd.net/2006/ for more
information and also check aspectprogramming.com/papers for information 
about getting these papers. 

The definitive site on AOSD is http://www.aosd.net.

See http://www.aspectj.org for information on AspectJ. Note that there are 
plans to incorporate Contract4J into the new standard library for AspectJ5 that 
is under development.

For alternative approaches to doing Design by Contract in Java, see the
"Barter" project, which uses XDoclet and also generates AspectJ. Barter 
partially inspired Contract4J. http://barter.sourceforge.net/. 

JBoss AOP has basic support for contracts. Spring AOP may have similar support.

There is a discussion group doing DbC in Java and possibly getting a future
version of Java to support DbC natively. See http://dbc.dev.java.net/. However,
this effort appears to be dead for the time being.

Some more sophisticated approaches to program correctness include the J-LO
tool for runtime checks of temporal assertions about the program.
http://www-i2.informatik.rwth-aachen.de/Research/RV/JLO/

Another project is the Java Modeling Language (JML), which supports DbC
for Java. http://www.cs.iastate.edu/~leavens/JML/
