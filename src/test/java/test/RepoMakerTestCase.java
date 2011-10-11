package test;

import java.io.File;
import java.io.IOException;

import org.stringtree.maven.RepoMaker;

import junit.framework.TestCase;

public class RepoMakerTestCase extends TestCase {

	protected File make(String dir) throws IOException {
		RepoMaker maker = new RepoMaker("src/test/input/" + dir, "src/test/output/" + dir);
		File ret = new File("src/test/output/" + dir);
		clearOutput(ret);
		maker.make();
		return ret;
	}

	private void clearOutput(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				clearOutput(file);
			} else {
				file.delete();
			}
		}
		dir.delete();
	}

}
