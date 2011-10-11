package test;

import java.io.File;
import java.io.IOException;

import org.stringtree.maven.RepoMaker;

import junit.framework.TestCase;

public class ErrorTests extends TestCase {

	private File make(String dir) throws IOException {
		RepoMaker maker = new RepoMaker("src/test/input/" + dir, "src/test/output/" + dir);
		maker.make();
		return new File("src/test/output/" + dir);
	}

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
		assertEquals(0, dir.list().length);
	}
}