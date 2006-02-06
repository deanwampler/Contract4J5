package org.contract4j5.test;

import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

/**
 * Interface that declares some contracts, but fails to include the required
 * "@Contract" annotation.
 */
@Invar ("$this.getFlag() > 0")
public interface NoContractInterface extends BaseInterface {
	@Pre   void setName (String s);
	@Post  String getName ();
	@Invar("$this.getName().length() > 0") 
	@Post("!$this.getName.equals(\"bad\")") 
	void m (String s);
}
