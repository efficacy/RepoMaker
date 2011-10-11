package test;

import java.io.File;
import java.io.IOException;

import org.stringtree.maven.RepoMaker;

import junit.framework.TestCase;

public class RepoMakerTestCase extends TestCase {

	File generated;
	
	protected File make(String dir) throws IOException {
		RepoMaker maker = new RepoMaker("src/test/input/" + dir, "src/test/output/" + dir);
		generated = new File("src/test/output/" + dir);
		clearOutput();
		maker.make();
		return generated;
	}

	private void clearOutput() {
		clearOutput(generated);
	}

	private void clearOutput(File dir) {
		if (null == dir || !dir.exists()) return;
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				clearOutput(file);
			} else {
				file.delete();
			}
		}
		dir.delete();
	}

	protected void assertFileExists(String name) {
		assertTrue(new File(generated, name).exists());
	}
}
