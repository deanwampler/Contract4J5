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

package org.contract4j5.interpreter.test;

import junit.framework.TestCase;

import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;

/**
 *  simple example to show how to access method and properties
 *
 *  @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 *  @version $Id: MethodPropertyExample.java,v 1.4 2004/02/28 13:45:20 yoavs Exp $
 *  
 *  Converted print staements into assertEquals, so it's more of a real test! (Dean Wampler)
 */
public class MethodPropertyExample extends TestCase {
	public static class Foo
	{
		public static final String FOO_STRING = "This is from getFoo()";
	    public String getFoo()
	    {
	        return FOO_STRING;
	    }
	
	    public static final String GET_PREFIX = "This is the property ";
	    public String get(String arg)
	    {
	        return GET_PREFIX + arg;
	    }
	
	    public static final String LONG_PREFIX = "The value is : ";
	    public String convert(long i)
	    {
	        return LONG_PREFIX + i;
	    }
	}

	public void setUp() throws Exception {
    	super.setUp();
    }
    
    @SuppressWarnings("unchecked")
	public void test() {
    	try {
		    /*
		     *  First make a jexlContext and put stuff in it
		     */
		    JexlContext jc = JexlHelper.createContext();
		
		    jc.getVars().put("foo", new Foo());
		    jc.getVars().put("number", new Integer(10));
		
		    /*
		     *  access a method w/o args
		     */
		    Expression e = ExpressionFactory.createExpression("foo.getFoo()");
		    Object o = e.evaluate(jc);
		    assertEquals(Foo.FOO_STRING, o);
		
		    e = ExpressionFactory.createExpression("foo.foo");
		    o = e.evaluate(jc);
		    assertEquals(Foo.FOO_STRING, o);
		
		    /*
		     *  access a method w/ args
		     */
		    e = ExpressionFactory.createExpression("foo.convert(1)");
		    o = e.evaluate(jc);
		    assertEquals(Foo.LONG_PREFIX+"1", o);
		
		    e = ExpressionFactory.createExpression("foo.convert(1+7)");
		    o = e.evaluate(jc);
		    assertEquals(Foo.LONG_PREFIX+"8", o);
		
		    e = ExpressionFactory.createExpression("foo.convert(1+number)");
		    o = e.evaluate(jc);
		    assertEquals(Foo.LONG_PREFIX+"11", o);
		
		    /*
		     * access a property
		     */
		    e = ExpressionFactory.createExpression("foo.bar");
		    o = e.evaluate(jc);
		    assertEquals(Foo.GET_PREFIX + "bar", o);
    	} catch (Exception e) {
    		System.err.println (e.toString());
    		e.printStackTrace();
    	}
	}

}
