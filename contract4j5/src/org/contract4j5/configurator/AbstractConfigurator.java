package org.contract4j5.configurator;

public abstract class AbstractConfigurator implements Configurator {
	
	public void configure() throws ConfigurationFailedException {
		doConfigure();
	}
	
	abstract protected void doConfigure() throws ConfigurationFailedException;
}
