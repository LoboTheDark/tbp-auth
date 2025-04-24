package com.tbp.tbp_auth;

import org.junit.jupiter.api.Test;
import static org.springframework.test.util.AssertionErrors.assertTrue;


class ToBePlayedAuthApplicationTests {

	@Test
	void mainRunsWithoutExceptions() {
		// This test fails if during the start any exception will appear
		ToBePlayedAuthApplication.main(new String[]{});
		assertTrue("Dummy assertion", true);
	}

}
