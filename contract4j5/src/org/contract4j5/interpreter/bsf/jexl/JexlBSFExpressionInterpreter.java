package org.contract4j5.interpreter.bsf.jexl;

import org.apache.bsf.BSFException;
import org.contract4j5.interpreter.bsf.BSFExpressionInterpreterAdapter;

public class JexlBSFExpressionInterpreter extends BSFExpressionInterpreterAdapter {

	static {
		JexlBSFEngine.registerWithBSF();
	}
	
	public JexlBSFExpressionInterpreter() throws BSFException {
		super("jexl");
	}
}
