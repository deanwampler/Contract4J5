package org.contract4j5.util.reporter;

import org.contract4j5.reporter.Severity;

import junit.framework.TestCase;

public class SeverityTest extends TestCase {

	public void testParseGood() {
		for (Severity s: Severity.values()) {
			String name = s.name();
			Severity s2 = Severity.parse(s.name());
			assertEquals(name, s, s2);
			Severity s3 = Severity.parse(s.name().toLowerCase());
			assertEquals(name.toLowerCase(), s, s3);
		}
	}
	public void testParseBad() {
		assertNull(Severity.parse(null));
		assertNull(Severity.parse(""));
		assertNull(Severity.parse("unknown"));
	}
}
