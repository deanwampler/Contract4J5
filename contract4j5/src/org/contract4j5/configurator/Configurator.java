package org.contract4j5.configurator;

public interface Configurator {
	public class ConfigurationFailedException extends RuntimeException {
		private static final long serialVersionUID = 8190835362858941492L;

		public ConfigurationFailedException(String message) { super(message); }
		public ConfigurationFailedException(Throwable th)   { super(th); }
		public ConfigurationFailedException(String message, Throwable th) { super(message, th); }
	}
	
	void configure() throws ConfigurationFailedException;
}
