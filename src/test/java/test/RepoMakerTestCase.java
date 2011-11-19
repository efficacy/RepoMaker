package test;

import java.io.File;
import java.io.IOException;

import org.stringtree.maven.RepoMaker;

import junit.framework.TestCase;

public class RepoMakerTestCase extends TestCase {

	protected File generated;
	
	public void setUp() {
		clearOutput();
	}
	
	protected File make(String dir) throws IOException {
		return make(dir, dir);
	}
	
	protected File make(String from, String to) throws IOException {
		String in = "src/test/input/" + from;
		String out = "src/test/output/" + to;
		RepoMaker maker = new RepoMaker(in, out);
		generated = new File(out);
		maker.make();
		return generated;
	}

	protected void clearOutput() {
		clearOutput(new File("src/text/output"));
	}

	private void clearOutput(File dir) {
		if (null == dir || !dir.exists()) return;
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				clearOutput(file);
				dir.delete();
			} else {
				file.delete();
			}
		}
	}

	protected void assertFileExists(String name) {
		assertTrue(new File(generated, name).exists());
	}
}
