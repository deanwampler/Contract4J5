package org.contract4j5.interpreter.bsf.jexl;

import java.util.Map;
import java.util.Vector;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.BSFEngineImpl;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

public class JexlBSFEngine extends BSFEngineImpl {

	public static void registerWithBSF() {
		BSFManager.registerScriptingEngine(
				"jexl", 
				JexlBSFEngine.class.getName(),
				new String[] { "jexl", "jl" }
			);
	}
	
	private JexlContext jexlContext = null;

	@SuppressWarnings("unchecked")
	public Object call(Object object, String methodName, Object[] argList)
			throws BSFException {
       Map<String, Object> map = jexlContext.getVars();
	   for (Map.Entry<String, Object> entry: map.entrySet()) {
		   if (entry.getValue() == object) {
			   String script = makeInvokeMethodString(object, entry.getKey(), methodName, argList);
			   return eval("", 0, 0, script);
		   }
	   }
	   return null;
	}

	public Object eval(String source, int lineNo, int columnNo, Object script)
			throws BSFException {
		   Expression expr;
		   try {
			   expr = ExpressionFactory.createExpression ((String) script);
			   return expr.evaluate(jexlContext);
		   } catch (Exception e) {
			   throw new BSFException(-1, e.toString(), e);
		   }
	}

	@SuppressWarnings("unchecked")
	public Object apply(String source, int lineNo, int columnNo, Object funcBody, Vector paramNames,
			Vector arguments) throws BSFException {
		throw new BSFException("exec not supported.");
	}
	
	public void exec(String source, int lineNo, int columnNo, Object script) throws BSFException {
		throw new BSFException("exec not supported.");
	  }

	protected String makeInvokeMethodString(Object object, String beanObjectName, String method, Object[] args) {
		StringBuffer buff = new StringBuffer();
		buff.append(beanObjectName);
		buff.append(".");
		buff.append(method);
		buff.append("(");
		boolean notFirst = false;
		for (Object arg: args) {
			if (notFirst)
				buff.append(", ");
			notFirst = true;
			buff.append(arg);
		}
		buff.append(")");
		return buff.toString();
	}

   @SuppressWarnings("unchecked")
   public void initialize(BSFManager mgr, String lang, Vector declaredBeans) throws BSFException {
       super.initialize(mgr, lang, declaredBeans);
       jexlContext = JexlHelper.createContext();
       int size = declaredBeans.size();
       for (int i = 0; i < size; i++) {
           declareBean((BSFDeclaredBean) declaredBeans.elementAt(i));
       }
   }
   
   @SuppressWarnings("unchecked")
   public void declareBean(BSFDeclaredBean bean) throws BSFException {
	   Map<String, Object> map = jexlContext.getVars();
	   map.put (bean.name, bean.bean);
   }

   @SuppressWarnings("unchecked")
   public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
	   Map<String, Object> map = jexlContext.getVars();
	   map.remove (bean.name);
   }
}
