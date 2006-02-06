package org.contract4j5.test;

import org.contract4j5.Contract;
import org.contract4j5.Invar;
import org.contract4j5.Post;
import org.contract4j5.Pre;

/**
 * Interface that declares some contracts and includes the required
 * "@Contract" annotation.
 */
@Contract
@Invar ("$this.getFlag() > 0")
public interface ContractInterface extends BaseInterface {
	@Pre   void setName (String s);
	@Post  String getName ();
	@Invar("$this.name != null && $this.name.length() > 0") void m (String s);
}
