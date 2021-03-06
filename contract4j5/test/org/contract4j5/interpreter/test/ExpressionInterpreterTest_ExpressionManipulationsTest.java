package org.contract4j5.interpreter.test;

import java.util.Map;

import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.contract4j5.context.TestContext;
import org.contract4j5.contract.Pre;
import org.contract4j5.controller.Contract4J;
import org.contract4j5.instance.Instance;
import org.contract4j5.interpreter.ExpressionInterpreterHelper;
import org.contract4j5.interpreter.groovy.GroovyExpressionInterpreter;

public class ExpressionInterpreterTest_ExpressionManipulationsTest extends TestCase {

	class NullTestContext implements TestContext {
		public Instance getField() {
			return null;
		}
		public String getFileName() {
			return null;
		}
		public Instance getInstance() {
			return null;
		}
		public String getItemName() {
			return null;
		}
		public int getLineNumber() {
			return 0;
		}
		public Instance[] getMethodArgs() {
			return null;
		}
		public Instance getMethodResult() {
			return null;
		}
		public Map<String, Object> getOldValuesMap() {
			return null;
		}
		public String getTestExpression() {
			return null;
		}
		public String getActualTestExpression() {
			return null;
		}
		public String getInternalTestExpression() {
			return null;
		}

		public void setField(Instance target) {}
		public void setFileName(String fileName) {}
		public void setInstance(Instance instance) {}
		public void setItemName(String itemName) {}
		public void setLineNumber(int lineNumber) {}
		public void setMethodArgs(Instance[] methodArgs) {}
		public void setMethodResult(Instance methodResult) {}
		public void setOldValuesMap(Map<String, Object> map) {}
		public void setTestExpression(String testExpression) {}
		public void setActualTestExpression(String testExpression) {}
		public void setInternalTestExpression(String testExpression) {}
	}
	
	private static final String COLOR_FQN = "org.contract4j5.interpreter.test.Color";
	
	private ExpressionInterpreterHelper interpreter;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new GroovyExpressionInterpreter();
	}
	
	public void testFindReferencedObjectsAndLoadCanFindQualifiedClass() throws BSFException {
		assertNull(interpreter.getObjectInContext(COLOR_FQN));
		interpreter.findReferencedObjectsAndLoad("c4jX.foo "+COLOR_FQN+".BLUE", new NullTestContext());
		assertNotNull(interpreter.getObjectInContext(COLOR_FQN));
	}
	
	public void testFindReferencedObjectsAndLoadCantFindUnqualifiedClass() throws BSFException {
		assertNull(interpreter.getObjectInContext("Color"));
		interpreter.findReferencedObjectsAndLoad("c4jX.foo Color.BLUE", new NullTestContext());
		assertNull(interpreter.getObjectInContext("Color"));
	}
		
	public void testFindReferencedObjectsAndLoadCanFindUnqualifiedClassIfPreviouslyRegistered() throws BSFException {
		assertNull(interpreter.getObjectInContext("Color"));
		Contract4J.getInstance().registerGlobalContextObject("Color", Color.class);
		interpreter.findReferencedObjectsAndLoad("c4jX.foo Color.BLUE", new NullTestContext());
		assertNull(interpreter.getObjectInContext("Color"));
	}
	
	static class Tester {
		static {
			Contract4J.getInstance().registerGlobalContextObject("Color", Color.class);
		}
		@Pre("$return = Color.BLUE")
		public Color getColor() {return Color.BLUE;}
	}
	
	public void testFindReferencedObjectsAndLoadCanFindUnqualifiedClassIfPreviouslyRegisteredInAStaticInitializer() throws BSFException {
		assertNull(interpreter.getObjectInContext("Color"));
		Contract4J.getInstance().registerGlobalContextObject("Color", Color.class);
		interpreter.findReferencedObjectsAndLoad("c4jX.foo Color.BLUE", new NullTestContext());
		assertNull(interpreter.getObjectInContext("Color"));
	}
}
