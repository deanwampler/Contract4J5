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

package org.contract4j5.context;

import java.util.TreeMap;

public class TestContextCache {
	// Uses the type of annotation ("testType") and the file location
	// as the key. This works since only one instance of an annotation can 
	// be applied to any element, so this "triple" of fields is unique.
	static public class Key implements Comparable<Key> {
		public String testType;
		public String fileName;
		public int    lineNumber;
		public Key(String testType, String fileName, int lineNumber) {
			this.testType = testType;
			this.fileName = fileName;
			this.lineNumber = lineNumber;
		}
		public int compareTo(Key o) {
			int compare = testType.compareTo(o.testType);
			if (compare != 0) return compare; 
			compare = fileName.compareTo(o.fileName); 
			if (compare != 0) return compare; 
			return lineNumber - o.lineNumber;
		}
		public boolean equals(Object o) {
			if (o instanceof Key) 
				return compareTo((Key) o) == 0;
			return false;
		}
	}
	
	// Some fields may be null, depending on the context.
	static public class Entry {
		public TestContext testContext;
		public String[]    argNames;
		public Class<?>[]  argTypes;
		public String      fieldName;
		public Class<?>    fieldType;
		public Entry(TestContext testContext, String[] argNames, Class<?>[] argTypes, 
						String fieldName, Class<?> fieldType) {
			this.testContext = testContext;
			this.argNames = argNames;
			this.argTypes = argTypes;
			this.fieldName = fieldName;
			this.fieldType = fieldType;
		}
	}

	protected TreeMap<Key, Entry> cache = new TreeMap<Key, Entry>();

	// This global flag is for performance testing the impact of caching. 
	// When disabled, you get 100% cache misses!

	private static boolean cachePutsEnabled = true;
	public void enableCachePuts()  { cachePutsEnabled = true; }
	public void disableCachePuts() { cachePutsEnabled = false; }
	
	public Entry get(Key key) { return cache.get(key); }
	public void  put(Key key, Entry entry) { if (cachePutsEnabled) cache.put(key, entry); }
}
