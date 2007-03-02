package org.contract4j5.performance.test;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.interpreter.ExpressionInterpreter;
import org.contract4j5.interpreter.jexl.JexlExpressionInterpreter;

/**
 * Sample class with a contract used for performance testing.
 */
@Contract
public class Person {
	@Invar("name != null && name.length() > 0")
	private String name;
	
	@Invar("ssn > 0")
	private long   ssn;

	@Invar("age > 0")
	private int    age;
	
	@Post("$return != null && $return.length() > 0")
	public String getName() {
		return name;
	}

	@Pre("name != null && name.length() > 0")
	public void setName(String name) {
		this.name = name;
	}

	@Post("$return > 0")
	public long getSsn() {
		return ssn;
	}

	@Pre("ssn > 0")
	public void setSsn(long ssn) {
		this.ssn = ssn;
	}

	@Post("$return > 0")
	public int getAge() {
		return age;
	}

	@Pre("age > 0")
	public void setAge(int age) {
		this.age = age;
	}

	@Pre("name != null && name.length() > 0 && ssn > 0 && age > 0")
	public Person(String name, long ssn, int age) {
		super();
		this.name = name;
		this.ssn  = ssn;
		this.age  = age;
	}
	
	public String toString() {
		return "Person: name = \""+name+"\", ssn = "+ssn+", age = "+age;
	}
	
	public static void main(String[] args) {
		int maxCount = 10;
		System.setProperty("interpreter", "groovy");
		if (args.length > 0) {
			try {
				maxCount = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("usage: java Person N [jexl | groovy | jruby | stub | nobsf] [name | ssn | age]");
				System.err.println("You specified argument \""+args[0]+"\".");
				System.exit(1);
			}
		}
		for (int i=1; i<args.length; i++) {
			String arg = args[i];
			if (arg.equals("stub")) {
				System.setProperty("interpreter", "stub");
			} else if (arg.equals("nobsf")) {
				setupNoBSFExpressionInterpreter(null);				
			} else if (arg.equals("nobsfjexl")) {
				setupNoBSFExpressionInterpreter("jexl");				
			} else if (arg.equals("jexl")) {
				System.setProperty("interpreter", "jexl");
			} else if (arg.equals("jruby")) {
				System.setProperty("interpreter", "jruby");
			} else if (arg.equals("groovy")) {
				System.setProperty("interpreter", "groovy");
			} else {
				runSpecialCase(arg);
			}
		}
		managePersons(maxCount);
		System.out.println("Created "+maxCount+" persons");
	}

	private static void setupNoBSFExpressionInterpreter(String name) {
		Contract4J c4j = Contract4J.getInstance();
		ExpressionInterpreter interpreter = null;
		if (name == null)
			interpreter = new NoBSFExpressionInterpreter();
		else if (name.equals("jexl"))
			interpreter = new JexlExpressionInterpreter();
		else
			throw new RuntimeException ("Unknown \"nobsf\" interpreter option \""+name+"\".");
					
		c4j.getContractEnforcer().setExpressionInterpreter(interpreter);
	}

	private static void runSpecialCase(String arg) {
		if (arg.equals("name")) {
			new Person(null, 1, 1);
		} else if (arg.equals("ssn")) {
			new Person("name", 0, 1);
		} else if (arg.equals("age")) {
			new Person("name", 1, 0);
		}
		else {
			System.out.println("Unknown argument: \""+arg+"\".");
			System.exit(1);
		}
		System.out.println("new Person(...) did not throw exception!");
		System.exit(1);	
	}
	
	private static void managePersons(int maxCount) {
		for (int i=0; i<maxCount; i++) {
			Person person = new Person("name"+i, 1000000+i, 1+i);
			person.setName("new "+person.getName());
			person.setSsn(person.getSsn()+1);
			person.setAge(person.getAge()+1);
			// System.out.println(person);
		}
	}
}
