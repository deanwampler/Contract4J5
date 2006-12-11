package org.contract4j5.configurator;

public abstract class AbstractConfigurator implements Configurator {
	
	public void configure() {
		doConfigure();
	}
	
	abstract protected void doConfigure();
}
