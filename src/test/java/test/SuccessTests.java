package test;

import java.io.File;
import java.io.IOException;

public class SuccessTests extends RepoMakerTestCase {

	public void testSingle() throws IOException {
		File dir = make("single");
		assertTrue(dir.exists() && dir.isDirectory());
		assertTrue(new File(dir, "index.html").exists());
		assertTrue(new File(dir, "something-1.0.jar").exists());
	}

}
