# Contract4J5: A Quick Example

Suppose you have a virtual phone book module with a method `search` that guarantees to return the phone number of a person if you supply non-null fields for the person's first name, last name, and street address. (I'll ignore the fact that a user might specify "valid" names and addresses for non-existent people...) 

This is the *contract* for the module, in simple terms, where the
*preconditions* are the requirements for the inputs to `search`,
and the one *postcondition* is the guaranteed result.

In Java, you might validate the contract at runtime using `asserts`
or other "in-line" tests: 

    import ...PhoneNumber;
    import ...Address;

    class SearchEngine {
      ...
      PhoneNumber search (String first, String last, 
                          Address streetAddress) {
        assert first   != null : "bad first name";
        assert last    != null : "bad last name";
        assert address != null : "bad address";
        PhoneNumber result = 
          doSearch (first, last, streetAddress);
        assert result != null && result.isValid() > 0 
               : "bad phone number";
        return result;
      }
      ...
    }

Here I have assumed the existence of `Address` and `PhoneNumber` classes to hide some details and I've also assumed that the latter has an `isValid()` method to confirm the returned phone number has a "valid" value. Note also the "worker" method `doSearch`.

The application logic in this function is cluttered by the contract "concern", to use <a href="http://www.aspectprogramming.com/aosd">aspect-oriented programming</a> terminology. Also, for efficiency, once testing is done and we're ready for a production deployment, we would like to remove these tests from the code. Fortunately, assertions can be turned off completely or selectively when the JVM is started. Other, *ad hoc* contract test mechanisms may not be so flexible.

It would be nice to remove the clutter, yet still do the tests. [Contract4J5](https://github.com/deanwampler/Contract4J5) helps you do that. Using the Java 5 annotations, we can define these tests less obtrusively. 

Here is `SearchEngine` rewritten to use Contract4J5 annotations:

    import ...PhoneNumber;
    import ...Address;
    import com.contract4j5.contract.*;

    @Contract
    public class SearchEngine {
      ...
      @Pre
      @Post("$return != null && $return.isValid()")
      public PhoneNumber search (String first, String last, 
                                 Address streetAddress) {
        PhoneNumber result = 
          doSearch (first, last, streetAddress);
        return result;
      }
      ...
    }

The `@Contract` annotation is required at the beginning of any
class (including nested classes and derived classes) that uses the other annotations to define tests. 
The `@Pre` annotation defines a precondition test on the method. In this case, none of the input parameters can be null, which is the default test when no expression is defined, as shown in this example. 
The `@Post` annotation defines a postcondition test. It uses the
special keyword `$return`
that represents the object or primitive data value returned by the method.
Here, the expression string defines an executable test on the returned 
phone number. Compare this expression with the `assert` 
statement used previously.

So, [Contract4J5](https://github.com/deanwampler/Contract4J5) reduces the clutter (and the amount of typing), while
providing great flexibility for defining, building, and running *Design
by Contract* tests.

Now, we need to apply the aspects in *Contract4J5* 
to our code. There are three ways to do this:

* Compile our code with [AspectJ's](http://www.aspectj.org) `ajc` compiler instead of `javac`. You will need to add the `contract4j5.jar` to your `CLASSPATH`.
* Use a *binary weaving* step. After you compile your code with `javac`, use `ajc` to weave in the *Contract4J5* aspects.
* Use *load-time weaving* to apply the *Contract4J5* aspects at runtime.

All three approaches are demonstrated by *Contract4J5's* `ant` build process, as described in the [Contract4J5 README](https://github.com/deanwampler/Contract4J5/blob/master/README.md). However, because *load-time weaving* is the easiest and the least intrusive approach, although not the most runtime-efficient, we recommend that you start with it. Here is a brief description of the steps for using it:

* Install [AspectJ](http://www.aspectj.org) v1.5 or later. (However, note that we haven't tested with the most recent versions.)
* Make sure the `ASPECTJ_HOME` environment variable is defined.
* Add the `contract4j5.jar` to your `CLASSPATH`.
* Copy `test/META-INF/aop.xml` to a `META-INF` directory at the root of your application or somewhere in your CLASSPATH. Typically, `META-INF` would be a top-level directory in your application's jar file.
* Edit the `aop.xml` and change the `<include within="...">` tag to the correct packages or classes for your application. 
* Invoke your application (*e.g.,* JUnit tests) using the `ASPECTJ_HOME/bin/aj5` (*nix) or `%ASPECTJ_HOME%\bin\aj5.bat` (Windows) script, instead of `java`.
* Profit!!

That's it! The *Contract4J5* aspects will be applied as your application's classes as they are loaded. For production deployments, simply use `java` to invoke your application, as before.

**Note:** To see how to invoke JUnit tests from ant with load-time weaving, see the `_junitTemplate.ltw` build target in `ant/targets.xml`.

See the [Contract4J5 README](https://github.com/deanwampler/Contract4J5/blob/master/README.md) for more detailed information on installation, usage, and theory.