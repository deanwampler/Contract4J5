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
package org.contract4j5.interpreter.bsf;

import java.util.Map;
import java.util.Vector;

import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.BSFEngineImpl;

public abstract class BSFEngineAdapter extends BSFEngineImpl {

	@SuppressWarnings("unchecked")
	public Object call(Object object, String methodName, Object[] argList)
			throws BSFException {
       Map<String, Object> map = getVariableMap();
	   for (Map.Entry<String, Object> entry: map.entrySet()) {
		   if (entry.getValue() == object) {
			   String script = makeInvokeMethodString(object, entry.getKey(), methodName, argList);
			   return eval("", 0, 0, script);
		   }
	   }
	   return null;
	}

	protected abstract Map<String, Object> getVariableMap();

	public abstract Object eval(String source, int lineNo, int columnNo, Object script)
			throws BSFException;

	@SuppressWarnings("unchecked")
	public Object apply(String source, int lineNo, int columnNo, Object funcBody, Vector paramNames,
			Vector arguments) throws BSFException {
		throw new BSFException("apply not supported.");
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
       int size = declaredBeans.size();
       for (int i = 0; i < size; i++) {
           declareBean((BSFDeclaredBean) declaredBeans.elementAt(i));
       }
   }
   
   @SuppressWarnings("unchecked")
   public void declareBean(BSFDeclaredBean bean) throws BSFException {
	   Map<String, Object> map = getVariableMap();
	   map.put (bean.name, bean.bean);
   }

   @SuppressWarnings("unchecked")
   public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
	   Map<String, Object> map = getVariableMap();
	   map.remove (bean.name);
   }
}
