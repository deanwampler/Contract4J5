package org.contract4j5.generics;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Pre;

@Contract
public class NonGenericTestClass {

	@Pre
	public void test(Object object) {
		object.hashCode();
	}
}
