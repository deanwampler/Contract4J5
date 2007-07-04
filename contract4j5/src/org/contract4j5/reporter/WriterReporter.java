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

package org.contract4j5.reporter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.contract4j5.utils.StringUtils;

/** 
 * A Reporter implementation that uses a {@link Writer}, which can wrap 
 * {@link System#out} and {@link System#err}, the default case. It can also be 
 * used for {@link java.io.StringWriter} output, which is useful for testing.
 * @note To imlement support for log4j or other 3rd-party logging toolkit, follow
 * the example here.
 */
public class WriterReporter extends ReporterHelper {
	protected void reportSupport (Severity level, Class clazz, String message) {
		try {
			StringBuffer buff = new StringBuffer(256);
			buff.append("[").append(level).append("] ");
			String cname = clazz != null ? clazz.getSimpleName() : "<null>";
			buff.append(cname).append(": ");
			buff.append(message).append(StringUtils.newline());
			writers[level.ordinal()].write(buff.toString());
			writers[level.ordinal()].flush();
		} catch (IOException e) {
			System.err.println ("Could not flush writer: ");
			e.printStackTrace(System.err);
		}
	}
	
	private Writer[] writers = null; 
	
	/**
	 * Get the stream to use for a particular level. 
	 * @return Writer being used. Can be null.
	 */
	public Writer getWriter (Severity level) {
		return writers[level.ordinal()];
	}
	
	/**
	 * Set the writer to use for a particular level. By default, a {@link Writer} constructed
	 * from {@link System#out} is used for
	 * {@link Severity#Debug} and {@link Severity#Info}, while a {@link Writer} 
	 * constructed from {@link System#err} is used for the higher level messages.
	 * @param level The level for this Writer
	 * @param writer The Writer to use; can be null
	 */
	public void setWriter (Severity level, Writer writer) {
		setWriterSupport (level.ordinal(), writer);
	}
	
	/**
	 * A convenience method to set a common writer for use for all levels. 
	 * @param writer The Writer to use; can be null
	 */
	public void setWriters (Writer writer) {
		for (int l = 0; l < this.writers.length; l++) {
			setWriterSupport (l, writer);
		}
	}
	
	/**
	 * A convenience method to set an array of writers at once. 
	 * @param writers The array of Writers to use; can be null. If the array is too
	 * short, the lower-level writers will be changed and the remaining writers 
	 * will be unchanged. If the array is too long, the extra elements will be ignored.
	 */
	public void setWriters (Writer writers[]) {
		if (writers == null) {
			return;
		}
		for (int l = 0; l < writers.length && l < this.writers.length; l++) {
			setWriterSupport (l, writers[l]);
		}
	}
	
	/**
	 * Set the stream to use for a particular level. This is a convenience method that allows
	 * the caller to specify an {@link OutputStream} instead of a {@link Writer}.
	 * @param stream the OutputStream to use. Can be null. If not null, it will be wrapped in 
	 * a {@link Writer}.
	 */
	public void setStreams (Severity level, OutputStream stream) {
		int l = level.ordinal();
		setWriterSupport (l, stream != null ? new PrintWriter(stream) : null);
	}
	
	public void setStreams (OutputStream stream) {
		setWriters (stream != null ? new PrintWriter(stream) : null);		
	}
	
	private void setWriterSupport (int l, Writer writer) {
		if (writers[l] != null) {
			try {
				writers[l].flush(); 		// flush, just in case.
			} catch (IOException e) {
				System.err.println ("Could not flush writer: ");
				e.printStackTrace(System.err);
			}  
		}
		this.writers[l] = writer;
	}
	
	/**
	 * Default constructor. Sets the threshold to {@link Severity#Warn}.
	 */
	public WriterReporter () {
		setThreshold (Severity.WARN);
		initWriters();
	}
	
	/**
	 * Constructor. 
	 * @param threshold for messages.
	 *
	 */
	public WriterReporter (Severity threshold) {
		setThreshold (threshold);
		initWriters();
	}
	
	/**
	 * Constructor.
	 * @param threshold for messages.
	 * @param writers an array of writers to use. If the array is too
	 * short, the remaining rewriters will be initialized with default values.
	 * If the array is too long, the extra elements are ignored.
	 */
	public WriterReporter (Severity threshold, Writer[] writers) {
		setThreshold (threshold);
		initWriters();
		for (int i = 0; i < writers.length && i < this.writers.length; i++) {
			this.writers[i] = writers[i];
		}
	}
	
	/**
	 * Constructor.
	 * @param threshold for messages.
	 * @param writer to use for all severities.
	 */
	public WriterReporter (Severity threshold, Writer writer) {
		setThreshold (threshold);
		initWriters();
		setWriters(writer);
	}
	
	// Init different writers with defaults: System.out or System.err wrapped in PrintWriters.
	private void initWriters() {
		writers = new Writer[Severity.OFF.ordinal()+1];
		writers[0] = new PrintWriter (System.out);
		writers[1] = writers[0];
		writers[2] = new PrintWriter (System.err);	  // "warn" level
		for (int i=3; i < Severity.OFF.ordinal(); i++) {
			writers[i] = writers[2];
		}
	}
}
