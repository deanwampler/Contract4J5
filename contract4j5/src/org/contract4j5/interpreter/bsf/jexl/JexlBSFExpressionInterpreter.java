package org.contract4j5.interpreter.bsf.jexl;

import java.util.Map;

import org.apache.bsf.BSFException;
import org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter;

/**
 * A Jexl subclass of {@link BSFExpressionInterpreterAdapter} is useful since we
 * need to register the engine with the bean scripting framework, which we do with
 * a static initializer. This isn't necessary for other languages, such as Groovy. 
 */
public class JexlBSFExpressionInterpreter extends BSFExpressionInterpreterAdapter {

	static {
		JexlBSFEngine.registerWithBSF();
	}
	
	public JexlBSFExpressionInterpreter() throws BSFException {
		super("jexl");
	}
	
	public JexlBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid) throws BSFException {
		super("jexl", treatEmptyTestExpressionAsValid);
	}
	
	public JexlBSFExpressionInterpreter(
			boolean treatEmptyTestExpressionAsValid, 
			Map<String, String> optionalKeywordSubstitutions) throws BSFException {
		super("jexl", treatEmptyTestExpressionAsValid, optionalKeywordSubstitutions);
	}
	
}
