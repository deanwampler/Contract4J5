package org.contract4j5.generics;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Pre;

@Contract
public class GenericTestClass<T> {

	@Pre
	public void test(T object) {
		object.hashCode();
	}
}
