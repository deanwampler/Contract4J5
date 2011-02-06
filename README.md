# README for Contract4J5: V0.9.0.0#

**Design-by-Contract&#174; for Java Using Java 5 Annotations**

Dean Wampler<br/>
[polyglotprogramming.com/contract4j](http://polyglotprogramming.com/contract4j)<br/>
A project of [Aspect Research Associates](http://aspectresearchassociates.com)

## Contents

* <a href='#copyright'>Copyright</a>
* <a href='#wheretogetc4j'>Where to Get Contract4J5</a>
* <a href='#versioning'>Naming and Versioning</a>
* <a href='#manifest'>Distribution Manifest</a>
* <a href='#whatisc4j'>What Is "Contract4J5"?</a>
  * <a href='#whatisdbc'>What is Design by Contract?</a>
  * <a href='#dbcandaop'>Design by Contract and Aspect-Oriented Programming</a>
  * <a href='#how'>How Does Contract4J5 Support Design by Contract?</a>
  * <a href='#inherit'>Inheritance Behavior of Contracts</a>
  * <a href='#usage'>How Do I Use Contract4J5?</a>
  * <a href='#distribution'>Distribution</a>
  * <a href='#building'>Building Contract4J5</a>
  * <a href='#showme'>Show Me the Code!</a>
  * <a href='#details'>Details of Contract Specifications</a>
  * <a href='#limits'>Known Limitations</a>
  * <a href='#tips'>Miscellaneous Notes and Debugging Tips</a>
  * <a href='#config'>Configuration of Contract4J5</a>
* <a href='#todo'>TODO Items</a>
* <a href='#history'>History</a>
* <a href='#notes'>Notes for Each Release</a>
* <a href='#furtherinfo'>For Further Information...</a>

**Impatient?** If you want to get started quickly, go to
  <a href='#showme'>Show Me the Code!</a>.

<a name="copyright"></a>

## Copyright

Contract4J is open source software covered by the Eclipse Public License - v1.0. A complete copy of the license is shown below and it can be found in the LICENSE-ECLIPSE.txt file in the top-level directory of the distribution.

    ===========================================================
    Copyright 2005-2011 Contract4J Development Team

       Licensed under the Eclipse Public License - v 1.0; you
       may not use this software except in compliance with the 
       License. You may obtain a copy of the License a

           http://www.eclipse.org/legal/epl-v10.html

       A copy is also included with this distribution. See the
       "LICENSE" file. Unless required by applicable law or 
       agreed to in writing, software distributed under the 
       License is distributed on an "AS IS" BASIS, WITHOUT 
       WARRANTIES OR CONDITIONS OF ANY KIND, either express or
       implied. See the License for the specific language 
       governing permissions and limitations under the License.
    ==========================================================

In addition, several third-party components are used by Contract4J5. Their licenses are described by the LICENSE.txt file.
<a name='wheretogetc4j'></a>

## Where to Get Contract4J5

Contract4J5 is hosted on [GitHub](https://github.com/deanwampler/Contract4J5) (note that the project is called *Contract4J5*). Builds can be downloaded from [SourceForge](https://sourceforge.net/project/showfiles.php?group_id=130191). The home page is [polyglotprogramming.com/contract4j](http://polyglotprogramming.com/contract4j).

**NOTE:** The previous web site for Contract4J5 was **contract4j.org**.

<a name='versioning'></a>

## Naming and Versioning

The active development project is **Contract4J5**, which uses Java 5 annotations to define the contracts. There is a separate, dormant project called **Contract4JBeans**, which was an interesting, but not very successful experiment that required no Java 5 annotations, but relied on method naming conventions to define contracts for target methods and classes. The JavaBeans-like naming conventions were the origin of the Contract4JBeans name. Contract4JBeans can be found only as a jar distribution on the [SourceForge](https://sourceforge.net/project/showfiles.php?group_id=130191) site.

The newest release of Contract4J5 described in these notes is version 0.9.0. As this is a "pre-1.0" release, it is possible that planned features and other changes may force API changes that break backwards-compatibility. However, at this point, we anticipate no such problems going forward (except where documented in the TODO Items).

<a name='manifest'></a>

## Distribution Manifest

Contract4J5 comes in *.zip and *.tar.gz formats. For convenience, each archive includes the required Jexl, Groovy, JRuby and dependent libraries. If you are using only one of the languages, a table below lists which of the jars is required for each language choice. (The Spring framework is not included. It is required to build the optional Spring-configuration example.)

After expanding the archive you will have the following directory structure:
<table>
  <tr><th>File or Directory</th><th>Description</th></tr>
  <tr><td>contract4j5_XYZ.jar</td><td>Pre-built Contract4J5 jar.</td></tr>
  <tr><td>lib</td><td>Location of Jexl, Groovy, JRuby and dependent libraries.</td></tr>
  <tr><td>contract45</td><td>Directory with the main distribution files</td></tr>
  <tr><td>contract4j5WithSpring</td><td>Directory with an example of configuring Contract4J5 using Spring 1.2.X or Spring 2.0. The example works with either release (it uses the 1.2.X API, which is supported by 2.0). We recommend using Spring for customizations. However, without any configuration, Contract4J5 works with suitable defaults. There is also a property configuration option, demonstrated in the JUnit tests and discussed in more detail below.</td></tr>
</table>

Inside the contract4j5 directory, you will find:
<table>
  <tr><td>ant</td><td>Directory of ant support files.</td></tr>
  <tr><td>build.bat</td><td>Window's build driver script.</td></tr>
  <tr><td>build.sh</td><td>Linux/MacOSX/Cygwin build driver script.</td></tr>
  <tr><td>build.xml</td><td>Ant build script.</td></tr>
  <tr><td>Contract4J.properties.example</td><td>Example properties file for configuring Contract4J5 using properties. Note that we recommend using Spring for this purpose.</td></tr>
  <tr><td>doc</td><td>Created during a full build; where JavaDocs go.</td></tr>
  <tr><td>env.bat</td><td>Windows environment variable setup for running the build. Invoked by build.bat. Edit this file for your environment.</td></tr>
  <tr><td>env.sh</td><td>Linux, MacOSX, Cygwin environment variable setup for running the build. Invoked by build.sh. Edit this file for your environment.</td></tr>
  <tr><td>jdepend.sh</td><td>*nix driver script for jdepend. Uses XSLT and Graphviz to generate a PNG file with the results. See the script for more information.</td></tr>
  <tr><td>jdepend.bak</td><td>Created when you run the jdepend.sh script; results from previous runs are stored here.</td></tr>
  <tr><td>jdepend.properties</td><td>Configure jdepend.</td></tr>
  <tr><td>jdepend_report.png</td><td>Example output generated by jdepend.sh.</td></tr>
  <tr><td>LICENSE.txt</td><td>Contract4J license file (Eclipse Public License 1.0). Other license files for included components may also appear in this directory.</td></tr>
  <tr><td>README.html</td><td>This README.</td></tr>
  <tr><td>src</td><td>Directory with all the non-test source files.</td></tr>
  <tr><td>src-classes</td><td>Created during a build; where the src classes go.</td></tr>
  <tr><td>test</td><td>Directory with all of Contract4J5's JUnit test sources.</td></tr>
  <tr><td>test-classes</td><td>Created during a build; where the test classes go.</td></tr>
  <tr><td>lib</td><td>3rd-party libraries required by Contract4J5.</td></tr>
</table>

<a name='whatisc4j'></a>

## What Is "Contract4J5"?

Contract4J5 supports "Design by Contract"&#174; programming in Java. a programming practice introduced by Bertrand Meyer and incorporated in the *Eiffel* programming language in the 1980's.

The Contract4J5 project is sponsored by [Aspect Research Associates](http://aspectresearchassociates.com), a consulting firm specializing in Scala, Data Analytics, Aspect-Oriented Programming, Enterprise Java, and Ruby on Rails.

<a name='whatisdbc'></a>
  
### What is Design by Contract?

*Design by Contract*&#174; (DbC) starts with the observation that, implicitly or explicitly, a component defines a "contract" with its clients. When a client invokes an operation on the component, it must agree to provide the component with appropriate inputs and context. Otherwise, the component can't perform its services. In return, if the input constraints are satisfied the component guarantees delivery of prescribed results.

DbC encourages the component developer to state the contract explicitly, by specifying the input constraints, known as "preconditions", and guaranteed results, known as "postconditions", in a programmatic form that can be tested at runtime. In addition, state "invariants" may be defined.

DbC is a powerful and underused tool for detecting bugs during development and testing. A key principle is that if a test fails during execution, the program terminates abruptly. While this may seem draconian, it forces the developer to solve the problem immediately, rather than allow problems to "slide", multiple, and thereby undermine the quality of the software.

Hence, during development, all tests are enabled and the code is thoroughly tested. During deployment, the tests are often disabled, both to prevent sudden shutdown and and to allow possible recovery should a contract-violating condition arise that was never detected during development. Turning off the tests also removes their overhead.

As such, DbC is a wonderful complement to Test-Driven Development, which exercises the code and hence the contract tests, thereby increasing the probability they will detect bugs. DbC tends to emphasize the fine-grained design a little more than test-driven development by itself. Designing the unit tests and specifying the contracts also force the developer to think through the details of the design before writing the code. A third technique that supports thinking through the design is to write the comment blocks for classes and methods before implementing them.

For more information on Design by Contract, see the references below.

<a name='dbcandaop'></a>

### Design by Contract and Aspect-Oriented Programming

So what does DbC have to do with [Aspect-Oriented Software Development](http://www.aspectprogramming.com/home/aosd) (Aspect-Oriented Programming - AOP - for short)? On the one hand, the component's contract is an essential part of the complete, logical component specification that clients must support. For example, an interface for a bank account may have a contract requirement that all methods that return a balance must always return a non-negative number (ignoring overdraft features). However, in practical terms, contracts often include implementation concerns that may have little relationship to the domain logic of the application. For example, the code implementing the bank account may prohibit passing null values as method parameters.

For both types of contract details, AOP allows us to specify the details with sufficient "proximity" to the interface so that clients can see the constraints and AOP gives us an elegant way of testing the constraints at runtime without cluttering the code with logic to run the tests and handle failures.

More generally, AOP is a new approach to modularizing "concerns" that need to be handled by a component, but which tend to obscure the main logic of the component, often compromising clarity, maintainability, reusability, etc. For example, modern web and enterprise applications typically must support secure access, transactional behavior, persistence of data, and mundane support issues like logging. Without AOP, the code for these "concerns" gets mixed in with the domain logic, thereby cluttering the code and diminishing the "ilities" we all strive for. AOP keeps these concerns in separate modules and provides powerful facilities for "injecting" the concern behavior in the specific execution points where needed. Contract4J5 uses AOP techniques to find the contract specifications and test them at runtime at the appropriate execution points.

AOP is a good approach to supporting DbC because it permits DbC concerns to be managed in a modular and minimally-intrusive way, without cluttering application logic, while still allowing the contracts to be integrated into the runtime environment for development and testing. Contract4J5 uses the best-known AOP language, AspectJ, to support DbC for Java.

For more information on AOP, see the references below.

<a name='how'></a>

### How Does Contract4J5 Support Design by Contract?

I'm a long-time believer in DbC and wanted to use it in Java. A few years ago, I discovered the clever Barter project, which supports DbC in Java using XDoclet tags and AspectJ code generation to perform the tests as "advice".

Two problems with doclet-based approaches are that they are buried in the comments, which means they are decoupled from the runtime environment, and a preprocessor step is required. Java 5 introduced annotations, which are like "JavaDoc tags for code". In particular, annotations are used to ascribe meta-information to the code and to make that information available to the runtime environment, when desired. Annotations are a logical tool for associating contract tests with code and Contract4J5 uses them for this purpose.

The specifications are written as executable Java expressions, enclosed in the String "value()" attribute of the annotation. The details are discussed below. A contrived example suffices for now:

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

The `@Contract` annotation tells Contract4J5 that `MyClass` defines tests. The tests are defined using `@Pre`, `@Post`, and `@Invar` annotations, for *precondition*, *postcondition*, and *invariant* tests respectively. In this example, the tests are:

The field `name` has an invariant test that it can never be `null` (after the object has been constructed...).

The `setName` method has a precondition that the value of the parameter cannot be `null`.

The `getName` method has a postcondition that it can never return `null`.

The `doIt` method has both a pre- and a postcondition test.

If a test fails, Contract4J5 throws an unchecked exception, `ContractError`, after printing some diagnostic information (see the unit tests for examples). A special subclass of `ContractError` is thrown if the test itself can't be evaluated for some reason (discussed in more detail below). That error is `TestSpecificationError`. So, clients who want to catch contract errors and also distinguish between these two types of failures should follow this idiom:

    try {
      ...
    } catch (TestSpecificationError tse) {
      ...
    } catch (ContractError ce) {
      ...
    }

The versions of Contract4J5 before V0.7 embedded the Jakarta **Jexl** interpreter, an expression evaluator, to evaluate the test expressions in the annotations. However, there are quirks and limitations of Jexl (discussed throughout this README). In V0.7, Contract4J5 added support for **Groovy** and **JRuby**, as well as Jexl, as scripting engine options. The tests exercise all three options.

In V0.8, we have **deprecated** JRuby and we may deprecate **Jexl** before the final release. As we expanded the test suite to better exercise test expressions for generic classes and test expressions that use other objects and statics in other classes, it became clear that maintaining three engines is not cost-effective, as each has its quirks.

Therefore, we have standardized on **Groovy** as the best compromise between performance and full support for Java 5 features. However, Jexl offers the best performance (about 2 times faster than Groovy, when running the test suite), so we may continue supporting it, even though Jexl doesn't appear to be as full-featured as Groovy (e.g., tests for generic classes don't seem to work).

The V0.9 release does continue support for JRuby, but removal is planned before the 1.0 release. While there is great interest in JRuby and Java interoperability, JRuby is not an ideal choice for Contract4J, because it's more useful for the syntax of the scripts to be as close to Java syntax as is "reasonable". 

Here are some performance numbers, from the V0.7 release, for the different scripting engines. (The numbers are a little bigger for V0.8+, due to the increase in the number of tests in subsequent release.) The numbers compare the performance of load-time weaving (LTW) vs. binary weaving and also the performance of the JVMs in JDK 5 vs. JDK 6.

<table>
  <tr><td colspan="6">JDK 5 (sec.)</td><td colspan="6">JDK 6 (sec.)</td></tr>
  <tr><td colspan="2">Jexl</td><td colspan="2">Goovy</td><td colspan="2">JRuby</td><td colspan="2">Jexl</td><td colspan="2">Goovy</td><td colspan="2">JRuby</td></tr>
  <tr><td>Binary</td><td>LTW</td><td>Binary</td><td>LTW</td><td>Binary</td><td>LTW</td><td>Binary</td><td>LTW</td><td>Binary</td><td>LTW</td><td>Binary</td><td>LTW</td></tr>
  <tr><td>21.7</td><td>98.6</td><td>54.9</td><td>324.4</td><td>79.6</td><td>189.4</td><td>17.7</td><td>62.9</td><td>44.8</td><td>133.5</td><td>63.2</td><td>108.4</td></tr>
</table>

These times are approximate "user times", averaged over a few runs, measured using the "time" shell command on a ThinkPad T42 running Ubuntu Linux. There are slightly different build activities and I/O overhead involved in the LTW numbers, adding a few percentage points to the numbers. The tests do some different manipulations depending on which interpreter is used. Hence, the results are rough, at best. Most likely, the numbers reflect the relative amounts of overhead to load the interpreters and to set up the "environments" for evaluating the scripts. Your mileage may vary...

Note that the JDK 6 performance is significantly better. The code was compiled with the JDK 5 javac and executed using the JDK 6 JVM. Ironically, we saw slightly slower performance when we compiled with JDK 6 (not shown in the table).

The Jexl test runs are roughly twice as fast as the Groovy and JRuby runs, probably because Jexl is a more "minimalist" environment, incurring less startup and execution overhead.

While not shown, we observed that the memory requirements for Groovy and JRuby are also higher. In fact, we had to increase the maximum heap size for the LTW JUnit run. We also increased the heap size for the binary JUnit runs in Eclipse, when using JRuby.

The LTW test runs take significantly longer, but the results are somewhat hard to interpret. Under JDK 5, the Jexl and Groovy runs are five to six times longer, while the JRuby runs are only about 2.5 times longer. Under JDK 6, the Jexl and Groovy runs are a little more than three times longer, while the JRuby run is under twice as long. It is not clear while JRuby and LTW performs so much better under both JDKs.

In general, the longer LTW times occur because a weaving step happens at the beginning of each TestCase, as the class is loaded. The JDK 6 speed-up is a good argument for running this VM, at least for your tests!

LTW is the most convenient way to adopt Contract4J5 (as well as other aspects, in general), but if the startup time is too long for your needs, then binary weaving of compiled code provides better performance with only a modest change in the build process.

You can select which interpreter to use at runtime by passing one of the following flags to the JVM:

    -Dinterpreter=groovy (the default)
    -Dinterpreter=groovybsf (Groovy using BSF - see below)
    -Dinterpreter=jexl
    -Dinterpreter=jexlbsf (Jexl using BSF - see below)
    -Dinterpreter=jruby

If you use the Contract4J `PropertiesConfigurator`, you can use the following property:

    org.contract4j5.ExpressionInterpreter=org.contract4j5.interpreter.jexl.JexlExpressionInterpreter

(Jexl example). For Spring, the tests for the include Spring example `applicationContext-contract4j5.xml` demonstrates setting Groovy as the interpreter. There are more details on language configuration below.

For more information on the differences between the different scripting languages, see the V0.7.0 release notes.

<a name='inherit'></a>

### Inheritance Behavior of Contracts

In DbC, there are rules for proper behavior of inherited contracts, based on the *Lyskov Substitution Principle* (LSP), which is a minimal definition of inheritance. Class B is considered a child class of class A, if objects of type B caN be substituted for objects of type A without breaking the program. In DbC terms, this means that class B must obey A's contract, including all the class, method, and field tests.

However, there is one nuance affecting derived preconditions and postconditions. A derived (overriding) precondition test can actually satisfy a "looser" restriction than the overridden test. Put another way, the set of valid inputs can be larger than the set for the parent (overridden) test. This is because precondition tests are tests the client must meet, so if a client already meets a strict test defined by the overridden test, then it will also satisfy a looser derived test transparently. This is "contravariant" behavior because even though subclassing is a form of increasing specialization and restriction, the restrictions imposed by the precondition test can actually grow looser.

In contrast, postcondition tests are "covariant", meaning they must be as narrow or narrower than the tests they override. This is true because postcondition tests are tests on the results the component promises to deliver, as opposed to tests on clients. So, if the client is expecting a result in a set of possible results and a derived test narrows the set further, then the result will still satisfy the client's expectations.

Contract4J5 provides only minimal support for contravariant precondition tests and covariant postcondition tests. First, because Java 5 annotations on methods are NOT inherited, it is a requirement for writers of subclass method overrides to also include the annotations on the parent method. However, the annotations do not have to reproduce the test expressions. C4J5 will locate the corresponding parent class test expressions automatically. In contrast, class-level invariants are inherited, since class annotations can be inherited. (However, it is harmless to repeat those in subclasses, too.)

C4J5 attempts to enforce the rule that invariant tests can't change. However, it uses a simple string comparison, ignoring whitespace, so some logically equivalent expressions may get flagged incorrectly as different. For example:

    a == b   vs.   b == a

will appear to be different, when they are logically equivalent.

To properly write contravariant precondition tests and covariant postcondition tests, you will have to repeat the "inherited" test expression and "append" the appropriate refinements, e.g.,

    @Pre("new_test || parent_test")
    @Post("new_test && parent_test")

However, since the parent test is always valid for the derived method override, if you don't need to modify the test, then you can simply use the `@Pre` or `@Post` annotation without a test expression and C4J5 will find the parent expression.

We are considering ways to support the correct inheritance behavior before the 1.0 release of Contract4J5.

<a name='usage'></a>

### How Do I Use Contract4J5?

The distribution contains ant files and examples of how to use Contract4J5. The examples are actually part of the unit/acceptance test suite. If you are using Eclipse, the project configuration files are also in the source distribution. You may or may not want to remove them.

**NOTE:** The tests/examples are the best way to see how to write test expressions correctly!

Installation and Configuration:

For Linux/Unix systems, use these commands:

    1) cd ~/work		# or wherever...
    3) cp .../contract4j5_XYZ.tar.gz .
    4) tar xvzf contract4j5_XYZ.tar.gz 

On Windows systems, Unzip the zip file to an appropriate location.
You will need to have Java 5 or Java 6 and AspectJ 5 installed to use Contract4J5. You will need to select which scripting language you prefer for the annotation scripts used to define contracts. (See the notes above about plans to deprecate JRuby and possibly Jexl.) The distribution includes all the currently-supported options; Jakarta Commons Jexl 1.1, Groovy 1.0, and JRuby 1.0. (Several other jars, such as Commons Logging, are also included, which are required by the scripting engines).

If you build Contract4J5 yourself, you will also need JUnit 3.8.X.

**Note:** You must use AspectJ v1.5.3 or later if you want to use load-time weaving (LTW) with Contract4J5.

Once you have started adding contract tests to your code, there are three ways to enable Contract4J5's contract enforcement:

1. Compile our code with AspectJ's `ajc` compiler instead of `javac`. You will need to add the `contract4j5.jar` to your `CLASSPATH`.
2. Use a *binary weaving* step. After you compile your code with `javac`, use `ajc` to weave in the Contract4J5 aspects.
3. Use *load-time weaving* (LTW) to apply the Contract4J5 aspects at runtime.

All three approaches are demonstrated by Contract4J5's ant build process, as described below. We describe load-time weaving (LTW) here. It is the easiest and the least intrusive approach to adopt and we recommend you start with it. Recall, however, from the discussion above, that the performance of LTW is slower than "binary" weaving (discussed below), so you may wish to convert to that approach at a later time.

To use LTW, follow these steps.

1. Install AspectJ v1.5.3 or later.
2. Make sure the `ASPECTJ_HOME` environment variable is defined.
3. Add the `contract4j5.jar` to your `CLASSPATH` and your `ASPECTPATH`, if using Eclipse or `ajc` (i.e., `-aspectpath` ...).
4. Copy and adapt the `test/META-INF/aop.xml` file to your project.
5. Invoke your application (e.g., JUnit tests) using the `ASPECTJ_HOME/bin/aj5` (*nix) or `%ASPECTJ_HOME%\bin\aj5.bat` (Windows) script, instead of java.

Now the Contract4J5 aspects will be applied as your application's classes as they are loaded. For production deployments, simply use java to invoke your application, as before.

**Note:** To see how to invoke JUnit tests from ant with load-time weaving (LTW), see the `_junitTemplate.ltw` build target in `ant/targets.xml`. This example also provides more details on how to use LTW with Contract4J5.

If you are already using the AspectJ `ajc` compiler to compile your Java and AspectJ sources, then simply include `contract4j5.jar` in the `-aspectpath` argument to `ajc`.

Another alternative, whether you use `ajc` or `javac` to build your code, is to add a final *binary weaving* step to your build.

To see how to do a binary weaving step, consult the `ant` build files in the distribution, in particular, the `test` target and dependencies. The `compile.test` target uses `javac` to compile the JUnit test code. The `project-test.jar` target uses `ajc` to do binary weaving, where the compiled class files are read by `ajc`, aspects are woven into them (note the `-aspectpath` option to `ajc`) and the `contract4j5-test.jar` file is output. The `ant/targets.xml` defines the `binaryWeaveTemplate` target used for this process.

**Note:** To see what the `ajc` command does, invoke ant with the option `-Dbuild.compiler.verbose=true` which will cause `ajc` to print the command-line options used. Look for the output that is part of the `compile.test` target. This output may be easier to understand than trying to understand the ant files!

You can use the installed `contract4j5.jar` file as is. If you want to rebuild Contract4J5, use the ant driver script `build.sh` or `build.bat`. First, edit the corresponding `env.sh` or `env.bat` file and change the environment variable definitions as appropriate for your environment. Or, you can define the appropriate environment variables in your environment and use the `build.xml` ant script directly.

<a name='distribution'></a>

## Distribution

The distribution has the following structure:

<table>
  <tr><td>File/Directory</td><td>Description</td></tr>
  <tr><td>README.html</td><td>This file.</td></tr>
  <tr><td>LICENSE.txt</td><td>The Apache 2 license file for Contract4J5.</td></tr>
  <tr><td>build.sh</td><td>Unix/Linux build driver script. Sets "home" variables where the tools are found. Edit to taste.</td></tr>
  <tr><td>build.bat</td><td>Windows build driver script. Also sets "home" variables...</td></tr>
  <tr><td>build.xml</td><td>Ant build script.</td></tr>
  <tr><td>src</td><td>The source code tree.</td></tr>
  <tr><td>test</td><td>The JUnit unit/acceptance tests, which also function as usage examples. The files ending with "*Test.java" are JUnit tests. The other classes under "test" are example classes used by the tests, which also provide C4J5 usage examples. The JUnit test files often contain additional example classes that demonstrate usage and they contain comments about tests the demonstrate known idiosyncrasies or limitations of C4J5 and script evaluation using Jexl, Groovy, and JRuby.</td></tr>
  <tr><td>classes</td><td>Where build artifacts (except the jars) are stored.
  <tr><td>doc</td><td>Where Javadocs are written.</td></tr>
  <tr><td>contract4j5.jar</td><td>The runtime deployment jar. It contains the build products from "src".</td></tr>
  <tr><td>contract4j5-test.jar</td><td>The jar containing the build products from "test". Not part of the normal runtime deployment.</td></tr>
</table>

<a name='building'></a>

## Building Contract4J5

If you want to build Contract4J5:

    ./build.sh all  # *nix
    build.bat all   # windows

or

    ant all

The jar files `contract4j5.jar` and `contract4j5-test.jar` in the current directory will be built and the unit/acceptance tests will be executed for all three supported languages. The tests generate a LOT of output, but they should all pass. Also, there will be some warnings that fall into two categories:

Warnings in some unit tests when test annotations are used without the required `@Contract` annotation. This is deliberate for those tests.

The `javadocs` target also results in many warnings for references to aspects from Java files, which `javadoc` doesn't know how to resolve. To be clear, the following missing "classes" are actually aspects:

* `AbstractConditions`
* `ConstructorBoundaryConditions`
* `Invariant*Conditions` (several)
* `MethodBoundaryConditions`

For example, you'll see lots of warnings about not being able to find members of these aspects.

If the unit tests fail, look for output in `contract4j5/TEST-*.txt` files. Usually, the problem will be a `CLASSPATH` issue.

To build the example of wiring the components and properties of Contract4J5 using **Spring's Dependency Injection** run any of the following commands after building `all` as before:

    ./build.sh all.spring   # *nix
    build.bat all.spring    # windows

or

    ant all.spring

This will run a test target that confirms that Spring can "wire" Contract4J correctly.

This build target builds the example in the "sister" directory `../Contract4J5WithSpring`. It contains a separate example demonstrating how to use the Spring Framework's Dependency Injection (DI) to configure the properties of Contract4J. This is done separately from the main build so that Spring is not required for those people not using it.

The key files in this directory tree are the following:

<table>
    <tr><td>test/org/contract4j5/configurator/spring/test/ConstructWithSpringTest.java</td><td>Uses Spring's "ApplicationContext" to construct C4J, then tests that the components and properties are wired as expected.</td></tr>
  <tr><td>test/conf/applicationContext-contract4j5.xml</td><td>The application context configuration file that defines the "wiring".</td></tr>
  <tr><td>test/conf/contract4j.properties</td><td>A properties files used by the config. file.</td></tr>
</table>

An example of running the test suite using Contract4J5 with Load-Time Weaving (LTW) is also included. (The details of using LTW were discussed above.)

**Note:** LTW requires AspectJ 1.5.3 or newer, due to a bug in early versions.

To run the tests using load-time weaving, first build all as above, then build the following target:

    ./build.sh test.ltw   # *nix
    build.bat test.ltw    # windows

or

    ant test.ltw

This target will run the tests, using LTW of the aspects on the fly.

#### More Details on Building Contract4J5

The following third-party tools are required, along with corresponding `HOME` environment variable definitions needed by the ant build scripts:

* JUnit 3.8.1+ (`JUNIT_HOME`)
* Java 5 (`JAVA_HOME`)
* AspectJ 1.5.3+ (`ASPECTJ_HOME`)
* Ant 1.6.5+ (`ANT_HOME`)
* Jexl 1.1+ (included in the lib directory)
* JRuby 1.0+ (included in the lib directory)
* Groovy 1.0+ (included in the lib directory)
* Other support libraries in the lib directory.
* Also define `Contract4J5_HOME` to be the `.../contract4j5_XYZ/contract4j5` directory where you installed it.

For your convenience, you can use the build driver script `build.sh` or `build.bat`. Edit the values of the environment variables in the corresponding scripts `env.sh` or `env.bat` for your environment.

Only Java, AspectJ, the libraries for one of the scripting engines, and the support jars it requires are needed if you simply use the binary contract4j5.jar in the distribution. (All the scripting engines jars are in the lib directory.) Make sure your build process compiles with AspectJ, weaves your precompiled jars or class files with contract4j5.jar, or uses load-time weaving, as demonstrated in the "LTW" tests.


Next, we'll look at code examples, then return to a discussion of invoking and configuring Contract4J5.

<a name='showme'></a>

## Show Me the Code!

Here is a large example showing how to define Contract4J5 tests in your code so that Contract4J5 can discover and execute them at runtime. It is the file `test/org/Contract4J5/test/BaseTestClass.java` (with some superfluous details omitted). See additional examples in the unit/acceptance suite under the "test" directory.

The comments in this class should be self explanatory. More specific details on writing contracts are provided below. 
`BaseTestClass.java`:

    package org.contract4j5.test;

    import org.contract4j5.contract.*;
    /**
     * A (contrived) example Java class that demonstrates how 
     * to define DbC tests. The "@Contract" annotation is 
     * required. Then, we define a class-level invariant, 
     * which happens to be for one of the fields. Note that 
     * we have to prefix the field name with "$this", one of 
     * several special keywords that begin with "$" and are
     * replaced with special values before passing the 
     * expression to the script interpreter. In this case,
     * "$this" means "this object" (You can't just use "this"
     * without the "$" for backwards compatibility reasons;
     * this may be relaxed in a future release). 
     * Prefixing field names with $this is necessary for the
     * scripting engine to be able to resolve the variable
     * name. While not required in all cases, as a rule it is
     * best to always refer to fields this way for consistent.
     * The one case where you don't need the "$this." is when
     * you define an invariant for a field itself (See the 
     * test for "name" below). Note also that in order for
     * Jexl to resolve the field reference, a JavaBeans
     * "getter" method must exist for the field, even if the
     * field is public!
     */
    @Contract
    @Invar("$this.lazyPi==3.14159")	// see comments for "lazyPi"
    public class BaseTestClass {
      /**
       * A field that is initialized "lazily", but cannot 
       * change after that. This invariance is enforced by the 
       * @Invar annotation on the class. The constructor must 
       * call {@link #getLazyPi()} BEFORE ANY OTHER PUBLIC
       * FUNCTION, or the invariant test will fail!
       * NOTE: the Jexl parser chokes if the invariant test 
       * appends "f" to the constant!
       * NOTE: Jexl can't resolve "lazyPi" unless "getLazyPi()"
       * exists!
       */
      private float lazyPi = -1f;

      /**
       * "getLazyPi()" always simply sets the value to 3.14159,
       * so the class invariant "$this.lazyPi==3.14159" will 
       * always pass. However, see {@link #setLazyPi(float)}.
       * @return pi
       */
      public float getLazyPi() {
        if (lazyPi == -1f) {
          lazyPi = 3.14159f;
        }
        return lazyPi;
      }

      /**
       * This function allows unit tests to force a failure!
       */ 
      public void setLazyPi (float f) {
        lazyPi = f;
      }

      /**
       * A field that should never be null or "". See also 
       * comments in {@link #setName(String)}. Note that you 
       * can safely use the "bare" field name "name" here. 
       * You can also use "$this.name", which you have to use
       * in all other types of tests (i.e., tests other than 
       * the invariant test on the field itself). You can also
       * use the keyword "$target", which currently is only 
       * used to refer to a corresponding field when used in a
       * test expression. (In the future, "$target" may have 
       * other uses in the more general AspectJ-sense of the 
       * poincut "target()" expression.)
       * NOTE: You can specify an optional error message that
       * will be reported with any failure message. Also, as
       * stated before, "name" must have a "getName()" 
       * accessor or Jexl can't resolve it!
       */
      @Invar(value="name != null && name.length() > 0",
       message="this.name must never be null!")
      private String name;

      /**
       * @return String name of the object
       */
      public String getName() { return this.name; }

      /**
       * Use a precondition to prevent setting name to null. 
       * Note this test is less restrictive than the invariant
       * test on the field itself, a poor design. (Hopefully, 
       * the developer will realize the mistake when one test
       * fails while the other passes.) In this case, this 
       * "mistake" is useful for the dbc4j unit tests.
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
       * Set the flag. This method is used in unit tests to  
       * force a contract assertion failures.
       */
      public void setFlag (boolean f) { flag = f; }

      /**
       * Constructor. Note that the precondition on the "name"
       * parameter is redundant, since {@link #setName(String)}
       * is called, but it is still useful for documenting the 
       * interface. Note that the @Pre test does not
       * define a test expression. In this case, C4J5 uses a 
       * {@link org.contract4j5.testexpression.DefaultTestExpressionMaker}
       * to generate a default test expression. There are 
       * separate "makers" for different types of tests and 
       * contexts and they are user configurable. For 
       * preconditions, the default is to require that all
       * arguments are non-null.
       * Note that tests can call methods, too, but watch for 
       * side effects, especially since tests will normally be 
       * disabled in production builds. Therefore, never call 
       * a method with side effects!
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
       * Constructor. As discussed in {@link 
       * #BaseTestClass(String)}, the default test expression 
       * for the precondition test will be that all parameters,
       * in this case "name" and "flag", must be non-null. What 
       * does that mean for "flag", which is boolean. Not much; 
       * this argument will be converted to
       * {@link java.lang.Boolean} internally and it will never
       * be null! Also, in this example, the precondition test 
       * is actually redundant, since {@link #setName(String)} 
       * is called. However, the test is still useful for 
       * documenting the interface.
       * @param name a non-null String
       * @param flag a boolean flag; if false, causes the 
       *    postcondition to fail.
       */
      @Pre
      @Post ("$this.isValid() == true") // watch for side effects!
      public BaseTestClass (String name, boolean flag) {
        /* float ignore = */ getLazyPi();
        setName (name);
        setFlag (flag);
      }

      /**
       * Is the object valid?
       */
      public boolean isValid () {
        System.out.println ("ExampleClass.isValid(): flag: "+flag);
        return flag;		// reusing our flag...
      }

      /** 
       * Method that requires flag to have been previously set.
       * E.g., {@link #setFlag(boolean)}, {@link #doIt()}, etc.
       * Note the postcondition to confirm that the method 
       * succeeded, where "$return" is the keyword that matches
       * the value returned by the method (an int in this case).
       */
      @Pre(value="$this.flag == true", 
            message="this.flag true before calling 'doIt()'?")
      @Post("$return == 0")
      public int doIt () {
        if (name != null && name.equals("bad name")) {
          return 1;
        }
        return 0;
      }

      /** 
       * Overloaded method. Useful to confirm that the 
       * generated tests correctly discriminate between the 
       * methods (note the conflicting @Post annotations on the 
       * two versions.)
       */
      @Post("$return != 0")
      public int doIt (int toss) {
        if (name.equals("good name")) {
          return 1;
        }
        return 0;
      }

      /** 
       * Method with tests on more than one parameter. The 
       * keywords "$args[n]" refer to the parameter arguments, 
       * counting from 0.
       */
      @Pre ("$args[0]> 0 && $args[1].equals(\"foo\")") 
      public int doThat (int toss, String fooStr) {
        return toss;
      }

      /** 
       * Method with tests on more than one parameter. Tests 
       * whether we correctly generate matching aspects on the 
       * second and last parameter. Note that a nested string
       * in a test must be escaped.
       */
      @Pre ("toss2 > 0 && toss4.equals(\"foo\")") 
      public int doTheOther (int toss1, int toss2, 
                             String toss3, String toss4) {
        return toss1;
      }

      /**
       * Test Contract4J5 with a nested class
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
         * Method to force the invariant test to fail, 
         * if a negative argument is used.
         */
        public void setPositive (int p) { this.positive = p; }

        public int getPositive () { return this.positive; }

        // The @Post on "name" should really be a @Pre on "nm",
        // as it is more restrictive, but it is useful for 
        // example purposes.
        @Post ("$this.name != null && $this.name.length() > 0 && nm != null")  
        NestedBaseTestClass (String nm) {
          this.name = nm;
          this.positive  = nm != null ? nm.length() : -1;
        }
      }
    }

<a name='details'></a>
  
## Details of Contract Specifications

Here are the rules for using Contract4J5, which clarify the examples just discussed.

### Annotate Interfaces and Classes with `@Contract`

Examples:

    @Contract
    public interface Foo { ... } 

    @Contract
    public class Bar { ... } 

Any interface or class that declares a contract must be annotated with `@Contract`. Otherwise, the tests will be ignored. C4J5 will issue a warning during compilation, but if you use `javac` to compile and then weave later with `ajc`, you may not get any warnings and the tests will be silently ignored.

The annotation must also appear on any derived interfaces or classes if they define new tests.

### Define Class Invariants

Examples:

    @Contract
    @Invar("boolean_test_expression")
    public interface Foo { ... }

    @Contract
    @Invar(value="boolean_test_expression", message="The test failed")
    public class Bar { ... }

The second example shows the optional "message" that will be reported on error, in addition to standard messages. The test expression must return boolean, or it will be treated as a contract failure!

For subclasses, it isn't necessary to annotate them, too, because class-level annotations are inherited, but you can do so for consistency with method annotations, which aren't inherited and must be added to subclass overrides.

Define Field Invariants

Examples:

    @Contract
    public class Bar {
      @Invar("name != null && name.length() > 0")
      private String name;
      public  String getName() { return name; }
      ...
    }

Note that field invariants can't be defined on interfaces, since they don't have mutable fields, but you can simulate the same thing by annotating corresponding accessor methods in the interfaces (see below).

Note that for the field `name`, we are able to use the "bare" field name when defining an invariant test for it. You can also use `$this.name` or the `$target` keyword.

**WARNING!** As discussed previously, the JEXL interpreter can only resolve the field if a JavaBeans "getter" method is defined for it, as shown in the example.

In the future, `$target` may be used more generally for objects that correspond to AspectJ's `target()` *pointcut* expression, but currently `$target` is only used in field invariant tests to refer to the field.

### Define Method and Constructor Preconditions, Postconditions, and Invariants

**NOTE:** we have to use `$this.name` in the following interface example, not just `name` by itself, because we are no longer defining a field invariant test! This is because **Jexl requires a field getter method is required for Jexl to resolve the field reference.** (We recommend the same convention for JRuby and Groovy, too.)

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

      int getI() { ... }     // getter method required by Jexl!
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

      float getFactor() { ... }   // getter required by Jexl!
    }

