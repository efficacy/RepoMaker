package test;

import java.io.File;
import java.io.IOException;

import org.stringtree.maven.RepoMaker;

import junit.framework.TestCase;

public class RepoMakerTestCase extends TestCase {

	protected File generated;
	
	protected File make(String dir) throws IOException {
		return make(dir, dir);
	}
	
	protected File make(String from, String to) throws IOException {
		RepoMaker maker = new RepoMaker("src/test/input/" + from, "src/test/output/" + to);
		generated = new File("src/test/output/" + to);
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
