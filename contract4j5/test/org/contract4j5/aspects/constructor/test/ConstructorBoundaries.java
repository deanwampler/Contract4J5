package org.contract4j5.aspects.constructor.test;

import java.util.List;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Post;

@Contract
public class ConstructorBoundaries {
//	@Pre("ConstructorBoundaries.validList(list)")
	@Post("$this.validList(list)")
	public ConstructorBoundaries(List<String> list) {}
	public static boolean validList(List<String> list) { return list.size() == 3; }
}
