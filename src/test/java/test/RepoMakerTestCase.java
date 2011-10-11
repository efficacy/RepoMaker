package test;

import java.io.File;
import java.io.IOException;

import org.stringtree.maven.RepoMaker;

import junit.framework.TestCase;

public class RepoMakerTestCase extends TestCase {

	protected File make(String dir) throws IOException {
		RepoMaker maker = new RepoMaker("src/test/input/" + dir, "src/test/output/" + dir);
		maker.make();
		return new File("src/test/output/" + dir);
	}

}
