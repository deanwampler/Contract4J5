/*
 * Copyright 2005, 2006 Dean Wampler. All rights reserved.
 * http://www.aspectprogramming.com
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
package org.contract4j5;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the {@link TestContext} interface.
 * @author Dean Wampler <mailto:dean@aspectprogramming.com>
 */
public class TestContextImpl implements TestContext {
	private String itemName = null;
	public  String getItemName() {
		return this.itemName;
	}
	public void setItemName (String itemName) {
		this.itemName = itemName;
	}

	private Instance instance = null;
	public  Instance getInstance() {
		return this.instance;
	}
	public void setInstance (Instance instance) {
		this.instance = instance;
	}

	private Instance field = null;
	public  Instance getField() {
		return field;
	}
	public void setField (Instance field) {
		this.field = field;
	}

	private Instance[] methodArgs = null;
	public  Instance[] getMethodArgs() {
		return methodArgs;
	}
	public void setMethodArgs(Instance[] methodArgs) {
		this.methodArgs = methodArgs;
	}

	private Instance methodResult = null;
	public  Instance getMethodResult() {
		return methodResult;
	}
	public void setMethodResult(Instance methodResult) {
		this.methodResult = methodResult;
	}

	private Map<String, Object> oldValuesMap = new HashMap<String, Object>();
	public  Map<String, Object> getOldValuesMap() {
		return oldValuesMap;
	}
	public void setOldValuesMap (Map<String, Object> map) {
		oldValuesMap = map;
	}
	
	private String fileName;
	public  String getFileName() {
		return fileName;
	}
	public void setFileName (String fileName) {
		this.fileName = fileName;
	}
	
	private int lineNumber;
	public  int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber (int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public TestContextImpl() {}
	
	public TestContextImpl(
			String     itemName,
			Instance   instance, 
			Instance   field, 
			Instance[] methodArgs, 
			Instance   methodResult) {
		setItemName(itemName);
		setInstance(instance);
		setField(field);
		setMethodArgs(methodArgs);
		setMethodResult(methodResult);
		setOldValuesMap(new HashMap<String, Object>());
		setFileName("");
		setLineNumber(0);
	}
	
	public TestContextImpl(
			String     itemName,
			Instance   instance, 
			Instance   field, 
			Instance[] methodArgs, 
			Instance   methodResult,
			Map<String, Object> oldValuesMap) {
		setItemName(itemName);
		setInstance(instance);
		setField(field);
		setMethodArgs(methodArgs);
		setMethodResult(methodResult);
		setOldValuesMap(oldValuesMap);
		setFileName("");
		setLineNumber(0);
	}
	
	public TestContextImpl(
			String     itemName,
			Instance   instance, 
			Instance   field, 
			Instance[] methodArgs, 
			Instance   methodResult,
			Map<String, Object> oldValuesMap,
			String     fileName,
			int        lineNumber) {
		setItemName(itemName);
		setInstance(instance);
		setField(field);
		setMethodArgs(methodArgs);
		setMethodResult(methodResult);
		setOldValuesMap(oldValuesMap);
		setFileName(fileName);
		setLineNumber(lineNumber);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer(1024);
		sb.append("name = ").append(getItemName());
		sb.append(", instance = ").append(getInstance());
		sb.append(", field = ").append(getField());
		sb.append(", method args = ");
		if (getMethodArgs() == null) {
			sb.append("null");
		} else {
			sb.append("(");
			for (Instance i: getMethodArgs()) {
				sb.append(i);
				sb.append(", ");
			}
			sb.append(")");
		}
		sb.append(", method result = ");
		sb.append(getMethodResult());
		return sb.toString();
	}
}
