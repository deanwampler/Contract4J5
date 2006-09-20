package org.contract4j5.configurator;

import org.contract4j5.Contract4J;

public abstract class AbstractConfigurator implements Configurator {

	/**
	 * Because Contract4J is a singleton aspect, we return the instance.
	 */
	public Contract4J getContract4J() {
		return Contract4J.getInstance();
	}
	
	public void configure() {
		doConfigure();
	}
	
	abstract protected void doConfigure();
}
