package org.contract4j5.aspects.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Disabled;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.contract.RunFlag;
import org.contract4j5.errors.ContractError;

public class ContractDisableTest extends TestCase {
	@Contract class Default {
		@Pre("arg == null") public Default(String arg) {};
	}
	
	@Contract @Disabled class ContractDisabled {
		@Pre("arg == null") public ContractDisabled(String arg) {};
	}

	@Disabled @Contract class DisabledContract {
		@Pre("arg == null") public DisabledContract(String arg) {};
	}

	@Contract @Disabled class DisabledWithPre {
		@Pre(value="arg == null") public DisabledWithPre(String arg) {};
	}

	@Contract class PreDisabled {
		@Pre(value="arg == null") @Disabled public PreDisabled(String arg) {};
	}

	@Contract class DisabledPre {
		@Disabled @Pre(value="arg == null") public DisabledPre(String arg) {};
	}

	@Contract @Disabled class DisabledWithPost {
		@Post(value="arg == null") public DisabledWithPost(String arg) {};
	}

	@Contract class PostDisabled {
		@Post(value="arg == null") @Disabled public PostDisabled(String arg) {};
	}

	@Contract class DisabledPost {
		@Disabled @Post(value="arg == null") public DisabledPost(String arg) {};
	}

	@Contract @Disabled class DisabledWithInvar {
		@Invar(value="arg == null") public DisabledWithInvar(String arg) {};
	}

	@Contract class InvarDisabled {
		@Invar(value="arg == null") @Disabled public InvarDisabled(String arg) {};
	}

	@Contract class DisabledInvar {
		@Disabled @Invar(value="arg == null") public DisabledInvar(String arg) {};
	}


	public void testContractIsAlwaysOnWithoutDisabledAnnotation() {
		try {
			new Default("hello!");
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testContractWithDisabledAnnotationDisablesAllNestedTests() {
		new ContractDisabled("hello!");
		new DisabledWithPre("hello!");
		new DisabledWithPost("hello!");
		new DisabledWithInvar("hello!");
	}

	public void testPreWithDisabledAnnotationIsDisabled() {
		new PreDisabled("hello!");
	}
	public void testPostWithDisabledAnnotationIsDisabled() {
		new PostDisabled("hello!");
	}
	public void testInvarWithDisabledAnnotationIsDisabled() {
		new InvarDisabled("hello!");
	}

	public void testDisabledAnnotationCanAppearBeforeOrAfterContractAnnotation() {
		new ContractDisabled("hello!");
		new DisabledContract("hello!");
	}
	public void testDisabledAnnotationCanAppearBeforeOrAfterPreAnnotation() {
		new PreDisabled("hello!");
		new DisabledPre("hello!");
	}
	public void testDisabledAnnotationCanAppearBeforeOrAfterPostAnnotation() {
		new PostDisabled("hello!");
		new DisabledPost("hello!");
	}
	public void testDisabledAnnotationCanAppearBeforeOrAfterInvarAnnotation() {
		new InvarDisabled("hello!");
		new DisabledInvar("hello!");
	}
}
