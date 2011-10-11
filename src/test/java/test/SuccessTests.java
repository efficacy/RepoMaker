package test;

import java.io.File;
import java.io.IOException;

public class SuccessTests extends RepoMakerTestCase {

	public void testSingle() throws IOException {
		File dir = make("single");
		assertTrue(dir.exists() && dir.isDirectory());
		assertTrue(new File(dir, "index.html").exists());
		assertTrue(new File(dir, "org/index.html").exists());
		assertTrue(new File(dir, "org/stringtree/index.html").exists());
		assertTrue(new File(dir, "org/stringtree/something/index.html").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/index.html").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.jar").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.jar.md5").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.jar.sha1").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.pom").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.pom.md5").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.pom.sha1").exists());
	}

	public void testSingleWithSources() throws IOException {
		File dir = make("single-sources");
		assertTrue(dir.exists() && dir.isDirectory());
		assertTrue(new File(dir, "index.html").exists());
		assertTrue(new File(dir, "org/index.html").exists());
		assertTrue(new File(dir, "org/stringtree/index.html").exists());
		assertTrue(new File(dir, "org/stringtree/something/index.html").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/index.html").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.jar").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.jar.md5").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.jar.sha1").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0-sources.jar").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0-sources.jar.md5").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0-sources.jar.sha1").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.pom").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.pom.md5").exists());
		assertTrue(new File(dir, "org/stringtree/something/1.0/something-1.0.pom.sha1").exists());
	}

}
