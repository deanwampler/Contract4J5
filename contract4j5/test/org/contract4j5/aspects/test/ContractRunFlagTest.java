package org.contract4j5.aspects.test;

import junit.framework.TestCase;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Invar;
import org.contract4j5.contract.Post;
import org.contract4j5.contract.Pre;
import org.contract4j5.contract.RunFlag;
import org.contract4j5.errors.ContractError;

public class ContractRunFlagTest extends TestCase {
	@Contract class Default {
		@Pre("arg == null") public Default(String arg) {};
	}
	
	@Contract(run=RunFlag.ALWAYS) class Always {
		@Pre("arg == null") public Always(String arg) {};
	}

	@Contract(run=RunFlag.NEVER) class Never {
		@Pre("arg == null") public Never(String arg) {};
	}

	@Contract(run=RunFlag.NEVER) class NeverWithPreAlways {
		@Pre(value="arg == null", run=RunFlag.ALWAYS) public NeverWithPreAlways(String arg) {};
	}

	@Contract(run=RunFlag.ALWAYS) class AlwaysWithPreNever {
		@Pre(value="arg == null", run=RunFlag.NEVER) public AlwaysWithPreNever(String arg) {};
	}

	@Contract(run=RunFlag.NEVER) class NeverWithPostAlways {
		@Post(value="arg == null", run=RunFlag.ALWAYS) public NeverWithPostAlways(String arg) {};
	}

	@Contract(run=RunFlag.ALWAYS) class AlwaysWithPostNever {
		@Post(value="arg == null", run=RunFlag.NEVER) public AlwaysWithPostNever(String arg) {};
	}

	@Contract(run=RunFlag.NEVER) class NeverWithInvarAlways {
		@Invar(value="arg == null", run=RunFlag.ALWAYS) public NeverWithInvarAlways(String arg) {};
	}

	@Contract(run=RunFlag.ALWAYS) class AlwaysWithInvarNever {
		@Invar(value="arg == null", run=RunFlag.NEVER) public AlwaysWithInvarNever(String arg) {};
	}

	public void testContractAlwaysOnIsDefaultRunFlagSetting() {
		try {
			new Default("hello!");
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testContractRunFlagSettingAlwaysCausesTheTestToRunEveryTime() {
		try {
			new Always(null);
			new Always(null);
			new Always("hello!");
			fail();
		} catch (ContractError ce) {
			// Expected
		}
	}

	public void testContractRunFlagSettingNeverCausesTheTestToNeverRun() {
		new Never("hello!");
	}

	public void testContractRunFlagSettingNeverOverridesNestedPreContractSettings() {
		new NeverWithPreAlways("hello!");
	}

	public void testContractRunFlagSettingAlwaysOverriddenByNestedPreContractSettings() {
		new AlwaysWithPreNever("hello!");
	}

	public void testContractRunFlagSettingNeverOverridesNestedPostContractSettings() {
		new NeverWithPostAlways("hello!");
	}

	public void testContractRunFlagSettingAlwaysOverriddenByNestedPostContractSettings() {
		new AlwaysWithPostNever("hello!");
	}

	public void testContractRunFlagSettingNeverOverridesNestedInvarContractSettings() {
		new NeverWithInvarAlways("hello!");
	}

	public void testContractRunFlagSettingAlwaysOverriddenByNestedInvarContractSettings() {
		new AlwaysWithInvarNever("hello!");
	}
}