The `Foo` interface simulates a field invariant test on an implied name field by defining invariant tests on name's accessor methods. The precondition test for `incrementI` requires the input amount to be positive. (You could also write this test `amount > 0`, but this is actually less robust; it is more likely to trip over parsing idiosyncrasies in Contract4J5 and Jexl.) This test implies an `int i` field, as does the post condition test which requires that the new value of `i` be equal to the old value (grabbed by the `$old($this.i)` expression) plus the amount.

**NOTE:** The `$this` cannot appear in `@Pre` tests on constructors, as the object doesn't exist yet! However, `$this` is allowed in `@Invar` tests on classes and constructors, as they won't be evaluated until after the constructor finishes executing.

The `FooImpl` implementing class *must* repeat the annotations, but it can omit the test expressions. Contract4J5 will determine the expressions from the parent class or interface.

What if you omit the expressions? Currently, there is no compile-time checking possible the tests will not be evaluated on these method implementations. For subclass overrides of parent class methods, the tests will not be evaluated on the overrides, but they will be evaluated on the parent methods if `super` is called.

**NOTE:** A desired future enhancement is to either enforce proper usage of the annotations on implementing classes or subclasses or else evaluate the tests even when they are absent!

The `Bar` class shows that tests can also be defined for constructors. (Invariant tests are also allowed, but seldom useful, since contract tests mostly focus on object state, which won't exist before construction!)

Note the idiom for the `@Pre` and `@Post` test expressions for the `Bar` constructor. The `@Pre` expression references the `factor` parameter, while the `@Post` expression references the factor field, using the `$this.` prefix. A common mistake is to omit the `$this.` causing the test to be executed with the parameter value instead.

**NOTE:** Jexl appears to choke if floats have a trailing `f`. As shown in the example, they should be omitted.

### Special Keywords

The previous examples use the special keywords. Contract4J5 substitutes the correct values before invoking the scripting engine to evaluate the expressions. Here is a description of the keywords and their proper use.

<table>
  <tr><td>Keyword</td><td>Usage</td></tr>
  <tr><td>$this</td><td>The "this" object under test.</td></tr>
  <tr><td>$target</td><td>A field in an invariant test. There must be a corresponding JavaBeans "getter" method or Jexl won't be able to resolve the field.</td></tr>
  <tr><td>$return</td><td>The return result of a method; only valid in postconditions.
  <tr><td>$args[n]</td><td>The "nth" argument in a parameter list.</td></tr>
  <tr><td>$old(..)</td><td>The "old" value (before a method is actually executed) of the contents of the expression, which can be one of the following:
    <table>
      <tr><td>$old($this)</td><td>Not recommended, because only the reference is saved and the object pointed to by "this" may change! Use fields or method calls instead.</td></tr>
      <tr><td>$old($target)</td><td>Equal to $old($this.field). Be careful if "field" is mutable; the value is not saved, just the reference to the object!</td></tr>
      <tr><td>$old($this.field)</td><td>Recommended usage, if "field" is primitive, in which case the value is captured, or it refers to an immutable object. Same for $old($target.otherField).</td></tr>
      <tr><td>$old($this.method(x,y))</td><td>The returned value is saved. Due to parser limitations, method calls may not contain nested method calls.</td></tr>
    </table>
  </td></tr>
</table>

The most important thing to remember about `$old(..)` is that Contract4J5 only remembers the value, which may be a reference to a mutable object. (We can't rely on `clone()` working.) Try to use it only with primitives or immutable objects like strings.

### Test Expression Best Practices

This section outlines the right way to write test expressions, reflecting the limitations of Contract4J5 and the different supported scripting engines. For detailed examples, see the code in the test suite (e.g., `BSFExpressionInterpreterAdapterExpressionEvalTest.java`).

#### Default Test Expressions

If a contract annotation is used with no test expression, a default expression will be inferred with possible. For cases where no test can be inferred, it is considered an error, but this can be overridden with an API call:

    ExpressionInterpreter.setTreatEmptyTestExpressionAsValidTest(boolean);
    
If the test is on an element with no superclass equivalent, the following default rules apply:

<table>
  <tr><td>@Pre</td><td>All arguments are expected to be non-null, which means there is no meaningful default test for primitive arguments.</td></tr>
  <tr><td>@Post</td><td>The return value is expected to be non-null, unless the method returns void.</td></tr>
  <tr><td>@Invar</td><td>There is no default expression except for field invariants, where the field is expected to be non-null.</td></tr>
</table>

For elements with superclass equivalents, the following rules apply. First, recall that preconditions and postconditions can only be used on methods and constructors. Also, because Java5 method annotations are never inherited, you *must* annotate any method with the same annotations found on its parent. However, the test expressions can be empty and if so, the corresponding test defined in the parent element will be used. Note that if you don't put the annotations on derived class overrides, if they call the parent methods, the parent methods will be tested, but not anything the override does (including constructors).

API calls exist in the `org.contract4j5.aspects..*.aj` aspects to specify customized objects for calculating a default expression at runtime. These objects must implement

    org.contract4j5.testexpression.DefaultTestExpressionMaker.

#### Inheritance Rules for Annotation Test Expressions

This was discussed in depth above, in the section titled How Does Contract4J5 Support Design by Contract?.

* `$this` refers to the object being tested.

You can call any public method on the object in the test expression; the scripting engine will resolve the type. Additionally, if you refer to a bare field that is not public, the scripting engine will convert the expression to the corresponding "getter" call. Unfortunately, it appears that the getter method is actually required in order for Jexl to resolve the field; it won't just use the bare field directly.

* `$target` currently is used only to refer to the field in a field invariant test.

Future use may include any context associated with the `target()` *pointcut* expression. Just as for `$this`, you can reference any method or field defined for the object.

* `$return` is the value returned by a method.

It is only valid in postcondition tests. As for `$this`, you can reference any method or field defined for the object.

* `$args[]` are the arguments passed to a method, indexed starting at zero.

You can also use the declared argument name. However, if the name shadows an instance field, the parser may confuse the two; use the appropriate `$arg[n]` in this case. As for `$this`, you can reference any method or field defined for the objects in the array.

* Use of the `$old(..)` Keyword

The "old" `$old(..)` keyword tells Contract4J5 to remember the value of the contained expression before evaluating evaluating the join point, so that value can be compared to the "new" value after evaluating the join point. It can only be used in `@Invar` and `@Post` condition tests and the saved value is forgotten once the test completes.

The most important thing to remember about `$old(..)` is that Contract4J5 only remembers the value, which may be a reference to a mutable object. Since `clone()` is not guaranteed by Java to be publicly available on an object, we can't clone it and it was deemed too "obscure" to only permit, for example `$old($this)` on objects where clone is publicly available. Hence, you should try to use the `$old` keyword only with primitives or references to immutable like strings.

Here are the allowed expressions.

<table>
  <tr><td>$old($this)</td><td>Not recommended, since only the reference is saved.</td></tr>
  <tr><td>$old($target)</td><td>Equal to $old($this.field). Be careful if "field" is a reference to a mutable object!</td></tr>
  <tr><td>$old($this.field)</td><td>Recommended usage, if "field" is primitive. A synonym for $old($target) when used in a field invariant test.</td></tr>
  <tr><td>$old($this.method(x,y))</td><td>Method call where the returned value is saved. Due to current parser limitations, nested method calls are not supported. So, the following is okay:
    $old($this.getFoo().doIt(1))
but this is not
    $old($this.getFoo().doIt(getIntI()))</td></tr>
</table>

* References to Instance Fields.

For fields, Jexl, Groovy, and JRuby will automatically convert a "bare" field reference to its accessor, even if the field is private. Hence, an expression like

    $this.foo.bar.baz.doIt(1)

is allowed and will be translated to

    $this.getFoo().getBar().getBaz().doIt(1)

**Note:** In fact, for Jexl the corresponding getter methods are *required* or Jexl will not be able to resolve the field references.

Normally, you should prepend `$this.` before a "bare" field reference as the parser does not always correctly resolve the reference to an instance field. The one case where $this is unnecessary is inside a field invariant test. Using the field's "bare" name will work. As an alternative in field invariant tests, $target can be used to refer to the field.

Note the example previously where a field invariant test was written with the bare field called `name`, but when a set of "conceptually similar" `@Pre` and `@Post` tests were written in an interface on `setName()` and `getName()` methods, it was necessary to use `$this.name`. Contract4J5 may not resolve the field correctly in those cases.

* Tests Defined on Interfaces

You can define tests on interfaces and their methods. In fact, you are urged to do so. Unfortunately, for reasons discussed previously, you must include the same annotations, although not their test expressions, on the declarations of the method implementations in implementing classes. Contract4J5 will find the test expressions in the interfaces.

Unfortunately, you can't define contract tests for constructors, since they don't exist in an interface. You may be able to work around this using class invariants and tests on instance methods. Note that you can also implicitly define field invariant tests, either on declared accessor methods or as invariants on the class itself. You can refer to the (implied) bare field in the test, as long as you declare an appropriate accessor method for it.

<a name='limits'></a>

### Known Limitations

(New for V0.8.0) See also Miscellaneous Notes and Debugging Tips and Inheritance Behavior of Contracts.

Referencing other objects, classes, and static methods/fields in test expressions. The scripting engines all run in a different "context" from your running classes. This means that simply referring to an object in an expression doesn't guarantee that the script will see it, even if the same reference would compile if it were in regular Java code appearing in the class under test. Hence, there are limitations and they vary from one scripting engine to the other. Contract4J offers some help and where it won't be able to resolve a reference, there are a few workarounds. Here is a summary of the behavior for Groovy:

* You can reference another instance field or method without the prefix $this..
* You can reference a static field or method in the class under test, but you must prefix it with $this.. This appears to be a limitation of Groovy.
* You can reference another class in the same package as the class under test, without qualifying it with its package.
* Except for Groovy, you can reference another class in a different package with its fully qualified name. However, nested classes don't work, because you have to use '$' instead of '.', as Contract4J will attempt to load the class in the scripting context using `Class.forName(...)`, which requires the '$' delimiter (e.g., `com.foo.bar.MyClass$MyNestedClass`). However, when the expression is evaluated later, some scripting engines will choke on the '$' (catch-22...). **Workaround:** use a validation method in your class under test to invoke the static method or use one of the other workarounds described here. Groovy interprets the fully-qualified names as strings of property queries.
For Groovy and JRuby (not Jexl), you can "preregister" classes or objects in your static initializer block or constructor. Using the static initializer block will usually be the easiest approach, especially if you need the classes or objects in constructor precondition tests. You can register the classes or objects in the constructor if they are only needed by subsequent calls to instance methods. JRuby can only reference "global" variables. Here is an example for Groovy:

    // In Validator.java
    package com.foo.bar1;
    public class Validator {
      public static boolean called = false;
      public static boolean valid(String s) { return s != null && s.length() > 0; }
      public        boolean valid2(String s) { return valid(s); }
    }

    // In MyClass1.java
    package com.foo.bar2; // different package
    class MyClass1 {
      static {
        Contract4J.getInstance().registerGlobalContextObject("Validator", com.foo.bar1.Validator.class);
      }
	
      @Pre("Validator.valid(name)")
      public MyClass1(String name) {...}
	
    }

    // In MyClass2.java
    package com.foo.bar2; // different package
    import com.foo.bar1.Validator;
    class MyClass2 {
      static {
        Contract4J.getInstance().registerGlobalContextObject("validator", new Validator());
      }
	
      @Pre("validator.valid2(name)")
      public MyClass2(String name) {...}
    }
	
* For JRuby, the previous test expressions must put a '$' before the Validator and the validator references. See `test.org.contract4j5.aspects.constructor.test.ConstructorBoundary*ExpressionsWithObjectReferencesTest` classes for more examples for the different languages.

* JRuby support is deprecated and will be removed in a future release.

* Jexl support may be deprecated, but keeping it provides a faster, if limited, scripting alternative.

* It appears that Jexl and JRuby **can't handle Java 5 generic objects**. The Contract4J unit tests with generics are effectively "no-ops" except when Groovy is the interpreter.

<a name='tips'></a>

### Miscellaneous Notes and Debugging Tips

* All test expressions *must* evaluate to a **boolean** value.

* Test expressions that fail to be evaluated by the scripting engine are treated as test failures, on the grounds that the expression is buggy in this case! Note that if an annotation is empty (i.e., it doesn't define a test expression), then it is considered an error if a default expression can't be inferred and no corresponding test exists on a parent class. However, there is an API call to allow empty tests, `ExpressionInterpreter.setTreatEmptyTestExpressionAsValidTest(boolean)` in the `org.contract4j5.interpreter` package).

* When a test fails due to a buggy or empty test expression, as just described, a subclass of `ContractError` is thrown, `TestSpecificationError` (new as of v0.6.0).

* Remember that when using Jexl, if any test accesses an instance field, the field must have a corresponding JavaBeans getter method. Otherwise, Jexl will fail to resolve the field and the test will fail.

* Fields in expressions must be prefixed with ., except in field `@Invar` test expressions. Jexl is more forgiving if you omit the prefix.

* Avoid expressions with side effects. Since tests will usually be turned off in production, test expressions with side effects, e.g., assignments, will not be evaluated, thereby changing the logical behavior of the application.
When using load-time weaving (LTW), if it appears that the tests aren't being evaluated, make sure you have an `aop.xml` file in your class path. Adapt the example file used for the LTW tests.

* Because runtime expression evaluation is very slow compared to compiled code, consider embedding non-trivial tests in "validation" methods and calling them from the test expression. (Prepend instance tests with `$this.`)

* Keywords follow the same white space rules as for Java. Don't allow whitespace between `$` and the keyword names.

* Jexl can't parse literal floats and doubles with the 'f' and 'd' appended, respectively. Leave them off in both cases.

* Most other Java expressions, like comparisons and arithmetic expressions can be used. See the Jexl website for more information on allowed expressions.

* Before passing the expressions to Jexl, substitutions are made. Normally, you shouldn't case, but when debugging, you may see strings with these substitutions. All the '' keywords are changed. For example,

<table>
  <tr><td>$this</td><td>becomes</td><td>c4jThis</td></tr>
  <tr><td>$target</td><td>becomes</td><td>c4jTarget</td></tr>
  <tr><td>$old($this)</td><td>becomes</td><td>c4jOldThis</td></tr>
  <tr><td>$old($target)</td><td>becomes</td><td>c4jOldTarget</td></tr>
  <tr><td colspan="3" style="text-align='left'">etc.</td></tr>
</table>

* Turn on `DEBUG` logging to see what expressions are being evaluated and some of the substitutions that are made.

* Some common test expression errors have "canned" strings defined for them in `org.contract4j5.interpreter.ExpressionInterpreter.java`. The heavy lifting of expression evaluation is done in `org.contract4j5.interpreter.ExpressionInterpreterHelper.java` and its subclasses for Groovy, Jexl, and JRuby, respectively; `org.contract4j5.interpreter.groovy.GroovyExpressionInterpreter` (default), `org.contract4j5.interpreter.bsf.groovy.GroovyBSFExpressionInterpreter` (through BSF), `org.contract4j5.interpreter.jexl.JexlExpressionInterpreter`, `org.contract4j5.interpreter.bsf.jexl.JexlBSFExpressionInterpreter` (through BSF), and `org.contract4j5.interpreter.bsf.jruby.JRubyBSFExpressionInterpreter`.

* As of V0.8, the Bean Scripting Framework (BSF) is an option for Jexl and Groovy and remains the only option for JRuby. There is no noticeable performance penalty when BSF is used, compared to the rest of the Contract4J overhead, although there appears to be a noticeable overhead over invoking an "empty" script through BSF vs. invoking it directly with the scripting engine, when using test applications separate from Contract4J. By default, Jexl and Groovy don't use BSF.

* If you want to use a different language, the easiest approach is to create a subclass of `BSFExpressionInterpreterAdapter`. See e.g., `GroovyBSFExpressionInterpreter`.

* To select a different language option, see the **Configuration** section (next).

<a name='config'></a>

### Configuration of Contract4J5

To configure the behavior of Contract4J5, when the default behavior doesn't meet your needs, you have several options.

<table>
  <tr><td>Spring dependency injection</td><td>The preferred method for nontrivial configuration changes. See the Spring V1.2 example in Contract4J5WithSpring. (There should be no issues using Spring V2.X)</td></tr>
  <tr><td>Property file configuration</td><td>The property file approach is fine for basic needs. See the unit test `org.contract4j5.configurator.test.PropertiesConfiguratorTest.java` for examples.</td></tr>
  <tr><td>API calls</td><td>There is an extensive internal API for configuration, which we discuss next.</td></tr>
</table>

#### Configuration Through the API

Here are some API examples.

* Select a Different Scripting Language

Set a property when invoking the JVM:

    java -Dinterpreter=lang ...

where `lang` is one of:
<table>
  <tr></td>`lang`</td><td>Which language?</td></tr>
  <tr></td>groovy</td><td>Groovy without BSF (default)</td></tr>
  <tr></td>groovybsf</td><td>Groovy with BSF</td></tr>
  <tr></td>jexl</td><td>Jexl without BSF</td></tr>
  <tr></td>jexlbsf</td><td>Jexl with BSF</td></tr>
  <tr></td>jruby</td><td>JRuby with BSF</td></tr>
  <tr></td>other</td><td>Any other language for which you have implemented an integration with Contract4J, as discussed elsewhere in these notes.</td></tr>
</table>

You can also specify the language using Spring configuration, as discussed above.

* Enable or Disable Test Types

    import org.contract4j5.controller.Contract4J;
    ...
    Contract4J.getInstance().setEnabled(Contract4J.TestType.Pre,   true); // or false
    Contract4J.getInstance().setEnabled(Contract4J.TestType.Post,  true);
    Contract4J.getInstance().setEnabled(Contract4J.TestType.Invar, true);

To completely disable contract checking, e.g., in production builds, build the application without `contract4j5.jar` in your `ASPECTPATH`.

* Specifying Major Components

There are several key components in Contract4J5 that are configurable:

<table>
  <tr></td>ExpressionInterpreter</td><td>Wraps Groovy, Jexl, and JRuby (It could also wrap other languages)</td></tr>
  <tr></td>ContractEnforcer</td><td>Handles test invocation and failure handling.</td></tr>
  <tr></td>Reporter</td><td>Simple output/logging wrapper.</td></tr>
</table>

Note that a runtime warning are issued if the `ExpressionInterpreter` or `ContractEnforcer` are not defined, as tests can't be run otherwise! The Reporter objects will default to `stdout` and `stderr` if undefined.

Here are more details about these component interfaces and implementing classes:

<table>
  <tr></td>org.contract4j5.enforcer.ContractEnforcer</td><td>The "enforcer" interface.</td></tr>
  <tr></td>org.contract4j5.enforcer.ContractEnforcerImpl</td><td>The one implementation used here. It runs the tests and on failure, logs a detailed error message and terminates program execution.</td></tr>
  <tr></td>org.contract4j5.interpreter.ExpressionInterpreter</td><td>The expression interpreter interface.</td></tr>
  <tr></td>org.contract4j5.interpreter.ExpressionInterpreterHelper</td><td>An abstract helper class that provides a partial implementation. Subclass this class to support the actual interpreters.</td></tr>
  <tr></td>org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter</td><td>Adapts the Bean Scripting Framework (BSF) for use by Contract4J5. (As of V0.8, Groovy and Jexl no longer use it, by default, although the BSF integration is still in the code base.)</td></tr>
  <tr></td>org.contract4j5.interpreter.groovy.GroovyExpressionInterpreter</td><td>The Groovy subclass of the "helper" class.</td></tr>
  <tr></td>org.contract4j5.interpreter.bsf.groovy.GroovyBSFExpressionInterpreter</td><td>The Groovy BSF support class (optional).</td></tr>
  <tr></td>org.contract4j5.interpreter.jexl.JexlExpressionInterpreter</td><td>The Jexl subclass of the "helper" class.</td></tr>
  <tr></td>org.contract4j5.interpreter.bsf.jexl.JexlBSFExpressionInterpreter</td><td>The Jexl BSF support class (optional).</td></tr>
  <tr></td>org.contract4j5.interpreter.bsf.jruby.JRubyBSFExpressionInterpreter</td><td>(Deprecated) The JRuby BSF support class.</td></tr>
  <tr></td>org.contract4j5.reporter.Reporter</td><td>The "reporter" interface that is a thin veneer for a logging abstraction.</td></tr>
  <tr></td>org.contract4j5.reporter.Severity</td><td>Defines logging levels of severity, like INFO, WARN, ERROR, etc.</td></tr>
  <tr></td>org.contract4j5.reporter.WriterReporter</td><td>"Logs" to stdout and stderr by default, but also supports file output. It would be very easy to implement a "log4j reporter", for example.</td></tr>
</table>

### Notes:

For most properties currently defined, if a value is empty, it is ignored! In some cases, warnings are issued.

<a name='todo'></a>

### TODO Items

Here is a brief list of the most important "TODO" items that we want to complete before the V1.0 release, roughly in order of importance.

* Improve performance. Unfortunately, the current runtime overhead of C4J is prohibitive, making it difficult to use continuously on large projects.
* Drop support for JRuby and possibly Jexl. It's getting increasingly difficult to support three languages, as each has its own tool chains, idiosyncrasies, etc. Would everyone be happy with Groovy as the officially-supported language with possibly limited support for Jexl, when optimal performance is most important?
* Find a way to automatically enforce proper usage of method and constructor annotations in subclasses when they aren't annotated.
* Implement correct contravariant behavior of inherited precondition tests and covariant behavior of inherited postcondition tests.
* Support a method for defining constructor conditions in interfaces, where constructors don't exist.
* Support end-user extensions with custom annotations and "behavior" handlers.
* Support tests on static methods.
* Mock out logging output so test runs don't generate so much output. 

Here is a list of other possible enhancements.

* Extend @Pre and @Post to support class-level annotations that will apply to all protected and public instance and static methods. For example, @Contract @Pre @Post public class Foo {...} would require all such methods to take non-null arguments and return non-null values, consistent with the way @Pre and @Post work by default today on methods. Add optional configuration properties to each annotation to set the protection level for which the annotation applies (e.g., also do private methods.) and whether or not to exclude static methods.
* Interface to scripting languages using the native scripting support in Java 6.
* For the keywords:
 * Provide a "$field" alias for "$target", since the latter isn't used for anything other than fields. Keep "$target" for backwards compatibility.

<a name='history'></a>

## History

### Contract4J5 (Annotation Form)

 * v0.9.0.0	November 13, 2009
 * v0.8.0.0	September 13, 2007
 * v0.7.1.0	January 21, 2007
 * v0.7.0.0	December 31, 2006
 * v0.6.0.0	September 21, 2006
 * v0.5.0.0	February 7, 2006
 * v0.1.1.0	October 4, 2005
 * v0.1.0.2	April 24, 2005
 * v0.1.0.1	February 6, 2005
 * v0.1.0.0	January 31, 2005

 Contract4JBeans (Experimental)

 * v0.3.0.0	February 20, 2006
 * v0.2.0.0	October 5, 2005
 * v0.2.0.0M1	August 15, 2005

<a name='notes'></a>

## Notes for Each Release

v0.9.0 November 13, 2009

Performance improvements and refactorings. 

I've been away from Contract4J for two years! This release focuses on performance-related enhancements, the most important area of work required before a 1.0 release.

Added a new field for the contract annotations. Using run=NEVER disables the test, for @Pre, @Post, @Invariant annotations, and disables all the tests in the type for @Contract annotations. The other allowed value is run=ALWAYS, which is the default. I used ALWAYS and NEVER, rather than ON and OFF, for example, to allow possible additional values. ONCE, as in run each test only once, is a logical and potentially-useful choice, although the implementation is somewhat challenging.

v0.8.0 September 13, 2007

Deprecated JRuby and "partially" deprecated Jexl, made BSF optional, implemented bug fixes in test expression handling, etc.

Deprecated JRuby and raised the possibility of deprecating Jexl. Groovy is now the standard scripting language for Contract4J. (Load-time weaving with JRuby is no longer supported at all; the "test.ltw.jruby" target is still in the build, but isn't executed as part of the standard "test.ltw" target anymore...)
Made BSF optional as the mediator between Contract4J and Jexl and Groovy. This was done originally because of a perceived performance penalty imposed by BSF and because different versions of BSF are needed for JRuby vs. Jexl and Groovy. The performance penalty turned out to be minimal, so the BSF integration was retained as an option. Since JRuby itself is also deprecated, it still uses BSF exclusively. (Feedback on the use of BSF is welcome.)
Fixed bugs that prevented tests from being applied for generic classes. (But it appears that only Groovy supports testing generics!)
Added support for referencing other objects and classes in test expressions, not just the class under test itself, which was not well supported previously. (See the Known Limitations for details.)
Increased the number of tests by ~15%.
Thanks to Chuck H. for additional feedback and Sebastiaan v. E. and Daniel S. for finding bugs!

v0.7.1 January 21, 2007

Minor Documentation Bug Fixes

Fixed examples that had obsolete package and class references. Added more details about how to use load-time weaving (LTW). 
Thanks to Chuck H. for valuable feedback.

v0.7.0 December 31, 2006

Support for Groovy, JRuby, and Other Refinements

This release adds built-in support for using Groovy or JRuby as an alternative to Jexl as the scripting engine. In fact, because of Jexl limitations, Groovy is now the default scripting language at startup. (This is easily configurable, as discussed previously.) In our experiments, most Jexl-compatible expressions work just fine with Groovy.

Using JRuby requires porting some existing expressions. However, to facilitate using common Java idioms in Ruby without translation, Contract4J5 makes a few simple substitutions automatically in test expressions:

null is changed to nil
equals(...) is changed to eql?(...)
compareTo(...) is changed to <=>(...)
New scripting engines can be integrated through the Jakarta Bean Scripting Framework (BSF). However, the integration with Groovy and Jexl no longer use BSF, be default, although the option is still there. Are the library dependencies:

Scripting Engine	Required Libraries
Several	bsf-2.4.X.jar, commons-logging.jar-1.0.X.jar
Jexl	All plus commons-jexl-1.1.jar
Groovy	All plus groovy-1.0.jar (RC 1 or newer), asm-2.2.jar, and antlr-2.7.5.jar
JRuby	All plus jruby-1.0.jar or later
All these jars, which the exception of JRuby and its dependent jars, are included with the distribution (in the lib directory). Remove the ones you don't need from your deployment. If you use JRuby, you'll need to define the JRUBY_HOME environment variable appropriately in the env.sh

The following is the partial list of differences between scripts written with Jexl, Groovy, or JRuby. Some of these differences may reflect idiosyncrasies of how they are used in the implementation, rather than real language differences. Also, in general, Groovy and JRuby are more full-featured environments, so they provide facilities that Jexl doesn't, such as closures.

Groovy and JRuby scripts can reference fields in a class without requiring the class to provide an accessor method. Jexl always requires accessors (as emphasized previously).
Groovy can reference accessor methods that aren't public, while JRuby and Jexl only accesses public accessors.
JRuby will translate some idiomatic-Ruby expressions to corresponding idiomatic-Java expressions. For example, field to getField(), field= to setField(), and method_with_words to methodWithWords.
Jexl appears to swallow ArrayIndexOutOfBoundsExceptions while Groovy and JRuby do not.
When writing tests while using Groovy or JRuby, fields must be prefixed with $this., except in field @Invar expressions. (Jexl parsing is more forgiving.) If you get a test-failure message that reports a groovy.lang.MissingPropertyException, for Groovy, or "undefined local variable or method", for JRuby, make sure the test expression doesn't have a bare field reference!
Jexl can access package protected and protected fields while Groovy and JRuby cannot.
The Groovy interpreter provides more descriptive error messages when a test fails because the expression is bad, e.g., because it references a non-existent field on an object. As a result, while such tests fail whether using Jexl or Groovy, debugging the issue will often be easier with Groovy. JRuby messages are descriptive as well.
Search the class BSFExpressionInterpreterAdapterExpressionEvalTest for the variables isJexl, isJRuby, isGroovy and isBSF to see examples of tests where Jexl, JRuby, Groovy, and whether or not they are going through BSF, behave differently.

Configuration of Failure Handling

It is now easier to configure the behavior for what happens when a contract failure occurs. The interface ContractEnforcer exposes more configuration options, which are exercised by the unit tests, example property files, and the Spring example. The default enforcer is now called DefaultContractEnforcer (was ContractEnforcerImpl), which implements a new abstract class ContractEnforcerHelper. DefaultContractEnforcer implements an abstract method finishFailureHandling() that throws the usual ContractError. Create a new subclass of the helper that does your desired handling. One option is to override the makeContractError() method and have it return your own subclass of ContractError, then catch that error in your code.

This change is in anticipation of a planned generalization of Contract4J to support user-defined annotations and associated behaviors, so that the infrastructure can be used for a variety of applications beyond Design by Contract.

More Robust Handling of String Literals With Test Expressions

Previously, if a variable (field, parameter, etc.) name appeared within a string literal, it was sometimes replaced with an internal representation of the variable. This would break test expressions like param1.equals("param1"), where param1 is a method parameter. The result was a test failure even if param1 really had the value "param1"! Now, string literals are unmodified.

Better Diagnostic Information

More descriptive information from nested exceptions is provided in the output. Furthermore, by default, BSF logs exceptions that are thrown. This should make debugging problems easier. (To disable BSF logging, configure Apache's Commons Logging as desired.)

Java JDK 1.6 Compatability

This release of Contract4J was tested using the final release of Java SE 1.6.0. While it doesn't use any Java 6 features (such as the JSR-223 support for scripting), it does offer performance improvements. The JUnit suite runs approximately 18% faster than under Java 5 and when the load-time weaving (LTW) test target is run, the improvement exceeds 30%!

Support for JSR-223 (Java 6) is under consideration.

JDepend and FindBugs Support

The distribution now includes convenience scripts for evaluating the structure of Contract4J5 using jdepend and findbugs. These tools were used to restructure Contract4J5 to eliminate a number of circular dependencies and other problems. See the jdepend.sh and findbugs.txt files in the contract4j5 directory for more information, such as how to install the required tools. A graph of jdepend output for Contract4J5 is jdepend_report.png.

General Clean-ups

More clean-ups of package dependencies and generics-related warnings were also done.

v0.6.0 September 21, 2006

Restructuring

This release is a major restructuring to reduce the component complexity, to reduce over-reliance on singletons, and to improve the "wiring" options. Defining contracts is unchanged (except for some bug fixes), so most users won't be affected. However, the configuration API has changed (this is still pre-1.0 software!), as discussed below in these notes and elsewhere in this README.

Spring Dependency Injection Example

An example has been added that uses the Spring Framework (v1.2.5) to configure Contract4J5. See the separate folder called Contract4J5WithSpring. Spring v2.0 configuration should be backwards compatable, if not easier with the new 2.0 features. However, using Contract4J5 with Spring v2.0 has not been tested.

Improved Configuration Options

The restructuring greatly improved the options for using properties files to "wire" Contract4J5, as an alternative to using Spring. See the tests in org.contract4j5.configurator.test for examples.

New Error Thrown to Indicate Bad Test Specifications

If a test fails because the expression is empty or can't be evaluated by Jexl, a new subclass of ContractError, TestSpecificationError, is now thrown. This makes it easier to determine when a test failed because the test itself was bad, as opposed to the class under test failing to meet the contract. Using a subclass of ContractError means that any existing catch (ContractError ce) code will continue to catch both kinds of failures. However, users who want to distinguish between the two types of errors should use this idiom:

  try {
    ...
  } catch (TestSpecificationError tse) {
    ...
  } catch (ContractError ce) {
    ...
  }
Binary Weaving and Load-Time Weaving Options

The build process now uses binary (jar) weaving when the "tests" are built, providing an example of using Contract4J5 with this approach. An example of using load-time weaving (LTW) is also provided, using a separate test target, as discussed above.

"Binary" weaving is weaving done after compiling all code, using a post-compilation weaving step to incorporate the Contract4J5 aspects. It is useful for organizations that prefer to use javac for all java files.

Load-time weaving is done as the application loads class files, using a special "java agent" for this purpose. Using load-time weaving is the least disruptive way to adopt Contract4J5 into "pure Java" environments. For this reason, we recommend starting with load-time weaving, since this approach requires minimal changes to the existing build environment. However, since LTW is slower than binary weaving, especially when running numerous tests, larger projects may prefer to switch to binary weaving at some point.

Previous releases of Contract4J5 used just compile-time weaving, where ajc was used to compile all sources and weave the class files. This approach is still used to build Contract4J5's own source code (as opposed to its unit tests) and to create the contract4j5.jar file.

Miscellaneous

Replaced the entity definitions in the build-related XML files with the ant task. Apparently, NetBeans doesn't like the entity definitions. (Thanks to Matthew Harrison for bringing this to my attention and for providing refactored build files.)
Added more tests that explicitly demonstrate that contract expressions that access instance properties only work if the properties have JavaBeans getter methods. This is an unfortunate Jexl limitation.
Fixed numerous small bugs, including a few warnings related to generics. (Thanks to Falk Bruegmann for a generics warning fix.)
Some of the API Changes

Handling of "Reporters"
Removed the API calls to set separate Reporter objects (for logging) in each major component. This greatly reduced some boilerplate code with a small reduction in flexibility that had dubious value. Now, a global Reporter object is used, the one set in the singleton instance of the Contract4J5 class.

All properties on aspects are no longer static methods. Instead, use the aspectOf() method to get the instance. So for example,

  ConstructorBoundaryConditions.getDefaultPreTestExpressionMaker();
is now
  ConstructorBoundaryConditions.aspectOf().getDefaultPreTestExpressionMaker();
Contract4J5 is no longer an aspect, but a class. The aspect code was moved to a separate aspect called AbstractConditions, leaving only pure-Java code. The conversion to Java made it easier to instantiate these objects as needed, especially for testing, and also to exploit the more complete support for Java in Eclipse (e.g., for refactorings). Note that this change means that any properties that were previously set on Contract4J5 are not now accessed using aspectOf, as just described for the aspects. Instead, Contract4J5 properties are accessed, e.g., as follows:

  Contract4J5.getInstance().setEnabled(Contract4J5.TestType.Pre, true);
Bugs

It seems that the ant build expects Spring to be present in order to define some properties or classpaths. This causes the build to fail if Spring is not present! This is a bug in the ant scripts. Workaround: Install Spring somewhere and point the "env.sh" or "env.bat" script to it. It won't actually be used unless you explicitly invoke one of the demonstration *.spring ant targets.
v0.5.0 February 7, 2006

Major Rewrite

Eliminated the precompilation step, replacing it with runtime teste expression evaluation using the Jakarta Commons Jexl expression parser.

The package structure has been changed from com.aspectprogramming.* to org.contract4j5.*.

Deprecated several features; they aren't supported in this milestone release.

Ad Hoc Configuration API

The ad hoc configuration API and full support for configuration through property files. Subsequent releases will "re-add" limited support for property file configuration, for convenience, but the preferred way to configure Contract4J5 is through a standard dependency injection (DI) solution like the Spring Framework. This release does all you to globally enable or disable all tests or just all precondition, postcondition, or invariant tests. To use, define one or more of the following System properties (true or false):
org.contract4j5.Contract	Enable/disable all tests
org.contract4j5.Pre	Enable/disable all precondition tests
org.contract4j5.Post	Enable/disable all postcondition tests
org.contract4j5.Invar	Enable/disable all invariant tests
The "alwaysActive" property of the V0.1 annotations

This annotation property, when used, kept a test "active" even if all other annotations of the same kind were disabled globally. The complexity of implementing this feature in the new architecture outweighed the benefits. Use an alternative implementation like embedded assert statements.
The "messagePrefix" and "messageSuffix" annotation properties

Similarly, these properties of the @Contract annotation are no longer supported. However, you can still define individual messages in the test annotations themselves.
Annotations on method parameters

AspectJ5 has the limitation that it doesn't support annotations on method parameters. They were supported in Contract4J5 v0.1, because it didn't rely on AspectJ's support. The workaround is to put all parameter tests in a method precondition test.
v0.1.1.0 October 4, 2005

Support for Ant builds and numerous small bug fixes in the V1 branch.
v0.1.0.2 April 24, 2005

Fixed a bug that prevented use of precondition annotations on individual method parameters if the method contains more than one parameter. (The generated aspectj code pointcut uses the "args()" specifier. For it to match correctly, args() must contain the correct parameter list for the method, with the parameter name used for the parameter of interest and the parameter types used for the other parameters.)
Numerous minor enhancements.

v1.0.1 February 6, 2005

Minor bug fixes.

<a name='furtherinfo'></a>

## For Further Information...

[polyglotprogramming.com/contract4j](http://polyglotprogramming.com/contract4j) is the home page for Contract4J5 and Contract4JBeans. It is developed by [Aspect Research Associates](http://aspectresearchassociates.com) (ARA), a consulting company specializing in *Polyglot* Programming technologies, such as Aspect-Oriented, Functional, and Object-Oriented Programming, "enterprise" Scala and Java, and Ruby on Rails. ARA also manages the [Aspect Programming](http://aspectprogramming.com) web site, where you will find more information and whitepapers on Contract4J5 and Aspect-Oriented Software Development (AOSD), in general.

We recently released the first version of a new AOP framework for Ruby called [Aquarium](http://aquarium.rubyforge.org/). The examples included with Aquarium include a basic Design-by-Contract module.

The [AOP@Work](http://www.ibm.com/developerworks/views/java/libraryview.jsp?search_by=aop@work:) series at [developerWorks.com](http://developerWorks.com) contains an [article about Contract4J5](http://www.ibm.com/developerworks/java/library/j-aopwork17.html). It introduces Design by Contract and how Contract4J5 supports it in Java. The article concludes with a discussion of emerging trends in Aspect-Oriented Design.

The AOSD.06 Conference in Bonn, Germany (March 19-24) featured a talk in the Industry Track on Contract4J5, specifically on the lessoned learned about writing generic, reusable aspects in AspectJ while implementing Contract4J5. There was also a paper on aspect-oriented design patterns in Contract4J5 in the ACP4IS workshop. Both papers can be found at the conference [website](http://aosd.net/2006).

The AOSD.07 Conference in Vancouver, British Columbia (March 12-16) featured a talk in the Industry Track on emerging principles of Aspect-Oriented Design, based on adaptations of well-known Object-Oriented Design principles. The paper can be found at the conference [website](http://aosd.net/2007/).

The definitive site on AOSD is [aosd.net](http://www.aosd.net).

See [aspectj.org](http://www.aspectj.org) for information on AspectJ, which was used to implement Contract4J.

For more on Design by Contract, see [Building bug-free O-O software: An introduction to Design by Contract(TM)](http://www.eiffel.com/developers/design_by_contract.html) and the discussion of DbC in the larger context of Agile Methods in Martin, et al., "Agile Software Development: Principles, Patterns, and Practices", Prentice Hall, 2003 (ISBN 0-13-597444-5).

For alternative approaches to doing Design by Contract in Java, see the [Barter](http://www.google.com/url?sa=t&source=web&cd=1&ved=0CBsQFjAA&url=http%3A%2F%2Fbarter.sourceforge.net%2F&ei=cMhOTbPQGcatgQfXpMQL&usg=AFQjCNHvW-VD4TQh964pjIZZN2zSIEi5fA) project, which uses [XDoclet](http://www.google.com/url?sa=t&source=web&cd=1&sqi=2&ved=0CBkQFjAA&url=http%3A%2F%2Fxdoclet.sourceforge.net%2F&ei=ishOTbD_C87egQfB4pUg&usg=AFQjCNFXfXQantxoEPUiSvf_hjnw0lnSPQ) and also generates AspectJ. Barter was an inspiration for Contract4J5.

JBoss AOP has basic support for contracts. Spring AOP may have similar support.



Copyright © 2003-2011 Aspect Research Associates. All Rights Reserved.
