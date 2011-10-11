package test;

import java.io.File;
import java.io.IOException;

public class ErrorTests extends RepoMakerTestCase {

	public void testNoSourceFolder() {
		try {
			make("missing");
			fail("make missing dir should throw");
		} catch (IOException e) {
			// nothing to see here 
		}
	}

	public void testNoArtefacts() throws IOException {
		File dir = make("empty");
		assertTrue(dir.exists() && dir.isDirectory());
		assertEquals(1, dir.list().length);
		assertTrue(new File(dir, "index.html").exists());
	}
}